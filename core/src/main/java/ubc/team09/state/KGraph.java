package ubc.team09.state;

import ubc.team09.bitboard.BitBoard;
import ubc.team09.bitboard.P;

/**
 * This class includes operations that model the board state as a graph, where
 * <br />
 * - vertices are board positions, and <br />
 * - two vertices <code>v</code> and <code>u</code> are adjacent if and only
 * if a king could move from one to the other in a single move.<br />
 * <br />
 * The king-move definition of edges is the reason for the "K" in the name.
 * <hr />
 * See {@link QGraph QGraph}
 */
public class KGraph {

	private KGraph() {}

	/**
	 * Returns a bitboard with flags for each position adjacent to 
	 * <code>position</code>.
	 */
	public static long[] neighbors(byte position, long[] occupancy) {
		long[] adjacentSquares = P.kingAttack[position];
		long[] neighbors = {
			adjacentSquares[0] & ~occupancy[0],
			adjacentSquares[1] & ~occupancy[1]
		};

		return neighbors;
	}

	/**
	 * Given a set of origins (on a bitboard) and an occupancy board, this
	 * returns a board where each position is flagged if and only if it is
	 * adjacent to any one of the origins.
	 * 
	 * @param originalPositions A bitboard with each possible origin flagged.
	 * @param occupancy A bitboard with each occupied square flagged.
	 */
	public static long[] neighbors(
		long[] originalPositions,
		long[] occupancy
	) {
		long[] positions = BitBoard.copy(originalPositions);
		long[] domain = BitBoard.create();

		for (
			byte position = BitBoard.poll(positions); 
			position != -1; 
			position = BitBoard.poll(positions)
		) {
			long[] adjacent = neighbors(position, occupancy);

			domain[0] |= adjacent[0];
			domain[1] |= adjacent[1];
		}

		return domain;
	}
}
