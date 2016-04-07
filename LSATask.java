package sLSRP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LSATask extends Thread {
	//
	int routerID;
	LSA lsa;
	RouterData routerdata;
	List<Link> links;
	public LSATask(int routerID,LSA lsa){
		this.routerID = routerID;
		this.lsa = lsa;
		this.links = NetworkInfo.getInstance().getNeighborLinks(routerID);
		routerdata = NetworkInfo.getInstance().getRouters().get(routerID);
	}
	@Override
	public void run() {
		String ip = routerdata.ipAddress;
		int port = routerdata.port;
		ip = ip.replaceFirst("/", "");
		System.out.println("Try to send an LSA to the next destinated router : "+routerID+"   IP: "+ip+":"+port);
		SocketBundle client = NetUtils.clientSocket(ip, port);
		int connectionType = 0;
		try {
			//Send the connection type
			client.out.writeInt(connectionType);
            lsa.forward(client.out);
			
			//read response type
			int responseType = client.in.readInt();
			System.out.println("Successfully sent an LSA out, the response type is: "+responseType);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
