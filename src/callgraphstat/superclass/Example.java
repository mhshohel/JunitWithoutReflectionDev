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
 * Linnaeus University, Växjö, Sweden
 *
 */
package callgraphstat.superclass;

public class Example {

    /**
     * @param Example
     */
    public static void main(String[] args) {
	B b1 = new B();
	A a2 = new A();
	f(b1);
	g(b1);
    }

    static void f(A a2) {
	a2.foo();
    }

    static void g(B b3) {
	B b4 = b3;
	b4 = new C();
	b4.foo();
    }
}
