import java.awt.*;
import javax.swing.*;

/**
 * Displays the GUI frame of a Mancala board.
 * The action listeners in this are the "controllers" of the MVC model.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaView extends JFrame {

	private MancalaModel model;
	private MancalaBoardPanel board;
	private Button buttonStart;
	private Button buttonUndo;
	ButtonGroup bg;

	public MancalaView(MancalaModel model) {
		
		// Connect the view to the model
		this.model = model;

		// A panel with just radio buttons
		JPanel radioPanel = new JPanel();
		JRadioButton rbutton1 = new JRadioButton("3", true);
		rbutton1.setActionCommand(rbutton1.getText());
		JRadioButton rbutton2 = new JRadioButton("4");
		rbutton2.setActionCommand(rbutton2.getText());
		bg = new ButtonGroup();
		bg.add(rbutton1);
		bg.add(rbutton2);
		radioPanel.add(rbutton1);
		radioPanel.add(rbutton2);

		// Top right panel, merging radio buttons with its label
		JPanel topRightPanel = new JPanel(new BorderLayout());
		topRightPanel.add(new Label("Initial number of stones:"), BorderLayout.NORTH);
		topRightPanel.add(radioPanel, BorderLayout.SOUTH);

		// Top left panel, just all push buttons
		JPanel topLeftPanel = new JPanel();
		buttonStart = new Button("Start");
		buttonStart.addActionListener(event -> {
			model.getState().emptyAllPits();
			model.getState().setPlayerTurn(Player.A);
			model.getState().populatePits(Integer.parseInt(bg.getSelection().getActionCommand()));
			board.clearStones();
			board.populateStones(model.getState().getPitMap());
			board.randomizeAllPositions();
			board.setGameStarted(true);
			board.repaint();
		});
		buttonUndo = new Button("Undo");
		buttonStart.setPreferredSize(new Dimension(80, 40));
		buttonUndo.setPreferredSize(new Dimension(80, 40));
		topLeftPanel.add(buttonStart);
		topLeftPanel.add(buttonUndo);

		// Add panel with start and undo buttons at upper left corner
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(topLeftPanel, BorderLayout.WEST);
		topPanel.add(topRightPanel, BorderLayout.EAST);

		// Make a new board for the bottom panel
		board = new MancalaBoardPanel(1000, 400, model);

		// Put all panels in one frame
		add(topPanel, BorderLayout.NORTH);
		add(board, BorderLayout.CENTER);
		setTitle("Mancala");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null); // Center the window
		setVisible(true);

	}

	public Button getButtonStart() {
		return buttonStart;
	}

	public Button getButtonUndo() {
		return buttonUndo;
	}

	public MancalaBoardPanel getBoard() {
		return board;
	}

}
