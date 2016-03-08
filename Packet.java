package sLSRP;
import java.io.*;

public abstract class Packet {
	int senderId;
	int checkSum;
	
	abstract void packAndSend(SocketBundle socketBundle);
	abstract void receiveAndUnpack(SocketBundle socketBundle);
}