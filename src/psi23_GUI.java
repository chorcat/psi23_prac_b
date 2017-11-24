

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

public class psi23_GUI extends JFrame implements ActionListener, ItemListener, WindowListener {

	private JMenuBar barraMenu;
	private JMenu menuFile, menuEdit, menuWindow, menuHelp;
	private JMenuItem itemNewGame, itemExit, itemRenamePlayer, itemResetPlayers, itemAddPlayer, itemRemovePlayer,
			itemNumberGames, itemAbout;
	
	private JCheckBoxMenuItem itemCommentsOnOff;
	
	private boolean check_comments = true;

	private TextArea areaInfo, areaPlayers, areaMatrix;
	private JButton buttonNew, buttonStop, butttonContinue;
	private JLabel labelNPlayers, labelNGames;
	
	private JTextArea listPlayers;
	private JScrollPane panel_listPlayers;
	
	private int total_number_of_games, number_of_games_played, number_of_players_left_in_game, number_set_games_played;

	private psi23_MainAg mainAgent;

	public psi23_GUI(psi23_MainAg main) {
		this.mainAgent = main;

		this.total_number_of_games = 0;
		this.number_of_games_played = 0;
		this.number_of_players_left_in_game = 0;
		this.number_set_games_played = 0;

		Container cp = getContentPane();
		cp.setLayout(new GridBagLayout());
		setTitle("psi23_GUI");
		setSize(800, 500);
		setVisible(true);
		setResizable(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.barraMenu = new JMenuBar();

		this.menuFile = new JMenu("File");
		this.menuEdit = new JMenu("Edit");
		this.menuWindow = new JMenu("Window");
		this.menuHelp = new JMenu("Help");

		this.itemNewGame = new JMenuItem("New Game");
		this.itemExit = new JMenuItem("Exit");
		this.itemRenamePlayer = new JMenuItem("Rename Player");
		//this.itemResetPlayers = new JMenuItem("Reset Players");
		//this.itemAddPlayer = new JMenuItem("Add Player");
		//this.itemRemovePlayer = new JMenuItem("Remove Player");
		this.itemNumberGames = new JMenuItem("Number of games");
		this.itemCommentsOnOff = new JCheckBoxMenuItem("Comments ON/OFF", true);
		this.itemCommentsOnOff.addItemListener(this);
		this.itemAbout = new JMenuItem("About");

		this.areaInfo = new TextArea("Area de Informacion");
		this.areaInfo.setEditable(false);
		//this.areaPlayers = new TextArea("Nombre Id G P Parcial Total");
		//this.areaPlayers.setEditable(false);
		this.listPlayers = new JTextArea("NAME\tID\tPOSITION\tGAMES_WIN\tGAMES_LOST");
		this.panel_listPlayers = new JScrollPane(listPlayers);
		this.listPlayers.setEditable(false);

		this.buttonNew = new JButton("New");
		this.buttonNew.addActionListener(this);
		this.buttonStop = new JButton("Stop");
		this.buttonStop.addActionListener(this);
		this.butttonContinue = new JButton("Continue");
		this.butttonContinue.addActionListener(this);
		this.labelNPlayers = new JLabel("NPlayers In Game= " + number_of_players_left_in_game, JLabel.CENTER);
		this.labelNGames = new JLabel("NGames Played= " + number_of_games_played, JLabel.CENTER);

		this.init();
		this.pack();
	}

	public void init() {

		drawMenuBar();
		drawPanel();

	}

	private void drawMenuBar() {
		this.barraMenu.add(this.menuFile);
		this.itemNewGame.addActionListener(this);
		this.menuFile.add(this.itemNewGame);
		this.itemExit.addActionListener(this);
		this.menuFile.add(this.itemExit);

		this.barraMenu.add(menuEdit);
		//this.itemRenamePlayer.addActionListener(this);
		//this.menuEdit.add(this.itemRenamePlayer);
		//this.menuEdit.add(this.itemResetPlayers);
		//this.menuEdit.add(this.itemAddPlayer);
		//this.menuEdit.add(this.itemRemovePlayer);
		this.itemNumberGames.addActionListener(this);
		this.menuEdit.add(this.itemNumberGames);

		this.barraMenu.add(menuWindow);
		this.menuWindow.add(this.itemCommentsOnOff);

		this.barraMenu.add(menuHelp);
		this.itemAbout.addActionListener(this);
		this.menuHelp.add(this.itemAbout);

		this.setJMenuBar(this.barraMenu);
	}

	private void drawPanel() {
		GridBagConstraints constraints = new GridBagConstraints();

		// Posicion TextArea de Area de Informacion
		constraints.gridx = 6;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 3;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		this.getContentPane().add(this.areaInfo, constraints);
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;

		// Posicion TextArea de Area de Jugadores
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 6;
		constraints.gridheight = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		this.getContentPane().add(this.panel_listPlayers, constraints);
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;

//		// Posicion TextArea de Matriz de Pagos
//		constraints.gridx = 0;
//		constraints.gridy = 2;
//		constraints.gridwidth = 6;
//		constraints.gridheight = 1;
//		constraints.fill = GridBagConstraints.BOTH;
//		constraints.insets = new Insets(2, 2, 2, 2);
//		constraints.weightx = 1.0;
//		constraints.weighty = 1.0;
//		this.getContentPane().add(this.areaMatrix, constraints);
//		constraints.weightx = 0.0;
//		constraints.weighty = 0.0;

		// Posicion Boton New Game
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = new Insets(1, 1, 1, 1);
		this.getContentPane().add(this.buttonNew, constraints);

		// Posicion Boton Stop Game
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = new Insets(1, 1, 1, 1);
		this.getContentPane().add(this.buttonStop, constraints);

		// Posicion Boton Continue Game
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = new Insets(1, 1, 1, 1);
		this.getContentPane().add(this.butttonContinue, constraints);

		// Posicion Boton Number of Players
		constraints.gridx = 3;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(1, 20, 1, 20);
		this.getContentPane().add(this.labelNPlayers, constraints);

		// Posicion Boton Number of Games
		constraints.gridx = 4;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(1, 0, 1, 20);
		this.getContentPane().add(this.labelNGames, constraints);

	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		mainAgent.doDelete();
		dispose();
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if(arg0.paramString().indexOf("Comments ON/OFF") > 0)
			check_comments = !check_comments;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "New":
		case "New Game":
			System.out.println("----------------------- NUEVA PARTIDA -------------------------------");
			mainAgent.setStop_gameBehaviour(false);
			mainAgent.setFinish_game(false);
			mainAgent.removeGameBehaviour();
			mainAgent.initGameBehaviour();
			break;
		case "Stop":
			mainAgent.setStop_gameBehaviour(true);
			break;
		case "Continue":
			mainAgent.setStop_gameBehaviour(false);
			break;
		case "About":
			String sAux[] = { "Game 1.1", "@ Borja Gonzalez Enriquez", "23/11/2017" };
			new MessageDialog(sAux);
			break;
		case "Number of games":
			InputDialog input = new InputDialog();
			mainAgent.setNumero_de_juegos_por_partida(Integer.parseInt(input.getRespuesta()));
			break;
		case "Rename Player":
			InputDialog input2 = new InputDialog();
			break;
		case "Exit":
			dispose();
			System.exit(0);
			break;
		}
	}
	
