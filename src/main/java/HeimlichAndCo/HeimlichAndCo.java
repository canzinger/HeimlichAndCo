package HeimlichAndCo;
import HeimlichAndCo.Actions.*;
import HeimlichAndCo.Cards.HeimlichAndCoCard;
import HeimlichAndCo.Factories.HeimlichAndCoCardStackFactory;
import HeimlichAndCo.Util.CardStack;
import at.ac.tuwien.ifs.sge.game.ActionRecord;
import at.ac.tuwien.ifs.sge.game.Game;

import java.util.*;

public class HeimlichAndCo implements Game<HeimlichAndCoAction, HeimlichAndCoBoard>{

    // maximum of 7 players (according to the real game)
    private final static int maximumNumberOfPlayers = 7;
    private final static int minimumNumberOfPlayers = 7;
    private int currentPlayer;
    private final int numberOfPLayers;
    private final HeimlichAndCoBoard board;
    // map which saves which player is assigned to which Agent (SECRET!)
    private final Map<Integer, Agent> playersToAgentsMap;
    private HeimlichAndCoPhase phase;

    //region Turn-Variables
    private int currentTurnPlayer;
    private boolean scoreTriggered;
    //endregion

    //region Variables and fields needed for Top-Secret-Variant
    private final boolean withCards;
    private final Map<Integer, List<HeimlichAndCoCard>> cards; //cards are assigned to players not agents
    private CardStack<HeimlichAndCoCard> cardStack;
    private int playersSkippedInARowDuringCardPhase;
    //endregion


    //linked list is chosen for efficiency reasons,
    LinkedList<ActionRecord<HeimlichAndCoAction>> actionRecords;

    private boolean allowCustomDieRolls; //TODO

    //region Constructors

    public HeimlichAndCo() {
        this(0, HeimlichAndCo.maximumNumberOfPlayers, null, null, null, false);
    }

    public HeimlichAndCo(int numberOfPLayers) {
        this(0, numberOfPLayers, null, null, null, false);
    }

