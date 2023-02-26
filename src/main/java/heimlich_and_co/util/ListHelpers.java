package heimlich_and_co.util;

import at.ac.tuwien.ifs.sge.game.ActionRecord;
import heimlich_and_co.actions.HeimlichAndCoAction;

import java.util.LinkedList;
import java.util.List;

public class ListHelpers {

    private ListHelpers() {
    }

    /**
     * Creates a deep copy of a List of ActionRecord<HeimlichAndCoAction> by using clone on the respective actions.
     *
     * @param list list to be copied
     * @return LinkedList deep copy of the list
     */
    public static LinkedList<ActionRecord<HeimlichAndCoAction>> deepCopyActionRecordList(List<ActionRecord<HeimlichAndCoAction>> list) {
        LinkedList<ActionRecord<HeimlichAndCoAction>> newList = new LinkedList<>();
        for (ActionRecord<HeimlichAndCoAction> actionRecord : list) {
            newList.add(new ActionRecord<>(actionRecord.getPlayer(), actionRecord.getAction().deepCopy()));
        }
        return newList;
    }
}
