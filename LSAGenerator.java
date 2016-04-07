package sLSRP;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
/**
 * This class is singleton in the program so that every class in the program can access to this same instance.
 * It it called to produce an LSA when some changes happen in the routing table
 * (for example, loss a neighbor or gain a neighbor)
 * It is also responsible for periodically sending LSAs.
 */
public class LSAGenerator {
	private static LSAGenerator generator;
	
	private LSAGenerator() {}
	
	public static LSAGenerator getInstance(Configuration inConfig, NetworkInfo inNetInfo){
		if(generator == null) {
			generator = new LSAGenerator(inConfig,inNetInfo);
		}
		return generator;
	}
	int sequenceNumber;
	Configuration config;
	NetworkInfo netInfo;
	Timer timer = new Timer();
	//Store all the LSAs received from neighbors, the first key(integer) is routerID, the second key is sequenceID.
	//When receive a LSA, first check if the table has the LSA records of this router, then check if this LSA has been received
	public static HashMap<Integer,HashMap<Integer,LSA>> recievedLSAHistoryTable = new HashMap<Integer,HashMap<Integer,LSA>>();
	
	private LSAGenerator(Configuration inConfig, NetworkInfo inNetInfo) {
		this.config = inConfig;
		this.netInfo = inNetInfo;
		sequenceNumber = 0;
		
		  
//		timer.schedule(new TimerTask(){
//			public void run() {
//				LSA lsa = generateLSA();
//				for(int i = 0; i < netInfo.getNeighbors().size(); i++) {
//					/*
//					 *
//					 TO DO: Get ip/port from netInfo.
//					 *
//					 */
//					SocketBundle client = NetUtils.clientSocket("IP ADDR", -1);
//					try {
//						lsa.forward(client.out);
//					} catch(IOException e) {
//						System.out.println(e);
//					}
//				}
//			}
//		}, config.forwardInterval);
	}
	public LSA generateLSA() throws IOException {
		sequenceNumber++;
		LSA lsa = new LSA(config.routerID, sequenceNumber, netInfo.getNeighborLinks(config.routerID));
		return lsa;
	}
	public void processLSA(LSA lsa) throws IOException {
        
        long checksum = NetUtils.getChecksum(new BufferedReader(new StringReader(lsa.toChecksumString())));
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
				}
			}else{
				//put it into the history table
				recievedLSAHistoryTable.get(lsa.router).put(lsa.sequenceNumber, lsa);
			}
		}else{
			//put it into the history table
			HashMap<Integer,LSA> map = new HashMap<Integer,LSA>();
			map.put(lsa.sequenceNumber, lsa);
			recievedLSAHistoryTable.put(lsa.router, map);
		}
		//send the LSA to all the neighbor except the neighbor who sent it to this router
		
		HashMap<Integer,RouterData> routers = NetworkInfo.getInstance().getRouters();
		Iterator iterator = routers.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry entry = (Map.Entry)iterator.next();
			int routerID = (Integer)entry.getKey();
			if(routerID!=lsa.router){
				LSATask task = new LSATask(routerID, LSAGenerator.getInstance(config, netInfo).generateLSA());
				task.start();
			}else{
				System.out.println("Try not to send LSA back to the original sender.");
			}
			
		}
	}
	
}
