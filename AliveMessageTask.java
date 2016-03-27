package sLSRP;

import java.io.IOException;
import java.util.Queue;


public class AliveMessageTask extends Thread {
	int interval;
	boolean isMessageDeamon = false;
	Queue LSAQueue;
	public AliveMessageTask(int interval, boolean isMessageDeamon, Queue LSAQueue){
		this.interval = interval;
		this.isMessageDeamon = isMessageDeamon;
		this.LSAQueue = LSAQueue;
	}
	@Override
	public void run() {
		if(this.isMessageDeamon){
			while(true){
				
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//TODO Periodically set up socket connection to send alive messages
				//Number of current neighbors
				int numberOfNeighbors = 1;
				
				for(int i =0;i<numberOfNeighbors;i++){
					String address = "10.0.0.7=1999";
					System.out.println("Try to send an alive message to a neighbor : "+address);
					String[] strs = address.split("=");
					String ip = strs[0].trim();
					int port = Integer.parseInt(strs[1]);
					SocketBundle client = NetUtils.clientSocket(ip, port);
					int connectionType = 2;
					try {
						//Send the connection type
						client.out.writeInt(connectionType);
						//read response type
						int responseType = client.in.readInt();
						System.out.println("Successufully sent an alive message, the response type is: "+responseType);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//TODO if alive messages fail to reach destination, then the destination may go down, so send LSA
//				Runnable task = new LSATask(NetworkInfo.getInstance().getLinks());
//			    LSAQueue.add(task);
//			    //Call the queue to process the task
//			    synchronized (LSAQueue) {
//			    	LSAQueue.notify();
//		    	}
			}
		}
	}
}
