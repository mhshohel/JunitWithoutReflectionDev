package callgraphstat.superclass;

public class A extends SuperClassA {
	public B b = null;
	D dval = null;
	Object oObject = null;

	// static SuperClassA seval = new B();
	void claA() {
		privatem();
	}

	private void privatem() {

	}

	void aValEx() {
		System.err.println("I am From A");
		// b();
	}

	SuperClassA getVal() {
		b = new B();
		return this.b;
	}

	void bval(B b) {

	}

	void aVC(SuperClassA val) {
		((A) val).b();
	}

	private void b() {
		c();
	}

	private void c() {
		d();
	}

	private void d() {
		e();
	}

	private void e() {
		F f = f();
		f.h();
	}
}
