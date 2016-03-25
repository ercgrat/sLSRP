package sLSRP;
import java.net.*;
import java.util.*;

public class Router {

	public static volatile boolean failing = false;
	
	//Task queue for storing all the LSA tasks
	private Queue LSAQueue;
	//Worker thread for invoking LSA tasks
	private WorkerThread LSAworker;
	
	//Task queue for storing all the packet tasks
	private Queue packetQueue;
	//Worker thread for invoking packet tasks
	private WorkerThread packetworker;
	
	private Queue aliveMessageQueue;
	private WorkerThread aliveMessageworker;
	

	public static void main(String args[]) {
		// Read in configuration
		Configuration config = new Configuration();
		
		// Create data structures
		Links linkData = new Links(config);
		RoutingTable routingTable = new RoutingTable(config);
		LSAHistory history = new LSAHistory(config);
		
		// Loop over neighbors in the configuration
		
		// Fork all threads
		
	    LSAQueue = new LinkedList();
    	LSAworker = new WorkerThread(LSAQueue);
    	
    	packetQueue = new LinkedList();
    	packetworker = new WorkerThread(LSAQueue);
    	
    	//The alive message task only has to be called only once and there is only one item in the queue
    	AliveMessageTask aliveMessageTask = new AliveMessageTask(config.helloInterval);
    	aliveMessageQueue = new LinkedList();
    	aliveMessageQueue.add(aliveMessageTask);
    	aliveMessageQueue.notify();
    	aliveMessageworker = new WorkerThread(aliveMessageQueue);
		
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
					case 0://TODO LSA???
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
							Packet packet ;
				    	    Runnable task = new PacketTask(packet);
				    	    packetQueue.add(task);
				    	    //Call the queue to process the task
				    	    packetQueue.notify();
				    	}
						break;
					case 2:
						break;
					case 3:
						break;
				}
			}
		}
	}

}