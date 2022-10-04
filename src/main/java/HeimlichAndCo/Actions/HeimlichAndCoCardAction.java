package HeimlichAndCo.Actions;

import HeimlichAndCo.Agent;
import HeimlichAndCo.Cards.HeimlichAndCoCard;
import HeimlichAndCo.HeimlichAndCoBoard;

import java.util.Arrays;
import java.util.Set;

public class HeimlichAndCoCardAction implements HeimlichAndCoAction {

    private final HeimlichAndCoCard card; //the card which should be played; if card == null it means that the player does not want/can play a card
    private final Agent[] agents;
    private final int number;

    public HeimlichAndCoCardAction(HeimlichAndCoCard card, Agent[] agents, int number) {
        if (card != null) {
            this.card = card; //TODO clone
        } else {
            this.card = null;
        }
        if (agents != null) {
            this.agents = Arrays.copyOf(agents, agents.length);
        } else {
            this.agents = new Agent[] {};
        }
        this.number = number;
    }

    @Override
    public int applyAction(HeimlichAndCoBoard board) {
        if (card == null) {
            return 0;
        }
        card.applyCard(board, agents, number);
        return 0; //TODO
    }

    /**
     * calculates all possible actions for a board and a given card
     *
     * @param board current board
     * @param card the card for which actions should be calculated
     * @return Set of HeimlichAndCoCardActions with possible actions
     */
    public static Set<HeimlichAndCoCardAction> getPossibleActions(HeimlichAndCoBoard board, HeimlichAndCoCard card) {
        return card.getPossibleActions(board);
    }

    /**
     *
     * @return the Action that denotes that the player does not want/can play a card
     */
    public static HeimlichAndCoCardAction getSkipCardAction() {return new HeimlichAndCoCardAction(null, null, 0);}

    /**
     *
     * @return whether the Action is the Skip Action
     */
    public boolean isSkipCardAction() {
        return card == null;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (card != null) {
            hashCode += card.hashCode() * 11;
        } else {
            return 67;
        }
        hashCode += number * 17;
        for(int i = 0; i < agents.length; i++) {
            hashCode += (agents[i].ordinal() * (i + 10)) * 7;
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof HeimlichAndCoCardAction) {
            HeimlichAndCoCardAction toComp = (HeimlichAndCoCardAction) obj;
            if (this.isSkipCardAction() && toComp.isSkipCardAction()) {
                return true;
            }
            if (!this.card.equals(toComp.card) || this.number != toComp.number) {
                return false;
            }
            if (this.card.agentsOrderInvariant()) {
                if (this.agents.length == toComp.agents.length) {
                    Arrays.sort(this.agents);
                    Arrays.sort(toComp.agents);
                    return Arrays.equals(this.agents, toComp.agents);
                } else {
                    return false;
                }
            } else {
                return Arrays.equals(this.agents, toComp.agents);
            }

        } else {
            return false;
        }
    }
}
