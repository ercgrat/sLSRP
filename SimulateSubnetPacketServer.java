package sLSRP;

import java.io.Console;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Scanner;

/*
 * This class is used for testing packet or file transfer between routers;
 * This is a separate program that simulates accepting packets from the network.
 */
public class SimulateSubnetPacketServer {
	//The key at the first level is ID of the object that packets belong to, 
	//the second key is sequenceID that identifies a packet.
	static HashMap<String,HashMap<Integer,Packet>> packTable = new HashMap<String,HashMap<Integer,Packet>>();
	
	public static void assemblePacket(Packet packet){
		if(packTable.containsKey(packet.contentType)){//if the table contains storage info of incoming object(files)
			if(packTable.get(packet.contentType).containsKey(packet.sequenceID)){
				//drop the packet because this packet has been received.
			}else{
				packTable.get(packet.contentType).put(packet.sequenceID, packet);
			}
		}else{
			HashMap<Integer,Packet> m = new HashMap<Integer,Packet>();
			m.put(packet.sequenceID, packet);
			packTable.put(packet.contentType, m);
		}
		//if we have all the packets we need to assemble them into an whole object file
		if(packet.isLastPacket){
			
		}
	}
	public static volatile boolean failing = false;	
	private static final int ACK_FLAG = 100; //The universal acknowledgement number
	public static void main(String[] args) {
		
		//First, register itself  to its edge router
		//Then start to listen to incoming packets from its edge routers
		// Create socket and listen, get ip/port info
		ServerSocket serverSocket = NetUtils.serverSocket();
		String localIp = null;
		try {
			localIp = IpChecker.getIp();
		} catch(IOException e) {
			System.out.println(e);
		}
		final int localPort = serverSocket.getLocalPort();
		System.out.println("Listening on port " + serverSocket.getLocalPort() + " at IP Address " + localIp + ".");
		//Register itself to its edge router
		new Thread(){
			@Override
			public void run() {
				String ip = "";
				int port = 0;
				System.out.println("Try to register itself to its edge router : "+ip+":"+port);
				SocketBundle client = NetUtils.clientSocket(ip, port);
				int connectionType = 4;
				try {
					//Send the connection type
					client.out.writeInt(connectionType);
					client.out.writeInt(localPort);
					//read response type
					int responseType = client.in.readInt();
					System.out.println("Successfully registered: "+responseType);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}.start();
		while(true) {
			if(!failing) {
				System.out.println("Waiting to accept incoming packets...");
				SocketBundle client = NetUtils.acceptClient(serverSocket);
				String ip = client.socket.getInetAddress().toString();
				int packetType = -1;
				try {
					packetType = client.in.readInt();
				} catch(Exception e) {
					System.out.println(e);
				}
				
				switch(packetType) {
					
					case 1:// If a packet, call the algorithm to calculate the short path and put the result into the table, then send it to all the neighbors.
						Packet packet = null;
					    try {
                            packet = new Packet(client.in);
						    client.out.writeInt(ACK_FLAG);
					    } catch (IOException e) {
						    e.printStackTrace();
					    }
					    assemblePacket(packet);
						break;
				}
			}
		}
	}

}
