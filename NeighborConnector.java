package sLSRP;

import java.io.IOException;


public class NeighborConnector extends Thread {
	Configuration config;
	public NeighborConnector(Configuration config){
		this.config = config;
	}
	@Override
	public void run() {
		//TODO Iterate the neighbor list and establish connection with them.
		SocketBundle client = NetUtils.clientSocket("", 1033);
		int connectionType = 1;
		try {
			//Send the connection type
			client.out.writeInt(connectionType);
			//read response type
			int responseType = client.in.readInt();
			System.out.println("Build connection with neighbor, the response type is: "+responseType);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
