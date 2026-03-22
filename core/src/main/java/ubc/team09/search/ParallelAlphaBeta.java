package ubc.team09.search;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import ubc.team09.eval.HeuristicMethod;
import ubc.team09.state.C;
import ubc.team09.state.State;
import ubc.team09.state.StateGenerator;
import ubc.team09.view.Display;

/**
 * This implementation is largely similar to {@link AlphaBeta} and uses all of
 * the same optimizations, except that it is meant to be run on multiple 
 * cores. 
 * 
 * Parallelization of alpha-beta search is not straightforward to implement, as
 * the quality of the pruning largely depends on information gained
 * sequentially (i.e., the alpha/beta cutoff scores). Additionally, this
 * program isn't meant to be run on a high-powered machine with hundreds of
 * cores. With these limitations in mind, I have chosen a straightforward 
 * approach that should, in theory, perform better than sequential Alpha-
 * Beta in all cases.
 */
public class ParallelAlphaBeta
	extends TimeConstrained
	implements SearchMethod {

	// Configuration options.
	/** The maximum depth to search to. */
	private int maxDepth = 92;
	/** The table of history scores. */
	private AtomicHistoryTable history = new AtomicHistoryTable();
	/** The initial board state. */
	private State root;
	/** The player who we want to win. */
	private final byte player;
	/** The heuristic used to evaluate game states. */
	private HeuristicMethod heuristic;
	/** Indicates whether or not we want to see output to the console. */
	private boolean showOutput = false;

	public ParallelAlphaBeta(
		State initialState,
		HeuristicMethod heuristic,
		byte maximizingPlayer
	) {
		this.root = initialState.copy();
		this.player = maximizingPlayer;
		this.heuristic = heuristic;
	}

	//
	// Methods for setting configurations.
	//

	public void setShowOutput(boolean showOutput) {
		this.showOutput = showOutput;
	}

	public void setHeuristic(HeuristicMethod heuristic) {
		this.heuristic = heuristic;
	}

	public void setBoard(State state) {
		this.root = state.copy();
	}

	public void resetHistory() {
		this.history = new AtomicHistoryTable();
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	//
	// The actual search method.
	//

	public int search() {
		startTimer();
		int bestMove = 0;

		try {
			for (int depth = 1; depth < maxDepth; depth++) {
				int bestMoveAtDepth = depthLimitedSearch(depth);
				if (bestMoveAtDepth == 0) {
					break;
				} else {
					bestMove = bestMoveAtDepth;
					if (showOutput) {
						Display.printText(0,
								"Searching at depth " +
										Integer.toString(depth + 1) +
										"...");
					}
				}
			}
		} catch (TimeoutException e) {}

		return bestMove;
	}

	private int depthLimitedSearch(int depth) throws TimeoutException {

		//
		// Note: The heuristic evaluation will return lesser values if a state
		// favors Black, and greater values if a state favors White.
		//

		//
		// Collect all the children.
		//

		ArrayList<State> children = new ArrayList<State>(400);
		StateGenerator iter = root.children();
		for (State child = iter.next(); child != null; child = iter.next()) {
			children.add(child);
		}

		//
		// Check if the state is terminal.
		//

		if (children.isEmpty()) {
			return 0;
		}

		//
		// Order the children based on their history scores.
		//

		children.sort(history.descending);

		//
		// Find the best move.
		//

		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;

		int color = this.player == C.WHITE ? +1 : -1;

		int bestMove = 0;

		for (State child : children) {

			double score = -alphaBeta(
					child, -beta, -alpha, depth - 1, -color);

			if (score > alpha) {
				alpha = score;
				bestMove = child.move;
			}
		}

		return bestMove;
	}

	/**
	 * 
	 * @param state
	 * @param alpha
	 * @param beta
	 * @param depth
	 * @param color <code>-1</code> to find the ideal score for Black, and
	 *              <code>+1</code> to find the ideal score for White.
	 * @return
	 * @throws TimeoutException
	 */
	private double alphaBeta(
			State state,
			double alpha,
			double beta,
			int depth,
			int color) throws TimeoutException {

		//
		// Check the time limit; if time limit is expired, we throw an
		// exception. To save sytem calls, this function only actually checks
		// the time once every ~1000 invocations.
		//

		checkTimeOccasionally();

		//
		// Check if we are maximum depth.
		//

		if (depth == 0) {
			return color * heuristic.evaluate(state);
		}

		//
		// Collect all the children.
		//

		ArrayList<State> children = new ArrayList<State>(400);
		StateGenerator iter = state.children();
		for (State child = iter.next(); child != null; child = iter.next()) {
			children.add(child);
		}

		//
		// Check if the state is terminal.
		//

		if (children.isEmpty()) {
			return color * heuristic.evaluate(state);
		}

		//
		// Sort the children based on their history scores.
		//

		children.sort(history.descending);

		//
		// Find the best score using the negamax implementation of alpha-beta.
		//

		int bestMove = 0;
		double score = Double.NEGATIVE_INFINITY;
		for (State child : children) {

			double result = -alphaBeta(
					child, -beta, -alpha, depth - 1, -color);
			if (result > score) {
				score = result;
			}

			if (score >= beta) {
				bestMove = child.move;
				// update the history score for "sufficient" moves; i.e., ones
				// that cause a cutoff.
				history.increaseScore(bestMove, depth);
				break;
			}

			alpha = Math.max(alpha, score);
		}

		//
		// Return the score.
		//

		return score;
	}
}
