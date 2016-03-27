package sLSRP;

import java.io.IOException;
import java.util.List;


public class LSATask extends Thread {
	//
	List<Link> links;
	public LSATask(List<Link> links){
		this.links = links;
	}
	@Override
	public void run() {
		//TODO Process the LSA and set up socket connection
		String address = "10.0.0.7=1999";
		
		String[] strs = address.split("=");
		String ip = strs[0].trim();
		int port = Integer.parseInt(strs[1]);
		System.out.println("Try to send an LSA to the next destinated router : "+ip+":"+port);
		SocketBundle client = NetUtils.clientSocket(ip, port);
		int connectionType = 0;
		try {
			//Send the connection type
			client.out.writeInt(connectionType);
			//read response type
			int responseType = client.in.readInt();
			System.out.println("Successfully sent an LSA out, the response type is: "+responseType);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
