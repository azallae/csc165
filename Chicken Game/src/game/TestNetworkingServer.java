package game.network;

public class TestNetworkingServer
{
	public static void main(String [] args)
	{
		testTCPServer = new GameServerTCP(args[1]);
	}
}