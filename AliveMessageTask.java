package sLSRP;

import java.io.IOException;


public class AliveMessageTask extends Thread {
	int interval;
	boolean isMessageDeamon = false;
	public AliveMessageTask(int interval, boolean isMessageDeamon){
		this.interval = interval;
		this.isMessageDeamon = isMessageDeamon;
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
			}
		}
	}
}
