package callgraphstat.superclass;

public class B extends SuperClassA {
	public B(E e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	static E e = new E();
	public A val = new A();

	// public void have() {
	// new F();
	// }
	//
	public void aValEx() {
		System.err.println("I am From B");
		A bv = new A();
		A h = bv;
	}

	public A retA() {
		aValEx();
		return this.val;
	}
	//
	// void exam() {
	// }
}
