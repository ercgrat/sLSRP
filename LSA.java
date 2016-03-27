package sLSRP;
import java.util.ArrayList;
import java.util.Date;
import java.io.DataInputStream;
import java.io.IOException;

public class LSA {

	public int router;
	public Date age;
	public int sequenceNumber;
	public ArrayList<Link> links;
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

}