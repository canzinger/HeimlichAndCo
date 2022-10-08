package HeimlichAndCo;

import HeimlichAndCo.Actions.HeimlichAndCoAction;
import HeimlichAndCo.Actions.HeimlichAndCoDieRollAction;
import HeimlichAndCo.Enums.Agent;

public class DevTests {
    public static void main(String[] args) {
        HeimlichAndCoBoard board = new HeimlichAndCoBoard(5);
        System.out.println(board);
        board.moveSafe(11);
        board.moveAgent(Agent.Perry, 3);
        System.out.println(board);

        HeimlichAndCo hc = new HeimlichAndCo(null, 4);
        System.out.println(hc.getNumberOfPlayers());


        HeimlichAndCoDieRollAction dieRollAction1 = HeimlichAndCoDieRollAction.getRandomRollAction();
        HeimlichAndCoAction dieRollAction2 = HeimlichAndCoDieRollAction.getRandomRollAction();
        System.out.println(dieRollAction1.equals(dieRollAction2));

    }
}
