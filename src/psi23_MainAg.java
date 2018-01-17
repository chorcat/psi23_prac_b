


import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import java.util.TreeMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class psi23_MainAg extends Agent {

	private psi23_GUI psi23_GUI;
	public Hashtable<AID, psi23_Player> table_aid_player;
	private ArrayList<String> localname_aid_table;
	private int id_given = 0;
	private boolean stop_gameBehaviour;
	private boolean finish_game;
	private psi23_MainAg.gameBehaviour gameBehaviour;
	private boolean first_round, winner_round;
	private int pos_winner_round;
	private int numero_de_juegos_por_partida = 5;
	private gameBehaviour game;

	private int played_juegos;

	protected void setup() {
		init();

		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd2 = new ServiceDescription();
				sd2.setType("Player");
				template.addServices(sd2);

				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					for (int i = 0; i < result.length; ++i) {
						if (!localname_aid_table.contains(result[i].getName().getLocalName())) {
							psi23_Player player = new psi23_Player();
							player.setName(result[i].getName().getLocalName());
							player.setId(id_given);
							player.setGames_win(0);
							player.setGames_lost(0);
							player.setInGame(true);
							player.setPosition(-1);
							player.setMycoins(-1);
							player.setMybet(-1);
							table_aid_player.put(result[i].getName(), player);
							localname_aid_table.add(result[i].getName().getLocalName());

							ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
							inform.addReceiver(result[i].getName());
							inform.setContent("Id#" + player.getId());
							send(inform);

							id_given++;
						}
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		});

		addBehaviour(new finish_game_Behaviour());

	}

	protected void init() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Main");
		sd.setName("Main-Agent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		table_aid_player = new Hashtable<>();
		localname_aid_table = new ArrayList<>();

		psi23_GUI = new psi23_GUI(this);
	}

	/**
	 * Clean-up and finish operations of the Main Agent
	 */
	protected void takeDown() {
		// DeRegister the Main Agent into the Directory Facilitator
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		//System.out.println("Main Agent " + getAID().getName() + " terminating.");
	}

	private int total_players_in_game() {
		int total_players = 0;
		Iterator<?> it = table_aid_player.entrySet().iterator();
		while (it.hasNext()) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
			if (pair.getValue().isInGame() && pair.getValue().getPosition() != -1) {
				total_players++;
			}
		}
		return total_players;
	}

	private String list_ids_players_in_game() {
		Map<AID, psi23_Player> sorted_table_aid_player = new LinkedHashMap<AID, psi23_Player>();

		List<Map.Entry<AID, psi23_Player>> list = new LinkedList<Map.Entry<AID, psi23_Player>>(
				table_aid_player.entrySet());
		Collections.sort(list, new psi23_SortPlayerPosition());
		for (Entry<AID, psi23_Player> entry : list) {
			if (entry.getValue().isInGame())
				sorted_table_aid_player.put(entry.getKey(), entry.getValue());
		}
		String list_ids = "";
		Iterator<?> it = sorted_table_aid_player.entrySet().iterator();
		while (it.hasNext()) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
			if (pair.getValue().isInGame()) {
				list_ids += pair.getValue().getId() + ",";
			}
		}

		list_ids = list_ids.substring(0, list_ids.length() - 1);

		return list_ids;
	}

	private void assign_position_round() {
		int pos_temp = 1;

		Map<AID, psi23_Player> sorted_table_aid_player = new LinkedHashMap<AID, psi23_Player>();

		List<Map.Entry<AID, psi23_Player>> list = new LinkedList<Map.Entry<AID, psi23_Player>>(
				table_aid_player.entrySet());
		Collections.sort(list, new psi23_SortPlayerID());
		for (Entry<AID, psi23_Player> entry : list) {
			sorted_table_aid_player.put(entry.getKey(), entry.getValue());
		}

		Iterator<?> it = sorted_table_aid_player.entrySet().iterator();
		while (it.hasNext()) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
			if (pair.getValue().isInGame()) {
				pair.getValue().setPosition(pos_temp);
				pos_temp++;
			}
		}
	}

	private void assign_position_consecutive_round() {

		Map<AID, psi23_Player> sorted_table_aid_player = new LinkedHashMap<AID, psi23_Player>();

		List<Map.Entry<AID, psi23_Player>> list = new LinkedList<Map.Entry<AID, psi23_Player>>(
				table_aid_player.entrySet());
		Collections.sort(list, new psi23_SortPlayerPosition());
		for (Entry<AID, psi23_Player> entry : list) {
			sorted_table_aid_player.put(entry.getKey(), entry.getValue());
		}

		Iterator<?> it = sorted_table_aid_player.entrySet().iterator();
		while (it.hasNext()) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
			if (pair.getValue().isInGame()) {
				if (pair.getValue().getPosition() == 1) {
					pair.getValue().setPosition(total_players_in_game());
				} else {
					pair.getValue().setPosition(pair.getValue().getPosition() - 1);
				}
			}
		}
	}

	private String list_bets_players_in_game() {
		TreeMap<Integer, Integer> map_position_bet = new TreeMap<Integer, Integer>();
		String list = "";
		Iterator<?> it = table_aid_player.entrySet().iterator();
		while (it.hasNext()) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
			if (pair.getValue().isInGame() && pair.getValue().getMybet() != -1) {
				map_position_bet.put(pair.getValue().getPosition(), pair.getValue().getMybet());
			}
		}
		Iterator<?> it2 = map_position_bet.entrySet().iterator();
		while (it2.hasNext()) {
			Entry<Integer, Integer> pair = (Entry<Integer, Integer>) it2.next();
			if (it2.hasNext())
				list += pair.getValue() + ",";
			else
				list += pair.getValue();
		}
		return list;
	}

	private String list_coins_hidden_players_in_game() {
		TreeMap<Integer, Integer> map_position_coins = new TreeMap<Integer, Integer>();
		String list = "";
		Iterator<?> it = table_aid_player.entrySet().iterator();
		while (it.hasNext()) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
			if (pair.getValue().isInGame()) {
				map_position_coins.put(pair.getValue().getPosition(), pair.getValue().getMycoins());
			}
		}
		Iterator<?> it2 = map_position_coins.entrySet().iterator();
		while (it2.hasNext()) {
			Entry<Integer, Integer> pair = (Entry<Integer, Integer>) it2.next();
			if (it2.hasNext())
				list += pair.getValue() + ",";
			else
				list += pair.getValue();
		}
		return list;
	}

	private int total_coins_hidden() {
		int coins = 0;
		Iterator<?> it = table_aid_player.entrySet().iterator();
		while (it.hasNext()) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
			if (pair.getValue().isInGame()) {
				coins += pair.getValue().getMycoins();
			}
		}
		return coins;
	}

	private int id_player_winner() {
		int id = -1;
		Iterator<?> it = table_aid_player.entrySet().iterator();
		while (it.hasNext() && id == -1) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
			if (pair.getValue().isInGame()) {
				if (total_coins_hidden() == pair.getValue().getMybet())
					id = pair.getValue().getId();
			}
		}
		return id;
	}

	private void player_win_round(int id_player_winner) {
		AID player_winner = null;

		Iterator<?> it = table_aid_player.entrySet().iterator();
		while (it.hasNext()) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
			pair.getValue().setMybet(-1);
			pair.getValue().setMycoins(-1);
			if (pair.getValue().isInGame() && pair.getValue().getId() == id_player_winner) {
				player_winner = pair.getKey();
			}
		}

		if (player_winner != null) {
			if (psi23_GUI.isCheck_comments())
				psi23_GUI.getAreaInfo().append(
						"\n" + getAID().getLocalName() + " => El ganador fue => " + player_winner.getLocalName());
			//System.out.println(getAID().getLocalName() + " => El ganador fue => " + player_winner.getLocalName());
			table_aid_player.get(player_winner).setGames_win(table_aid_player.get(player_winner).getGames_win() + 1);
			table_aid_player.get(player_winner).setInGame(false);
			table_aid_player.get(player_winner).setMybet(-1);
			table_aid_player.get(player_winner).setMycoins(-1);
			pos_winner_round = table_aid_player.get(player_winner).getPosition();
			winner_round = true;
		} else {
			if (psi23_GUI.isCheck_comments())
				psi23_GUI.getAreaInfo().append("\n" + getAID().getLocalName() + " => No hubo ganador.");
			//System.out.println(getAID().getLocalName() + " => No hubo ganador esta ronda.");
			winner_round = false;
		}

		psi23_GUI.getListPlayers().setText("NAME\tID\tPOSITION\tGAMES_WIN\tGAMES_LOST");
		Iterator<?> it2 = table_aid_player.entrySet().iterator();
		while (it2.hasNext()) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it2.next();
			//System.out.println(pair.getValue());
			if (psi23_GUI.isCheck_comments())
				psi23_GUI.getListPlayers().append("\n" + pair.getValue());
		}
		if (psi23_GUI.isCheck_comments())
			psi23_GUI.getAreaInfo().append("\n-----------------------------------------------------");
		//System.out.println("-----------------------------------------------------");
	}

	private void set_all_players_in_game() {
		Iterator<?> it = table_aid_player.entrySet().iterator();
		while (it.hasNext()) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
			pair.getValue().setInGame(true);
		}
	}

	private void resetPlayers() {
		Iterator<?> it = table_aid_player.entrySet().iterator();
		while (it.hasNext()) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
			pair.getValue().setGames_lost(0);
			pair.getValue().setGames_win(0);
		}
	}

	public void initGameBehaviour() {
		resetPlayers();
		set_all_players_in_game();
		assign_position_round();
		first_round = false;
		played_juegos = 1;
		addBehaviour(new refreshGUI());
		game = new gameBehaviour();
		addBehaviour(game);
	}

	public void setStop_gameBehaviour(boolean stop_gameBehaviour) {
		this.stop_gameBehaviour = stop_gameBehaviour;
	}

	public void removeGameBehaviour() {
		if (game != null)
			this.removeBehaviour(game);
	}

	/**
	 * @return the finish_game
	 */
	public boolean isFinish_game() {
		return finish_game;
	}

	/**
	 * @param finish_game
	 *            the finish_game to set
	 */
	public void setFinish_game(boolean finish_game) {
		this.finish_game = finish_game;
	}

	private class gameBehaviour extends Behaviour {

		private int step = 0;

		private Map<AID, psi23_Player> sorted_table_aid_player;

		@Override
		public void action() {
			switch (step) {
			case 0:
				
				psi23_GUI.getListPlayers().setText("NAME\tID\tPOSITION\tGAMES_WIN\tGAMES_LOST");
				Iterator<?> it10 = table_aid_player.entrySet().iterator();
				while (it10.hasNext()) {
					Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it10.next();
					//System.out.println(pair.getValue());
					psi23_GUI.getListPlayers().append("\n" + pair.getValue());
				}
				
				while (stop_gameBehaviour) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (first_round) {
					assign_position_consecutive_round();
				}
				step++;
				break;
			case 1:
				sorted_table_aid_player = new LinkedHashMap<AID, psi23_Player>();
				List<Map.Entry<AID, psi23_Player>> list = new LinkedList<Map.Entry<AID, psi23_Player>>(
						table_aid_player.entrySet());
				Collections.sort(list, new psi23_SortPlayerPosition());
				for (Entry<AID, psi23_Player> entry : list) {
					sorted_table_aid_player.put(entry.getKey(), entry.getValue());
				}

				Iterator<Entry<AID, psi23_Player>> it = sorted_table_aid_player.entrySet().iterator();
				while (it.hasNext()) {
					Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
					if (pair.getValue().getPosition() != -1 && pair.getValue().isInGame()) {
						ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
						request.addReceiver(pair.getKey());
						request.setContent(
								"GetCoins#" + list_ids_players_in_game() + "#" + pair.getValue().getPosition());
						if (psi23_GUI.isCheck_comments())
							psi23_GUI.getAreaInfo().append("\n" + request.getContent());
						send(request);
						
						ACLMessage msg = blockingReceive();
						pair.getValue().setMycoins(Integer.parseInt(msg.getContent().split("#")[1]));
						if (psi23_GUI.isCheck_comments())
							psi23_GUI.getAreaInfo()
									.append("\n" + getAID().getLocalName() + " => el Player ("
											+ pair.getValue().getName() + ") ha escondido "
											+ pair.getValue().getMycoins() + " monedas");
						//System.out.println(getAID().getLocalName() + " => el Player (" + pair.getValue().getName()
						//		+ ") ha escondido " + pair.getValue().getMycoins() + " monedas");
					}
				}

				step++;
				break;
			case 2:
				Iterator<?> it3 = sorted_table_aid_player.entrySet().iterator();
				while (it3.hasNext()) {
					Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it3.next();
					if (pair.getValue().getPosition() != -1 && pair.getValue().isInGame()) {
						ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
						request.addReceiver(pair.getKey());
						request.setContent("GuessCoins#" + list_bets_players_in_game());
						if (psi23_GUI.isCheck_comments())
							psi23_GUI.getAreaInfo().append("\n" + request.getContent());
						send(request);
						ACLMessage msg2 = blockingReceive();
						sorted_table_aid_player.get(msg2.getSender())
								.setMybet(Integer.parseInt(msg2.getContent().split("#")[1]));
						if (psi23_GUI.isCheck_comments())
							psi23_GUI.getAreaInfo()
									.append("\n" + getAID().getLocalName() + " => el Player ("
											+ msg2.getSender().getLocalName() + ") ha apostado "
											+ sorted_table_aid_player.get(msg2.getSender()).getMybet() + " monedas");
						//System.out.println(getAID().getLocalName() + " => el Player (" + msg2.getSender().getLocalName()
						//		+ ") ha apostado " + sorted_table_aid_player.get(msg2.getSender()).getMybet()
						//		+ " monedas");
					}
				}
				step++;
				break;
			case 3:
				Iterator<?> it4 = sorted_table_aid_player.entrySet().iterator();
				while (it4.hasNext()) {
					Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it4.next();
					if (pair.getValue().getPosition() != -1 && pair.getValue().isInGame()) {
						ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
						inform.addReceiver(pair.getKey());
						if (id_player_winner() != -1)
							inform.setContent("Result#" + id_player_winner() + "#" + total_coins_hidden() + "#"
									+ list_bets_players_in_game() + "#" + list_coins_hidden_players_in_game());
						else
							inform.setContent("Result##" + total_coins_hidden() + "#" + list_bets_players_in_game()
									+ "#" + list_coins_hidden_players_in_game());
						if (psi23_GUI.isCheck_comments())
							psi23_GUI.getAreaInfo().append("\n" + inform.getContent());
						send(inform);
					}
				}
				first_round = false;
				player_win_round(id_player_winner());
				if (!finish_game)
					step = 0;
				else
					step++;
				break;
			default:
				break;

			}
		}

		public int onEnd() {
			if (played_juegos != numero_de_juegos_por_partida) {
				set_all_players_in_game();
				first_round = true;
				played_juegos++;
				if (psi23_GUI.isCheck_comments())
					psi23_GUI.getAreaInfo()
							.append("\n" + "\n\n------------JUEGO " + played_juegos + " -----------\n\n");
				//System.out.println("\n\n------------JUEGO " + played_juegos + " -----------\n\n");
				game = new gameBehaviour();
				addBehaviour(game);
			}
			psi23_GUI.getListPlayers().setText("NAME\tID\tPOSITION\tGAMES_WIN\tGAMES_LOST");
			Iterator<?> it2 = table_aid_player.entrySet().iterator();
			while (it2.hasNext()) {
				Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it2.next();
				//System.out.println(pair.getValue());
				psi23_GUI.getListPlayers().append("\n" + pair.getValue());
			}
			// cambio
			step = 0;
			return step;
		}

		@Override
		public boolean done() {
			return finish_game;
		}
	}

	private class finish_game_Behaviour extends CyclicBehaviour {

		@Override
		public void action() {
			// TODO Auto-generated method stub

			int count_players = 0;
			Iterator<?> it = table_aid_player.entrySet().iterator();
			while (it.hasNext()) {
				Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
				if (pair.getValue().isInGame()) {
					count_players++;
				}
			}

			if (count_players == 1) {
				setFinish_game(true);
				Iterator<?> it2 = table_aid_player.entrySet().iterator();
				while (it2.hasNext()) {
					Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it2.next();
					if (pair.getValue().isInGame()) {
						pair.getValue().setGames_lost(pair.getValue().getGames_lost() + 1);
						pair.getValue().setInGame(false);
					}
				}
			} else {
				setFinish_game(false);
			}
		}
	}

	public int getNumero_de_juegos_por_partida() {
		return numero_de_juegos_por_partida;
	}

	public void setNumero_de_juegos_por_partida(int numero_de_juegos_por_partida) {
		this.numero_de_juegos_por_partida = numero_de_juegos_por_partida;
	}

	private class refreshGUI extends CyclicBehaviour {

		@Override
		public void action() {
			psi23_GUI.getLabelNPlayers().setText("NPlayers In Game= " + total_players_in_game());
			psi23_GUI.getLabelNGames().setText("NGames Played= " + played_juegos);
		}

	}

}
