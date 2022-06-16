package HeimlichAndCo;

import java.util.HashMap;
import java.util.Map;

public class HeimlichAndCoBoard {
    // save where the agents are
    // save where the score markers of each agents are

    /**
     * saves the positions of each agent
     */
    private Map<Agent, Integer> positions;

    /**
     * saves the current points of each agent
     */
    private Map<Agent, Integer> scores;

    private int safePosition;

    // constructors

    public HeimlichAndCoBoard() {
        this.positions = new HashMap<Agent, Integer>();
        this.scores = new HashMap<Agent, Integer>();
        this.safePosition = 7; //the default starting position for the safe
    }


    /**
     * determines the amounts of points an agent should receive based on the field they are on
     *
     * @param fieldId field in question
     * @return points that would be awarded
     */
    public int getPointsForField(int fieldId) {
        if (fieldId < 0 || fieldId > 11) {
            throw new IllegalArgumentException("Illegal fieldId, either too large or too small");
        }
        if (fieldId == 11) {
            return -3; //the only case where the agent is awarded negative points for a field (field 11)
        } else {
            return fieldId;
        }
    }

    @Override
    public HeimlichAndCoBoard clone() {
        HeimlichAndCoBoard newBoard = new HeimlichAndCoBoard();
        newBoard.safePosition = this.safePosition;
        newBoard.positions = new HashMap<Agent, Integer>(this.positions); //TODO check if this is safe
        newBoard.scores = new HashMap<Agent, Integer>(this.scores); //TODO check if this is safe
        return newBoard;
        //TODO adapt to new variables in HeimlichAndCoBoard
    }

}

enum Agent {
    Perry,
    Schulz,
    Doyle,
    Jaques,
    Bucci,
    Mirkov,
    Larsson
}
