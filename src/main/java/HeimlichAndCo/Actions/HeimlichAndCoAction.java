package HeimlichAndCo.Actions;

import HeimlichAndCo.HeimlichAndCoBoard;

public interface HeimlichAndCoAction {

    /**
     * does the action on which the function is called; either changes the board or does a die roll
     *
     * @param board board on which the action should be taken
     */
    void applyAction(HeimlichAndCoBoard board);

    //implementing classes should implement toString as well!
    String toString();

    //as well as .equals()
    boolean equals(Object obj);

    int hashCode();

    public HeimlichAndCoAction clone();
}
