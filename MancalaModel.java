import java.io.*;
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

	// Constructor
	public MancalaModel() {
		state = new MancalaGameState();
		root = new DefaultMutableTreeNode(state);
		gameTree = new DefaultTreeModel(root);
	}

	// Getters and Setters

	public MancalaGameState getState() {
		return state;
	}

	/**
	 * Use depth-first search to build a game tree of all possible Mancala moves to
	 * be used with minmax algorithm to find best moves.
	 * 
	 * @param root
	 *            the root node to grow a game tree.
	 */
	public void dfs(DefaultMutableTreeNode root) {

		Stack<DefaultMutableTreeNode> stack = new Stack<>();
		stack.push(root);

		int gameNumber = 1;
		System.out.printf("Starting game %d... %n", gameNumber);

		while (!stack.isEmpty()) {

			DefaultMutableTreeNode parent = stack.pop();
			MancalaGameState parentState = new MancalaGameState((MancalaGameState) parent.getUserObject());

			// If game is over print out all the moves from the beginning and who won
			if (parentState.isGameOver()) {

				Object[] path = parent.getUserObjectPath();

				for (int i = 1; i < path.length; i++) {
					// System.out.printf("%s ", ((MancalaGameState) path[i]).getSowedPit());
					System.out.printf("Turn %d: Player %s picked pit %s.%n%n", i,
							((MancalaGameState) path[i - 1]).getPlayerTurn(),
							((MancalaGameState) path[i]).getSowedPit());
					System.out.println(path[i]);
				}

				if (parentState.getPitMap().get(Pit.MANCALA_A).size() > parentState.getPitMap().get(Pit.MANCALA_B)
						.size())
					System.out.println("Player A won.");

				else if (parentState.getPitMap().get(Pit.MANCALA_A).size() < parentState.getPitMap().get(Pit.MANCALA_B)
						.size())
					System.out.println("Player B won.");

				else
					System.out.println("Game ended in draw.");

				System.out.printf("%nStarting game %d... %n%n", ++gameNumber);

				continue;
			}

			Object[] moves = parentState.getSowablePits().toArray();

			for (int index = 0; index < moves.length; index++) {
				MancalaGameState childState = new MancalaGameState(parentState);
				childState.sow((Pit) moves[moves.length - index - 1]);
				childState.checkIfGameEnded();
				DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(childState);
				gameTree.insertNodeInto(newChild, parent, index);
				stack.push(newChild);
			}

		}

		// state = (MancalaGameState) root.getUserObject();

		// // If game is over print out all the moves from the beginning and who won
		// if (state.isGameOver()) {
		//
		// Object[] path = root.getUserObjectPath();
		//
		// for (int i = 1; i < path.length; i++)
		// System.out.printf("%s ", ((MancalaGameState) path[i]).getSowedPit());
		//
		// if (state.getPitMap().get(Pit.MANCALA_A).size() >
		// state.getPitMap().get(Pit.MANCALA_B).size())
		// System.out.println("Player A won.");
		//
		// else if (state.getPitMap().get(Pit.MANCALA_A).size() <
		// state.getPitMap().get(Pit.MANCALA_B).size())
		// System.out.println("Player B won.");
		//
		// else
		// System.out.println("Game ended in draw.");
		//
		// return;
		// }

		// EnumSet<Pit> moves = state.getSowablePits();
		// Object[] moves = state.getSowablePits().toArray();
		//
		// for (int index = 0; index < moves.length; index++) {
		// MancalaGameState newState = new MancalaGameState((MancalaGameState)
		// root.getUserObject());
		// newState.sow((Pit) moves[index]);
		// newState.checkIfGameEnded();
		// DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(newState);
		// gameTree.insertNodeInto(newChild, root, index);
		// dfs(newChild);
		// }

	}

	/**
	 * Write the game tree to file by serialization
	 * 
	 * @param filename
	 *            the name of the file to write the game tree to
	 */
	public void saveGameTree(String filename) {
		try {
			FileOutputStream file = new FileOutputStream(filename);
			ObjectOutputStream output = new ObjectOutputStream(file);
			output.writeObject(filename);
			output.close();
			file.close();
		} catch (IOException e) {
			System.out.println("Unable to write to " + filename);
			e.printStackTrace();
		}
	}

	/**
	 * All testings are done here.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		MancalaModel model = new MancalaModel();
		model.state.populatePits(3);
		model.dfs(model.root);
		model.saveGameTree("gametree.ser");
	}

}
