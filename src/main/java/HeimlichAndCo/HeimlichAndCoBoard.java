package HeimlichAndCo;

import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

    @Override
    //TODO make nicer
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
        for(int i = 1; i < 6; i++) {
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
        for(int i = 11; i > 6; i--) {
            if (safePosition == i) {
                stringBuilder.append("|__[$]__| ");
            } else {
                stringBuilder.append("|_______| ");
            }
        }
        stringBuilder.append("\n");



        //printing the points for each agent
        stringBuilder.append("Points:\n");
        for (Agent a: agents) {
            stringBuilder.append(a.toString()).append(": ").append(scores.get(a)).append("\n");
        }
        return stringBuilder.toString();
    }

    public Map<Integer, Agent[]> agentsOnFields() {
        Map<Integer, Agent[]> agentsMap = new HashMap<>();
        for (int i = 0; i < numberOfFields; i++) {
            List<Agent> agents = new LinkedList<Agent>();
            for (Agent a: Agent.values()) {
                if (agentsPositions.get(a) == i) {
                    agents.add(a);
                }
            }
            agentsMap.put(i, agents.toArray(new Agent[0]));
        }
        return agentsMap;
    }

}

