package HeimlichAndCo.Util;

import java.util.*;

public class CardStack<T> {

    List<T> cards;

    public CardStack(List<T> cards) {
        this.cards = new ArrayList<>(cards);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public T drawCard() {
        if (cards.size() == 0) {
            return null;
        }
        T returnCard = cards.get(0);
        cards.remove(0);
        return returnCard;
    }

    public CardStack<T> clone() {
        List<T> newCards = new LinkedList<>(this.cards); //TODO
        return new CardStack<>(newCards);
    }

    public boolean isEmpty() {
        return cards.size() == 0;
    }

    public int count() {return cards.size();}
}
