

public class psi23_Player {

	private String name;
	private int id;
	private int games_win;
	private int games_lost;
	private boolean inGame;
	private int position;
	private int mycoins;
	private int mybet;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the games_win
	 */
	public int getGames_win() {
		return games_win;
	}

	/**
	 * @param games_win
	 *            the games_win to set
	 */
	public void setGames_win(int games_win) {
		this.games_win = games_win;
	}

	/**
	 * @return the games_lost
	 */
	public int getGames_lost() {
		return games_lost;
	}

	/**
	 * @param games_lost
	 *            the games_lost to set
	 */
	public void setGames_lost(int games_lost) {
		this.games_lost = games_lost;
	}

	/**
	 * @return the inGame
	 */
	public boolean isInGame() {
		return inGame;
	}

	/**
	 * @param inGame
	 *            the inGame to set
	 */
	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return the mycoins
	 */
	public int getMycoins() {
		return mycoins;
	}

	/**
	 * @param mycoins
	 *            the mycoins to set
	 */
	public void setMycoins(int mycoins) {
		this.mycoins = mycoins;
	}

	/**
	 * @return the mybet
	 */
	public int getMybet() {
		return mybet;
	}

	/**
	 * @param mybet
	 *            the mybet to set
	 */
	public void setMybet(int mybet) {
		this.mybet = mybet;
	}

	public String toString() {
		return this.name + "\t" + this.id + "\t" + this.position + "\t" + this.games_win + "\t" + this.games_lost;
	}

}
