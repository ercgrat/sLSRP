package sLSRP;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import sLSRP.NetUtils;
import sLSRP.SocketBundle;

/*
 * This class is used for testing packet or file transfer between routers;
 * This is a separate program that simulating injecting packets into the network, 
 * then the network sends these packets to the subnet receiver.
 */
public class SimulateSubnetPacketClient {
	private static final int ACK_FLAG = 100; //The universal acknowledgement number
	static int packetLength = 512;
	static int senderId =2;
	static int destinationID =1; 
	
	static String ip = "10.0.0.7";
	static int port = 49345;
	
    
	public SimulateSubnetPacketClient(int packetLength){
		this.packetLength = packetLength;
	}
    
	static ArrayList<Byte> readFile(String filePath){
        FileInputStream inputStream;
        ArrayList<Byte> list = new ArrayList<Byte>();
		try {
			inputStream = new FileInputStream(filePath);
	        int dataByte = 0;
	        
	        while((dataByte = inputStream.read()) != -1) {
	        	list.add((byte)dataByte);
	        }   
	        inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return list;
	}
    static void sendFile(String filePath,String fileName){
    	ArrayList<Byte> list = readFile(filePath);
    	ArrayList<Packet> packets = generatePackets(list,fileName);
    	//Send the packets
    	for(final Packet packet:packets){
    		new Thread(){
    			@Override
    			public void run() {
    				//We want to make sure the packets will be received by its destination in order
    				//so every time, we send a packet, make the thread wait for a while
    				try {
						this.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    				connectToEdgeRouter(ip,port, packet);
    			}
        	}.start();
    	}
    	
    }
	static ArrayList<Packet> generatePackets(ArrayList<Byte> list,String fileName){
		ArrayList<Packet> packetList = new ArrayList<Packet>();
		int sequenceID = 1;
		int fromIndex = 0;
		int toIndex = fromIndex+packetLength;
		boolean flag = true;
		while(flag){
			if(toIndex>=list.size()){
				System.out.println("toIndex>=list.size()");
				List<Byte> sublist = (List<Byte>) list.subList(fromIndex, list.size());
				byte list2[] = new byte[sublist.size()];
				for(int i=0;i<sublist.size();i++){
					list2[i] = sublist.get(i);
				}
				try {
					Packet p = new Packet(senderId,destinationID,sequenceID,list2);
					p.contentType = fileName;
					packetList.add(p);
					sequenceID = sequenceID+1;
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				flag = false;
			}else{
				System.out.println("toIndex<list.size()"+toIndex);
				List<Byte> sublist = (List<Byte>) list.subList(fromIndex, toIndex);
				byte list2[] = new byte[sublist.size()];
				for(int i=0;i<sublist.size();i++){
					list2[i] = sublist.get(i);
				}
				try {
					Packet p = new Packet(senderId,destinationID,sequenceID,list2);
					p.contentType = fileName;
					packetList.add(p);
					sequenceID = sequenceID+1;
					
					fromIndex = toIndex;
					toIndex = toIndex+packetLength;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		for(Packet packet:packetList){
			packet.numberOfPackets = packetList.size();
		}
		return packetList;
	}
	static void connectToEdgeRouter(String ip,int port,Packet packet){
		System.out.println("connectToEdgeRouter ip:"+ip +"  port: "+port);
    	SocketBundle client = NetUtils.clientSocket(ip, port);
		int requestType = 1;
		
		try {
			
			client.out.writeInt(requestType);
			System.out.println("connectToEdgeRouter ip:"+ip +"  port: "+port );
			packet.forward(client.out);
			
			//read response type
			int responseType = client.in.readInt();
			
			client.socket.close();
			
			if(responseType == ACK_FLAG) {
				System.out.println("Successfully sent a packet" );
			} else {
				System.out.println("Failed to send a packet" );
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	public static void userAction(){
		Scanner reader = new Scanner(System.in);
		System.out.println("Please enter a directory path.");
		String text = reader.nextLine();
		System.out.println("Your input is: "+text);
		System.out.println("\n");
		File filePath = new File(text);
		if(filePath.exists() && filePath.isDirectory()){
			File[] childFiles = filePath.listFiles();
			for(int i = 0;i<childFiles.length;i++){
				System.out.println(childFiles[i].getName());
			}
			System.out.println("\n");
			System.out.println("Please enter a file name to be sent or enter 1 to send all the files.");
			String name = reader.nextLine();
			if("1".equals(name)){
				for(int i = 0;i<childFiles.length;i++){
					sendFile(childFiles[i].getAbsolutePath(),childFiles[i].getName());
				}
			}else{
				String fpath = filePath.getAbsolutePath()+File.separator+name;
				if(new File(fpath).exists()){
					sendFile(fpath,name);
				}else{
					System.out.println("The file does not exist.");
					userAction();
				}
			}
		}else{
			System.err.println("Please enter a valid directory path.");
			userAction();
		}
	}
    
	public static void main(String[] args) {
//		//First connect to its edge router
		if(args.length < 4) {
            System.out.println("Invalid number of arguments. Please provide an IP address and port number.");
            return;
        }
		ip = args[0];
		port = Integer.parseInt(args[1]);
		senderId = Integer.parseInt(args[2]);;
		destinationID = Integer.parseInt(args[3]);;
		userAction();
		
		
//		NetworkInfo info = NetworkInfo.getInstance();
//		Link link = new Link(0,1);
//		link.delay=85;
//		info.getLinks().add(link);
//		
//		link = new Link(0,2);
//		link.delay=217;
//		info.getLinks().add(link);
//		
//		link = new Link(0,4);
//		link.delay=173;
//		info.getLinks().add(link);
//		
//		link = new Link(2,6);
//		link.delay=186;
//		info.getLinks().add(link);
//		
//		link = new Link(2,7);
//		link.delay=103;
//		info.getLinks().add(link);
//		
//		link = new Link(3,7);
//		link.delay=183;
//		info.getLinks().add(link);
//		
//		link = new Link(5,8);
//		link.delay=250;
//		info.getLinks().add(link);
//		
//		link = new Link(8,9);
//		link.delay=84;
//		info.getLinks().add(link);
//		
//		link = new Link(7,9);
//		link.delay=167;
//		info.getLinks().add(link);
//		
//		link = new Link(4,9);
//		link.delay=502;
//		info.getLinks().add(link);
//		
//		link = new Link(9,10);
//		link.delay=40;
//		info.getLinks().add(link);
//		
//		link = new Link(1,10);
//		link.delay=600;
//		info.getLinks().add(link);
//	    
//		info.execute(0);
//		
//		LinkedList<Integer> path = info.getPath(10);
//	    
//	    for (int id : path) {
//	      System.out.println("id--->"+id);
//	    }
		
	}
}
