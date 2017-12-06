import java.util.EnumSet;

/**
 * Enumerator of pits
 * 
 * @author Vincent Stowbunenko
 *
 */
public enum Pit {
	A1, A2, A3, A4, A5, A6, MANCALA_A, B1, B2, B3, B4, B5, B6, MANCALA_B;
	public static final EnumSet<Pit> mancalas = EnumSet.of(MANCALA_A, MANCALA_B);
	public static final EnumSet<Pit> smallPits = EnumSet.complementOf(mancalas);
	public static final EnumSet<Pit> sideAPits = EnumSet.range(A1, A6);
	public static final EnumSet<Pit> sideBPits = EnumSet.range(B1, B6);

}
