package UnitTests;

import HeimlichAndCo.HeimlichAndCo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HeimlichAndCoTests {

    //region Constructor tests

    @Test
    public void given_Nothing_UsingBoardConstructorWithBoardEquals_1_ThenGameWithCards() {
        HeimlichAndCo game = new HeimlichAndCo("1", 2);
        Assertions.assertTrue(game.isWithCards());
    }

    @Test
    public void given_Nothing_UsingBoardConstructorWithBoardEquals_Cards_ThenGameWithCards() {
        HeimlichAndCo game = new HeimlichAndCo("Cards", 2);
        Assertions.assertTrue(game.isWithCards());
    }

    @Test
    public void given_Nothing_UsingBoardConstructorWithBoardEquals_cards_ThenGameWithCards() {
        HeimlichAndCo game = new HeimlichAndCo("cards", 2);
        Assertions.assertTrue(game.isWithCards());
    }

    @Test
    public void given_Nothing_UsingBoardConstructorWithBoardNull_ThenGameWithoutCards() {
        HeimlichAndCo game = new HeimlichAndCo(null, 2);
        Assertions.assertFalse(game.isWithCards());
    }

    @Test
    public void given_Nothing_UsingBoardConstructorWithBoardEquals_0_ThenGameWithoutCards() {
        HeimlichAndCo game = new HeimlichAndCo("0", 2);
        Assertions.assertFalse(game.isWithCards());
    }

    @Test
    public void given_Nothing_UsingBoardConstructorWithBoardEquals_NonSpecifiedValue_ThenGameWithoutCards() {
        HeimlichAndCo game = new HeimlichAndCo("NotSpecified", 2);
        Assertions.assertFalse(game.isWithCards());
    }

    //endregion

    @Test
    public void givenValidInstance_getGame_ReturnsDeepCopyOfGame() {
        HeimlichAndCo game = new HeimlichAndCo("1", 2);
        HeimlichAndCo copiedGame = (HeimlichAndCo) game.getGame();

        Assertions.assertNotSame(game, copiedGame);

        //compare primitives
        Assertions.assertAll(
                () -> Assertions.assertEquals(game.getNumberOfPlayers(), copiedGame.getNumberOfPlayers()),
                () -> Assertions.assertEquals(game.isWithCards(), copiedGame.isWithCards()),
                () -> Assertions.assertEquals(game.getCurrentPlayer(), copiedGame.getCurrentPlayer()),
                () -> Assertions.assertEquals(game.getCurrentPhase(), copiedGame.getCurrentPhase()),
                () -> Assertions.assertEquals(game.getCurrentTurnPlayer(), copiedGame.getCurrentTurnPlayer()),
                () -> Assertions.assertEquals(game.getPlayersSkippedInARowDuringCardPhase(), copiedGame.getPlayersSkippedInARowDuringCardPhase()),
                () -> Assertions.assertEquals(game.isAllowCustomDieRolls(), copiedGame.isAllowCustomDieRolls())
        );

        //compare objects
        Assertions.assertAll(
                () -> Assertions.assertNotSame(game.getBoard(), copiedGame.getBoard()),
                () -> Assertions.assertNotSame(game.getPlayersToAgentsMap(), copiedGame.getPlayersToAgentsMap()),
                () -> Assertions.assertNotSame(game.getCards(), copiedGame.getCards()),

                () -> Assertions.assertNotSame(game.getActionRecords(), copiedGame.getActionRecords()),
                () -> Assertions.assertEquals(game.getActionRecords(), copiedGame.getActionRecords()),

                () -> Assertions.assertNotSame(game.getCardStack(), copiedGame.getCardStack()),
                () -> Assertions.assertEquals(game.getCardStack().count(), copiedGame.getCardStack().count())
        );
    }


    @Test
    public void givenValidInstance_getGame_ReturnsDeepCopyOfGameWithInformationStrippedForCurrentPlayer() {
        HeimlichAndCo game = new HeimlichAndCo("1",2);
        HeimlichAndCo copiedGame = (HeimlichAndCo) game.getGame();

        //check if (correct) information is stripped
        Assertions.assertEquals(1, copiedGame.getPlayersToAgentsMap().size());
        Assertions.assertTrue(copiedGame.getPlayersToAgentsMap().containsKey(game.getCurrentPlayer()));

        Assertions.assertEquals(1, copiedGame.getCards().size());
        Assertions.assertTrue(copiedGame.getCards().containsKey(0));
    }

    @Test
    public void givenValidInstance_getGameWithPlayer_ReturnsDeepCopyOfGameWithInformationStrippedForGivenPlayer() {
        HeimlichAndCo game = new HeimlichAndCo("1", 2);
        HeimlichAndCo copiedGame = (HeimlichAndCo) game.getGame(1);
        Assertions.assertEquals(game.getCurrentPlayer(), copiedGame.getCurrentPlayer());

        Assertions.assertEquals(1, copiedGame.getPlayersToAgentsMap().size());
        Assertions.assertTrue(copiedGame.getPlayersToAgentsMap().containsKey(1));

        Assertions.assertEquals(1, copiedGame.getCards().size());
        Assertions.assertTrue(copiedGame.getCards().containsKey(1));
    }
}
