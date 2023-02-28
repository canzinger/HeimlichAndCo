package heimlich_and_co;

import at.ac.tuwien.ifs.sge.game.ActionRecord;
import at.ac.tuwien.ifs.sge.game.Game;
import heimlich_and_co.actions.*;
import heimlich_and_co.cards.HeimlichAndCoCard;
import heimlich_and_co.enums.Agent;
import heimlich_and_co.enums.HeimlichAndCoPhase;
import heimlich_and_co.factories.HeimlichAndCoCardStackFactory;
import heimlich_and_co.util.CardStack;
import heimlich_and_co.util.ListHelpers;

import java.util.*;

/**
 * The main game class.
 */
public class HeimlichAndCo implements Game<HeimlichAndCoAction, HeimlichAndCoBoard> {

    private static final int MAXIMUM_NUMBER_OF_PLAYERS = 7;
    private static final int MINIMUM_NUMBER_OF_PLAYERS = 2;
    private static final String ILLEGAL_STATE_MESSAGE = "The game is in a state it should not be in!";
    private final int numberOfPLayers;
    private final HeimlichAndCoBoard board;
    /**
     * Saves which agent belongs to which player (MUST REMAIN SECRET).
     */
    private final Map<Integer, Agent> playersToAgentsMap;
    /**
     * Whether the game is played with or without cards
     */
    private final boolean withCards;
    /**
     * Saves which card belongs to which player (MUST REMAIN SECRET).
     */
    private final Map<Integer, List<HeimlichAndCoCard>> cards;
    private final LinkedList<ActionRecord<HeimlichAndCoAction>> actionRecords; //Linked list for efficiency reasons
    private final Set<Integer> disqualifiedPlayers = new HashSet<>();
    private int currentPlayer;
    /**
     * The phase the game is currently in.
     */
    private HeimlichAndCoPhase phase;
    /**
     * Saves whose turn it actually is when playing with cards, as the currentPlayer can change during the card round.
     * More specifically, this player stays the same from rolling the die until the next player rolls the die
     */
    private int currentTurnPlayer;
    /**
     * The Stack of cards where card will be drawn if needed.
     */
    private CardStack<HeimlichAndCoCard> cardStack;
    /**
     * Counter of how many players skipped in a row during the card playing phase.
     */
    private int playersSkippedInARowDuringCardPhase;
    /**
     * Whether custom die rolls are allowed in this game or not. Will be false in a normal game setting.
     */
    private boolean allowCustomDieRolls;

    /**
     * Creates a new HeimlichAndCo instance with the minimum amount of players needed and without cards.
     */
    public HeimlichAndCo() {
        this(0, HeimlichAndCo.MINIMUM_NUMBER_OF_PLAYERS, null, null, null, false);
    }

    /**
     * Creates a new HeimlichAndCo instance with the given amount of players and without cards.
     */
    public HeimlichAndCo(int numberOfPLayers) {
        this(0, numberOfPLayers, null, null, null, false);
    }

    /**
     * Copies values from the given game into a new game object. Possible to either strip the new game of private information or not.
     * Therefore, this can be used to create a copy for a certain player which only contains information for the given player.
     * If information is stripped, the only information that will remain is that for the CURRENT PLAYER.
     * Note: There might be some functions that do not work for games with stripped information.
     * Note: A player can find out which agent belongs to him by using the playersToAgentsMap
     *
     * @param game             the game which acts as the basis for the new game
     * @param stripInformation whether to strip non-public information from the new game
     */
    public HeimlichAndCo(HeimlichAndCo game, boolean stripInformation) {
        this(game.getCurrentPlayer(), game.numberOfPLayers, game.actionRecords, game.board, null, game.withCards);
        phase = game.phase;
        currentTurnPlayer = game.currentTurnPlayer;
        playersToAgentsMap.clear();
        if (withCards) {
            cards.clear();
            cardStack = null;
            playersSkippedInARowDuringCardPhase = game.playersSkippedInARowDuringCardPhase;
        }

        if (stripInformation) {
            playersToAgentsMap.put(game.currentPlayer, game.playersToAgentsMap.get(game.currentPlayer));
            if (withCards) {
                List<HeimlichAndCoCard> playerCards = game.cards.get(game.currentPlayer);
                cards.put(game.getCurrentPlayer(), new LinkedList<>(playerCards));
                cardStack = HeimlichAndCoCardStackFactory.newInstance(game.cardStack.count());
            }
        } else {
            playersToAgentsMap.putAll(game.playersToAgentsMap);
            if (withCards) {
                for (Integer player : game.cards.keySet()) {
                    cards.put(player, new LinkedList<>(game.cards.get(player)));
                }
                HeimlichAndCoCard[] oldCards = game.cardStack.getCards().toArray(new HeimlichAndCoCard[]{});
                List<HeimlichAndCoCard> newCards = new LinkedList<>();
                for (HeimlichAndCoCard oldCard : oldCards) {
                    newCards.add(oldCard.deepCopy());
                }
                cardStack = new CardStack<>(newCards);
            }
        }
        allowCustomDieRolls = game.allowCustomDieRolls;
        this.disqualifiedPlayers.addAll(game.disqualifiedPlayers);
    }

