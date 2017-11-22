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
	private Queue<Pair> stoneSequence; // For animating one stone at a time
	private Set<Stack<Pair>> solutions;

	/**
	 * Constructor
	 */
	public MancalaModel() {

		// pits = new int[14];
		pits = new ArrayList<>();
		for (int i = 0; i < 14; i++)
			pits.add(new LinkedList<>());
		playerTurn = 'A';
		stoneSequence = new LinkedList<>();
		solutions = new HashSet<>();

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

	public char getPlayerTurn() {
		return playerTurn;
	}

	public Queue<Pair> getStoneSequence() {
		return stoneSequence;
	}

	/**
	 * Empty all stones in each pit.
	 */
	public void emptyPits() {

		for (int i = 0; i < 14; i++)
			// pits[i] = 0;
			pits.get(i).clear();
		stoneSequence.clear();

	}

	/**
	 * Add initial number of stones to each small pits
	 */
	public void populatePits(int initStones) {

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
				if (currentPitNo == pitNoMancalaOppositingPlayer) {
					stones++;
					continue;
				}

				// Add a stone in the current pit
				this.stones[pits.get(pitNo).getFirst()] = currentPitNo;
				stoneSequence.add(new Pair(pits.get(pitNo).getFirst(), currentPitNo));
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
	 * Use depth-first search to see if it is possible to end a game with one
	 * Mancala containing all stones.
	 * 
	 * @param initStones
	 */
	public void dfs(int initStones) {
		Stack<Pair> stack = new Stack<Pair>();
		Stack<Pair> sequence = new Stack<Pair>();
		Pair current;
		boolean gameEnded;

		for (int i = 0; i < 6; i++) {
			emptyPits();
			populatePits(initStones);
			playerTurn = 'A';
			stack.add(new Pair(0, i));
			sequence.add(new Pair(0, i));
			while (!stack.isEmpty()) {

				current = stack.pop();
				while (!sequence.isEmpty() && sequence.peek().left >= current.left)
					sequence.pop();
				sequence.push(current);

				emptyPits();
				populatePits(initStones);
				playerTurn = 'A';
				for (Pair pitNo : sequence)
					sow(pitNo.right);

				gameEnded = checkIfGameEnded();
				if (gameEnded && (pits.get(6).isEmpty() || pits.get(13).isEmpty())) {
					solutions.add((Stack<Pair>) sequence.clone());
					// for (Pair pitNo : sequence)
					// System.out.printf("%d:%d ", pitNo.left, pitNo.right);
					// System.out.println();
				}

				if (gameEnded || !pits.get(6).isEmpty() && !pits.get(13).isEmpty()) {
					for (Pair pitNo : sequence)
						System.out.printf("%d:%d ", pitNo.left, pitNo.right);
					System.out.println();
				}

				if (!gameEnded && (pits.get(6).isEmpty() || pits.get(13).isEmpty()))
					stack.addAll(getSowablePits(current.left + 1));

			}

		}
	}

	/**
	 * Get a list of sowable pits for current player.
	 * 
	 * @return the list of sowable pits.
	 */
	public LinkedList<Pair> getSowablePits(int turn) {
		LinkedList<Pair> queue = new LinkedList<>();
		switch (playerTurn) {
		case ('A'):
			for (int i = 5; i >= 0; i--)
				if (!pits.get(i).isEmpty())
					queue.add(new Pair(turn, i));
			return queue;
		case ('B'):
			for (int i = 12; i >= 7; i--)
				if (!pits.get(i).isEmpty())
					queue.add(new Pair(turn, i));
			return queue;
		}
		return null;
	}

	/**
	 * Check if the game has ended by checking to see if one side has no more
	 * stones. If so, place all the remaining stones from the other side to its
	 * Mancala.
	 * 
	 * @return whether if game has ended or not
	 */
	public boolean checkIfGameEnded() {

		// Check if side A is empty
		boolean isSideAEmpty = true;
		for (int i = 0; i < 6; i++) {
			if (!pits.get(i).isEmpty()) {
				isSideAEmpty = false;
				break;
			}
		}
		// Check if side B is empty
		boolean isSideBEmpty = true;
		for (int i = 7; i < 13; i++) {
			if (!pits.get(i).isEmpty()) {
				isSideBEmpty = false;
				break;
			}
		}

		// If side A is empty, place remaining stones to Mancala B
		if (!isSideAEmpty && isSideBEmpty) {
			for (int i = 0; i < 6; i++) {
				pits.get(6).addAll(pits.get(i));
				pits.get(i).clear();
			}
			return true;
		}

		// If side B is empty, place remaining stones to Mancala A
		if (isSideAEmpty && !isSideBEmpty) {
			for (int i = 7; i < 13; i++) {
				pits.get(13).addAll(pits.get(i));
				pits.get(i).clear();
			}
			return true;
		}

		if (isSideAEmpty && isSideBEmpty)
			return true;

		return false;

	}

	/**
	 * Produces a String output of the current numbers of stones in each pit
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("   ");
		for (int i = 12; i > 6; i--)
			sb.append(String.format("%3d", pits.get(i).size()));
		sb.append('\n');
		sb.append(String.format("%3d", pits.get(13).size()));
		sb.append(String.format("%0" + 18 + "d", 0).replace("0", " "));
		sb.append(String.format("%3d", pits.get(6).size()));
		sb.append("\n   ");
		for (int i = 0; i < 6; i++)
			sb.append(String.format("%3d", pits.get(i).size()));
		sb.append("\n");
//		sb.append(String.format("\nPlayer's turn: %c\n", playerTurn));
		return sb.toString();
	}

	/**
	 * Define a 2-tuple (pair) to store two pieces of data.
	 * 
	 * @author Vincent Stowbunenko
	 *
	 */
	public class Pair {

		private final int left;
		private final int right;

		public Pair(int left, int right) {
			this.left = left;
			this.right = right;
		}

		public int getLeft() {
			return left;
		}

		public int getRight() {
			return right;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Pair))
				return false;
			Pair pair = (Pair) obj;
			return this.left == pair.getLeft() && this.right == pair.getRight();
		}

	}

//	public static void main(String[] args) {
//		MancalaModel model = new MancalaModel();
//		model.dfs(3);
//		System.out.println("List of solutions:");
//		for (Stack<Pair> sequence : model.solutions) {
//			for (Pair pitNo : sequence)
//				System.out.printf("%d:%d ", pitNo.left, pitNo.right);
//		System.out.println();
//		}
//	}

}
