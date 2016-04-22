package sLSRP;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Configuration {
	
    int routerID;
    String nameServerIp;
    int nameServerPort;
	int maxNetworkSize;
	int helloInterval;
	int updateInterval;
	String routerIpAddress;
	int routerPort;
	String version;
	int maxPacketLength;
	int ageLimit;
	double packetErrorRate;
	double networkCongestionRate;
	double routerFailureRate;
	int failureInterval;
	
	HashMap configNeighbors = new HashMap();
	List<String> neighborBlacklist = new ArrayList<String>();
	
	
	public Configuration(String routerIdString, String filepath) {
		try {
            this.routerID = Integer.parseInt(routerIdString);
            
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			
			String[] tokens = readAndParse(br);
            nameServerIp = tokens[1];
            tokens = readAndParse(br);
            nameServerPort = Integer.parseInt(tokens[1]);
            tokens = readAndParse(br);
			maxNetworkSize = Integer.parseInt(tokens[1]);
			tokens = readAndParse(br);
			helloInterval = Integer.parseInt(tokens[1]);
			tokens = readAndParse(br);
			updateInterval = Integer.parseInt(tokens[1]);
			tokens = readAndParse(br);
			version = tokens[1];
			tokens = readAndParse(br);
			maxPacketLength = Integer.parseInt(tokens[1]);
			tokens = readAndParse(br);
			ageLimit = Integer.parseInt(tokens[1]);
			
			tokens = readAndParse(br);
			packetErrorRate = Double.parseDouble(tokens[1]);
			tokens = readAndParse(br);
			networkCongestionRate = Double.parseDouble(tokens[1]);
			tokens = readAndParse(br);
			routerFailureRate = Double.parseDouble(tokens[1]);
			
			tokens = readAndParse(br);
			failureInterval = Integer.parseInt(tokens[1]);
			
			tokens = readAndParse(br);
			String nonNeighborStr = tokens[1];
			String[] configNonNeighbors =  nonNeighborStr.split(",");
			neighborBlacklist = (List) Arrays.asList(configNonNeighbors);
			
			try {
				tokens = readAndParse(br);
				if(tokens!=null){
					String neighborStr = tokens[1];
					String[] neigstrs = neighborStr.split(",");
					for(int i=0; i < neigstrs.length; i++) {
						String[] ns = neigstrs[i].split("=");
                        if(ns[0].equals("")) {
                            continue;
                        }
						configNeighbors.put(Integer.parseInt(ns[0]), neigstrs[i]);
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	String[] readAndParse(BufferedReader br) throws IOException {
		String line = br.readLine();
		if(line==null){return null;}
		String[] tokens = line.split(":");
		tokens[0] = tokens[0].trim();
		tokens[1] = tokens[1].trim();
		
		return tokens;
	}
	
}
