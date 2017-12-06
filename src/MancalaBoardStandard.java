import java.awt.*;
import java.awt.geom.*;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.OverlayLayout;

/**
 * A drawing strategy to draw a standard Mancala board.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaBoardStandard implements MancalaBoardFormatter {

	// Constants
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 400;
	private static final Color LIGHT = new Color(176, 136, 87);
	private static final Color MEDIUM = new Color(142, 106, 63);
	private static final Color DARK = new Color(117, 81, 38);

	// Instance variables
	MancalaBoardPanel boardPanel;

	// Constructors

	public MancalaBoardStandard() {

	}

	public MancalaBoardStandard(MancalaBoardPanel boardPanel) {
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
		((RoundRectangle2D) boardPanel.getBoard()).setRoundRect(pad, pad, WIDTH - pad * 2, HEIGHT - pad * 2,
				pitWidth + pad, pitWidth + pad);

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
		boolean isGameStarted = boardPanel.isGameStarted();
		boolean isBoardStillAnimating = boardPanel.isBoardStillAnimating();
		for (Pit pit : Pit.sideAPits) {
			boolean isPlayerATurn = boardPanel.getModel().getState().getPlayerTurn().equals(Player.A);
			boolean isMouseOverThisPit = boardPanel.getPitNoHighlight() == pit;
			boolean isPitNotEmpty = !boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty();
			if (isGameStarted && isBoardStillAnimating && isPlayerATurn && isMouseOverThisPit && isPitNotEmpty)
				g2.setColor(DARK);
			g2.fill(boardPanel.getPitGraphicsMap().get(pit).getOuterBound());
			g2.setColor(MEDIUM);
		}
		for (Pit pit : Pit.sideBPits) {
			boolean isPlayerBTurn = boardPanel.getModel().getState().getPlayerTurn().equals(Player.B);
			boolean isMouseOverThisPit = boardPanel.getPitNoHighlight() == pit;
			boolean isPitNotEmpty = !boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty();
			if (isGameStarted && isBoardStillAnimating && isPlayerBTurn && isMouseOverThisPit && isPitNotEmpty)
				g2.setColor(DARK);
			g2.fill(boardPanel.getPitGraphicsMap().get(pit).getOuterBound());
			g2.setColor(MEDIUM);
		}
	}

}
