import java.util.*;

/**
 * Stores the current state of the Mancala game.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaGameState {

	// Instance variables
//	private Pit sowedPit;
	private EnumMap<Pit, LinkedList<Stone>> pitMap;
	private Player playerTurn;
	private boolean isGameOver;
	private MancalaGameState previousState;
	private Map<Stone, Pit> stoneMap;
	private Queue<Tuple<Stone>> stoneSequence;
	private int initStones;
	private int finiteState; // whether to save previous state or not

	// Constructor
	public MancalaGameState() {
//		sowedPit = null;
		pitMap = new EnumMap<>(Pit.class);
		for (Pit pit : Pit.values())
			pitMap.put(pit, new LinkedList<>());
		playerTurn = Player.A;
		isGameOver = false;
		previousState = null;
		stoneMap = new HashMap<>();
		stoneSequence = new LinkedList<>();
		finiteState = 0;
	}

	// Copy-constructor
	public MancalaGameState(MancalaGameState state) {
//		sowedPit = state.sowedPit;
		pitMap = new EnumMap<>(Pit.class);
		stoneMap = new HashMap<>(state.stoneMap);
		for (Pit pit : Pit.values()) {
			pitMap.put(pit, new LinkedList<>());
			for (Stone stone : state.pitMap.get(pit))
				pitMap.get(pit).offer(stone);
		}
		playerTurn = state.playerTurn;
		isGameOver = state.isGameOver;
		if (state.previousState != null)
			previousState = new MancalaGameState(state.previousState);
		stoneSequence = new LinkedList<>(state.stoneSequence);
		initStones = state.initStones;
		finiteState = 0;
	}

	// Getters and setters

	public boolean isGameOver() {
		return isGameOver;
	}

	// public Pit getSowedPit() {
	// return sowedPit;
	// }

	public EnumMap<Pit, LinkedList<Stone>> getPitMap() {
		return pitMap;
	}

	public Player getPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(Player playerTurn) {
		this.playerTurn = playerTurn;
	}

	public Map<Stone, Pit> getStoneMap() {
		return stoneMap;
	}

	public Queue<Tuple<Stone>> getStoneSequence() {
		return stoneSequence;
	}

	public MancalaGameState getPreviousState() {
		return previousState;
	}

	public int getInitStones() {
		return initStones;
	}

	/**
	 * Offer a stone to a pit.
	 * 
	 * @param stone
	 * @param pit
	 */
	public void offerStone(Stone stone, Pit pit) {
		stoneMap.put(stone, pit);
		pitMap.get(pit).offer(stone);
	}

	/**
	 * Offer a list of stones to a pit.
	 * 
	 * @param stoneList
	 * @param pit
	 */
	public void offerStones(LinkedList<Stone> stoneList, Pit pit) {
		for (Stone stone : stoneList)
			offerStone(stone, pit);
	}

	/**
	 * Remove a specified stone from a pit.
	 * 
	 * @param stone
	 * @param pit
	 * @return the stone that is being removed
	 */
	public Stone removeStone(Stone stone, Pit pit) {
		stoneMap.put(stone, null);
		pitMap.get(pit).remove(stone);
		return stone;
	}

	/**
	 * Remove a stone from a pit.
	 * 
	 * @param pit
	 * @return the stone that was removed from the pit.
	 */
	public Stone pollStone(Pit pit) {
		Stone stone = pitMap.get(pit).poll();
		// stone.setPit(null);
		// searchTuple(stone).setPit(null);
		stoneMap.put(stone, null);
		return stone;
	}

	/**
	 * Empty all stones from a pit.
	 * 
	 * @param pit
	 * @return the list of stones that was emptied from the pit.
	 */
	public LinkedList<Stone> emptyPit(Pit pit) {
		LinkedList<Stone> stoneList = new LinkedList<>();
		while (!pitMap.get(pit).isEmpty())
			stoneList.offer(pollStone(pit));
		return stoneList;
	}

	/**
	 * Empty all stones from all pits.
	 */
	public void emptyAllPits() {
		for (Pit pit : pitMap.keySet())
			emptyPit(pit);
		// stoneList.clear();
		// tupleList.clear();
		stoneMap.clear();
	}

	/**
	 * Add initial number of stones to each small pits
	 * 
	 * @param initStones
	 */
	public void populatePits(int initStones) {
		this.initStones = initStones;
		for (Pit pit : Pit.smallPits)
			for (int i = 0; i < initStones; i++) {
				Stone stone = new Stone();
				// offerStone(stone, pit);
				// stoneList.add(stone);
				// tupleList.add(new Tuple<>(stone, pit));
				stoneMap.put(stone, pit);
				pitMap.get(pit).offer(stone);
			}
	}

	/**
	 * Get the opposite pit from the specified pit.
	 * 
	 * @param pit
	 * @return the opposite pit
	 */
	public static Pit getOppositePit(Pit pit) {

		// Test if it is null
		if (pit == null)
			return null;

		// If the pit is a mancala
		if (Pit.mancalas.contains(pit))
			return (pit.equals(Pit.MANCALA_A)) ? Pit.MANCALA_B : Pit.MANCALA_A;

		// Else return the opposite pit of the small pit
		return (Pit.sideAPits.contains(pit)) ? (Pit) Pit.sideBPits.toArray()[5 - pit.ordinal()]
				: (Pit) Pit.sideAPits.toArray()[12 - pit.ordinal()];

	}

	/**
	 * Switch player turn.
	 */
	public void changePlayer() {
		switch (playerTurn) {
		case A:
			playerTurn = Player.B;
			return;
		case B:
			playerTurn = Player.A;
			return;
		}
	}

	/**
	 * Get a list of sowable pits for current player.
	 * 
	 * @return the list of sowable pits.
	 */
	public EnumSet<Pit> getSowablePits() {
		EnumSet<Pit> pitSet = EnumSet.noneOf(Pit.class);
		switch (playerTurn) {
		case A:
			for (Pit pit : Pit.sideAPits)
				if (!pitMap.get(pit).isEmpty())
					pitSet.add(pit);
			break;
		case B:
			for (Pit pit : Pit.sideBPits)
				if (!pitMap.get(pit).isEmpty())
					pitSet.add(pit);
		}
		return pitSet;
	}

	/**
	 * Sow the stones around the board.
	 * 
	 * @param pit
	 *            the pit containing the stones to sow.
	 * @param isHuman
	 *            whether the current player is human or computer.
	 * @return boolean values whether the stones were sowed successfully or not.
	 */
	// public boolean sow(Pit pit, boolean isHuman) {
	public boolean sow(Pit pit) {

		// // Check if the pit is sowable.
		// if (!getSowablePits().contains(pit))
		// return false;

		// // Update the state.
		// sowedPit = pit;

		// Save the state as the previous state before modifying it.
		// shouldISaveGameState(isHuman);

		// Check for player's turn to run proper subroutine.
		switch (playerTurn) {

		case A:
			sowSubroutine(pit, Pit.MANCALA_A);
			return true;

		case B:
			sowSubroutine(pit, Pit.MANCALA_B);
			return true;

		}

		return false;

	}

	/**
	 * To allow sowing on specific pits for current player.
	 * 
	 * @param pit
	 * @param mancalaCurrentPlayer
	 */
	public void sowSubroutine(Pit pit, Pit mancalaCurrentPlayer) {

		// Keep count of stones left to sow
		int stonesLeft = pitMap.get(pit).size();

		// Get an iterator for pits that the player can sow in
		EnumSet<Pit> pitSet = EnumSet.complementOf(EnumSet.of(getOppositePit(mancalaCurrentPlayer)));
		Iterator<Pit> pitIterator = pitSet.iterator();

		// Get the iterator to the next pit that the first stone will be sown in
		while (!pitIterator.next().equals(pit))
			;

		// Sow the stone to the right pits
		Pit pitCurrent;
		while (!pitMap.get(pit).isEmpty()) {

			// Rotate iterator to beginning of pit set if it has reached to the end
			if (!pitIterator.hasNext())
				pitIterator = pitSet.iterator();

			// Get the current pit
			pitCurrent = (Pit) pitIterator.next();

			// Add a stone in the current pit
			Stone stoneCurrent = pollStone(pit);
			offerStone(stoneCurrent, pitCurrent);

			// For animating one stone at a time
			stoneSequence.offer(new Tuple<>(stoneCurrent, pitCurrent));

			// Check which pit the last stone is in
			if (--stonesLeft == 0) {

				// If the stone is in its Mancala current player gets other turn, otherwise,
				// switch player turn
				if (pitCurrent.equals(mancalaCurrentPlayer))
					return;

				// Check if the last stone is being placed on the empty pit ON player's side. If
				// so, get that last stone and all the stones in the opposite pit
				if (((playerTurn == Player.A && Pit.sideAPits.contains(pitCurrent))
						|| (playerTurn == Player.B && Pit.sideBPits.contains(pitCurrent)))
						&& pitMap.get(pitCurrent).size() == 1 && !pitMap.get(getOppositePit(pitCurrent)).isEmpty()) {
					offerStone(pollStone(pitCurrent), mancalaCurrentPlayer);
					offerStones(emptyPit(getOppositePit(pitCurrent)), mancalaCurrentPlayer);
				}

				changePlayer();

			}

		}

	}

	/**
	 * Uses Finite State Machine (FSM) to determine to whether to save previous
	 * state or not if an opponent is AI.
	 * 
	 * @param isHuman
	 *            whether the player is human or not.
	 * @return whether the state should be saved or not
	 */
	public boolean shouldISaveGameState(boolean isHuman) {
		switch (finiteState) {
		case (0):
			finiteState = (isHuman) ? 0 : 1;
			break;
		case (1):
			finiteState = (isHuman) ? 2 : 3;
			break;
		case (2):
			finiteState = (isHuman) ? 0 : 1;
			break;
		case (3):
			finiteState = (isHuman) ? 0 : 3;
			break;
		}
		if (finiteState == 0 || finiteState == 1) {
			previousState = new MancalaGameState(this);
			return true;
		}
		return false;
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
		for (Pit pit : Pit.sideAPits)
			if (!pitMap.get(pit).isEmpty()) {
				isSideAEmpty = false;
				break;
			}

		// Check if side B is empty
		boolean isSideBEmpty = true;
		for (Pit pit : Pit.sideBPits)
			if (!pitMap.get(pit).isEmpty()) {
				isSideBEmpty = false;
				break;
			}

		// If side B is empty, place remaining stones to Mancala A
		if (!isSideAEmpty && isSideBEmpty) {
			for (Pit pit : Pit.sideAPits)
				offerStones(emptyPit(pit), Pit.MANCALA_A);
			isGameOver = true;
			return true;
		}

		// If side A is empty, place remaining stones to Mancala B
		if (isSideAEmpty && !isSideBEmpty) {
			for (Pit pit : Pit.sideBPits)
				offerStones(emptyPit(pit), Pit.MANCALA_B);
			isGameOver = true;
			return true;
		}

		// If both sides are already empty, don't need to do anything and game is ended
		if (isSideAEmpty && isSideBEmpty) {
			isGameOver = true;
			return true;
		}

		// If anything fails... game isn't over yet
		return false;

	}

	/**
	 * Set up a new game.
	 * 
	 * @param initStones
	 *            initial stones per pit to start with
	 */
	public void setupGame(int initStones) {
		isGameOver = false;
		emptyAllPits();
		setPlayerTurn(Player.A);
		populatePits(initStones);
	}

	/**
	 * Produces a String output of the current numbers of stones in each pit
	 */
	@Override
	public String toString() {

		// Visual text representation of number of stones in each pit
		StringBuilder sb = new StringBuilder("   ");
		Object[] pits = Pit.sideBPits.toArray();
		for (int i = pits.length - 1; i >= 0; i--)
			sb.append(String.format("%3d", pitMap.get(pits[i]).size()));
		sb.append('\n');
		sb.append(String.format("%3d", pitMap.get(Pit.MANCALA_B).size()));
		sb.append(String.format("%0" + 18 + "d", 0).replace("0", " "));
		sb.append(String.format("%3d", pitMap.get(Pit.MANCALA_A).size()));
		sb.append("\n   ");
		for (Pit pit : Pit.sideAPits)
			sb.append(String.format("%3d", pitMap.get(pit).size()));
		sb.append("\n\n");
		// sb.append(String.format("Stone Sequence: %s%n%n", stoneSequence));
		return sb.toString();

		// // Print out pit graphics map info
		// StringBuilder strBldr = new StringBuilder(sb);
		// strBldr.append("pitMap:\n");
		// for (Pit pit : Pit.values()) {
		// strBldr.append(String.format(" %s:%n ", pit));
		// for (Stone stone : pitMap.get(pit))
		// strBldr.append(String.format("%x:%s ", stone.hashCode(), stone.getPit()));
		// strBldr.append("\n");
		// }
		//
		// // Print out stone graphics map info
		// strBldr.append("stoneList:\n");
		// for (Stone stone : stoneList) {
		// strBldr.append(String.format(" %x:%s%n", stone.hashCode(), stone.getPit()));
		// }
		//
		// return strBldr.toString();

	}

}
