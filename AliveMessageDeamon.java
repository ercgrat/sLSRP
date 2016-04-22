package sLSRP;

import java.util.*;

public class AliveMessageDeamon extends Thread {
    
    Configuration config;
	
    public AliveMessageDeamon(Configuration config){
        this.config = config;
	}
    
	@Override
	public void run() {
		while(true){
			
			try {
				Thread.sleep(config.helloInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Periodically set up socket connection to send alive messages
            if(!Router.failing) {
                synchronized(NetworkInfo.getInstance()) {
                    HashMap<Integer,RouterData> routers = NetworkInfo.getInstance().getNeighbors();
                    List<Link> links = NetworkInfo.getInstance().getLinks();
                    
                    Iterator iterator = routers.entrySet().iterator();
                    while(iterator.hasNext()){
                        Map.Entry entry = (Map.Entry)iterator.next();
                        int routerID = (Integer)entry.getKey();
                        Link searchLink = new Link(routerID, config.routerID);
                        Link existingLink = links.get(links.indexOf(searchLink));
                        AliveMessageTask task = new AliveMessageTask(routers.get(routerID), config.routerID, existingLink, LSAProcessor.getInstance(config, NetworkInfo.getInstance()));
                        task.start();
                    }
                }
            }
		}
	}
}
