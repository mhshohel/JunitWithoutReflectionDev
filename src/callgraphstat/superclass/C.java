package callgraphstat.superclass;

public class C extends SuperClassA {

	public C(E e) {
		super(e);
	}

	static C getC() {
		return new C(new E());
	}

}
