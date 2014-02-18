package callgraphstat.superclass;

public class O {
	P p = null;

	O() {
	}

	public O(P p) {
		// TODO Auto-generated constructor stub
	}

	P getP() {
		this.igp();
		return this.p;
	}

	void igp() {
		p = new P();
	}
}
