package sLSRP;

import java.io.IOException;
import java.util.*;

public class NewNeighborTask extends Thread {
    
    Timer timer;
    Configuration config;
    int neighborID;
    private final int GIVE_UP_NEIGHBOR_REQ_TIME = 1000;
    
	public NewNeighborTask(int neighborID, Configuration config){
		this.config = config;
        this.neighborID = neighborID;
        timer = new Timer();
		timer.schedule(new TimerTask(){
            @Override
            public void run() {
                if(NewNeighborTask.this.isAlive()){
                    NewNeighborTask.this.interrupt();
                    System.err.println("Time to build a connection with neighbor has reached a limit, so the thread is interrupted.");
                }
            }
        }, GIVE_UP_NEIGHBOR_REQ_TIME);
	}
	@Override
	public void run() {
        NeighborConnector.addNeighborViaNameServer(neighborID, config);
        timer.cancel();
        timer.purge();
        this.interrupt();
	}
}
