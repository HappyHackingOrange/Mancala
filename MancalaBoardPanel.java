import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;

/**
 * The view section of the Mancala board.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaBoardPanel extends JPanel {

	private RoundRectangle2D board;
	private MancalaHoleGraphics[] holes;
	private double stoneSize;
	private boolean hasOutlines;
	private Color[] colors = new Color[] { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW };

	public MancalaBoardPanel(int width, int height) {

		hasOutlines = true;
		int pad = height / 20;
		stoneSize = 30;
		holes = new MancalaHoleGraphics[14];
		for (int i = 0; i < 14; i++)
			holes[i] = new MancalaHoleGraphics();

		setPreferredSize(new Dimension(width, height));

		// Draw the initial board without holes
		board = new RoundRectangle2D.Double(pad, pad, width - pad * 2, height - pad * 2, 0, 0);

		// Draw big holes
		double holePad = 10;
		double holeWidth = board.getWidth() / 8 - 2 * holePad;
		double bigHoleHeight = board.getHeight() - 2 * holePad;
		holes[6].setOuterBound(new RoundRectangle2D.Double(board.getX() + holePad, board.getY() + holePad, holeWidth,
				bigHoleHeight, holeWidth, holeWidth));
		holes[13].setOuterBound(new RoundRectangle2D.Double(board.getX() + (holeWidth + 2 * holePad) * 7 + holePad,
				board.getY() + holePad, holeWidth, bigHoleHeight, holeWidth, holeWidth));

		// Create big holes boundaries for placing each center of stone in random
		// positions
		double holeBoundPad = stoneSize / 2;
		double holeBoundWidth = holes[6].getOuterBound().getWidth() - 2 * holeBoundPad;
		double bigHoleBoundHeight = holes[6].getOuterBound().getHeight() - 2 * holeBoundPad;
		holes[6].setInnerBound(new RoundRectangle2D.Double(holes[6].getOuterBound().getX() + holeBoundPad,
				holes[6].getOuterBound().getY() + holeBoundPad, holeBoundWidth, bigHoleBoundHeight, holeBoundWidth,
				holeBoundWidth));
		holes[13].setInnerBound(new RoundRectangle2D.Double(holes[13].getOuterBound().getX() + holeBoundPad,
				holes[13].getOuterBound().getY() + holeBoundPad, holeBoundWidth, bigHoleBoundHeight, holeBoundWidth,
				holeBoundWidth));

		// Draw small holes
		double smallHoleHeight = board.getHeight() / 2 - 2 * holePad;
		for (int i = 0; i < 6; i++)
			holes[i].setOuterBound(
					new RoundRectangle2D.Double(board.getX() + (holeWidth + 2 * holePad) * (i + 1) + holePad,
							board.getY() + holePad, holeWidth, smallHoleHeight, holeWidth, holeWidth));
		for (int i = 7; i < 13; i++)
			holes[i].setOuterBound(new RoundRectangle2D.Double(
					board.getX() + (holeWidth + 2 * holePad) * (i - 6) + holePad,
					board.getY() + smallHoleHeight + holePad * 3, holeWidth, smallHoleHeight, holeWidth, holeWidth));

		// Create big holes boundaries for placing each center of stone in random
		// positions
		double smallHoleBoundHeight = holes[0].getOuterBound().getHeight() - 2 * holeBoundPad;
		for (int i = 0; i < 6; i++)
			holes[i].setInnerBound(new RoundRectangle2D.Double(holes[i].getOuterBound().getX() + holeBoundPad,
					holes[i].getOuterBound().getY() + holeBoundPad, holeBoundWidth, smallHoleBoundHeight,
					holeBoundWidth, holeBoundWidth));
		for (int i = 7; i < 13; i++)
			holes[i].setInnerBound(new RoundRectangle2D.Double(holes[i].getOuterBound().getX() + holeBoundPad,
					holes[i].getOuterBound().getY() + holeBoundPad, holeBoundWidth, smallHoleBoundHeight,
					holeBoundWidth, holeBoundWidth));

		// Make the corners look more natural
		board.setRoundRect(pad, pad, width - pad * 2, height - pad * 2, holeWidth + pad, holeWidth + pad);

		// Randomize all stone positions
		randomizeAllPositions();

	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(176, 136, 87));
		g2.draw(board);

		// Color the board
		g2.fill(board);
		g2.setColor(new Color(142, 106, 63));
		g2.fill(holes[6].getOuterBound());
		g2.fill(holes[13].getOuterBound());
		for (int i = 0; i < 6; i++)
			g2.fill(holes[i].getOuterBound());
		for (int i = 7; i < 13; i++)
			g2.fill(holes[i].getOuterBound());

		// Draw the outlines of the board
		if (hasOutlines) {
			g2.setColor(Color.BLACK);
			g2.draw(board);
			g2.draw(holes[6].getOuterBound());
			g2.draw(holes[13].getOuterBound());
			for (int i = 0; i < 6; i++)
				g2.draw(holes[i].getOuterBound());
			for (int i = 7; i < 13; i++)
				g2.draw(holes[i].getOuterBound());
		}

		// Draw stones
		for (int i = 0; i < 13; i++)
			for (MancalaStoneGraphics stone : holes[i].getStones()) {
				g2.setColor(stone.getColor());
				g2.fill(new Ellipse2D.Double(stone.getX(), stone.getY(), stoneSize, stoneSize));
				g2.setColor(Color.BLACK);
				g2.draw(new Ellipse2D.Double(stone.getX(), stone.getY(), stoneSize, stoneSize));
			}

		// View the hole boundaries
		// g2.setColor(Color.BLACK);
		// for (int i = 0; i < 14; i++)
		// g2.draw(holes[i].getInnerBound());

	}

	/**
	 * Place a stone in a random position in a hole.
	 * 
	 * @param stone
	 * @param shape
	 */
	public void randomizePosition(MancalaStoneGraphics stone, RoundRectangle2D shape) {

		Random rand = new Random();
		double randX = shape.getMinX() + (shape.getMaxX() - shape.getMinX()) * rand.nextDouble();
		double randY = shape.getMinY() + (shape.getMaxY() - shape.getMinY()) * rand.nextDouble();

		// If the stone is not inside the shape, randomize again
		while (!shape.contains(randX, randY)) {
			randX = shape.getMinX() + (shape.getMaxX() - shape.getMinX()) * rand.nextDouble();
			randY = shape.getMinY() + (shape.getMaxY() - shape.getMinY()) * rand.nextDouble();
		}

		stone.setX(randX - stoneSize / 2);
		stone.setY(randY - stoneSize / 2);

	}

	/**
	 * Randomize stone positions. Need to make sure each stones are not too close to
	 * each other.
	 */
	public void randomizeAllPositions() {

		for (int i = 0; i < 13; i++) {
			if (holes[i].getStones().size() > 0) {
				randomizePosition(holes[i].getStones().get(0), holes[i].getInnerBound());
				for (int j = 1; j < holes[i].getStones().size(); j++) {
					MancalaStoneGraphics stone1 = holes[i].getStones().get(j);
					boolean tooClose = true;
					while (tooClose) {
						randomizePosition(stone1, holes[i].getInnerBound());
						tooClose = false;
						for (int k = 0; k < j; k++) {
							MancalaStoneGraphics stone2 = holes[i].getStones().get(k);
							if (distance(stone1, stone2) < stoneSize / 2) {
								tooClose = true;
								break;
							}
						}
					}
				}
			}
		}

	}

	/**
	 * Calculate distance between stones.
	 * 
	 * @param stone1
	 * @param stone2
	 * @return the distance between stones
	 */
	public double distance(MancalaStoneGraphics stone1, MancalaStoneGraphics stone2) {
		return Math.sqrt(Math.pow(stone1.getX() - stone2.getX(), 2) + Math.pow(stone1.getY() - stone2.getY(), 2));

	}

	/**
	 * Clear all stones on the viewer.
	 */
	public void clearStones() {
		for (MancalaHoleGraphics holeGraphics : holes)
			holeGraphics.stones.clear();
	}

	/**
	 * Populate the stones on the holes based on the model.
	 * 
	 * @param holes
	 *            the holes from the model.
	 */
	public void populateStones(int[] holes) {
		for (int i = 0; i < holes.length; i++)
			for (int j = 0; j < holes[i]; j++)
				this.holes[i].stones.add(new MancalaStoneGraphics(colors[j % colors.length]));

	}

}
