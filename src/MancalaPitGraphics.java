import java.awt.geom.*;
import java.util.*;

import javax.swing.JLabel;

/**
 * A class that holds graphics properties for each pit.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaPitGraphics {

	// Instance variables
	private RectangularShape outerBound;
	private RectangularShape innerBound;
	private LinkedList<Stone> stoneList;
//	private JLabel label;

	// Constructor
	public MancalaPitGraphics() {
		stoneList = new LinkedList<>();
	}
	
	// Copy-constructor
	public MancalaPitGraphics(MancalaPitGraphics pitGraphics) {
		outerBound = pitGraphics.outerBound;
		innerBound = pitGraphics.innerBound;
		stoneList = new LinkedList<>(pitGraphics.stoneList);
	}

	// Getters and setters

	public RectangularShape getOuterBound() {
		return outerBound;
	}

	public void setOuterBound(RectangularShape outerBound) {
		this.outerBound = outerBound;
	}

	public RectangularShape getInnerBound() {
		return innerBound;
	}

	public void setInnerBound(RectangularShape innerBound) {
		this.innerBound = innerBound;
	}

	public LinkedList<Stone> getStoneList() {
		return stoneList;
	}

	public void setStoneList(LinkedList<Stone> stoneList) {
		this.stoneList = stoneList;
	}

//	public JLabel getLabel() {
//		return label;
//	}

//	public void setLabel(JLabel label) {
//		this.label = label;
//	}

}
