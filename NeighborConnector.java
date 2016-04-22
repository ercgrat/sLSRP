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
    NetworkInfo netInfo;

	public NeighborConnector(Configuration config, NetworkInfo netInfo){
		this.config = config;
        this.netInfo = netInfo;
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
			
			sendNeighborRequest(config.routerID, config.routerPort, routerID, ip, port);
		}
        
		//And then send LSAs to tell every neighbor about new established links.
		LSA lsa = LSAProcessor.getInstance(config, netInfo).createLSA();
        LSAProcessor.getInstance(config, netInfo).broadcastLSA(lsa);
	    
	}
	
	public static void addNeighbor(int routerID, int neighborRouterID, String ip, int port, int delay) {
        synchronized(NetworkInfo.getInstance()) {
            // Add entries to the neighbor, router, and link lists
            NetworkInfo.getInstance().getNeighbors().put(neighborRouterID, new RouterData(neighborRouterID, ip, port));
            
            Link link = new Link(routerID, neighborRouterID);
            link.delay = delay;
            if(!NetworkInfo.getInstance().getLinks().contains(link)) {
                NetworkInfo.getInstance().getLinks().add(link);
                System.out.println("Adding link from this router (" + routerID + ") to (" + neighborRouterID + ")");
            }
        }
	}
	
	public static void removeNeighbor(int routerID, int neighborRouterID, String ip, int port) {
		synchronized(NetworkInfo.getInstance()) {
            // Remove entries from the neighbor, router, and link lists
            NetworkInfo.getInstance().getNeighbors().remove(neighborRouterID);
            
            Link searchLink = new Link(routerID, neighborRouterID);
            List<Link> links = NetworkInfo.getInstance().getLinks();
            links.remove(links.indexOf(searchLink));
        }
	}
    
    public static void addNeighborViaNameServer(int routerId, Configuration config) {
        try {
            SocketBundle client = NetUtils.clientSocket(config.nameServerIp, config.nameServerPort);
            client.out.writeInt(1); // Request router info
            client.out.writeInt(routerId); // Router to get info about
            
            int routerDataLength = client.in.readInt();
            String routerData = "";
            for(int i = 0; i < routerDataLength; i++) {
                routerData += client.in.readChar();
            }
            client.socket.close();
            System.out.println("router data length: " + routerDataLength);
            System.out.println("router data: " + routerData);
            
            String[] routerDataInfo = routerData.split("=");
            sendNeighborRequest(config.routerID, config.routerPort, Integer.parseInt(routerDataInfo[0]), routerDataInfo[1], Integer.parseInt(routerDataInfo[2]));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void sendNeighborRequest(int routerID, int routerPort, int neighborRouterID, String neighborIp, int neighborPort) {
	
		SocketBundle client = NetUtils.clientSocket(neighborIp, neighborPort);
		long timeStamp = System.currentTimeMillis();
		int connectionType = 3;
		int requestType = 1;
		
		try {
			//Send the connection type
			client.out.writeInt(connectionType);
			// Send the neighbor request type (1 = request, 2 = cease)
			client.out.writeInt(requestType);
			//Send this router's ID
			client.out.writeInt(routerID);
			//Send this router's port number so that the receiving router can connect to this router.
			client.out.writeInt(routerPort);
			
			//read response type
			int responseType = client.in.readInt();
			
			client.socket.close();
			
			if(responseType == 1) {
				long laterTimeStamp = System.currentTimeMillis();
				int delay = (int) (laterTimeStamp - timeStamp);
				System.out.println("Connection established with neighbor, the delay is: " + delay);
				
				addNeighbor(routerID, neighborRouterID, neighborIp, neighborPort, delay);
                
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
