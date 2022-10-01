package HeimlichAndCo.Cards;

import HeimlichAndCo.Actions.HeimlichAndCoCardAction;
import HeimlichAndCo.Agent;
import HeimlichAndCo.HeimlichAndCoBoard;

import java.util.HashSet;
import java.util.Set;

public class HeimlichAndCoMoveSafeCard extends HeimlichAndCoCard {

    public HeimlichAndCoMoveSafeCard(HeimlichAndCoCardSpecification cardSpecification) {
        super(cardSpecification);
        if (cardSpecification.type != 0) {
            throw new IllegalArgumentException("Type in Card Specification must be 0 for this type of Card");
        }
    }

    @Override
    protected void applyCardSpecific(HeimlichAndCoBoard board, Agent[] agents, int number) {
        board.moveSafe(number);
    }

    @Override
    public Set<HeimlichAndCoCardAction> getPossibleActions(HeimlichAndCoBoard board) {
        Set<HeimlichAndCoCardAction> actions = new HashSet<>();
        int numberOfFields = board.getNumberOfFields();
        for (int i = 0; i < numberOfFields; i++) {
            if (board.getSafePosition() == i) {
                continue;
            }
            actions.add(new HeimlichAndCoCardAction(this, null, i));
        }
        return actions;
    }

}
