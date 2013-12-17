/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraphstat.testclasses
 *
 * @FileName Student.java
 * 
 * @FileCreated Nov 29, 2013
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

public class Student extends Person {

    String name = gName();

    void getName() {
	name();
    }

}
