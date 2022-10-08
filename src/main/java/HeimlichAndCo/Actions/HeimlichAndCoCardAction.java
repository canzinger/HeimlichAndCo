package HeimlichAndCo.Actions;

import HeimlichAndCo.Agent;
import HeimlichAndCo.Cards.HeimlichAndCoCard;
import HeimlichAndCo.HeimlichAndCoBoard;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class HeimlichAndCoCardAction implements HeimlichAndCoAction {

    /**
     * The card which should be played; if card == null it means that the player does not want/can play a card
     */
    private final HeimlichAndCoCard card;
    /**
     * The agents which are needed for playing the card
     */
    private final Agent[] agents;
    /**
     * The number needed for playing the card
     */
    private final int number;

    /**
     * Creates a new HeimlichAndCoCardAction which is the action of playing the card with the given agents (if needed) and the number (if needed).
     * Whether agents or a number is needed depends on the card being player
     *
     * @param card the card to be played
     * @param agents the agents needed for playing the card
     * @param number the number needed for playing the card
     */
    public HeimlichAndCoCardAction(HeimlichAndCoCard card, Agent[] agents, int number) {
        if (card != null) {
            this.card = card.clone();
        } else {
            this.card = null;
        }
        if (agents != null) {
            this.agents = Arrays.copyOf(agents, agents.length);
        } else {
            this.agents = new Agent[]{};
        }
        this.number = number;
    }

    /**
     * Calculates all possible actions for a board and a given card.
     * Calls the respective method of the implementing classes
     *
     * @param board current board
     * @param card  the card for which actions should be calculated
     * @return Set of HeimlichAndCoCardActions with possible actions
     */
    public static Set<HeimlichAndCoCardAction> getPossibleActions(HeimlichAndCoBoard board, HeimlichAndCoCard card) {
        return card.getPossibleActions(board);
    }

    /**
     * @return the Action that denotes that the player does not want to or cannot play a card
     */
    public static HeimlichAndCoCardAction getSkipCardAction() {
        return new HeimlichAndCoCardAction(null, null, 0);
    }

    /**
     * Applies this action to the given board. The original board is changed.
     *
     * @param board board on which the action should be taken
     */
    @Override
    public void applyAction(HeimlichAndCoBoard board) {
        if (card != null) {
            card.applyCard(board, agents, number);
        }
    }

    public HeimlichAndCoCardAction clone() {
        if (card != null) {
            return new HeimlichAndCoCardAction(this.card.clone(), agents, number);
        } else {
            return new HeimlichAndCoCardAction(null, agents, number);
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj.getClass().equals(HeimlichAndCoCardAction.class)) {
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

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (card != null) {
            hashCode += card.hashCode() * 11;
        } else {
            return 67;
        }
        hashCode += number * 17;
        if (card.getCardSpecification().agentsOrderInvariant) {
            Arrays.sort(agents);
        }
        for (int i = 0; i < agents.length; i++) {
            hashCode += (agents[i].ordinal() * (i + 10)) * 7;
        }
        return hashCode;
    }

    /**
     * removes the card associated with this action (if there is one) from the given list
     *
     * @param list where card should be removed
     */
    public void removePlayedCardFromList(List<HeimlichAndCoCard> list) {
        if (card != null) {
            list.remove(card);
        }
    }

    public String toString() {
        if (isSkipCardAction()) {
            return "CardAction: skip";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CardAction: ");
        stringBuilder.append("Card: ").append(card.toString()).append(";");
        if (agents != null && agents.length > 0) {
            stringBuilder.append(" With Agents: ");
            for (int i = 0; i < agents.length - 1; i++) {
                stringBuilder.append(agents[i].toString()).append(", ");
            }
            stringBuilder.append(agents[agents.length - 1].toString()).append(";");
        }
        if (card.getCardSpecification().numberNeeded) {
            stringBuilder.append(" With Number: ").append(number);
        }
        return stringBuilder.toString();
    }

    /**
     * @return whether the Action is the Skip Action
     */
    public boolean isSkipCardAction() {
        return card == null;
    }
}
