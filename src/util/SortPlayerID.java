package util;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import agents.psi23_Player;
import jade.core.AID;

public class SortPlayerID implements Comparator<Map.Entry<AID, psi23_Player>> {

	@Override
	public int compare(Entry<AID, psi23_Player> o1, Entry<AID, psi23_Player> o2) {
		if (o1.getValue().isInGame() && o2.getValue().isInGame())
			return o1.getValue().getId() - o2.getValue().getId();
		else
			return 0;
	}

}
