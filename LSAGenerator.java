package sLSRP;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

public class LSAGenerator extends Thread {
	
	int sequenceNumber;
	
	public LSAGenerator(Configuration inConfig, NetworkInfo inNetInfo) {
		final Configuration config = inConfig;
		final NetworkInfo netInfo = inNetInfo;
		sequenceNumber = 0;
		
		Timer timer = new Timer();  
		timer.schedule(new TimerTask(){
			public void run() {
				sequenceNumber++;
				LSA lsa = new LSA(config.routerID, sequenceNumber, netInfo.getNeighborLinks(config.routerID));
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
	
}
