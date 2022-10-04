package HeimlichAndCo.Actions;

import HeimlichAndCo.HeimlichAndCoBoard;

import java.util.HashSet;
import java.util.Set;

public class HeimlichAndCoDieRollAction implements HeimlichAndCoAction {

    //this can either be a custom action (meaning the die roll is decided by the initiator) or just an action to roll the die
    int dieRoll;
    boolean randomRoll;

    //this is for a "custom" die roll
    public HeimlichAndCoDieRollAction(int dieRoll) {
        this.dieRoll = dieRoll;
        this.randomRoll = false;
    }

    public HeimlichAndCoDieRollAction(boolean randomRoll) {
        if (!randomRoll) {
            throw new IllegalArgumentException("RandomRoll must not be true, if that is the case, use the constructor with a dieRoll as parameter");
        }
        dieRoll = 0;
        this.randomRoll = true;
    }

    /**
     * gets the possible die roll actions depending on whether custom rolls are allowed and the die faces that can be rolled
     *
     * @param allowCustomRoll whether custom die rolls are allowed
     * @param dieFaces different faces that can be rolled
     * @return Set of HeimlichAndCoActions that denote the possible actions
     */
    public static Set<HeimlichAndCoAction> getPossibleActions(boolean allowCustomRoll, int[] dieFaces) {
        Set<HeimlichAndCoAction> possibleActions = new HashSet<HeimlichAndCoAction>();
        if (allowCustomRoll) {
            for (int face : dieFaces) {
                possibleActions.add(new HeimlichAndCoDieRollAction(face));
            }
            possibleActions.add(getRandomRollAction());
        } else {
            possibleActions.add(new HeimlichAndCoDieRollAction(true));
        }
        return possibleActions;
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

    @Override
    public int applyAction(HeimlichAndCoBoard board) {
        if (!randomRoll) {
            board.setLastDieRoll(dieRoll);
        } else {
            board.rollDie();
            dieRoll = board.getLastDieRoll();
        }
        return 0;
    }

    /**
     *
     * @return the Actions that denotes the random roll action
     */
    public static HeimlichAndCoDieRollAction getRandomRollAction() {
        return new HeimlichAndCoDieRollAction(true);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof HeimlichAndCoDieRollAction) {
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

}
