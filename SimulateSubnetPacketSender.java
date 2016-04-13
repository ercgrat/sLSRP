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
    
	int packetSize = 0;
    
	public SimulateSubnetPacketSender(int packetSize){
		this.packetSize = packetSize;
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
    static void sendFile(String filePath){
    	ArrayList<Byte> list = readFile(filePath);
    	generatePackets(list);
    }
	static ArrayList<Packet> generatePackets(ArrayList<Byte> list){
		ArrayList<Packet> packetList = new ArrayList<Packet>();
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
				
			}else{
				String fpath = filePath.getAbsolutePath()+File.separator+name;
				if(new File(fpath).exists()){
					sendFile(fpath);
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
