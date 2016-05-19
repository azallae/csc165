package game.network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import game.GhostAvatar;
import game.MyNetworkingClient;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import sage.networking.client.GameConnectionClient;

public class GameClientTCP extends GameConnectionClient{
	private MyNetworkingClient game;
	private UUID id;
	private GhostAvatar ghost;
	
	public GameClientTCP(InetAddress remAddr, int remPort, ProtocolType pType,
			MyNetworkingClient game) throws IOException{ 
		super(remAddr, remPort, pType);

		this.game = game;
		this.id = UUID.randomUUID();
		//this.ghost = new GhostAvatar(id, new Point3D(0,0,0));
	}
	protected void processPacket (Object msg) {
		// extract incoming message into substrings. Then process:
		String message = (String) msg;
		System.out.println(message);
		String[] msgTokens = message.split(",");

		if(msgTokens[0].compareTo("join") == 0)	{ // format: join, success or join, failure
			if(msgTokens[1].compareTo("success") == 0){
				game.setIsConnected(true);
				sendCreateMessage(game.getPlayerPosition());
			}
			else if(msgTokens[1].compareTo("failure") == 0)
				game.setIsConnected(false);
		}
		else if(msgTokens[0].compareTo("bye") == 0) { // format: bye, remoteId
			UUID ghostID = UUID.fromString(msgTokens[1]);
			removeGhostAvatar(ghostID);
		}
		else if (msgTokens[0].compareTo("create")==0){
			UUID ghostID = UUID.fromString(msgTokens[1]);
			Point3D ghostPosition = new Point3D(Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]));
			if(ghost==null){
				createGhostAvatar(ghostID, ghostPosition);
			}
		}
		else if (msgTokens[0].compareTo("dsfr") == 0 ) { // format: create, remoteId, x,y,z or dsfr, remoteId, x,y,z
			UUID ghostID = UUID.fromString(msgTokens[1]);
			Point3D ghostPosition = new Point3D(Double.parseDouble(msgTokens[2])
												,Double.parseDouble(msgTokens[3])
												,Double.parseDouble(msgTokens[4]));
			// extract ghost x,y,z, position from message, then:
			if(ghost==null){
				createGhostAvatar(ghostID, ghostPosition);
			}
		}
		else if(msgTokens[0].compareTo("wsds") == 0) { // etc….. 
			Point3D pos = game.getPlayerPosition();
			UUID remID = UUID.fromString(msgTokens[1]);
			sendDetailsForMessage(remID, pos);
		}

		else if(msgTokens[0].compareTo("move") == 0) { // etc….. 
			UUID ghostID = UUID.fromString(msgTokens[1]); 
			// extract ghost x,y,z, position from message, then: 
			Point3D ghostPosition = new Point3D(Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]));
			moveGhostAvatar(ghostID, ghostPosition);
		}
	}

	private void moveGhostAvatar(UUID ghostID, Point3D ghostPosition) {
		if(ghostID != null){
			ghost.setLocation(ghostPosition);
		}
		// TODO Auto-generated method stub
		
	}
	private void removeGhostAvatar(UUID ghostID) {
		game.removeGameWorldObject(ghost);
		ghost = null;		
	}
	
	private void createGhostAvatar(UUID ghostID, Point3D ghostPosition) {		
		ghost = new GhostAvatar(ghostID, ghostPosition);
		game.textureObj(ghost, "chicken.png");
		
		ghost.updateLocalBound();

		ghost.updateGeometricState(1.0f, true);

		Matrix3D p1M = ghost.getLocalTranslation(); 
		ghost.translate(0,5f,0); 
		ghost.setLocalTranslation(p1M); 
		game.addGameWorldObject(ghost);
	}
	public void sendCreateMessage(Point3D point3d){ // format: (create, localId, x,y,z)
		try
		{ String message = new String("create," + id.toString());
		message += "," + point3d.getX()+"," + point3d.getY() + "," + point3d.getZ();
		sendPacket(message);
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	public void sendJoinMessage(){ // format: join, localId
		try
		{ sendPacket(new String("join," + id.toString())); }
		catch (IOException e) { e.printStackTrace(); }
	}
	public void sendByeMessage(){ // etc….. }
		try{
			String message = new String("bye," + id.toString());
			sendPacket(message);
		}
		catch(IOException e){ e.printStackTrace(); }
	}
	public void sendDetailsForMessage(UUID remId, Point3D pos){ // etc….. }
		try { 
			String message = new String("dsfr," + id.toString() + "," + remId.toString());
			message += "," + pos.getX(); 
			message += "," + pos.getY(); 
			message += "," + pos.getZ(); 
			sendPacket(message); 
		} 
		catch (IOException e) { 
			e.printStackTrace();
		}
	}
	/*public void sendWantsDetailsMessages(UUID remID){
		try{
			String message = new String("wsds," + remID.toString());
			sendPacket(message);
		}
		catch(IOException e){ e.printStackTrace(); }
	}*/
	public void sendMoveMessage(Point3D pos){ // etc….. }
		try { 
			String message = new String("move," + id.toString()); 
			message += "," + pos.getX(); 
			message += "," + pos.getY(); 
			message += "," + pos.getZ(); 
			sendPacket(message); 
		} 
		catch (IOException e) { 
			e.printStackTrace();
		}

	}
}

