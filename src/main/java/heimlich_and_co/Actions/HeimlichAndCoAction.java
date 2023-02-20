package heimlich_and_co.Actions;

import heimlich_and_co.HeimlichAndCoBoard;

public interface HeimlichAndCoAction {

    /**
     * Applies this action to the given board. The original board is changed.
     *
     * @param board board on which the action should be taken
     */
    void applyAction(HeimlichAndCoBoard board);

    HeimlichAndCoAction clone();

    /**
     * Checks whether the given object is equal to this action. Must take semantics into account where some actions
     * should be considered equal in terms of the outcome when applying them to a board. (E.g. when the order of some
     * parameters does not matter because they are order invariant).
     *
     * @param obj the object which should be compared
     * @return whether this and obj are considered equal
     */
    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    @Override
    String toString();
}
