package ubc.cosc322;

import java.util.HashMap;
import java.util.Map;

import ubc.cosc322.player.EDI;
import ubc.cosc322.view.Display;
import ygraph.ai.smartfox.games.GamePlayer;

/**
 * 
 */
public class Main {

    public static void main(String[] args) throws Exception {

		Map<String, String> players = new HashMap<>();
		players.put(
			"EDI", 
			"primarily uses an optimized alpha-beta search."
		);
		String playerName = 
			Display.prompt("Choose an AI player.", players);

		GamePlayer player = null;
		switch (playerName) {
		case "EDI":
			player = new EDI();
			break;
		default:
			Display.printText(0, "Error: No bot chosen.");
			return;
		}

		player.connect();
	}
}
