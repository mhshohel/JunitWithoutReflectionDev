package callgraphstat.superclass;

public class C extends SuperClassA {

	public C(E e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	static C getC() {
		return new C(new E());
	}

}
