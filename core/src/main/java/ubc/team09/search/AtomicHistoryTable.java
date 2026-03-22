package ubc.team09.search;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import ubc.team09.state.Move;
import ubc.team09.state.State;

/**
 * An implementation of a history table, identical to {@link HistoryTable}
 * except with atomic operations for thread-safety.
 */
public class AtomicHistoryTable {
	private final AtomicInteger[][][][] scores = 
		new AtomicInteger[2][100][100][100];
	private static final int MAX_HISTORY = 2 << 13;

	public AtomicHistoryTable() {}

	public int score(int move) {
		return scores
			[Move.player(move)]
			[Move.start(move)]
			[Move.end(move)]
			[Move.arrow(move)]
			.get();
	}

	public void increaseScore(int move, int depth) {
		int bonus = Integer.max(
			-MAX_HISTORY, 
			Integer.min(depth * depth, MAX_HISTORY)
		);
		int initial = scores
			[Move.player(move)]
			[Move.start(move)]
			[Move.end(move)]
			[Move.arrow(move)]
			.get();

		scores
			[Move.player(move)]
			[Move.start(move)]
			[Move.end(move)]
			[Move.arrow(move)]
			.getAndAdd(bonus - initial * bonus / MAX_HISTORY);
	}

	public final Comparator<State> descending = new Comparator<State>() {
		public int compare(State s1, State s2) {
			return score(s2.move) - score(s1.move);
		}
	};
}
