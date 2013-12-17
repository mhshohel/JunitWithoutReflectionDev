/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraphstat
 *
 * @FileName JonasAClass.java
 * 
 * @FileCreated Nov 21, 2013
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
package callgraphstat.testclasses;

public class JonasAClass implements JonasInterface {

    public JonasAClass(String n) {

    }

    public JonasAClass(Sec s) {

    }

    public JonasAClass() {
    }

    @Override
    public void interfaceMethod() {
	s.dck();
	methodA();
    }

    public void methodA() {
	new JonasSimpleClass().someMethod();
    }

    @Override
    public void intrfaceCheckMethod(JonasInterface ji, JonasInterface jj,
	    JonasInterface jc, JonasInterface jf) {
    }

}