    /**
     * NOTE: This is a deviation from the "original" description of the engine. The engine uses this method to allow
     * for custom boards. As this is not possible or rather does not make sense for Heimlich and Co, a custom board does not
     * make sense.
     * <p>
     * The -b option is therefore used to set whether to play with or without cards. If board equals "1",
     * "cards" or "Cards", then the game is constructed to be played with cards. If board does not equals any of these
     * values or the option is not used, the game is played without cards.
     *
     * @param board           string indicating whether the game is played with cards
     * @param numberOfPlayers
     */
    public HeimlichAndCo(String board, int numberOfPlayers) {
        this(0, numberOfPlayers, null, null, null,
                board != null && (board.equals("1") || board.equals("cards") || board.equals("Cards")));
    }

    /**
     * Creates a new HeimlichAndCo instance which is a copy of the game given as a parameter.
     * Private information is not stripped.
     *
     * @param game the game which acts as the basis for the new game
     */
    public HeimlichAndCo(HeimlichAndCo game) {
        this(game, false);
    }

    /**
     * Creates a new HeimlichAndCo instance with the given parameters.
     * Acts as the base constructor which all other constructors call.
     *
     * @param currentPlayer      current player
     * @param numberOfPlayers    the number of players that are playing (must confine to the restrictions by minimum and
     *                           maximum players needed for playing)
     * @param actionRecords      the action records (can be null)
     * @param board              the board (can be null)
     * @param playersToAgentsMap Map which maps players to agents (can be null)
     * @param withCards          whether the game should be with or without cards
     */
    public HeimlichAndCo(int currentPlayer, int numberOfPlayers,
                         List<ActionRecord<HeimlichAndCoAction>> actionRecords,
                         HeimlichAndCoBoard board, Map<Integer, Agent> playersToAgentsMap, boolean withCards) {
        if (currentPlayer < 0 || currentPlayer >= numberOfPlayers) {
            throw new IllegalArgumentException("Current player must be a valid player." + currentPlayer);
        }
        this.currentPlayer = currentPlayer;
        if (numberOfPlayers < getMinimumNumberOfPlayers() || numberOfPlayers > getMaximumNumberOfPlayers()) {
            throw new IllegalArgumentException("Invalid value given for number of players.");
        }
        this.numberOfPLayers = numberOfPlayers;
        if (actionRecords == null) {
            this.actionRecords = new LinkedList<>();
        } else {
            this.actionRecords = ListHelpers.deepCopyActionRecordList(actionRecords);
        }
        if (board == null) {
            this.board = new HeimlichAndCoBoard(numberOfPlayers + getNumberOfDummyAgents(numberOfPlayers));
        } else {
            this.board = new HeimlichAndCoBoard(board);
        }
        if (playersToAgentsMap == null) {
            this.playersToAgentsMap = createPlayersToAgentsMap(this.numberOfPLayers);
        } else {
            this.playersToAgentsMap = new HashMap<>(playersToAgentsMap);
        }
        this.withCards = withCards;
        if (withCards) {
            this.cardStack = HeimlichAndCoCardStackFactory.newInstance();
            this.cards = new HashMap<>();
            for (Integer player : this.playersToAgentsMap.keySet()) { //each player gets two cards at the start of the game
                List<HeimlichAndCoCard> playerCards = new LinkedList<>();
                playerCards.add(cardStack.drawCard());
                playerCards.add(cardStack.drawCard());
                this.cards.put(player, playerCards);
            }
        } else {
            this.cards = null;
            this.cardStack = null;
        }
        this.phase = HeimlichAndCoPhase.DIE_ROLL_PHASE;
        this.allowCustomDieRolls = false;
    }

