package HeimlichAndCo;
import at.ac.tuwien.ifs.sge.game.ActionRecord;
import at.ac.tuwien.ifs.sge.game.Game;

import java.util.*;

public class HeimlichAndCo implements Game<HeimlichAndCoAction, HeimlichAndCoBoard>{

    private int currentPlayer;
    private int numberOfPLayers;
    private HeimlichAndCoBoard board;
    // map which saves which player is assigned to which Agent (SECRET!)
    private final Map<Integer, Agent> playersToAgentsMap;

    LinkedList<ActionRecord<HeimlichAndCoAction>> actionRecords;

    private boolean allowCustomDieRolls; //TODO

    public HeimlichAndCo() {
        playersToAgentsMap = new HashMap<>();
    }

    //information that should not be public is stripped, there is only one thing, that is the map that maps the players to their agents
    // there might be some functions which do not work for the game with stripped information
    // an agent can find out which player belongs to him by using the playersToAgentsMap with only one entry
    public HeimlichAndCo(HeimlichAndCo game, boolean stripInformation) {
        //TODO do the same for cards
        if (stripInformation) {
            playersToAgentsMap = new HashMap<>();
            playersToAgentsMap.put(game.currentPlayer, game.playersToAgentsMap.get(game.currentPlayer));
        } else {
            playersToAgentsMap = new HashMap<>(game.playersToAgentsMap);
        }
        board = game.board.clone();
        numberOfPLayers = game.numberOfPLayers;
        currentPlayer = game.currentPlayer;
        actionRecords = new LinkedList<>();
    }

    @Override
    public boolean isGameOver() {
        // the game ends, if there is a score marker that reaches the field 42, i.e. if there is a player with a score of 42 or more.

        //is only over if it is also the end of a round???
        //TODO
        return board.isGameOver();
    }

    @Override
    public int getMinimumNumberOfPlayers() {
        // minimum number of 2 players
        return 2;
    }

    @Override
    public int getMaximumNumberOfPlayers() {
        // maximum of 7 players (according to the real game; could probably adapt game though)
        return 7;
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
    public double getHeuristicValue(int player) {
        return Game.super.getHeuristicValue(player);
    }

    @Override
    public Set<HeimlichAndCoAction> getPossibleActions() {
        //check if the last Action triggered a Wertung
        if (actionRecords.isEmpty()) {
            return HeimlichAndCoDieRollAction.getPossibleActions(allowCustomDieRolls, board.getDie().getFaces());
        } else {
            //TODO maybe improve
            HeimlichAndCoAction lastAction = getPreviousAction();
            if (lastAction instanceof HeimlichAndCoDieRollAction) {
                return HeimlichAndCoAgentMoveAction.getPossibleActions(board);
            } else if (lastAction instanceof HeimlichAndCoAgentMoveAction) {
                HeimlichAndCoAgentMoveAction agentMoveAction = (HeimlichAndCoAgentMoveAction) lastAction;
                if (agentMoveAction.isTriggersScoringRound()) {
                    return HeimlichAndCoSafeMoveAction.getPossibleActions(board);
                } else {
                    return HeimlichAndCoDieRollAction.getPossibleActions(allowCustomDieRolls, board.getDie().getFaces());
                }
            } else if (lastAction instanceof HeimlichAndCoSafeMoveAction) {
                return HeimlichAndCoDieRollAction.getPossibleActions(allowCustomDieRolls, board.getDie().getFaces());
            }
        }
        return null;
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
        //TODO this should actually never happen in my game!
        return Game.super.doAction();
    }

    @Override
    public boolean isValidAction(HeimlichAndCoAction action) {
        if (action == null) {
            return false;
        }
        return Game.super.isValidAction(action);
    }

    @Override
    public HeimlichAndCoAction determineNextAction() {
        //TODO this should never actually happen!
        return null;
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

    //TODO change for more information
    @Override
    public String toTextRepresentation() {
        return "Number of players: " + numberOfPLayers + "\n" +
                "CurrentPlayer: " + currentPlayer + "\n" +
                board.toString();
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
        int ret = action.doAction(this.board);
        this.actionRecords.addLast(new ActionRecord<>(currentPlayer, action));
        if (ret == -1) {
            this.currentPlayer = (this.currentPlayer + 1) % numberOfPLayers;
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
}
