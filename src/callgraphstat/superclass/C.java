/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraphstat.superclass
 *
 * @FileName C.java
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

public class C extends B {

	public C() {

	}

	public C(A c, Object d, B ds, Object s) {
	}

	void foooC() {
		System.out.println("FOOOOOOOOC");
	}

	void foo() {
		foooC();
		// A v = new B();
		// int c;
	}
}
