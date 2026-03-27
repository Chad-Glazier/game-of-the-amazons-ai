package ubc.team09.state;

import java.util.Arrays;

import ubc.team09.bitboard.BitBoard;

/**
 * Represents a complete game state.
 */
public class State {
	/**
	 * An occupancy board; i.e., a bitboard that has a flag wherever there is
	 * a queen or an arrow. If a position is not flagged on this board, then
	 * it means that position is empty.
	 */
	public final long[] occupancy = new long[2];
	/**
	 * The position indices of the queens on the board. The first four are
	 * White's queens, and the last four are Black's.
	 */
	public final byte[] queens = new byte[8];
	/**
	 * The player who can make a move from this position.
	 */
	public final byte activePlayer;
	/**
	 * The move that created this board state.
	 * 
	 * See {@link ubc.team09.state.Move Move}
	 */
	public final int move;

	/**
	 * Creates a new game state.
	 * 
	 * @param occupancy A bitboard with each occupied square (i.e., any square
	 * with an arrow or a queen) flagged.
	 * @param queens The position indices (0-99) of the eight queens on the
	 * board. It must be maintained that the first four queens are White's
	 * queens, and the latter four are Black's.
	 * @param activePlayer The player whose turn it is in this state; i.e.,
	 * who will make the next move. Use {@link C#WHITE} or {@link C#BLACK}.
	 * @param move The integer representation of the most recent move. See
	 * {@link Move}.
	 */
	public State(
		long[] occupancy, byte[] queens, byte activePlayer, int move
	) {
		this.activePlayer = activePlayer;
		this.occupancy[0] = occupancy[0];
		this.occupancy[1] = occupancy[1];
		this.queens[0] = queens[0];
		this.queens[1] = queens[1];
		this.queens[2] = queens[2];
		this.queens[3] = queens[3];
		this.queens[4] = queens[4];
		this.queens[5] = queens[5];
		this.queens[6] = queens[6];
		this.queens[7] = queens[7];
		this.move = move;
	}

	/**
	 * Creates a new board state by applying a move to an existing one.
	 * 
	 * @param prev The previous board state. This will not be mutated.
	 * @param move The move to apply to get the new board state. See
	 * {@link Move}.
	 */
	public State(State prev, int move) {
		activePlayer = prev.activePlayer == C.WHITE ? C.BLACK : C.WHITE;
		occupancy[0] = prev.occupancy[0];
		occupancy[1] = prev.occupancy[1];
		queens[0] = prev.queens[0];
		queens[1] = prev.queens[1];
		queens[2] = prev.queens[2];
		queens[3] = prev.queens[3];
		queens[4] = prev.queens[4];
		queens[5] = prev.queens[5];
		queens[6] = prev.queens[6];
		queens[7] = prev.queens[7];
		this.move = move;

		for (byte i = 0; i < 8; i++) {
			if (queens[i] == Move.start(move)) {
				BitBoard.unflag(occupancy, queens[i]);
				queens[i] = Move.end(move);
				BitBoard.flag(occupancy, queens[i]);
				break;
			}
		}

		BitBoard.flag(occupancy, Move.arrow(move));
	}

	/**
	 * Returns a generator which yields all possible child states.
	 */
	public StateGenerator children() {
		return new StateGenerator(this);
	}

	/**
	 * Returns a copy of this state.
	 */
	public State copy() {
		return new State(
				BitBoard.copy(occupancy),
				Arrays.copyOf(queens, 8),
				activePlayer,
				move);
	}

	/**
	 * Updates this board's occupancy and queen positions to match the given
	 * board.
	 */
	public void impose(BoardState newState) {
		occupancy[0] = newState.occupancy[0];
		occupancy[1] = newState.occupancy[1];
		queens[0] = newState.queens[0];
		queens[1] = newState.queens[1];
		queens[2] = newState.queens[2];
		queens[3] = newState.queens[3];
		queens[4] = newState.queens[4];
		queens[5] = newState.queens[5];
		queens[6] = newState.queens[6];
		queens[7] = newState.queens[7];
	}
}
