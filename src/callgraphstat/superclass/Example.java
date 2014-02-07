/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraphstat.superclass
 *
 * @FileName Example.java
 * 
 * @FileCreated Dec 13, 2013
 *
 * @Author MD. SHOHEL SHAMIM
 *
 * @CivicRegistration 19841201-0533
 *
 * MSc. in Software Technology
 *
 * Linnaeus University, V�xj�, Sweden
 *
 */
package callgraphstat.superclass;

public class Example {
	// static A na = null;

	/**
	 * @param Example
	 */
	public static void main(String[] args) {
		// na = new A();
		// A a = null;
		// new C();
		B b1 = new B();
		A a2 = new A();
		f(b1);
		g(b1);
		// a = new C();
		// a = b1;
		// A d = get();
	}

	static void f(A a2) {
		a2.foo();
	}

	static void g(B b3) {
		B b4 = b3;
		b4 = new C();
		b4.foo();
	}

	static A get() {
		return new D();
	}
}
