package sLSRP;

import java.io.File;
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
    
	int packetSize = 0;
    
	public SimulateSubnetPacketSender(int packetSize){
		this.packetSize = packetSize;
	}
    
	ArrayList<Byte> readFile(String filePath){
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
    
	ArrayList<Packet> generatePackets(ArrayList<Byte> list){
		ArrayList<Packet> packetList = new ArrayList<Packet>();
		return packetList;
	}
    
	public static void userAction(){
		Scanner reader = new Scanner(System.in);
		System.out.println("Please enter a directory path.");
		String text = reader.nextLine();
		System.out.println("Your input is: "+text);
		
		File filePath = new File(text);
		if(filePath.exists() && filePath.isDirectory()){
			System.out.println("Please enter a file name to be sent or enter 1 to send all the files.");
			String name = reader.nextLine();
			if("1".equals(name)){
				
			}else{
				
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
