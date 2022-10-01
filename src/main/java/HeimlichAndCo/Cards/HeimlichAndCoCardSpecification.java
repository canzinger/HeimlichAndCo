package HeimlichAndCo.Cards;

public class HeimlichAndCoCardSpecification {

    public int minNumberOfAgents;
    public int maxNumberOfAgents;

    public boolean numberNeeded;

    public int type;

    public HeimlichAndCoCardSpecification(int minNumberOfAgents, int maxNumberOfAgents, boolean numberNeeded, int type) {
        this.minNumberOfAgents = minNumberOfAgents;
        this.maxNumberOfAgents = maxNumberOfAgents;
        this.numberNeeded = numberNeeded;
        this.type = type;
    }

    @Override
    public HeimlichAndCoCardSpecification clone() {
        return new HeimlichAndCoCardSpecification(this.minNumberOfAgents, this.maxNumberOfAgents,
                this.numberNeeded, this.type);
    }

    public String toString() {
        return null;
    }
}
