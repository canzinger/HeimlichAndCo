package HeimlichAndCo;

import HeimlichAndCo.Actions.*;
import HeimlichAndCo.Cards.HeimlichAndCoCard;
import HeimlichAndCo.Enums.Agent;
import HeimlichAndCo.Enums.HeimlichAndCoPhase;
import HeimlichAndCo.Factories.HeimlichAndCoCardStackFactory;
import HeimlichAndCo.Util.CardStack;
import HeimlichAndCo.Util.ListHelpers;
import at.ac.tuwien.ifs.sge.game.ActionRecord;
import at.ac.tuwien.ifs.sge.game.Game;

import java.util.*;

/**
 * The main game class.
 */
public class HeimlichAndCo implements Game<HeimlichAndCoAction, HeimlichAndCoBoard> {

    private final static int MAXIMUM_NUMBER_OF_PLAYERS = 7;
    private final static int MINIMUM_NUMBER_OF_PLAYERS = 2;
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
                    newCards.add(oldCard.clone());
                }
                cardStack = new CardStack<>(newCards);
            }
        }
    }

    //TODO custom state positions see documentation
    // for now this is used for card configuration: board == 1 means that the game will be played with cards
    public HeimlichAndCo(String board, int numberOfPLayers) {
        //TODO
        this(0, numberOfPLayers, null, null, null, board.equals("1"));
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
     * @param numberOfPLayers    the number of players that are playing (must confine to the restrictions by minimum and
     *                           maximum players needed for playing)
     * @param actionRecords      the action records (can be null)
     * @param board              the board (can be null)
     * @param playersToAgentsMap Map which maps players to agents (can be null)
     * @param withCards          whether the game should be with or without cards
     */
    public HeimlichAndCo(int currentPlayer, int numberOfPLayers,
                         List<ActionRecord<HeimlichAndCoAction>> actionRecords,
                         HeimlichAndCoBoard board, Map<Integer, Agent> playersToAgentsMap, boolean withCards) {
        if (currentPlayer < 0 || currentPlayer >= numberOfPLayers) {
            throw new IllegalArgumentException("Current player must be a valid player." + currentPlayer);
        }
        this.currentPlayer = currentPlayer;
        if (numberOfPLayers < getMinimumNumberOfPlayers() || numberOfPLayers > getMaximumNumberOfPlayers()) {
            throw new IllegalArgumentException("Invalid value given for number of players.");
        }
        this.numberOfPLayers = numberOfPLayers;
        if (actionRecords == null) {
            this.actionRecords = new LinkedList<>();
        } else {
            this.actionRecords = ListHelpers.deepCopyActionRecordList(actionRecords);
        }
        if (board == null) {
            this.board = new HeimlichAndCoBoard(numberOfPLayers + getNumberOfDummyAgents(numberOfPLayers));
        } else {
            this.board = board.clone();
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
        this.phase = HeimlichAndCoPhase.DieRollPhase;
        this.allowCustomDieRolls = false;
    }

    /**
     * Applies an action to this game, DOES NOT create a copy of this game (in contrast to doAction).
     *
     * @param action action to take
     */
    public void applyAction(HeimlichAndCoAction action) {
        if (!isValidAction(action)) {
            throw new IllegalArgumentException("Invalid Action given");
        }

        HeimlichAndCoBoard boardCopy = this.board.clone();
        action.applyAction(this.board);
        this.actionRecords.addLast(new ActionRecord<>(currentPlayer, action));

        if (action.getClass().equals(HeimlichAndCoAgentMoveAction.class)) {
            if (((HeimlichAndCoAgentMoveAction) action).movesAgentsIntoRuins(boardCopy)) { //check whether action moves agent into ruins and should therefore be awarded a card
                if (withCards && cards.get(currentPlayer).size() < 4 && !cardStack.isEmpty()) { //maximum of 4 cards per player
                    cards.get(currentPlayer).add(cardStack.drawCard());
                }
            } else if (((HeimlichAndCoAgentMoveAction) action).isNoMoveAction()) { //special case where the player chose to draw a card instead of moving agents
                if (withCards) { //maximum of 4 cards per player
                    if (cards.get(currentPlayer).size() < 4 && !cardStack.isEmpty()) {
                        cards.get(currentPlayer).add(cardStack.drawCard());
                    }
                } else {
                    throw new IllegalArgumentException("An invalid action was given when playing without cards");
                }
            }
        } else if (action.getClass().equals(HeimlichAndCoCardAction.class)) { //need to remove card if one played
            ((HeimlichAndCoCardAction) action).removePlayedCardFromList(cards.get(currentPlayer));
            if (((HeimlichAndCoCardAction) action).isSkipCardAction()) {
                playersSkippedInARowDuringCardPhase++;
                nextPlayer();
            }
        }

        phase = getNextPhase(action, board.scoringTriggered());
        if (phase == HeimlichAndCoPhase.SafeMovePhase) {
            currentPlayer = currentTurnPlayer;
            board.awardPoints();
        } else if (phase == HeimlichAndCoPhase.DieRollPhase) {
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
        this.currentPlayer = oldCurrentPlayer;
        return copy;
    }

    /**
     * Returns the current score of the player
     * Exception: when the game is over, only the winning player is awarded utility, the others get 0 (because they lost).
     *
     * @param i player for which utility is wanted
     * @return utility value
     */
    @Override
    public double getUtilityValue(int i) {
        if (isGameOver()) {
            int maxScore = 0;
            for (Agent a : board.getScores().keySet()) {
                if (board.getScores().get(a) > maxScore) {
                    maxScore = board.getScores().get(a);
                }
            }
            if (board.getScores().get(playersToAgentsMap.get(i)) == maxScore) {
                return maxScore;
            } else {
                return 0;
            }
        } else {
            return board.getScores().get(playersToAgentsMap.get(i));
        }
    }

    /**
     * Returns whether the given action is a valid action in the curretn state of the game.
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
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Number of players: ").append(numberOfPLayers).append("\n");
        builder.append("CurrentPlayer: ").append(currentPlayer).append(" -> Agent ").append(playersToAgentsMap.get(currentPlayer).toString()).append("\n");
        if (withCards) {
            builder.append("Playing with cards: ").append(cardStack.count()).append(" cards left on the stack").append("\n");
            if (cards.get(currentPlayer).size() > 0) {
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

    @Override
    public int getMaximumNumberOfPlayers() {
        return HeimlichAndCo.MAXIMUM_NUMBER_OF_PLAYERS;
    }

    @Override
    public int getMinimumNumberOfPlayers() {
        return HeimlichAndCo.MINIMUM_NUMBER_OF_PLAYERS;
    }

    @Override
    public int getNumberOfPlayers() {
        return numberOfPLayers;
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
            case DieRollPhase:
                return HeimlichAndCoDieRollAction.getPossibleActions(allowCustomDieRolls, board.getDieFaces());
            case AgentMovePhase:
                return HeimlichAndCoAgentMoveAction.getPossibleActions(board, withCards);
            case CardPlayPhase:
                Set<HeimlichAndCoAction> cardActions = new HashSet<>();
                for (HeimlichAndCoCard card : cards.get(currentPlayer)) {
                    cardActions.addAll(HeimlichAndCoCardAction.getPossibleActions(board, card));
                }
                cardActions.add(HeimlichAndCoCardAction.getSkipCardAction());
                return cardActions;
            case SafeMovePhase:
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
     * Creates a map with maps each player to their agent.
     * Note: This is randomized!
     *
     * @param numberOfPLayers how many players are playing
     * @return a map with an entry for each player
     */
    private Map<Integer, Agent> createPlayersToAgentsMap(int numberOfPLayers) {
        Map<Integer, Agent> playersToAgentsMap = new HashMap<>();
        //we have players numberOfPlayers - 1
        //we also have numberOfPlayers - 1 Agents
        //therefore we can just shuffle all agents an assign them to the players
        List<Agent> shuffledAgents = Arrays.asList(this.board.getAgents());
        //there should be a different number of total agents and players (-> fake agents)
        Collections.shuffle(shuffledAgents);
        for (int i = 0; i < numberOfPLayers; i++) {
            playersToAgentsMap.put(i, shuffledAgents.get(i));
        }
        return playersToAgentsMap;
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
            if (phase == HeimlichAndCoPhase.DieRollPhase) {
                return HeimlichAndCoPhase.AgentMovePhase;
            } else {
                throw new IllegalStateException("The game is in a state it should not be in.");
            }
        }

        if (action.getClass().equals(HeimlichAndCoAgentMoveAction.class)) {
            if (this.phase != HeimlichAndCoPhase.AgentMovePhase) {
                throw new IllegalStateException("The game is in a state it should not be in.");
            }
            if (this.withCards) {
                return HeimlichAndCoPhase.CardPlayPhase;
            } else {
                if (scoreTriggered) {
                    return HeimlichAndCoPhase.SafeMovePhase;
                } else {
                    return HeimlichAndCoPhase.DieRollPhase;
                }
            }
        }

        if (action.getClass().equals(HeimlichAndCoCardAction.class)) {
            HeimlichAndCoCardAction cardAction = (HeimlichAndCoCardAction) action;
            if (cardAction.isSkipCardAction()) {
                if (playersSkippedInARowDuringCardPhase == numberOfPLayers) {
                    if (scoreTriggered) {
                        return HeimlichAndCoPhase.SafeMovePhase;
                    } else {
                        return HeimlichAndCoPhase.DieRollPhase;
                    }
                } else if (playersSkippedInARowDuringCardPhase > numberOfPLayers) {
                    throw new IllegalStateException("The game is in a state it should not be in.");
                } else {
                    return HeimlichAndCoPhase.CardPlayPhase;
                }
            } else {
                return HeimlichAndCoPhase.CardPlayPhase;
            }
        }

        if (action.getClass().equals(HeimlichAndCoSafeMoveAction.class)) {
            if (phase == HeimlichAndCoPhase.SafeMovePhase) {
                return HeimlichAndCoPhase.DieRollPhase;
            } else {
                throw new IllegalStateException("The game is in a state it should not be in.");
            }
        }
        throw new IllegalArgumentException("The action given does not correspond to a valid type.");
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
     * Sets the currentPlayer to the next player.
     * Extra method for this to be able to easier control the changing of it.
     */
    private void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % this.numberOfPLayers;
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
