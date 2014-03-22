/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraphstat.superclass
 *
 * @FileName A.java
 * 
 * @FileCreated Dec 13, 2013
 *
 * @Author MD. SHOHEL SHAMIM
 *
 * @CivicRegistration 19841201-0533
 *
 * MSc. in Software Technology
 *
 * Linnaeus University, Växjö, Sweden
 *
 */
package callgraphstat.superclass;


public class A {
	public int intval;
	protected int intvalA;
	public A valAOfA;
	protected A valAOfB;

	E foos(A v, int i, M a) {
		return null;
	}

	void foo() {
		// dval = new E();
		// int a = dval.val();
		// valAA = dval.val();
		// E ev = dval.valA();
	}

	void fooBException() {
		try {
			new M();
		} catch (ArrayStoreException is) {
			is.fillInStackTrace();
		}
	}

	void foos() {

	}

	void foos(Class<?> cl) {

	}

	A aval() {
		return null;
	}

	A as = null;

	void aav(A a) {
		as = a;
	}

	static void staticall() {

	}

	void obFo() {
	}

	public E foos(Object object, int i, double d, IInterface m, String stv) {
		m.foo();
		return new P().getE();
	}

	// static A na = null;
	// static int i;
	// E dval;
	// private A nums;
	//
	// // static E e = new E();
	// // private static int valSS = 5;
	// // public static int valS = 5;
	// // static int valA = 5;
	// public int valAA = 10;
	// private int valAAA = 55;
	// protected int valAAAA = 66;

	// void fos(A s) {
	//
	// }
}
