package sLSRP;
import java.util.*;
import java.io.*;

public class LSA {

//	Router ID: Identifies the router sending the LSA
//	LSA Type: Classifies the packet as of type LSA Advertising Router. It identifies the
//	          originating router of the LSA. Knowing which router has originated a particular LSA
//	          tells the calculating router whether the LSA should be used in the routing
//	          calculation and, if so, how it should be used.
//	LS Age:   Indicates the number of seconds since the LSA was originated. This field is
//	          reset every time a new instance of the same LSA is received. If the age of the LSA
//	          reaches a predefined threshold, Age Limit, the LSA is deleted from the database.
//	LS Sequence Number: This field is used by the routers to distinguish between
//	          instances of the same LSA. The LSA instance having the larger LS Sequence Number
//	          is considered to be more recent.
//	Length:   This field contains the length, in bytes, of the LSA counting both LSA header and contents.
//	Number of Links: Identifies the number of links reported in the LSA.
//	Link ID:    Identifies the ID of the Link.
//	Link Data: Contains all data describing the status of the link based on the routing
//	          metrics in terms of availability and cost. The cost metric, ranging from 1 to
//	          Max_Cost, indicates the relative cost of sending data packets over the link, namely
//	          the larger the cost, the less likely the data will be routed over the link.
	public int router;
	public Date age;
	public int sequenceNumber;
	public List<Link> links;
	public long checksum;

	public LSA(DataInputStream in) throws IOException {
		router = in.readInt();
		age = new Date(in.readLong());
		sequenceNumber = in.readInt();
		int numLinks = in.readInt();
		
        links = new ArrayList<Link>();
		for(int i = 0; i < numLinks; i++) {
			int routerA = in.readInt();
			int routerB = in.readInt();
			int delay = in.readInt();
			
			Link l = new Link(routerA, routerB);
			l.delay = delay;
			links.add(l);
		}
		
		checksum = in.readLong();
	}
	
	public LSA(int router, int sequenceNumber, List<Link> links) throws IOException {
		this.router = router;
		this.age = new Date();
		this.sequenceNumber = sequenceNumber;
		this.links = links;
        this.checksum = NetUtils.getChecksum(new BufferedReader(new StringReader(this.toChecksumString())));
	}

	public void forward(DataOutputStream out) throws IOException {
        out.writeInt(0);
		out.writeInt(router);
		out.writeLong(age.getTime());
		out.writeInt(sequenceNumber);
		out.writeInt(links.size());
		for(int i = 0; i < links.size(); i++) {
			out.writeInt(links.get(i).A);
			out.writeInt(links.get(i).B);
			out.writeInt(links.get(i).delay);
		}
		out.writeLong(checksum);
	}
    
    public String toChecksumString() {
        String result = "";
        result += router;
        result += age.getTime();
        result += sequenceNumber;
        result += links.size();
        for(int i = 0; i < links.size(); i++) {
			result += links.get(i).A;
			result += links.get(i).B;
			result += links.get(i).delay;
		}
        
        return result;
    }
	
}