package sLSRP;
import java.io.IOException;
import java.net.*;
import java.util.*;

import sLSRP.AliveMessageTask;
import sLSRP.NeighborConnector;
import sLSRP.WorkerThread;

public class Router {

	public static volatile boolean failing = false;
	
	
	//The universal acknowledgement number
	private static final int ACK_FLAG = 100;
	
	
	public static void main(String args[]) {
		// Read in configuration
		Configuration config = new Configuration();
		
		// Create data structures
		RoutingTable routingTable = new RoutingTable(config);
		LSAHistory history = new LSAHistory(config);
		
		
		//LSA thread that processes all the LSAs
		Queue LSAQueue = new LinkedList();
		WorkerThread LSAWorker = new WorkerThread(LSAQueue);
		LSAWorker.start();
		
		// Loop over neighbors in the configuration
		NeighborConnector connector = new NeighborConnector(config,LSAQueue);
		connector.start();
		
		// Fork all threads
		    	
		//Establish the packet thread which processes all the incoming packet and send them to the next router
		Queue packetQueue = new LinkedList();
		WorkerThread packetWorker = new WorkerThread(packetQueue);
		//packetWorker.start();
		    	
		//Alive message thread that handle all the ongoing alive messages
		Queue aliveMessageQueue = new LinkedList();
		//Add one task thread to the queue, this task will periodically send alive messages, so it will keep acitve all the time.
		AliveMessageTask aliveMessageTask = new AliveMessageTask(config.helloInterval,true,LSAQueue);
		aliveMessageQueue.add(aliveMessageTask);
		WorkerThread aliveMessageWorker = new WorkerThread(aliveMessageQueue);
		//aliveMessageWorker.start();
		
		
		// Create socket and listen
		ServerSocket serverSocket = NetUtils.serverSocket();
		config.routerPort = serverSocket.getLocalPort();
		System.out.println("Listening on port " + serverSocket.getLocalPort() + ".");
		
		while(true) {
			if(!failing) {
				System.out.println("Waiting to accept incoming client...");
				SocketBundle client = NetUtils.acceptClient(serverSocket);
				int packetType = -1;
				try {
					packetType = client.in.readInt();
				} catch(Exception e) {
					System.out.println(e);
				}
				
				switch(packetType) {
					case 0://TODO If LSA, call algorithm to calculate the short path and put the rusult into the table, then send an to neighbors???
						synchronized (LSAQueue) {
				    	    // Add LSA task to the queue 
				    	    Runnable task = new LSATask(new ArrayList());
				    	    LSAQueue.add(task);
				    	    //Call the queue to process the task
				    	    LSAQueue.notify();
				    	}
						try {
						    client.out.writeInt(ACK_FLAG);
					    } catch (IOException e) {
						    e.printStackTrace();
					    }
						break;
					case 1://TODO Packet???
						synchronized (packetQueue) {
				    	    // Add packet task to the queue 
							Packet packet = null;
				    	    Runnable task = new PacketTask(packet);
				    	    packetQueue.add(task);
				    	    //Call the queue to process the task
				    	    packetQueue.notify();
				    	}
					    try {
						    client.out.writeInt(ACK_FLAG);
					    } catch (IOException e) {
						    e.printStackTrace();
					    }
						break;
					case 2://TODO If Receive an alive message, send an ACK back to the sender???
						synchronized (aliveMessageQueue) {
				    	    //Should send an ACK immediately in listening thread(in this thread) 
				        	
				    	}
						try {
						    client.out.writeInt(ACK_FLAG);
					    } catch (IOException e) {
						    e.printStackTrace();
					    }
						break;
					case 3://Establish Neighborhood 
						try {
							//Get the router ID and check if it is in the neighbor list and out of the blacklist.
							int routerID = client.in.readInt();
							System.out.println("Neighbor establishment request:"+routerID);
							if(config.configNeighbors.containsKey(routerID)){
								NetworkInfo.getInstance().getNeighbors().add(routerID);
								//Send the confirmation flag
								client.out.writeInt(1);
							}else{
								//Send the deny flag
								client.out.writeInt(0);
								System.out.println("This router has not been registered for a neighborhood :"+routerID);
							}
						    
					    } catch (IOException e) {
						    e.printStackTrace();
					    }
						break;
				}
			}
		}
	}

}