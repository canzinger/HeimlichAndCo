package HeimlichAndCo.Actions;

import HeimlichAndCo.HeimlichAndCoBoard;

public interface HeimlichAndCoAction {

    /**
     * does the action on which the function is called; either changes the board or does a die roll
     *
     * @param board board on which the action should be taken
     * @return a value determining whether the action triggers scoring (1), does not influence scoring (0), or reverse scoring (by moving safe away from agent)
     */
    int applyAction(HeimlichAndCoBoard board);

    //TODO add isValidAction

    //implementing classes should implement toString as well!
    String toString();

    //as well as .equals()
    boolean equals(Object obj);

    int hashCode();
}