package sLSRP;
import java.io.*;
import java.util.List;

public class UserInterface extends Thread {

	Configuration config;
	NetworkInfo netInfo;
	
	public UserInterface(Configuration config, NetworkInfo netInfo) {
		this.config = config;
		this.netInfo = netInfo;
	}
	
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			System.out.println("\n--------------Router Interface--------------\n" +
			"1. Print Link State Database\n" +
			"2. Print LSA History\n" +
			"3. Print Router Info (id, ip address, port)\n" +
			"4. Add Router Info to Neighbor List (id, ip address, port)\n" +
			"5. Remove Router from Neighbor List\n" +
			"6. Shut down router");
			
			try {
				String input = br.readLine();
				if(!input.matches("^\\d+$")) {
					continue;
				}
				int option = Integer.parseInt(input);
				switch(option) {
					case 1:
						break;
					case 2:
						List<RouterData> routers = netInfo.getRouters();
						LSAHistory lsaHistory = netInfo.getLSAHistory();
						
						String output = "\n~~~UI Response to 2.~~~\n";
						for(int i = 0; i < routers.size(); i++) {
							int routerID = routers.get(i).routerID;
							int sequenceNo = lsaHistory.getLastSequenceNumber(routerID);
							output += "Router ID " + routerID + " - Sequence No. " + sequenceNo + "\n";
						}
						System.out.println(output);
						break;
					case 3:
						System.out.println("\n~~~UI Response to 3.~~~\n" +
						"Router Id: " + config.routerID + "\n" +
						"IP Address: " + config.routerIpAddress + "\n" +
						"Port: " + config.routerPort);
						break;
					case 4:
					case 5:
						break;
					case 6:
						System.exit(0);
						break;
					default:
						break;
				}
			} catch(IOException e) {
				System.out.println(e);
			}
		}
	}
}