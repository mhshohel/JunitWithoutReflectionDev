package callgraphstat.superclass;

public class P {
	private M m = null;

	P(M m) {
		this.m = m;
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
