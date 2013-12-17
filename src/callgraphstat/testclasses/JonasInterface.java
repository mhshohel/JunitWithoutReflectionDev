/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraphstat
 *
 * @FileName JonasInterface.java
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

public interface JonasInterface {
    Sec s = new Sec();
    JonasBClass jie = new JonasBClass();

    void interfaceMethod();

    public void intrfaceCheckMethod(JonasInterface ji, JonasInterface jj,
	    JonasInterface jc, JonasInterface jf);
}
