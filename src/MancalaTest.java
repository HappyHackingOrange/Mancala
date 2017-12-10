import javax.swing.*;

/**
 * Test and run the Mancala game using a MVC model.
 * The code is uploaded in:
 * https://github.com/HappyHackingOrange/Mancala
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaTest implements Runnable{

	@Override
	public void run() {
		new MancalaView(new MancalaModel());
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new MancalaTest());
	}

}
