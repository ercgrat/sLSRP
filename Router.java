package sLSRP;
import java.io.IOException;
import java.net.*;
import java.util.*;

import sLSRP.AliveMessageTask;
import sLSRP.NeighborConnector;
import sLSRP.NetworkInfo;

public class Router {

	public static volatile boolean failing = false;	
    private static boolean needToRecover = false;
    private static ArrayList<Integer> recoveryNeighbors = new ArrayList<Integer>();
	private static final int ACK_FLAG = 100; //The universal acknowledgement number
	
	public static void main(String args[]) {
        
        if(args.length != 2) {
            System.out.println("Invalid number of arguments. Please provide a router id and configuration filename, e.g.:\n\tjava sLSRP/Router 1 config.txt");
            return;
        }
        
		// Read in configuration
		Configuration config = new Configuration(args[0], args[1]);
		NetworkInfo.getInstance().setConfiguration(config);
		
		// Create data structures
		RoutingTable routingTable = new RoutingTable(config);
		
		// Fork all threads
        
		// Create socket and listen, get ip/port info
		ServerSocket serverSocket = NetUtils.serverSocket();
		try {
			config.routerIpAddress = IpChecker.getIp();
		} catch(IOException e) {
			System.out.println(e);
		}
		config.routerPort = serverSocket.getLocalPort();
		System.out.println("Listening on port " + serverSocket.getLocalPort() + " at IP Address " + config.routerIpAddress + ".");
		
        // Connect to name server and register self
        try {
            SocketBundle client = NetUtils.clientSocket(config.nameServerIp, config.nameServerPort);
            client.out.writeInt(0); // register router
            client.out.writeInt(config.routerID); // router ID to register
            client.out.writeInt(serverSocket.getLocalPort());
            client.socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        // Loop over neighbors in the configuration
		NeighborConnector connector = new NeighborConnector(config, NetworkInfo.getInstance());
		connector.start();
        
        //Alive message thread that handle all the ongoing alive messages
		AliveMessageDeamon aliveMessageDeamon = new AliveMessageDeamon(config);
		aliveMessageDeamon.start();
        
        // LSA utility singleton
        LSAProcessor lsaProcessor = LSAProcessor.getInstance(config, NetworkInfo.getInstance());
        
        // LSA generating thread
        LSAGeneratorDaemon lsaGenerator = new LSAGeneratorDaemon(config, NetworkInfo.getInstance(), lsaProcessor);
        lsaGenerator.start();
        
        // Router failure daemon
        RouterFailureDaemon failureDaemon = new RouterFailureDaemon(config, NetworkInfo.getInstance());
        failureDaemon.start();
        
		// Create user interface
		UserInterface ui = new UserInterface(config, NetworkInfo.getInstance());
		ui.start();
		
		while(true) {
			if(!failing) {
                if(needToRecover) {
                    System.out.println("Recovering from failure... reestablishing neighbor connections...");
                    for(Integer routerID : recoveryNeighbors) {
                        NeighborConnector.addNeighborViaNameServer(routerID, config);
                    }
                    needToRecover = false;
                }
                
				System.out.println("Waiting to accept incoming client...");
				SocketBundle client = NetUtils.acceptClient(serverSocket);
				String ip = client.socket.getInetAddress().toString();
				int packetType = -1;
				try {
					packetType = client.in.readInt();
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				switch(packetType) {
					case 0://If LSA, call the algorithm to calculate the short path and put the result into the table, then send it to all the neighbors.
						LSA lsa = null;
						
						try {
							lsa = new LSA(client.in);
							System.out.println("Receive an LSA, sequenceNumber: "+lsa.sequenceNumber+"  router: "+lsa.router);
                            lsaProcessor.processLSA(lsa);
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
                            packet = new Packet(client.in);
						    client.out.writeInt(ACK_FLAG);
					    } catch (IOException e) {
						    e.printStackTrace();
					    }
					    //Check if the destination is this router
					    PacketTask task = new PacketTask(packet,config.routerID,config.failureInterval);
					    task.start();
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
								if(config.neighborBlacklist.contains(routerID+"")){ // Send NAK flag
									client.out.writeInt(0);
									System.out.println("This router has been denied neighborhood: " + routerID + ", " + ip + ", " + port);
								} else { //Send ACK flag
                                    NeighborConnector.addNeighbor(config.routerID, routerID, ip, port, 0);
									client.out.writeInt(1);
								}
							} else if(requestType == 2) { // Cease neighbors
								NeighborConnector.removeNeighbor(config.routerID, routerID, ip, port);
							} else {
								System.out.println("Neighbor packet had invalid request type.");
							}						    
					    } catch (IOException e) {
						    e.printStackTrace();
					    }
						break;
					case 4://Server host registration 
						try {
							//Read the port
							int port = client.in.readInt();
							System.out.println("Received a host registration request: " + ip + ", " + port);
							//Add the host to the host list
							NetworkInfo.SERVER_IP = ip;
							NetworkInfo.SERVER_PORT = port;
						    client.out.writeInt(ACK_FLAG);
					    } catch (IOException e) {
						    e.printStackTrace();
					    }
						break;
                    default:
                        System.out.println("Received invalid connection type " + packetType);
                        break;
				}
			} else {
                NetworkInfo netInfo = NetworkInfo.getInstance();
                synchronized(netInfo) {
                    Iterator iterator = netInfo.getNeighbors().keySet().iterator();
                    while(iterator.hasNext()) {
                        Integer routerID = (Integer)iterator.next();
                        recoveryNeighbors.add(routerID);
                    }
                    netInfo.getNeighbors().clear();
                    netInfo.getLinks().clear();
                }
                needToRecover = true;
            }
		}
	}

}