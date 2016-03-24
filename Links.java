package sLSRP;
import java.util.*;

public class Links {

	public ArrayList<Link> links;
	public ArrayList<Link> neighborLinks;
	Configuration config;

	public Links(Configuration config) {
		this.config = config;
		links = new ArrayList<Link>();
		neighborLinks = new ArrayList<Link>();
	}
	
	public void addLink(int A, int B) {
		if(A == B) {
			return;
		}
		Link l = new Link(A, B);
		if(!links.contains(l)) {
			links.add(l);
			if(A == config.routerID || B == config.routerID) {
				neighborLinks.add(l);
			}
		}
	}
	
	public void removeLink(Link l) {
		links.remove(l);
		neighborLinks.remove(l);
	}

}