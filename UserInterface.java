package sLSRP;
import java.io.*;
import java.util.*;

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
			"4. Add Router to Neighbor List\n" +
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
                        synchronized(netInfo) {
                            List<Link> links = netInfo.getLinks();
                            
                            output = "\n~~~UI Response to 1.~~~\n";
                            for(int i = 0; i < links.size(); i++) {
                                Link l = links.get(i);
                                
                                output += "Link 1:\n\tRouter A: " + l.A + "\tRouter B: " + l.B + "\t delay:" + l.delay + "\tloads class: " + l.load + "\n";
                            }
                            System.out.println(output);
                        }
						break;
					case 2:
                        synchronized(netInfo) {
                            HashMap<Integer,HashMap<Integer,LSA>> receivedLSAHistoryTable = LSAProcessor.getInstance(config, netInfo).receivedLSAHistoryTable;
                            
                            output = "\n~~~UI Response to 2.~~~\n";
                            Iterator iterator = receivedLSAHistoryTable.keySet().iterator();
                            while(iterator.hasNext()) {
                                Integer routerID = (Integer)iterator.next();
                                HashMap<Integer, LSA> lsaHistory = receivedLSAHistoryTable.get(routerID);
                                ArrayList<Integer> sequenceNumbers = new ArrayList<Integer>();
                                sequenceNumbers.addAll(lsaHistory.keySet());
                                Collections.sort(sequenceNumbers);
                                int sequenceNo = sequenceNumbers.get(sequenceNumbers.size() - 1);
                                output += "Router ID " + routerID + " - Sequence No. " + sequenceNo + "\n";
                            }
                            System.out.println(output);
                        }
						break;
					case 3:
						System.out.println("\n~~~UI Response to 3.~~~\n" +
						"Router Id: " + config.routerID + "\n" +
						"IP Address: " + config.routerIpAddress + "\n" +
						"Port: " + config.routerPort);
						break;
					case 4:
						System.out.println("\n~~~UI Response to 4.~~~\nPlease enter the router id you wish to connect to:");
						String routerArg = br.readLine();
                        
                        if(!routerArg.matches("^\\d+$")) {
							System.out.println("\n~~~UI Feedback~~~\nThe router ID must be an integer.");
						} else {
                            int routerId = Integer.parseInt(routerArg);
                            NewNeighborTask neighborTask = new NewNeighborTask(routerId, config);
                            neighborTask.start();
						}
						break;
					case 5:
                        synchronized(netInfo) {
                            System.out.println("\n~~~UI Response to 5.~~~\nPlease enter the router id:");
                            String routerInput = br.readLine();
                            
                            if(!routerInput.matches("^\\d+$")) {
                                System.out.println("\n~~~UI Feedback~~~\nThe router id must be an integer.");
                            } else {
                                int routerID = Integer.parseInt(routerInput);
                                
                                HashMap<Integer,RouterData> neighbors = netInfo.getNeighbors();
                                RouterData data = neighbors.get(routerID);                                
                                if(data != null) {
                                    NeighborConnector.sendCeaseNeighborRequest(config.routerID, routerID, data.ipAddress, data.port);						
                                } else {
                                    System.out.println("\n~~~UI Feedback~~~\nThere is no neighbor with that id.");
                                }
                            }
                        }
						break;
                    case 6:
						System.exit(0);
						break;
                    case 7:
                        Router.failing = !Router.failing;
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