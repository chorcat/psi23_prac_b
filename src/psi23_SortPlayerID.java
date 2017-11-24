

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import jade.core.AID;

public class psi23_SortPlayerID implements Comparator<Map.Entry<AID, psi23_Player>> {

	@Override
	public int compare(Entry<AID, psi23_Player> o1, Entry<AID, psi23_Player> o2) {
		return o1.getValue().getId() - o2.getValue().getId();
	}

}
