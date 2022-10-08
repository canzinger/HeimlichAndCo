package HeimlichAndCo.Cards;

import java.util.Objects;

public class HeimlichAndCoCardSpecification {

    public int minNumberOfAgents;
    public int maxNumberOfAgents;

    public boolean numberNeeded;

    public int type;

    public boolean agentsOrderInvariant;

    public HeimlichAndCoCardSpecification(int minNumberOfAgents, int maxNumberOfAgents, boolean numberNeeded, int type, boolean agentsOrderInvariant) {
        this.minNumberOfAgents = minNumberOfAgents;
        this.maxNumberOfAgents = maxNumberOfAgents;
        this.numberNeeded = numberNeeded;
        this.type = type;
        this.agentsOrderInvariant = agentsOrderInvariant;
    }

    @Override
    public HeimlichAndCoCardSpecification clone() {
        return new HeimlichAndCoCardSpecification(this.minNumberOfAgents, this.maxNumberOfAgents,
                this.numberNeeded, this.type, this.agentsOrderInvariant);
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

    public String toString() {
        return null;
    }
}
