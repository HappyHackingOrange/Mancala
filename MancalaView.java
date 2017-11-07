import java.awt.*;
import javax.swing.*;

/**
 * Displays the GUI frame of a Mancala board.
 * @author vxs8122
 *
 */
public class MancalaView{
	
	public MancalaView() {
		
		
		// A panel with just radio buttons
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new FlowLayout());
		JRadioButton rbutton1 = new JRadioButton("3", true);
		JRadioButton rbutton2 = new JRadioButton("4");
		ButtonGroup bg = new ButtonGroup();
		bg.add(rbutton1);
		bg.add(rbutton2);
		radioPanel.add(rbutton1);
		radioPanel.add(rbutton2);
		
		// Top right panel to allow user to choose initial number of stones
		JPanel topRightPanel = new JPanel();
		topRightPanel.setLayout(new BorderLayout());
		topRightPanel.add(new Label("Initial number of stones:"), BorderLayout.NORTH);
		topRightPanel.add(radioPanel, BorderLayout.SOUTH);

		// Add panel with start and undo buttons at upper left corner
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		topPanel.add(new Button("Start"));
		topPanel.add(new Button("Undo"));
		topPanel.add(topRightPanel);
		
		// Put all panels in one frame
		JFrame frame = new JFrame();
		frame.add(topPanel, BorderLayout.NORTH);
		frame.setTitle("Mancala");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
	}
	
}
