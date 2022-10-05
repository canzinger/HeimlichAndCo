package HeimlichAndCo.Cards;

import HeimlichAndCo.Actions.HeimlichAndCoCardAction;
import HeimlichAndCo.Agent;
import HeimlichAndCo.HeimlichAndCoBoard;

import java.util.*;

public class HeimlichAndCoMoveAgentsCard extends HeimlichAndCoCard {

    public HeimlichAndCoMoveAgentsCard(HeimlichAndCoCardSpecification cardSpecification) {
        super(cardSpecification);
        if (cardSpecification.type < 0 || cardSpecification.type > 11) {
            throw new IllegalArgumentException("Type in Card Specification must be between 0 and 11 for this type of Card");
        }
    }

    @Override
    protected void applyCardSpecific(HeimlichAndCoBoard board, Agent[] agents, int number) {
        switch (super.cardSpecification.type) {
            case 0:
                applyType0(board, agents[0], number);
                break;
            case 1:
                applyType1(board, agents[0], number);
                break;
            case 2:
                applyType2(board, agents);
                break;
            case 3:
                applyType3(board, agents);
                break;
            case 4:
                applyType4(board, agents[0], number);
                break;
            case 5:
                applyType5(board, agents[0]);
                break;
            case 6:
                applyType6(board);
                break;
            case 7:
                applyType7(board, agents);
                break;
            case 8:
                applyType8(board, agents);
                break;
            case 9:
                applyType9(board, agents);
                break;
            case 10:
                applyType10(board, agents[0], number);
                break;
            case 11:
                applyType11(board, agents[0]);
                break;
        }
    }

    @Override
    public Set<HeimlichAndCoCardAction> getPossibleActions(HeimlichAndCoBoard board) {
        switch (super.cardSpecification.type) {
            case 0:
                return getPossibleActionsType0(board);
            case 1:
                return getPossibleActionsType1(board);
            case 2:
                return getPossibleActionsType2(board);
            case 3:
                return getPossibleActionsType3(board);
            case 4:
                return getPossibleActionsType4(board);
            case 5:
                return getPossibleActionsType5(board);
            case 6:
                return getPossibleActionsType6(board);
            case 7:
                return getPossibleActionsType7(board);
            case 8:
                return getPossibleActionsType8(board);
            case 9:
                return getPossibleActionsType9(board);
            case 10:
                return getPossibleActionsType10(board);
            case 11:
                return getPossibleActionsType11(board);
            default:
                throw new IllegalStateException("Invalid type for a Heimlich and Co Card");
        }
    }

    public String toString() {
        switch (super.cardSpecification.type) {
            case 0:
                return "Move an agent of your choice back by one or two buildings.";
            case 1:
                return "Move an agent of your choice backward or forward by one building.";
            case 2:
                return "Move two agents of your choice back by one building each.";
            case 3:
                return "Move two agents of your choice forward by one building each.";
            case 4:
                return "Move an agent that is on the safe away from the safe by one building.";
            case 5:
                return "Move an agent of your choice into the ruins.";
            case 6:
                return "Move all agents into the church.";
            case 7:
                return "Swap the places of two agents.";
            case 8:
                return "Move one agent of your choice to another one. (First player that is given is being moved)";
            case 9:
                return "Move one or two agents of your choice from the ruins into the church.";
            case 10:
                return "Move an agent one, two or three buildings forward.";
            case 11:
                return "Move an agent of your choice to the safe (Will trigger scoring).";
            default:
                throw new IllegalStateException("Invalid type for a Heimlich and Co Card");
        }
    }

    //region applyTypeX()
    private void applyType0(HeimlichAndCoBoard board, Agent agent, int number) {
        if (number == -1 || number == -2) {
            board.moveAgent(agent, number);
        } else {
            throw new IllegalArgumentException("Given number must be -1 or -2.");
        }
    }

    private void applyType1(HeimlichAndCoBoard board, Agent agent, int number) {
        if (number == -1 || number == 1) {
            board.moveAgent(agent, number);
        } else {
            throw new IllegalArgumentException("Given number must be -1 or 1.");
        }
    }

    private void applyType2(HeimlichAndCoBoard board, Agent[] agents) {
        board.moveAgent(agents[0], -1);
        board.moveAgent(agents[1], -1);
    }

