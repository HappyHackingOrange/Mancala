import java.awt.*;
import java.awt.geom.*;

/**
 * A drawing strategy to draw a standard Mancala board.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaBoardStandard implements MancalaBoardFormatter {

	// Constant variables;
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 400;

	/**
	 * Draw the standard Mancala board.
	 */
	@Override
	public void drawBoard(MancalaBoardPanel boardPanel) {

		boardPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		// Draw the initial board without pits
		int pad = HEIGHT / 20;
		boardPanel.setBoard(new RoundRectangle2D.Double(pad, pad, WIDTH - pad * 2, HEIGHT - pad * 2, 0, 0));

		// Draw big pits
		double pitPad = 10;
		double pitWidth = boardPanel.getBoard().getWidth() / 8 - 2 * pitPad;
		double bigPitHeight = boardPanel.getBoard().getHeight() - 2 * pitPad;
		boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).setOuterBound(
				new RoundRectangle2D.Double(boardPanel.getBoard().getX() + (pitWidth + 2 * pitPad) * 7 + pitPad,
						boardPanel.getBoard().getY() + pitPad, pitWidth, bigPitHeight, pitWidth, pitWidth));
		boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B)
				.setOuterBound(new RoundRectangle2D.Double(boardPanel.getBoard().getX() + pitPad,
						boardPanel.getBoard().getY() + pitPad, pitWidth, bigPitHeight, pitWidth, pitWidth));

		// Create pits boundaries for placing each center of stone in random positions
		double pitBoundPad = boardPanel.getStoneSize() / 2;
		double pitBoundWidth = boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).getOuterBound().getWidth()
				- 2 * pitBoundPad;
		double bigPitBoundHeight = boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).getOuterBound().getHeight()
				- 2 * pitBoundPad;
		boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A)
				.setInnerBound(new RoundRectangle2D.Double(
						boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).getOuterBound().getX() + pitBoundPad,
						boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).getOuterBound().getY() + pitBoundPad,
						pitBoundWidth, bigPitBoundHeight, pitBoundWidth, pitBoundWidth));
		boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B)
				.setInnerBound(new RoundRectangle2D.Double(
						boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B).getOuterBound().getX() + pitBoundPad,
						boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B).getOuterBound().getY() + pitBoundPad,
						pitBoundWidth, bigPitBoundHeight, pitBoundWidth, pitBoundWidth));

		// Draw small pits
		double smallPitHeight = boardPanel.getBoard().getHeight() / 2 - 2 * pitPad;
		for (Pit pit : Pit.sideAPits)
			boardPanel.getPitGraphicsMap().get(pit).setOuterBound(new RoundRectangle2D.Double(
					boardPanel.getBoard().getX() + (pitWidth + 2 * pitPad) * (pit.ordinal() + 1) + pitPad,
					boardPanel.getBoard().getY() + smallPitHeight + pitPad * 3, pitWidth, smallPitHeight, pitWidth,
					pitWidth));
		for (Pit pit : Pit.sideBPits)
			boardPanel.getPitGraphicsMap().get(pit)
					.setOuterBound(new RoundRectangle2D.Double(
							boardPanel.getBoard().getX() + (pitWidth + 2 * pitPad) * (13 - pit.ordinal()) + pitPad,
							boardPanel.getBoard().getY() + pitPad, pitWidth, smallPitHeight, pitWidth, pitWidth));

		// Create big pits boundaries for placing each center of stone in random
		// positions
		double smallPitBoundHeight = boardPanel.getPitGraphicsMap().get(Pit.A1).getOuterBound().getHeight()
				- 2 * pitBoundPad;
		for (Pit pit : Pit.sideAPits)
			boardPanel.getPitGraphicsMap().get(pit)
					.setInnerBound(new RoundRectangle2D.Double(
							boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX() + pitBoundPad,
							boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY() + pitBoundPad, pitBoundWidth,
							smallPitBoundHeight, pitBoundWidth, pitBoundWidth));
		for (Pit pit : Pit.sideBPits)
			boardPanel.getPitGraphicsMap().get(pit)
					.setInnerBound(new RoundRectangle2D.Double(
							boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX() + pitBoundPad,
							boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY() + pitBoundPad, pitBoundWidth,
							smallPitBoundHeight, pitBoundWidth, pitBoundWidth));

		// Make the corners look more natural
		((RoundRectangle2D) boardPanel.getBoard()).setRoundRect(pad, pad, WIDTH - pad * 2, HEIGHT - pad * 2, pitWidth + pad, pitWidth + pad);

	}

	/**
	 * Set the preferred size for the board panel.
	 */
	@Override
	public void setPreferredSize(MancalaBoardPanel boardPanel) {
		boardPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

}
