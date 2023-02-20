package UnitTests;

import heimlich_and_co.Cards.HeimlichAndCoCard;
import heimlich_and_co.Factories.HeimlichAndCoCardStackFactory;
import heimlich_and_co.Util.CardStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CardStackFactoryTests {

    //region newInstance() tests

    @Test
    void given_Nothing_UsingEmptyFactoryMethod_Then_CardStackWithSize25() {
        CardStack<HeimlichAndCoCard> cardStack = HeimlichAndCoCardStackFactory.newInstance();
        Assertions.assertEquals(25, cardStack.count());
    }

    @Test
    void given_Nothing_UsingFactoryMethodWithSize_Then_CardStackWithCorrectSize() {
        CardStack<HeimlichAndCoCard> cardStack = HeimlichAndCoCardStackFactory.newInstance(10);
        Assertions.assertEquals(10, cardStack.count());
    }

    @Test
    void given_Nothing_UsingFactoryMethodWithNegativeSize_Then_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> HeimlichAndCoCardStackFactory.newInstance(-1));
    }

    @Test
    void given_Nothing_UsingFactoryMethodWithTooLargeSize_Then_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> HeimlichAndCoCardStackFactory.newInstance(26));
    }


}
