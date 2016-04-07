package sLSRP;
import java.io.*;

public class Packet {
	int senderId;
	int destinationID;
	int sequenceID ;//Used to assemble packets together
	boolean isLastPacket;
	int contentType;//file or something alse. <-- why do we need this? application should determine
	int dataLength;
    byte[] data;
	long checksum;
    
    public Packet(DataInputStream in) throws IOException {
        senderId = in.readInt();
        destinationID = in.readInt();
        sequenceID = in.readInt();
        isLastPacket = in.readBoolean();
        dataLength = in.readInt();
        
        data = new byte[dataLength];
        for(int i = 0; i < dataLength; i++) {
            data[i] = in.readByte();
        }
        
        checksum = in.readLong();
    }
    
    public Packet(int senderId, int destinationID, int sequenceID, boolean isLastPacket, byte[] data) throws IOException {
        this.senderId = senderId;
        this.destinationID = destinationID; 
        this.sequenceID = sequenceID;
        this.isLastPacket = isLastPacket;
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
        out.writeBoolean(isLastPacket);
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
        result += isLastPacket;
        result += dataLength;
        result += new String(data);
        
        return result;
    }
}