package HeimlichAndCo.Cards;

import HeimlichAndCo.Actions.HeimlichAndCoCardAction;
import HeimlichAndCo.Enums.Agent;
import HeimlichAndCo.HeimlichAndCoBoard;

import java.util.Arrays;
import java.util.Set;

public abstract class HeimlichAndCoCard {

    /**
     * The card specification each HeimlichAndCo card must have determining how many agents, numbers are needed and the
     * type of the card.
     */
    protected final HeimlichAndCoCardSpecification cardSpecification;

    /**
     * Creates a new HeimlichAndCoCard instance with the given card specification.
     *
     * @param cardSpecification card specification for the new card
     */
    public HeimlichAndCoCard(HeimlichAndCoCardSpecification cardSpecification) {
        if (cardSpecification == null) {
            throw new IllegalArgumentException("Card Specification must not be null;");
        }
        this.cardSpecification = cardSpecification.clone();
    }

    public abstract HeimlichAndCoCard clone();

    /**
     * Calculates all possible actions for a board and this card.
     *
     * @param board current board
     * @return Set of HeimlichAndCoCardActions with possible actions
     */
    public abstract Set<HeimlichAndCoCardAction> getPossibleActions(HeimlichAndCoBoard board);

    public abstract int hashCode();

    public abstract String toString();

    /**
     * Will return true if the card will have the same outcome whether it is played with [Agent1, Agent2] or [Agent2, Agent1]
     * I.e. if the card is order invariant when it comes to the agents.
     *
     * @return whether the card will have a different effect if the indices of agents are swapped
     */
    public boolean agentsOrderInvariant() {
        return this.cardSpecification.agentsOrderInvariant;
    }

    /**
     * Plays/applies the card on the current board with the given agents and number.
     * First checks general things about the parameters and then calls specific functions depending on the card.
     *
     * @param board  to which the Card should be applied
     * @param agents which should be used for the card (if applicable)
     * @param number which determine how far agents should be moved (if applicable)
     */
    public final void applyCard(HeimlichAndCoBoard board, Agent[] agents, int number) {
        if (!checkArgumentsAgainstCardSpecification(agents, number)) {
            throw new IllegalArgumentException("One of the argument arrays is not valid with the Card Specification");
        }
        //check validity with playing agents and check whether an agent is null
        Agent[] playingAgents = board.getAgents();
        for (Agent a : agents) {
            if (a == null) {
                throw new IllegalArgumentException("An agent cannot be null.");
            } else if (!Arrays.asList(playingAgents).contains(a)) {
                throw new IllegalArgumentException("A non playing agent was given.");
            }
        }
        applyCardSpecific(board, agents, number);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass().equals(obj.getClass())) {
            return cardSpecification.equals(((HeimlichAndCoCard) obj).cardSpecification);
        } else {
            return false;
        }
    }

    /**
     * Returns the HeimlichAndCoCardSpecification for the given card.
     * Giving information about how many agents or numbers are needed to apply the card and more.
     *
     * @return the card specification
     */
    public final HeimlichAndCoCardSpecification getCardSpecification() {
        return cardSpecification;
    }

    /**
     * Applies this card.
     * Used to implement the specific impacts of a card.
     *
     * @param board  to which the Card should be applied
     * @param agents which should be used for the card (if applicable)
     * @param number which determine how far agents should be moved (if applicable)
     */
    protected abstract void applyCardSpecific(HeimlichAndCoBoard board, Agent[] agents, int number);

    /**
     * Checks arguments given when applying/playing a card against the card specification
     *
     * @param agents the given agents
     * @param number the given number
     * @return true if the parameters are valid, false otherwise
     */
    private boolean checkArgumentsAgainstCardSpecification(Agent[] agents, int number) {
        // first check agents constraints
        if (agents == null) {
            if (cardSpecification.minNumberOfAgents != 0) {
                return false;
            }
        } else {
            if (agents.length < cardSpecification.minNumberOfAgents || agents.length > cardSpecification.maxNumberOfAgents) { //agent array too small/large
                return false;
            }
        }
        if (!cardSpecification.numberNeeded && number!= 0) {
            return false;
        }
        return true;
    }
}
