package HeimlichAndCo;

import java.util.HashMap;
import java.util.Map;

public class HeimlichAndCoBoard {

    /**
     * saves which agents are in play
     */
    private final Agent[] agents;

    /**
     * saves the positions of each agent
     */
    private Map<Agent, Integer> agentsPositions;

    /**
     * saves the current points of each agent
     */
    private Map<Agent, Integer> scores;
    private int safePosition;
    private final static int numberOfFields = 12;

    // constructors

    public HeimlichAndCoBoard() {
        agents = getParticipatingAgents(7); //default number of agents
        this.agentsPositions = getAgentMapWithZeros(agents);
        this.scores = getAgentMapWithZeros(agents);
        this.safePosition = 7; //the default starting position for the safe
    }

    public HeimlichAndCoBoard(int numberOfAgents) {
        agents = getParticipatingAgents(numberOfAgents);
        this.agentsPositions = getAgentMapWithZeros(agents);
        this.scores = getAgentMapWithZeros(agents);
        this.safePosition = 7; //the default starting position for the safe
    }

    /**
     * moves an agent forward a certain number of fields
     * @param a Agent to be moved
     * @param numberOfFields number of fields the agent should be moved forward
     */
    public void moveAgent(Agent a, int numberOfFields) {
        int oldPosition = agentsPositions.get(a);
        agentsPositions.replace(a, (oldPosition + numberOfFields) % numberOfFields);
    }

    public void moveSafe(int fieldId) {
        safePosition = fieldId;
    }

    /**
     * awards all playings agents points according to their position on the board
     */
    public void awardPoints() {
        for (Agent a : this.agents) {
            int fieldId = agentsPositions.get(a);
            int awardedPoints = getPointsForField(fieldId);
            int oldPoints = scores.get(a);
            scores.replace(a, awardedPoints + oldPoints);
        }
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

    /**
     *
     * @param number number of participating agents
     * @return Agent array with the participating agents
     */
    private Agent[] getParticipatingAgents(int number) {
        Agent[] totalAgents = Agent.values();
        Agent[] participatingAgents = new Agent[number];
        for (int i = 0; i < number; i++) {
            participatingAgents[i] = totalAgents[i];
        }
        return participatingAgents;
    }

    /**
     *
     * @param agents array of participating agents
     * @return Map with an entry of 0 for each agent
     */
    private Map<Agent, Integer> getAgentMapWithZeros(Agent[] agents) {
        Map<Agent, Integer> map = new HashMap<Agent, Integer>();
        for (Agent a: agents) {
            map.put(a, 0);
        }
        return map;
    }

    @Override
    public HeimlichAndCoBoard clone() {
        HeimlichAndCoBoard newBoard = new HeimlichAndCoBoard();
        newBoard.safePosition = this.safePosition;
        newBoard.agentsPositions = new HashMap<Agent, Integer>(this.agentsPositions); //TODO check if this is safe 100%
        newBoard.scores = new HashMap<Agent, Integer>(this.scores); //TODO check if this is safe 100%
        System.arraycopy(this.agents, 0, newBoard.agents, 0, this.agents.length);
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
