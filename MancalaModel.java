/**
 * A model to store raw data for the Mancala game
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaModel {

	int[] pits;

	/**
	 * Constructor
	 */
	public MancalaModel() {

		pits = new int[14];

	}

	// Getters and setters

	public int[] getPits() {
		return pits;
	}

	public void setPits(int[] pits) {
		this.pits = pits;
	}

	/**
	 * Empty all stones in each pit.
	 */
	public void emptyPits() {

		for (int i = 0; i < 14; i++)
			pits[i] = 0;

	}

	/**
	 * Add initial number of stones to each small pits
	 */
	public void populateStones(int initStones) {

		for (int i = 0; i < 6; i++)
			pits[i] = initStones;

		for (int i = 7; i < 13; i++)
			pits[i] = initStones;

	}

}
