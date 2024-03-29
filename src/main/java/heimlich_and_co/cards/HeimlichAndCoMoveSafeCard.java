package heimlich_and_co.cards;

import heimlich_and_co.HeimlichAndCoBoard;
import heimlich_and_co.actions.HeimlichAndCoCardAction;
import heimlich_and_co.enums.Agent;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the cards that can be used to move the safe.
 * In the traditional game, there is only one specific type of this card.
 */
public class HeimlichAndCoMoveSafeCard extends HeimlichAndCoCard {

    /**
     * Creates a new HeimlichAndCoAddScorePointCard with the given card specification
     *
     * @param cardSpecification card specification for the new card
     */
    public HeimlichAndCoMoveSafeCard(HeimlichAndCoCardSpecification cardSpecification) {
        super(cardSpecification);
        if (cardSpecification.type != 0) {
            throw new IllegalArgumentException("Type in Card Specification must be 0 for this type of Card");
        }
    }

    /**
     * Creates a deep copy of the given card.
     *
     * @param card card to copy
     */
    public HeimlichAndCoMoveSafeCard(HeimlichAndCoMoveSafeCard card) {
        this(card.cardSpecification);
    }

    @Override
    public HeimlichAndCoMoveSafeCard deepCopy() {
        return new HeimlichAndCoMoveSafeCard(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (HeimlichAndCoMoveSafeCard.class == obj.getClass()) {
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
        int numberOfFields = board.getNumberOfFields();
        for (int i = 0; i < numberOfFields; i++) {
            if (board.getSafePosition() == i) {
                continue;
            }
            actions.add(new HeimlichAndCoCardAction(this, null, i));
        }
        return actions;
    }

    @Override
    public int hashCode() {
        return cardSpecification.hashCode() * 53;
    }

    public String toString() {
        return "MoveSafeCard: Move the safe in a building of your choice.";
    }

    /**
     * Applies this specific card to the board.
     * I.e. moves the safe to the given field.
     *
     * @param board  to which the Card should be applied
     * @param agents which should be used for the card (not needed here/may be null)
     * @param number which determine how far agents should be moved
     */
    @Override
    protected void applyCardSpecific(HeimlichAndCoBoard board, Agent[] agents, int number) {
        board.moveSafe(number);
    }
}
