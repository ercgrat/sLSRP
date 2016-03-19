package sLSRP;

public class LSAHistory {

	private int[] table;
	Configuration config;
	
	public LSAHistory(Configuration config) {
		this.config = config;
		table = new int[config.maxNetworkSize];
	}
	
	public void setLastSequenceNumber(int router, int sequenceNumber) {
		if(router > 0 && router <= config.maxNetworkSize) {
			table[router-1] = sequenceNumber;
		}
	}
	
	public int getLastSequenceNumber(int router) {
		if(router > 0 && router <= config.maxNetworkSize) {
			return table[router-1];
		} else {
			return Integer.MAX_VALUE;
		}
	}
}