package sLSRP;

import java.io.IOException;
import java.util.*;

public class AliveMessageTask extends Thread {
    
    RouterData routerObject;
    Link neighborLink;
    final LSAProcessor processor;
	int routerID = 0;
    private final int ALIVE_FAILURE_TIME = 500;
    Timer timer; 
    
	public AliveMessageTask(RouterData routerObject, int routerID, Link neighborLink, LSAProcessor processorInstance){
		this.routerObject = routerObject;
		this.routerID = routerID;
        this.neighborLink = neighborLink;
        this.processor = processorInstance;
        timer = new Timer();
		timer.schedule(new TimerTask(){
            @Override
            public void run() {
                if(AliveMessageTask.this.isAlive()){
                    AliveMessageTask.this.interrupt();
                    System.err.println("Time to build a connection with neighbor has reached a limit, so the thread is interrupted.");
                    //remove this router from router table
                    NeighborConnector.removeNeighbor(AliveMessageTask.this.routerID,AliveMessageTask.this.routerObject.routerID
                            ,AliveMessageTask.this.routerObject.ipAddress,AliveMessageTask.this.routerObject.port);
                    
                    // The neighbor router failed, so remove all associated links
                    synchronized(NetworkInfo.getInstance()) {
                        List<Link> links = NetworkInfo.getInstance().getLinks();
                        ArrayList<Link> removeLinks = new ArrayList<Link>();
                        for(Link link : links) {
                            if(link.A == AliveMessageTask.this.routerObject.routerID || link.B == AliveMessageTask.this.routerObject.routerID) {
                                removeLinks.add(link);
                            }
                        }
                        links.removeAll(removeLinks);
                    }
                    
                    //And then send LSAs to tell every neighbor about new established links.
            		LSA lsa = processor.createLSA();
                    processor.broadcastLSA(lsa);
                }
            }
        }, ALIVE_FAILURE_TIME);
	}
	@Override
	public void run() {
		String ip = routerObject.ipAddress;
		ip = ip.replace("/", "");
		int port = routerObject.port;
		System.err.println("Try to send an alive message to a neighbor : " + ip + ", port: " + port);
		
		SocketBundle client = NetUtils.clientSocket(ip, port);
		int connectionType = 2;
        long timeStamp = System.currentTimeMillis();
        
		try {
			//Send the connection type
			client.out.writeInt(connectionType);
			//read response type
			int responseType = client.in.readInt();
            long laterTimeStamp = System.currentTimeMillis();
            int delay = (int) (laterTimeStamp - timeStamp);
            neighborLink.delay = delay;
			System.out.println("Successufully sent an alive message, the response type is: "+responseType);
		} catch (Exception e) {
			e.printStackTrace();
            NeighborConnector.removeNeighbor(AliveMessageTask.this.routerID, AliveMessageTask.this.routerObject.routerID,
                AliveMessageTask.this.routerObject.ipAddress, AliveMessageTask.this.routerObject.port);
            
            // The neighbor router failed, so remove all associated links
            synchronized(NetworkInfo.getInstance()) {
                List<Link> links = NetworkInfo.getInstance().getLinks();
                ArrayList<Link> removeLinks = new ArrayList<Link>();
                for(Link link : links) {
                    if(link.A == AliveMessageTask.this.routerObject.routerID || link.B == AliveMessageTask.this.routerObject.routerID) {
                        removeLinks.add(link);
                    }
                }
                links.removeAll(removeLinks);
            }
            
            LSA lsa = processor.createLSA();
            processor.broadcastLSA(lsa);
		}
        timer.cancel();
        timer.purge();
        this.interrupt();
	}
}
