package callgraphstat.superclass;

import java.io.IOException;

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

	public A getAVal(B b) throws Exception, IOException {
		return getBVal(b);
	}

	private A getBVal(SuperClassA b) throws Exception {
		SuperClassA whatIsBVal = b;
		C c = C.getC();
		setB(c, whatIsBVal);
		return this.bvals.retA();
	}

	B bvals = null;

	private void setB(C c, SuperClassA b) throws Exception {
		try {
			this.bvals = c.retB();
			have();
			A avs = new A("Some", new D(null), new C(null), new E(),
					(this.bvals != null) ? new B(null) : new C(null));
		} catch (Exception e) {
			throw new Exception("Message");
		}
	}

	public SuperClassA baseFull = null;
	public SuperClassA baseFully = null;

	public A(String dd, SuperClassA oo, SuperClassA blp, E e, SuperClassA dfd) {
		super(e);
		SuperClassA blps = blp;
		this.baseFull = blp;
		String df = dd;
		E tu = e;
		baseFully = dfd;
	}

	public A getA() {
		return new A();
	}

	public B getB() {
		return null;
	}

	public A() {
		super(new E());
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
