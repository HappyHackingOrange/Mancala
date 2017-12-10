import java.awt.*;
import java.awt.geom.*;

/**
 * A drawing strategy to draw a Mancala board with sharp corners.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaBoardSharp implements MancalaBoardFormatter {

	// Constants
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 400;
	private static final float highlightThickness = 3;
	private static final double stoneSize = 30;

	// Colorings
	private static final Color LIGHT = new Color(176, 136, 87);
	private static final Color MEDIUM = new Color(142, 106, 63);
	private static final Color DARK = new Color(117, 81, 38);

	// Labeling styles
	private static final Font fontLabelSmallPits = new Font("SansSerif", Font.BOLD, 50);
	private static final Font fontLabelSmallPitsNoOfStones = new Font("SansSerif", Font.BOLD, 100);
	private static final Font fontLabelMancalas = new Font("SansSerif", Font.BOLD, 25);
	private static final Composite c_trans = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .4f);
	private static final Composite c_reg = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);

	// Instance variables
	private MancalaBoardPanel boardPanel;
	private Rectangle2D board;

	// Constructors

	public MancalaBoardSharp() {

	}

	public MancalaBoardSharp(MancalaBoardPanel boardPanel) {
		this.boardPanel = boardPanel;
	}

	// Copy-constructor
	public MancalaBoardSharp(MancalaBoardSharp boardSharp) {
		boardPanel = boardSharp.boardPanel;
		board = boardSharp.board;
	}

	/**
	 * Clones this instance.
	 */
	public MancalaBoardFormatter cloneThis() {
		return new MancalaBoardSharp(this);
	}

	/**
	 * Set the board panel.
	 * 
	 * @param boardPanel
	 */
	public void setBoardPanel(MancalaBoardPanel boardPanel) {
		this.boardPanel = boardPanel;
	}

	public double getStoneSize() {
		return stoneSize;
	}

	/**
	 * Draw the standard Mancala board.
	 */
	@Override
	public void createShapes() {

		boardPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		// Draw the initial board without pits
		int pad = HEIGHT / 20;
		board = new Rectangle2D.Double(pad, pad, WIDTH - pad * 2, HEIGHT - pad * 2);

		// Draw big pits
		double pitPad = 10;
		double pitWidth = board.getWidth() / 8 - 2 * pitPad;
		double bigPitHeight = board.getHeight() - 2 * pitPad;
		boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).setOuterBound(new Rectangle2D.Double(
				board.getX() + (pitWidth + 2 * pitPad) * 7 + pitPad, board.getY() + pitPad, pitWidth, bigPitHeight));
		boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B).setOuterBound(
				new Rectangle2D.Double(board.getX() + pitPad, board.getY() + pitPad, pitWidth, bigPitHeight));

		// Create pits boundaries for placing each center of stone in random positions
		double pitBoundPad = stoneSize / 2;
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
		double smallPitHeight = board.getHeight() / 2 - 2 * pitPad;
		for (Pit pit : Pit.sideAPits)
			boardPanel.getPitGraphicsMap().get(pit)
					.setOuterBound(new Rectangle2D.Double(
							board.getX() + (pitWidth + 2 * pitPad) * (pit.ordinal() + 1) + pitPad,
							board.getY() + smallPitHeight + pitPad * 3, pitWidth, smallPitHeight));
		for (Pit pit : Pit.sideBPits)
			boardPanel.getPitGraphicsMap().get(pit)
					.setOuterBound(new Rectangle2D.Double(
							board.getX() + (pitWidth + 2 * pitPad) * (13 - pit.ordinal()) + pitPad,
							board.getY() + pitPad, pitWidth, smallPitHeight));

		// Create small pits boundaries for placing each center of stone in random
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
	 * Coloring and filling of the shapes.
	 */
	@Override
	public void colorBoard(Graphics2D g2) {
		g2.setColor(LIGHT);
		g2.fill(board);
		g2.setColor(MEDIUM);
		g2.fill(boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).getOuterBound());
		g2.fill(boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B).getOuterBound());
		boolean isGameStarted = boardPanel.isGameStarted();
		for (Pit pit : Pit.sideAPits) {
			boolean isPlayerATurn = boardPanel.getModel().getState().getPlayerTurn().equals(Player.A);
			Pit pitNoHighlight = boardPanel.getPitNoHighlight();
			boolean isMouseOverThisPit = pitNoHighlight == pit;
			boolean isPitNotEmpty = !boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty();
			if (isGameStarted && isPlayerATurn && isMouseOverThisPit && isPitNotEmpty)
				g2.setColor(DARK);
			g2.fill(boardPanel.getPitGraphicsMap().get(pit).getOuterBound());
			g2.setColor(MEDIUM);
		}
		for (Pit pit : Pit.sideBPits) {
			boolean isPlayerBTurn = boardPanel.getModel().getState().getPlayerTurn().equals(Player.B);
			Pit pitNoHighlight = boardPanel.getPitNoHighlight();
			boolean isMouseOverThisPit = pitNoHighlight == pit;
			boolean isPitNotEmpty = !boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty();
			if (isGameStarted && isPlayerBTurn && isMouseOverThisPit && isPitNotEmpty)
				g2.setColor(DARK);
			g2.fill(boardPanel.getPitGraphicsMap().get(pit).getOuterBound());
			g2.setColor(MEDIUM);
		}
	}

	/**
	 * Draw the outlines of the shapes
	 */
	@Override
	public void drawBoard(Graphics2D g2) {

		// Pits on current player border should thicken on that player's turn
		Stroke oldStroke = g2.getStroke();

		// Draw the outlines
		g2.setColor(Color.BLACK);
		g2.draw(board);
		g2.draw(boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).getOuterBound());
		g2.draw(boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B).getOuterBound());
		boolean isGameStarted = boardPanel.isGameStarted();
		boolean isBoardStillAnimating = boardPanel.isBoardStillAnimating();
		for (Pit pit : Pit.sideAPits) {
			boolean isPlayerATurn = boardPanel.getModel().getState().getPlayerTurn().equals(Player.A);
			boolean isPitEmpty = boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty();
			boolean isHuman = boardPanel.isHuman().get(Player.A);
			if (isGameStarted && !isBoardStillAnimating && isPlayerATurn && isHuman && !isPitEmpty)
				g2.setStroke(new BasicStroke(highlightThickness));
			g2.draw(boardPanel.getPitGraphicsMap().get(pit).getOuterBound());
			g2.setStroke(oldStroke);
		}
		for (Pit pit : Pit.sideBPits) {
			boolean isPlayerBTurn = boardPanel.getModel().getState().getPlayerTurn().equals(Player.B);
			boolean isPitEmpty = boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty();
			boolean isHuman = boardPanel.isHuman().get(Player.B);
			if (isGameStarted && !isBoardStillAnimating && isPlayerBTurn && isHuman && !isPitEmpty)
				g2.setStroke(new BasicStroke(highlightThickness));
			g2.draw(boardPanel.getPitGraphicsMap().get(pit).getOuterBound());
			g2.setStroke(oldStroke);
		}
	}

	/**
	 * Draws the labels on the pit.
	 */
	@Override
	public void drawLabelsPit(Graphics2D g2) {

		// Variables for drawing labels
		g2.setComposite(c_trans);
		int strWidth, strHeight, width, height, x, y;
		String str, letter;
		FontMetrics metrics;

		// Draw labels on small pits
		metrics = g2.getFontMetrics(fontLabelSmallPits);
		strHeight = metrics.getHeight();
		g2.setFont(fontLabelSmallPits);
		for (Pit pit : Pit.smallPits) {
			str = pit.toString();
			strWidth = metrics.stringWidth(str);
			width = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getWidth();
			height = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getHeight();
			x = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX();
			y = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY();
			g2.drawString(str, x + (width - strWidth) / 2, y + (height - strHeight) / 2 + metrics.getAscent());
		}

		// Draw labels on big pits
		metrics = g2.getFontMetrics(fontLabelMancalas);
		strHeight = metrics.getHeight();
		g2.setFont(fontLabelMancalas);
		for (Pit pit : Pit.mancalas) {
			str = pit.toString();
			width = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getWidth();
			height = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getHeight();
			x = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX();
			y = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY();
			for (int i = 0; i < str.length(); i++) {
				letter = str.substring(i, i + 1);
				if (letter.compareTo("_") == 0)
					letter = " ";
				strWidth = metrics.stringWidth(letter);
				g2.drawString(letter.substring(0, 1), x + (width - strWidth) / 2,
						y + (height - strHeight) / 2 + metrics.getAscent() + strHeight * (i - str.length() / 2));
			}
		}
		g2.setComposite(c_reg);

	}

	/**
	 * Draws the stones.
	 */
	@Override
	public void drawStones(Graphics2D g2) {
		for (Pit pit : Pit.values())
			for (Stone stone : boardPanel.getPitGraphicsMap().get(pit).getStoneList()) {
				MancalaStoneGraphics stoneGraphics = boardPanel.getStoneGraphicsMap().get(stone).getStoneComponent();
				g2.setColor(stoneGraphics.getColor());
				g2.fill(new Ellipse2D.Double(stoneGraphics.getX(), stoneGraphics.getY(), stoneSize, stoneSize));
				g2.setColor(Color.BLACK);
				g2.draw(new Ellipse2D.Double(stoneGraphics.getX(), stoneGraphics.getY(), stoneSize, stoneSize));
			}

	}

	/**
	 * Lets the user know how many stones on the pit the mouse is over.
	 */
	@Override
	public void drawLabelsNumberOfStonesPerPit(Graphics2D g2) {

		boolean isGameStarted = boardPanel.isGameStarted();
		boolean isBoardStillAnimating = boardPanel.isBoardStillAnimating();

		if (isGameStarted && !isBoardStillAnimating) {

			// Variables for drawing labels
			int strWidth, strHeight, width, height, x, y;
			String str;
			FontMetrics metrics;

			// Draw labels on small pits
			metrics = g2.getFontMetrics(fontLabelSmallPitsNoOfStones);
			strHeight = metrics.getHeight();
			g2.setFont(fontLabelSmallPitsNoOfStones);
			for (Pit pit : Pit.smallPits) {
				boolean isPitEmpty = boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty();
				Pit pitNoHighlight = boardPanel.getPitNoHighlight();
				boolean isMouseOverThisPit = pitNoHighlight == pit;
				if (isMouseOverThisPit && !isPitEmpty) {
					str = String.format("%d", boardPanel.getModel().getState().getPitMap().get(pit).size());
					strWidth = metrics.stringWidth(str);
					width = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getWidth();
					height = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getHeight();
					x = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX();
					y = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY();
					g2.drawString(str, x + (width - strWidth) / 2, y + (height - strHeight) / 2 + metrics.getAscent());
				}
			}

			// Draw labels on big pits
			metrics = g2.getFontMetrics(fontLabelSmallPitsNoOfStones);
			strHeight = metrics.getHeight();
			g2.setFont(fontLabelSmallPitsNoOfStones);
			for (Pit pit : Pit.mancalas) {
				boolean isPitEmpty = boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty();
				Pit pitNoHighlight = boardPanel.getPitNoHighlight();
				boolean isMouseOverThisPit = pitNoHighlight == pit;
				if (isMouseOverThisPit && !isPitEmpty) {
					str = String.format("%d", boardPanel.getModel().getState().getPitMap().get(pit).size());
					strWidth = metrics.stringWidth(str);
					width = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getWidth();
					height = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getHeight();
					x = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX();
					y = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY();
					g2.drawString(str, x + (width - strWidth) / 2, y + (height - strHeight) / 2 + metrics.getAscent());
				}
			}

		}

	}

}
