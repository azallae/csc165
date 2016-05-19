package game.network;

import game.ChickenGame;
import game.GhostAvatar;
import game.MyNetworkingClient;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import sage.networking.client.GameConnectionClient;

public class GameClientTCP extends GameConnectionClient 
{ 
	private ChickenGame game; 
	private UUID id; 
	private GhostAvatar ghost;
 
	public GameClientTCP(InetAddress remAddr, int remPort, ProtocolType pType, ChickenGame chickenGame) throws IOException 
	{ 
		super(remAddr, remPort, pType); 
		this.game = chickenGame; 
		this.id = UUID.randomUUID(); 
		System.out.println(id.toString());
	} 
	
	protected void processPacket(Object msg) // override 
	{ 
		// extract incoming message into substrings. Then process: 
		String message = (String) msg; 
		String[] msgTokens = message.split(","); 
	 
		if(msgTokens.length > 0) 
		{ 		
			if(msgTokens[0].compareTo("join") == 0) 
			{
				// receive join 
				// format: join,mainClient, success or join,mainClient, failure 			
				if(msgTokens[1].compareTo("success") == 0) 
				{ 
					game.setIsConnected(true); 
					if(msgTokens[2].compareTo("main") == 0){
						game.setMainClient(true);
						game.addKitty();
					}
					else if(msgTokens[2].compareTo("notMain") == 0){
						game.setMainClient(false);
						game.addGhostKitty();
					}
						
					sendCreateMessage(game.getPlayerPosition()); 
				} 
				else if(msgTokens[1].compareTo("failure") == 0) 
					game.setIsConnected(false); 
			} 
			else if(msgTokens[0].compareTo("bye") == 0) // receive bye 
			{ 
				// format: bye, remoteId 
				UUID ghostID = UUID.fromString(msgTokens[1]); 
				removeGhostAvatar(ghostID); 
			} 
			else if(msgTokens[0].compareTo("create") == 0) // receive create 
			{  
				// format: create, remoteId, x,y,z or dsfr, remoteId, x,y,z 
				UUID ghostID = UUID.fromString(msgTokens[1]); 
				// extract ghost x,y,z, position from message, then: 
				Point3D ghostPosition = new Point3D(Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4])); 
				if (ghost==null){
					createGhostAvatar(ghostID, ghostPosition);
				}
			} 
			else if(msgTokens[0].compareTo("move") == 0) // receive move 
			{ 
				UUID ghostID = UUID.fromString(msgTokens[1]); 
				// extract ghost x,y,z, position from message, then: 
				Point3D ghostPosition = new Point3D(Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]));
				moveGhostAvatar(ghostID, ghostPosition);
			} 
			else if (msgTokens[0].compareTo("dsfr") == 0) // receive details for 
			{
				// format: dsfr, remoteId, x,y,z 
				UUID ghostID = UUID.fromString(msgTokens[1]);
				Point3D location = new Point3D(Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]));
				
				if (ghost==null){
					createGhostAvatar(ghostID, location);
					sendReadyMessage();
				}
				
			} 
			else if(msgTokens[0].compareTo("sync") == 0) // receive move 
			{ 
				UUID ghostID = UUID.fromString(msgTokens[1]);
				// extract ghost x,y,z, position from message, then: 
				Point3D ghostPosition = new Point3D(Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]));
				syncKitty(ghostID, ghostPosition);
			} 
			else if(msgTokens[0].compareTo("krot") == 0) // receive move 
			{ 
				UUID ghostID = UUID.fromString(msgTokens[1]);
				// extract ghost x,y,z, position from message, then: 
				double[] rot = {Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]), Double.parseDouble(msgTokens[5])};
				kittyDirection(ghostID, rot);
			} 
			else if(msgTokens[0].compareTo("grot") == 0) // receive move 
			{ 
				UUID ghostID = UUID.fromString(msgTokens[1]);
				// extract ghost x,y,z, position from message, then: 
				double[] rot = {Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]), Double.parseDouble(msgTokens[5])};
				ghostDirection(ghostID, rot);
			}
			else if(msgTokens[0].compareTo("wsds") == 0) // receive wants details 
			{ 
				Point3D pos = game.getPlayerPosition();
				UUID remID = UUID.fromString(msgTokens[1]);
				sendDetailsForMessage(remID, pos);
			} 
			else if(msgTokens[0].compareTo("pwUp") == 0) // receive wants details 
			{ 
				UUID remID = UUID.fromString(msgTokens[1]);
				sendPowerUpUsed(remID);
			} 
			
		}
	} 

	private void sendReadyMessage() {
		try 
		{ 
			String message = new String("ready," + id.toString()); 
			sendPacket(message); 
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	}

	private void createGhostAvatar(UUID ghostID, Point3D ghostPosition) {
		/*ghost = new GhostAvatar(ghostID, ghostPosition);
		game.textureObj(ghost, "chicken.png");
		game.addGameWorldObject(ghost);*/
		game.createGhostAvatar(ghostID, ghostPosition);
	}

	private void removeGhostAvatar(UUID ghostID) {
		game.removeGameWorldObject(ghost);
		ghost = null;		
	}
	
	private void moveGhostAvatar(UUID ghostID, Point3D ghostPosition) {	
		if (game.doesGhostExist()) 
			game.moveGhostAvatar(ghostPosition);
	}
	private void syncKitty(UUID ghostID,Point3D p){
		game.moveKitty(p);
	}
	
	private void kittyDirection(UUID ghostID,double[] rot){
		game.rotateKitty(rot);
	}
	private void ghostDirection(UUID ghostID,double[] rot){
		game.rotateGhost(rot);	
	}
	private void sendPowerUpUsed(UUID remID){
		game.usePowerUp();
	}

	public void sendCreateMessage(Point3D pos) 
	{	
		// format: (create, localId, x,y,z) 
		try 
		{ 
			String message = new String("create," + id.toString()); 
			message += "," + pos.getX()+"," + pos.getY() + "," + pos.getZ(); 
			sendPacket(message); 
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	}
	 
	public void sendJoinMessage() 
	{
		// format: join, localId 
		try 
		{ 
			sendPacket(new String("join," + id.toString()));
		}
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	}
	 
	
	public void sendByeMessage() 
	{  
		try 
		{ 
			String message = new String("bye," + id.toString()); 
			sendPacket(message); 
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	} 
	
	public void sendDetailsForMessage(UUID remId, Point3D pos) 
	{
		try 
		{ 
			String message = new String("dsfr," + id.toString() + "," + remId.toString());
			message += "," + pos.getX(); 
			message += "," + pos.getY(); 
			message += "," + pos.getZ(); 
			sendPacket(message); 
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	}
	
	public void sendMoveMessage(Point3D pos) 
	{
		try 
		{ 
			String message = new String("move," + id.toString()); 
			message += "," + pos.getX(); 
			message += "," + pos.getY(); 
			message += "," + pos.getZ(); 
			sendPacket(message); 
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	}
	public void sendSyncKittyMessage(Point3D pos){
		try 
		{ 
			String message = new String("sync," + id.toString()); 
			message += "," + pos.getX(); 
			message += "," + pos.getY(); 
			message += "," + pos.getZ(); 
			sendPacket(message); 
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	}
	public void sendKittyRotMessage(double[] rot){
		try 
		{ 
			String message = new String("krot," + id.toString()); 
			message += "," + rot[0]; 
			message += "," + rot[1]; 
			message += "," + rot[2]; 
			message += "," + rot[3]; 
			sendPacket(message); 
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	}
	public void sendGhostRotMessage(double[] rot){
		try 
		{ 
			String message = new String("grot," + id.toString()); 
			message += "," + rot[0]; 
			message += "," + rot[1]; 
			message += "," + rot[2]; 
			message += "," + rot[3]; 
			sendPacket(message); 
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	}
	public void sendPowerUpObtained(){
		try 
		{ 
			String message = new String("pwUp," + id.toString()); 
			sendPacket(message); 
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	}
}