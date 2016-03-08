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