import java.awt.*;
import java.awt.geom.*;

/**
 * A drawing strategy to draw a Mancala board with sharp corners.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaBoardSharp implements MancalaBoardFormatter {

	// Constant variables;
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 400;
	private static final Color LIGHT = new Color(176, 136, 87);
	private static final Color MEDIUM = new Color(142, 106, 63);
	private static final Color DARK = new Color(117, 81, 38);

	// Instance variables
	MancalaBoardPanel boardPanel;

	//Constructors

	public MancalaBoardSharp() {
		
	}

	public MancalaBoardSharp(MancalaBoardPanel boardPanel) {
		this.boardPanel = boardPanel;
	}


	/**
	 * Set the board panel.
	 * 
	 * @param boardPanel
	 */
	public void setBoardPanel(MancalaBoardPanel boardPanel) {
		this.boardPanel = boardPanel;
	}

	/**
	 * Draw the standard Mancala board.
	 */
	@Override
	public void drawBoard() {

		boardPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		// Draw the initial board without pits
		int pad = HEIGHT / 20;
		boardPanel.setBoard(new Rectangle2D.Double(pad, pad, WIDTH - pad * 2, HEIGHT - pad * 2));

		// Draw big pits
		double pitPad = 10;
		double pitWidth = boardPanel.getBoard().getWidth() / 8 - 2 * pitPad;
		double bigPitHeight = boardPanel.getBoard().getHeight() - 2 * pitPad;
		boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).setOuterBound(
				new Rectangle2D.Double(boardPanel.getBoard().getX() + (pitWidth + 2 * pitPad) * 7 + pitPad,
						boardPanel.getBoard().getY() + pitPad, pitWidth, bigPitHeight));
		boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B).setOuterBound(new Rectangle2D.Double(
				boardPanel.getBoard().getX() + pitPad, boardPanel.getBoard().getY() + pitPad, pitWidth, bigPitHeight));

		// Create pits boundaries for placing each center of stone in random positions
		double pitBoundPad = boardPanel.getStoneSize() / 2;
		double pitBoundWidth = boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).getOuterBound().getWidth()
				- 2 * pitBoundPad;
		double bigPitBoundHeight = boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).getOuterBound().getHeight()
				- 2 * pitBoundPad;
		boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A)
				.setInnerBound(new Rectangle2D.Double(
						boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).getOuterBound().getX() + pitBoundPad,
						boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).getOuterBound().getY() + pitBoundPad,
						pitBoundWidth, bigPitBoundHeight));
		boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B)
				.setInnerBound(new Rectangle2D.Double(
						boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B).getOuterBound().getX() + pitBoundPad,
						boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B).getOuterBound().getY() + pitBoundPad,
						pitBoundWidth, bigPitBoundHeight));

		// Draw small pits
		double smallPitHeight = boardPanel.getBoard().getHeight() / 2 - 2 * pitPad;
		for (Pit pit : Pit.sideAPits)
			boardPanel.getPitGraphicsMap().get(pit)
					.setOuterBound(new Rectangle2D.Double(
							boardPanel.getBoard().getX() + (pitWidth + 2 * pitPad) * (pit.ordinal() + 1) + pitPad,
							boardPanel.getBoard().getY() + smallPitHeight + pitPad * 3, pitWidth, smallPitHeight));
		for (Pit pit : Pit.sideBPits)
			boardPanel.getPitGraphicsMap().get(pit)
					.setOuterBound(new Rectangle2D.Double(
							boardPanel.getBoard().getX() + (pitWidth + 2 * pitPad) * (13 - pit.ordinal()) + pitPad,
							boardPanel.getBoard().getY() + pitPad, pitWidth, smallPitHeight));

		// Create big pits boundaries for placing each center of stone in random
		// positions
		double smallPitBoundHeight = boardPanel.getPitGraphicsMap().get(Pit.A1).getOuterBound().getHeight()
				- 2 * pitBoundPad;
		for (Pit pit : Pit.sideAPits)
			boardPanel.getPitGraphicsMap().get(pit)
					.setInnerBound(new Rectangle2D.Double(
							boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX() + pitBoundPad,
							boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY() + pitBoundPad, pitBoundWidth,
							smallPitBoundHeight));
		for (Pit pit : Pit.sideBPits)
			boardPanel.getPitGraphicsMap().get(pit)
					.setInnerBound(new Rectangle2D.Double(
							boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX() + pitBoundPad,
							boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY() + pitBoundPad, pitBoundWidth,
							smallPitBoundHeight));

	}

	/**
	 * Set the preferred size for the board panel.
	 */
	@Override
	public void setPreferredSize() {
		boardPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

	/**
	 * Coloring and filling scheme for the board.
	 */
	@Override
	public void colorBoard(Graphics2D g2) {
		g2.setColor(LIGHT);
		g2.fill(boardPanel.getBoard());
		g2.setColor(MEDIUM);
		g2.fill(boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).getOuterBound());
		g2.fill(boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B).getOuterBound());
		for (Pit pit : Pit.sideAPits) {
			if (boardPanel.isGameStarted() && !boardPanel.isBoardStillAnimating()
					&& boardPanel.getModel().getState().getPlayerTurn().equals(Player.A)
					&& boardPanel.getPitNoHighlight() == pit
					&& !boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty())
				g2.setColor(DARK);
			g2.fill(boardPanel.getPitGraphicsMap().get(pit).getOuterBound());
			g2.setColor(MEDIUM);
		}
		for (Pit pit : Pit.sideBPits) {
			if (boardPanel.isGameStarted() && !boardPanel.isBoardStillAnimating()
					&& boardPanel.getModel().getState().getPlayerTurn().equals(Player.B)
					&& boardPanel.getPitNoHighlight() == pit
					&& !boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty())
				g2.setColor(DARK);
			g2.fill(boardPanel.getPitGraphicsMap().get(pit).getOuterBound());
			g2.setColor(MEDIUM);
		}
	}

}
