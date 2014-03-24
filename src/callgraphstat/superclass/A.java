package callgraphstat.superclass;

public class A extends SuperClassA {
	public B b = null;
	D dval = null;
	Object oObject = null;
	static SuperClassA seval = new B();

	SuperClassA getVal() {
		b = new B();
		return this.b;
	}

	void a(SuperClassA val) {
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
