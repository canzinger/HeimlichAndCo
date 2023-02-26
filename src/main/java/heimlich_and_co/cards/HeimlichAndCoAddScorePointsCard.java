package heimlich_and_co.cards;

import heimlich_and_co.HeimlichAndCoBoard;
import heimlich_and_co.actions.HeimlichAndCoCardAction;
import heimlich_and_co.enums.Agent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the cards that can be used to add points to the scores of agents.
 * In the traditional game, there is only one specific type of this card.
 */
public class HeimlichAndCoAddScorePointsCard extends HeimlichAndCoCard {

    /**
     * Creates a new HeimlichAndCoAddScorePointCard with the given card specification
     *
     * @param cardSpecification card specification for the new card
     */
    public HeimlichAndCoAddScorePointsCard(HeimlichAndCoCardSpecification cardSpecification) {
        super(cardSpecification);
        if (cardSpecification.type != 0) {
            throw new IllegalArgumentException("Type in Card Specification must be 0 for this type of Card");
        }
    }

    public HeimlichAndCoAddScorePointsCard(HeimlichAndCoAddScorePointsCard card) {
        super(card.cardSpecification);
        if (cardSpecification.type != 0) {
            throw new IllegalArgumentException("Type in Card Specification must be 0 for this type of Card");
        }
    }

    @Override
    public HeimlichAndCoAddScorePointsCard deepCopy() {
        return new HeimlichAndCoAddScorePointsCard(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (HeimlichAndCoAddScorePointsCard.class == obj.getClass()) {
            return cardSpecification.equals(((HeimlichAndCoCard) obj).cardSpecification);
        } else {
            return false;
        }
    }

    /**
     * Calculates all possible actions for a board and this card.
     *
     * @param board current board
     * @return Set of HeimlichAndCoCardActions with possible actions
     */
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
                if (scores.get(agents[j]) < 40) {
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

    /**
     * Applies this specific card to the board.
     * I.e. moves the score markers of two agents.
     *
     * @param board  to which the Card should be applied
     * @param agents which should be used for the card
     * @param number which determine how far agents should be moved (not needed here)
     */
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
