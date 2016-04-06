package gameStarter;

import game.*;
import game.network.GameServerTCP;

import java.io.IOException;
import java.util.Scanner;

public class Starter {
	private static final int localPort = 1550;
	
	public static void main (String [] args)throws IOException{
		Scanner kb = new Scanner(System.in);
		String ans = "";
		System.out.print("Are you the host?");
		ans = kb.nextLine();
		if(ans.charAt(0) == 'y'){
			System.out.println("You are now hosting the server.");
			GameServerTCP testTCPServer = new GameServerTCP(localPort);
			System.out.println("Your localInetAddress: " + testTCPServer.getLocalInetAddress());
			System.out.println("Port number is: " + localPort);
			
			String[] msgTokens = testTCPServer.getLocalInetAddress().toString().split("/");
			MyNetworkingClient game = new MyNetworkingClient(msgTokens[1], localPort);
			game.start();
		}
		else{
			System.out.print("You are a client.");
			MyNetworkingClient testTCPClient = new MyNetworkingClient("130.86.96.235", localPort);
			testTCPClient.start();
		}
	}
}