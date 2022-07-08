package HeimlichAndCo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface HeimlichAndCoAction {

    /**
     * does the action on which the function is called; either changes the board or does a die roll
     *
     * @param board board on which the action should be taken
     * @return 0 if the board was changed, or -1 if the result is a change in the currentPlayer
     */
    public int doAction(HeimlichAndCoBoard board);

    //TODO add isValidAction

    //implementing classes should implement toString as well!

    //aswell as .equals()
}
