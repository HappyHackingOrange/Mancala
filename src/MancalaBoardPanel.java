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
public class MancalaBoardPanel extends JPanel implements ActionListener {

	// Constants
	private static final long serialVersionUID = 1L;
	private static final int DELAY = 11;
	private static final Color[] colors = new Color[] { new Color(0xF16A70), new Color(0xB1D877), new Color(0x8CDCDA),
			new Color(0x4D4D4D) };

	// Instance variables
	private MancalaModel model;
	private EnumMap<Pit, MancalaPitGraphics> pitGraphicsMap;
	private Map<Stone, Tuple<MancalaStoneGraphics>> stoneGraphicsMap;
	private boolean gameStarted;
	private Pit pitNoHighlight; // the pit to highlight when mouse is over the pit
	private Timer timer;
	private MancalaBoardPanel previousState;
	private MancalaBoardFormatter boardFormatter;
	private JLabel statusLabel;
	private Player playerTurn;

	// Constructors
	public MancalaBoardPanel(MancalaModel model, MancalaBoardFormatter boardFormatter, JLabel statusLabel) {

		this.model = model;
		pitGraphicsMap = new EnumMap<>(Pit.class);
		for (Pit pit : Pit.values())
			pitGraphicsMap.put(pit, new MancalaPitGraphics());
		stoneGraphicsMap = new HashMap<>();
		gameStarted = false;
		pitNoHighlight = null;
		previousState = null;
		this.boardFormatter = boardFormatter;
		this.statusLabel = statusLabel;

		// Call the strategy pattern to draw a specific style of the board
		boardFormatter.setBoardPanel(this);
		boardFormatter.createShapes();

		// Listen for any mouse actions
		addMouseListener(new MouseReleasedListener());
		addMouseMotionListener(new MouseMovedListener());

		// Timer for animation
		timer = new Timer(DELAY, this);
		timer.start();

	}

	// Copy-constructor. Used to save states.
	public MancalaBoardPanel(MancalaBoardPanel boardPanel) {
		model = boardPanel.model;
		boardFormatter = boardPanel.boardFormatter.cloneThis();
		boardFormatter.setBoardPanel(this);
		boardFormatter.setPreferredSize();
		statusLabel = boardPanel.statusLabel;
		pitGraphicsMap = new EnumMap<>(Pit.class);
		for (Pit pit : Pit.values())
			pitGraphicsMap.put(pit, new MancalaPitGraphics(boardPanel.pitGraphicsMap.get(pit)));
		stoneGraphicsMap = new HashMap<Stone, Tuple<MancalaStoneGraphics>>();
		for (Map.Entry<Stone, Tuple<MancalaStoneGraphics>> entry : boardPanel.stoneGraphicsMap.entrySet()) {
			Stone stone = entry.getKey();
			Tuple<MancalaStoneGraphics> tuple = entry.getValue();
			Tuple<MancalaStoneGraphics> newTuple = new Tuple<>(tuple);
			newTuple.setStoneComponent(new MancalaStoneGraphics(tuple.getStoneComponent()));
			stoneGraphicsMap.put(stone, newTuple);
		}
		gameStarted = boardPanel.gameStarted;
		previousState = null;
		playerTurn = boardPanel.playerTurn;
		addMouseListener(new MouseReleasedListener());
		addMouseMotionListener(new MouseMovedListener());
		timer = new Timer(DELAY, this);
	}

	// Getters and Setters

