package callgraphstat.superclass;

public class P {
	private M m = null;

	P(M m) {
		this.m = m;
	}

	P(M m, int m1, String m2, M m3, double m4) {

	}

	P() {
	}

	public E getE() {
		return new E();
	}

	int getVal() {
		return 5;
	}
}
