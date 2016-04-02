package game.network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;

public class GameServerTCP extends GameConnectionServer<UUID>
{
	public TestGameServerTCP(int localPort) throws IOException
	{super(localPort, ProtocolType.TCP);}
	
	public void acceptClient(IClientInfo ci, Object o){
		String message = (String)o;
		String[] messageTokens = message.split(",");
		
		if(messageTokens.length > 0){
			if(messageTokens[0].compareTo("join")==0){
				UUID clientID = UUID.fromString(messageTokens[1]);
				addClient(ci, clientID);
				sendJoinedMessage(clientID, true);
			}
		}
	}
	
	public void processPacket(Object o, InetAddress senderIP, int sendPort)
	{
		String message = (String) o;
		String[] msgTokens = message.split(",");
		
		if(msgTokens.length > 0){
			if(msgTokens[0].compareTo("bye") == 0){
				UUID clientID = UUID.fromString(msgTokens[1]);
				sendByeMessage(clientID);
				removeClient(clientID);
			}
		}
		
		if(msgTokens[0].compareTo("create") == 0){
			UUID clientID = UUID.fromString(messageTokens[1]);
			String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
			sendCreateMessages(clientID, pos);
			sendWantsDetailsMessages(clientID);
		}
		
		if(msgTokens[0].compareTo("dsfr")==0){
			UUID clientID = UUID.fromString(msgTokens[1]);
			UUID remoteID = UUID.fromString(msgTokens[2]);
			String[] pos = {msgTokens[3], msgTokens[4], msgTokens[5]};
			sendDetailsMessages(clientID, remoteID, pos);
		}
		
		if(msgTokens[0].compareTo("move")){
			UUID clientID = UUID.fromString(msgToken[1]);
			String[] pos = {msgTokens[3], msgTokens[4], msgTokens[5]};
			sendMoveMessages(clientID, pos);
		}
	}
	
	public void sendJoinedMessage(UUID clientID, boolean success){
		try{
			String message = new String("join");
			if(success) message += "success";
			else message += "failure";
			sendPacket(message, clientID);
		}
		catch(IOException e){ e.printStackTrace();}
	}
	
	public void sendCreateMessages(UUID clientID, String[] position){
		try{
			String message = new String("create, " + clientID.toString());
			message += ", " + position[0];
			message += ", " + position[1];
			message += ", " + position[2];
			forwardPacketToAll(message, clientID);
		}
		catch(IOException e){ e.printStackTrace(); }
	}
	
	public void sendDetailsMessages(UUID clientID, UUID remoteID, String[] position){
		try{
			String message = new String("dsfr, " + clientID.toString());
			message += ", " + position[0];
			message += ", " + position[1];
			message += ", " + position[2];
			sendPacket(message, remoteID);
		}
		catch(IOException e){ e.printStackTrace(); }
	}
	
	public void sendWantsDetailsMessages(UUID clientID, UUID remoteID, String[] position){
		try{
			String message = new String("dsfr, " + clientID.toString());
			message += ", " + position[0];
			message += ", " + position[1];
			message += ", " + position[2];
			sendPacket(message, remoteID);
		}
		catch(IOException e){ e.printStackTrace(); }
	}
	
	public void sendByeMessage(UUID clientID){
		try{
			String message = new String("bye, " + clientID.toString());
			forwardPacketToAll(message, clientID);
		}
		catch(IOException e){ e.printStackTrace(); }
	}
}