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
 * Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer {

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
    
    private String userName = null;
    private String passwd = null;
    
    // Variable to store your assigned color
    // 1 = White, 2 = Black
    private int myColor = -1; 
    
    // NEW: Internal 2D Array to represent the board for AI calculations
    // Size 10x10 is used as requested by team to simplify evaluation indices.
    // NOTE: All incoming server coordinates (1-10) must be shifted by -1.
    private int[][] board = new int[10][10];
 
    
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {                 
    	// Add parameter safety checks to prevent out-of-bounds errors
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
    
    /**
     * Any name and passwd 
     * @param userName
     * @param passwd
     */
    public COSC322Test(String userName, String passwd) {
        this.userName = userName;
        this.passwd = passwd;
        
     // Enable GUI instance
        this.gamegui = new BaseGameGUI(this);
    }
 

    @Override
    public void onLogin() {
        System.out.println("Congratulations!!! Login successful.");
        
        // 1. Synchronize usernames and GUI room list
        this.userName = gameClient.getUserName();
        if (gamegui != null) {
            gamegui.setRoomInformation(gameClient.getRoomList());
        }

     // 2. Automatically find and join rooms
     // Get the current list of rooms on the server
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
                // Use "game-state" as the extraction key.
                ArrayList<Integer> state = (ArrayList<Integer>) msgDetails.get("game-state"); 
                
                // Add security checks to prevent future server fluctuations from causing crashes again.
                if (state != null) {
                    gamegui.setGameState(state);
                    setupLocalBoard(state); // no more NullPointerException 
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

            // Determine if I am White or Black
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
            
            // NEW: Update the internal 2D board to reflect the move
            @SuppressWarnings("unchecked")
            ArrayList<Integer> queenPosCurr = (ArrayList<Integer>) msgDetails.get("queen-position-current");
            @SuppressWarnings("unchecked")
            ArrayList<Integer> queenPosNext = (ArrayList<Integer>) msgDetails.get("queen-position-next");
            @SuppressWarnings("unchecked")
            ArrayList<Integer> arrowPos = (ArrayList<Integer>) msgDetails.get("arrow-position");
            
            updateLocalBoard(queenPosCurr, queenPosNext, arrowPos);
            
            return true;
        }
                
        return true;    
    }

    
    
    //AI can "remember" and "update" the chessboard.
    /**
     * Converts the 1D ArrayList from the server into a 10x10 2D array.
     * Skips the 0th row and 0th column padding sent by the server.
     * 0 = Empty, 1 = White Queen, 2 = Black Queen, 3 = Arrow
     */
    private void setupLocalBoard(ArrayList<Integer> state) {
        // The server sends 121 elements (11x11). We only want rows 1-10 and cols 1-10.
        // We map server index (r, c) to local board index [r-1][c-1]
        for (int r = 1; r <= 10; r++) {
            for (int c = 1; c <= 10; c++) {
                // Server index formula: r * 11 + c
                int serverIndex = r * 11 + c;
                this.board[r - 1][c - 1] = state.get(serverIndex);
            }
        }
        System.out.println("Internal 10x10 board initialized.");
    }
    
    /**
     * Updates the internal 10x10 array when a move is made by any player.
     * Applies a -1 offset to convert server coordinates (1-10) to local array indices (0-9).
     */
    private void updateLocalBoard(ArrayList<Integer> qCurr, ArrayList<Integer> qNext, ArrayList<Integer> arrow) {
        // Apply -1 to shift from 1-based index to 0-based index
        int rCurr = qCurr.get(0) - 1;
        int cCurr = qCurr.get(1) - 1;
        
        int rNext = qNext.get(0) - 1;
        int cNext = qNext.get(1) - 1;
        
        int rArrow = arrow.get(0) - 1;
        int cArrow = arrow.get(1) - 1;
        
        // Find which queen is moving (1 for White, 2 for Black)
        int movingQueen = this.board[rCurr][cCurr];
        
        // 1. Remove the queen from the old position
        this.board[rCurr][cCurr] = 0;
        
        // 2. Place the queen in the new position
        this.board[rNext][cNext] = movingQueen;
        
        // 3. Place the arrow (represented by 3)
        this.board[rArrow][cArrow] = 3;
        
        System.out.println("Internal 10x10 board updated with recent move.");
    }
    
    
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