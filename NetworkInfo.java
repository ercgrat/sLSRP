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
	
	private HashMap<Integer,RouterData> neighbors = new HashMap<Integer,RouterData>();
	private List<Link> links = new ArrayList();
	
	public static String SERVER_IP = "";
	public static int SERVER_PORT = 0;
	
	public void setConfiguration(Configuration config) {
		this.config = config;
	}
	
	public Configuration getConfiguration() {
		return this.config;
	}
	
	
	public HashMap<Integer,RouterData> getNeighbors() {
		return neighbors;
	}
	public void updateLinks(int sourceID, List<Link> links) {
		//int size = NetworkInfo.getInstance().getLinks().size();
		//for(int i=0;i<size;i++){
		//	System.out.println("A--->"+NetworkInfo.getInstance().getLinks().get(i).A);
		//	System.out.println("B--->"+NetworkInfo.getInstance().getLinks().get(i).B);
		//}
		//System.out.println("updateLinks a--->"+links.get(0).A);
		//System.out.println("updateLinks b--->"+links.get(0).B);
        // Update delay for existing links, add new links
		for(Link link : links) {
            if(!this.links.contains(link)) {
//            	if(config.routerID==link.B){
//            		link.B = link.A;
//            		link.A = config.routerID;
//            	}
                this.links.add(link);
            } else {
                Link old = this.links.get(this.links.indexOf(link));
                old.delay = link.delay;
            }
        }
        // Remove any absent links
        for(Link link : this.getNeighborLinks(sourceID)) {
            if(!links.contains(link)) {
                this.links.remove(link);
            }
        }
        //size = NetworkInfo.getInstance().getLinks().size();
		//for(int i=0;i<size;i++){
		//	System.out.println("A--->"+NetworkInfo.getInstance().getLinks().get(i).A);
		//	System.out.println("B--->"+NetworkInfo.getInstance().getLinks().get(i).B);
		//}
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
	
	
	
	  
	  private Set<Integer> settledNodes;
	  private Set<Integer> unSettledNodes;
	  private Map<Integer, Integer> predecessors;
	  private Map<Integer, Integer> distance;

	  public void execute(Integer source) {
	    settledNodes = new HashSet<Integer>();
	    unSettledNodes = new HashSet<Integer>();
	    distance = new HashMap<Integer, Integer>();
	    predecessors = new HashMap<Integer, Integer>();
	    distance.put(source, 0);
	    unSettledNodes.add(source);
	    while (unSettledNodes.size() > 0) {
	    	int node = getMinimum(unSettledNodes);
	      settledNodes.add(node);
	      unSettledNodes.remove(node);
	      findMinimalDistances(node);
	    }
	  }

	  private void findMinimalDistances(int node) {
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
//	    for (Link edge : links) {
//	      if (edge.A==node
//	          && edge.B==target) {
//	        return edge.getDelay();
//	      }
//	    }
		  
	    Link searchLink = new Link(node, target);
        Link existingLink = links.get(links.indexOf(searchLink));
        if(existingLink.load == Link.Load.LIGHT) {
            return 1;
        } else if(existingLink.load == Link.Load.MEDIUM) {
            return 2;
        } else if(existingLink.load == Link.Load.HEAVY) {
            return 3;
        }
	    throw new RuntimeException("Should not happen");
	  }

	  public List<Integer> getNeighbors(int node) {
	    List<Integer> neighbors = new ArrayList<Integer>();
	    for (Link edge : links) {
	      if (edge.A==node
	          && !isSettled(edge.B)) {
	        neighbors.add(edge.B);
	      }
	    }
	    return neighbors;
	  }

	  private int getMinimum(Set<Integer> vertexes) {
	    int minimum = 0;
	    for (int vertex : vertexes) {
	      if (minimum == 0) {
	        minimum = vertex;
	      } else {
	        if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
	          minimum = vertex;
	        }
	      }
	    }
	    return minimum;
	  }

	  private boolean isSettled(int vertex) {
	    return settledNodes.contains(vertex);
	  }

	  private int getShortestDistance(int destination) {
	    Integer d = distance.get(destination);
	    if (d == null) {
	      return Integer.MAX_VALUE;
	    } else {
	      return d;
	    }
	  }

	  
	  public LinkedList<Integer> getPath(Integer target) {
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
      
      private void categorizeDelays() {
          int minDelay = links.get(0).delay;
          int maxDelay = links.get(0).delay;
          for(Link link : links) {
              if(link.delay < minDelay) {
                  minDelay = link.delay;
              }
              if(link.delay > maxDelay) {
                  maxDelay = link.delay;
              }
          }
          int range = maxDelay - minDelay;
          int groupSize = range/3;
          for(Link link : links) {
              if(link.delay < minDelay + groupSize) {
                  link.load = Link.Load.LIGHT;
              } else if(link.delay < maxDelay - groupSize) {
                  link.load = Link.Load.MEDIUM;
              } else {
                  link.load = Link.Load.HEAVY;
              }
          }
      }
}
