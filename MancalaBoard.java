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
public class MancalaBoard extends JPanel {

	private MancalaModel model;
	private RoundRectangle2D board;
	private RoundRectangle2D[] holes;
	private RoundRectangle2D[] holesBound;
	private int width;
	private int height;
	private double stoneSize;
	private boolean hasOutlines;

	public MancalaBoard(MancalaModel model, int width, int height) {

		hasOutlines = true;
		this.model = model;
		this.width = width;
		this.height = height;
		int pad = height * 5 / 100;
		stoneSize = 30;

		setPreferredSize(new Dimension(width, height));

		// Draw the initial board without holes
		board = new RoundRectangle2D.Double(pad, pad, width - pad * 2, height - pad * 2, 0, 0);
		double boardX = board.getX();
		double boardY = board.getY();
		double boardWidth = board.getWidth();
		double boardHeight = board.getHeight();

		// Draw big holes
		double holePad = 10;
		holes = new RoundRectangle2D[14];
		double holeWidth = board.getWidth() / 8 - 2 * holePad;
		double bigHoleHeight = board.getHeight() - 2 * holePad;
		holes[6] = new RoundRectangle2D.Double(board.getX() + holePad, board.getY() + holePad, holeWidth, bigHoleHeight,
				holeWidth, holeWidth);
		holes[13] = new RoundRectangle2D.Double(board.getX() + (holeWidth + 2 * holePad) * 7 + holePad,
				board.getY() + holePad, holeWidth, bigHoleHeight, holeWidth, holeWidth);

		// Create big holes boundaries for placing each center of stone in random
		// positions
		double holeBoundPad = stoneSize / 2;
		holesBound = new RoundRectangle2D[14];
		double holeBoundWidth = holes[6].getWidth() - 2 * holeBoundPad;
		double bigHoleBoundHeight = holes[6].getHeight() - 2 * holeBoundPad;
		holesBound[6] = new RoundRectangle2D.Double(holes[6].getX() + holeBoundPad, holes[6].getY() + holeBoundPad,
				holeBoundWidth, bigHoleBoundHeight, holeBoundWidth, holeBoundWidth);
		holesBound[13] = new RoundRectangle2D.Double(holes[13].getX() + holeBoundPad, holes[13].getY() + holeBoundPad,
				holeBoundWidth, bigHoleBoundHeight, holeBoundWidth, holeBoundWidth);

		// Draw small holes
		double smallHoleHeight = board.getHeight() / 2 - 2 * holePad;
		for (int i = 0; i < 6; i++)
			holes[i] = new RoundRectangle2D.Double(board.getX() + (holeWidth + 2 * holePad) * (i + 1) + holePad,
					board.getY() + holePad, holeWidth, smallHoleHeight, holeWidth, holeWidth);
		for (int i = 7; i < 13; i++)
			holes[i] = new RoundRectangle2D.Double(board.getX() + (holeWidth + 2 * holePad) * (i - 6) + holePad,
					board.getY() + smallHoleHeight + holePad * 3, holeWidth, smallHoleHeight, holeWidth, holeWidth);

		// Create big holes boundaries for placing each center of stone in random
		// positions
		double smallHoleBoundHeight = holes[0].getHeight() - 2 * holeBoundPad;
		for (int i = 0; i < 6; i++)
			holesBound[i] = new RoundRectangle2D.Double(holes[i].getX() + holeBoundPad, holes[i].getY() + holeBoundPad,
					holeBoundWidth, smallHoleBoundHeight, holeBoundWidth, holeBoundWidth);
		for (int i = 7; i < 13; i++)
			holesBound[i] = new RoundRectangle2D.Double(holes[i].getX() + holeBoundPad, holes[i].getY() + holeBoundPad,
					holeBoundWidth, smallHoleBoundHeight, holeBoundWidth, holeBoundWidth);

		// Make the corners look more natural
		board.setRoundRect(pad, pad, width - pad * 2, height - pad * 2, holeWidth + pad, holeWidth + pad);
		
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
		g2.fill(holes[6]);
		g2.fill(holes[13]);
		for (int i = 0; i < 6; i++)
			g2.fill(holes[i]);
		for (int i = 7; i < 13; i++)
			g2.fill(holes[i]);

		if (hasOutlines) {
			g2.setColor(Color.BLACK);
			g2.draw(board);
			g2.draw(holes[6]);
			g2.draw(holes[13]);
			for (int i = 0; i < 6; i++)
				g2.draw(holes[i]);
			for (int i = 7; i < 13; i++)
				g2.draw(holes[i]);
		}

		// Draw stones
		for (int i = 0; i < 13; i++)
			for (MancalaStone stone : model.holes.get(i)) {
				g2.setColor(stone.getColor());
				g2.fill(new Ellipse2D.Double(stone.getX(), stone.getY(), stoneSize, stoneSize));
				g2.setColor(Color.BLACK);
				g2.draw(new Ellipse2D.Double(stone.getX(), stone.getY(), stoneSize, stoneSize));
			}

		// View the hole boundaries
		// g2.setColor(Color.BLACK);
		// for (int i = 0; i < 14; i++)
		// g2.draw(holesBound[i]);

	}

	/**
	 * Place a stone in a random position in a hole.
	 * 
	 * @param stone
	 * @param shape
	 */
	public void randomizePosition(MancalaStone stone, RoundRectangle2D shape) {

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
			if (model.holes.get(i).size() > 0) {
				randomizePosition(model.holes.get(i).get(0), holesBound[i]);
				for (int j = 1; j < model.holes.get(i).size(); j++) {
					MancalaStone stone1 = model.holes.get(i).get(j);
					boolean tooClose = true;
					while (tooClose) {
						randomizePosition(stone1, holesBound[i]);
						tooClose = false;
						for (int k = 0; k < j; k++) {
							MancalaStone stone2 = model.holes.get(i).get(k);
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
	public double distance(MancalaStone stone1, MancalaStone stone2) {
		return Math.sqrt(Math.pow(stone1.getX() - stone2.getX(), 2) + Math.pow(stone1.getY() - stone2.getY(), 2));

	}

}
