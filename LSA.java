package sLSRP;
import java.util.List;
import java.util.Date;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LSA {

	public int router;
	public Date age;
	public int sequenceNumber;
	public List<Link> links;
	public int checksum;

	public LSA(DataInputStream in) throws IOException {
		router = in.readInt();
		age = new Date(in.readLong());
		sequenceNumber = in.readInt();
		int numLinks = in.readInt();
		
		for(int i = 0; i < numLinks; i++) {
			int routerA = in.readInt();
			int routerB = in.readInt();
			double delay = in.readDouble();
			
			Link l = new Link(routerA, routerB);
			l.delay = delay;
			links.add(l);
		}
		
		checksum = in.readInt();
	}
	
	/*
	 *
	 TO DO: Write method to calculate checksum...
	 *
	 */
	
	public LSA(int router, int sequenceNumber, List<Link> links) {
		this.router = router;
		this.age = new Date();
		this.sequenceNumber = sequenceNumber;
		this.links = links;
	}

	public void forward(DataOutputStream out) throws IOException {
		out.writeInt(router);
		out.writeLong(age.getTime());
		out.writeInt(sequenceNumber);
		out.writeInt(links.size());
		for(int i = 0; i < links.size(); i++) {
			out.writeInt(links.get(i).A);
			out.writeInt(links.get(i).B);
			out.writeDouble(links.get(i).delay);
		}
		
		/*
		 *
		 TO DO: Call method to calculate checksum...
		 *
		 */
		int checksum = -1;
		out.writeInt(checksum);
	}
	
}