    private void applyType3(HeimlichAndCoBoard board, Agent[] agents) {
        board.moveAgent(agents[0], 1);
        board.moveAgent(agents[1], 1);
    }

    private void applyType4(HeimlichAndCoBoard board, Agent agent, int number) {
        if (board.getAgentsPositions().get(agent) != board.getSafePosition()) {
            throw new IllegalArgumentException("Agent must be in the same building as the safe");
        }
        if (number == 1 || number == -1) {
            board.moveAgent(agent, number);
        } else {
            throw new IllegalArgumentException("Agent can only be moved one building forward or backward");
        }
    }

    private void applyType5(HeimlichAndCoBoard board, Agent agent) {
        board.getAgentsPositions().replace(agent, 11);
    }

    private void applyType6(HeimlichAndCoBoard board) {
        Map<Agent, Integer> positions = board.getAgentsPositions();
        for(Agent a: positions.keySet()) {
            positions.replace(a, 0);
        }
    }

    private void applyType7(HeimlichAndCoBoard board, Agent[] agents) {
        Map<Agent, Integer> positions = board.getAgentsPositions();
        int oldPosition0 = positions.get(agents[0]);
        int oldPosition1 = positions.get(agents[1]);
        positions.replace(agents[0], oldPosition1);
        positions.replace(agents[1], oldPosition0);
    }

    private void applyType8(HeimlichAndCoBoard board, Agent[] agents) {
        Map<Agent, Integer> positions = board.getAgentsPositions();
        positions.replace(agents[0], positions.get(agents[1]));
    }

    private void applyType9(HeimlichAndCoBoard board, Agent[] agents) {
        Map<Agent, Integer> positions = board.getAgentsPositions();
        for (Agent a:agents) {
            if (positions.get(a) != 11) {
                throw new IllegalArgumentException("Agent must be in the ruins before moving it");
            }
        }
        for (Agent a:agents) {
            positions.replace(a, 0);
        }
    }

    private void applyType10(HeimlichAndCoBoard board, Agent agent, int number) {
        if (number < 1 || number > 3) {
            throw new IllegalArgumentException("Number must be between 1 and 3.");
        }
        board.moveAgent(agent, number);
    }

    private void applyType11(HeimlichAndCoBoard board, Agent agent) {
        board.getAgentsPositions().replace(agent, board.getSafePosition());
    }

    //endregion

    //region getPossibleActionsTypeX()

    private Set<HeimlichAndCoCardAction> getPossibleActionsType0(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoCardAction> actions = new HashSet<>();
        Agent[] agents = board.getAgents();
        for (Agent a: agents) {
            actions.add(new HeimlichAndCoCardAction(this, new Agent[]{a}, -1));
            actions.add(new HeimlichAndCoCardAction(this, new Agent[]{a}, -2));
        }
        return actions;
    }

    private Set<HeimlichAndCoCardAction> getPossibleActionsType1(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoCardAction> actions = new HashSet<>();
        Agent[] agents = board.getAgents();
        for (Agent a: agents) {
            actions.add(new HeimlichAndCoCardAction(this, new Agent[]{a}, -1));
            actions.add(new HeimlichAndCoCardAction(this, new Agent[]{a}, 1));
        }
        return actions;
    }

    private Set<HeimlichAndCoCardAction> getPossibleActionsType2(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoCardAction> actions = new HashSet<>();
        List<Agent[]> agentPairs = this.getPairsOfAgents(board.getAgents());
        for (Agent[] pair:agentPairs) {
            actions.add(new HeimlichAndCoCardAction(this, pair, 0));
        }
        return actions;
    }

    private Set<HeimlichAndCoCardAction> getPossibleActionsType3(HeimlichAndCoBoard board) {
        return this.getPossibleActionsType2(board);
    }

    private Set<HeimlichAndCoCardAction> getPossibleActionsType4(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoCardAction> actions = new HashSet<>();
        Map<Integer, Agent[]> agentsOnFields = board.agentsOnFields();
        for (Agent a:agentsOnFields.get(board.getSafePosition())) {
            actions.add(new HeimlichAndCoCardAction(this, new Agent[]{a}, -1));
            actions.add(new HeimlichAndCoCardAction(this, new Agent[]{a}, 1));
        }
        return actions;
    }

