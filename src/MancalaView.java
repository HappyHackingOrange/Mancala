import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * Displays the GUI frame of a Mancala board. The action listeners in this are
 * the "controllers" of the MVC model.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaView extends JFrame {

	// Constants
	private static final long serialVersionUID = 1L;
	private static final Font fontStatus = new Font("SansSerif", Font.BOLD, 36);

	// Instance Variables
	private MancalaBoardPanel boardPanel;
	private JButton buttonStart;
	private JButton buttonUndo;
	private ButtonGroup bg;
	private MancalaBoardFormatter boardFormatter;

	// Constructor
	public MancalaView(MancalaModel model) {

		// JPanel content = new JPanel();
		Box content = Box.createHorizontalBox();

		// Panel with radio buttons to ask user for initial number of stones per pit
		JLabel initStoneLabel = new JLabel("Initial number of stones:");
		Box initStoneLabelBox = Box.createHorizontalBox();
		initStoneLabelBox.add(initStoneLabel);
		initStoneLabelBox.add(Box.createHorizontalGlue());
		Box radioBox = Box.createHorizontalBox();
		JRadioButton rbutton1 = new JRadioButton("3", true);
		rbutton1.setActionCommand(rbutton1.getText());
		JRadioButton rbutton2 = new JRadioButton("4");
		rbutton2.setActionCommand(rbutton2.getText());
		bg = new ButtonGroup();
		bg.add(rbutton1);
		bg.add(rbutton2);
		radioBox.add(Box.createHorizontalGlue());
		radioBox.add(rbutton1);
		radioBox.add(Box.createHorizontalGlue());
		radioBox.add(rbutton2);
		radioBox.add(Box.createHorizontalGlue());

		Box initStoneBox = Box.createVerticalBox();
		initStoneBox.add(initStoneLabelBox);
		initStoneBox.add(radioBox);
		initStoneBox.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Panel with combo box to ask for board type
		JLabel boardTypeLabel = new JLabel("Style:");
		Box boardTypeLabelBox = Box.createHorizontalBox();
		boardTypeLabelBox.add(boardTypeLabel);
		boardTypeLabelBox.add(Box.createHorizontalGlue());

		// Combo Boxes do not behave well with Boxes, need to include an anon class :(
		// Not including anon class will cause the combo box's height to stretch out
		JComboBox boardTypeComboBox = new JComboBox() {
			@Override
			public Dimension getMaximumSize() {
				Dimension max = super.getMaximumSize();
				max.height = getPreferredSize().height;
				return max;
			}
		};
		boardTypeComboBox.addItem("Standard");
		boardTypeComboBox.addItem("Sharp");
		boardTypeComboBox.addItem("Egg Carton");
		boardTypeComboBox.setSelectedIndex(0);
		boardTypeComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					changeBoardStyle((String) boardTypeComboBox.getSelectedItem());
					boardPanel.revalidate();
					boardPanel.repaint();
					pack();
					setLocationRelativeTo(null); // Center the window
				}
			}
		});
		Box boardTypeBox = Box.createVerticalBox();
		boardTypeBox.add(boardTypeLabelBox);
		boardTypeBox.add(boardTypeComboBox);
		boardTypeBox.setBorder(new EmptyBorder(10, 10, 10, 10));

		// User can pick whether to play PvP, Player vs Computer, or Computer vs
		// Computer

		// For player A
		JLabel playerALabel = new JLabel("Player A:");
		Box playerALabelBox = Box.createHorizontalBox();
		playerALabelBox.add(playerALabel);
		playerALabelBox.add(Box.createHorizontalGlue());
		JComboBox playerAComboBox = new JComboBox() {
			@Override
			public Dimension getMaximumSize() {
				Dimension max = super.getMaximumSize();
				max.height = getPreferredSize().height;
				return max;
			}
		};
		playerAComboBox.addItem("Human");
		playerAComboBox.addItem("Computer");
		playerAComboBox.setSelectedIndex(0);
		Box playerABox = Box.createVerticalBox();
		playerABox.add(playerALabelBox);
		playerABox.add(playerAComboBox);
		playerABox.setBorder(new EmptyBorder(10, 10, 10, 10));

		// For player A
		JLabel playerBLabel = new JLabel("Player B:");
		Box playerBLabelBox = Box.createHorizontalBox();
		playerBLabelBox.add(playerBLabel);
		playerBLabelBox.add(Box.createHorizontalGlue());
		JComboBox playerBComboBox = new JComboBox() {
			@Override
			public Dimension getMaximumSize() {
				Dimension max = super.getMaximumSize();
				max.height = getPreferredSize().height;
				return max;
			}
		};
		playerBComboBox.addItem("Human");
		playerBComboBox.addItem("Computer");
		playerBComboBox.setSelectedIndex(1);
		Box playerBBox = Box.createVerticalBox();
		playerBBox.add(playerBLabelBox);
		playerBBox.add(playerBComboBox);
		playerBBox.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Add panel with start and undo buttons at upper left corner
		Box optionsBox = Box.createVerticalBox();
		optionsBox.add(Box.createGlue());
		optionsBox.add(initStoneBox);
		optionsBox.add(Box.createGlue());
		optionsBox.add(boardTypeBox);
		optionsBox.add(Box.createGlue());
		optionsBox.add(playerABox);
		optionsBox.add(Box.createGlue());
		optionsBox.add(playerBBox);
		optionsBox.add(Box.createGlue());
		optionsBox.setBorder(BorderFactory.createTitledBorder("Options"));

		// Label to tell whose turn or the status of the game
		JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
		statusLabel.setFont(fontStatus);
		Box statusLabelBox = Box.createHorizontalBox();
		statusLabelBox.add(statusLabel);

		// Set up default strategy patterns
		boardFormatter = new MancalaBoardStandard();

		// Make a new board at the left panel
		boardPanel = new MancalaBoardPanel(model, boardFormatter, statusLabel);
		boardPanel.setLayout(null);

		// Put board and start/undo button in one box
		Box leftBox = Box.createVerticalBox();
		leftBox.add(boardPanel);

		// Bottom panel, status label and push buttons
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		buttonStart = new JButton("Start");
		buttonStart.addActionListener(event -> {
			model.getState().setupGame(Integer.parseInt(bg.getSelection().getActionCommand()));
			boardPanel.setupGraphics();
			boardPanel.setGameStarted(true);
			boardPanel.repaint();
			String playerAType = (String) playerAComboBox.getSelectedItem();
			switch (playerAType) {
			case ("Human"):
				boardPanel.getPlayerMap().put(Player.A, true);
				break;
			case ("Computer"):
				boardPanel.getPlayerMap().put(Player.A, false);
				break;
			}
			String playerBType = (String) playerBComboBox.getSelectedItem();
			switch (playerBType) {
			case ("Human"):
				boardPanel.getPlayerMap().put(Player.B, true);
				break;
			case ("Computer"):
				boardPanel.getPlayerMap().put(Player.B, false);
				break;
			}
		});
		buttonUndo = new JButton("Undo");
		buttonUndo.addActionListener(event -> {
			if (boardPanel.getPreviousState() != null) {
				model.setState(model.getPreviousState());
				boardPanel.getTimer().stop();
				leftBox.remove(boardPanel);
				leftBox.remove(bottomPanel);
				boardPanel = boardPanel.getPreviousState();
				leftBox.add(boardPanel);
				leftBox.add(bottomPanel);
				boardPanel.getTimer().start();
				changeBoardStyle((String) boardTypeComboBox.getSelectedItem());
				boardPanel.revalidate();
				boardPanel.repaint();
			}
		});
		buttonStart.setPreferredSize(new Dimension(80, 40));
		buttonStart.setMaximumSize(new Dimension(80, 40));
		buttonUndo.setPreferredSize(new Dimension(80, 40));
		buttonUndo.setMaximumSize(new Dimension(80, 40));

		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(statusLabelBox);
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(buttonUndo);
		bottomPanel.add(buttonStart);
		bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		leftBox.add(bottomPanel);

		// Put all panels in one frame
		content.add(leftBox);
		content.add(optionsBox);
		setContentPane(content);
		setTitle("Mancala");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		getRootPane().setDefaultButton(buttonStart);
		buttonStart.requestFocus();
		setLocationRelativeTo(null); // Center the window
		setVisible(true);

	}

	public JButton getButtonStart() {
		return buttonStart;
	}

	public JButton getButtonUndo() {
		return buttonUndo;
	}

	public MancalaBoardPanel getBoard() {
		return boardPanel;
	}

	/**
	 * Change the board style.
	 * 
	 * @param boardStyle
	 */
	public void changeBoardStyle(String boardStyle) {
		boolean boardStyleChanged = false;
		switch (boardStyle) {
		case ("Standard"):
			if (!(boardPanel.getBoardFormatter() instanceof MancalaBoardStandard)) {
				boardStyleChanged = true;
				boardFormatter = new MancalaBoardStandard(boardPanel);
			}
			break;
		case ("Sharp"):
			if (!(boardPanel.getBoardFormatter() instanceof MancalaBoardSharp)) {
				boardStyleChanged = true;
				boardFormatter = new MancalaBoardSharp(boardPanel);
			}
			break;
		case ("Egg Carton"):
			if (!(boardPanel.getBoardFormatter() instanceof MancalaBoardEggCarton)) {
				boardStyleChanged = true;
				boardFormatter = new MancalaBoardEggCarton(boardPanel);
			}
			break;
		}
		if (boardStyleChanged) {
			boardPanel.setBoardFormatter(boardFormatter);
			boardFormatter.createShapes();
			boardPanel.randomizeAllPositions();
		}

	}

}
