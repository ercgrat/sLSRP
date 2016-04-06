package sLSRP;

import java.util.*;

/**
 * This class is singleton in the program so that every class in the program can access to this same instance.
 * It store all the neighbor nodes information and relative links.
 * It is also responsible for the calculation of the short path algorithm.
 */
public class NetworkInfo {
	private static NetworkInfo network;
	private Configuration config;
	
	private NetworkInfo() {}
	
	public static NetworkInfo getInstance(){
		if(network == null) {
			network = new NetworkInfo();
		}
		return network;
	}
	
	private HashMap<Integer,RouterData> routers = new HashMap<Integer,RouterData>();
	private List<Link> links = new ArrayList();
	private LSAHistory lsaHistory;
	
	public void setConfiguration(Configuration config) {
		this.config = config;
		lsaHistory = new LSAHistory(config);
	}
	
	
	public HashMap<Integer,RouterData> getRouters() {
		return routers;
	}
	
	public List<Link> getLinks() {
		return links;
	}
	
	public List<Link> getNeighborLinks(int routerID) {
		List<Link> neighborList = new ArrayList<Link>();
		for(int i = 0; i < links.size(); i++) {
			if(links.get(i).A == routerID || links.get(i).B == routerID) {
				neighborList.add(links.get(i));
			}
		}
		return neighborList;
	}
	
	public LSAHistory getLSAHistory() {
		return lsaHistory;
	}
	
	private Set<Integer> settledNodes;
	private Set<Integer> unSettledNodes;
	private Map<Integer, Integer> predecessors;
	private Map<Integer, Integer> distance;
	public void execute(int sourceRouter) {
	    settledNodes = new HashSet<Integer>();
	    unSettledNodes = new HashSet<Integer>();
	    distance = new HashMap<Integer, Integer>();
	    predecessors = new HashMap<Integer, Integer>();
	    distance.put(sourceRouter, 0);
	    unSettledNodes.add(sourceRouter);
	    while (unSettledNodes.size() > 0) {
	      int node = getMinimum(unSettledNodes);
	      settledNodes.add(node);
	      unSettledNodes.remove(node);
	      findMinimalDistances(node);
	    }
	  }

	  private void findMinimalDistances(Integer node) {
	    List<Integer> adjacentNodes = getNeighbors(node);
	    for (int target : adjacentNodes) {
	      if (getShortestDistance(target) > getShortestDistance(node)
	          + getDistance(node, target)) {
	        distance.put(target, getShortestDistance(node)
	            + getDistance(node, target));
	        predecessors.put(target, node);
	        unSettledNodes.add(target);
	      }
	    }

	  }

	  private int getDistance(int node, int target) {
	    for (Link link : links) {
	      if (link.A==node
	          && link.B==target) {
	        return (int) link.delay;
	      }
	    }
	    throw new RuntimeException("Should not happen");
	  }

	  private List<Integer> getNeighbors(int node) {
	    List<Integer> neighbors = new ArrayList<Integer>();
	    for (Link link : links) {
	      if (link.A==node
	          && !isSettled(link.B)) {
	        neighbors.add(link.B);
	      }
	    }
	    return neighbors;
	  }

	  private int getMinimum(Set<Integer> routers) {
	    int minimum =0;
	    for (int r : routers) {
	      if (minimum == 0) {
	        minimum = r;
	      } else {
	        if (getShortestDistance(r) < getShortestDistance(minimum)) {
	          minimum = r;
	        }
	      }
	    }
	    return minimum;
	  }

	  private boolean isSettled(int r) {
	    return settledNodes.contains(r);
	  }

	  private int getShortestDistance(int destination) {
	    Integer d = distance.get(destination);
	    if (d == null) {
	      return Integer.MAX_VALUE;
	    } else {
	      return d;
	    }
	  }

	  /*
	   * This method returns the path from the source to the selected target and
	   * NULL if no path exists
	   */
	  public LinkedList<Integer> getPath(int target) {
	    LinkedList<Integer> path = new LinkedList<Integer>();
	    int step = target;
	    // check if a path exists
	    if (predecessors.get(step) == null) {
	      return null;
	    }
	    path.add(step);
	    while (predecessors.get(step) != null) {
	      step = predecessors.get(step);
	      path.add(step);
	    }
	    // Put it into the correct order
	    Collections.reverse(path);
	    return path;
	  }
}
