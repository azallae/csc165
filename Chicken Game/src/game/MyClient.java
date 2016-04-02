package game;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;

import graphicslib3D.Vector3D;
import sage.networking.client.GameConnectionClient;

public class MyClient extends GameConnectionClient{
	private MyNetworkingClient game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;
	private GhostAvatar ghost;
	public MyClient(InetAddress remAddr, int remPort, ProtocolType pType,
			MyNetworkingClient game) throws IOException{ 
		super(remAddr, remPort, pType);

		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new Vector<GhostAvatar>();
	}
	protected void processPacket (Object msg) {
		// extract incoming message into substrings. Then process:
		String message = (String) msg;
		String[] msgTokens = message.split(",");

		if(msgTokens[0].compareTo("join") == 0)	{ // format: join, success or join, failure
			if(msgTokens[1].compareTo("success") == 0){
				game.setIsConnected(true);
				sendCreateMessage(game.getPlayerPosition());
			}
			if(msgTokens[1].compareTo("failure") == 0)
				game.setIsConnected(false);
		}
		if(msgTokens[0].compareTo("bye") == 0) { // format: bye, remoteId
			UUID ghostID = UUID.fromString(msgTokens[1]);
			removeGhostAvatar(ghostID);
		}
		if (msgTokens[0].compareTo("dsfr") == 0 ) { // format: create, remoteId, x,y,z or dsfr, remoteId, x,y,z
			UUID ghostID = UUID.fromString(msgTokens[1]);
			String[] ghostPosition = {msgTokens[2], msgTokens[3], msgTokens[4]};
			// extract ghost x,y,z, position from message, then:
			createGhostAvatar(ghostID, ghostPosition);
		}
		if(msgTokens[0].compareTo("wsds") == 0) { // etc….. 
		}
		if(msgTokens[0].compareTo("wsds") == 0) { // etc….. 
		}
		if(msgTokens[0].compareTo("move") == 0) { // etc….. 

		}
	}

	private void removeGhostAvatar(UUID ghostID) {
		// TODO Auto-generated method stub

	}
	private void createGhostAvatar(UUID ghostID, String[] ghostPosition) {		
		ghost = new GhostAvatar(ghostID, ghostPosition);
		ghost.scale(.30f,.30f,.30f);
		game.textureObj(ghost, "ghostfighter.png");
		game.addGameWorldObject(ghost);
	}
	public void sendCreateMessage(Vector3D pos)
	{ // format: (create, localId, x,y,z)
		try
		{ String message = new String("create," + id.toString());
		message += "," + pos.getX()+"," + pos.getY() + "," + pos.getZ();
		sendPacket(message);
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	public void sendJoinMessage()
	{ // format: join, localId
		try
		{ sendPacket(new String("join," + id.toString())); }
		catch (IOException e) { e.printStackTrace(); }
	}
	public void sendByeMessage(){ // etc….. }

	}
	public void sendDetailsForMessage(UUID remId, Vector3D pos)
	{ // etc….. }

	}
	public void sendMoveMessage(Vector3D pos)
	{ // etc….. }

	}
}

