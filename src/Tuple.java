/**
 * 2-tuple structure to store an ordered list of 2 elements.
 * 
 * @author Vinent Stowbunenko
 *
 */
public class Tuple<StoneComponent> {

	// Instance Variable
	private StoneComponent stoneComponent;
	private Pit pit;

	// Constructor
	public Tuple(StoneComponent stoneComponent, Pit pit) {
		this.stoneComponent = stoneComponent;
		this.pit = pit;
	}
	
	// Copy-constructor
	public Tuple(Tuple<StoneComponent> tuple) {
//		stoneComponent = tuple.stoneComponent;
		stoneComponent = null;
		pit = tuple.pit;
	}

	public StoneComponent getStoneComponent() {
		return stoneComponent;
	}

	public Pit getPit() {
		return pit;
	}

	public void setStoneComponent(StoneComponent stoneComponent) {
		this.stoneComponent = stoneComponent;
	}

	public void setPit(Pit pit) {
		this.pit = pit;
	}

}