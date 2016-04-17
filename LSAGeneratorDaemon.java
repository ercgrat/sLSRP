package sLSRP;
import java.util.*;
import java.io.*;

public class LSAGeneratorDaemon extends Thread {
	
	Configuration config;
	NetworkInfo netInfo;
    LSAProcessor processor;
	
	public LSAGeneratorDaemon(Configuration inConfig, NetworkInfo inNetInfo, LSAProcessor processor) {
		this.config = inConfig;
		this.netInfo = inNetInfo;
        this.processor = processor;
	}
    
	@Override
	public void run() {
		while(true){
            // Sleep for LSA forwarding interval
			try {
				Thread.sleep(config.updateInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            
            // Broadcast a new LSA
            LSA lsa = processor.createLSA();
            processor.broadcastLSA(lsa);
		}
	}
}
