package HeimlichAndCo.Cards;

import HeimlichAndCo.Actions.HeimlichAndCoCardAction;
import HeimlichAndCo.Agent;
import HeimlichAndCo.HeimlichAndCoBoard;

import java.util.Arrays;
import java.util.Set;

public abstract class HeimlichAndCoCard {

    protected final HeimlichAndCoCardSpecification cardSpecification;

    public HeimlichAndCoCard(HeimlichAndCoCardSpecification cardSpecification) {
        this.cardSpecification = cardSpecification.clone();
    }

    /**
     *
     * @param board to which the Card should be applied
     * @param agents which should be used for the card (if applicable)
     * @param number which determine how far agents should be moved (if applicable)
     */
    public final void applyCard(HeimlichAndCoBoard board, Agent[] agents, int number) {
        if (!checkArgumentsAgainstCardSpecification(agents)) {
            throw new IllegalArgumentException("One of the argument arrays is not valid with the Card Specification");
        }
        //check validity with playing agents and check whether an agent is null
        Agent[] playingAgents = board.getAgents();
        for (Agent a: agents) {
            if (a == null) {
                throw new IllegalArgumentException("An agent cannot be null.");
            } else if (!Arrays.asList(playingAgents).contains(a)) {
                throw new IllegalArgumentException("A non playing agent was given.");
            }
        }

        applyCardSpecific(board, agents, number); //TODO
    }

    private boolean checkArgumentsAgainstCardSpecification(Agent[] agents) {
        // first check agents constraints
        if (agents == null) {
            if (cardSpecification.minNumberOfAgents != 0) { //no agent array given but need at least 1 agent
                return false;
            }
        } else {
            if (agents.length < cardSpecification.minNumberOfAgents || agents.length > cardSpecification.maxNumberOfAgents) { //agent array too small/large
                return false;
            }
        }
        return true;
    }

    protected abstract void applyCardSpecific(HeimlichAndCoBoard board, Agent[] agents, int number);

    public abstract Set<HeimlichAndCoCardAction> getPossibleActions(HeimlichAndCoBoard board);



    /**
     * Returns the HeimlichAndCoCardSpecification for the given card.
     * Giving information about how many agents or numbers are needed to apply the card.
     * @return the card speficiation
     */
    HeimlichAndCoCardSpecification getCardSpecification() {
        return cardSpecification;
    }

}