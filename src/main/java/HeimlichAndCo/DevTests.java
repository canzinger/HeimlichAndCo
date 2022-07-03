package HeimlichAndCo;

public class DevTests {
    public static void main(String[] args) {
        HeimlichAndCoBoard board = new HeimlichAndCoBoard(5);
        System.out.println(board);
        board.moveSafe(11);
        board.moveAgent(Agent.Perry, 3);
        System.out.println(board);
        System.out.println(HeimlichAndCoAction.getPossibleActions(board, 7, 0).size());
    }
}
