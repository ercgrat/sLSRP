package sLSRP;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


public class PacketTask extends Thread {
	//
	Packet packet;
	
	RouterData routerObject = null;
	public PacketTask(Packet packet,int routerID,int failureInterval){
		this.packet = packet;
		
		//Check if the destination is this router
	    if(packet.destinationID==routerID){
	    	//
	    	
	    }else{
	    	//Get the SPF and send the packet to the destination
	    	LinkedList<Integer> paths = NetworkInfo.getInstance().getPath(packet.destinationID);
	    	//routerObject = NetworkInfo.getInstance().getRouters().get(this.packet.nextID)
	    	Timer timer=new Timer();  
			//The following will executed in 'helloInterval'  
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					if(PacketTask.this.isAlive()){
						PacketTask.this.interrupt();
						System.err.println("Time to send a packet has reached a limit, so the thread is interrupted.");
						//try to resend the packet
			        }
			}}, failureInterval);
	    }
	}
	@Override
	public void run() {
		if(routerObject!=null){
			//TODO Process the packet and set up socket connection
			String ip = routerObject.ipAddress;
			ip = ip.replace("/", "");
			int port = routerObject.port;
			System.out.println("Try to send a packet to the next destinated router : "+ip+":"+port);
			SocketBundle client = NetUtils.clientSocket(ip, port);
			int connectionType = 0;
			try {
				//Send the connection type
				client.out.writeInt(connectionType);
				//read response type
				int responseType = client.in.readInt();
				System.out.println("Successfully sent a packet out, the response type is: "+responseType);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