	public boolean isGameStarted() {
		return gameStarted;
	}

	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}

	public MancalaBoardPanel getPreviousState() {
		return previousState;
	}

	public Timer getTimer() {
		return timer;
	}

	public EnumMap<Pit, MancalaPitGraphics> getPitGraphicsMap() {
		return pitGraphicsMap;
	}

	public MancalaBoardFormatter getBoardFormatter() {
		return boardFormatter;
	}

	public void setBoardFormatter(MancalaBoardFormatter boardFormatter) {
		this.boardFormatter = boardFormatter;
	}

	public MancalaModel getModel() {
		return model;
	}

	public Pit getPitNoHighlight() {
		return pitNoHighlight;
	}

	public Map<Stone, Tuple<MancalaStoneGraphics>> getStoneGraphicsMap() {
		return stoneGraphicsMap;
	}

	/**
	 * Set up graphics for a new game.
	 */
	public void setupGraphics() {
		emptyAllPitGraphics();
		populateStones(model.getState().getPitMap());
		randomizeAllPositions();
	}

	/**
	 * Randomize all stone positions. Need to make sure each stones are not too
	 * close to each other. The stones will not animate.
	 */
	public void randomizeAllPositions() {

		// Look througth all pits
		for (Pit pit : Pit.values())

			// Check if the pit is not empty
			if (!pitGraphicsMap.get(pit).getStoneList().isEmpty()) {

				// Randomize first stone position
				randomizePosition(pitGraphicsMap.get(pit).getStoneList().get(0),
						pitGraphicsMap.get(pit).getInnerBound(), false);

				// Randomize the next stone position, making sure it is not too close to the
				// previous randomized stones.
				for (int j = 1; j < pitGraphicsMap.get(pit).getStoneList().size(); j++) {
					Stone stone1 = pitGraphicsMap.get(pit).getStoneList().get(j);
					if (boardFormatter instanceof MancalaBoardEggCarton) {
						randomizePosition(stone1, pitGraphicsMap.get(pit).getInnerBound(), false);
					} else {
						boolean tooClose = true;
						while (tooClose) {
							randomizePosition(stone1, pitGraphicsMap.get(pit).getInnerBound(), false);
							tooClose = false;
							for (int k = 0; k < j; k++) {
								Stone stone2 = pitGraphicsMap.get(pit).getStoneList().get(k);
								if (distance(stone1, stone2) < boardFormatter.getStoneSize() / 2) {
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
	 * Place a stone in a random position in a hole. Also has an option to whether
	 * animate this stone or not.
	 * 
	 * @param stone
	 * @param shape
	 * @param animate
	 */
	public void randomizePosition(Stone stone, RectangularShape shape, boolean animate) {

		Random rand = new Random();
		double randX = shape.getMinX() + (shape.getMaxX() - shape.getMinX()) * rand.nextDouble();
		double randY = shape.getMinY() + (shape.getMaxY() - shape.getMinY()) * rand.nextDouble();

		// If the stone is not inside the shape, randomize again
		while (!shape.contains(randX, randY)) {
			randX = shape.getMinX() + (shape.getMaxX() - shape.getMinX()) * rand.nextDouble();
			randY = shape.getMinY() + (shape.getMaxY() - shape.getMinY()) * rand.nextDouble();
		}

		// The stone position is located at the upper left corner
		randX -= boardFormatter.getStoneSize() / 2;
		randY -= boardFormatter.getStoneSize() / 2;

		if (animate) {
			stoneGraphicsMap.get(stone).getStoneComponent().queueAnimating(randX, randY);
		} else {
			stoneGraphicsMap.get(stone).getStoneComponent().setX(randX);
			stoneGraphicsMap.get(stone).getStoneComponent().setY(randY);
			stoneGraphicsMap.get(stone).getStoneComponent().setRandX(randX);
			stoneGraphicsMap.get(stone).getStoneComponent().setRandY(randY);
		}

	}

	/**
	 * Calculate distance between stones.
	 * 
	 * @param stone1
	 * @param stone2
	 * @return the distance between stones
	 */
	public double distance(Stone stone1, Stone stone2) {
		MancalaStoneGraphics stoneGraphics1 = stoneGraphicsMap.get(stone1).getStoneComponent();
		MancalaStoneGraphics stoneGraphics2 = stoneGraphicsMap.get(stone2).getStoneComponent();
		return Math.sqrt(Math.pow(stoneGraphics1.getX() - stoneGraphics2.getX(), 2)
				+ Math.pow(stoneGraphics1.getY() - stoneGraphics2.getY(), 2));
	}

	/**
	 * Calculate distance between final positions of stones.
	 * 
	 * @param stone1
	 * @param stone2
	 * @return the distance between stones
	 */
	public double distanceFinal(Stone stone1, Stone stone2) {
		MancalaStoneGraphics stoneGraphics1 = stoneGraphicsMap.get(stone1).getStoneComponent();
		MancalaStoneGraphics stoneGraphics2 = stoneGraphicsMap.get(stone2).getStoneComponent();
		return Math.sqrt(Math.pow(stoneGraphics1.getRandX() - stoneGraphics2.getRandX(), 2)
				+ Math.pow(stoneGraphics1.getRandY() - stoneGraphics2.getRandY(), 2));
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
	 * Add a stone graphics to the pit graphic and update stone's pit info.
	 * 
	 * @param pit
	 * @param stone
	 */
	public void offerStone(Pit pit, Stone stone) {
		pitGraphicsMap.get(pit).getStoneList().offer(stone);
		stoneGraphicsMap.get(stone).setPit(pit);
	}

	/**
	 * Poll a stone graphics from the pit graphics and update stone's pit info.
	 */
	public Stone pollStone(Pit pit) {
		Stone stone = pitGraphicsMap.get(pit).getStoneList().poll();
		stoneGraphicsMap.get(stone).setPit(pit);
		return stone;
	}

	/**
	 * Remove a stone graphic from the pit graphics and update stone's pit info.
	 */
	public Stone removeStone(Pit pit, Stone stone) {
		pitGraphicsMap.get(pit).getStoneList().remove(stone);
		// stone.setPit(null);
		stoneGraphicsMap.get(stone).setPit(null);
		return stone;
	}

	/**
	 * Empty the stone graphics from the pit graphics.
	 */
	public void emptyPitGraphics(Pit pit) {
		while (!pitGraphicsMap.get(pit).getStoneList().isEmpty())
			pollStone(pit);
	}

	/**
	 * Clear all stones on the viewer.
	 */
	public void emptyAllPitGraphics() {
		for (Pit pit : Pit.values())
			emptyPitGraphics(pit);
		stoneGraphicsMap.clear();
	}

	/**
	 * Populate the stones on the pits based on the model.
	 * 
	 * @param pitMap
	 *            the pit map from the model
	 */
	public void populateStones(EnumMap<Pit, LinkedList<Stone>> pitMap) {
		for (Pit pit : Pit.smallPits)
			for (int i = 0; i < pitMap.get(pit).size(); i++) {
				MancalaStoneGraphics stoneGraphics = new MancalaStoneGraphics(colors[i]);
				Stone stone = pitMap.get(pit).get(i);
				stoneGraphicsMap.put(stone, new Tuple<>(stoneGraphics, pit));
				offerStone(pit, stone);
			}
	}

	/**
	 * Randomize all stone positions which the stones that have moved. Need to check
	 * to make sure the stones are not too close to each other.
	 */
	public void updateStonePositions(boolean animate) {

		// First check to see if the model has a list of stones...
		if (model.getState().getStoneMap() != null) {

			// Check if there is animating sequence from the model
			if (!model.getState().getStoneSequence().isEmpty()) {
				Tuple<Stone> tuple = model.getState().getStoneSequence().poll();
				updateStonePosition(tuple.getStoneComponent(), tuple.getPit(), animate);
			}

			// Otherwise animate the rest of the stones at same time
			else
				for (Map.Entry<Stone, Pit> entry : model.getState().getStoneMap().entrySet()) {
					Stone stone = entry.getKey();
					Pit pit = entry.getValue();
					if (!pit.equals(stoneGraphicsMap.get(stone).getPit()))
						updateStonePosition(stone, pit, animate);
				}
		}
	}

	/**
	 * Update the stone position.
	 * 
	 * @param stone
	 */
	public void updateStonePosition(Stone stone, Pit pit, boolean animate) {

		// Get stone graphics info
		MancalaStoneGraphics stoneGraphics = stoneGraphicsMap.get(stone).getStoneComponent();
		Pit pitPrevious = stoneGraphicsMap.get(stone).getPit();

		// Remove the stone from the previous pit and put it in the next pit
		offerStone(pit, removeStone(pitPrevious, stone));

		// Tell the stone to start animating
		stoneGraphics.setAnimating(animate);

		// Randomize the stone position in its next pit while check to make sure that it
		// is not too close to other pits
		boolean tooClose = true;
		if (boardFormatter instanceof MancalaBoardEggCarton) {
			randomizePosition(stone, pitGraphicsMap.get(pit).getInnerBound(), animate);
		} else {
			while (tooClose) {
				randomizePosition(stone, pitGraphicsMap.get(pit).getInnerBound(), animate);
				tooClose = false;
				for (Stone stoneOther : pitGraphicsMap.get(pit).getStoneList()) {
					if (stone == stoneOther)
						continue;
					if (distanceFinal(stone, stoneOther) < boardFormatter.getStoneSize() / 2) {
						tooClose = true;
						break;
					}
				}
			}
		}

	}

	/**
	 * Check if the stones are still moving around.
	 */
	public boolean isBoardStillAnimating() {
		for (Tuple<MancalaStoneGraphics> tuple : stoneGraphicsMap.values())
			if (tuple.getStoneComponent().isAnimating())
				return true;
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		for (Tuple<MancalaStoneGraphics> tuple : stoneGraphicsMap.values()) {

			MancalaStoneGraphics stoneGraphics = tuple.getStoneComponent();

			// Check if any of the stones are animating
			if (stoneGraphics.isAnimating()) {
				if (stoneGraphics.getDiffX() * (stoneGraphics.getRandX() - stoneGraphics.getNextX()) > 0
						&& stoneGraphics.getDiffY() * (stoneGraphics.getRandY() - stoneGraphics.getNextY()) > 0) {
					stoneGraphics.setX(stoneGraphics.getNextX());
					stoneGraphics.setY(stoneGraphics.getNextY());
					stoneGraphics.setNextX(stoneGraphics.getNextX() + stoneGraphics.getSegmentX());
					stoneGraphics.setNextY(stoneGraphics.getNextY() + stoneGraphics.getSegmentY());
				} else {
					stoneGraphics.setX(stoneGraphics.getRandX());
					stoneGraphics.setY(stoneGraphics.getRandY());
					stoneGraphics.setAnimating(false);
					updateStonePositions(true);
				}
				revalidate();
				repaint();
			}

		}

		// Let the players know the status of the game
		if (isBoardStillAnimating()) {
			if (pitNoHighlight != null)
				pitNoHighlight = null;
			if (boardFormatter instanceof MancalaBoardEggCarton)
				statusLabel.setText(String.format("Player %s is sowing the pennies...", playerTurn));
			else
				statusLabel.setText(String.format("Player %s is sowing the stones...", playerTurn));
		} else if (model.getState().isGameOver()) {
			int playerAScore = model.getState().getPitMap().get(Pit.MANCALA_A).size();
			int playerBScore = model.getState().getPitMap().get(Pit.MANCALA_B).size();
			if (playerAScore > playerBScore)
				statusLabel.setText(String.format("Player %s won the game!", Player.A));
			else if (playerAScore < playerBScore)
				statusLabel.setText(String.format("Player %s won the game!", Player.B));
			else
				statusLabel.setText("Game ended in draw.");
		} else {
			if (isGameStarted())
				statusLabel
						.setText(String.format("It's player %s's turn. Pick a pit.", model.getState().getPlayerTurn()));
			else
				statusLabel.setText("Welcome to the game of Mancala!");
		}

	}

	/**
	 * Updates the board graphics.
	 */
	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// Color, draw outlines of the board, labels, and stones
		boardFormatter.colorBoard(g2);
		boardFormatter.drawBoard(g2);
		boardFormatter.drawLabelsPit(g2);
		boardFormatter.drawStones(g2);
		boardFormatter.drawLabelsNumberOfStonesPerPit(g2);

	}

	/**
	 * For debugging purposes.
	 */
	@Override
	public String toString() {
		StringBuilder strBldr = new StringBuilder();

		// Print out pit graphics map info
		strBldr.append("pitGraphicsMap:\n");
		for (Pit pit : Pit.values()) {
			strBldr.append(String.format("  %s:%n    ", pit));
			for (Stone stone : pitGraphicsMap.get(pit).getStoneList()) {
				MancalaStoneGraphics stoneGraphics = stoneGraphicsMap.get(stone).getStoneComponent();
				strBldr.append(String.format("%x:%s:%s ", stoneGraphics.hashCode(), pit, stoneGraphics.isAnimating()));
			}
			strBldr.append("\n");
		}

		// Print out stone graphics map info
		strBldr.append("stoneMap:\n");
		for (Map.Entry<Stone, Tuple<MancalaStoneGraphics>> entry : stoneGraphicsMap.entrySet())
			strBldr.append(String.format("  %x:%s%n", entry.getValue().hashCode(), entry.getValue().getPit()));

		return strBldr.toString();

	}

	/**
	 * Mouse adapter class to just focus on one listener (do something when the
	 * mouse is released)
	 * 
	 * @author Vincetn Stowbunenko
	 *
	 */
	private class MouseReleasedListener extends MouseAdapter {

		public void mouseReleased(MouseEvent event) {

			// Get the pit number the user clicked on and sow it
			Point point = event.getPoint();
			for (Pit pit : Pit.values())
				if (!isBoardStillAnimating() && !model.getState().getSowablePits().isEmpty()
						&& pitGraphicsMap.get(pit).getOuterBound().contains(point)) {
					playerTurn = model.getState().getPlayerTurn();
					model.getState().sow(pit);
					model.getState().checkIfGameEnded();
					previousState = new MancalaBoardPanel(MancalaBoardPanel.this);
					updateStonePositions(true);
					break;
				}

		}

	}

	/**
	 * Mouse motion adapter class to listen for mouse motions
	 */
	private class MouseMovedListener extends MouseMotionAdapter {

		/**
		 * Pit should darken when a mouse moves over it.
		 */
		public void mouseMoved(MouseEvent event) {
			Point point = event.getPoint();
			Pit pitCurrent = null;
			for (Pit pit : Pit.values()) {
				if (!isBoardStillAnimating() && pitGraphicsMap.get(pit).getOuterBound().contains(point)) {
					pitCurrent = pit;
					break;
				}
			}
			pitNoHighlight = pitCurrent;
			revalidate();
			repaint();
		}

	}

}
