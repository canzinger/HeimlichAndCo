package HeimlichAndCo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HeimlichAndCoAction {

    private int moveType; //0 for safeMove, 1 for regular player move, 2 for card play
    private boolean triggersScoring;
    private Map<Agent, Integer> agentsMoves;

    public HeimlichAndCoAction(Map<Agent, Integer> agentsMoves) {
        this.agentsMoves = agentsMoves;
    }

    //dieRoll can be 0 if no die was rolled
    public static Set<HeimlichAndCoAction> getPossibleActions(HeimlichAndCoBoard board, int dieRoll, int currentPlayer) {
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
                                        Map<Agent, Integer> agentsMoves = new HashMap<>();
                                        agentsMoves.put(playingAgents[0], agent0);
                                        agentsMoves.put(playingAgents[1], agent1);
                                        agentsMoves.put(playingAgents[2], agent2);
                                        agentsMoves.put(playingAgents[3], agent3);
                                        agentsMoves.put(playingAgents[4], agent4);
                                        agentsMoves.put(playingAgents[5], agent5);
                                        agentsMoves.put(playingAgents[6], rem);
                                        retSet.add(new HeimlichAndCoAction(agentsMoves));
                                        System.out.println(new HeimlichAndCoAction(agentsMoves));
                                    }
                                } else {
                                    Map<Agent, Integer> agentsMoves = new HashMap<>();
                                    agentsMoves.put(playingAgents[0], agent0);
                                    agentsMoves.put(playingAgents[1], agent1);
                                    agentsMoves.put(playingAgents[2], agent2);
                                    agentsMoves.put(playingAgents[3], agent3);
                                    agentsMoves.put(playingAgents[4], agent4);
                                    agentsMoves.put(playingAgents[5], rem);
                                    retSet.add(new HeimlichAndCoAction(agentsMoves));
                                    System.out.println(new HeimlichAndCoAction(agentsMoves));
                                }
                            }
                        } else {
                            //this means all remaining points have to be given to the remaining agent
                            Map<Agent, Integer> agentsMoves = new HashMap<>();
                            agentsMoves.put(playingAgents[0], agent0);
                            agentsMoves.put(playingAgents[1], agent1);
                            agentsMoves.put(playingAgents[2], agent2);
                            agentsMoves.put(playingAgents[3], agent3);
                            agentsMoves.put(playingAgents[4], rem);
                            retSet.add(new HeimlichAndCoAction(agentsMoves));
                            System.out.println(new HeimlichAndCoAction(agentsMoves));
                        }

                    }
                }
            }
        }
        return retSet;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(Agent a: Agent.values()) {
            if (agentsMoves.containsKey(a)) {
                stringBuilder.append(a.toString()).append(": ").append(agentsMoves.get(a)).append("\n");
            }

        }
        return stringBuilder.toString();
    }
}
