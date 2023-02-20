package heimlichAndCo.Actions;

import heimlichAndCo.HeimlichAndCoBoard;

import java.util.HashSet;
import java.util.Set;

public class HeimlichAndCoSafeMoveAction implements HeimlichAndCoAction {

    private final int newSafeLocation;

    /**
     * Creates a new HeimlichAndCoSafeMoveAction that will move the safe to the new location.
     *
     * @param newSafeLocation new location for the safe
     */
    public HeimlichAndCoSafeMoveAction(int newSafeLocation) {
        this.newSafeLocation = newSafeLocation;
    }

    /**
     * Calculates the possible Safe Move Actions depending on a board. Will not return the action to keep the safe on
     * the location that it is currently on (i.e. that action is not allowed).
     *
     * @param board current board
     * @return Set of possible HeimlichAndCoActions
     */
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

    /**
     * Applies this action to the given board. The original board is changed.
     *
     * @param board board on which the action should be taken
     */
    @Override
    public void applyAction(HeimlichAndCoBoard board) {
        board.moveSafe(newSafeLocation);
    }

    public HeimlichAndCoSafeMoveAction clone() {
        return new HeimlichAndCoSafeMoveAction(newSafeLocation);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj.getClass().equals(HeimlichAndCoSafeMoveAction.class)) {
            HeimlichAndCoSafeMoveAction toComp = (HeimlichAndCoSafeMoveAction) obj;
            return toComp.newSafeLocation == this.newSafeLocation;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return newSafeLocation;
    }

    public String toString() {
        return "SafeMoveAction:" +
                "move safe to " + newSafeLocation;
    }
}
