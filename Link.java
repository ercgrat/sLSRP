package sLSRP;

public class Link {

	public enum Load { LIGHT, MEDIUM, HEAVY };

	public int A;
	public int B;
	public int delay;
	public Load load;
	
	public Link(int A, int B) {
		if(B < A) {
			int temp = A;
			this.A = B;
			this.B = temp;
		} else {
            this.A = A;
            this.B = B;
        }
        System.out.println(this.A);
        System.out.println(this.B);
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