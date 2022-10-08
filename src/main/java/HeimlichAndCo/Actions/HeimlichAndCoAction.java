package HeimlichAndCo.Actions;

import HeimlichAndCo.HeimlichAndCoBoard;

public interface HeimlichAndCoAction {

    /**
     * does the action on which the function is called; either changes the board or does a die roll
     *
     * @param board board on which the action should be taken
     */
    void applyAction(HeimlichAndCoBoard board);

    HeimlichAndCoAction clone();

    //as well as .equals()
    boolean equals(Object obj);

    int hashCode();

    //implementing classes should implement toString as well!
    String toString();
}
