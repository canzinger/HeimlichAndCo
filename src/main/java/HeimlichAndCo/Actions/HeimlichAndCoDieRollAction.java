package HeimlichAndCo.Actions;

import HeimlichAndCo.HeimlichAndCoBoard;

import java.util.HashSet;
import java.util.Set;

public class HeimlichAndCoDieRollAction implements HeimlichAndCoAction {

    /**
     * Number in case this is a custom action where the result is predetermined.
     */
    int dieRoll;
    /**
     * Whether this card is a random roll (in a real game this should always be true).
     */
    boolean randomRoll;

    //this is for a "custom" die roll

    /**
     * Creates a new HeimlichAndCoDieRollAction instance that represents a custom roll where the result of the roll is
     * predetermined.
     *
     * @param dieRoll the wanted result of the roll
     */
    public HeimlichAndCoDieRollAction(int dieRoll) {
        this.dieRoll = dieRoll;
        this.randomRoll = false;
    }

    /**
     * Creates a new HeimlichAndCoDieRollAction instance that represents a random (i.e.) real die roll.
     */
    public HeimlichAndCoDieRollAction() {
        dieRoll = 0;
        this.randomRoll = true;
    }

    /**
     * Calculates the possible die roll actions depending on whether custom rolls are allowed and the die faces that can be rolled.
     *
     * @param allowCustomRoll whether custom die rolls are allowed
     * @param dieFaces        different faces that can be rolled
     * @return Set of HeimlichAndCoActions that denote the possible actions
     */
    public static Set<HeimlichAndCoAction> getPossibleActions(boolean allowCustomRoll, int[] dieFaces) {
        Set<HeimlichAndCoAction> possibleActions = new HashSet<>();
        if (allowCustomRoll) {
            for (int face : dieFaces) {
                possibleActions.add(new HeimlichAndCoDieRollAction(face));
            }
            possibleActions.add(getRandomRollAction());
        } else {
            possibleActions.add(new HeimlichAndCoDieRollAction());
        }
        return possibleActions;
    }

    /**
     * Returns the action that represents a random die roll.
     *
     * @return the Action that denotes the random roll action.
     */
    public static HeimlichAndCoDieRollAction getRandomRollAction() {
        return new HeimlichAndCoDieRollAction();
    }

    /**
     * Applies this action to the given board. The original board is changed.
     *
     * @param board board on which the action should be taken
     */
    @Override
    public void applyAction(HeimlichAndCoBoard board) {
        if (!randomRoll) {
            board.setLastDieRoll(dieRoll);
        } else {
            board.rollDie();
            dieRoll = board.getLastDieRoll();
        }
    }

    public HeimlichAndCoDieRollAction clone() {
        if (randomRoll) {
            return new HeimlichAndCoDieRollAction();
        } else {
            return new HeimlichAndCoDieRollAction(dieRoll);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj.getClass().equals(HeimlichAndCoDieRollAction.class)) {
            HeimlichAndCoDieRollAction toComp = (HeimlichAndCoDieRollAction) obj;
            return toComp.randomRoll == this.randomRoll && toComp.dieRoll == this.dieRoll;
        }

        return false;
    }

    public int hashCode() {
        if (this.randomRoll) {
            return 1;
        } else {
            return this.dieRoll * 5;
        }
    }

    public String toString() {
        if (randomRoll) {
            if (dieRoll == 0) {
                return "DieRollAction (random)";
            } else { //adding the result of the random roll to the string in case it is known
                return "DieRollAction (random) - roll: " + dieRoll;
            }
        } else {
            return "DieRollAction (custom) - roll: " + dieRoll;
        }
    }

}
