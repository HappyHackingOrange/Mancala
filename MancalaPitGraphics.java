import java.awt.geom.*;
import java.util.*;

/**
 * A class that holds graphics properties for each pit.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaPitGraphics {

	private RoundRectangle2D outerBound;
	private RoundRectangle2D innerBound;
	LinkedList<MancalaStoneGraphics> stones;
	
	public MancalaPitGraphics() {
		stones = new LinkedList<>();
	}
	
	public RoundRectangle2D getOuterBound() {
		return outerBound;
	}
	public void setOuterBound(RoundRectangle2D outerBound) {
		this.outerBound = outerBound;
	}
	public RoundRectangle2D getInnerBound() {
		return innerBound;
	}
	public void setInnerBound(RoundRectangle2D innerBound) {
		this.innerBound = innerBound;
	}
	public LinkedList<MancalaStoneGraphics> getStones() {
		return stones;
	}
	public void setStones(LinkedList<MancalaStoneGraphics> stones) {
		this.stones = stones;
	}
	

}
