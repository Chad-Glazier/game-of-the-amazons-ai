package ubc.team09.player;

import ubc.team09.bitboard.BitBoard;
import ubc.team09.eval.KMinDist;
import ubc.team09.eval.QMinDist;
import ubc.team09.search.AlphaBeta;
import ubc.team09.state.State;

/**
 * The flagship VI for this project. EDI uses a lightweight, single-threaded
 * implementation of alpha-beta search with heavily optimized operations.
 */
public class EDI implements VI {
	/** The turn at which the algorithm shifts from kmindist to qmindist. */
	private final static int INFLECTION_POINT = 55;

	private final AlphaBeta ab = new AlphaBeta();
	private final KMinDist kmindist = new KMinDist();
	private final QMinDist qmindist = new QMinDist();

	public EDI() {
		ab.setShowOutput(true);
	}

	public int consult(State state) {

		ab.setBoard(state);

		int turn = BitBoard.count(state.occupancy) - 8;
		if (turn >= INFLECTION_POINT) {
			ab.setHeuristic(qmindist);
		} else {
			ab.setHeuristic(kmindist);
		}

		return ab.search();
	}

	public EDI setColor(byte color) {
		ab.setColor(color);
		return this;
	}

	public EDI setTimeLimit(int seconds) {
		ab.setTimeLimit(seconds);
		return this;
	}
}
