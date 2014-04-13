package callgraphstat.superclass;

public class SuperClassA {
	static SuperClassA b = null;

	public SuperClassA(E e) {

	}

	public static void lol() {

	}

	// static SuperClassA b = new C();
	// public SuperClassA val;
	// public F fval;
	// public SuperClassA aVal;
	// protected E eval = null;
	//

	protected B retB() {
		return new B(null);
	}

	public void have() {
		new E();
		this.aValEx();
		this.exam();
	}

	void aValEx() {
		System.err.println("I am From SuperClassA");
		f();
		lol();
	}

	protected F f() {
		F f = new F();
		return f;
	}

	//
	// void add(A a) {
	//
	// }
	//
	void exam() {
	}

	// public void aValEx() {
	// // System.err.println("I am from SuperClass");
	// }
}
