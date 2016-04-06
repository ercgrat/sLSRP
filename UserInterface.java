package sLSRP;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
			"4. Add Router Info to Neighbor List\n" +
			"5. Remove Router from Neighbor List\n" +
			"6. Shut down router");
			
			try {
				String input = br.readLine();
				if(!input.matches("^\\d+$")) {
					continue;
				}
				int option = Integer.parseInt(input);
				String output = "";
				switch(option) {
					case 1:
						List<Link> links = netInfo.getLinks();
						
						output = "\n~~~UI Response to 1.~~~\n";
						for(int i = 0; i < links.size(); i++) {
							Link l = links.get(i);
							
							output += "Link 1:\n\tRouter A: " + l.A + "\tRouter B: " + l.B + "\t delay:" + l.delay + "\tloads class: " + l.load + "\n";
						}
						System.out.println(output);
						break;
					case 2:
						HashMap<Integer,RouterData> routers = netInfo.getRouters();
						LSAHistory lsaHistory = netInfo.getLSAHistory();
						
						
						output = "\n~~~UI Response to 2.~~~\n";
						Iterator iterator = routers.entrySet().iterator();
						while(iterator.hasNext()){
							Map.Entry entry = (Map.Entry)iterator.next();
							int routerID = (Integer)entry.getKey();
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
						System.out.println("\n~~~UI Response to 4.~~~\nPlease enter the router id, ip address, and port separated by commas:");
						String[] routerArgs = br.readLine().split(", |,");
						if(routerArgs.length != 3) {
							System.out.println("\n~~~UI Feedback~~~\nAn incorrect number of arguments was specified.");
						} else if(!routerArgs[0].matches("^\\d+$") || !routerArgs[1].matches("^(\\d{1,3}\\.){3}\\d{1,3}$") || !routerArgs[2].matches("^\\d+$")) {
							System.out.println("\n~~~UI Feedback~~~\nOne or more of the arguments was invalid.");
						} else {
							NeighborConnector.sendNeighborRequest(config.routerID, Integer.parseInt(routerArgs[0]), routerArgs[1], Integer.parseInt(routerArgs[2]));
						}
						break;
					case 5:
						System.out.println("\n~~~UI Response to 5.~~~\nPlease enter the router id:");
						String routerInput = br.readLine();
						
						if(!routerInput.matches("^\\d+$")) {
							System.out.println("\n~~~UI Feedback~~~\nAn incorrect number of arguments was specified.");
						} else {
							int routerID = Integer.parseInt(routerInput);
							
							//List<RouterData> rData = netInfo.getRouters();
							HashMap<Integer,RouterData> rData = netInfo.getRouters();
							
							RouterData data = null;
							if(rData.containsKey(routerID)){
								data = rData.get(routerID);
								break;
							} else {
								System.out.println("\n~~~UI Feedback~~~\nThere is no neighboring router with that id.");
							}
//							for(int i = 0; i < rData.size(); i++) {
//								if(rData.get(i).routerID == routerID) {
//									if(netInfo.getNeighbors().contains(routerID)) {
//										data = rData.get(i);
//										break;
//									} else {
//										System.out.println("\n~~~UI Feedback~~~\nThere is no neighboring router with that id.");
//									}
//								}
//							}
							
							if(data != null) {
								NeighborConnector.sendCeaseNeighborRequest(config.routerID, routerID, data.ipAddress, data.port);						
							} else {
								System.out.println("\n~~~UI Feedback~~~\nThere is no router in the database with that id.");
							}
						}
						break;
					case 6:
						System.exit(0);
						break;
					default:
						break;
				}
			} catch(IOException e) {
				System.out.println(e);
			} catch(NumberFormatException e) {
				System.out.println(e);
			} catch(Exception e) {
				System.out.println(e);
			}
		}
	}
}