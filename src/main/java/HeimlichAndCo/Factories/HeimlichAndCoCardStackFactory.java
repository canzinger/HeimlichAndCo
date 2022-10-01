package HeimlichAndCo.Factories;

import HeimlichAndCo.Cards.*;
import HeimlichAndCo.Util.CardStack;

import java.util.LinkedList;

public class HeimlichAndCoCardStackFactory {

    public static CardStack<HeimlichAndCoCard> newInstance() {
        LinkedList<HeimlichAndCoCard> cards = new LinkedList<>();

        //Add Move Safe Cards
        cards.add(new HeimlichAndCoMoveSafeCard(new HeimlichAndCoCardSpecification(0,0,true, 0)));
        cards.add(new HeimlichAndCoMoveSafeCard(new HeimlichAndCoCardSpecification(0,0,true, 0)));

        //Add Score Points Cards
        cards.add(new HeimlichAndCoAddScorePointsCard(new HeimlichAndCoCardSpecification(2,2, false, 0)));
        cards.add(new HeimlichAndCoAddScorePointsCard(new HeimlichAndCoCardSpecification(2,2, false, 0)));

        //Add Move Agents Cards
        //type 0
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,1, true, 0)));
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,1, true, 0)));
        //type 1
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,1, true, 1)));
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,1, true, 1)));
        //type 2
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(2,2, false, 2)));
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(2,2, false, 2)));
        //type 3
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(2,2, false, 3)));
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(2,2, false, 3)));
        //type 4
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,1, true, 4)));
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,1, true, 4)));
        //type 5
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,1, false, 5)));
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,1, false, 5)));
        //type 6
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(0,0, false, 6)));
        //type 7
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(2,2, false, 7)));
        //type 8
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(2,2, false, 8)));
        //type 9
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,2, false, 9)));
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,2, false, 9)));
        //type 10
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,1, true, 10)));
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,1, true, 10)));
        //type 11
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,1, false, 11)));
        cards.add(new HeimlichAndCoMoveAgentsCard(new HeimlichAndCoCardSpecification(1,1, false, 11)));

        CardStack<HeimlichAndCoCard> cardStack = new CardStack<>(cards);
        cardStack.shuffle();
        return cardStack;
    }

    public static CardStack<HeimlichAndCoCard> newInstance(int size){
        CardStack<HeimlichAndCoCard> cardStack = newInstance();
        if (size > cardStack.count()) {
            throw new IllegalArgumentException("Size is too large, cannot create a cardStack with this many cards");
        }
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        while(cardStack.count() > size) {
            cardStack.drawCard();
        }
        return cardStack;
    }
}
