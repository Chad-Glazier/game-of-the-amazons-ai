package ubc.team09.player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sfs2x.client.entities.Room;
import ubc.team09.state.BoardState;
import ubc.team09.state.C;
import ubc.team09.state.Move;
import ubc.team09.state.State;
import ubc.team09.view.Ansi;
import ubc.team09.view.Display;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * This class handles communication with the game server. It holds no logic
 * for making decisions; all game decisions are made by the VI that it is
 * assigned.
 */
public class Player extends GamePlayer {

	//
	// Fields that have to be populated by server events.
	//

	private GameClient client = null;
	private Room room = null;
	private String opponentUsername = "Unknown";
	private byte ourColor = -1;

	//
	// Fields that have to be initialized.
	//

	private final String username;
	private final String password = "password";
	private final VI vi;
	/** The player who should make the first move. */
	private final byte startingColor;
	/** The board state. */
	private BoardState state = BoardState.initial();
	/** The time limit for making a move. */
	private final int timeLimit;

	//
	// Recorded values used for displaying info or debugging.
	//
	
	private final LinkedList<String> systemMessages = new LinkedList<>();
	private final ArrayList<Integer> moveHistory;

	/**
	 * Creates a new game player which consults the given <code>VI</code> for
	 * decisions.
	 * 
	 * @param username The username assigned to this player.
	 * @param vi The VI to consult for move recommendations.
	 * @param startingColor Indicates which color will move first (in case, for
	 * some reason, Black is supposed to move first).
	 * @param timeLimit The time limit per turn, in seconds.
	 */
	public Player(String username, VI vi, byte startingColor, int timeLimit) {

		this.username = username;
		this.startingColor = startingColor;
		this.timeLimit = timeLimit;
		this.vi = vi;

		this.vi.setTimeLimit(timeLimit);

		moveHistory = new ArrayList<Integer>(92);
		moveHistory.add(0);
	}

	private void display() {

		String whiteName, blackName;
		if (ourColor == C.WHITE) {
			whiteName = username;
			blackName = opponentUsername;
		} else {
			whiteName = opponentUsername;
			blackName = username;
		}

		Display.printGameBoard(
			state, 
			moveHistory.get(moveHistory.size() - 1), 
			this.username + " vs " + opponentUsername,
			blackName,
			whiteName,
			systemMessages
		);
	}

	private void displayText(String message) {
		Display.printText(0, message);
	}

	private State fullState(byte activePlayer) {
		return new State(
			state.occupancy,
			state.queens,
			activePlayer,
			moveHistory.get(moveHistory.size() - 1)
		);
	}

	private boolean handleBoardMessage(Map<String, Object> details) {

		// Parse the received board state, and ensure that it's coherent.
		BoardState received = Util.parseState(details);
		if (received == null) {
			handleMessage(
				"Client",
				"Incoherent board received; Ignoring."
			);
			display();
			return false;
		}

		// Check that the received board state is consistent with the state we
		// have locally.
		if (received.equals(state)) {
			handleMessage(
				"Client",
				"Consistent board received."
			);
			display();
			return true;
		}

		// If the received state isn't consistent with our local copy, then we
		// update our local state to match it.
		state = received;
		handleMessage(
			"Client",
			"Resolving inconsistency."
		);
		display();
		return true;
	}

	private boolean handleStartMessage(Map<String, Object> details) {

		boolean ok = true;

		// Handle the new board state received from the server, if one was
		// included.
		if (details.get(AmazonsGameMessage.GAME_STATE) != null) {
			ok = handleBoardMessage(details);
		}

		//
		// Assign player colors/usernames.
		//

		String black = (String) details.get(AmazonsGameMessage.PLAYER_BLACK);
		String white = (String) details.get(AmazonsGameMessage.PLAYER_WHITE);

		if (white.equalsIgnoreCase(username)) {
			ourColor = C.WHITE;
			opponentUsername = black;
		} else if (black.equalsIgnoreCase(username)) {
			ourColor = C.BLACK;
			opponentUsername = white;
		} else {
			handleMessage(
				"Client",
				"Username not recognized by server.");
			display();
			return false;
		}

		// We print the starting board.
		handleMessage("Client", "Game start.");
		display();
		displayText("Waiting for " + opponentUsername + "...");

		// We initialize the VI's settings.
		vi.setColor(ourColor)
		  .setTimeLimit(timeLimit - 1);

		// If we don't move first, then we stop here.
		if (startingColor != ourColor) {
			return ok;
		}

		// Otherwise, we start.
		int ourMove = vi.consult(fullState(ourColor));

		// We update our local board to reflect our new move and send the
		// move to the server.
		sendMove(ourMove);

		return ok;
	}

