package ubc.team09.search;

import java.util.Comparator;

import ubc.team09.state.Move;
import ubc.team09.state.State;

/**
 * A history table is used to track which moves have produced cutoffs in the
 * past and increments their score accordingly. When searching the game tree,
 * the history table can be used to order child states. Since this approach
 * only involves table lookups, it's much faster than just about any other 
 * ordering method, and the quality of the ordering has been shown to be far
 * more effective than ordering by evaluation scores.
 * 
 * <hr />
 * <strong>See Also</strong>
 * <ul>
 * 	<li>Schaeffer, J. <em><a href=
 * 	"https://webdocs.cs.ualberta.ca/~jonathan/publications/ai_publications/pami.pdf">The
 * 	History Heuristic and Alpha-Beta Search Enhancements in
 * 	Practice</a></em>.</li>
 * </ul>
 */
public class HistoryTable {
	// All possible moves. The indices correspond to:
	// - the player making the move (0 or 1).
	// - the "from" square.
	// - the "to" square.
	// - the "arrow" square.
	//
	// This table requires roughly 7.8 MB of raw data, plus any overhead that
	// the JVM tacks on.
	private final int[][][][] scores = new int[2][100][100][100];
	/** 
	 * A maximum history score is imposed to ensure that values aren't 
	 * oversaturated.
	 */
	private static final int MAX_HISTORY = 2 << 13;

	public HistoryTable() {}

	/**
	 * Gets the history score of a move.
	 */
	public int score(int move) {
		return scores
			[Move.player(move)]
			[Move.start(move)]
			[Move.end(move)]
			[Move.arrow(move)];
	}

	/**
	 * Used to indicate that a move is sufficient; i.e., it's score should be
	 * increased. This should be used whenever a move produces a cutoff.
	 * 
	 * @param depth The depth at which the cutoff was found. Greater depths
	 * (i.e., those closer to the root) prune more branches, and are thus
	 * given higher history scores.
	 */
	public void increaseScore(int move, int depth) {

		//
		// Below is the history score increment from Shaeffer's 2006 paper.
		//

		// scores
		// [Move.player(move)]
		// [Move.start(move)]
		// [Move.end(move)]
		// [Move.arrow(move)] += 2 << depth;

		//
		// Below is the History Gravity update, from this page:
		// https://www.chessprogramming.org/History_Heuristic
		//
		// The History Gravity formula has the benefit of removing the need for
		// any kind of explicit decay update between searches.
		//

		int bonus = Integer.max(
			-MAX_HISTORY, 
			Integer.min(depth * depth, MAX_HISTORY)
		);
		int initial = scores
			[Move.player(move)]
			[Move.start(move)]
			[Move.end(move)]
			[Move.arrow(move)];

		scores
			[Move.player(move)]
			[Move.start(move)]
			[Move.end(move)]
			[Move.arrow(move)] += bonus - initial * bonus / MAX_HISTORY;
	}

	/**
	 * A comparator for states that orders states in <em>descending</em> order
	 * based on their history scores.
	 */
	public final Comparator<State> descending = new Comparator<State>() {
		public int compare(State s1, State s2) {
			return score(s2.move) - score(s1.move);
		}
	};
}
