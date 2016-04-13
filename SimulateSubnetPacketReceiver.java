package sLSRP;

import java.io.Console;
import java.util.HashMap;
import java.util.Scanner;

/*
 * This class is used for testing packet or file transfer between routers;
 * This is a separate program that simulates accepting packets from the network.
 */
public class SimulateSubnetPacketReceiver {
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
	
//	public static void main(String[] args) {
//		//TODO
////		Scanner reader = new Scanner(System.in);
////		System.out.println("Enter a file path: ");
////		reader.nextInt();
//		
//		Console console = System.console();
//		String s = console.readLine();
//		String text = console.readLine();
//	}

}
