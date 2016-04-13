package sLSRP;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * This class is used for testing packet or file transfer between routers;
 * This is a separate program that simulating injecting packets into the network, 
 * then the network sends these packets to the subnet receiver.
 */
public class SimulateSubnetPacketSender {
    
	static int packetLength = 0;
	static int senderId;
	static int destinationID; 
	
    
	public SimulateSubnetPacketSender(int packetLength){
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
    	generatePackets(list,fileName);
    }
	static ArrayList<Packet> generatePackets(ArrayList<Byte> list,String fileName){
		ArrayList<Packet> packetList = new ArrayList<Packet>();
		int sequenceID = 1;
		int fromIndex = 0;
		int toIndex = fromIndex+packetLength;
		boolean flag = true;
		while(flag){
			if(toIndex>=list.size()){
				ArrayList<Byte> sublist = (ArrayList<Byte>) list.subList(fromIndex, list.size());
				byte list2[] = new byte[sublist.size()];
				for(int i=0;i<sublist.size();i++){
					list2[i] = sublist.get(i);
				}
				try {
					Packet p = new Packet(senderId,destinationID,sequenceID, true,list2);
					p.contentType = fileName;
					packetList.add(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				flag = false;
			}else{
				ArrayList<Byte> sublist = (ArrayList<Byte>) list.subList(fromIndex, toIndex);
				byte list2[] = new byte[sublist.size()];
				for(int i=0;i<sublist.size();i++){
					list2[i] = sublist.get(i);
				}
				try {
					Packet p = new Packet(senderId,destinationID,sequenceID, false,list2);
					p.contentType = fileName;
					packetList.add(p);
					toIndex = fromIndex+packetLength;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return packetList;
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
			System.out.println("Please enter a valid directory path.");
			userAction();
		}
	}
    
	public static void main(String[] args) {
		//TODO
		userAction();
		
	}
}
