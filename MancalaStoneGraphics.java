import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * A Mancala stone class to hold each stone's graphics properties.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaStoneGraphics {

	private double x;
	private double y;
	private Color color;

	// Constructors

	/**
	 * If nothing is specified, point starts at (0,0) and give it a random color
	 */
	public MancalaStoneGraphics() {
		x = 0;
		y = 0;
		Random rand = new Random();
		color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
	}

	/**
	 * For if just color is specified
	 */
	public MancalaStoneGraphics(Color color) {
		x = 0;
		y = 0;
		this.color = color;
	}

	/**
	 * If only point is specified, give it a random color
	 * 
	 * @param x
	 * @param y
	 */
	public MancalaStoneGraphics(double x, double y) {
		this.x = x;
		this.y = y;
		Random rand = new Random();
		color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
}
