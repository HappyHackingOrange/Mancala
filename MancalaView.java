import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Displays the GUI frame of a Mancala board.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaView {
	
	private MancalaModel model;
	private MancalaBoard board;

	public MancalaView(MancalaModel model) {
		
		this.model = model;
		
		// A panel with just radio buttons
		JPanel radioPanel = new JPanel();
		JRadioButton rbutton1 = new JRadioButton("3", true);
		JRadioButton rbutton2 = new JRadioButton("4");
		ButtonGroup bg = new ButtonGroup();
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
		Button buttonStart = new Button("Start");
		Button buttonUndo = new Button("Undo");
		buttonStart.setPreferredSize(new Dimension(80,40));
		buttonUndo.setPreferredSize(new Dimension(80,40));
		topLeftPanel.add(buttonStart);
		topLeftPanel.add(buttonUndo);
		
		// Add panel with start and undo buttons at upper left corner
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(topLeftPanel, BorderLayout.WEST);
		topPanel.add(topRightPanel, BorderLayout.EAST);
		
		// Make a new board for the bottom panel
		this.board = new MancalaBoard(model, 1000, 400);

		// Action listeners
		buttonStart.addActionListener( event -> {
			model.emptyHoles();
			model.addInitStones();
			board.randomizeAllPositions();
			board.repaint();
		});

		// Put all panels in one frame
		JFrame frame = new JFrame();
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(board, BorderLayout.CENTER);
		frame.setTitle("Mancala");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null); // Center the window
		frame.setVisible(true);

	}

}
