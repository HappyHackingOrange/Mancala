import javax.swing.SwingUtilities;

/**
 * Test and run the Mancala game using MVC model.
 * 
 * @author Vincent Stowbunenko
 *
 */
public class MancalaTest implements Runnable{

	@Override
	public void run() {

		MancalaModel model = new MancalaModel();
		MancalaView view = new MancalaView(model);
		
	}

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new MancalaTest());

	}

}
