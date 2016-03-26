package sLSRP;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is singleton in the program so that every class in the program can access to this same instance.
 * It store all the neighbor nodes information and relative links.
 * It has also responsible for the calculation of the short path algorithm.
 */
public class NetworkInfo {
	private static  NetworkInfo network;
	private NetworkInfo(){}
	public static NetworkInfo getInstance(){
		if(network==null)
			network = new NetworkInfo();
		return network;
	}
	
	private List neighbors = new ArrayList();
	public List getNeighbors() {
		return neighbors;
	}
	
	
}
