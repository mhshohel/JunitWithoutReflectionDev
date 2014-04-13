package callgraphstat.superclass;

public class D extends SuperClassA {
	public D(E e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	public D getD() {
		return new D(null);
	}

	public SuperClassA val;

	SuperClassA getVals(SuperClassA v) {
		return v;
	}
}
