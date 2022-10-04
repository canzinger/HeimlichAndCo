package HeimlichAndCo.Actions;

import HeimlichAndCo.HeimlichAndCoBoard;

import java.util.HashSet;
import java.util.Set;

public class HeimlichAndCoSafeMoveAction implements HeimlichAndCoAction {

    private final int newSafeLocation;

    public HeimlichAndCoSafeMoveAction(int newSafeLocation) {
        this.newSafeLocation = newSafeLocation;
    }

    public static Set<HeimlichAndCoAction> getPossibleActions(HeimlichAndCoBoard board) {
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
        return "SafeMoveAction:" +
                "move safe to " + newSafeLocation;
    }

    @Override
    public int applyAction(HeimlichAndCoBoard board) {
        board.moveSafe(newSafeLocation);
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof HeimlichAndCoSafeMoveAction) {
            HeimlichAndCoSafeMoveAction toComp = (HeimlichAndCoSafeMoveAction) obj;
            return toComp.newSafeLocation == this.newSafeLocation;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return newSafeLocation;
    }
}
