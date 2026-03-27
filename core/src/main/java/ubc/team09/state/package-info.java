/**
 * <h1>Game State Representation</h1>
 * 
 * This package contains the classes and methods used for representing the game
 * state and generating child states. The more relevant classes are listed
 * below.
 * 
 * <ul>
 * 	<li>{@link BoardState} contains the minimal information of a board.
 * 	Namely, a bitboard flagging each occupied square (a square is <em>
 * 	occupied</em> if and only if it has either a queen or an arrow on it),
 * 	and some <code>byte[]</code> arrays tracking the position indexes of the
 * 	queens.</li>
 * 	<li>{@link State} contains a more complete game state. It has the same
 * 	fields as {@link BoardState}, but also tracks which player moves next,
 * 	the most recent move taken, and contains methods to generate child states.
 * 	<li>{@link Move} contains a set of methods used for working with moves,
 * 	which are encoded as <code>int</code>s.</li>
 * 	<li>{@link KGraph} and {@link QGraph} contain operations that can be 
 * 	applied to board states and which model such states as graphs where
 * 	edges are defined by king-move adjacency and queen move adjacency,
 * 	respectively.</li>
 * </ul>
 */
package ubc.team09.state;
