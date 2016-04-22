package sLSRP;
import java.util.*;
import java.io.*;

public class RouterFailureDaemon extends Thread {
	
	Configuration config;
	NetworkInfo netInfo;
	
	public RouterFailureDaemon(Configuration inConfig, NetworkInfo inNetInfo) {
		this.config = inConfig;
		this.netInfo = inNetInfo;
	}
    
	@Override
	public void run() {
		while(true){
            // Sleep for LSA forwarding interval
			try {
				Thread.sleep(config.failureInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            
            if(Router.failing) {
                Router.failing = false;
            } else {
                Random r = new Random();
                double val = r.nextDouble();
                if(val < config.routerFailureRate) {
                	System.out.println("\033[31;4m ****************************\n *****ROUTER FAILING NOW*****\n ****************************\033[0m");
                    Router.failing = true;
                }
            }
		}
	}
}
