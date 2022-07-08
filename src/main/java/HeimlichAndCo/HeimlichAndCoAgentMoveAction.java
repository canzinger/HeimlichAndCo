package HeimlichAndCo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HeimlichAndCoAgentMoveAction implements HeimlichAndCoAction{

    private final Map<Agent, Integer> agentsMoves;
    private final boolean triggersScoringRound;

    public HeimlichAndCoAgentMoveAction(Map<Agent, Integer> agentsMoves, boolean triggersScoringRound) {
        this.agentsMoves = new HashMap<>(agentsMoves);
        this.triggersScoringRound = triggersScoringRound;
    }

    public static Set<HeimlichAndCoAction> getPossibleActions(HeimlichAndCoBoard board) {
        int dieRoll = board.getLastDieRoll();
        Set<HeimlichAndCoAction> retSet = new HashSet<>();
        Agent[] playingAgents = board.getAgents();
        for(int agent0 = dieRoll; agent0 >= 0; agent0--) {
            int rem = dieRoll - agent0;
            for (int agent1 = rem; agent1 >= 0 ; agent1--) {
                rem = dieRoll - agent0 -  agent1;
                for (int agent2 = rem; agent2 >= 0 ; agent2--) {
                    rem = dieRoll - agent0 - agent1 - agent2;
                    for (int agent3 = rem; agent3 >= 0 ; agent3--) {
                        rem = dieRoll -agent0 - agent1 - agent2 - agent3;
                        if (playingAgents.length > 5) {
                            for (int agent4 = rem; agent4 >= 0; agent4--) {
                                rem = dieRoll - agent0 - agent1 - agent2 - agent3 - agent4;
                                if (playingAgents.length > 6) {
                                    for (int agent5 = rem; agent5 >= 0; agent5--) {
                                        rem = dieRoll - agent0 - agent1 - agent2 - agent3 - agent4 - agent5;
                                        Map<Agent, Integer> agentsMoves =
                                                agentsMoveHelper(playingAgents, agent0, agent1, agent2, agent3, agent4, agent5, rem);
                                        retSet.add(new HeimlichAndCoAgentMoveAction
                                                (agentsMoves, movesTriggerScoringRound(agentsMoves, board)));
                                    }
                                } else {
                                    Map<Agent, Integer> agentsMoves =
                                            agentsMoveHelper(playingAgents, agent0, agent1, agent2, agent3,agent4, rem);
                                    retSet.add(new HeimlichAndCoAgentMoveAction
                                            (agentsMoves, movesTriggerScoringRound(agentsMoves, board)));
                                }
                            }
                        } else {
                            //this means all remaining points have to be given to the remaining agent
                            Map<Agent, Integer> agentsMoves =
                                    agentsMoveHelper(playingAgents, agent0, agent1, agent2, agent3, rem);
                            retSet.add(new HeimlichAndCoAgentMoveAction
                                    (agentsMoves, movesTriggerScoringRound(agentsMoves, board)));
                        }

                    }
                }
            }
        }
        return retSet;
    }

    public boolean isTriggersScoringRound() {
        return triggersScoringRound;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AgentMoveAction:\n");
        for(Agent a: Agent.values()) {
            if (agentsMoves.containsKey(a)) {
                stringBuilder.append(a.toString()).append(": ").append(agentsMoves.get(a)).append("\n");
            }
        }
        if (triggersScoringRound) {
            stringBuilder.append("Triggers scoring round\n");
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param agentsMoves agents moves to be made
     * @param board board on which action is performed
     * @return if the result of the moves is a scoring round (i.e. if an agent gets moved to the field with the safe)
     */
    private static boolean movesTriggerScoringRound(Map<Agent, Integer> agentsMoves, HeimlichAndCoBoard board) {
        int safePosition = board.getSafePosition();
        Map<Agent, Integer> agentsPositions = board.getAgentsPositions();
        for(Agent a: agentsMoves.keySet()) {
            int oldPos = agentsPositions.get(a);
            int newPos = (oldPos + agentsMoves.get(a)) % board.getNumberOfFields();
            if (newPos == board.getSafePosition()) {
                return true;
            }
        }
        return false;
    }

    private static Map<Agent, Integer> agentsMoveHelper(Agent[] playingAgents, int ... agentsMoves) {
        Map<Agent, Integer> agentsMovesMap = new HashMap<Agent, Integer>();
        for (int i = 0; i < agentsMoves.length; i++) {
            agentsMovesMap.put(playingAgents[i], agentsMoves[i]);
        }
        return agentsMovesMap;
    }

    @Override
    public int doAction(HeimlichAndCoBoard board) {
        board.moveAgents(agentsMoves);
        if (triggersScoringRound) {
            board.awardPoints();
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof HeimlichAndCoAgentMoveAction) {
            HeimlichAndCoAgentMoveAction toComp = (HeimlichAndCoAgentMoveAction) obj;
            return  toComp.agentsMoves.equals(this.agentsMoves) && toComp.triggersScoringRound && this.triggersScoringRound;
        }
        return false;
    }
}
