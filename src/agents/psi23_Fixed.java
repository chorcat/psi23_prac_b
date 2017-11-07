package agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class psi23_Fixed extends Agent {

	private int id;
	private int position;
	private Behaviour gameBehaviour;
	private static final int mycoins = 2;

	protected void setup() {
		System.out.println("Hello! Fixed Agent " + getAID().getName());

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Player");
		sd.setName(getAID().getName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(new getIdBehaviour());
	}

	/**
	 * Clean-up and finish operations of the Player Agent
	 */
	protected void takeDown() {
		// DeRegister the Main Agent into the Directory Facilitator
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Player Agent " + getAID().getName() + " terminating.");
	}

	private class getIdBehaviour extends OneShotBehaviour {

		@Override
		public void action() {
			ACLMessage msg = blockingReceive();
			if (msg != null) {
				id = Integer.parseInt(msg.getContent().split("#")[1]);
				System.out.println(getAID().getLocalName() + " he recibido el ID => " + id);
			}
			gameBehaviour = new gameBehaviour();
			addBehaviour(gameBehaviour);
		}

	}

	private class gameBehaviour extends Behaviour {

		private int step = 0;
		private int number_of_players_in_game = 0;
		private ArrayList<String> list_bets;

		@Override
		public void action() {

			switch (step) {
			case 0:
				ACLMessage msg = blockingReceive();
				if (msg != null) {
					position = Integer.parseInt(msg.getContent().split("#")[2]);
					System.out.println(getAID().getLocalName() + " he recibido la lista de IDs => "
							+ msg.getContent().split("#")[1]);
					System.out.println(getAID().getLocalName() + " he recibido la posicion => " + position);
					number_of_players_in_game = msg.getContent().split("#")[1].split(",").length;

					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent("MyCoins#" + mycoins);
					send(reply);

				}
				step++;
				break;
			case 1:
				int max_bet = number_of_players_in_game * 3;
				int min_bet = mycoins;
				int mybet;
				list_bets = new ArrayList<>();
				Random random = new Random();
				mybet = random.nextInt(max_bet - min_bet + 1) + min_bet;

				ACLMessage msg2 = blockingReceive();
				if (msg2.getContent().split("#").length > 1) {
					list_bets = new ArrayList<>(Arrays.asList(msg2.getContent().split("#")[1].split(",")));
					System.out.println(getAID().getLocalName() + " he recibido la lista de apuestas => " + list_bets);
					while (list_bets.contains(String.valueOf(mybet))) {
						mybet = random.nextInt(max_bet - min_bet + 1) + min_bet;
					}
				}

				ACLMessage reply2 = msg2.createReply();
				reply2.setPerformative(ACLMessage.INFORM);
				reply2.setContent("MyBet#" + mybet);
				send(reply2);
				
				step++;
				break;
			case 2:
				ACLMessage msg3 = blockingReceive();
				System.out.println(getAID().getLocalName() + " => " + msg3.getContent());
				step = 0;
				break;
			}
		}

		@Override
		public boolean done() {
			return false;
		}

	}

}
