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

		int stones;

		// Can't sow on Mancala pits
		if (pitNo != 6 && pitNo != 13)
			switch (playerTurn) {

			// If its player A's turn
			case 'A':

				// Player A only can click on pits 0 to 5
				if (pitNo < 6) {

					// stones = pits[pitNo];
					stones = pits.get(pitNo).size();

					// Empty the sowed pit
					// pits[pitNo] = 0;

					// Sow one stone in each pit at correct pit sequence
					// for (int i = 1; i <= stones; i++) {
					int i = 0;
					while (!pits.get(pitNo).isEmpty()) {

						// Get the current pit number
						int currentPitNo = (pitNo + ++i) % 14;

						// Make sure not to sow a stone in other player's Mancala
						if (currentPitNo == 13)
							continue;

						// Add a stone in the current pit
						// pits[currentPitNo]++;
						this.stones[pits.get(pitNo).getFirst()] = currentPitNo;
						pits.get(currentPitNo).add(pits.get(pitNo).pop());

						// Check which pit the last stone is in
						if (i == stones) {

							// If the stone is NOT in its Mancala, switch player turn
							if (currentPitNo != 6) {
								changePlayer();
							}

							// Check if the last stone is being placed on the empty pit ON player's side. If
							// so, get your stone and all the stones on the other side to your Mancala
							// if (pits[currentPitNo] == 0 && currentPitNo < 6) {
							if (pits.get(currentPitNo).size() == 1 && currentPitNo < 6) {
								// pits[6] += pits[12 - currentPitNo] + 1;
								// pits[12 - currentPitNo] = 0;
								// pits[currentPitNo] = 0;
								for (int j = 0; j < pits.get(12 - currentPitNo).size(); j++)
									this.stones[pits.get(12 - currentPitNo).get(j)] = 6;
								pits.get(6).addAll(pits.get(12 - currentPitNo));
								pits.get(12 - currentPitNo).clear();
								this.stones[pits.get(currentPitNo).getFirst()] = 6;
								pits.get(6).add(pits.get(currentPitNo).pop());
							}

						}

					}

				}

				return;

			// If it's player B's turn
			case 'B':

				// Player A only can click on pits 7 to 12
				if (pitNo > 6) {

					// stones = pits[pitNo];
					stones = pits.get(pitNo).size();

					// Empty the sowed pit
					// pits[pitNo] = 0;

					// Sow one stone in each pit at correct pit sequence
					// for (int i = 1; i <= stones; i++) {
					int i = 0;
					while (!pits.get(pitNo).isEmpty()) {

						// Get the current pit number
						int currentPitNo = (pitNo + ++i) % 14;

						// Make sure not to sow a stone in other player's Mancala
						if (currentPitNo == 6)
							continue;

						// Add a stone in the current pit
						// pits[currentPitNo]++;
						this.stones[pits.get(pitNo).getFirst()] = currentPitNo;
						pits.get(currentPitNo).add(pits.get(pitNo).pop());

						// Check which pit the last stone is in
						if (i == stones) {

							// If the stone is NOT in its Mancala, switch player turn
							if (currentPitNo != 13) {
								changePlayer();
							}

							// Check if the last stone is being placed on the empty pit ON player's side. If
							// so, get your stone and all the stones on the other side to your Mancala
							// if (pits[currentPitNo] == 0 && currentPitNo > 6 && currentPitNo < 13) {
							if (pits.get(currentPitNo).size() == 1 && currentPitNo > 6 && currentPitNo < 13) {
								// pits[13] += pits[12 - currentPitNo];
								// pits[12 - currentPitNo] = 0;
								// pits[currentPitNo] = 0;
								for (int j = 0; j < pits.get(12 - currentPitNo).size(); j++)
									this.stones[pits.get(12 - currentPitNo).get(j)] = 13;
								pits.get(13).addAll(pits.get(12 - currentPitNo));
								pits.get(12 - currentPitNo).clear();
								this.stones[pits.get(currentPitNo).getFirst()] = 13;
								pits.get(13).add(pits.get(currentPitNo).pop());
							}

						}

					}

				}

				return;

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
