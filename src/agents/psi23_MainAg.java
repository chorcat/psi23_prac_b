package agents;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import gui_interface.psi23_GUI;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
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
	private TickerBehaviour gameBehaviour;

	protected void setup() {
		init();

		addBehaviour(new TickerBehaviour(this, 5000) {

			@Override
			protected void onTick() {
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
		System.out.println("Main Agent " + getAID().getName() + " terminating.");
	}

	private String list_ids_players_in_game() {
		String list = "";
		Iterator<?> it = table_aid_player.entrySet().iterator();
		while (it.hasNext()) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
			if (pair.getValue().isInGame() && pair.getValue().getPosition() != -1) {
				list += pair.getValue().getId() + ",";
			}
		}

		list = list.substring(0, list.length() - 1);

		return list;
	}

	private void assign_position_round() {
		int position_temp = 1;
		Iterator<?> it = table_aid_player.entrySet().iterator();
		while (it.hasNext()) {
			Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
			if (pair.getValue().isInGame()) {
				pair.getValue().setPosition(position_temp);
				position_temp++;
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
			System.out.println(getAID().getLocalName() + " => El ganador fue => " + player_winner.getLocalName());
			table_aid_player.get(player_winner).setGames_win(table_aid_player.get(player_winner).getGames_win() + 1);
			table_aid_player.get(player_winner).setInGame(false);
			table_aid_player.get(player_winner).setPosition(-1);
			table_aid_player.get(player_winner).setMybet(-1);
			table_aid_player.get(player_winner).setMycoins(-1);
		} else {
			System.out.println(getAID().getLocalName() + " => No hubo ganador esta ronda.");
		}
		System.out.println("-----------------------------------------------------");
	}

	public void initGameBehaviour() {
		gameBehaviour = new gameBehaviour(this, 10000);
		addBehaviour(gameBehaviour);
	}

	public void setStop_gameBehaviour(boolean stop_gameBehaviour) {
		this.stop_gameBehaviour = stop_gameBehaviour;
	}

	public void removeGameBehaviour() {
		if (gameBehaviour != null)
			this.removeBehaviour(gameBehaviour);
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

	private class gameBehaviour extends TickerBehaviour {

		private int step = 0;

		public gameBehaviour(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			if (!stop_gameBehaviour && !finish_game) {
				switch (step) {
				case 0:
					assign_position_round();
					step++;
					break;
				case 1:
					Iterator<Entry<AID, psi23_Player>> it = table_aid_player.entrySet().iterator();
					while (it.hasNext()) {
						Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
						if (pair.getValue().getPosition() != -1 && pair.getValue().isInGame()) {
							ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
							request.addReceiver(pair.getKey());
							request.setContent(
									"GetCoins#" + list_ids_players_in_game() + "#" + pair.getValue().getPosition());
							send(request);
						}
					}

					Iterator<Entry<AID, psi23_Player>> it2 = table_aid_player.entrySet().iterator();
					while (it2.hasNext()) {
						Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it2.next();
						if (pair.getValue().getPosition() != -1 && pair.getValue().isInGame()) {
							ACLMessage msg = blockingReceive();
							pair.getValue().setMycoins(Integer.parseInt(msg.getContent().split("#")[1]));
							System.out.println(getAID().getLocalName() + " => el Player (" + pair.getValue().getName()
									+ ") ha escondido " + pair.getValue().getMycoins() + " monedas");
						}
					}
					step++;
					break;

				case 2:
					Iterator<?> it3 = table_aid_player.entrySet().iterator();
					while (it3.hasNext()) {
						Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it3.next();
						if (pair.getValue().getPosition() != -1 && pair.getValue().isInGame()) {

							ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
							request.addReceiver(pair.getKey());
							request.setContent("GuessCoins#" + list_bets_players_in_game());
							send(request);
							ACLMessage msg2 = blockingReceive();
							table_aid_player.get(msg2.getSender())
									.setMybet(Integer.parseInt(msg2.getContent().split("#")[1]));
							System.out.println(getAID().getLocalName() + " => el Player ("
									+ msg2.getSender().getLocalName() + ") ha apostado "
									+ table_aid_player.get(msg2.getSender()).getMybet() + " monedas");
						}
					}
					step++;
					break;

				case 3:
					Iterator<?> it4 = table_aid_player.entrySet().iterator();
					while (it4.hasNext()) {
						Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it4.next();
						if (pair.getValue().getPosition() != -1 && pair.getValue().isInGame()) {
							ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
							inform.addReceiver(pair.getKey());
							inform.setContent("Result#" + id_player_winner() + "#" + total_coins_hidden() + "#"
									+ list_bets_players_in_game() + "#" + list_coins_hidden_players_in_game());
							send(inform);
						}
					}

					player_win_round(id_player_winner());
					step = 0;
					break;

				}
			}
		}
	}

	private class finish_game_Behaviour extends CyclicBehaviour {

		private AID player_lost;

		@Override
		public void action() {
			int count_players = 0;
			Iterator<?> it = table_aid_player.entrySet().iterator();
			while (it.hasNext()) {
				Entry<AID, psi23_Player> pair = (Entry<AID, psi23_Player>) it.next();
				if (pair.getValue().isInGame()) {
					count_players++;
					setPlayer_lost(pair.getKey());
					setFinish_game(true);
				}
			}

			if (count_players == 1) {
				setFinish_game(true);
			} else {
				setFinish_game(false);
			}
		}

		/**
		 * @return the player_lost
		 */
		public AID getPlayer_lost() {
			return player_lost;
		}

		/**
		 * @param player_lost
		 *            the player_lost to set
		 */
		public void setPlayer_lost(AID player_lost) {
			this.player_lost = player_lost;
		}

	}

}
