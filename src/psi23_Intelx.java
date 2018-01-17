
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

public class psi23_Intelx extends Agent {

	private int id;
	private int position;
	private Behaviour gameBehaviour;
	private double eval = 0d;

	protected void setup() {
		System.out.println("Hello! Intelx Agent " + getAID().getName());

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
		// System.out.println("Player Agent " + getAID().getName() + " terminating.");
	}

	private int myCoins() {
		return (int) Math.floor(Math.random() * 4);
	}

	private class getIdBehaviour extends OneShotBehaviour {

		@Override
		public void action() {
			ACLMessage msg = blockingReceive();
			if (msg != null) {
				id = Integer.parseInt(msg.getContent().split("#")[1]);
				// System.out.println(getAID().getLocalName() + " he recibido el ID => " + id);
			}
			gameBehaviour = new gameBehaviour();
			addBehaviour(gameBehaviour);
		}

	}

	private class gameBehaviour extends Behaviour {

		private int step = 0;
		private int number_of_players_in_game = 0;
		private ArrayList<String> list_bets;
		private int mycoins;
		psi23_QLearning ql = new psi23_QLearning();
		int mybet;

		@Override
		public void action() {

			switch (step) {
			case 0:
				ACLMessage msg = blockingReceive();
				if (msg != null) {
					position = Integer.parseInt(msg.getContent().split("#")[2]);
					number_of_players_in_game = msg.getContent().split("#")[1].split(",").length;

					mycoins = myCoins();
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent("MyCoins#" + mycoins);
					send(reply);

				}
				step++;
				break;
			case 1:
				ACLMessage msg2 = blockingReceive();
	
				ql.vGetNewActionQLearning(String.valueOf(number_of_players_in_game) + "_" + String.valueOf(mycoins),
						((number_of_players_in_game * 3) + 1) - (3 - mycoins), eval);
				mybet = ql.iNewAction;
				if (msg2.getContent().split("#").length > 1) {
					list_bets = new ArrayList<>(Arrays.asList(msg2.getContent().split("#")[1].split(",")));
					while (list_bets.contains(String.valueOf(mybet))) {
						ql.vGetNewActionQLearning_it(String.valueOf(number_of_players_in_game) + "_" + String.valueOf(mycoins),
								((number_of_players_in_game * 3) + 1) - (3 - mycoins), eval, list_bets);
						mybet = ql.iNewAction;

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
				if (Integer.parseInt(msg3.getContent().split("#")[2]) == mybet) {
					eval += 0.03;
				} else {
					eval -= 0.001;
					if (eval < 0)
						eval = 0;
					ql.vGetNewActionQLearning(String.valueOf(number_of_players_in_game) + "_" + String.valueOf(mycoins),
							((number_of_players_in_game * 3) + 1) - (3 - mycoins), eval);
					ql.iAction = Integer.parseInt(msg3.getContent().split("#")[2]);

					eval += 0.01;
					
//					eval -= 0.001;
//					if (eval < 0)
//						eval = 0;
//					ql.Refuerzo(Integer.parseInt(msg3.getContent().split("#")[2]), eval,
//							String.valueOf(number_of_players_in_game) + "_" + String.valueOf(mycoins));
//					eval += 0.01;
				}
				if (eval < 0)
					eval = 0;
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
