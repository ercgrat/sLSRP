package sLSRP;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class AliveMessageTask extends Thread {
    
    RouterData routerObject;
	int routerID = 0;
    private final int ALIVE_FAILURE_TIME = 10000;
    Timer timer; 
    
	public AliveMessageTask(RouterData routerObject, int routerid){
		this.routerObject = routerObject;
		this.routerID = routerID;
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
                  //And then send LSAs to tell every neighbor about new established links.
            		HashMap<Integer,RouterData> routers = NetworkInfo.getInstance().getRouters();
            		Iterator iterator2 = routers.entrySet().iterator();
            		while(iterator2.hasNext()){
            			Map.Entry entry = (Map.Entry)iterator2.next();
            			int routerID = (Integer)entry.getKey();
                        try {
                            LSATask task = new LSATask(routerID,LSAGenerator.getInstance(NetworkInfo.getInstance().getConfiguration(), NetworkInfo.getInstance()).generateLSA());
                            task.start();
                        } catch(IOException e) {
                            System.out.println("Failed to send LSA update about new neighbor connection.");
                            e.printStackTrace();
                        }
            		}
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
		try {
			//Send the connection type
			client.out.writeInt(connectionType);
			//read response type
			int responseType = client.in.readInt();
			System.out.println("Successufully sent an alive message, the response type is: "+responseType);
		} catch (Exception e) {
			e.printStackTrace();
            NeighborConnector.removeNeighbor(AliveMessageTask.this.routerID, AliveMessageTask.this.routerObject.routerID,
                AliveMessageTask.this.routerObject.ipAddress, AliveMessageTask.this.routerObject.port);
          //And then send LSAs to tell every neighbor about new established links.
    		HashMap<Integer,RouterData> routers = NetworkInfo.getInstance().getRouters();
    		Iterator iterator2 = routers.entrySet().iterator();
    		while(iterator2.hasNext()){
    			Map.Entry entry = (Map.Entry)iterator2.next();
    			int routerID = (Integer)entry.getKey();
                try {
                    LSATask task = new LSATask(routerID,LSAGenerator.getInstance(NetworkInfo.getInstance().getConfiguration(), NetworkInfo.getInstance()).generateLSA());
                    task.start();
                } catch(IOException ex) {
                    System.out.println("Failed to send LSA update about new neighbor connection.");
                    ex.printStackTrace();
                }
    		}
		}
        timer.cancel();
        timer.purge();
        this.interrupt();
	}
}