    //information that should not be public is stripped, there is only one thing, that is the map that maps the players to their agents
    // there might be some functions which do not work for the game with stripped information
    // an agent can find out which player belongs to him by using the playersToAgentsMap with only one entry
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
                for(Integer player: game.cards.keySet()) {
                    cards.put(player, new LinkedList<>(game.cards.get(player)));
                }
                HeimlichAndCoCard[] oldCards = game.cardStack.getCards().toArray(new HeimlichAndCoCard[] {});
                List<HeimlichAndCoCard> newCards = new LinkedList<>();
                for (HeimlichAndCoCard oldCard : oldCards) {
                    newCards.add(oldCard.clone());
                }
                cardStack = new CardStack<>(newCards);
            }
        }
    }

    //TODO custom state positions see documentation
    // for now this is used for card configuration
    public HeimlichAndCo(String board, int numberOfPLayers) {
        //TODO
        this(0,numberOfPLayers, null, null, null, board.equals("1"));
    }

    public HeimlichAndCo(HeimlichAndCo game) {
        this(game, false);
    }

    //the main constructor for this class, others call this one
    public HeimlichAndCo(int currentPlayer, int numberOfPLayers,
                List<ActionRecord<HeimlichAndCoAction>> actionRecords,
                         HeimlichAndCoBoard board, Map<Integer, Agent> playersToAgentsMap, boolean withCards)  {
        this.currentPlayer = currentPlayer;
        this.numberOfPLayers = numberOfPLayers;
        if (actionRecords == null) {
            this.actionRecords = new LinkedList<>();
        } else {
            this.actionRecords = new LinkedList<>(actionRecords); //TODO check safety
        }
        if (board == null) {
            this.board = new HeimlichAndCoBoard(numberOfPLayers + getNumberOfDummyAgents(numberOfPLayers));
        } else {
            this.board = board.clone();
        }
        if (playersToAgentsMap == null) {
            this.playersToAgentsMap = createPlayersToAgentsMap(this.numberOfPLayers);
        } else {
            this.playersToAgentsMap = new HashMap<>(playersToAgentsMap); //TODO check safety
        }
        this.withCards = withCards;
        if (withCards) {
            this.cardStack = HeimlichAndCoCardStackFactory.newInstance();
            this.cards = new HashMap<>();
            for (Integer player: this.playersToAgentsMap.keySet()) { //each player gets two cards at the start of the game
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
    }

    //endregion

    //pre-cond: this object must be initialized and have a board
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


    @Override
    public boolean isGameOver() {
        // the game ends, if there is a score marker that reaches the field 42, i.e. if there is a player with a score of 42 or more.
        return board.isGameOver();
    }

    @Override
    public int getMinimumNumberOfPlayers() {
        return HeimlichAndCo.minimumNumberOfPlayers;
    }

    @Override
    public int getMaximumNumberOfPlayers() {
        return HeimlichAndCo.maximumNumberOfPlayers;
    }

    @Override
    public int getNumberOfPlayers() {
        return numberOfPLayers;
    }

    @Override
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * just returns the current score of the player
     *
     * @param i player for which utility is wanted
     * @return utility value
     */
    @Override
    public double getUtilityValue(int i) {
        return board.getScores().get(playersToAgentsMap.get(i));
    }

    @Override
    public Set<HeimlichAndCoAction> getPossibleActions() {
        //check if the last Action triggered a Wertung
        switch(phase) {
            case DieRollPhase:
                return HeimlichAndCoDieRollAction.getPossibleActions(allowCustomDieRolls, board.getDie().getFaces());
            case AgentMovePhase:
                return HeimlichAndCoAgentMoveAction.getPossibleActions(board, withCards);
            case CardPlayPhase:
                Set<HeimlichAndCoAction> cardActions = new HashSet<>();
                for(HeimlichAndCoCard card: cards.get(currentPlayer)) {
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
     * gives the agents the possibility to get a copy of the board which can be modified however
     * @return a copy of the board
     */
    @Override
    public HeimlichAndCoBoard getBoard() {
        return board.clone();
    }

    @Override
    public Game<HeimlichAndCoAction, HeimlichAndCoBoard> doAction() {
        throw new IllegalStateException("Game should never be in an indeterminate state!");
    }

    @Override
    public boolean isValidAction(HeimlichAndCoAction action) {
        if (action == null) {
            return false;
        }
        return getPossibleActions().contains(action);
    }

    @Override
    public HeimlichAndCoAction determineNextAction() {
        throw new IllegalStateException("Game should never be in an indeterminate state!");
    }

    @Override
    public ActionRecord<HeimlichAndCoAction> getPreviousActionRecord() {
        if (!actionRecords.isEmpty()) {
            return actionRecords.getLast();
        } else {
            return null;
        }
    }

    @Override
    public List<ActionRecord<HeimlichAndCoAction>> getActionRecords() {
        //TODO make this safe, i.e. a deep copy
        return new LinkedList<>(actionRecords);
    }

    @Override
    public boolean isCanonical() {
        return false;
    }

    @Override
    public Game<HeimlichAndCoAction, HeimlichAndCoBoard> getGame(int i) {
        int oldCurrentPlayer = this.currentPlayer;
        this.currentPlayer = i;
        HeimlichAndCo copy = new HeimlichAndCo(this, true);
        this.currentPlayer = oldCurrentPlayer;
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Number of players: ").append(numberOfPLayers).append("\n");
        builder.append("CurrentPlayer: ").append(currentPlayer).append(" -> Agent ").append(playersToAgentsMap.get(currentPlayer).toString()).append("\n");
        if (withCards) {
            builder.append("Playing with cards: ").append(cardStack.count()).append(" cards left on the stack").append("\n");
        } else {
            builder.append("Playing without cards.\n");
        }
        builder.append(board.toString());
        return builder.toString();
    }

    @Override
    public HeimlichAndCo doAction(HeimlichAndCoAction heimlichAndCoAction) {
        HeimlichAndCo newGame = new HeimlichAndCo(this, false);
        newGame.applyAction(heimlichAndCoAction);
        return newGame;
    }

    /**
     * applies an action to this game, i.e. does not create a copy of this game as doAction does
     * @param action action to take
     */
    private void applyAction(HeimlichAndCoAction action) {
        if (!isValidAction(action)) {
            throw new IllegalArgumentException("Invalid Action given");
        }

        int result = action.applyAction(this.board);
        if (result == 1) {
            scoreTriggered = true;
        } else if (result == -1) {
            scoreTriggered = false;
        }
        this.actionRecords.addLast(new ActionRecord<>(currentPlayer, action));

        if (action.getClass().equals(HeimlichAndCoAgentMoveAction.class)) {
            if (((HeimlichAndCoAgentMoveAction) action).movesAgentsIntoRuins(this.board)) { //check whether action moves agent into ruins and should therefore be awarded a card
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

        phase = getNextPhase(action);
        if (phase == HeimlichAndCoPhase.SafeMovePhase) {
            currentPlayer = currentTurnPlayer;
            board.awardPoints();
        } else if (phase == HeimlichAndCoPhase.DieRollPhase) {
            turnFinished();
        }
    }

    /** gives information about the amount of dummy agents that need to be in play according to the rulebook
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

    public HeimlichAndCoPhase getCurrentPhase() {
        return phase;
    }

    //calculates the phase that will be (the action must already be applied!!!)
    private HeimlichAndCoPhase getNextPhase(HeimlichAndCoAction action) {
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


    private void turnFinished() {
        nextPlayer();
        currentTurnPlayer = currentPlayer;
        playersSkippedInARowDuringCardPhase = 0;
        scoreTriggered = false;
    }

    private void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % this.numberOfPLayers;
    }
}
