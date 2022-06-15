package HeimlichAndCo;
import HeimlichAndCo.Util.Die;
import at.ac.tuwien.ifs.sge.game.ActionRecord;
import at.ac.tuwien.ifs.sge.game.Game;

import java.util.List;
import java.util.Set;

public class HeimlichAndCo implements Game<HeimlichAndCoAction, HeimlichAndCoBoard>{

    private final Die die;

    public HeimlichAndCo() {
        this.die = new Die();
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
        return 0;
    }

    @Override
    public int getMaximumNumberOfPlayers() {
        // maximum of 7 players (according to the real game; could probably adapt game though)
        return 0;
    }

    @Override
    public int getNumberOfPlayers() {
        return 0;
    }

    @Override
    public int getCurrentPlayer() {
        return 0;
    }

    @Override
    public double[] getGameUtilityValue() {
        return Game.super.getGameUtilityValue();
    }

    @Override
    public double getPlayerUtilityWeight(int player) {
        return Game.super.getPlayerUtilityWeight(player);
    }

    @Override
    public double[] getGameHeuristicValue() {
        return Game.super.getGameHeuristicValue();
    }

    @Override
    public double getPlayerHeuristicWeight(int player) {
        return Game.super.getPlayerHeuristicWeight(player);
    }

    @Override
    public double getUtilityValue(double... weights) {
        return Game.super.getUtilityValue(weights);
    }

    @Override
    public double getHeuristicValue(double... weights) {
        return Game.super.getHeuristicValue(weights);
    }

    @Override
    public double getUtilityValue(int i) {
        return 0;
    }

    @Override
    public double getHeuristicValue(int player) {
        return Game.super.getHeuristicValue(player);
    }

    @Override
    public Set<HeimlichAndCoAction> getPossibleActions() {
        return null;
    }

    @Override
    public HeimlichAndCoBoard getBoard() {
        return null;
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
    public Game<HeimlichAndCoAction, HeimlichAndCoBoard> getGame() {
        return Game.super.getGame();
    }

    @Override
    public Game<HeimlichAndCoAction, HeimlichAndCoBoard> getGame(int i) {
        return null;
    }

    @Override
    public String toTextRepresentation() {
        return Game.super.toTextRepresentation();
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
