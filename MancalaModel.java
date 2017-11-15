import java.util.*;

/**
 * A model to store raw data for the Mancala game
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaModel {

	// int[] pits;
	private ArrayList<LinkedList<Integer>> pits;
	private ArrayList<LinkedList<Integer>> pitsUndo;
	private int[] stones; // Each stone tells which pit they are in
	private char playerTurn;

	/**
	 * Constructor
	 */
	public MancalaModel() {

		// pits = new int[14];
		pits = new ArrayList<>();
		for (int i = 0; i < 14; i++)
			pits.add(new LinkedList<>());
		playerTurn = 'A';

	}

	// Getters and setters

	// public int[] getPits() {
	// return pits;
	// }

	public ArrayList<LinkedList<Integer>> getPits() {
		return pits;
	}

	public int[] getStones() {
		return stones;
	}

	public void setPlayerTurn(char playerTurn) {
		this.playerTurn = playerTurn;
	}

	/**
	 * Empty all stones in each pit.
	 */
	public void emptyPits() {

		for (int i = 0; i < 14; i++)
			// pits[i] = 0;
			pits.get(i).clear();

	}

	/**
	 * Add initial number of stones to each small pits
	 */
	public void populateStones(int initStones) {

		stones = new int[initStones * 12];
		int counter = 0;
		for (int i = 0; i < 13; i++) {
			if (i == 6)
				continue;
			// pits[i] = initStones;
			for (int j = 0; j < initStones; j++) {
				stones[counter] = i;
				pits.get(i).add(counter++);
			}
		}

	}

	/**
	 * Sow the stones around the board.
	 */
	public void sow(int pitNo) {

		// Can't sow on Mancala pits
		if (pitNo != 6 && pitNo != 13)

			switch (playerTurn) {

			// If its player A's turn
			case 'A':
				sowSubroutine(pitNo, 6, 13);
				break;

			// If it's player B's turn
			case 'B':
				sowSubroutine(pitNo, 13, 6);

			}

	}

	/**
	 * To allow sowing on specific pits for current player.
	 * 
	 * @param pitNoMancalaCurrentPlayer
	 * @param pitNoMancalaOppositingPlayer
	 */
	public void sowSubroutine(int pitNo, int pitNoMancalaCurrentPlayer, int pitNoMancalaOppositingPlayer) {

		// Player A only can click on pits 0 to 5
		if (pitNo >= pitNoMancalaCurrentPlayer - 6 && pitNo < pitNoMancalaCurrentPlayer) {

			// stones = pits[pitNo];
			int stones = pits.get(pitNo).size();

			// Sow one stone in each pit at correct pit sequence
			int i = 0;
			while (!pits.get(pitNo).isEmpty()) {

				// Get the current pit number
				int currentPitNo = (pitNo + ++i) % 14;

				// Make sure not to sow a stone in other player's Mancala
				if (currentPitNo == pitNoMancalaOppositingPlayer)
					continue;

				// Add a stone in the current pit
				this.stones[pits.get(pitNo).getFirst()] = currentPitNo;
				pits.get(currentPitNo).add(pits.get(pitNo).pop());

				// Check which pit the last stone is in
				if (i == stones) {

					// If the stone is in its Mancala current player gets other turn
					if (currentPitNo == pitNoMancalaCurrentPlayer)
						return;
					// Otherwise, switch player turn
					else 
						changePlayer();

					// Check if the last stone is being placed on the empty pit ON player's side. If
					// so, get your stone and all the stones on the other side to your Mancala
					if (pits.get(currentPitNo).size() == 1 && pits.get(12 - currentPitNo).size() != 0
							&& currentPitNo >= pitNoMancalaCurrentPlayer - 6
							&& currentPitNo < pitNoMancalaCurrentPlayer) {
						for (int j = 0; j < pits.get(12 - currentPitNo).size(); j++)
							this.stones[pits.get(12 - currentPitNo).get(j)] = pitNoMancalaCurrentPlayer;
						pits.get(pitNoMancalaCurrentPlayer).addAll(pits.get(12 - currentPitNo));
						pits.get(12 - currentPitNo).clear();
						this.stones[pits.get(currentPitNo).getFirst()] = pitNoMancalaCurrentPlayer;
						pits.get(pitNoMancalaCurrentPlayer).add(pits.get(currentPitNo).pop());
					}

				}

			}

		}

	}

	/**
	 * Switch player turn.
	 */
	public void changePlayer() {
		switch (playerTurn) {
		case ('A'):
			playerTurn = 'B';
			return;
		case ('B'):
			playerTurn = 'A';
			return;
		}
	}

	/**
	 * Produces a String output of the current numbers of stones in each pit
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("   ");
		for (int i = 12; i > 6; i--)
			// sb.append(String.format("%3d", pits[i]));
			sb.append(String.format("%3d", pits.get(i).size()));
		sb.append('\n');
		// sb.append(String.format("%3d", pits[13]));
		sb.append(String.format("%3d", pits.get(13).size()));
		sb.append(String.format("%0" + 18 + "d", 0).replace("0", " "));
		// sb.append(String.format("%3d", pits[6]));
		sb.append(String.format("%3d", pits.get(6).size()));
		sb.append("\n   ");
		for (int i = 0; i < 6; i++)
			// sb.append(String.format("%3d", pits[i]));
			sb.append(String.format("%3d", pits.get(i).size()));
		sb.append("\n\n");
		sb.append(String.format("Player's turn: %c\n", playerTurn));
		return sb.toString();
	}

	/**
	 * Test the methods in this class
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MancalaModel model = new MancalaModel();
		System.out.println(model.toString());
		model.populateStones(4);
		System.out.println(model.toString());
		model.sow(4);
		System.out.println(model.toString());
		model.sow(6);
		System.out.println(model.toString());
		model.sow(7);
		System.out.println(model.toString());
		model.sow(0);
		System.out.println(model.toString());
		model.sow(0);
		System.out.println(model.toString());
		model.sow(8);
		System.out.println(model.toString());
		model.sow(1);
		System.out.println(model.toString());
	}

}
