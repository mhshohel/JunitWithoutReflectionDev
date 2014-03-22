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

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.sun.tools.internal.ws.wscompile.AbortException;

public class B extends A {
	private A valA;

	// private A valB = new E();
	// public A valC = new E();
	public int num;

	public B(int ssss, A ab, String aa, A d) {
		try {
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
		} catch (ParseException p) {
			p.initCause(new AbortException());
		}
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
		try {
			new M();
		} catch (ParseException p) {
			p.initCause(new AbortException());
		}
	}

	E foos(A v, int i, M m) {
		mValue = m;
		mValue.foo();
		this.foo();
		return new E();
	}

	void foo() {
		L l = new L();
	}

	static void st() {

	}

	A val() {
		return new C();
	}
}
