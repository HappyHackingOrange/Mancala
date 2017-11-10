import java.util.*;

/**
 * A model to store raw data for the Mancala game
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaModel extends Observable{
	
	int[] holes;
	
	/**
	 * Constructor
	 */
	public MancalaModel() {
		
		holes = new int[14];
		
	}
	
	// Getters and setters
	
	public int[] getHoles() {
		return holes;
	}
	
		public void setHoles(int[] holes) {
		this.holes = holes;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Empty all stones in each hole.
	 */
	public void emptyHoles() {
		
		for (int i = 0; i < 14; i++)
			holes[i] = 0;
		
	}
	
	/**
	 * Add initial number of stones to each small holes
	 */
	public void populateStones(int initStones) {
		
		for (int i = 0; i < 6; i++)
			holes[i] = initStones;

		for (int i = 7; i < 13; i++)
			holes[i] = initStones;

	}
	
}
