package sLSRP;
import java.net.*;
import java.util.*;

public class Router {

	public static volatile boolean failing = false;

	public static void main(String args[]) {
		// Read in configuration
		Configuration config = new Configuration();
		
		// Create data structures
		Links linkData = new Links(config);
		RoutingTable routingTable = new RoutingTable(config);
		LSAHistory history = new LSAHistory(config);
		
		// Loop over neighbors in the configuration
		
		// Fork all threads
		
		// Create socket and listen
		ServerSocket serverSocket = NetUtils.serverSocket();
		System.out.println("Listening on port " + serverSocket.getLocalPort() + ".");
		while(true) {
			if(!failing) {
				System.out.println("Waiting to accept incoming client...");
				SocketBundle client = NetUtils.acceptClient(serverSocket);
				int packetType = -1;
				try {
					packetType = client.in.read();
				} catch(Exception e) {
					System.out.println(e);
				}
				
				switch(packetType) {
					case 0:
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						break;
				}
			}
		}
	}

}