import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * A Mancala stone class containing some properties.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaStone {

	private double x;
	private double y;
	private Color color;

	// Constructors

	/**
	 * If nothing is specified, point starts at (0,0) and give it a random color
	 */
	public MancalaStone() {
		x = 0;
		y = 0;
		Random rand = new Random();
		color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
	}

	/**
	 * If only point is specified, give it a random color
	 * 
	 * @param x
	 * @param y
	 */
	public MancalaStone(double x, double y) {
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