    /**
     * Applies an action to this game, DOES NOT create a copy of this game (in contrast to doAction).
     * <p>
     * More performant than doAction (as the game does not have to be copied), but changes THIS instance.
     *
     * @param action action to take
     */
    public void applyAction(HeimlichAndCoAction action) {
        if (!isValidAction(action)) {
            throw new IllegalArgumentException("Invalid Action given");
        }

        HeimlichAndCoBoard boardBeforeAction = new HeimlichAndCoBoard(this.board);
        action.applyAction(this.board);
        this.actionRecords.addLast(new ActionRecord<>(currentPlayer, action));

        handleCardsAfterAction(action, boardBeforeAction);

        phase = getNextPhase(action, board.scoringTriggered());
        if (phase == HeimlichAndCoPhase.SAFE_MOVE_PHASE) {
            currentPlayer = currentTurnPlayer;
            board.awardPoints();

            //skip the safe move phase for the disqualified player
            if (disqualifiedPlayers.contains(currentTurnPlayer)) {
                board.moveSafe(7);
                phase = HeimlichAndCoPhase.DIE_ROLL_PHASE;
                turnFinished();
            }
        } else if (phase == HeimlichAndCoPhase.DIE_ROLL_PHASE) {
            turnFinished();
        }
    }

    /**
     * If the game is in a state of indeterminacy, this method will return an action according to the
     * distribution of probabilities, or hidden information. If the game is in a definitive state null
     * is returned.
     * Note: Because HeimlichAndCo can never be in a state of indeterminacy, this function will always return null.
     *
     * @return null
     */
    @Override
    public HeimlichAndCoAction determineNextAction() {
        return null;
    }

    /**
     * Disqualifies a player. Can be invoked after a player times out for example. The new game is "as identical
     * as possible" to this game, but with the player disqualified/removed. The state of the game changes
     * as little as possible (meaning the state of the game, board, other players are only changed if absolutely
     * necessary).
     * For some games this might not be possible/feasible, in this case an UnsupportedOperationException is thrown.
     *
     * @return a new game with the given player disqualified
     * @throws IllegalArgumentException      if the player is not currently in the game
     * @throws IllegalStateException         if the player cannot be disqualified because not enough players would remain
     * @throws UnsupportedOperationException if the game does not support disqualification
     */
    @Override
    public HeimlichAndCo disqualifyCurrentPlayer() {
        if (numberOfPLayers - this.disqualifiedPlayers.size() <= 2) {
            throw new IllegalStateException("There are only 2 players (left), therefore no player can be disqualified");
        }
        if (!playersToAgentsMap.containsKey(this.currentPlayer)) {
            throw new IllegalArgumentException("Given player does not play in the game");
        }

        HeimlichAndCo newGame = new HeimlichAndCo(this);
        newGame.disqualifiedPlayers.add(newGame.currentPlayer);

        switch (newGame.phase) {
            //in case of die roll phase or agent move phase we can just make the next player roll the die
            case DIE_ROLL_PHASE:
            case AGENT_MOVE_PHASE:
                newGame.turnFinished();
                newGame.phase = HeimlichAndCoPhase.DIE_ROLL_PHASE;
                break;
            case CARD_PLAY_PHASE:
                newGame.nextPlayer();
                break;
            case SAFE_MOVE_PHASE:
                newGame.board.moveSafe(7);
                newGame.phase = HeimlichAndCoPhase.DIE_ROLL_PHASE;
                newGame.turnFinished();
                break;
        }
        return newGame;
    }

