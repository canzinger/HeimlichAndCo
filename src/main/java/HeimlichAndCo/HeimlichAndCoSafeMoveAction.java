package HeimlichAndCo;

import HeimlichAndCo.Util.Die;

import java.util.HashSet;
import java.util.Set;

public class HeimlichAndCoSafeMoveAction implements HeimlichAndCoAction{

    private final int newSafeLocation;

    public HeimlichAndCoSafeMoveAction(int newSafeLocation) {
        this.newSafeLocation = newSafeLocation;
    }

    public Set<HeimlichAndCoAction> getPossibleActions(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoAction> possibleActions = new HashSet<>();
        int currentSafePosition = board.getSafePosition();
        for (int i = 0; i < board.getNumberOfFields(); i++) {
            if (currentSafePosition != i) {
                possibleActions.add(new HeimlichAndCoSafeMoveAction(i));
            }
        }
        return possibleActions;
    }

    public String toString() {
        return "SafeMoveAction:\n" +
                "move safe to " + newSafeLocation + "\n";
    }

    //TODO change this for cards
    @Override
    public int doAction(HeimlichAndCoBoard board) {
        board.moveSafe(newSafeLocation);
        return -1;
    }
}
