package sLSRP;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class NeighborConnector extends Thread {
	Configuration config;
	 

	public NeighborConnector(Configuration config){
		this.config = config;
		//Set up timer to count down how much time has been spent on the neighborhood request. 
		Timer timer=new Timer();  
		//The following will executed in 'helloInterval'  
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				if(NeighborConnector.this.isAlive()){
					NeighborConnector.this.interrupt();
					System.out.println("Time to build a connection with neighbor has reached a limit, so the thread is interrupted.");
		        }
		}}, this.config.helloInterval);
	}
	@Override
	public void run() {
		
		//Iterate the neighbor list and establish connection with them.
		
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
//			System.out.println("Try to establish a connection with the neighbor : "+ip+":"+port);
			SocketBundle client = NetUtils.clientSocket(ip, port);
			int connectionType = 3;
			try {
				//Send the connection type
				client.out.writeInt(connectionType);
				//Send this router's ID
				client.out.writeInt(config.routerID);
				//Send this router's port number so that the receiving router can connect to this router.
				client.out.writeInt(config.routerPort);
				//read response type
				int responseType = client.in.readInt();
				if(responseType==1){
					System.out.println("Connection established with neighbor, the response type is: "+responseType);
					//Put the other router into current neighbor list
					NetworkInfo.getInstance().getNeighbors().add(Integer.parseInt(strs[0]));
				}else{
					System.out.println("The neighborhood request has been rejected by the other router, the response type is: "+responseType);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
