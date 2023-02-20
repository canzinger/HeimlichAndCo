package heimlich_and_co.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardStack<T> {

    /**
     * Saves the cards that are on the stack
     */
    private final List<T> cards;

    /**
     * Creates a new CardStack instance with the given cards.
     * Note: Does not shuffle automatically.
     *
     * @param cards cards for the stack
     */
    public CardStack(List<T> cards) {
        this.cards = new ArrayList<>(cards);
    }

    /**
     * Returns how many cards are in the stack
     */
    public int count() {
        return cards.size();
    }

    /**
     * Draws a card from teh stack and returns it.
     * Returns null if there are no cards left.
     *
     * @return the drawn card
     */
    public T drawCard() {
        if (cards.size() == 0) {
            return null;
        }
        T returnCard = cards.get(0);
        cards.remove(0);
        return returnCard;
    }

    /**
     * Shuffles this stack.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    public List<T> getCards() {
        return cards;
    }

    /**
     * Returns whether the stack is empty
     */
    public boolean isEmpty() {
        return cards.size() == 0;
    }
}
