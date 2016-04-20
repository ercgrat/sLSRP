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
                        synchronized(netInfo) {
                            List<Link> links = netInfo.getLinks();
                            System.out.println(links);
                            
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
                            HashMap<Integer,RouterData> routers = netInfo.getNeighbors();
                            HashMap<Integer,HashMap<Integer,LSA>> receivedLSAHistoryTable = LSAProcessor.getInstance(config, netInfo).receivedLSAHistoryTable;
                            
                            output = "\n~~~UI Response to 2.~~~\n";
                            Iterator iterator = routers.entrySet().iterator();
                            while(iterator.hasNext()){
                                Map.Entry entry = (Map.Entry)iterator.next();
                                int routerID = (Integer)entry.getKey();
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
						System.out.println("\n~~~UI Response to 4.~~~\nPlease enter the router id, ip address, and port separated by commas:");
						String[] routerArgs = br.readLine().split(", |,");
						if(routerArgs.length != 3) {
							System.out.println("\n~~~UI Feedback~~~\nAn incorrect number of arguments was specified.");
						} else if(!routerArgs[0].matches("^\\d+$") || !routerArgs[1].matches("^(\\d{1,3}\\.){3}\\d{1,3}$") || !routerArgs[2].matches("^\\d+$")) {
							System.out.println("\n~~~UI Feedback~~~\nOne or more of the arguments was invalid.");
						} else {
							NeighborConnector.sendNeighborRequest(config.routerID, config.routerPort, Integer.parseInt(routerArgs[0]), routerArgs[1], Integer.parseInt(routerArgs[2]));
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
                                
                                List<Integer> neighbors = netInfo.getNeighbors(config.routerID);
                                RouterData data = null;
                                if(neighbors.contains(routerID)) { // Router specified is a neighbor
                                    data = netInfo.getNeighbors().get(routerID);
                                } else {
                                    System.out.println("\n~~~UI Feedback~~~\nThere is no neighboring router with that id.");
                                    break;
                                }
                                
                                if(data != null) {
                                    NeighborConnector.sendCeaseNeighborRequest(config.routerID, routerID, data.ipAddress, data.port);						
                                } else {
                                    System.out.println("\n~~~UI Feedback~~~\nThere is no router in the database with that id.");
                                }
                            }
                        }
						break;
                    case 6:
						System.exit(0);
						break;
                        /*System.out.println("\n~~~UI Response to 6.~~~\nPlease enter the destination router id and a filename separated by a space:");
						String[] fileInput = br.readLine().split(" ");
                        
                        if(fileInput.length != 2) {
                            System.out.println("\n~~~UI Feedback~~~\nAn incorrect number of arguments was specified.");
                            break;
                        }
                        if(!fileInput[0].matches("^\\d+$")) {
							System.out.println("\n~~~UI Feedback~~~\nThe router id must be an integer.");
                            break;
						}
						int routerID = Integer.parseInt(fileInput[0]);
                        
                        List<Integer> path = netInfo.getPath(routerID);
                        if(path == null){
                            System.out.println("\n~~~UI Feedback~~~\nThere is no valid path to the router with that id.");
                            break;
                        }
                        
                        RouterData rData = netInfo.getRouters().get(path.get(0)); // Router data for next hop
                        Packet packet = new Packet(config.routerID, routerID, 0, false, null);
                        int connectionType = 1;
                        
                        File file = new File(fileInput[1]);
                        byte[] fileData = new byte[(int)file.length()];
                        FileInputStream fis = new FileInputStream(file);
                        fis.read(fileData);
                        fis.close();
                        
                        List<Byte> buffer = new ArrayList<Byte>();
                        for(int i = 0; i < fileData.length; i++) {
                            buffer.add(fileData[i]);
                            if(buffer.size() == config.maxPacketLength || i == fileData.length - 1) {
                                Byte[] data = (Byte [])(buffer.toArray());
                                packet.data = new byte[data.length];
                                for(int j = 0; j < data.length; j++) {
                                    packet.data[j] = data[j];
                                }
                                
                                packet.dataLength = config.maxPacketLength;
                                if(i == fileData.length - 1) {
                                    packet.isLastPacket = true;
                                }
                                packet.refreshChecksum();
                                
                                SocketBundle client = NetUtils.clientSocket(rData.ipAddress, rData.port);
                                client.out.writeInt(connectionType);
                                packet.forward(client.out);
                                client.socket.close();
                                
                                buffer.clear();
                            }
                        }
                        
                        
                        System.out.println("\n~~~UI Response to 6.~~~\nFile successfully transmitted.");
                        
                        break;
					*/
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