	public TextArea getAreaInfo() {
		return areaInfo;
	}

	public void setAreaInfo(TextArea areaInfo) {
		this.areaInfo = areaInfo;
	}

	class InputDialog extends JDialog implements ActionListener {

		private String respuesta;

		public InputDialog() {
			setRespuesta(JOptionPane.showInputDialog("Set the number of games:"));
			setVisible(true);

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if ("OK".equals(e.getActionCommand())) {
				dispose();
			} else if ("Cancel".equals(e.getActionCommand())) {
				setVisible(false);
				dispose();
			}
		}

		public String getRespuesta() {
			return respuesta;
		}

		public void setRespuesta(String respuesta) {
			this.respuesta = respuesta;
		}

	}

	class MessageDialog extends JDialog implements ActionListener {

		public MessageDialog(String cadena[]) {
			JOptionPane.showMessageDialog(null, cadena);
			setVisible(true);

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if ("OK".equals(e.getActionCommand())) {
				setVisible(false);
				dispose();
			}
		}

	}

	/**
	 * @return the labelNPlayers
	 */
	public JLabel getLabelNPlayers() {
		return labelNPlayers;
	}

	/**
	 * @param labelNPlayers the labelNPlayers to set
	 */
	public void setLabelNPlayers(JLabel labelNPlayers) {
		this.labelNPlayers = labelNPlayers;
	}

	/**
	 * @return the labelNGames
	 */
	public JLabel getLabelNGames() {
		return labelNGames;
	}

	/**
	 * @param labelNGames the labelNGames to set
	 */
	public void setLabelNGames(JLabel labelNGames) {
		this.labelNGames = labelNGames;
	}

	/**
	 * @return the listPlayers
	 */
	public JTextArea getListPlayers() {
		return listPlayers;
	}

	/**
	 * @param listPlayers the listPlayers to set
	 */
	public void setListPlayers(JTextArea listPlayers) {
		this.listPlayers = listPlayers;
	}

	/**
	 * @return the check_comments
	 */
	public boolean isCheck_comments() {
		return check_comments;
	}

	/**
	 * @param check_comments the check_comments to set
	 */
	public void setCheck_comments(boolean check_comments) {
		this.check_comments = check_comments;
	}

}
