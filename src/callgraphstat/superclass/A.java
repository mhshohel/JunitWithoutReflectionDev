package callgraphstat.superclass;

public class A extends SuperClassA {
	// static SuperClassA b = null;
	public B b = null;

	// public D dval = null;
	// Object oObject = null;
	//
	// // static SuperClassA seval = new B();
	// void claA() {
	// dval = new D();
	// D s = dval;
	// privatem();
	// }
	//
	// private void privatem() {
	//
	// }
	//

	public A(SuperClassA b) {

	}

	public A() {

	}

	public static void lol() {

	}

	public void aValEx() {
		System.err.println("I am From A");
		b();
	}

	//
	// SuperClassA getVal() {
	// b = new B();
	// return this.b;
	// }
	//
	// void bval(B b) {
	//
	// }
	//
	// void aVC(SuperClassA val) {
	// ((A) val).b();
	// }
	//
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
