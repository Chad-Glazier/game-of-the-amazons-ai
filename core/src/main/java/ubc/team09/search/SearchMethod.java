package ubc.team09.search;

import ubc.team09.state.State;

/**
 * Defines the interface of a game tree search method.
 */
public interface SearchMethod {
	
	/**
	 * The board state to search from.
	 */
	public void setBoard(State board);

	/**
	 * Sets the a time constraint on the search.
	 */
	public void setTimeLimit(int seconds);

	/**
	 * Sets a time constraint on the search.
	 */
	public void setTimeLimitMs(long milliseconds);

	/**
	 * Indicates that you want the search to print feedback to the console.
	 */
	public void setShowOutput(boolean show);

	/**
	 * Conducts a search of the game tree and yields the best move.
	 * <br /><br />
	 * The move will be encoded as an <code>int</code>. See 
	 * {@link ubc.team09.state.Move Move} for information about how to use
	 * such a value.
	 */
	public int search();
}
