package gui_interface;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import agents.psi23_MainAg;

/**
 * 
 * @author Borja
 * @version 1.2
 */
public class psi23_GUI extends JFrame implements ActionListener, ItemListener, WindowListener {

	private psi23_MainAg mainAgent;

	private JButton buttonNuevo, buttonStop, buttonContinuar;

	public psi23_GUI(psi23_MainAg a) {
		this.mainAgent = a;

		Container cp = getContentPane();
		cp.setLayout(new GridBagLayout());
		setTitle("psi23_GUI");
		setSize(800, 500);
		setVisible(true);
		setResizable(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.buttonNuevo = new JButton("Nuevo");
		this.buttonNuevo.addActionListener(this);
		this.buttonStop = new JButton("Stop");
		this.buttonStop.addActionListener(this);
		this.buttonContinuar = new JButton("Continuar");
		this.buttonContinuar.addActionListener(this);

		this.init();
		this.pack();
	}

	private void init() {
		this.drawPanel();
	}

	private void drawPanel() {
		GridBagConstraints constraints = new GridBagConstraints();

		this.getContentPane().add(this.buttonNuevo);
		this.getContentPane().add(this.buttonStop);
		this.getContentPane().add(this.buttonContinuar);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		mainAgent.doDelete();
		dispose();
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "Nuevo":
			System.out.println("------------------------------------------------------");
			mainAgent.setStop_gameBehaviour(false);
			mainAgent.setFinish_game(false);
			mainAgent.removeGameBehaviour();
			mainAgent.initGameBehaviour();
			break;
		case "Stop":
			mainAgent.setStop_gameBehaviour(true);
			break;
		case "Continuar":
			mainAgent.setStop_gameBehaviour(false);
			break;
		default:
			break;
		}
	}

}
