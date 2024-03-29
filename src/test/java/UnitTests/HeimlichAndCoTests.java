package UnitTests;

import heimlich_and_co.HeimlichAndCo;
import heimlich_and_co.actions.HeimlichAndCoAction;
import heimlich_and_co.actions.HeimlichAndCoAgentMoveAction;
import heimlich_and_co.actions.HeimlichAndCoCardAction;
import heimlich_and_co.actions.HeimlichAndCoDieRollAction;
import heimlich_and_co.enums.Agent;
import heimlich_and_co.enums.HeimlichAndCoPhase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

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

    //region getGame

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

    //endregion

    //region test phase (sequences)

    @Test
    void givenNothing_CreatingNewGame_StartsInDieRollPhase() {
        HeimlichAndCo game = new HeimlichAndCo(3);

        Assertions.assertEquals(HeimlichAndCoPhase.DIE_ROLL_PHASE, game.getCurrentPhase());
    }

    @Test
    void givenDieRollPhase_NextPhaseIsAgentMovePhase() {
        HeimlichAndCo game = new HeimlichAndCo(3);
        game = game.doAction(game.getPossibleActions().iterator().next());

        Assertions.assertEquals(HeimlichAndCoPhase.AGENT_MOVE_PHASE, game.getCurrentPhase());
    }

    @Test
    void givenGameWithoutCardsAndAgentMovePhase_DoingActionWithoutTriggeringScoring_NextPhaseIsDieRollPhase() {
        HeimlichAndCo game = new HeimlichAndCo(3);
        game = game.doAction(game.getPossibleActions().iterator().next()); //-> to agentMovePhase

        game = game.doAction(game.getPossibleActions().iterator().next()); //-> we can safely do any action, as no action can trigger scoring at this point (safe on field 7, player can move at most 6 fields)
        Assertions.assertFalse(game.getBoard().scoringTriggered());

        Assertions.assertEquals(HeimlichAndCoPhase.DIE_ROLL_PHASE, game.getCurrentPhase());
    }

    @Test
    void givenGameWithoutCardsAndAgentMovePhase_DoingActionTriggeringScoring_NextPhaseIsSafeMovePhase() {
        HeimlichAndCo game = new HeimlichAndCo(3);
        game.setAllowCustomDieRolls(true);
        game.getBoard().moveSafe(2);

        game = game.doAction(new HeimlichAndCoDieRollAction(2));

        Agent agentToMove = game.getBoard().getAgents()[0];
        Map<Agent, Integer> moves = new EnumMap<>(Agent.class);
        moves.put(agentToMove, 2);
        HeimlichAndCoAction action = new HeimlichAndCoAgentMoveAction(moves);
        game = game.doAction(action);

        Assertions.assertEquals(HeimlichAndCoPhase.SAFE_MOVE_PHASE, game.getCurrentPhase());
    }

    @Test
    void givenSafeMovePhase_NextPhaseIsDieRollPhase() {
        HeimlichAndCo game = new HeimlichAndCo(3);
        game.setAllowCustomDieRolls(true);
        game.getBoard().moveSafe(2);

        game = game.doAction(new HeimlichAndCoDieRollAction(2));

        Agent agentToMove = game.getBoard().getAgents()[0];
        Map<Agent, Integer> moves = new EnumMap<>(Agent.class);
        moves.put(agentToMove, 2);
        HeimlichAndCoAction action = new HeimlichAndCoAgentMoveAction(moves);
        game = game.doAction(action); //-> to safe move phase

        game = game.doAction(game.getPossibleActions().iterator().next()); //to die roll phase

        Assertions.assertEquals(HeimlichAndCoPhase.DIE_ROLL_PHASE, game.getCurrentPhase());
    }

    @Test
    void givenGameWithCardsAndAgentMovePhase_NextPhaseIsCardPhase() {
        HeimlichAndCo game = new HeimlichAndCo("1", 3);
        game = game.doAction(game.getPossibleActions().iterator().next());
        game = game.doAction(game.getPossibleActions().iterator().next());

        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());
    }

    @Test
    void givenGameWithCardsAndScoringNotTriggeredAndCardPhase_AfterAllPlayersSkip_DieRollPhase() {
        HeimlichAndCo game = new HeimlichAndCo("1", 3);
        game = game.doAction(game.getPossibleActions().iterator().next());
        game = game.doAction(game.getPossibleActions().iterator().next());

        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());

        //all (3) players must skip before die roll phase
        game = game.doAction(HeimlichAndCoCardAction.getSkipCardAction());
        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());
        game = game.doAction(HeimlichAndCoCardAction.getSkipCardAction());
        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());
        game = game.doAction(HeimlichAndCoCardAction.getSkipCardAction());

        Assertions.assertEquals(HeimlichAndCoPhase.DIE_ROLL_PHASE, game.getCurrentPhase());
    }

    @Test
    void givenGameWithCardsAndScoringTriggeredAndCardPhase_AfterAllPlayersSkip_SafeMovePhase() {
        HeimlichAndCo game = new HeimlichAndCo("1", 3);
        game.setAllowCustomDieRolls(true);
        game.getBoard().moveSafe(2);

        game = game.doAction(new HeimlichAndCoDieRollAction(2));

        Agent agentToMove = game.getBoard().getAgents()[0];
        Map<Agent, Integer> moves = new EnumMap<>(Agent.class);
        moves.put(agentToMove, 2);
        HeimlichAndCoAction action = new HeimlichAndCoAgentMoveAction(moves);
        game = game.doAction(action); //-> to die roll phase

        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());
        game = game.doAction(HeimlichAndCoCardAction.getSkipCardAction());
        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());
        game = game.doAction(HeimlichAndCoCardAction.getSkipCardAction());
        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());
        game = game.doAction(HeimlichAndCoCardAction.getSkipCardAction());

        Assertions.assertEquals(HeimlichAndCoPhase.SAFE_MOVE_PHASE, game.getCurrentPhase());
    }

    @Test
    void givenGameWithCardsAndScoringNotTriggered_PlayingCardsExtendsCardPhase() {
        HeimlichAndCo game = new HeimlichAndCo("1", 3);
        game = game.doAction(game.getPossibleActions().iterator().next());
        game = game.doAction(game.getPossibleActions().iterator().next());

        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());

        game = game.doAction(HeimlichAndCoCardAction.getSkipCardAction());
        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());
        game = game.doAction(HeimlichAndCoCardAction.getSkipCardAction());
        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());

        for(HeimlichAndCoAction action: game.getPossibleActions()) {
            if (action.getClass().equals(HeimlichAndCoCardAction.class)) {
                if (!((HeimlichAndCoCardAction)action).isSkipCardAction()) {
                    game = game.doAction(action);
                    break;
                }
            }
        }
        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());

        game = game.doAction(HeimlichAndCoCardAction.getSkipCardAction());
        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());
        game = game.doAction(HeimlichAndCoCardAction.getSkipCardAction());
        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());
        game = game.doAction(HeimlichAndCoCardAction.getSkipCardAction());

        Assertions.assertNotEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, game.getCurrentPhase());
    }

    //endregion

    // region DisqualificationTests

    @Test
    void givenValidInstance_disqualifyingPlayerInDieRollPhase_ReturnsNewGameWithNextPlayerInDieRollPhase() {
        HeimlichAndCo game = new HeimlichAndCo(3);
        int currentPlayerToDisqualify = game.getCurrentPlayer();
        HeimlichAndCo newGame = game.disqualifyCurrentPlayer();

        Assertions.assertTrue(newGame.getDisqualifiedPlayers().contains(currentPlayerToDisqualify));
        Assertions.assertEquals(1, newGame.getDisqualifiedPlayers().size());

        Assertions.assertEquals(HeimlichAndCoPhase.DIE_ROLL_PHASE, newGame.getCurrentPhase());
        Assertions.assertEquals(1, newGame.getCurrentPlayer());

    }

    @Test
    void givenValidInstance_disqualifyingPlayerInAgentMovePhase_ReturnsNewGameWithNextPlayerInDieRollPhase() {
        HeimlichAndCo game = new HeimlichAndCo(3);
        game = game.doAction(game.getPossibleActions().iterator().next());
        int currentPlayerToDisqualify = game.getCurrentPlayer();
        HeimlichAndCo newGame = game.disqualifyCurrentPlayer();

        Assertions.assertTrue(newGame.getDisqualifiedPlayers().contains(currentPlayerToDisqualify));
        Assertions.assertEquals(1, newGame.getDisqualifiedPlayers().size());

        Assertions.assertEquals(HeimlichAndCoPhase.DIE_ROLL_PHASE, newGame.getCurrentPhase());
        Assertions.assertEquals(1, newGame.getCurrentPlayer());
    }

    @Test
    void givenValidInstance_disqualifyPlayerInCardPlayPhase_ReturnsNewGameWithNextPlayerInCardPlayPhase() {
        HeimlichAndCo game = new HeimlichAndCo("1", 3);
        game = game.doAction(game.getPossibleActions().iterator().next());
        game = game.doAction(game.getPossibleActions().iterator().next());
        int currentPlayerToDisqualify = game.getCurrentPlayer();
        HeimlichAndCo newGame = game.disqualifyCurrentPlayer();

        Assertions.assertTrue(newGame.getDisqualifiedPlayers().contains(currentPlayerToDisqualify));
        Assertions.assertEquals(1, newGame.getDisqualifiedPlayers().size());

        Assertions.assertEquals(HeimlichAndCoPhase.CARD_PLAY_PHASE, newGame.getCurrentPhase());
        Assertions.assertEquals(1, newGame.getCurrentPlayer());
    }

    @Test
    void givenValidInstance_disqualifyPlayerInSafeMovePhase_ReturnsNewGameWithNextPlayerInDieRollPhaseWithSafeOnField7() {
        HeimlichAndCo game = new HeimlichAndCo(3);
        int currentPlayerToDisqualify = game.getCurrentPlayer();
        game.setAllowCustomDieRolls(true);
        game = game.doAction(new HeimlichAndCoDieRollAction(13));

        game.getBoard().moveSafe(1);
        Agent agentForScoring = game.getBoard().getAgents()[0];
        Map<Agent, Integer> movesForAction = new HashMap<>();
        movesForAction.put(agentForScoring, 1);
        game = game.doAction(new HeimlichAndCoAgentMoveAction(movesForAction));

        Assertions.assertEquals(HeimlichAndCoPhase.SAFE_MOVE_PHASE, game.getCurrentPhase());

        HeimlichAndCo newGame = game.disqualifyCurrentPlayer();

        Assertions.assertTrue(newGame.getDisqualifiedPlayers().contains(currentPlayerToDisqualify));
        Assertions.assertEquals(1, newGame.getDisqualifiedPlayers().size());
        Assertions.assertEquals(HeimlichAndCoPhase.DIE_ROLL_PHASE, newGame.getCurrentPhase());
        Assertions.assertEquals(1, newGame.getCurrentPlayer());
        Assertions.assertEquals(7, newGame.getBoard().getSafePosition());
        Assertions.assertEquals(1, newGame.getBoard().getScores().get(agentForScoring));
    }

    @Test
    void givenValidInstanceWithoutCards_disqualifyPlayer_ReturnsGameWithPlayerDisqualified() {
        HeimlichAndCo game = new HeimlichAndCo(3);
        HeimlichAndCo newGame = game.disqualifyCurrentPlayer();

        Assertions.assertEquals(1, newGame.getDisqualifiedPlayers().size());
        Assertions.assertTrue(newGame.getDisqualifiedPlayers().contains(0));
    }

    @Test
    void givenValidInstanceWithCards_disqualifyPlayer_ReturnsGameWithPlayerDisqualified() {
        HeimlichAndCo game = new HeimlichAndCo("1",3);
        HeimlichAndCo newGame = game.disqualifyCurrentPlayer();

        Assertions.assertEquals(1, newGame.getDisqualifiedPlayers().size());
        Assertions.assertTrue(newGame.getDisqualifiedPlayers().contains(0));
    }

    //This is generally not a good test case, but the only way to check
    @Test
    void givenValidInstanceWithoutCards_disqualifyPlayer_DisqualifiedPlayerNeverBecomesCurrentPlayerAgain() {
        HeimlichAndCo game = new HeimlichAndCo(3);
        game = game.disqualifyCurrentPlayer();
        Assertions.assertTrue(game.getDisqualifiedPlayers().contains(0));
        while(!game.isGameOver()) {
            game = game.doAction(game.getPossibleActions().iterator().next());
            Assertions.assertNotEquals(0, game.getCurrentPlayer());
        }
    }

    //This is generally not a good test case, but the only way to check
    @Test
    void givenValidInstanceWithCards_disqualifyPlayer_DisqualifiedPlayerNeverBecomesCurrentPlayerAgain() {
        HeimlichAndCo game = new HeimlichAndCo("1",3);
        game = game.disqualifyCurrentPlayer();
        Assertions.assertTrue(game.getDisqualifiedPlayers().contains(0));
        while(!game.isGameOver()) {
            game = game.doAction(game.getPossibleActions().iterator().next());
            Assertions.assertNotEquals(0, game.getCurrentPlayer());
        }
    }

    @Test
    void givenValidInstanceWith2Players_disqualifyPlayer_ThrowsIllegalStateException() {
        HeimlichAndCo game = new HeimlichAndCo(2);
        Assertions.assertThrows(IllegalStateException.class, game::disqualifyCurrentPlayer);
    }



    //endregion


}
