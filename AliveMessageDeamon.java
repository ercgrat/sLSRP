package sLSRP;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AliveMessageDeamon extends Thread {
	int interval = 0;
	int failureInterval;
	int routerID = 0;
	public AliveMessageDeamon(int interval,int failureInterval,int routerID){
		this.interval = interval;
		this.failureInterval = failureInterval;
		this.routerID = routerID;
	}
	@Override
	public void run() {
		while(true){
			
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Periodically set up socket connection to send alive messages
			HashMap<Integer,RouterData> routers = NetworkInfo.getInstance().getRouters();
			
			Iterator iterator = routers.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry entry = (Map.Entry)iterator.next();
				int routerID = (Integer)entry.getKey();
				AliveMessageTask task = new AliveMessageTask(routers.get(routerID),failureInterval,this.routerID);
				task.start();
			}
			
		}
	}
}
