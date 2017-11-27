import java.awt.geom.*;

/**
 * Strategy pattern to draw different types of rectangles on the Mancala board.
 * 
 * @author Monsi Magal, Vincent Stowbunenko
 *
 */
public interface MancalaBoardFormatter {
	public void drawBoard();
	public void setPreferredSize();
}
