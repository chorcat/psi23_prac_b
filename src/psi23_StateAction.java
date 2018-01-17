
public class psi23_StateAction {
	String sState;
	double[] dValAction;

	psi23_StateAction (String sAuxState, int iNActions) {
	  sState = sAuxState;
	  dValAction = new double[iNActions];	   
	}

	public String sGetState() {
		return sState;
	}

	public double dGetQAction(int i) {
		return dValAction[i];
	}
	
	public String toString() {
		String retorno = sState + "\t";
		for(int i = 0; i < dValAction.length; i++) {
			retorno += dValAction[i] + "\t";
		}
		return retorno;
	}
}