    /**
     * Does a given action.
     *
     * @param heimlichAndCoAction - the action to take
     * @return a new copy of the game with the action applied
     */
    @Override
    public HeimlichAndCo doAction(HeimlichAndCoAction heimlichAndCoAction) {
        HeimlichAndCo newGame = new HeimlichAndCo(this, false);
        newGame.applyAction(heimlichAndCoAction);
        return newGame;
    }

    /**
     * Progresses the game if it currently is in an indeterminant state.
     * Note: HeimlichAndCo is never in an indeterminant state therefore this method will always throw an exception.
     *
     * @throws IllegalStateException (always)
     */
    @Override
    public Game<HeimlichAndCoAction, HeimlichAndCoBoard> doAction() {
        throw new IllegalStateException("Game should never be in an indeterminate state!");
    }

    /**
     * The game as seen from the given player.
     * In this case this means that private information that the given player should not
     * be seeing from the game is stripped.
     * (playersToAgents Map, cards Map, cardStack)
     *
     * @param i - the player
     * @return a copy of this game with private information stripped
     */
    @Override
    public Game<HeimlichAndCoAction, HeimlichAndCoBoard> getGame(int i) {
        int oldCurrentPlayer = this.currentPlayer;
        this.currentPlayer = i;
        HeimlichAndCo copy = new HeimlichAndCo(this, true);
        copy.currentPlayer = oldCurrentPlayer;
        this.currentPlayer = oldCurrentPlayer;
        return copy;
    }

    /**
     * Returns the utility (usually the current score) of the player
     *
     * @param i player for which utility is wanted
     * @return utility value
     * @throws IllegalArgumentException if there is no entry for the player in the playersToAgentsMap
     */
    @Override
    public double getUtilityValue(int i) {
        if (!playersToAgentsMap.containsKey(i)) {
            throw new IllegalArgumentException("There is no entry for the given player. This might be because there is no player with the given id or because the entry for the given player was removed because it is secret information.");
        }
        if (disqualifiedPlayers.contains(i)) {
            return -1;
        }
        if (!this.isGameOver()) {
            return board.getScores().get(playersToAgentsMap.get(i));
        }
        Map<Agent, Integer> agentsToPlayersMap = new EnumMap<>(Agent.class);
        for(Map.Entry<Integer, Agent> entry : playersToAgentsMap.entrySet()) {
            agentsToPlayersMap.put(entry.getValue(), entry.getKey());
        }

        int maxScore = Integer.MIN_VALUE;
        for(Agent a : this.board.getAgents()) {
            if (this.board.getScores().get(a) > maxScore) {
                maxScore = this.board.getScores().get(a);
            }
        }

        List<Agent> winningAgents = new LinkedList<>();
        for(Agent a : this.board.getAgents()) {
            if (this.board.getScores().get(a) == maxScore) {
                winningAgents.add(a);
            }
        }

        boolean realNotDisqualifiedPlayerWon = false;
        for(Agent a: winningAgents) {
            if (agentsToPlayersMap.containsKey(a)) {
                int player = agentsToPlayersMap.get(a);
                if (!this.disqualifiedPlayers.contains(player)) {
                    realNotDisqualifiedPlayerWon = true;
                    break;
                }
            }
        }

        if (realNotDisqualifiedPlayerWon) {
            return board.getScores().get(playersToAgentsMap.get(i));
        } else {
            return 0;
        }


    }

    /**
     * Returns whether the given action is a valid action in the current state of the game.
     *
     * @param action - the action
     * @return whether the action is valid
     */
    @Override
    public boolean isValidAction(HeimlichAndCoAction action) {
        if (action == null) {
            return false;
        }
        return getPossibleActions().contains(action);
    }

    @Override
    public boolean supportsDisqualification() {
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Number of players: ").append(numberOfPLayers).append("\n");
        builder.append("CurrentPlayer: ").append(currentPlayer).append(" -> Agent ").append(playersToAgentsMap.get(currentPlayer).toString()).append("\n");
        if (withCards) {
            builder.append("Playing with cards: ").append(cardStack.count()).append(" cards left on the stack").append("\n");
            if (cards.get(currentPlayer).isEmpty()) {
                builder.append("Cards of current player:\n");
                for (HeimlichAndCoCard card : cards.get(currentPlayer)) {
                    builder.append(card.toString()).append("\n");
                }
            }
        } else {
            builder.append("Playing without cards.\n");
        }
        builder.append(board.toString());
        return builder.toString();
    }

