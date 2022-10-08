package HeimlichAndCo.Actions;

import HeimlichAndCo.Agent;
import HeimlichAndCo.HeimlichAndCoBoard;

import java.util.*;

public class HeimlichAndCoAgentMoveAction implements HeimlichAndCoAction {

    private final Map<Agent, Integer> agentsMoves;

    public HeimlichAndCoAgentMoveAction(Map<Agent, Integer> agentsMoves) {
        if (agentsMoves != null && !agentsMoves.isEmpty()) {
            this.agentsMoves = new HashMap<>(agentsMoves);
        } else {
            this.agentsMoves = new HashMap<>();
        }
    }

    /**
     * creates the Action that corresponds to moving no agents
     *
     * @return AgentMoveAction that is the No Move Action
     */
    public static HeimlichAndCoAgentMoveAction getNoMoveAction() {
        return new HeimlichAndCoAgentMoveAction(null);
    }

    /**
     * Calculates all possible actions depending on a board state and whether playing with cards
     *
     * @param board     the current board
     * @param withCards whether the game is with or without cards
     * @return a Set of HeimlichAndCoActions which are possible actions in the current state
     */
    public static Set<HeimlichAndCoAction> getPossibleActions(HeimlichAndCoBoard board, boolean withCards) {
        int dieResult = board.getLastDieRoll();
        List<Integer> possibleAmountOfMoves = new LinkedList<>();
        Set<HeimlichAndCoAction> retSet = new HashSet<>();
        if (dieResult == 13) {
            possibleAmountOfMoves.add(1);
            possibleAmountOfMoves.add(2);
            possibleAmountOfMoves.add(3);
            if (withCards) {
                retSet.add(getNoMoveAction());
            }
        } else {
            possibleAmountOfMoves.add(dieResult);
        }

        Agent[] playingAgents = board.getAgents();
        for (Integer dieRoll : possibleAmountOfMoves) {
            for (int agent0 = dieRoll; agent0 >= 0; agent0--) {
                int rem = dieRoll - agent0;
                for (int agent1 = rem; agent1 >= 0; agent1--) {
                    rem = dieRoll - agent0 - agent1;
                    for (int agent2 = rem; agent2 >= 0; agent2--) {
                        rem = dieRoll - agent0 - agent1 - agent2;
                        for (int agent3 = rem; agent3 >= 0; agent3--) {
                            rem = dieRoll - agent0 - agent1 - agent2 - agent3;
                            if (playingAgents.length > 5) {
                                for (int agent4 = rem; agent4 >= 0; agent4--) {
                                    rem = dieRoll - agent0 - agent1 - agent2 - agent3 - agent4;
                                    if (playingAgents.length > 6) {
                                        for (int agent5 = rem; agent5 >= 0; agent5--) {
                                            rem = dieRoll - agent0 - agent1 - agent2 - agent3 - agent4 - agent5;
                                            Map<Agent, Integer> agentsMoves =
                                                    agentsMoveHelper(playingAgents, agent0, agent1, agent2, agent3, agent4, agent5, rem);
                                            retSet.add(new HeimlichAndCoAgentMoveAction
                                                    (agentsMoves));
                                        }
                                    } else {
                                        //this means all remaining points have to be given to the remaining agent
                                        Map<Agent, Integer> agentsMoves =
                                                agentsMoveHelper(playingAgents, agent0, agent1, agent2, agent3, agent4, rem);
                                        retSet.add(new HeimlichAndCoAgentMoveAction
                                                (agentsMoves));
                                    }
                                }
                            } else {
                                //this means all remaining points have to be given to the remaining agent
                                Map<Agent, Integer> agentsMoves =
                                        agentsMoveHelper(playingAgents, agent0, agent1, agent2, agent3, rem);
                                retSet.add(new HeimlichAndCoAgentMoveAction
                                        (agentsMoves));
                            }

                        }
                    }
                }
            }
        }
        return retSet;
    }

    @Override
    public void applyAction(HeimlichAndCoBoard board) {
        board.moveAgents(agentsMoves);
    }

    public HeimlichAndCoAgentMoveAction clone() {
        return new HeimlichAndCoAgentMoveAction(this.agentsMoves);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj.getClass().equals(HeimlichAndCoAgentMoveAction.class)) {
            HeimlichAndCoAgentMoveAction toComp = (HeimlichAndCoAgentMoveAction) obj;
            if (toComp.agentsMoves.size() != this.agentsMoves.size()) {
                return false;
            }
            //compare manually, as a Map with no entry for an agent should be equal to a Map with an entry of value 0 for the same agent
            for (Agent a : toComp.agentsMoves.keySet()) {
                if (!(this.agentsMoves.containsKey(a) && this.agentsMoves.get(a).equals(toComp.agentsMoves.get(a)))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (Agent a : agentsMoves.keySet()) {
            hashCode += (a.ordinal() + 37) * agentsMoves.get(a);
        }
        return hashCode;
    }

    /**
     * Determines whether the action moves agents into the ruins
     *
     * @param board which the action will be applied to
     * @return whether the action will move one or more agents into the ruins on the given board
     */
    public boolean movesAgentsIntoRuins(HeimlichAndCoBoard board) {
        Map<Agent, Integer> positions = board.getAgentsPositions();
        for (Agent agent : this.agentsMoves.keySet()) {
            if ((positions.get(agent) + agentsMoves.get(agent)) % board.getNumberOfFields() == board.getRuinsField()) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AgentMoveAction: ");
        for (Agent a : Agent.values()) {
            if (agentsMoves.containsKey(a)) {
                stringBuilder.append(a.toString()).append(": ").append(agentsMoves.get(a)).append("; ");
            }
        }
        if (isNoMoveAction()) {
            stringBuilder.append("no agents are moved, a card is drawn if possible");
        }
        return stringBuilder.toString();
    }

    /**
     * @return whether the action is the No Move Action
     */
    public boolean isNoMoveAction() {
        return agentsMoves.isEmpty();
    }

    /**
     * creates a Map with Pairs of entries denoting moves for agents
     *
     * @param playingAgents Array of agents that are playing
     * @param agentsMoves   integers denoting the amount the corresponding agent should be moved forward
     * @return Map denoting moves for agents
     */
    private static Map<Agent, Integer> agentsMoveHelper(Agent[] playingAgents, int... agentsMoves) {
        Map<Agent, Integer> agentsMovesMap = new HashMap<>();
        for (int i = 0; i < agentsMoves.length; i++) {
            if (agentsMoves[i] > 0) {
                agentsMovesMap.put(playingAgents[i], agentsMoves[i]);
            }
        }
        return agentsMovesMap;
    }
}
