package game.network;

import graphicslib3D.Point3D;

import java.io.IOException; 
import java.net.InetAddress; 
import java.util.UUID; 
 

import sage.networking.server.GameConnectionServer; 
import sage.networking.server.IClientInfo; 

public class GameServerTCP extends GameConnectionServer<UUID> { 
	private UUID mainClient;
	private boolean firstClient;
	public GameServerTCP(int localPort) throws IOException { 
		super(localPort, ProtocolType.TCP);
	} 
	 
	public void acceptClient(IClientInfo ci, Object o) { 
		if (getClients().size()==2) { return; } //only 2 players allowed
		
		String message = (String)o; 
		String[] messageTokens = message.split(","); 
	 
		if(messageTokens.length > 0) { 
			if(messageTokens[0].compareTo("join") == 0)  { 
				// format: join,localid 
				UUID clientID = UUID.fromString(messageTokens[1]); 
				addClient(ci, clientID);  
				if(firstClient == false){
					mainClient = clientID;
					firstClient = true;

					sendJoinedMessage(clientID, true, true);
				}
				else sendJoinedMessage(clientID, true, false);
			} 
		} 
	} 

	public void processPacket(Object o, InetAddress senderIP, int sndPort) { 
		String message = (String) o; 
		String[] msgTokens = message.split(","); 
	 
		if(msgTokens.length > 0) { 
			if(msgTokens[0].compareTo("bye") == 0)  { 	
				// format: bye,localid 
				UUID clientID = UUID.fromString(msgTokens[1]); 
				sendByeMessages(clientID); 
				removeClient(clientID); 
			} 
	 
			else if(msgTokens[0].compareTo("create") == 0) { // format: create,localid,x,y,z 
				System.out.println("create message received from " + msgTokens[1]);
				UUID clientID = UUID.fromString(msgTokens[1]); 
				Point3D pos = new Point3D(Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4])); 
				sendCreateMessages(clientID, pos); 
				sendWantsDetailsMessages(clientID); 
			} 
		
			else if(msgTokens[0].compareTo("dsfr") == 0) { 
				System.out.println("Server recieved details from " + msgTokens[1] + " for " + msgTokens[2]);
				UUID clientID = UUID.fromString(msgTokens[1]);
				UUID remID = UUID.fromString(msgTokens[2]); 
				Point3D pos = new Point3D(Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]), Double.parseDouble(msgTokens[5])); 
				sendDetailsMessage(clientID, remID, pos); 
			}
		
			else if(msgTokens[0].compareTo("move") == 0) { 
				UUID clientID = UUID.fromString(msgTokens[1]);
				Point3D pos = new Point3D(Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]));
				sendMoveMessages(clientID, pos);
			}
			else if(msgTokens[0].compareTo("sync") == 0) // receive move 
			{ 
				UUID ghostID = UUID.fromString(msgTokens[1]);
				// extract ghost x,y,z, position from message, then: 
				Point3D ghostPosition = new Point3D(Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]));
				sendSyncKittyMessage(ghostID, ghostPosition);
			} 
			else if(msgTokens[0].compareTo("krot") == 0) // receive move 
			{ 
				UUID ghostID = UUID.fromString(msgTokens[1]);
				// extract ghost x,y,z, position from message, then: 
				double[] rot = {Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]), Double.parseDouble(msgTokens[5])};
				sendKittyRotMessage(ghostID, rot);
			} 
			else if(msgTokens[0].compareTo("grot") == 0) // receive move 
			{ 
				UUID ghostID = UUID.fromString(msgTokens[1]);
				// extract ghost x,y,z, position from message, then: 
				double[] rot = {Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]), Double.parseDouble(msgTokens[5])};
				sendGhostRotMessage(ghostID, rot);
			}
			else if(msgTokens[0].compareTo("pwUp") == 0) 
			{ 
				UUID remID = UUID.fromString(msgTokens[1]);
				sendPowerUpObtained(remID);
			} 
		}
	}
		 
	public void sendJoinedMessage(UUID clientID, boolean success, boolean mainClient){ 
		// format: join, success or join, failure 
		try { 
			String message = new String("join,"); 
			if (success) message += "success,"; 
			else message += "failure,"; 
			if (mainClient) message += "main";
			else message += "notMain";
			sendPacket(message, clientID); 
		} 
		catch (IOException e) { 
			e.printStackTrace(); 
		}
	} 
	 
	public void sendCreateMessages(UUID clientID, Point3D position) { 
		// format: create, remoteId, x, y, z 
		try { 
			System.out.println("create message sent to everyone except " + clientID.toString());
			String message = new String("create," + clientID.toString()); 
			message += "," + position.getX(); 
			message += "," + position.getY(); 
			message += "," + position.getZ(); 
			forwardPacketToAll(message, clientID); 
		} 
		catch (IOException e)   { 
			e.printStackTrace(); 
		} 
	} 
	 
	public void sendDetailsMessage(UUID clientID, UUID remoteId, Point3D position) { 
		try { 
			String message = new String("dsfr," + clientID.toString());
			message += "," + position.getX(); 
			message += "," + position.getY(); 
			message += "," + position.getZ(); 
			sendPacket(message, remoteId);
			System.out.println("Server sent details to " + remoteId.toString() + " for " + clientID.toString());
		} 
		catch (IOException e) { 
			e.printStackTrace();
		}
	} 
	 
	public void sendWantsDetailsMessages(UUID clientID) {  
		try { 
			String message = new String("wsds," + clientID.toString()); 
			forwardPacketToAll(message, clientID); 
			System.out.println("wants details message sent to everyone except " + clientID.toString());
		} 
		catch (IOException e) { 
			e.printStackTrace();
		}
	} 
	 
	public void sendMoveMessages(UUID clientID, Point3D position) {  
		try { 
			String message = new String("move," + clientID.toString()); 
			message += "," + position.getX(); 
			message += "," + position.getY(); 
			message += "," + position.getZ();
			forwardPacketToAll(message, clientID); 
		} 
		catch (IOException e) { 
			e.printStackTrace();
		}
	} 
	  
	public void sendByeMessages(UUID clientID) {  
		try { 
			String message = new String("bye," + clientID.toString()); 
			forwardPacketToAll(message, clientID); 
		} 
		catch (IOException e) { 
			e.printStackTrace();
		}
	}
	public void sendSyncKittyMessage(UUID clientID,Point3D pos){
		try 
		{ 
			String message = new String("sync," + clientID.toString()); 
			message += "," + pos.getX(); 
			message += "," + pos.getY(); 
			message += "," + pos.getZ(); 
			forwardPacketToAll(message, clientID);
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	}
	public void sendKittyRotMessage(UUID clientID,double[] rot){
		try 
		{ 
			String message = new String("krot," + clientID.toString()); 
			message += "," + rot[0]; 
			message += "," + rot[1]; 
			message += "," + rot[2]; 
			message += "," + rot[3]; 
			forwardPacketToAll(message, clientID);
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	}
	public void sendGhostRotMessage(UUID clientID,double[] rot){
		try 
		{ 
			String message = new String("grot," + clientID.toString()); 
			message += "," + rot[0]; 
			message += "," + rot[1]; 
			message += "," + rot[2]; 
			message += "," + rot[3]; 
			forwardPacketToAll(message, clientID);
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	}
	public void sendPowerUpObtained(UUID clientID){
		try 
		{ 
			String message = new String("pwUp," + clientID.toString()); 
			forwardPacketToAll(message, clientID);
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		}
	}

}
