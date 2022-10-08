package HeimlichAndCo.Cards;

import HeimlichAndCo.Actions.HeimlichAndCoCardAction;
import HeimlichAndCo.Enums.Agent;
import HeimlichAndCo.HeimlichAndCoBoard;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class HeimlichAndCoAddScorePointsCard extends HeimlichAndCoCard {

    public HeimlichAndCoAddScorePointsCard(HeimlichAndCoCardSpecification cardSpecification) {
        super(cardSpecification);
        if (cardSpecification.type != 0) {
            throw new IllegalArgumentException("Type in Card Specification must be 0 for this type of Card");
        }
    }

    public HeimlichAndCoAddScorePointsCard clone() {
        return new HeimlichAndCoAddScorePointsCard(cardSpecification.clone());
    }

    @Override
    public Set<HeimlichAndCoCardAction> getPossibleActions(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoCardAction> actions = new HashSet<>();
        Map<Agent, Integer> scores = board.getScores();
        Agent[] agents = board.getAgents();
        for (int i = 0; i < agents.length - 1; i++) {
            if (scores.get(agents[i]) >= 40) {
                continue;
            }
            for (int j = i + 1; j < agents.length; j++) {
                if (scores.get(agents[j]) >= 40) {
                    continue;
                } else {
                    actions.add(new HeimlichAndCoCardAction(this, new Agent[]{agents[i], agents[j]}, 0));
                }
            }
        }
        return actions;
    }

    @Override
    public int hashCode() {
        return cardSpecification.hashCode() * 41;
    }

    public String toString() {
        return "AddScorePointsCard: Move two score markers forward by three points each (max. to field 40).";
    }

    @Override
    protected void applyCardSpecific(HeimlichAndCoBoard board, Agent[] agents, int number) {
        Map<Agent, Integer> scores = board.getScores();
        for (Agent a : agents) {
            if (scores.get(a) <= 37) {
                scores.replace(a, scores.get(a) + 3);
            } else if (scores.get(a) < 40) {
                scores.replace(a, 40); //move agent to field 40 because it was either at 39 or 38
            } else if (scores.get(a) >= 40) {
                throw new IllegalArgumentException("Agents on fields 40 or more cannot be moved with this card.");
            }
        }
    }
}
