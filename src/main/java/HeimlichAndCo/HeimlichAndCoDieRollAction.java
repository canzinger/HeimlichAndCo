package HeimlichAndCo;

import HeimlichAndCo.Util.Die;

import java.util.HashSet;
import java.util.Set;

public class HeimlichAndCoDieRollAction implements HeimlichAndCoAction{

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

    public static Set<HeimlichAndCoAction> getPossibleActions(boolean allowCustomRoll, int[] dieFaces) {
        Set<HeimlichAndCoAction> possibleActions = new HashSet<HeimlichAndCoAction>();
        if (allowCustomRoll) {
            for (int face : dieFaces) {
                possibleActions.add(new HeimlichAndCoDieRollAction(face));
            }
            //TODO maybe add random Roll here aswell??
        } else {
            possibleActions.add(new HeimlichAndCoDieRollAction(true));
        }
        return possibleActions;
    }

    public String toString() {
        if (randomRoll) {
            return "DieRollAction (random)\n";
        } else {
            return "DieRollAction (custom), roll: " + dieRoll + "\n";
        }
    }

    @Override
    public int doAction(HeimlichAndCoBoard board) {
        if (randomRoll) {
            board.setLastDieRoll(dieRoll);
        } else {
            board.rollDie();
            dieRoll = board.getLastDieRoll();
        }
        return 0;
    }

    //returns the action for a random die Roll
    public HeimlichAndCoDieRollAction getRandomRollAction() {
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
}