	private boolean handleMoveMessage(Map<String, Object> details) {

		// Parse the move and determine whether or not it is legal.
		// If it isn't, then it is logged and ignored.
		int move = Util.parseMove(state, details);
		if (!Move.isLegal(state, move)) {
			handleMessage(
				"Client",
				"Incoherent move received; Ignoring."
			);
			display();
			return false;
		}

		// If the move is legal, then the current board state is updated.
		moveHistory.add(move);
		state = new BoardState(state, move);
		handleMessage(
			"Client",
			"Move received. Board updated."
		);
		display();

		// If the state is terminal, then we lose.
		if (state.isTerminal(ourColor)) {
			Display.printText(0, opponentUsername + " wins.\n");
			System.exit(0);
			return true;
		}

		// If the new state isn't terminal, then the VI is consulted for a
		// response.
		int ourMove = vi.consult(fullState(ourColor));
		sendMove(ourMove);

		return true;
	}

	private void sendMove(int move) {

		// We update our local board to reflect our new move.
		moveHistory.add(move);
		state = new BoardState(state, move);
		handleMessage(
			"Client",
			"Sending move to server."
		);
		display();
		displayText("Waiting for " + opponentUsername + "...");

		// Send the move to the server.
		getGameClient().sendMoveMessage(
			Util.getStartPosition(move),
			Util.getEndPosition(move),
			Util.getArrowPosition(move)
		);

		// Check if we won.
		if (state.isTerminal(ourColor == C.WHITE ? C.BLACK : C.WHITE)) {
			displayText(username + " wins.");
			try {
				Thread.sleep(3000);
				System.exit(0);
			} catch (InterruptedException e) {
				System.exit(0);
			}
		}
	}

	//
	// Methods to implement GamePlayer.
	//

	@Override
	public void connect() {
		client = new GameClient(username, password, this);
	}

	@Override
	public GameClient getGameClient() {
		return client;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		return null;
	}

	@Override
	public boolean handleMessage(String msgType, String msg) {
		// the server spams this message for some reason, so we just ignore it.
		if (msgType.equals("userCountChange")) {
			return true;
		}

		systemMessages.push(msgType + (msg != null ? ": " + msg : ""));

		return true;
	}

	@Override
	public boolean handleGameMessage(
			String msgType, Map<String, Object> details) {
		switch (msgType) {
		case GameMessage.GAME_STATE_BOARD:
			return handleBoardMessage(details);
		case GameMessage.GAME_ACTION_START:
			return handleStartMessage(details);
		case GameMessage.GAME_ACTION_MOVE:
			return handleMoveMessage(details);
		default:
			handleMessage("Unhandled", msgType);
			display();
			return true;
		}
	}

	@Override
	public void onLogin() {
		displayText("Logged in.");

		List<Room> rooms = client.getRoomList();
		List<String> roomNames = new LinkedList<String>();
		for (Room room : rooms) {
			roomNames.add(
				room.getName() +
				Ansi.FG_BRIGHT_BLACK +
				" (" + 
				Integer.toString(
					room.getUserCount() - room.getSpectatorCount()
				) +
				"/" + 
				Integer.toString(
					room.getMaxUsers() - room.getMaxSpectators()
				) +
				")" +
				Ansi.RESET
			);
		}

		String roomName = Display.prompt("Choose a room.", roomNames);

		for (Room room : rooms) {
			if (roomName.startsWith(room.getName())) {
				this.room = room;
			}
		}

		Display.clear();
		displayText("Joining room " + room.getName() + "...");

		client.joinRoom(room.getName());
	}

	@Override
	public String userName() {
		return getGameClient().getUserName();
	}
}