    /**
     * @return the original action records
     */
    @Override
    public List<ActionRecord<HeimlichAndCoAction>> getActionRecords() {
        return actionRecords;
    }

    /**
     * @return the original reference of the board
     */
    @Override
    public HeimlichAndCoBoard getBoard() {
        return board;
    }

    public CardStack<HeimlichAndCoCard> getCardStack() {
        return cardStack;
    }

    public void setCardStack(CardStack<HeimlichAndCoCard> cardStack) {
        this.cardStack = cardStack;
    }

    /**
     * Returns the map which the game uses to map the players to the cards they have on their hand.
     * This map is secret in the real game, so entries containing information an agent should not know (i.e. which
     * cards belong to which player) are removed when getting a copy of the game from the engine. So the map may only
     * contain one entry for the current player.
     * However, these entries may be added afterwards to use the copy of the game for MCTS for example.
     *
     * @return Map mapping player(s) to cards
     */
    public Map<Integer, List<HeimlichAndCoCard>> getCards() {
        return cards;
    }

    public HeimlichAndCoPhase getCurrentPhase() {
        return phase;
    }

    @Override
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getCurrentTurnPlayer() {
        return currentTurnPlayer;
    }

    public Set<Integer> getDisqualifiedPlayers() {
        return disqualifiedPlayers;
    }

    @Override
    public int getMaximumNumberOfPlayers() {
        return HeimlichAndCo.MAXIMUM_NUMBER_OF_PLAYERS;
    }

    @Override
    public int getMinimumNumberOfPlayers() {
        return HeimlichAndCo.MINIMUM_NUMBER_OF_PLAYERS;
    }

    /**
     * Determines the next phase after a dieRollAction.
     *
     * @return the phase the game should enter
     */
    private HeimlichAndCoPhase getNextPhaseAfterDieRoll() {
        if (phase == HeimlichAndCoPhase.DIE_ROLL_PHASE) {
            return HeimlichAndCoPhase.AGENT_MOVE_PHASE;
        } else {
            throw new IllegalStateException(ILLEGAL_STATE_MESSAGE);
        }
    }

    /**
     * Determines the next phase after a safeMoveAction.
     *
     * @return the phase the game should enter
     */
    private HeimlichAndCoPhase getNextPhaseAfterSafeMoveAction() {
        if (phase == HeimlichAndCoPhase.SAFE_MOVE_PHASE) {
            return HeimlichAndCoPhase.DIE_ROLL_PHASE;
        } else {
            throw new IllegalStateException(ILLEGAL_STATE_MESSAGE);
        }
    }

    @Override
    public int getNumberOfPlayers() {
        return numberOfPLayers;
    }

    public int getPlayersSkippedInARowDuringCardPhase() {
        return playersSkippedInARowDuringCardPhase;
    }

    public void setPlayersSkippedInARowDuringCardPhase(int playersSkippedInARowDuringCardPhase) {
        this.playersSkippedInARowDuringCardPhase = playersSkippedInARowDuringCardPhase;
    }

    /**
     * Returns the map which the game uses to map the players to their agents.
     * This map is secret in the real game, so entries containing information an agent should not know (i.e. which
     * agent belongs to which player) are removed when getting a copy of the game from the engine. So the map may only
     * contain one entry for the current player.
     * However, these entries may be added afterwards to use the copy of the game for MCTS for example.
     *
     * @return Map mapping player(s) to agent(s)
     */
    public Map<Integer, Agent> getPlayersToAgentsMap() {
        return playersToAgentsMap;
    }

