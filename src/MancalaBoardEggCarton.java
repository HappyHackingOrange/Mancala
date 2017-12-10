import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

/**
 * A drawing strategy to draw a standard Mancala board.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaBoardEggCarton implements MancalaBoardFormatter {

	// Constants
	private final static int HEADS = 0;
	private final static int TAILS = 1;
	private static final double pad = 20;
	private static final double pitPad = 10;
	private static final double pitSmallRadius = 100 - pitPad;
	private static final float highlightThickness = 3;
	private static final double stoneSize = 85;
	final static float dash[] = { 10.0f };
	final static BasicStroke strokeMancala = new BasicStroke(10.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
			dash, 0.0f);
	final static AffineTransform identity = new AffineTransform();

	// Colorings
	private static final Color LIGHTEST = new Color(0xBDB2AE);
	private static final Color LIGHTER = new Color(0xB0A5A1);
	private static final Color LIGHT = new Color(0xA39894);
	private static final Color MEDIUM = new Color(0x968B87);
	private static final Color DARK = new Color(0x8A7F7B);
	private static final Color DARKER = new Color(0x7D726E);
	private static final Color DARKEST = new Color(0x706561);

	// Labeling styles
	private static final Font fontLabelSmallPits = new Font("SansSerif", Font.BOLD, 50);
	private static final Font fontLabelSmallPitsNoOfStones = new Font("SansSerif", Font.BOLD, 100);
	private static final Font fontLabelMancalasNoOfStones = new Font("SansSerif", Font.BOLD, 200);
	private static final Composite c_trans = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .4f);
	private static final Composite c_reg = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);

	// Instance variables
	private MancalaBoardPanel boardPanel;
	private RoundRectangle2D[] eggCartonTop;
	private RoundRectangle2D[] eggCartonBottom;
	private Area eggCartonArea;
	private Rectangle2D[] eggCartonSeals;
	private RoundRectangle2D[] eggCartonDiamonds;
	private EnumMap<Pit, Ellipse2D[]> eggCartonPitGraphics;
	private double width, height;
	private BufferedImage imgHeads;
	private BufferedImage imgTails;

	// Constructors

	public MancalaBoardEggCarton() {

	}

	public MancalaBoardEggCarton(MancalaBoardPanel boardPanel) {
		this.boardPanel = boardPanel;
		eggCartonTop = new RoundRectangle2D[3];
		eggCartonBottom = new RoundRectangle2D[2];
		eggCartonSeals = new Rectangle2D[4];
		eggCartonDiamonds = new RoundRectangle2D[5];
		eggCartonPitGraphics = new EnumMap<>(Pit.class);
		for (Pit pit : Pit.values())
			eggCartonPitGraphics.put(pit, new Ellipse2D[2]);

		// Load img of a penny, heads
		String imgHeadsFilepath = "data/heads_us.png";
		ImageIcon imageHeadsIcon = new ImageIcon(imgHeadsFilepath);
		Image tmpImageHeads = imageHeadsIcon.getImage();
		imgHeads = new BufferedImage(imageHeadsIcon.getIconWidth(), imageHeadsIcon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		imgHeads.getGraphics().drawImage(tmpImageHeads, 0, 0, null);
		tmpImageHeads.flush();

		// Load img of a penny, tails
		String imgTailsFilepath = "data/tails_us.png";
		ImageIcon imageTailsIcon = new ImageIcon(imgTailsFilepath);
		Image tmpImageTails = imageTailsIcon.getImage();
		imgTails = new BufferedImage(imageTailsIcon.getIconWidth(), imageTailsIcon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		imgTails.getGraphics().drawImage(tmpImageTails, 0, 0, null);
		tmpImageTails.flush();

	}

	// Copy-constructor
	public MancalaBoardEggCarton(MancalaBoardEggCarton boardEggCarton) {
		boardPanel = boardEggCarton.boardPanel;
		eggCartonTop = boardEggCarton.eggCartonTop;
		eggCartonBottom = boardEggCarton.eggCartonBottom;
		eggCartonArea = boardEggCarton.eggCartonArea;
		eggCartonSeals = boardEggCarton.eggCartonSeals;
		eggCartonDiamonds = boardEggCarton.eggCartonDiamonds;
		eggCartonPitGraphics = boardEggCarton.eggCartonPitGraphics;
		width = boardEggCarton.width;
		height = boardEggCarton.height;
		imgHeads = boardEggCarton.imgHeads;
		imgTails = boardEggCarton.imgTails;
	}

	/**
	 * Clones this instance.
	 */
	public MancalaBoardFormatter cloneThis() {
		return new MancalaBoardEggCarton(this);
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

		double eggCartonWidth = 7 * pitPad + 12 * pitSmallRadius;
		double eggCartonLength = 3 * pitPad + 4 * pitSmallRadius;
		width = 2 * pad + eggCartonWidth;
		height = 3 * pad + 2 * eggCartonLength;

		// Draw the initial board without pits
		eggCartonTop[0] = new RoundRectangle2D.Double(pad, pad, eggCartonWidth, eggCartonLength,
				2 * pitSmallRadius + pitPad, 2 * pitSmallRadius + pitPad);
		eggCartonTop[1] = new RoundRectangle2D.Double(pad + pitPad, pad + pitPad, eggCartonWidth - 2 * pitPad,
				eggCartonLength - 2 * pitPad, 2 * pitSmallRadius, 2 * pitSmallRadius);
		int steepness = 6;
		eggCartonTop[2] = new RoundRectangle2D.Double(pad + steepness * pitPad, pad + steepness * pitPad,
				eggCartonWidth - 2 * steepness * pitPad, eggCartonLength - 2 * steepness * pitPad,
				2 * pitSmallRadius - (steepness - 1) * pitPad, 2 * pitSmallRadius - (steepness - 1) * pitPad);
		eggCartonBottom[0] = new RoundRectangle2D.Double(pad, pad + eggCartonLength, eggCartonWidth, eggCartonLength,
				2 * pitSmallRadius + pitPad, 2 * pitSmallRadius + pitPad);

		// Draw big pits
		double pitWidth = eggCartonTop[2].getWidth() / 2 - 4 * pitPad;
		double bigPitHeight = eggCartonTop[2].getHeight() - 4 * pitPad;
		double arcSize = 100;
		boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A)
				.setOuterBound(new RoundRectangle2D.Double(
						eggCartonTop[2].getX() + eggCartonTop[2].getWidth() / 2 + 2 * pitPad,
						eggCartonTop[2].getY() + 2 * pitPad, pitWidth, bigPitHeight, arcSize, arcSize));
		boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B)
				.setOuterBound(new RoundRectangle2D.Double(eggCartonTop[2].getX() + 2 * pitPad,
						eggCartonTop[2].getY() + 2 * pitPad, pitWidth, bigPitHeight, arcSize, arcSize));

		// Create pits boundaries for placing each center of stone in random positions
		double pitBoundPad = stoneSize / 2;
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
		for (Pit pit : Pit.sideAPits) {
			boardPanel.getPitGraphicsMap().get(pit)
					.setOuterBound(new Ellipse2D.Double(
							eggCartonBottom[0].getX() + (2 * pitSmallRadius + pitPad) * pit.ordinal() + pitPad,
							eggCartonBottom[0].getY() + 2 * pitSmallRadius + 2 * pitPad, 2 * pitSmallRadius,
							2 * pitSmallRadius));
			eggCartonPitGraphics.get(pit)[0] = new Ellipse2D.Double(
					boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX() + pitPad,
					boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY() + pitPad,
					2 * (pitSmallRadius - pitPad), 2 * (pitSmallRadius - pitPad));
			eggCartonPitGraphics.get(pit)[1] = new Ellipse2D.Double(
					boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX() + 2 * pitPad,
					boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY() + 2 * pitPad,
					2 * (pitSmallRadius - 2 * pitPad), 2 * (pitSmallRadius - 2 * pitPad));
		}
		for (Pit pit : Pit.sideBPits) {
			boardPanel.getPitGraphicsMap().get(pit)
					.setOuterBound(new Ellipse2D.Double(
							eggCartonBottom[0].getX() + (2 * pitSmallRadius + pitPad) * (12 - pit.ordinal()) + pitPad,
							eggCartonBottom[0].getY() + pitPad, 2 * pitSmallRadius, 2 * pitSmallRadius));
			eggCartonPitGraphics.get(pit)[0] = new Ellipse2D.Double(
					boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX() + pitPad,
					boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY() + pitPad,
					2 * (pitSmallRadius - pitPad), 2 * (pitSmallRadius - pitPad));
			eggCartonPitGraphics.get(pit)[1] = new Ellipse2D.Double(
					boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX() + 2 * pitPad,
					boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY() + 2 * pitPad,
					2 * (pitSmallRadius - 2 * pitPad), 2 * (pitSmallRadius - 2 * pitPad));
		}

		// Create small pits boundaries for placing each center of stone in random
		// positions
		double smallPitBoundRadius = pitSmallRadius - 2 * pitPad - pitBoundPad;
		for (Pit pit : Pit.sideAPits)
			boardPanel.getPitGraphicsMap().get(pit)
					.setInnerBound(new Ellipse2D.Double(eggCartonPitGraphics.get(pit)[1].getX() + pitBoundPad,
							eggCartonPitGraphics.get(pit)[1].getY() + pitBoundPad, 2 * smallPitBoundRadius,
							2 * smallPitBoundRadius));
		for (Pit pit : Pit.sideBPits)
			boardPanel.getPitGraphicsMap().get(pit)
					.setInnerBound(new Ellipse2D.Double(eggCartonPitGraphics.get(pit)[1].getX() + pitBoundPad,
							eggCartonPitGraphics.get(pit)[1].getY() + pitBoundPad, 2 * smallPitBoundRadius,
							2 * smallPitBoundRadius));

		// Draw the egg carton seals
		eggCartonSeals[0] = new Rectangle2D.Double(
				boardPanel.getPitGraphicsMap().get(Pit.A2).getOuterBound().getX() + 2 * pad,
				eggCartonBottom[0].getMaxY(), 3 * pad, pad);
		eggCartonSeals[1] = new Rectangle2D.Double(eggCartonBottom[0].getMaxX() - eggCartonSeals[0].getX() - 2 * pad,
				eggCartonBottom[0].getMaxY(), 3 * pad, pad);
		eggCartonSeals[2] = new Rectangle2D.Double(eggCartonSeals[0].getX(), eggCartonTop[2].getX() - 2 * pad, 3 * pad,
				2 * pad);
		eggCartonSeals[3] = new Rectangle2D.Double(eggCartonSeals[1].getX(), eggCartonSeals[2].getY(), 3 * pad,
				2 * pad);

		// Create egg carton diamonds
		double length = 0.70 * pitSmallRadius;
		double x1 = (boardPanel.getPitGraphicsMap().get(Pit.A1).getOuterBound().getCenterX()
				+ boardPanel.getPitGraphicsMap().get(Pit.A2).getOuterBound().getCenterX() - length) / 2;
		double x2 = (boardPanel.getPitGraphicsMap().get(Pit.A2).getOuterBound().getCenterX()
				+ boardPanel.getPitGraphicsMap().get(Pit.A3).getOuterBound().getCenterX() - length) / 2;
		double y = eggCartonBottom[0].getCenterX() - (length - pad) / 2;
		double spacing = x2 - x1;
		for (int i = 0; i < 5; i++)
			eggCartonDiamonds[i] = new RoundRectangle2D.Double(x1 + spacing * i, y, length, length, pad, pad);

		// Create the intermediate level between middle and bottom of egg carton
		double sizer = (1 - 1 / Math.sqrt(2)) * pitSmallRadius;
		x1 = boardPanel.getPitGraphicsMap().get(Pit.B6).getOuterBound().getX() + sizer;
		double y1 = boardPanel.getPitGraphicsMap().get(Pit.B6).getOuterBound().getY() + sizer;
		x2 = boardPanel.getPitGraphicsMap().get(Pit.B1).getOuterBound().getMaxX() - sizer;
		double y2 = boardPanel.getPitGraphicsMap().get(Pit.A1).getOuterBound().getMaxY() - sizer;
		eggCartonBottom[1] = new RoundRectangle2D.Double(x1, y1, x2 - x1, y2 - y1, 0, 0);
		eggCartonArea = new Area(eggCartonBottom[1]);
		for (Pit pit : Pit.smallPits)
			eggCartonArea.add(new Area(boardPanel.getPitGraphicsMap().get(pit).getOuterBound()));
		double substractionCircleRadius = (pitSmallRadius + pitPad / 2) * Math.sqrt(2) - pitSmallRadius;
		for (RoundRectangle2D diamond : eggCartonDiamonds) {
			eggCartonArea.subtract(new Area(new Ellipse2D.Double(diamond.getCenterX() - substractionCircleRadius,
					y1 - sizer - pitPad / 2 - substractionCircleRadius, 2 * substractionCircleRadius,
					2 * substractionCircleRadius)));
			eggCartonArea.subtract(new Area(new Ellipse2D.Double(diamond.getCenterX() - substractionCircleRadius,
					y2 + sizer + pitPad / 2 - substractionCircleRadius, 2 * substractionCircleRadius,
					2 * substractionCircleRadius)));
		}
		eggCartonArea.subtract(new Area(new Ellipse2D.Double(x1 - sizer - pitPad / 2 - substractionCircleRadius,
				eggCartonBottom[1].getCenterY() - substractionCircleRadius, 2 * substractionCircleRadius,
				2 * substractionCircleRadius)));
		eggCartonArea.subtract(new Area(new Ellipse2D.Double(x2 + sizer + pitPad / 2 - substractionCircleRadius,
				eggCartonBottom[1].getCenterY() - substractionCircleRadius, 2 * substractionCircleRadius,
				2 * substractionCircleRadius)));

		// Set the preferred size for this panel
		boardPanel.setPreferredSize(new Dimension((int) width, (int) height));

	}

	/**
	 * Set the preferred size for the board panel.
	 */
	@Override
	public void setPreferredSize() {
		boardPanel.setPreferredSize(new Dimension((int) width, (int) height));
	}

	/**
	 * Coloring and filling of the shapes.
	 */
	@Override
	public void colorBoard(Graphics2D g2) {
		g2.setColor(LIGHTER);
		g2.fill(eggCartonTop[0]);
		g2.fill(eggCartonBottom[0]);
		g2.fill(eggCartonSeals[0]);
		g2.fill(eggCartonSeals[1]);
		g2.setColor(MEDIUM);
		g2.fill(eggCartonTop[1]);
		g2.setColor(DARKER);
		g2.fill(eggCartonTop[2]);
		g2.setColor(boardPanel.getBackground());
		g2.fill(eggCartonSeals[2]);
		g2.fill(eggCartonSeals[3]);
		g2.setColor(LIGHT);
		g2.fill(eggCartonArea);
		g2.setColor(LIGHTEST);
		AffineTransform oldForm = g2.getTransform();
		for (int i = 0; i < 5; i++) {
			g2.rotate(Math.PI / 4, eggCartonDiamonds[i].getCenterX(), eggCartonDiamonds[i].getCenterY());
			g2.fill(eggCartonDiamonds[i]);
			g2.setTransform(oldForm);
		}
		boolean isGameStarted = boardPanel.isGameStarted();
		for (Pit pit : Pit.sideAPits) {
			boolean isPlayerATurn = boardPanel.getModel().getState().getPlayerTurn().equals(Player.A);
			Pit pitNoHighlight = boardPanel.getPitNoHighlight();
			boolean isMouseOverThisPit = pitNoHighlight == pit;
			boolean isPitNotEmpty = !boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty();
			if (isGameStarted && isPlayerATurn && isMouseOverThisPit && isPitNotEmpty) {
				g2.setColor(DARK);
				g2.fill(boardPanel.getPitGraphicsMap().get(pit).getOuterBound());
				g2.setColor(DARKER);
				g2.fill(eggCartonPitGraphics.get(pit)[0]);
				g2.setColor(DARKEST);
				g2.fill(eggCartonPitGraphics.get(pit)[1]);
			} else {
				g2.setColor(MEDIUM);
				g2.fill(boardPanel.getPitGraphicsMap().get(pit).getOuterBound());
				g2.setColor(DARK);
				g2.fill(eggCartonPitGraphics.get(pit)[0]);
				g2.setColor(DARKER);
				g2.fill(eggCartonPitGraphics.get(pit)[1]);
			}
		}
		for (Pit pit : Pit.sideBPits) {
			boolean isPlayerBTurn = boardPanel.getModel().getState().getPlayerTurn().equals(Player.B);
			Pit pitNoHighlight = boardPanel.getPitNoHighlight();
			boolean isMouseOverThisPit = pitNoHighlight == pit;
			boolean isPitNotEmpty = !boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty();
			if (isGameStarted && isPlayerBTurn && isMouseOverThisPit && isPitNotEmpty) {
				g2.setColor(DARK);
				g2.fill(boardPanel.getPitGraphicsMap().get(pit).getOuterBound());
				g2.setColor(DARKER);
				g2.fill(eggCartonPitGraphics.get(pit)[0]);
				g2.setColor(DARKEST);
				g2.fill(eggCartonPitGraphics.get(pit)[1]);
			} else {
				g2.setColor(MEDIUM);
				g2.fill(boardPanel.getPitGraphicsMap().get(pit).getOuterBound());
				g2.setColor(DARK);
				g2.fill(eggCartonPitGraphics.get(pit)[0]);
				g2.setColor(DARKER);
				g2.fill(eggCartonPitGraphics.get(pit)[1]);
			}
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
		g2.draw(eggCartonTop[0]);
		g2.draw(eggCartonTop[1]);
		g2.draw(eggCartonTop[2]);
		g2.draw(eggCartonBottom[0]);
		g2.draw(eggCartonSeals[0]);
		g2.draw(eggCartonSeals[1]);
		g2.draw(eggCartonSeals[2]);
		g2.draw(eggCartonSeals[3]);
		g2.draw(eggCartonArea);
		AffineTransform oldForm = g2.getTransform();
		for (int i = 0; i < 5; i++) {
			g2.rotate(Math.PI / 4, eggCartonDiamonds[i].getCenterX(), eggCartonDiamonds[i].getCenterY());
			g2.draw(eggCartonDiamonds[i]);
			g2.setTransform(oldForm);
		}
		g2.setStroke(strokeMancala);
		g2.setComposite(c_trans);
		g2.draw(boardPanel.getPitGraphicsMap().get(Pit.MANCALA_A).getOuterBound());
		g2.draw(boardPanel.getPitGraphicsMap().get(Pit.MANCALA_B).getOuterBound());
		g2.setStroke(oldStroke);
		g2.setComposite(c_reg);
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
			g2.draw(eggCartonPitGraphics.get(pit)[0]);
			g2.draw(eggCartonPitGraphics.get(pit)[1]);
		}
		for (Pit pit : Pit.sideBPits) {
			boolean isPlayerBTurn = boardPanel.getModel().getState().getPlayerTurn().equals(Player.B);
			boolean isPitEmpty = boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty();
			boolean isHuman = boardPanel.isHuman().get(Player.B);
			if (isGameStarted && !isBoardStillAnimating && isPlayerBTurn && isHuman && !isPitEmpty)
				g2.setStroke(new BasicStroke(highlightThickness));
			g2.draw(boardPanel.getPitGraphicsMap().get(pit).getOuterBound());
			g2.setStroke(oldStroke);
			g2.draw(eggCartonPitGraphics.get(pit)[0]);
			g2.draw(eggCartonPitGraphics.get(pit)[1]);
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
		String str;
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
		metrics = g2.getFontMetrics(fontLabelSmallPits);
		strHeight = metrics.getHeight();
		g2.setFont(fontLabelSmallPits);
		for (Pit pit : Pit.mancalas) {
			str = pit.toString().replace("_", " ");
			strWidth = metrics.stringWidth(str);
			width = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getWidth();
			height = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getHeight();
			x = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX();
			y = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY();
			g2.drawString(str, x + (width - strWidth) / 2, y + metrics.getAscent());
		}
		g2.setComposite(c_reg);

	}

	/**
	 * Draws the stones.
	 */
	@Override
	public void drawStones(Graphics2D g2) {

		// Make edges smooth
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (Pit pit : Pit.values())
			for (Stone stone : boardPanel.getPitGraphicsMap().get(pit).getStoneList()) {

				MancalaStoneGraphics stoneGraphics = boardPanel.getStoneGraphicsMap().get(stone).getStoneComponent();
				BufferedImage img = (stoneGraphics.getCoinSide() == HEADS) ? imgHeads : imgTails;
				int dstx1 = (int) stoneGraphics.getX();
				int dsty1 = (int) stoneGraphics.getY();
				int srcx2, srcy2;
				srcx2 = img.getWidth();
				srcy2 = img.getHeight();

				// Drawing the rotated image at the required drawing locations
				AffineTransform trans = new AffineTransform();
				trans.setToIdentity();
				trans.translate(dstx1, dsty1);
				trans.scale(stoneSize / srcx2, stoneSize / srcy2);
				trans.rotate(stoneGraphics.getCoinAngle(), srcx2 / 2, srcy2 / 2);
				g2.drawImage(img, trans, null);

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
					str = String.format("%d\u00A2", boardPanel.getModel().getState().getPitMap().get(pit).size());
					strWidth = metrics.stringWidth(str);
					width = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getWidth();
					height = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getHeight();
					x = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getX();
					y = (int) boardPanel.getPitGraphicsMap().get(pit).getOuterBound().getY();
					g2.drawString(str, x + (width - strWidth) / 2, y + (height - strHeight) / 2 + metrics.getAscent());
				}
			}

			// Draw labels on big pits
			metrics = g2.getFontMetrics(fontLabelMancalasNoOfStones);
			strHeight = metrics.getHeight();
			g2.setFont(fontLabelMancalasNoOfStones);
			for (Pit pit : Pit.mancalas) {
				boolean isPitEmpty = boardPanel.getPitGraphicsMap().get(pit).getStoneList().isEmpty();
				Pit pitNoHighlight = boardPanel.getPitNoHighlight();
				boolean isMouseOverThisPit = pitNoHighlight == pit;
				if (isMouseOverThisPit && !isPitEmpty) {
					str = String.format("%d\u00A2", boardPanel.getModel().getState().getPitMap().get(pit).size());
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
