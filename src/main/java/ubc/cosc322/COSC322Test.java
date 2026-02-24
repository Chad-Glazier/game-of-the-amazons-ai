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
        System.out.println("Congratualations!!! "
                + "I am called because the server indicated that the login is successfully");
        
     // Display the room list on the GUI
        userName = gameClient.getUserName();
        if (gamegui != null) {
        	gamegui.setRoomInformation(gameClient.getRoomList());
        }
        
        System.out.println("The next step is to find a room and join it: "
                + "the gameClient instance created in my constructor knows how!"); 
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
        // Handling chessboard initialization state
        if (messageType.equals(GameMessage.GAME_STATE_BOARD)) {
            if (gamegui != null) {
                @SuppressWarnings("unchecked")
                ArrayList<Integer> state = (ArrayList<Integer>) msgDetails.get(GameMessage.GAME_STATE_BOARD);
                gamegui.setGameState(state);
            }
            return true;
        }

        // Handling piece movement
        if (messageType.equals(GameMessage.GAME_ACTION_MOVE)) {
            if (gamegui != null) {
                gamegui.updateGameState(msgDetails);
            }
            return true;
        }
                
        return true;    
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