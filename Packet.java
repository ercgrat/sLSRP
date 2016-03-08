package sLSRP;
import java.io.*;

public abstract class Packet {
	public enum Type { Alive, Data, LSA, Neighbor };
	Type type;
	int senderId;
	int checkSum;
	
	void readHeader(SocketBundle socketBundle) {
		BufferedReader in = socketBundle.in;
		try {
			senderId = in.read();
			int typeId = in.read();
			switch(typeId) {
				case 0:
					type = Type.Alive; break;
				case 1:
					type = Type.Data; break;
				case 2:
					type = Type.LSA; break;
				case 3:
					type = Type.Neighbor; break;
				default:
					break;
			}
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	abstract void packAndSend(SocketBundle socketBundle);
	abstract void receiveAndUnpack(SocketBundle socketBundle);
}