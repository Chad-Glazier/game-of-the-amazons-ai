package ubc.cosc322.bitboard;

import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import ubc.cosc322.eval.HeuristicMethod;

public class OrderedState {
	private static final int MAX_CHILDREN = 400;

	private final BitState state;
	private final HeuristicMethod heuristic;
	private final IntAVLTreeSet[] killers;
	private final int depth;

	/**
	 * 
	 * @param activePlayer The player who can make a move from this state;
	 * i.e., the maximizing player. <code>0</code> for White, <code>1</code>
	 * for Black.
	 * @param occupancy
	 * @param queens All eight queens on the board. They must be ordered such
	 * that the indices <code>0</code> through <code>3</code> are White's 
	 * queens, and indices <code>4</code> through <code>7</code> are Black's 
	 * queens. 
	 * @param heuristic The heuristic used to evaluate child board states. This
	 * is used to determine the order in which child nodes are iterated over.
	 * @param killers A set of moves that have been potent in prior iterations
	 * at this ply. These moves will be prioritized above all others.
	 */
	public OrderedState(
		BitState state,
		HeuristicMethod heuristic,
		IntAVLTreeSet[] killers,
		int depth
	) {
		this.state = state;
		this.heuristic = heuristic;
		this.killers = killers;
		this.depth = depth;
	}


}


