/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraphstat.superclass
 *
 * @FileName B.java
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

public class B extends A {
	public B(A v, A vl, A c, A d, A f, A g) {

	}

	public B() {
		System.out.println("SD");
	}

	void foos(A v, A a) {

	}

	void foo() {
	}

	static void st() {

	}

	A val() {
		return new C();
	}
}
