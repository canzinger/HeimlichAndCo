package HeimlichAndCo;

import HeimlichAndCo.Enums.Agent;
import HeimlichAndCo.Util.Die;

import java.util.*;

public class HeimlichAndCoBoard {

    /**
     * the number of fields of the board
     */
    private final static int NUMBER_OF_FIELDS = 12;
    /**
     * saves which agents are in play.
     */
    private final Agent[] agents;
    private final Die die;
    /**
     * saves the positions of each agent.
     */
    private Map<Agent, Integer> agentsPositions; //must have an entry for all playing agents
    /**
     * saves the current points of each agent.
     */
    private Map<Agent, Integer> scores; //must have an entry for all playing agents
    private int safePosition;
    /**
     * saves the last result of a die roll.
     */
    private int lastDieRoll;
    /**
     * Saves at any point in time whether scoring was triggered for an agent or not.
     * will become true for an agent when it is moved onto the safe and will become false for an agent when it is moved off the safe or the safe is moved
     */
    private Map<Agent, Boolean> scoringTriggeredForAgent;

    //region constructors

    /**
     * Creates a new board instance with 7 agents
     */
    public HeimlichAndCoBoard() {
        this(7);
    }

    /**
     * Creates a new board instance with the given number of agents
     *
     * @param numberOfAgents amount of agents that are playing (must be between 5 and 7)
     */
    public HeimlichAndCoBoard(int numberOfAgents) {
        this(HeimlichAndCoBoard.getParticipatingAgents(numberOfAgents));
    }

    /**
     * Creates a new board instance with the given agents.
     *
     * @param agents Agents which are playing
     */
    public HeimlichAndCoBoard(Agent[] agents) {
        if (agents == null || agents.length < 5 || agents.length > 7) {
            throw new IllegalArgumentException("Invalid amount of playing agents, must be between 5 and 7");
        }
        this.agents = Arrays.copyOf(agents, agents.length);
        this.agentsPositions = getAgentMapWithZeros(agents);
        this.scores = getAgentMapWithZeros(agents);
        this.safePosition = 7; //the default starting position for the safe
        this.die = new Die();
        scoringTriggeredForAgent = getNewScoringTriggeredForAgentMap();
    }

    /**
     * Creates a new board instance with the given agents positions (must be non-null).
     * Optional parameter scores allows to set the scores for the agents to different values.
     * AgentsPositions and Scores must have the same key set (i.e. same agents present as keys).
     * Method should act as helper for testing agents/the game.
     *
     * @param agentsPositions starting positions for all playing agents (must have between 5 and 7 entries)
     * @param scores          starting scores for all playing agents (OPTIONAL) (must have between 5 and 7 entries if given)
     */
    public HeimlichAndCoBoard(Map<Agent, Integer> agentsPositions, Map<Agent, Integer> scores) {
        if (agentsPositions.size() < 5 || agentsPositions.size() > 7) {
            throw new IllegalArgumentException("Invalid amount of playing agents, must be between 5 and 7");
        }
        this.agents = agentsPositions.keySet().toArray(new Agent[0]);
        this.agentsPositions = new HashMap<>(agentsPositions);
        if (scores != null && scores.size() != 0) {
            if (!agentsPositions.keySet().equals(scores.keySet())) {
                throw new IllegalArgumentException("Scores and agentsPositions must have the same key set");
            }
            this.scores = new HashMap<>(scores);
        }
        this.safePosition = 7;
        this.die = new Die();
        scoringTriggeredForAgent = getNewScoringTriggeredForAgentMap();
    }

    //endregion

    /**
     * Calculates for each field which agents are on it.
     *
     * @return A map with entries for each field and a corresponding array giving the agents on the given field
     */
    public Map<Integer, Agent[]> agentsOnFields() {
        Map<Integer, Agent[]> agentsMap = new HashMap<>();
        for (int i = 0; i < NUMBER_OF_FIELDS; i++) {
            List<Agent> agents = new LinkedList<>();
            for (Agent a : this.agents) {
                if (agentsPositions.get(a) == i) {
                    agents.add(a);
                }
            }
            agentsMap.put(i, agents.toArray(new Agent[0]));
        }
        return agentsMap;
    }