    private Set<HeimlichAndCoCardAction> getPossibleActionsType5(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoCardAction> actions = new HashSet<>();
        Map<Agent, Integer> agentsPositions = board.getAgentsPositions();
        Agent[] agents = board.getAgents();
        for (Agent a: agents) {
            if (agentsPositions.get(a) != 11) {
                actions.add(new HeimlichAndCoCardAction(this, new Agent[]{a}, 11));
            }
        }
        return actions;
    }

    private Set<HeimlichAndCoCardAction> getPossibleActionsType6(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoCardAction> actions = new HashSet<>();
        actions.add(new HeimlichAndCoCardAction(this, null, 0));
        return actions;
    }

    private Set<HeimlichAndCoCardAction> getPossibleActionsType7(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoCardAction> actions = new HashSet<>();
        List<Agent[]> agentPairs = this.getPairsOfAgents(board.getAgents());
        for (Agent[] pair:agentPairs) {
            if (!board.getAgentsPositions().get(pair[0]).equals(board.getAgentsPositions().get(pair[1]))) {
                actions.add(new HeimlichAndCoCardAction(this, pair, 0));
            }
        }
        return actions;
    }

    private Set<HeimlichAndCoCardAction> getPossibleActionsType8(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoCardAction> actions = new HashSet<>();
        Agent[] playingAgents = board.getAgents();
        //for this card the order of agents matters, therefore do it differently here
        for (int i = 0; i < playingAgents.length; i++) {
            for (int j = 0; j < playingAgents.length; j++) {
                if (i != j && !board.getAgentsPositions().get(playingAgents[i]).equals(board.getAgentsPositions().get(playingAgents[j]))) {
                    actions.add(new HeimlichAndCoCardAction(this, new Agent[]{playingAgents[i], playingAgents[j]},0));
                }
            }
        }
        return actions;
    }

    private Set<HeimlichAndCoCardAction> getPossibleActionsType9(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoCardAction> actions = new HashSet<>();
        Agent[] playingAgents = board.getAgents();
        for (Agent a:playingAgents) {
            if (board.getAgentsPositions().get(a).equals(11)) {
                actions.add(new HeimlichAndCoCardAction(this, new Agent[]{a}, 0));
            }
        }
        List<Agent[]> agentPairs = getPairsOfAgents(playingAgents);
        for (Agent[] pair: agentPairs) {
            if (board.getAgentsPositions().get(pair[0]).equals(11) && board.getAgentsPositions().get(pair[0]).equals(11)) {
                actions.add(new HeimlichAndCoCardAction(this, new Agent[]{pair[0], pair[1]}, 0));
            }
        }
        return actions;
    }

    private Set<HeimlichAndCoCardAction> getPossibleActionsType10(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoCardAction> actions = new HashSet<>();
        Agent[] playingAgents = board.getAgents();
        for (Agent a: playingAgents) {
            actions.add(new HeimlichAndCoCardAction(this, new Agent[]{a}, 1));
            actions.add(new HeimlichAndCoCardAction(this, new Agent[]{a}, 2));
            actions.add(new HeimlichAndCoCardAction(this, new Agent[]{a}, 3));
        }
        return actions;
    }

    private Set<HeimlichAndCoCardAction> getPossibleActionsType11(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoCardAction> actions = new HashSet<>();
        Agent[] playingAgents = board.getAgents();
        for (Agent a: playingAgents) {
            if (!board.getAgentsPositions().get(a).equals(board.getSafePosition())) {
                actions.add(new HeimlichAndCoCardAction(this, new Agent[]{a}, 0));
            }
        }
        return actions;
    }

    //endregion

    private List<Agent[]> getPairsOfAgents(Agent[] playingAgents) {
        List<Agent[]> agentPairs = new LinkedList<>();
        for (int i = 0; i < playingAgents.length - 1; i++) {
            for (int j = i + 1; j < playingAgents.length; j++) {
                agentPairs.add(new Agent[]{playingAgents[i], playingAgents[j]});
            }
        }
        return agentPairs;
    }

    @Override
    public int hashCode() {
        return cardSpecification.hashCode() * 47;
    }

    public HeimlichAndCoMoveAgentsCard clone() {
        return new HeimlichAndCoMoveAgentsCard(cardSpecification.clone());
    }
}
