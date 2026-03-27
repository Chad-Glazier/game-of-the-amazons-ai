/**
 * This package is where the game client is implemented.
 * 
 * <ul>
 * 	<li>{@link VI} defines the general interface for a game engine that is
 * 	used to make decisions.</li>
 * 	<li>{@link Player} implements a client to communicate with the server
 * 	in tournaments. This class parses information from the server, passes it
 * 	onto a {@link VI}, and then sends the VI's response as a message to the
 * 	server.</li>
 * 	<li>{@link EDI} is our flagship VI.
 * </ul>
 */
package ubc.team09.player;