    /**
     * Awards all playings agents points according to their position on the board.
     */
    public void awardPoints() {
        for (Agent a : this.agents) {
            int fieldId = agentsPositions.get(a);
            int awardedPoints = getPointsForField(fieldId);
            int oldPoints = scores.get(a);
            scores.replace(a, awardedPoints + oldPoints);
        }
        scoringTriggeredForAgent = getNewScoringTriggeredForAgentMap();
    }

    @Override
    public HeimlichAndCoBoard clone() {
        HeimlichAndCoBoard newBoard = new HeimlichAndCoBoard(this.agents);
        newBoard.lastDieRoll = this.lastDieRoll;
        newBoard.safePosition = this.safePosition;
        newBoard.agentsPositions = new HashMap<>(this.agentsPositions);
        newBoard.scores = new HashMap<>(this.scores);
        System.arraycopy(this.agents, 0, newBoard.agents, 0, this.agents.length);
        newBoard.scoringTriggeredForAgent = new HashMap<>(this.scoringTriggeredForAgent);
        return newBoard;
    }

    /**
     * Determines the amounts of points an agent should receive based on the field they are on.
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
     * Moves an agent a certain number of fields/buildings.
     *
     * @param a              Agent to be moved
     * @param numberOfFields Number of fields the agent should be moved
     */
    public void moveAgent(Agent a, int numberOfFields) {
        if (agentsPositions.get(a) == safePosition && numberOfFields % HeimlichAndCoBoard.NUMBER_OF_FIELDS != 0) {
            scoringTriggeredForAgent.put(a, false);
        }
        agentsPositions.replace(a, (agentsPositions.get(a) + numberOfFields) % HeimlichAndCoBoard.NUMBER_OF_FIELDS);
        if (agentsPositions.get(a) == safePosition && numberOfFields % HeimlichAndCoBoard.NUMBER_OF_FIELDS != 0) {
            scoringTriggeredForAgent.put(a, true);
        }
    }

    /**
     * Moves an agent to a specific field/building.
     *
     * @param a          Agent to be moved
     * @param buildingId The building/field id of where the agent should be moved to
     */
    public void moveAgentToAbsoluteBuilding(Agent a, int buildingId) {
        if (buildingId < 0 || buildingId >= NUMBER_OF_FIELDS) {
            throw new IllegalArgumentException("Invalid buildingId.");
        }
        if (agentsPositions.get(a) == safePosition && buildingId != agentsPositions.get(a)) {
            scoringTriggeredForAgent.put(a, false);
        }
        agentsPositions.replace(a, buildingId);
        if (agentsPositions.get(a) == safePosition && buildingId != agentsPositions.get(a)) {
            scoringTriggeredForAgent.put(a, true);
        }
    }

    /**
     * Moves multiple agents by a certain number of fields.
     *
     * @param agentsMoves Map denoting the amount of fields certain agents should be moved
     */
    public void moveAgents(Map<Agent, Integer> agentsMoves) {
        for (Agent a : agentsMoves.keySet()) {
            moveAgent(a, agentsMoves.get(a));
        }
    }

    /**
     * Moves safe to a given building
     *
     * @param buildingId Target building for safe (ABSOLUTE POSITION)
     */
    public void moveSafe(int buildingId) {
        if (buildingId < 0 || buildingId >= NUMBER_OF_FIELDS) {
            throw new IllegalArgumentException("Invalid buildingId.");
        }
        if (safePosition != buildingId) {
            scoringTriggeredForAgent = getNewScoringTriggeredForAgentMap(); //this will always be reset when the safe is moved
        }
        safePosition = buildingId;
    }

    /**
     * Rolls the die and therefore sets the result of the last die roll.
     */
    public void rollDie() {
        this.lastDieRoll = die.roll();
    }