    /**
     * Collects all possible actions for the given game state and returns them as a set.
     * If the game is over en empty set is returned.
     *
     * @return a set of possible actions
     */
    @Override
    public Set<HeimlichAndCoAction> getPossibleActions() {
        if (isGameOver()) {
            return new HashSet<>();
        }
        switch (phase) {
            case DIE_ROLL_PHASE:
                return HeimlichAndCoDieRollAction.getPossibleActions(allowCustomDieRolls, board.getDieFaces());
            case AGENT_MOVE_PHASE:
                return HeimlichAndCoAgentMoveAction.getPossibleActions(board, withCards);
            case CARD_PLAY_PHASE:
                Set<HeimlichAndCoAction> cardActions = new HashSet<>();
                for (HeimlichAndCoCard card : cards.get(currentPlayer)) {
                    cardActions.addAll(HeimlichAndCoCardAction.getPossibleActions(board, card));
                }
                cardActions.add(HeimlichAndCoCardAction.getSkipCardAction());
                return cardActions;
            case SAFE_MOVE_PHASE:
                return HeimlichAndCoSafeMoveAction.getPossibleActions(board);
            default:
                throw new IllegalStateException("The game is in a state it should not be in");
        }
    }

    /**
     * @return the action record of the last taken action
     */
    @Override
    public ActionRecord<HeimlichAndCoAction> getPreviousActionRecord() {
        if (!actionRecords.isEmpty()) {
            return actionRecords.getLast();
        } else {
            return null;
        }
    }

    public boolean isAllowCustomDieRolls() {
        return allowCustomDieRolls;
    }

    /**
     * Sets whether custom die rolls should be allowed or not. If set to true, this allows the agents to "choose" what
     * they want to roll. This is useful when doing MCTS or something similar.
     *
     * @param value whether to allow custom die rolls.
     */
    public void setAllowCustomDieRolls(boolean value) {
        allowCustomDieRolls = value;
    }

    /**
     * Returns whether the game is canonical.
     * Note: Always returns false
     *
     * @return false
     */
    @Override
    public boolean isCanonical() {
        return false;
    }

    /**
     * Determines whether the game is over. In HeimlichAndCo this is the case if a player has a score of 42 or more.
     *
     * @return whether the game is over
     */
    @Override
    public boolean isGameOver() {
        // the game ends, if there is a score marker that reaches the field 42, i.e. if there is a player with a score of 42 or more.
        return board.isGameOver();
    }

    public boolean isWithCards() {
        return withCards;
    }

    /**
     * Creates a map with maps each player to their agent.
     * Note: This is randomized!
     *
     * @param numberOfPLayers how many players are playing
     * @return a map with an entry for each player
     */
    private Map<Integer, Agent> createPlayersToAgentsMap(int numberOfPLayers) {
        Map<Integer, Agent> map = new HashMap<>();
        //we have players numberOfPlayers - 1
        //we also have numberOfPlayers - 1 Agents
        //therefore we can just shuffle all agents an assign them to the players
        List<Agent> shuffledAgents = Arrays.asList(this.board.getAgents());
        //there should be a different number of total agents and players (-> fake agents)
        Collections.shuffle(shuffledAgents);
        for (int i = 0; i < numberOfPLayers; i++) {
            map.put(i, shuffledAgents.get(i));
        }
        return map;
    }

    /**
     * Determines the next phase of this game depending on a taken action.
     * Precondition: The action given must already be applied to the game.
     *
     * @param action         the action taken
     * @param scoreTriggered whether scoring is/was triggered on the last actions
     * @return the phase the game should enter
     */
    private HeimlichAndCoPhase getNextPhase(HeimlichAndCoAction action, boolean scoreTriggered) {
        if (action.getClass().equals(HeimlichAndCoDieRollAction.class)) {
            return getNextPhaseAfterDieRoll();
        }

        if (action.getClass().equals(HeimlichAndCoAgentMoveAction.class)) {
            return getNextPhaseAfterAgentMove(scoreTriggered);
        }

        if (action.getClass().equals(HeimlichAndCoCardAction.class)) {
            return getNextPhaseAfterCardAction((HeimlichAndCoCardAction) action, scoreTriggered);
        }

        if (action.getClass().equals(HeimlichAndCoSafeMoveAction.class)) {
            return getNextPhaseAfterSafeMoveAction();
        }
        throw new IllegalArgumentException("The action given does not correspond to a valid type.");
    }

