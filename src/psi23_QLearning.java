import java.util.ArrayList;
import java.util.Vector;

public class psi23_QLearning {
	final double dDecFactorLR = 1.0; // Value that will decrement the learning rate in each generation
	final double epsilon = 0.01; // Used to avoid selecting always the best action
	final double dMINLearnRate = 0.4; // We keep learning, after convergence, during 5% of times

	boolean bAllActions = false; // At the beginning we did not try all actions
	int iNewAction2Play; // This is the new action to be played
	int iLastAction; // The last action that has been played by this player
	psi23_StateAction oPresentStateAction; // Contains the present state we are and the actions that are available
	Vector<psi23_StateAction> oVStateActions = new Vector<psi23_StateAction>(); // A vector containing strings with the
																				// possible States and Actions available
																				// at each one

	double[] dProbAction;
	int iNewAction;
	psi23_StateAction oLastStateAction;
	// double dLastFunEval;
	double dLastFunEval;
	double dLearnRate;
	int iAction;
	double dGamma = 1.0;
	String lastState;
	double dQmax;

	public void vGetNewActionQLearning(String sState, int iNActions, double dFunEval) {

		boolean bFound;
		int iBest = -1, iNumBest = 1;
		double dR;
		psi23_StateAction oStateAction;

		bFound = false; // Searching if we already have the state
		for (int i = 0; i < oVStateActions.size(); i++) {
			oStateAction = (psi23_StateAction) oVStateActions.elementAt(i);
			if (oStateAction.sState.equals(sState)) {
				oPresentStateAction = oStateAction;
				bFound = true;
				break;
			}
		}
		// If we didn't find it, then we add it
		if (!bFound) {
			oPresentStateAction = new psi23_StateAction(sState, iNActions);
			oVStateActions.add(oPresentStateAction);
		}

		dQmax = 0;
		for (int i = 0; i < iNActions; i++) { // Determining the action to get Qmax{a'}
			if (oPresentStateAction.dValAction[i] > dQmax) {
				iBest = i;
				iNumBest = 1; // Reseting the number of best actions
				dQmax = oPresentStateAction.dValAction[i];
			} else if ((oPresentStateAction.dValAction[i] == dQmax) && (dQmax > 0)) { // If there is another one equal
																						// we must select one of them
																						// randomly
				iNumBest++;
				if (Math.random() < 1.0 / (double) iNumBest) { // Choose randomly with reducing probabilities
					iBest = i;
					dQmax = oPresentStateAction.dValAction[i];
				}
			}
		}
		// Adjusting Q(s,a)
		if (oLastStateAction != null) {
			dR = dFunEval - dLastFunEval; // Note that dR is also used as reward in the QL formulae
			if (dR > 0) { // If reward grows and the previous action was allowed --> reinforce the
							// previous action considering present values

				oLastStateAction.dValAction[iAction] += dLearnRate
						* (dR + dGamma * dQmax - oLastStateAction.dValAction[iAction]);
			}
		}

		if ((iBest > -1)) { 
			iNewAction = iBest;
		} else
			do {
				iNewAction = (int) (Math.random() * (double) iNActions);
			} while (iNewAction == iBest);

		iAction = iNewAction;
		lastState = sState;

		oLastStateAction = oPresentStateAction; // Updating values for the next time
		dLastFunEval = dFunEval;
		dLearnRate *= dDecFactorLR; // Reducing the learning rate
		if (dLearnRate < dMINLearnRate)
			dLearnRate = dMINLearnRate;
	}

	public void vGetNewActionQLearning_it(String sState, int iNActions, double dFunEval, ArrayList<String> list_bets) {

		boolean bFound;
		int iBest = -1, iNumBest = 1;
		double dR;
		psi23_StateAction oStateAction;

		bFound = false; // Searching if we already have the state
		for (int i = 0; i < oVStateActions.size(); i++) {
			oStateAction = (psi23_StateAction) oVStateActions.elementAt(i);
			if (oStateAction.sState.equals(sState)) {
				oPresentStateAction = oStateAction;
				bFound = true;
				break;
			}
		}
		// If we didn't find it, then we add it
		if (!bFound) {
			oPresentStateAction = new psi23_StateAction(sState, iNActions);
			oVStateActions.add(oPresentStateAction);
		}

		dQmax = 0;
		for (int i = 0; i < iNActions; i++) { // Determining the action to get Qmax{a'}
			if (!list_bets.contains(String.valueOf(i))) {
				if (oPresentStateAction.dValAction[i] > dQmax) {
					iBest = i;
					iNumBest = 1; // Reseting the number of best actions
					dQmax = oPresentStateAction.dValAction[i];
				} else if ((oPresentStateAction.dValAction[i] == dQmax) && (dQmax > 0)) { // If there is another one
																							// equal
																							// we must select one of
																							// them
																							// randomly
					iNumBest++;
					if (Math.random() < 1.0 / (double) iNumBest) { // Choose randomly with reducing probabilities
						iBest = i;
						dQmax = oPresentStateAction.dValAction[i];
					}
				}
			}
		}
		// Adjusting Q(s,a)
		if (oLastStateAction != null) {
			dR = dFunEval - dLastFunEval; // Note that dR is also used as reward in the QL formulae
			if (dR > 0) { // If reward grows and the previous action was allowed --> reinforce the
							// previous action considering present values

				oLastStateAction.dValAction[iAction] += dLearnRate
						* (dR + dGamma * dQmax - oLastStateAction.dValAction[iAction]);
			}
		}

		if ((iBest > -1)) { 
			iNewAction = iBest;
		} else
			do {
				iNewAction = (int) (Math.random() * (double) iNActions);
			} while (iNewAction == iBest);

		iAction = iNewAction;
		lastState = sState;

		oLastStateAction = oPresentStateAction; // Updating values for the next time
		dLastFunEval = dFunEval;
		dLearnRate *= dDecFactorLR; // Reducing the learning rate
		if (dLearnRate < dMINLearnRate)
			dLearnRate = dMINLearnRate;
	}

	public void Refuerzo(int parseInt, double eval, String estado) {
		double dR = 0d;
		boolean bFound = false; // Searching if we already have the state
		psi23_StateAction oStateAction, oStateAction_temp = null;

		for (int i = 0; i < oVStateActions.size(); i++) {
			oStateAction = (psi23_StateAction) oVStateActions.elementAt(i);
			if (oStateAction.sState.equals(estado)) {
				oStateAction_temp = oStateAction;
				bFound = true;
				break;
			}
		}

		if (bFound) {
			dR = eval - dLastFunEval; // Note that dR is also used as reward in the QL formulae
			if (dR > 0) {
				// System.out.println("\nHOLA\n");
				oStateAction_temp.dValAction[parseInt] += dLearnRate
						* (dR + dGamma * dQmax - oLastStateAction.dValAction[iAction]);
			}
		}

	}
}
