package sLSRP;
import java.net.*;
import java.util.*;

public class Router {

	public static volatile boolean failing = false;
	

	public static void main(String args[]) {
		// Read in configuration
		Configuration config = new Configuration();
		
		// Create data structures
		Links linkData = new Links(config);
		RoutingTable routingTable = new RoutingTable(config);
		LSAHistory history = new LSAHistory(config);
		
		// Loop over neighbors in the configuration
		NeighborConnector connector = new NeighborConnector(config);
		connector.start();
		
		// Fork all threads
		
		//LSA thread that processes all the LSAs
		Queue LSAQueue = new LinkedList();
		WorkerThread LSAWorker = new WorkerThread(LSAQueue);
    	
    	//Establish the packet thread which processes all the incoming packet and send them to the next router
    	Queue packetQueue = new LinkedList();
    	WorkerThread packetWorker = new WorkerThread(packetQueue);
    	
    	//Alive message thread that handle all the ongoing alive messages
    	Queue aliveMessageQueue = new LinkedList();
    	WorkerThread aliveMessageWorker = new WorkerThread(aliveMessageQueue);
    	
    	//Add one task thread to the queue, this task will periodically send alive messages, so it will keep acitve all the time.
    	AliveMessageTask aliveMessageTask = new AliveMessageTask(config.helloInterval,true);
    	aliveMessageQueue.add(aliveMessageTask);
    	aliveMessageQueue.notify();
		
		// Create socket and listen
		ServerSocket serverSocket = NetUtils.serverSocket();
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
				    	    Runnable task = new LSATask("");
				    	    LSAQueue.add(task);
				    	    //Call the queue to process the task
				    	    LSAQueue.notify();
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
						break;
					case 2://TODO If Receive an alive message, send an ACK back to the sender???
						synchronized (aliveMessageQueue) {
				    	    // Should send an ACK immediatelly in listening thread(in this thread) 
				        	
				    	}
						break;
					case 3:
						break;
				}
			}
		}
	}

}