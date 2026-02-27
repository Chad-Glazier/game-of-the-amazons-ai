package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sfs2x.client.entities.Room;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.GameMessage;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 */
public class COSC322Test extends GamePlayer {

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
    
    private String userName = null;
    private String passwd = null;
    
    // Variable to store your assigned color
    // 1 = White, 2 = Black
    private int myColor = -1; 
    
    // NEW: The dedicated object to handle all board logic and move generation
    private BoardState boardState = new BoardState();
 
    
    public static void main(String[] args) {                 
        String user = (args.length > 0) ? args[0] : "spectator";
        String pwd  = (args.length > 1) ? args[1] : "pwd";

        COSC322Test player = new COSC322Test(user, pwd);
        
        if(player.getGameGUI() == null) {
            player.Go();
        }
        else {
            BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    player.Go();
                }
            });
        }
    }
    
    public COSC322Test(String userName, String passwd) {
        this.userName = userName;
        this.passwd = passwd;
        
        // Enable GUI instance
        this.gamegui = new BaseGameGUI(this);
    }
 
    @Override
    public void onLogin() {
        System.out.println("Congratulations!!! Login successful.");
        
        this.userName = gameClient.getUserName();
        if (gamegui != null) {
            gamegui.setRoomInformation(gameClient.getRoomList());
        }

        List<Room> rooms = gameClient.getRoomList();
        String targetRoom = "Okanagan Lake"; 
        boolean joined = false;

        System.out.println("Searching for room: " + targetRoom);

        for (Room room : rooms) {
            if (room.getName().equals(targetRoom)) {
                System.out.println("Found it! Joining " + targetRoom + "...");
                gameClient.joinRoom(targetRoom); 
                joined = true;
                break;
            }
        }

        if (!joined) {
            System.out.println("Room '" + targetRoom + "' not found. Available rooms are:");
            for (Room r : rooms) {
                System.out.println("- " + r.getName());
            }
            System.out.println("Please double-click a room in the GUI to proceed.");
        }
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
        // 1. Handling chessboard initialization state
        if (messageType.equals(GameMessage.GAME_STATE_BOARD)) {
            if (gamegui != null) {
                @SuppressWarnings("unchecked")
                ArrayList<Integer> state = (ArrayList<Integer>) msgDetails.get("game-state"); 
                
                if (state != null) {
                    gamegui.setGameState(state);
                    // Delegate board setup to our new BoardState class
                    boardState.setupLocalBoard(state); 
                } else {
                    System.err.println("ERROR: Received GAME_STATE_BOARD but 'game-state' data is null!");
                }
            }
            return true;
        }

        // 2. Identify Role (Black vs White)
        if (messageType.equals(GameMessage.GAME_ACTION_START)) {
            String whiteUser = (String) msgDetails.get("player-white");
            String blackUser = (String) msgDetails.get("player-black");
            
            System.out.println("Game Starting!");
            System.out.println("White Player: " + whiteUser);
            System.out.println("Black Player: " + blackUser);

            if (whiteUser.equals(this.userName)) {
                this.myColor = 1; 
                System.out.println("I am playing as WHITE (First move).");
            } else if (blackUser.equals(this.userName)) {
                this.myColor = 2;
                System.out.println("I am playing as BLACK (Second move).");
            }
            return true;
        }

        // 3. Handling piece movement
        if (messageType.equals(GameMessage.GAME_ACTION_MOVE)) {
            if (gamegui != null) {
                gamegui.updateGameState(msgDetails);
            }
            
            @SuppressWarnings("unchecked")
            ArrayList<Integer> queenPosCurr = (ArrayList<Integer>) msgDetails.get("queen-position-current");
            @SuppressWarnings("unchecked")
            ArrayList<Integer> queenPosNext = (ArrayList<Integer>) msgDetails.get("queen-position-next");
            @SuppressWarnings("unchecked")
            ArrayList<Integer> arrowPos = (ArrayList<Integer>) msgDetails.get("arrow-position");
            
            // Delegate updating the board to our new BoardState class
            boardState.updateLocalBoard(queenPosCurr, queenPosNext, arrowPos);
            
            return true;
        }
                
        return true;    
    }

    // =========================================================
    // SFS2X REQUIRED METHODS
    // =========================================================

    @Override
    public String userName() {
        return userName;
    }

    @Override
    public GameClient getGameClient() {
        return this.gameClient;
    }
    
    @Override
    public BaseGameGUI getGameGUI() {
        return this.gamegui; 
    }

    @Override
    public void connect() {
        gameClient = new GameClient(userName, passwd, this);            
    }

}