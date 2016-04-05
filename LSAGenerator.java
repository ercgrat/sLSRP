package sLSRP;
import java.io.IOException;
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
	
	private LSAGenerator(Configuration inConfig, NetworkInfo inNetInfo) {
		this.config = inConfig;
		this.netInfo = inNetInfo;
		sequenceNumber = 0;
		
		  
		timer.schedule(new TimerTask(){
			public void run() {
				LSA lsa = generateLSA();
				for(int i = 0; i < netInfo.getNeighbors().size(); i++) {
					/*
					 *
					 TO DO: Get ip/port from netInfo.
					 *
					 */
					SocketBundle client = NetUtils.clientSocket("IP ADDR", -1);
					try {
						lsa.forward(client.out);
					} catch(IOException e) {
						System.out.println(e);
					}
				}
			}
		}, config.forwardInterval);
	}
	public LSA generateLSA(){
		sequenceNumber++;
		LSA lsa = new LSA(config.routerID, sequenceNumber, netInfo.getNeighborLinks(config.routerID));
		return lsa;
	}
}
