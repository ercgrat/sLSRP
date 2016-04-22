package sLSRP;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import sLSRP.NetworkInfo;
import sLSRP.RouterData;



public class PacketTask extends Thread {
    
	Packet packet;
	RouterData routerObject = null;

	public PacketTask(Packet packet,int routerID,int failureInterval){
		this.packet = packet;
		
		//Check if the destination is this router
	    if(packet.destinationID==routerID){
	    	//Send it to the sub-host
	    	//SimulateSubnetPacketServer.assemblePacket(packet);
	    	String ip = NetworkInfo.SERVER_IP ;
			int port = NetworkInfo.SERVER_PORT ;
			routerObject = new RouterData(0,ip,port);
	    }else{
	    	synchronized(NetworkInfo.getInstance()){
	    		//Get the SPF and send the packet to the destination
	    		//Get the SPF and send the packet to the destination
	    		//int size = NetworkInfo.getInstance().getLinks().size();
	    		//for(int i=0;i<size;i++){
	    		//	System.out.println("A--->"+NetworkInfo.getInstance().getLinks().get(i).A);
	    		//	System.out.println("B--->"+NetworkInfo.getInstance().getLinks().get(i).B);
	    		//}
	    		NetworkInfo.getInstance().execute(routerID);
		    	LinkedList<Integer> paths = NetworkInfo.getInstance().getPath(packet.destinationID);
		    	//System.out.println("path--->"+paths.get(0));
		    	routerObject = NetworkInfo.getInstance().getNeighbors().get(paths.get(1));
		    	//System.out.println("path 2--->"+paths.get(1));
		    	
		    	
		    	Timer timer=new Timer();  
				//The following will executed in 'helloInterval'  
				timer.schedule(new TimerTask(){
					@Override
					public void run() {
						if(PacketTask.this.isAlive()){
							PacketTask.this.interrupt();
							System.out.println("\033[31;4mRed Time to send a packet has reached a limit, so the thread is interrupted.\033[0m");
							//try to resend the packet
				        }
				}}, failureInterval);
	    	}
	    	
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
			int connectionType = 1;
			try {
				//Send the connection type
				client.out.writeInt(connectionType);
                packet.forward(client.out);
				//read response type
				int responseType = client.in.readInt();
				System.out.println("Successfully sent a packet out, the response type is: "+responseType);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
