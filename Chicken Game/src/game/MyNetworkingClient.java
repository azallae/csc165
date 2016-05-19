package game;



import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import game.network.GameClientTCP;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.networking.IGameConnection.ProtocolType;
import sage.scene.SceneNode;

public class MyNetworkingClient extends ChickenGame{
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private GameClientTCP thisClient;
	private boolean connected;
	private boolean mainClient;
	private GhostKitty gKitty;
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
	}
	protected void createSagePhysicsWorld(){
		super.createSagePhysicsWorld();
		float mass = 1.0f;
		kittyP = physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, kitty.getWorldTransform().getValues(), 1f);
		kitty.setPhysicsObject(kittyP);
		kittyP.setDamping(.9f, .9f);

	}
	public void update(float elapsedTimeMS){ // same as before, plus process any packets received from server
		//. . . .
		if(mainClient){
			if(kittyExists){
				kitty.kittyMove();
				if(ghost != null){
					Point2D.Double kittyLoc = new Point2D.Double(kitty.getLocalTranslation().elementAt(0, 3),kitty.getLocalTranslation().elementAt(2, 3));
					Point2D.Double chickenLoc = new Point2D.Double(player.getLocalTranslation().elementAt(0, 3),player.getLocalTranslation().elementAt(2, 3));
					Point2D.Double ghostLoc = new Point2D.Double(ghost.getLocalTranslation().elementAt(0, 3),ghost.getLocalTranslation().elementAt(2, 3));;
					float distToChicken = (float) kittyLoc.distance(chickenLoc);
					float distToGhost = (float) kittyLoc.distance(ghostLoc);
					if(distToChicken < distToGhost){
						kitty.kittyFollow(player);
					}// CHASE PRIORITY
					if(distToChicken > distToGhost){
						kitty.kittyFollow(ghost);
					}
				}
				if(ghost == null)
					kitty.kittyFollow(player);
				kitty.updateAnimation(elapsedTimeMS);
			}
		}
		if (thisClient != null) thisClient.processPackets();
		if (thisClient != null) thisClient.sendMoveMessage(getPlayerPosition());
		if (mainClient && thisClient != null) {
			thisClient.sendSyncKittyMessage(kitty.getLocation());
		}
		super.update(elapsedTimeMS);
		//. . . .
	}
	protected void initGameObjects(){
		super.initGameObjects();
		if(mainClient){
			kitty = new Kitty();
			textureObj(kitty, "kitty.png");
			addGameWorldObject(kitty); 
			kitty.updateLocalBound();
			Matrix3D k1M = kitty.getLocalTranslation(); 
			kitty.translate(5f,1f,0f); 
			kitty.setLocalTranslation(k1M); 
			kittyExists = true;
		}
		else{
			gKitty = new GhostKitty();
			textureObj(gKitty,"kitty.png");
			addGameWorldObject(gKitty);
		}

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
		return getPlayer().getLocation();
	}
	public void addGameWorldObject(SceneNode s) {
		super.addGameWorldObject(s);
	}

	public boolean removeGameWorldObject(SceneNode s) {
		return super.removeGameWorldObject(s);
	}
	public void setMainClient(boolean b) {
		mainClient = b;
	}
	public void moveKitty(Point3D p) {
		gKitty.move(p);
		
	}
	public void rotateKitty(double[] rot) {
		gKitty.rotateCharacter(rot);
		
	}
	public void rotateGhost(double[] rot) {
		ghost.rotateCharacter(rot);
		
	}
}