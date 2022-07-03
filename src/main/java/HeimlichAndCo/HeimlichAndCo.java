package HeimlichAndCo;
import HeimlichAndCo.Util.Die;
import at.ac.tuwien.ifs.sge.game.ActionRecord;
import at.ac.tuwien.ifs.sge.game.Game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HeimlichAndCo implements Game<HeimlichAndCoAction, HeimlichAndCoBoard>{

    private final Die die;
    private int currentPlayer;
    private int numberOfPLayers;
    private HeimlichAndCoBoard board;
    // map which saves which player is assigned to which Agent (SECRET!)
    private final Map<Integer, Agent> playersToAgentsMap;

    public HeimlichAndCo() {
        this.die = new Die();
        playersToAgentsMap = new HashMap<Integer, Agent>();
    }

    @Override
    public boolean isGameOver() {
        // the game ends, if there is a score marker that reaches the field 42, i.e. if there is a player with a score of 42 or more.

        //is only over if it is also the end of a round
        return false;
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
        return Game.super.doAction();
    }

    @Override
    public boolean isValidAction(HeimlichAndCoAction action) {
        return Game.super.isValidAction(action);
    }

    @Override
    public HeimlichAndCoAction determineNextAction() {
        return null;
    }

    @Override
    public ActionRecord<HeimlichAndCoAction> getPreviousActionRecord() {
        return Game.super.getPreviousActionRecord();
    }

    @Override
    public HeimlichAndCoAction getPreviousAction() {
        return Game.super.getPreviousAction();
    }

    @Override
    public List<ActionRecord<HeimlichAndCoAction>> getActionRecords() {
        return null;
    }

    @Override
    public int getNumberOfActions() {
        return Game.super.getNumberOfActions();
    }

    @Override
    public boolean isCanonical() {
        return false;
    }

    @Override
    public Game<HeimlichAndCoAction, HeimlichAndCoBoard> getGame(int i) {
        return null;
    }

    //TODO implement other information other than board
    @Override
    public String toTextRepresentation() {
        return board.toString();
    }

    @Override
    public Game<HeimlichAndCoAction, HeimlichAndCoBoard> doAction(HeimlichAndCoAction heimlichAndCoAction) {
        return null;
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
