package UnitTests;

import heimlich_and_co.HeimlichAndCo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class HeimlichAndCoTests {

    //region Constructor tests

    @ParameterizedTest
    @ValueSource(strings = {"1", "Cards", "cards"})
    void given_Nothing_UsingBoardConstructorWithBoardEquals_SpecifiedValue_ThenWithCards(String board) {
        HeimlichAndCo game = new HeimlichAndCo(board, 2);
        Assertions.assertTrue(game.isWithCards());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "elephant", "Not Specified"})
    void given_Nothing_UsingBoardConstructorWithBoardEquals_NonSpecifiedValue_ThenWithoutCards(String board) {
        HeimlichAndCo game = new HeimlichAndCo(board, 2);
        Assertions.assertFalse(game.isWithCards());
    }

    @Test
    void given_Nothing_UsingBoardConstructorWithBoardEquals_NonSpecifiedValue_ThenGameWithoutCards() {
        HeimlichAndCo game = new HeimlichAndCo("NotSpecified", 2);
        Assertions.assertFalse(game.isWithCards());
    }

    //endregion

    @Test
    void givenValidInstance_getGame_ReturnsDeepCopyOfGame() {
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
    void givenValidInstance_getGame_ReturnsDeepCopyOfGameWithInformationStrippedForCurrentPlayer() {
        HeimlichAndCo game = new HeimlichAndCo("1",2);
        HeimlichAndCo copiedGame = (HeimlichAndCo) game.getGame();

        //check if (correct) information is stripped
        Assertions.assertEquals(1, copiedGame.getPlayersToAgentsMap().size());
        Assertions.assertTrue(copiedGame.getPlayersToAgentsMap().containsKey(game.getCurrentPlayer()));

        Assertions.assertEquals(1, copiedGame.getCards().size());
        Assertions.assertTrue(copiedGame.getCards().containsKey(0));
    }

    @Test
    void givenValidInstance_getGameWithPlayer_ReturnsDeepCopyOfGameWithInformationStrippedForGivenPlayer() {
        HeimlichAndCo game = new HeimlichAndCo("1", 2);
        HeimlichAndCo copiedGame = (HeimlichAndCo) game.getGame(1);
        Assertions.assertEquals(game.getCurrentPlayer(), copiedGame.getCurrentPlayer());

        Assertions.assertEquals(1, copiedGame.getPlayersToAgentsMap().size());
        Assertions.assertTrue(copiedGame.getPlayersToAgentsMap().containsKey(1));

        Assertions.assertEquals(1, copiedGame.getCards().size());
        Assertions.assertTrue(copiedGame.getCards().containsKey(1));
    }
}
