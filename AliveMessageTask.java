package sLSRP;

import java.io.IOException;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class AliveMessageTask extends Thread {
    
    RouterData routerObject;
	int routerID = 0;
    private final int ALIVE_FAILURE_TIME = 10000;
    
	public AliveMessageTask(RouterData routerObject, int routerid){
		this.routerObject = routerObject;
		this.routerID = routerID;
		Timer timer=new Timer();  
		//The following will executed in 'helloInterval'  
		timer.schedule(new TimerTask(){
            @Override
            public void run() {
                if(AliveMessageTask.this.isAlive()){
                    AliveMessageTask.this.interrupt();
                    System.err.println("Time to build a connection with neighbor has reached a limit, so the thread is interrupted.");
                    //remove this router from router table
                    NeighborConnector.removeNeighbor(AliveMessageTask.this.routerID,AliveMessageTask.this.routerObject.routerID
                            ,AliveMessageTask.this.routerObject.ipAddress,AliveMessageTask.this.routerObject.port);
                }
            }
        }, ALIVE_FAILURE_TIME);
	}
	@Override
	public void run() {
		String ip = routerObject.ipAddress;
		ip = ip.replace("/", "");
		System.err.println("Try to send an alive message to a neighbor : "+ip);
		int port = routerObject.port;
		SocketBundle client = NetUtils.clientSocket(ip, port);
		int connectionType = 2;
		try {
			//Send the connection type
			client.out.writeInt(connectionType);
			//read response type
			int responseType = client.in.readInt();
			System.out.println("Successufully sent an alive message, the response type is: "+responseType);
		} catch (IOException e) {
			e.printStackTrace();
            NeighborConnector.removeNeighbor(AliveMessageTask.this.routerID, AliveMessageTask.this.routerObject.routerID,
                AliveMessageTask.this.routerObject.ipAddress, AliveMessageTask.this.routerObject.port);
		}
	}
}
