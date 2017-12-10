import java.io.*;
import java.nio.charset.*;
import java.util.*;
import javax.swing.tree.*;

/**
 * A model to store raw data for the Mancala game
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaModel {

	// Instance variables
	private MancalaGameState state;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel gameTree; // Game tree that stores all possible moves.
	private int gameCounter;
	private PrintWriter out;

	// Constructor
	public MancalaModel() {
		state = new MancalaGameState();
		root = new DefaultMutableTreeNode(state);
		gameTree = new DefaultTreeModel(root);
		gameCounter = 0;

		out = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(FileDescriptor.out), StandardCharsets.UTF_8), 512));

	}

	// Getters and Setters

	public MancalaGameState getState() {
		return state;
	}

	public void setState(MancalaGameState state) {
		this.state = state;
	}

	public MancalaGameState getPreviousState() {
		return state.getPreviousState();
	}

	// /**
	// * Build a game tree of all possible Mancala moves (this algorithm will take
	// * forever and eat up all your PC memory, don't use it)
	// *
	// * @param root
	// * the root node to grow a game tree.
	// */
	// public void dfs(DefaultMutableTreeNode root) {
	//
	// Stack<DefaultMutableTreeNode> stack = new Stack<>();
	// stack.push(root);
	//
	// int gameNumber = 1;
	// System.out.printf("running game %d... %n", gameNumber);
	//
	// while (!stack.isEmpty()) {
	//
	// DefaultMutableTreeNode parent = stack.pop();
	// MancalaGameState parentState = new MancalaGameState((MancalaGameState)
	// parent.getUserObject());
	// int playerAScore = parentState.getPitMap().get(Pit.MANCALA_A).size();
	// int playerBScore = parentState.getPitMap().get(Pit.MANCALA_B).size();
	// int totalNumberOfStones = parentState.getInitStones() * 12;
	//
	// // If game is over print out all the moves from the beginning and who won
	// if (parentState.isGameOver() || playerAScore > totalNumberOfStones / 2
	// || playerBScore > totalNumberOfStones / 2) {
	//
	// Object[] path = parent.getUserObjectPath();
	//
	// for (int i = 1; i < path.length; i++) {
	// System.out.printf("Turn %d: Player %s picked pit %s.%n%n", i,
	// ((MancalaGameState) path[i - 1]).getPlayerTurn(),
	// ((MancalaGameState) path[i]).getSowedPit());
	// System.out.println(path[i]);
	// }
	//
	// if (playerAScore > playerBScore)
	// System.out.println("Player A won.");
	//
	// else if (playerAScore < playerBScore)
	// System.out.println("Player B won.");
	//
	// else
	// System.out.println("Game ended in draw.");
	//
	// System.out.printf("%nRunning game %d...%n%n", ++gameNumber);
	//
	// continue;
	// }
	//
	// Object[] moves = parentState.getSowablePits().toArray();
	//
	// for (int index = 0; index < moves.length; index++) {
	// MancalaGameState childState = new MancalaGameState(parentState);
	// childState.sow((Pit) moves[moves.length - index - 1]);
	// childState.checkIfGameEnded();
	// DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(childState);
	// gameTree.insertNodeInto(newChild, parent, index);
	// stack.push(newChild);
	// }
	//
	// }
	//
	// }

	// /**
	// * Write the game tree to file by serialization
	// *
	// * @param filename
	// * the name of the file to write the game tree to
	// */
	// public void saveGameTree(String filename) {
	// try {
	// FileOutputStream file = new FileOutputStream(filename);
	// ObjectOutputStream output = new ObjectOutputStream(file);
	// output.writeObject(filename);
	// output.close();
	// file.close();
	// } catch (IOException e) {
	// System.out.println("Unable to write to " + filename);
	// e.printStackTrace();
	// }
	// }

	/**
	 * Minimax algorithm to calculate the best move
	 * 
	 * @param depth
	 *            how much moves the AI can look ahead. Searching time increases
	 *            exponentially as depth increases.
	 * @return the best move after looking ahead the number of moves (depth)
	 */
	public Pit minimax(int depth) {

		// Check if game has ended
		if (state.isGameOver())
			return null;

		// Current player is the maximizer
		Player maximizer = state.getPlayerTurn();

		Pair<Pit, Integer> bestMove = new Pair<>(null, Integer.MIN_VALUE);
		Pair<Pit, Integer> move;
		int totalGames = 0;

		// Go through sowable pits
		out.printf("%nIt's Player %s's turn.%n", state.getPlayerTurn());
		for (Pit pit : state.getSowablePits()) {
			out.printf("Analyzing first move of %s...", pit);
			out.flush();
			move = minimax(new MancalaGameState(state), pit, maximizer, depth);
			if (move.y > bestMove.y)
				bestMove = new Pair<>(pit, move.y);
			out.printf("done. (%d games searched)%n", gameCounter);
			out.flush();
			totalGames += gameCounter;
			gameCounter = 0;
		}
		out.printf("Total games analyzed: %d%n", totalGames);
		out.flush();

		return bestMove.x;

	}

	/**
	 * Recursive minimax algorithm to calculate the best move.
	 * 
	 * @param state
	 *            the state to calculate the best move from.
	 * @param pitMove
	 *            the pit to be sowed from
	 * @param maximizer
	 *            the maximizing player.
	 * @param depth
	 *            how many moves left to look ahead at this current state.
	 * @return the best move depending on the current score difference between
	 *         current player and opponent.
	 */
	public Pair<Pit, Integer> minimax(MancalaGameState state, Pit pitMove, Player maximizer, int depth) {

		// Make the move
		state.sow(pitMove);

		// Get necessary values
		int playerAScore = state.getPitMap().get(Pit.MANCALA_A).size();
		int playerBScore = state.getPitMap().get(Pit.MANCALA_B).size();
		int totalNumberOfStones = state.getInitStones() * 12;

		// Terminating conditions
		if (depth == 0 || state.checkIfGameEnded() || playerAScore > totalNumberOfStones / 2
				|| playerBScore > totalNumberOfStones / 2) {

			gameCounter++;
			int score = playerAScore - playerBScore;
			return new Pair<>(pitMove, maximizer == Player.A ? score : -score);
		}

		Pair<Pit, Integer> bestMove;
		Pair<Pit, Integer> move;

		// If current player is maximizer, get max score
		if (state.getPlayerTurn() == maximizer) {

			bestMove = new Pair<>(null, Integer.MIN_VALUE);

			// Go through sowable pits
			for (Pit pit : state.getSowablePits()) {
				move = minimax(new MancalaGameState(state), pit, maximizer, depth - 1);
				if (move.y > bestMove.y)
					bestMove = new Pair<>(pit, move.y);
			}

			return bestMove;

		}

		// Else get min score
		else {

			bestMove = new Pair<>(null, Integer.MAX_VALUE);

			// Go through sowable pits
			for (Pit pit : state.getSowablePits()) {
				move = minimax(new MancalaGameState(state), pit, maximizer, depth - 1);
				if (move.y < bestMove.y)
					bestMove = new Pair<>(pit, move.y);
			}

			return bestMove;

		}
	}

	// /**
	// * Tests the AI.
	// *
	// * @param args
	// */
	// public static void main(String[] args) {
	//
	// MancalaModel model = new MancalaModel();
	// model.state.populatePits(3);
	// // model.dfs(model.root);
	// // model.saveGameTree("gametree.ser");
	// System.out.printf(String.format("Best move for current player is: %s",
	// model.minimax(8)));
	// }

}
