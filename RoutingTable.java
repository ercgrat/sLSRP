package sLSRP;

public class RoutingTable {

	private int[] table;
	Configuration config;
	
	public RoutingTable(Configuration config) {
		this.config = config;
		table = new int[config.maxNetworkSize];
		for(int i = 0; i < table.length; i++) {
			table[i] = -1;
		}
	}
	
	public void setNextHop(int destination, int nextHop) {
		if(destination > 0 && nextHop > 0 && destination <= config.maxNetworkSize && nextHop <= config.maxNetworkSize) {
			table[destination-1] = nextHop;
		}
	}
	
	public int getNextHop(int destination) {
		if(destination > 0 && destination <= config.maxNetworkSize) {
			return table[destination-1];
		} else {
			return -1;
		}
	}
}