package sLSRP;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Configuration {
	
	int maxNetworkSize;
	int helloInterval;
	int updateInterval;
	int forwardInterval;
	int routerID;
	String version;
	int maxPacketLength;
	int ageLimit;
	double packetErrorRate;
	double networkCongestionRate;
	double routerFailureRate;
	int failureInterval;
	
	int routerPort;
	String[] configNeighbors;
	List blacklist = new ArrayList();
	
	public Configuration() {
		try {
//			BufferedReader br = new BufferedReader(new FileReader("sLSRP/config.txt"));
			BufferedReader br = new BufferedReader(new FileReader("/Users/fanlingling/Documents/javaworkplace/Sample/src/sLSRP/config.txt"));
			
			String[] tokens = readAndParse(br);
			maxNetworkSize = Integer.parseInt(tokens[1]);
			tokens = readAndParse(br);
			helloInterval = Integer.parseInt(tokens[1]);
			tokens = readAndParse(br);
			updateInterval = Integer.parseInt(tokens[1]);
			tokens = readAndParse(br);
			forwardInterval = Integer.parseInt(tokens[1]);
			tokens = readAndParse(br);
			routerID = Integer.parseInt(tokens[1]);
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
			
			//The predefined router port number that other neighbors can reach to it before building connection.
			tokens = readAndParse(br);
			routerPort = Integer.parseInt(tokens[1]);
			
			tokens = readAndParse(br);
			String neighborStr = tokens[1];
			configNeighbors =  neighborStr.split(",");
			
			tokens = readAndParse(br);
			String nonNeighborStr = tokens[1];
			String[] configNonNeighbors =  nonNeighborStr.split(",");
			blacklist = (List) Arrays.asList(configNonNeighbors);
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	String[] readAndParse(BufferedReader br) throws IOException {
		String line = br.readLine();
		String[] tokens = line.split(":");
		tokens[0] = tokens[0].trim();
		tokens[1] = tokens[1].trim();
		
		return tokens;
	}
	
}