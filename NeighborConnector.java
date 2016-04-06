package sLSRP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;


public class NeighborConnector extends Thread {
	Configuration config;

	public NeighborConnector(Configuration config){
		this.config = config;
//		//Set up timer to count down how much time has been spent on the neighborhood request. 
//		Timer timer=new Timer();  
//		//The following will executed in 'helloInterval'  
//		timer.schedule(new TimerTask(){
//			@Override
//			public void run() {
//				if(NeighborConnector.this.isAlive()){
//					NeighborConnector.this.interrupt();
//					System.out.println("Time to build a connection with neighbor has reached a limit, so the thread is interrupted.");
//		        }
//		}}, this.config.helloInterval);
	}
	
	@Override
	public void run() {
		
		//Iterate over the neighbor list and establish connections with them.
		int numberOfNeighbors = config.configNeighbors.size();
		Iterator iterator = config.configNeighbors.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry entry = (Map.Entry)iterator.next();
			int routerID = (Integer)entry.getKey();
			String address = (String)entry.getValue();
			System.out.println("Try to establish a connection with the neighbor : "+address);
			String[] strs = address.split("=");
			String ip = strs[1].trim();
			int port = Integer.parseInt(strs[2]);
			
			sendNeighborRequest(config.routerID, routerID, ip, port);
		}
		//And then send LSAs to tell every neighbor about new established links.
		HashMap<Integer,RouterData> routers = NetworkInfo.getInstance().getRouters();
		Iterator iterator2 = routers.entrySet().iterator();
		while(iterator2.hasNext()){
			Map.Entry entry = (Map.Entry)iterator2.next();
			int routerID = (Integer)entry.getKey();
			LSATask task = new LSATask(routerID,LSAGenerator.getInstance(config, NetworkInfo.getInstance()).generateLSA());
			task.start();
		}
	    
	}
	
	public static void addNeighbor(int routerID, int neighborRouterID, String ip, int port) {
		// Add entries to the neighbor, router, and link lists
		NetworkInfo.getInstance().getRouters().put(neighborRouterID, new RouterData(neighborRouterID, ip, port));
		
		Link link = new Link(routerID, neighborRouterID);
		if(!NetworkInfo.getInstance().getLinks().contains(link)) {
			NetworkInfo.getInstance().getLinks().add(link);
		}
	}
	
	public static void removeNeighbor(int routerID, int neighborRouterID, String ip, int port) {
		// Remove entries from the neighbor, router, and link lists
		NetworkInfo.getInstance().getRouters().remove(neighborRouterID);
		
		Link link = new Link(routerID, neighborRouterID);
		NetworkInfo.getInstance().getLinks().remove(link);
	}
	
	public static void sendNeighborRequest(int routerID, int neighborRouterID, String ip, int port) {
	
		SocketBundle client = NetUtils.clientSocket(ip, port);
		long timeStamp = System.currentTimeMillis();
		int connectionType = 3;
		int requestType = 1;
		System.out.println("Connection established with neighbor, the delay is: " +port);
		
		try {
			//Send the connection type
			client.out.writeInt(connectionType);
			// Send the neighbor request type (1 = request, 2 = cease)
			client.out.writeInt(requestType);
			//Send this router's ID
			client.out.writeInt(routerID);
			//Send this router's port number so that the receiving router can connect to this router.
			client.out.writeInt(port);
			
			//read response type
			int responseType = client.in.readInt();
			
			client.socket.close();
			
			if(responseType == 1) {
				long laterTimeStamp = System.currentTimeMillis();
				int delay = (int) (laterTimeStamp - timeStamp);
				System.out.println("Connection established with neighbor, the delay is: " + delay);
				
				addNeighbor(routerID, neighborRouterID, ip, port);
			} else {
				System.out.println("The neighborhood request has been rejected by the other router, the response type is: "+responseType);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendCeaseNeighborRequest(int routerID, int neighborRouterID, String ip, int port) {
	
		SocketBundle client = NetUtils.clientSocket(ip, port);
		int connectionType = 3;
		int requestType = 2;
		
		try {
			//Send the connection type
			client.out.writeInt(connectionType);
			// Send the neighbor request type (1 = request, 2 = cease)
			client.out.writeInt(requestType);
			//Send this router's ID
			client.out.writeInt(routerID);
			//Send this router's port number so that the receiving router can connect to this router.
			client.out.writeInt(port);
			client.socket.close();
			
			removeNeighbor(routerID, neighborRouterID, ip, port);		
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
