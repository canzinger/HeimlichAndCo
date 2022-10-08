package HeimlichAndCo.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardStack<T> {

    private final List<T> cards;

    public CardStack(List<T> cards) {
        this.cards = new ArrayList<>(cards);
    }

    public int count() {
        return cards.size();
    }

    public T drawCard() {
        if (cards.size() == 0) {
            return null;
        }
        T returnCard = cards.get(0);
        cards.remove(0);
        return returnCard;
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public List<T> getCards() {
        return cards;
    }

    public boolean isEmpty() {
        return cards.size() == 0;
    }
}
