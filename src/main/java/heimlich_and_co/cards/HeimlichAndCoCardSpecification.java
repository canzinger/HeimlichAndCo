package heimlich_and_co.cards;

import java.util.Objects;

/**
 * Class acts as further information for a card.
 * Provides information on the number of agents needed to play a card, whether a number is needed, what type the card is,
 * and whether the order of the agents given to the card when playing it will be relevant.
 */
public class HeimlichAndCoCardSpecification {

    /**
     * Minimum number of agents needed to play the card.
     */
    public final int minNumberOfAgents;
    /**
     * Maximum number of agents allowed when playing the card.
     */
    public final int maxNumberOfAgents;

    /**
     * Whether a number is needed when playing the card.
     */
    public final boolean numberNeeded;

    /**
     * The type of the card.
     * Currently, only relevant for the MoveAgentsCard.
     */
    public final int type;

    /**
     * Whether the order of the agents given to a card when applying it makes a difference.
     */
    public final boolean agentsOrderInvariant;

    /**
     * Creates a new HeimlichAndCoCardSpecification instance with the given values.
     */
    public HeimlichAndCoCardSpecification(int minNumberOfAgents, int maxNumberOfAgents, boolean numberNeeded, int type, boolean agentsOrderInvariant) {
        this.minNumberOfAgents = minNumberOfAgents;
        this.maxNumberOfAgents = maxNumberOfAgents;
        this.numberNeeded = numberNeeded;
        this.type = type;
        this.agentsOrderInvariant = agentsOrderInvariant;
    }

    public HeimlichAndCoCardSpecification(HeimlichAndCoCardSpecification spec) {
        this(spec.minNumberOfAgents, spec.maxNumberOfAgents, spec.numberNeeded, spec.type, spec.agentsOrderInvariant);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeimlichAndCoCardSpecification that = (HeimlichAndCoCardSpecification) o;
        return minNumberOfAgents == that.minNumberOfAgents && maxNumberOfAgents == that.maxNumberOfAgents && numberNeeded == that.numberNeeded && type == that.type && agentsOrderInvariant == that.agentsOrderInvariant;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minNumberOfAgents, maxNumberOfAgents, numberNeeded, type, agentsOrderInvariant);
    }

    @Override
    public String toString() {
        return "HeimlichAndCoCardSpecification{" +
                "minNumberOfAgents=" + minNumberOfAgents +
                ", maxNumberOfAgents=" + maxNumberOfAgents +
                ", numberNeeded=" + numberNeeded +
                ", type=" + type +
                ", agentsOrderInvariant=" + agentsOrderInvariant +
                '}';
    }
}
