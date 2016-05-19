package game;



import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import game.network.GameClientTCP;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.app.BaseGame;
import sage.networking.IGameConnection.ProtocolType;
import sage.scene.SceneNode;

public class MyNetworkingClient extends ChickenGame{
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private GameClientTCP thisClient;
	private boolean connected;
	private Point3D lastPos;
	// assumes main() gets address/port from command line
	public MyNetworkingClient(String serverAddr, int sPort)	{ 
		super();
		this.serverAddress = serverAddr;
		this.serverPort = sPort;
		this.serverProtocol = ProtocolType.TCP;
	}
	protected void initGame(){ // items as before, plus initializing network:
		
		try{
			thisClient = new GameClientTCP(InetAddress.getByName(serverAddress),
					serverPort, serverProtocol, this); 

		}
		catch (UnknownHostException e) {
			e.printStackTrace(); 
		}
		catch (IOException e) { 
			e.printStackTrace(); 
		}
		if (thisClient != null) {
			thisClient.sendJoinMessage(); 
		}
		super.initGame();
		lastPos = getPlayerPosition();
	}
	public void update(float time){ // same as before, plus process any packets received from server
		//. . . .
		if (thisClient != null) {
			thisClient.processPackets();
			if(lastPos != getPlayerPosition()){
				thisClient.sendMoveMessage(getPlayerPosition());
				lastPos = getPlayerPosition();
			}
		}
		super.update(time);
		//. . . .
	}
	protected void shutdown(){
		super.shutdown();
		if(thisClient != null){ 
			thisClient.sendByeMessage();

			try{ 
				thisClient.shutdown();  // shutdown() is inherited
			}
			catch (IOException e) { 
				e.printStackTrace(); 
			}
		} 
	}
	public void setIsConnected(boolean b) {
		connected = b;
		// TODO Auto-generated method stub
	}
	public boolean isConnected() {
		return connected;
	}
	public Point3D getPlayerPosition() {
		// TODO Auto-generated method stub
		return getPlayer();
	}
	public void addGameWorldObject(SceneNode s) {
		super.addGameWorldObject(s);
	}
	
	public boolean removeGameWorldObject(SceneNode s) {
		return super.removeGameWorldObject(s);
	}
}