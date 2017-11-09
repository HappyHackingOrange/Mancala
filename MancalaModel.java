import java.util.*;

/**
 * A model to store raw data for the Mancala game
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaModel {
	
	MancalaStone[] stones;
	List<List<MancalaStone>> holes;
	int initStones = 3;
	
	public MancalaModel() {
		
		holes = new ArrayList<List<MancalaStone>>(14);
		for (int i = 0; i < 14; i++)
			holes.add(new ArrayList<MancalaStone>());
		
		// Add initial number of stones to each small holes
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < initStones; j++)
				holes.get(i).add(new MancalaStone());
		}
		for (int i = 7; i < 13; i++)
			for (int j = 0; j < initStones; j++)
				holes.get(i).add(new MancalaStone());
		
	}

}
