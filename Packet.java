package sLSRP;
import java.io.*;

public class Packet {
	int senderId;
	int destinationID;
	int sequenceID ;//Used to assemble packets together
	int numberOfPackets;
	String contentType;//file or something alse. <-- why do we need this? application should determine
	int dataLength;
    byte[] data;
	long checksum;
    
    public Packet(DataInputStream in) throws IOException {
        senderId = in.readInt();
        System.out.println("Receive a packet 1 : "+senderId);
        destinationID = in.readInt();
        System.out.println("Receive a packet 2 : "+destinationID);
        sequenceID = in.readInt();
        System.out.println("Receive a packet 3 : "+sequenceID);
        numberOfPackets = in.readInt();
        System.out.println("Receive a packet 4 : "+numberOfPackets);
        
        contentType= in.readUTF();
        System.out.println("Receive a packet contentType : "+contentType);
        dataLength = in.readInt();
        
        data = new byte[dataLength];
        for(int i = 0; i < dataLength; i++) {
            data[i] = in.readByte();
        }
        
        checksum = in.readLong();
    }
    
    public Packet(int senderId, int destinationID, int sequenceID, byte[] data) throws IOException {
        this.senderId = senderId;
        this.destinationID = destinationID; 
        this.sequenceID = sequenceID;
        this.dataLength = data.length;
        this.data = data;
        this.checksum = NetUtils.getChecksum(new BufferedReader(new StringReader(this.toChecksumString())));
    }
    
    void refreshChecksum() throws IOException {
        this.checksum = NetUtils.getChecksum(new BufferedReader(new StringReader(this.toChecksumString())));
    }
    
    void forward(DataOutputStream out) throws IOException {
		out.writeInt(senderId);
		out.writeInt(destinationID);
		out.writeInt(sequenceID);
        out.writeInt(numberOfPackets);
        out.writeUTF(contentType);
        out.writeInt(dataLength);
		for(int i = 0; i < dataLength; i++) {
			out.writeByte(data[i]);
		}
		out.writeLong(checksum);
	}
    
    public String toChecksumString() {
        String result = "";
        result += senderId;
        result += destinationID;
        result += sequenceID;
        result += dataLength;
        result += new String(data);
        
        return result;
    }
}