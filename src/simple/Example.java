/**
 *
 * @ProjectName StaticCallGraph
 *
 * @PackageName simple
 *
 * @FileName Example.java
 * 
 * @FileCreated May 8, 2014
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
package simple;

import simple.CustomException.SomeClass;

public class Example {
	public static void main(String[] args) {
		B b1 = new B();
		A a2 = new A();
		try {
			f(b1);
		} catch (CustomException e) {
			e.printStackTrace();
			SomeClass sc = e.getNewList();
			if (sc != null) {
				sc.noMethod();
			}
		}
		g(b1);
	}

	static void f(A a2) throws CustomException {
		a2.foo();
	}

	static void g(B b3) {
		try {
			B b4 = b3;
			b4 = new C();
			b4.foo();
		} catch (Exception d) {
			System.err.println(d.getLocalizedMessage());
		}
	}
}