    /**
     * Returns true if points should be awarded.
     *
     * @return whether scoring was triggered
     */
    public boolean scoringTriggered() {
        for (Agent a : scoringTriggeredForAgent.keySet()) {
            if (scoringTriggeredForAgent.get(a)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        Map<Integer, Agent[]> agentsOnField = agentsOnFields();
        StringBuilder stringBuilder = new StringBuilder();

        //printing the first row of houses
        stringBuilder.append("       ___       ___       ___       ___       ___\n");
        stringBuilder.append("     /__1__\\   /__2__\\   /__3__\\   /__4__\\   /__5__\\\n");
        stringBuilder.append("    ");
        for (int i = 1; i < 6; i++) {
            stringBuilder.append("|");
            for (int j = 0; j < 4; j++) {
                if (agentsOnField.get(i).length > j) {
                    stringBuilder.append(agentsOnField.get(i)[j].toString().charAt(0)).append(" ");
                } else {
                    stringBuilder.append("  ");
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("| ");
        }
        stringBuilder.append("\n");
        stringBuilder.append("    ");
        for (int i = 1; i < 6; i++) {
            stringBuilder.append("|");
            for (int j = 4; j < 8; j++) {
                if (agentsOnField.get(i).length > j) {
                    stringBuilder.append(agentsOnField.get(i)[j].toString().charAt(0)).append(" ");
                } else {
                    stringBuilder.append("  ");
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("| ");
        }
        stringBuilder.append("\n");
        stringBuilder.append("    ");
        for (int i = 1; i < 6; i++) {
            if (safePosition == i) {
                stringBuilder.append("|__[$]__| ");
            } else {
                stringBuilder.append("|_______| ");
            }
        }
        stringBuilder.append("\n");

        //printing the second row of houses (there are only two houses in this row
        stringBuilder.append("   _+_                                             ___\n");
        stringBuilder.append(" /__0__\\                                         /__6__\\\n");
        //first row of agents on second row
        stringBuilder.append("|");
        for (int j = 0; j < 4; j++) {
            if (agentsOnField.get(0).length > j) {
                stringBuilder.append(agentsOnField.get(0)[j].toString().charAt(0)).append(" ");
            } else {
                stringBuilder.append("  ");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("| ");
        stringBuilder.append("                                      |");
        for (int j = 0; j < 4; j++) {
            if (agentsOnField.get(6).length > j) {
                stringBuilder.append(agentsOnField.get(6)[j].toString().charAt(0)).append(" ");
            } else {
                stringBuilder.append("  ");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("|\n");

        //second row of agents on second row
        stringBuilder.append("|");
        for (int j = 4; j < 8; j++) {
            if (agentsOnField.get(0).length > j) {
                stringBuilder.append(agentsOnField.get(0)[j].toString().charAt(0)).append(" ");
            } else {
                stringBuilder.append("  ");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("| ");
        stringBuilder.append("                                      |");
        for (int j = 4; j < 8; j++) {
            if (agentsOnField.get(6).length > j) {
                stringBuilder.append(agentsOnField.get(6)[j].toString().charAt(0)).append(" ");
            } else {
                stringBuilder.append("  ");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("|\n");
        if (safePosition == 0) {
            stringBuilder.append("|__[$]__|");
        } else {
            stringBuilder.append("|_______|");
        }
        stringBuilder.append("                                       ");
        if (safePosition == 6) {
            stringBuilder.append("|__[$]__|");
        } else {
            stringBuilder.append("|_______|");
        }
        stringBuilder.append("\n");

        //printing third row of houses
        stringBuilder.append("       ___       ___       ___       ___       ___\n");
        stringBuilder.append("     /_-3__\\   /_10__\\   /__9__\\   /__8__\\   /__7__\\\n");
        stringBuilder.append("    ");
        for (int i = 11; i > 6; i--) {
            stringBuilder.append("|");
            for (int j = 0; j < 4; j++) {
                if (agentsOnField.get(i).length > j) {
                    stringBuilder.append(agentsOnField.get(i)[j].toString().charAt(0)).append(" ");
                } else {
                    stringBuilder.append("  ");
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("| ");
        }
        stringBuilder.append("\n");
        stringBuilder.append("    ");
        for (int i = 11; i > 6; i--) {
            stringBuilder.append("|");
            for (int j = 4; j < 8; j++) {
                if (agentsOnField.get(i).length > j) {
                    stringBuilder.append(agentsOnField.get(i)[j].toString().charAt(0)).append(" ");
                } else {
                    stringBuilder.append("  ");
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("| ");
        }
        stringBuilder.append("\n");
        stringBuilder.append("    ");
        for (int i = 11; i > 6; i--) {
            if (safePosition == i) {
                stringBuilder.append("|__[$]__| ");
            } else {
                stringBuilder.append("|_______| ");
            }
        }
        stringBuilder.append("\n");

        //printing the points for each agent
        stringBuilder.append("Points:\n");
        for (Agent a : agents) {
            stringBuilder.append(a.toString()).append(": ").append(scores.get(a)).append("\n");
        }
        if (lastDieRoll != 0) {
            stringBuilder.append("Last die roll: ").append(lastDieRoll).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the agents playing on the board.
     */
    public Agent[] getAgents() {
        return Arrays.copyOf(agents, agents.length);
    }

    /**
     * Returns a map containing the position for each agent. Unmodifiable as agents should only be moved with the given board methods.
     *
     * @return unmodifiableMap of the agents positions
     */
    public Map<Agent, Integer> getAgentsPositions() {
        return Collections.unmodifiableMap(agentsPositions);
    }

    /**
     * Returns the possible outcomes of the die associated with this board
     *
     * @return array with the possible outcomes of a die roll
     */
    public int[] getDieFaces() {
        return Arrays.copyOf(die.getFaces(), die.getFaces().length);
    }

    /**
     * Returns the result of the last die roll.
     */
    public int getLastDieRoll() {
        return lastDieRoll;
    }

    /**
     * Sets the "result" of the last die roll to a given number.
     *
     * @param lastDieRoll the desired simulated outcome of the die roll
     */
    public void setLastDieRoll(int lastDieRoll) {
        this.lastDieRoll = lastDieRoll;
    }

    /**
     * Helper method to get a new Map<Agent, Boolean> with false entries for each playing agent
     *
     * @return method with an entry for each playing agent with value false
     */
    private Map<Agent, Boolean> getNewScoringTriggeredForAgentMap() {
        Map<Agent, Boolean> map = new HashMap<>();
        for (Agent a : agents) {
            map.put(a, false);
        }
        return map;
    }

    /**
     * Returns the number of fields/buildings on the board.
     */
    public int getNumberOfFields() {
        return NUMBER_OF_FIELDS;
    }

    /**
     * Returns the fieldId of the ruins
     *
     * @return the fieldId of the ruins
     */
    public int getRuinsField() {
        return 11;
    }

    public int getSafePosition() {
        return safePosition;
    }

    /**
     * Returns a Map depicting the scores of the playing agents
     *
     * @return Map containing the current scores for each agent
     */
    public Map<Agent, Integer> getScores() {
        return scores;
    }

    /**
     * Returns whether the game is over, i.e. if one player has at least 42 points.
     *
     * @return whether game is over
     */
    public boolean isGameOver() {
        for (Agent a : agents) {
            if (scores.get(a) >= 42) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a map with the given agents with entries of value 0.
     *
     * @param agents array of agents
     * @return Map with an entry of 0 for each agent
     */
    private Map<Agent, Integer> getAgentMapWithZeros(Agent[] agents) {
        Map<Agent, Integer> map = new HashMap<>();
        for (Agent a : agents) {
            map.put(a, 0);
        }
        return map;
    }

    /**
     * Returns which agents are playing depending on the number of agents.
     *
     * @param number number of participating agents
     * @return Agent array with the participating agents
     */
    private static Agent[] getParticipatingAgents(int number) {
        Agent[] totalAgents = Agent.values();
        Agent[] participatingAgents = new Agent[number];
        System.arraycopy(totalAgents, 0, participatingAgents, 0, number);
        return participatingAgents;
    }
}

