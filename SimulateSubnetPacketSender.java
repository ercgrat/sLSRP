package sLSRP;

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
//	public static void main(String[] args) {
//		//TODO
//		Scanner reader = new Scanner(System.in);
//		System.out.println("Enter a file path: ");
//		String text = reader.nextLine();
//		
//		System.out.println("--->"+text);
//	}
}
