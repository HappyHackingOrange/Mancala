import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;

/**
 * The view section of the Mancala board.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaBoardPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

	private MancalaModel model;
	private RoundRectangle2D board;
	private MancalaPitGraphics[] pits;
	private Map<Integer, MancalaStoneGraphics> stones;
	private double stoneSize;
	private boolean hasOutlines;
	private Color[] colors = new Color[] { new Color(0xF16A70), new Color(0xB1D877), new Color(0x8CDCDA),
			new Color(0x4D4D4D) };
	private Timer timer;
	private final int DELAY = 1;
	private boolean gameStarted;
	private int pitNoHighlight;

	public MancalaBoardPanel(int width, int height, MancalaModel model) {

		stones = new HashMap<>();
		this.model = model;
		hasOutlines = true;
		int pad = height / 20;
		stoneSize = 30;
		pits = new MancalaPitGraphics[14];
		gameStarted = false;
		pitNoHighlight = -1;
		for (int i = 0; i < 14; i++)
			pits[i] = new MancalaPitGraphics();

		setPreferredSize(new Dimension(width, height));

		// Draw the initial board without pits
		board = new RoundRectangle2D.Double(pad, pad, width - pad * 2, height - pad * 2, 0, 0);

		// Draw big pits
		double pitPad = 10;
		double pitWidth = board.getWidth() / 8 - 2 * pitPad;
		double bigPitHeight = board.getHeight() - 2 * pitPad;
		pits[6].setOuterBound(new RoundRectangle2D.Double(board.getX() + (pitWidth + 2 * pitPad) * 7 + pitPad,
				board.getY() + pitPad, pitWidth, bigPitHeight, pitWidth, pitWidth));
		pits[13].setOuterBound(new RoundRectangle2D.Double(board.getX() + pitPad, board.getY() + pitPad, pitWidth,
				bigPitHeight, pitWidth, pitWidth));

		// Create pits boundaries for placing each center of stone in random positions
		double pitBoundPad = stoneSize / 2;
		double pitBoundWidth = pits[6].getOuterBound().getWidth() - 2 * pitBoundPad;
		double bigPitBoundHeight = pits[6].getOuterBound().getHeight() - 2 * pitBoundPad;
		pits[6].setInnerBound(new RoundRectangle2D.Double(pits[6].getOuterBound().getX() + pitBoundPad,
				pits[6].getOuterBound().getY() + pitBoundPad, pitBoundWidth, bigPitBoundHeight, pitBoundWidth,
				pitBoundWidth));
		pits[13].setInnerBound(new RoundRectangle2D.Double(pits[13].getOuterBound().getX() + pitBoundPad,
				pits[13].getOuterBound().getY() + pitBoundPad, pitBoundWidth, bigPitBoundHeight, pitBoundWidth,
				pitBoundWidth));

		// Draw small pits
		double smallPitHeight = board.getHeight() / 2 - 2 * pitPad;
		for (int i = 0; i < 6; i++)
			pits[i].setOuterBound(new RoundRectangle2D.Double(board.getX() + (pitWidth + 2 * pitPad) * (i + 1) + pitPad,
					board.getY() + smallPitHeight + pitPad * 3, pitWidth, smallPitHeight, pitWidth, pitWidth));
		for (int i = 7; i < 13; i++)
			pits[i].setOuterBound(
					new RoundRectangle2D.Double(board.getX() + (pitWidth + 2 * pitPad) * (13 - i) + pitPad,
							board.getY() + pitPad, pitWidth, smallPitHeight, pitWidth, pitWidth));

		// Create big pits boundaries for placing each center of stone in random
		// positions
		double smallPitBoundHeight = pits[0].getOuterBound().getHeight() - 2 * pitBoundPad;
		for (int i = 0; i < 6; i++)
			pits[i].setInnerBound(new RoundRectangle2D.Double(pits[i].getOuterBound().getX() + pitBoundPad,
					pits[i].getOuterBound().getY() + pitBoundPad, pitBoundWidth, smallPitBoundHeight, pitBoundWidth,
					pitBoundWidth));
		for (int i = 7; i < 13; i++)
			pits[i].setInnerBound(new RoundRectangle2D.Double(pits[i].getOuterBound().getX() + pitBoundPad,
					pits[i].getOuterBound().getY() + pitBoundPad, pitBoundWidth, smallPitBoundHeight, pitBoundWidth,
					pitBoundWidth));

		// Make the corners look more natural
		board.setRoundRect(pad, pad, width - pad * 2, height - pad * 2, pitWidth + pad, pitWidth + pad);

		// Listen for any mouse actions
		addMouseListener(this);
		addMouseMotionListener(this);

		// Make all drawing be done in memory first
		setDoubleBuffered(true);

		// Timer for animation
		timer = new Timer(DELAY, this);
		timer.start();

	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(176, 136, 87));
		g2.draw(board);

		// Pits on current player border should thicken on that player's turn
		float thickness = 3;
		Stroke oldStroke = g2.getStroke();

		// Color the board
		g2.fill(board);
		g2.setColor(new Color(142, 106, 63));
		g2.fill(pits[6].getOuterBound());
		g2.fill(pits[13].getOuterBound());
		for (int i = 0; i < 6; i++) {
			if (gameStarted && !isBoardStillAnimating() && model.getPlayerTurn() == 'A' && pitNoHighlight == i
					&& !pits[i].getStones().isEmpty())
				g2.setColor(new Color(117, 81, 38));
			g2.fill(pits[i].getOuterBound());
			g2.setColor(new Color(142, 106, 63));
		}
		for (int i = 7; i < 13; i++) {
			if (gameStarted && !isBoardStillAnimating() && model.getPlayerTurn() == 'B' && pitNoHighlight == i
					&& !pits[i].getStones().isEmpty())
				g2.setColor(new Color(117, 81, 38));
			g2.fill(pits[i].getOuterBound());
			g2.setColor(new Color(142, 106, 63));
		}

		// Draw the outlines of the board
		if (hasOutlines) {
			g2.setColor(Color.BLACK);
			g2.draw(board);
			g2.draw(pits[6].getOuterBound());
			g2.draw(pits[13].getOuterBound());
			for (int i = 0; i < 6; i++) {
				if (gameStarted && !isBoardStillAnimating() && model.getPlayerTurn() == 'A'
						&& !pits[i].getStones().isEmpty())
					g2.setStroke(new BasicStroke(thickness));
				g2.draw(pits[i].getOuterBound());
				g2.setStroke(oldStroke);
			}
			for (int i = 7; i < 13; i++) {
				if (gameStarted && !isBoardStillAnimating() && model.getPlayerTurn() == 'B'
						&& !pits[i].getStones().isEmpty())
					g2.setStroke(new BasicStroke(thickness));
				g2.draw(pits[i].getOuterBound());
				g2.setStroke(oldStroke);
			}
		}

		// Draw stones
		for (int i = 0; i < 14; i++)
			for (MancalaStoneGraphics stone : pits[i].getStones()) {
				g2.setColor(stone.getColor());
				g2.fill(new Ellipse2D.Double(stone.getX(), stone.getY(), stoneSize, stoneSize));
				g2.setColor(Color.BLACK);
				g2.draw(new Ellipse2D.Double(stone.getX(), stone.getY(), stoneSize, stoneSize));
			}

	}

	/**
	 * Place a stone in a random position in a hole.
	 * 
	 * @param stone
	 * @param shape
	 */
	public void randomizePosition(MancalaStoneGraphics stone, RoundRectangle2D shape, boolean animate) {

		Random rand = new Random();
		double randX = shape.getMinX() + (shape.getMaxX() - shape.getMinX()) * rand.nextDouble();
		double randY = shape.getMinY() + (shape.getMaxY() - shape.getMinY()) * rand.nextDouble();

		// If the stone is not inside the shape, randomize again
		while (!shape.contains(randX, randY)) {
			randX = shape.getMinX() + (shape.getMaxX() - shape.getMinX()) * rand.nextDouble();
			randY = shape.getMinY() + (shape.getMaxY() - shape.getMinY()) * rand.nextDouble();
		}

		// The stone position is located at the upper left corner
		randX -= stoneSize / 2;
		randY -= stoneSize / 2;

		if (animate) {
			stone.queueAnimating(randX, randY);
		} else {
			stone.setX(randX);
			stone.setY(randY);
			stone.setRandX(randX);
			stone.setRandY(randY);
		}

	}

	/**
	 * Randomize all stone positions. Need to make sure each stones are not too
	 * close to each other.
	 */
	public void randomizeAllPositions() {

		for (int i = 0; i < 13; i++) {
			if (pits[i].getStones().size() > 0) {
				randomizePosition(pits[i].getStones().get(0), pits[i].getInnerBound(), false);
				for (int j = 1; j < pits[i].getStones().size(); j++) {
					MancalaStoneGraphics stone1 = pits[i].getStones().get(j);
					boolean tooClose = true;
					while (tooClose) {
						randomizePosition(stone1, pits[i].getInnerBound(), false);
						tooClose = false;
						for (int k = 0; k < j; k++) {
							MancalaStoneGraphics stone2 = pits[i].getStones().get(k);
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
	 * Calculate distance between final positions of stones.
	 * 
	 * @param stone1
	 * @param stone2
	 * @return the distance between stones
	 */
	public double distanceFinal(MancalaStoneGraphics stone1, MancalaStoneGraphics stone2) {
		return Math.sqrt(Math.pow(stone1.getRandX() - stone2.getRandX(), 2)
				+ Math.pow(stone1.getRandY() - stone2.getRandY(), 2));
	}

	/**
	 * Calculate distance between two points
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return the distance
	 */
	public double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	/**
	 * Clear all stones on the viewer.
	 */
	public void clearStones() {
		for (MancalaPitGraphics holeGraphics : pits)
			holeGraphics.getStones().clear();
	}

	/**
	 * Populate the stones on the pits based on the model.
	 * 
	 * @param pits
	 *            the pits from the model.
	 */
	public void populateStones(ArrayList<LinkedList<Integer>> pits) {
		for (int i = 0; i < pits.size(); i++)
			for (int j = 0; j < pits.get(i).size(); j++) {
				MancalaStoneGraphics msg = new MancalaStoneGraphics(colors[pits.get(i).get(j) % pits.get(i).size()], i);
				this.pits[i].getStones().add(msg);
				stones.put(pits.get(i).get(j), msg);
			}

	}

	/**
	 * Randomize all stone positions which the stones that have moved. Need to check
	 * to make sure the stones are not too close to each other.
	 */
	public void updateStonePositions() {

		// First check to see if the model has any stones in the board...
		if (model.getStones() != null) {

			MancalaStoneGraphics stone1 = null;

			// Check if there is a sequence from the model to animate the stones one at a
			// time
			if (!model.getStoneSequence().isEmpty()) {

				// Get the stone number (i) and its graphics info
				int i = model.getStoneSequence().peek().getLeft();
				stone1 = stones.get(i);

				// Remove the stone from the previous pit and put it in the next pit
				pits[model.getStoneSequence().peek().getRight()].getStones().add(stone1);
				pits[stone1.getPit()].getStones().remove(stone1);

				// Update the stone's pit number
				stone1.setPit(model.getStoneSequence().peek().getRight());

				// Dequeue the stone from the queue
				model.getStoneSequence().poll();

				// Tell the stone to start animating
				stone1.setAnimating(true);

				// Randomize the stone position in its next pit while check to make sure that it
				// is not too close to other pits
				boolean tooClose = true;
				while (tooClose) {
					randomizePosition(stone1, pits[stone1.getPit()].getInnerBound(), true);
					tooClose = false;
					for (int k = 0; k < pits[stone1.getPit()].getStones().size(); k++) {
						MancalaStoneGraphics stone2 = pits[stone1.getPit()].getStones().get(k);
						if (stone1 == stone2)
							continue;
						if (distanceFinal(stone1, stone2) < stoneSize / 2) {
							tooClose = true;
							break;
						}
					}
				}

			}

			// Otherwise animate the rest of the stones at same time
			else {
				
				// Go through each stone
				for (int i = 0; i < model.getStones().length; i++) {

					// Check if the stone have not been updated yet
					if (model.getStones()[i] != stones.get(i).getPit()) {

						// Get the stone's graphic info
						stone1 = stones.get(i);

						// Remove the stone from the previous pit and put it in the next pit
						pits[model.getStones()[i]].getStones().add(stone1);
						pits[stone1.getPit()].getStones().remove(stone1);

						// Update the stone's pit number
						stone1.setPit(model.getStones()[i]);

						// Tell the stones to start animating
						stone1.setAnimating(true);

						// Randomize the stone position in its next pit while check to make sure that it
						// is not too close to other pits
						boolean tooClose = true;
						while (tooClose) {
							randomizePosition(stone1, pits[stone1.getPit()].getInnerBound(), true);
							tooClose = false;
							for (int k = 0; k < pits[stone1.getPit()].getStones().size(); k++) {
								MancalaStoneGraphics stone2 = pits[stone1.getPit()].getStones().get(k);
								if (stone1 == stone2)
									continue;
								if (distanceFinal(stone1, stone2) < stoneSize / 2) {
									tooClose = true;
									break;
								}
							}
						}

					}
				}
			}

		}
	}

	/**
	 * Check if the stones are still moving around.
	 */
	public boolean isBoardStillAnimating() {
		for (MancalaStoneGraphics stone : stones.values())
			if (stone.isAnimating())
				return true;
		return false;
	}

	// Getters and Setters

	public boolean isGameStarted() {
		return gameStarted;
	}

	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent event) {

		// Then get the pit number the user clicked on and sow it
		Point point = event.getPoint();
		for (int i = 0; i < 14; i++)
			if (!isBoardStillAnimating() && pits[i].getOuterBound().contains(point)) {
				model.sow(i);
				updateStonePositions();
				repaint();
				break;
			}

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent event) {
		// TODO Auto-generated method stub
		Point point = event.getPoint();
		int pitNo = -1;
		for (int i = 0; i < 14; i++) {
			if (!isBoardStillAnimating() && pits[i].getOuterBound().contains(point)) {
				pitNo = i;
				break;
			}
		}
		pitNoHighlight = pitNo;
		repaint();

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		for (Map.Entry<Integer, MancalaStoneGraphics> entry : stones.entrySet()) {
			
			int stoneNo = entry.getKey();
			MancalaStoneGraphics stone = entry.getValue();

			// Check if any of the stones are animating
			if (stone.isAnimating()) {
				if (stone.getDiffX() * (stone.getRandX() - stone.getNextX()) > 0
						&& stone.getDiffY() * (stone.getRandY() - stone.getNextY()) > 0) {
					stone.setX(stone.getNextX());
					stone.setY(stone.getNextY());
					stone.setNextX(stone.getNextX() + stone.getSegmentX());
					stone.setNextY(stone.getNextY() + stone.getSegmentY());
				} else {
//					System.out.printf("Placed stone %d in pit %d.%n", stoneNo, stone.getPit());
					// Put the stones in its final position
					stone.setX(stone.getRandX());
					stone.setY(stone.getRandY());
					stone.setAnimating(false);
					// stone.setWaitingToAnimate(false);
//					if (isBoardStillAnimating())
						updateStonePositions();
				}
				repaint();
			}

		}

	}

}
