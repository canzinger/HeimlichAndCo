package HeimlichAndCo;

import HeimlichAndCo.Cards.HeimlichAndCoCard;
import HeimlichAndCo.Util.Die;

import java.util.*;

public class HeimlichAndCoBoard {

    /**
     * saves which agents are in play
     */
    private final Agent[] agents;

    /**
     * saves the positions of each agent
     */
    private Map<Agent, Integer> agentsPositions; //must have an entry for all playing agents

    /**
     * saves the current points of each agent
     */
    private Map<Agent, Integer> scores; //must have an entry for all playing agents
    private int safePosition;
    private final static int numberOfFields = 12;
    private int lastDieRoll;
    private final Die die;

    private Map<Agent, Boolean> scoringTriggeredForAgent; //saves at any point in time whether scoring was triggered for an agent of not

    //region constructors

    public HeimlichAndCoBoard() {
        this(7);
    }

    public HeimlichAndCoBoard(int numberOfAgents) {
        if (numberOfAgents < 5 || numberOfAgents > 7) {
            throw new IllegalArgumentException("Invalid amount of playing agents, must be between 5 and 7");
        }
        this.agents = getParticipatingAgents(numberOfAgents);
        this.agentsPositions = getAgentMapWithZeros(agents);
        this.scores = getAgentMapWithZeros(agents);
        this.safePosition = 7; //the default starting position for the safe
        this.die = new Die();
        scoringTriggeredForAgent = getNewScoringTriggeredForAgentMap();
    }

    //endregion

    /**
     * moves an agent forward a certain number of fields
     * @param a Agent to be moved
     * @param numberOfFields number of fields the agent should be moved forward
     */
    public void moveAgent(Agent a, int numberOfFields) {
        if (agentsPositions.get(a) == safePosition && numberOfFields % HeimlichAndCoBoard.numberOfFields != 0) {
            scoringTriggeredForAgent.put(a, false);
        }
        int oldPosition = agentsPositions.get(a);
        agentsPositions.replace(a, (oldPosition + numberOfFields) % HeimlichAndCoBoard.numberOfFields);
        if (agentsPositions.get(a) == safePosition && numberOfFields % HeimlichAndCoBoard.numberOfFields != 0) {
            scoringTriggeredForAgent.put(a, true);
        }
    }

    /**
     * moves an agent to a specific field
     *
     * @param a Agent to be moved
     * @param building the building/field id of the building the agent should be moved into
     */
    public void moveAgentToAbsoluteBuilding(Agent a, int building) {
        if (agentsPositions.get(a) == safePosition && building != agentsPositions.get(a)) {
            scoringTriggeredForAgent.put(a, false);
        }
        agentsPositions.replace(a, building);
        if (agentsPositions.get(a) == safePosition && building != agentsPositions.get(a)) {
            scoringTriggeredForAgent.put(a, true);
        }
    }

    /**
     * moves multiple agents by certain number of fields
     * @param agentsMoves Map denoting the amount of fields certain agents should move forward
     */
    public void moveAgents(Map<Agent, Integer> agentsMoves) {
        for(Agent a: agentsMoves.keySet()) {
            moveAgent(a, agentsMoves.get(a));
        }
    }

    /**
     * moves to safe to a given field
     * @param fieldId target field for safe; must be ABSOLUTE POSITION
     */
    public void moveSafe(int fieldId) {
        if (safePosition != fieldId) {
            scoringTriggeredForAgent = getNewScoringTriggeredForAgentMap(); //this will always be reset when the safe is moved
        }
        safePosition = fieldId;
    }

    /**
     * gives back the score Map
     * note that this is safe, as the player agents never have access to the real Board
     * @return Hashmap containing the current scores for each agent
     */
    public Map<Agent, Integer> getScores() {
        return scores;
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
        scoringTriggeredForAgent = getNewScoringTriggeredForAgentMap();
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
        System.arraycopy(totalAgents, 0, participatingAgents, 0, number);
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
        HeimlichAndCoBoard newBoard = new HeimlichAndCoBoard(this.agents.length);
        newBoard.lastDieRoll = this.lastDieRoll;
        newBoard.safePosition = this.safePosition;
        newBoard.agentsPositions = new HashMap<>(this.agentsPositions);
        newBoard.scores = new HashMap<>(this.scores);
        System.arraycopy(this.agents, 0, newBoard.agents, 0, this.agents.length);
        newBoard.scoringTriggeredForAgent = new HashMap<>(this.scoringTriggeredForAgent);
        return newBoard;
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
        if (lastDieRoll != 0) {
            stringBuilder.append("Last die roll: ").append(lastDieRoll).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * gives back the Agents on certain fields
     * @return a map with entries for each field and a corresponding array giving the agents in the given field
     */
    public Map<Integer, Agent[]> agentsOnFields() {
        Map<Integer, Agent[]> agentsMap = new HashMap<>();
        for (int i = 0; i < numberOfFields; i++) {
            List<Agent> agents = new LinkedList<>();
            for (Agent a: this.agents) {
                if (agentsPositions.get(a) == i) {
                    agents.add(a);
                }
            }
            agentsMap.put(i, agents.toArray(new Agent[0]));
        }
        return agentsMap;
    }

    public boolean isGameOver() {
        for(Agent a: agents) {
            if (scores.get(a) >= 42) {
                return true;
            }
        }
        return false;
    }

    public Agent[] getAgents() {
        return agents;
    }

    public int getSafePosition() {
        return safePosition;
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public Map<Agent, Integer> getAgentsPositions() {
        return Collections.unmodifiableMap(agentsPositions);
    }

    public int getLastDieRoll() {
        return lastDieRoll;
    }

    public void setLastDieRoll(int lastDieRoll) {
        this.lastDieRoll = lastDieRoll;
    }

    public void rollDie() {
        this.lastDieRoll = die.roll();
    }

    public Die getDie() {
        return die;
    }

    public int getRuinsField() {return 11;}

    private Map<Agent, Boolean> getNewScoringTriggeredForAgentMap() {
        Map<Agent, Boolean> map = new HashMap<>();
        for(Agent a: agents) {
            map.put(a, false);
        }
        return map;
    }

    /**
     *
     *
     * @return whether scoring was triggered
     */
    public boolean scoringTriggered() {
        for(Agent a: scoringTriggeredForAgent.keySet()) {
            if (scoringTriggeredForAgent.get(a)) {
                return true;
            }
        }
        return false;
    }
}

