package ubc.cosc322.player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sfs2x.client.entities.Room;
import ubc.cosc322.state.C;
import ubc.cosc322.state.State;
import ubc.cosc322.view.Ansi;
import ubc.cosc322.view.Display;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

public class EDI extends GamePlayer {

	private GameClient client = null;
	private Room room = null;
	private final String username = "EDI";
	private final String password = "password";

	public EDI() {
		
	}

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
		throw new UnsupportedOperationException("Unimplemented method 'getGameGUI'");
	}

	private LinkedList<String> systemMessages = new LinkedList<>();
	@Override
	public boolean handleMessage(String msgType, String msg) {
		systemMessages.push(msgType + (msg != null ? ": " + msg : ""));
		Display.printSystemMessages(systemMessages);		

		return true;
	}

	@Override
	public boolean handleGameMessage(
		String msgType, Map<String, Object> details
	) {
		switch (msgType) {
		case GameMessage.GAME_STATE_BOARD:
			Display.clear();
			ArrayList<Integer> board = 
				(ArrayList<Integer>) details.get(AmazonsGameMessage.GAME_STATE);
				State newState;
				try {
					newState = State.from(board, C.WHITE, 0);
					Display.printBoard(newState, "board received");
				} catch (Exception e) {
					System.out.print(e);
				}
			break;
		case GameMessage.GAME_ACTION_START:
			System.out.print(details);
			// handle the game starting.
			break;
		case GameMessage.GAME_ACTION_MOVE:
			System.out.print(details);
			// handle a move made.
			break;
		}
		handleMessage(msgType, null);

		return true;
	}

	@Override
	public void onLogin() {
		Display.printText(0, "Logged in.");

		List<Room> rooms = client.getRoomList();
		List<String> roomNames = new LinkedList<String>();
		for (Room room : rooms) {
			roomNames.add(
				room.getName() + 
				Ansi.FG_BRIGHT_BLACK +
				" (" + Integer.toString(
					room.getUserCount() - room.getSpectatorCount()
				) + 
				"/" + Integer.toString(
					room.getMaxUsers() - room.getMaxSpectators()
				) +
				")" +
				Ansi.RESET
			);
		}

		System.out.println(roomNames);

		String roomName = Display.prompt("Choose a room.", roomNames);	

		for (Room room : rooms) {
			if (roomName.startsWith(room.getName())) {
				this.room = room;
			}
		}

		Display.clear();
		Display.printText(0, "Joining room " + room.getName() + "...");

		client.joinRoom(room.getName());
	}

	@Override
	public String userName() {
		return username;
	}
	
}
