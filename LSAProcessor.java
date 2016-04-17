package sLSRP;
import java.io.*;
import java.util.*;
/**
 * This class is singleton in the program so that every class in the program can access to this same instance.
 * It it called to produce an LSA when some changes happen in the routing table
 * (for example, loss a neighbor or gain a neighbor)
 * It is also responsible for periodically sending LSAs.
 */
public class LSAProcessor {
    
	private static LSAProcessor processor;
    int sequenceNumber;
    Configuration config;
    NetworkInfo netInfo;
    //Store all the LSAs received from neighbors, the first key(integer) is routerID, the second key is sequenceID.
	//When receive a LSA, first check if the table has the LSA records of this router, then check if this LSA has been received
	public static HashMap<Integer,HashMap<Integer,LSA>> recievedLSAHistoryTable = new HashMap<Integer,HashMap<Integer,LSA>>();
	
	private LSAProcessor(Configuration config, NetworkInfo netInfo) {
        this.config = config;
        this.netInfo = netInfo;
        this.sequenceNumber = 0;
    }
	
	public static LSAProcessor getInstance(Configuration inConfig, NetworkInfo inNetInfo){
		if(processor == null) {
			processor = new LSAProcessor(inConfig,inNetInfo);
		}
		return processor;
	}
    
    public LSA createLSA() {
        sequenceNumber++;
        LSA lsa = null;
        try {
            lsa = new LSA(config.routerID, sequenceNumber, netInfo.getNeighborLinks(config.routerID));
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        return lsa;
    }
    
    public void broadcastLSA(LSA lsa) {
        try {
            HashMap<Integer,RouterData> neighbors = netInfo.getNeighbors();
            System.out.println("neighbors:\n" + neighbors);
            for(Integer key : neighbors.keySet()) {
                RouterData rData = neighbors.get(key);
                if(rData.routerID != lsa.router) {
                    System.out.println("router data id: " + rData.routerID);
                    System.out.println("lsa router id: " + lsa.router);
                    rData.ipAddress = rData.ipAddress.replaceFirst("/", "");
                    System.out.println("About to send LSA to router id " + rData.routerID + ", " + rData.ipAddress + ", " + rData.port);
                    SocketBundle client = NetUtils.clientSocket(rData.ipAddress, rData.port);
                    lsa.forward(client.out);
                }
            }
            System.out.println("done forwarding generated LSA to neighbors.");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
	public void processLSA(LSA lsa) throws IOException {
        
        long checksum = NetUtils.getChecksum(new BufferedReader(new StringReader(lsa.toChecksumString())));
        System.out.println("Received checksum on incoming packet: " + lsa.checksum);
        System.out.println("Calculated checksum on incoming packet: " + checksum);
        if(checksum != lsa.checksum) {
            System.out.println("LSA is corruped; received checksum does not match calculated checksum. Dropping this LSA update.");
            return;
        }
		
		//Check if LSA history table has records for this router
		if(recievedLSAHistoryTable.containsKey(lsa.router)){
			//if there is a record exists in the table
			//then check if this LSA has been in the table by using sequenceID
			HashMap<Integer,LSA> map = recievedLSAHistoryTable.get(lsa.router);
			if(map.containsKey(lsa.sequenceNumber)){
				//if table has a record of this LSA, then check the age attribute to see if this LSA should be replaced
				if(map.get(lsa.sequenceNumber).age.before(lsa.age)){
					//replace the old record
					recievedLSAHistoryTable.get(lsa.router).put(lsa.sequenceNumber, lsa);
					NetworkInfo.getInstance().setLinks(lsa.links);
				}
			}else{
				//put it into the history table
				recievedLSAHistoryTable.get(lsa.router).put(lsa.sequenceNumber, lsa);
				NetworkInfo.getInstance().setLinks(lsa.links);
			}
		}else{
			//put it into the history table
			HashMap<Integer,LSA> map = new HashMap<Integer,LSA>();
			map.put(lsa.sequenceNumber, lsa);
			recievedLSAHistoryTable.put(lsa.router, map);
			NetworkInfo.getInstance().setLinks(lsa.links);
		}
		
        //send the LSA to all the neighbor except the neighbor who sent it to this router
		this.broadcastLSA(lsa);
	}
	
}
