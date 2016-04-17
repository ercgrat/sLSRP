package sLSRP;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
			HashMap<Integer,RouterData> routers = NetworkInfo.getInstance().getRouters();
			
			Iterator iterator = routers.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry entry = (Map.Entry)iterator.next();
				int routerID = (Integer)entry.getKey();
				AliveMessageTask task = new AliveMessageTask(routers.get(routerID), config.routerID, LSAProcessor.getInstance(config, NetworkInfo.getInstance()));
				task.start();
			}
			
		}
	}
}
