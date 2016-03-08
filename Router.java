package sLSRP;
import java.net.*;
import java.util.*;

public class Router {

	public static volatile boolean failing = false;

	public static void main(String args[]) {
		// Create data structures
		
		// Read in configuration
		
		// Loop over neighbors in the configuration
		
		// Fork all threads
		
		// Create socket and listen
		ServerSocket serverSocket = NetUtils.serverSocket();
		while(true) {
			if(!failing) {
				System.out.println("Waiting to accept incoming client...");
				SocketBundle client = NetUtils.acceptClient(serverSocket);
				Packet packet = new Packet();
				packet.readHeader(client);
				
				switch(packet.type) {
					case Packet.Type.Alive:
					case Packet.Type.Data:
					case Packet.Type.LSA:
					case Packet.Type.Neighbor:
						break;
				}
			}
		}
	}

}