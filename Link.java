package sLSRP;

public class Link {

	public enum Load { LIGHT, MEDIUM, HEAVY };

	int A;
	int B;
	double delay;
	Load load;
	
	public Link(int A, int B) {
		if(B < A) {
			int temp = A;
			A = B;
			B = temp;
		}
		delay = 0;
		load = Load.LIGHT;
	}

    public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Link)) {
			return false;
		} else {
			Link l = (Link) obj;
			if(l.A == this.A && l.B == this.B) {
				return true;
			} else {
				return false;
			}
		}
    }

}