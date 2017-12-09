import java.awt.Graphics2D;
import java.awt.geom.*;

/**
 * Strategy pattern to draw different types of rectangles on the Mancala board.
 * 
 * @author Monsi Magal, Vincent Stowbunenko
 *
 */
public interface MancalaBoardFormatter {
	public void setBoardPanel(MancalaBoardPanel boardPanel);
	public MancalaBoardFormatter cloneThis();
	public void createShapes();
	public void setPreferredSize();
	public double getStoneSize();
	public void drawBoard(Graphics2D g2);
	public void colorBoard(Graphics2D g2);
	public void drawLabelsPit(Graphics2D g2);
	public void drawStones(Graphics2D g2);
	public void drawLabelsNumberOfStonesPerPit(Graphics2D g2);
}
