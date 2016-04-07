package sLSRP;
import java.io.IOException;
import java.net.*;
import java.util.*;

import sLSRP.AliveMessageTask;
import sLSRP.NeighborConnector;

public class Router {

	public static volatile boolean failing = false;	
	private static final int ACK_FLAG = 100; //The universal acknowledgement number
	
	public static void main(String args[]) {
		// Read in configuration
		Configuration config = new Configuration();
		NetworkInfo.getInstance().setConfiguration(config);
		
		// Create data structures
		RoutingTable routingTable = new RoutingTable(config);
		LSAHistory history = new LSAHistory(config);
		
		
		// Loop over neighbors in the configuration
		NeighborConnector connector = new NeighborConnector(config);
		connector.start();
		
		// Fork all threads
		    	
		//Alive message thread that handle all the ongoing alive messages
		AliveMessageDeamon aliveMessageDeamon = new AliveMessageDeamon(config.helloInterval,config.failureInterval,config.routerID);
		aliveMessageDeamon.start();
		
		// Create socket and listen, get ip/port info
		ServerSocket serverSocket = NetUtils.serverSocket();
		try {
			config.routerIpAddress = IpChecker.getIp();
		} catch(IOException e) {
			System.out.println(e);
		}
		config.routerPort = serverSocket.getLocalPort();
		System.out.println("Listening on port " + serverSocket.getLocalPort() + " at IP Address " + config.routerIpAddress + ".");
		
		// Create user interface
//		UserInterface ui = new UserInterface(config, NetworkInfo.getInstance());
//		ui.run();
		
		while(true) {
			if(!failing) {
				System.out.println("Waiting to accept incoming client...");
				SocketBundle client = NetUtils.acceptClient(serverSocket);
				String ip = client.socket.getInetAddress().toString();
				int packetType = -1;
				try {
					packetType = client.in.readInt();
				} catch(Exception e) {
					System.out.println(e);
				}
				
				switch(packetType) {
					case 0://If LSA, call the algorithm to calculate the short path and put the result into the table, then send it to all the neighbors.
						LSA lsa = null;
						
						try {
							lsa = new LSA(client.in);
							System.out.println("Receive an LSA, sequenceNumber: "+lsa.sequenceNumber+"  router: "+lsa.router);
                            LSAGenerator.getInstance(config, NetworkInfo.getInstance()).processLSA(lsa);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						try {
						    client.out.writeInt(ACK_FLAG);
					    } catch (IOException e) {
						    e.printStackTrace();
					    }
						break;
					case 1:// If a packet, call the algorithm to calculate the short path and put the result into the table, then send it to all the neighbors.
						Packet packet = null;
					    try {
						    client.out.writeInt(ACK_FLAG);
					    } catch (IOException e) {
						    e.printStackTrace();
					    }
					    //Check if the destination is this router
					    PacketTask task = new PacketTask(packet,config.routerID,config.failureInterval);
					    //task.start();
						break;
					case 2://Receive an alive message, send an ACK back to the sender.
						
						try {
						    client.out.writeInt(ACK_FLAG);
					    } catch (IOException e) {
						    e.printStackTrace();
					    }
						break;
					case 3://Establish Neighborhood 
						try {
							//Read the request type to figure out if it's a request or a cease
							int requestType = client.in.readInt();
							//Get the router ID and check if it is in the neighbor list and out of the blacklist.
							int routerID = client.in.readInt();
							//Read the port
							int port = client.in.readInt();
							System.out.println("Neighbor establishment request: " + routerID + ", " + ip + ", " + port);
							
							if(requestType == 1) { // Request neighbors
//								if(config.configNeighbors.containsKey(routerID)){ //Add and send confirmation flag
									NeighborConnector.addNeighbor(config.routerID, routerID, ip, port);
									client.out.writeInt(1);
//								} else { //Send deny flag
//									client.out.writeInt(0);
//									System.out.println("This router has been denied neighborhood: " + routerID + ", " + ip + ", " + port);
//								}
							} else if(requestType == 2) { // Cease neighbors
								NeighborConnector.removeNeighbor(config.routerID, routerID, ip, port);
							} else {
								System.out.println("Neighbor packet had invalid request type.");
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