package UnitTests;

import heimlich_and_co.Enums.Agent;
import heimlich_and_co.HeimlichAndCoBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Map;

class HeimlichAndCoBoardTests {

    //region Constructor tests

    @Test
    void given_Nothing_UsingEmptyConstructor_Then_NoFieldNullAndMapsFilledCorrectly() {
        HeimlichAndCoBoard board = new HeimlichAndCoBoard();
        Assertions.assertNotNull(board.getAgents());
        Assertions.assertNotNull(board.getAgentsPositions());
        Assertions.assertEquals(board.getAgents().length, board.getAgentsPositions().keySet().size());
        Assertions.assertNotNull(board.getScores());
        Assertions.assertEquals(board.getAgents().length, board.getScores().keySet().size());
    }

    @Test
    void given_Nothing_UsingConstructorWithNumberOfAgents_Then_NoFieldNullAndMapsFilledCorrectly() {
        HeimlichAndCoBoard board = new HeimlichAndCoBoard(7);
        Assertions.assertNotNull(board.getAgents());
        Assertions.assertNotNull(board.getAgentsPositions());
        Assertions.assertEquals(board.getAgents().length, board.getAgentsPositions().keySet().size());
        Assertions.assertNotNull(board.getScores());
        Assertions.assertEquals(board.getAgents().length, board.getScores().keySet().size());
    }

    @Test
    void given_Nothing_UsingConstructorWithInvalidNumberOfAgents_Then_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HeimlichAndCoBoard(8));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HeimlichAndCoBoard(4));
    }

    //endregion

    //region  Cloning tests

    @Test
    void given_Board_UsingClone_Then_NewDeepCopyOfBoard() {
        HeimlichAndCoBoard board = new HeimlichAndCoBoard(7);
        board.moveSafe(5);
        board.moveAgent(board.getAgents()[0], 6);
        HeimlichAndCoBoard newBoard = board.clone();

        Assertions.assertEquals(board.getLastDieRoll(), newBoard.getLastDieRoll());
        Assertions.assertArrayEquals(board.getAgents(), newBoard.getAgents());
        Assertions.assertEquals(board.getScoringTriggeredForAgent(), newBoard.getScoringTriggeredForAgent());

        //checking safe position and whether moving it on one board impacts the other one
        Assertions.assertEquals(board.getSafePosition(), newBoard.getSafePosition());
        newBoard.moveSafe(6);
        Assertions.assertNotEquals(board.getSafePosition(), newBoard.getSafePosition());

        //checking whether the agents positions are equal and whether moving an agent on one board impacts the other board
        Assertions.assertEquals(board.getAgentsPositions(), newBoard.getAgentsPositions());
        int oldPosition = board.getAgentsPositions().get(board.getAgents()[0]);
        newBoard.moveAgent(newBoard.getAgents()[0], 1);
        Assertions.assertEquals(board.getAgentsPositions().get(board.getAgents()[0]), oldPosition);
        Assertions.assertNotEquals(newBoard.getAgentsPositions().get(newBoard.getAgents()[0]), oldPosition);

        //checking whether the scores are equal and whether awarding points on one board impacts the other board
        Assertions.assertEquals(board.getScores(), newBoard.getScores());
        int oldScore = board.getScores().get(board.getAgents()[0]);
        Map<Agent, Integer> scores = newBoard.getScores();
        scores.replace(board.getAgents()[0], 100);
        Assertions.assertEquals(board.getScores().get(board.getAgents()[0]), oldScore);
        Assertions.assertEquals(100, newBoard.getScores().get(newBoard.getAgents()[0]));

    }

    //endregion

    //region Game Over tests

    @Test
    void given_BoardGameNotOver_CheckingWhetherGameOver_Then_ReturnsFalse() {
        HeimlichAndCoBoard board = new HeimlichAndCoBoard(5);
        Assertions.assertFalse(board.isGameOver());
    }

    @Test
    void given_BoardGameOver_CheckingWhetherGameOver_Then_ReturnsFalse() {
        HeimlichAndCoBoard board = new HeimlichAndCoBoard(5);
        Map<Agent, Integer> scores = board.getScores();
        Agent agent0 = board.getAgents()[0];
        scores.replace(agent0, 100);
        Assertions.assertTrue(board.isGameOver());
    }

    @Test
    void given_BoardGameOverMultiplePlayersEnoughPoints_CheckingWhetherGameOver_Then_ReturnsFalse() {
        HeimlichAndCoBoard board = new HeimlichAndCoBoard(5);
        Map<Agent, Integer> scores = board.getScores();
        Agent agent0 = board.getAgents()[0];
        scores.replace(agent0, 100);
        Agent agent1 = board.getAgents()[1];
        scores.replace(agent1, 100);
        Assertions.assertTrue(board.isGameOver());
    }

    //endregion



}
