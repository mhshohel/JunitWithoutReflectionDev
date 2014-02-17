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
	private A valA;

	// private A valB = new E();
	// public A valC = new E();
	public int num;

	public B(int ssss, A ab, String aa, A d) {
		valAOfA = ab;
		intval = ssss;
		// intvalA = ssss;
		// num = ssss;
		valA = valAOfA;
		valAOfB = valA;
		// int av = aa;
		// A dss = new A();
		// valB = d;
		// valC = c;
		// int a = 5;
		// String v = "s";
	}

	public B(A v, K vl) {
		// valA = v;
		// valB = valA;
		// this.valB = c;
		// valC = g;
		// A localV = d;
	}

	public B(A v, A vl, A c, A d, A f, A g) {
		// valA = v;
		// valB = valA;
		// this.valB = c;
		// valC = g;
		// A localV = d;
	}

	void B(A v, int i, K vl) {
		// valA = v;
		// valB = valA;
		// this.valB = c;
		// valC = g;
		// A localV = d;

	}

	public M mValue = null;

	public B(M m) {
		mValue = m;
	}

	public B() {
		// System.out.println("SD");
	}

	E foos(A v, int i, M m) {
		mValue = m;
		return new E();
	}

	void foo() {
	}

	static void st() {

	}

	A val() {
		return new C();
	}
}
