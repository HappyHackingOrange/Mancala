import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;

/**
 * The view section of the Mancala board.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaPanel extends JLabel {

	RoundRectangle2D board;
	int width;
	int height;

	public MancalaPanel(int width, int height) {
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width, height));
		int pad = height * 5 / 100;
		board = new RoundRectangle2D.Double(pad, pad, width - pad * 2, height - pad * 2, 50, 50);
	}

	@Override
	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		g2.draw(board);

	}

}
