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
		
		int numberOfNeighbors = config.neighbors.length;
		
		for(int i =0;i<numberOfNeighbors;i++){
			String address = config.neighbors[i];
			System.out.println("Try to establish a connection with the neighbor : "+address);
			String[] strs = address.split("=");
			String ip = strs[0].trim();
			int port = Integer.parseInt(strs[1]);
//			System.out.println("Try to establish a connection with the neighbor : "+ip+":"+port);
			SocketBundle client = NetUtils.clientSocket(ip, port);
			int connectionType = 1;
			try {
				//Send the connection type
				client.out.writeInt(connectionType);
				//read response type
				int responseType = client.in.readInt();
				System.out.println("Connection established with neighbor, the response type is: "+responseType);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
