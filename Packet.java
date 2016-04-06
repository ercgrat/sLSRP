package sLSRP;
import java.io.*;

public class Packet {
	int senderId;
	int checkSum;
	int destinationID;
	byte[] data;
	int sequenceID ;//Used to assemble packets together
	
//	abstract void packAndSend(SocketBundle socketBundle);
//	abstract void receiveAndUnpack(SocketBundle socketBundle);
}