    /**
     * Determines the next phase after an agentMoveAction.
     *
     * @param scoreTriggered whether scoring was triggered
     * @return the phase the game should enter
     */
    private HeimlichAndCoPhase getNextPhaseAfterAgentMove(boolean scoreTriggered) {
        if (this.phase != HeimlichAndCoPhase.AGENT_MOVE_PHASE) {
            throw new IllegalStateException(ILLEGAL_STATE_MESSAGE);
        }
        if (this.withCards) {
            return HeimlichAndCoPhase.CARD_PLAY_PHASE;
        } else {
            if (scoreTriggered) {
                return HeimlichAndCoPhase.SAFE_MOVE_PHASE;
            } else {
                return HeimlichAndCoPhase.DIE_ROLL_PHASE;
            }
        }
    }

    /**
     * Determines the next phase after a cardAction.
     *
     * @param action         action taken
     * @param scoreTriggered whether scoring was triggered
     * @return the phase the game should enter
     */
    private HeimlichAndCoPhase getNextPhaseAfterCardAction(HeimlichAndCoCardAction action, boolean scoreTriggered) {
        if (action.isSkipCardAction()) {
            if (playersSkippedInARowDuringCardPhase == numberOfPLayers - disqualifiedPlayers.size()) {
                if (scoreTriggered) {
                    return HeimlichAndCoPhase.SAFE_MOVE_PHASE;
                } else {
                    return HeimlichAndCoPhase.DIE_ROLL_PHASE;
                }
            } else if (playersSkippedInARowDuringCardPhase > numberOfPLayers - disqualifiedPlayers.size()) {
                throw new IllegalStateException(ILLEGAL_STATE_MESSAGE);
            } else {
                return HeimlichAndCoPhase.CARD_PLAY_PHASE;
            }
        } else {
            return HeimlichAndCoPhase.CARD_PLAY_PHASE;
        }
    }

    /**
     * Gives information about the amount of dummy agents that need to be in play according to the rulebook.
     *
     * @return the number of dummy agents
     * @throws IllegalArgumentException if realAgents < 2
     */
    private int getNumberOfDummyAgents(int realAgents) {
        if (realAgents == 2 || realAgents == 3) {
            return 3;
        } else if (realAgents > 3) {
            return 7 - realAgents;
        }
        throw new IllegalArgumentException("Invalid amount of real players");
    }

    /**
     * Handles assigning and removing cards after an action.
     *
     * @param action            action taken
     * @param boardBeforeAction state of the board before the action was applied
     */
    private void handleCardsAfterAction(HeimlichAndCoAction action, HeimlichAndCoBoard boardBeforeAction) {
        if (action.getClass().equals(HeimlichAndCoAgentMoveAction.class)) {
            HeimlichAndCoAgentMoveAction moveAction = (HeimlichAndCoAgentMoveAction) action;
            boolean playerCanReceiveCard = withCards && cards.get(currentPlayer).size() < 4 && !cardStack.isEmpty();
            if (moveAction.movesAgentsIntoRuins(boardBeforeAction) && playerCanReceiveCard) { //check whether action moves agent into ruins and should therefore be awarded a card
                cards.get(currentPlayer).add(cardStack.drawCard());
            }
            if (moveAction.isNoMoveAction() && playerCanReceiveCard) { //special case where the player chose to draw a card instead of moving agents
                cards.get(currentPlayer).add(cardStack.drawCard());
            }
        }
        if (action.getClass().equals(HeimlichAndCoCardAction.class)) { //need to remove card if one was played
            ((HeimlichAndCoCardAction) action).removePlayedCardFromList(cards.get(currentPlayer));
            if (((HeimlichAndCoCardAction) action).isSkipCardAction()) {
                playersSkippedInARowDuringCardPhase++;
                nextPlayer();
            } else {
                playersSkippedInARowDuringCardPhase = 0;
            }
        }
    }

    /**
     * Sets the currentPlayer to the next player.
     * Extra method for this to be able to easier control the changing of it.
     */
    private void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % this.numberOfPLayers;
        if (disqualifiedPlayers.contains(currentPlayer)) {
            nextPlayer();
        }
    }

    /**
     * This method must be called when a turn ends.
     * A turn consist of a die roll, a move action, card action(s), and sometimes a safe move action.
     */
    private void turnFinished() {
        nextPlayer();
        currentTurnPlayer = currentPlayer;
        playersSkippedInARowDuringCardPhase = 0;
    }
}
