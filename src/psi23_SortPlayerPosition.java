

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import jade.core.AID;

public class psi23_SortPlayerPosition implements Comparator<Map.Entry<AID, psi23_Player>> {

	@Override
	public int compare(Entry<AID, psi23_Player> o1, Entry<AID, psi23_Player> o2) {
		return o1.getValue().getPosition() - o2.getValue().getPosition();
	}

}
