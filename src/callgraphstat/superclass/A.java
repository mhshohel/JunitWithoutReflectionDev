package callgraphstat.superclass;

public class A extends SuperClassA {
	public B b = null;
	D dval = null;
	Object oObject = null;
	static SuperClassA seval = new B();

	void aValEx() {
		System.err.println("I am From A");
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
