package HeimlichAndCo.Actions;

import HeimlichAndCo.Agent;
import HeimlichAndCo.Cards.HeimlichAndCoCard;
import HeimlichAndCo.HeimlichAndCoBoard;

import java.util.Set;

public class HeimlichAndCoCardAction implements HeimlichAndCoAction {

    private final HeimlichAndCoCard card; //the card which should be played; if card == null it means that the player does not want/can play a card
    private final Agent[] agents;
    private final int number;

    public HeimlichAndCoCardAction(HeimlichAndCoCard card, Agent[] agents, int number) {
        this.card = card;
        this.agents = agents;
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

    public static Set<HeimlichAndCoCardAction> getPossibleActions(HeimlichAndCoBoard board, HeimlichAndCoCard card) {
        return card.getPossibleActions(board);
    }

    public static HeimlichAndCoCardAction getSkipCardAction() {return new HeimlichAndCoCardAction(null, null, 0);}

    public boolean isSkipCardAction() {
        return card == null;
    }
}
