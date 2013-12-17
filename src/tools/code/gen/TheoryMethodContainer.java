/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName TheoryMethodContainer.java
 * 
 * @FileCreated Feb 20, 2013
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
package tools.code.gen;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <li><strong>TheoryMethodContainer</strong></li>
 * 
 * <pre>
 * public class TheoryMethodContainer
 * </pre>
 * <p>
 * A special class for DataPoint or DataPoints annotated methods. It contains
 * method and generated method for output. This class is used to avoid
 * duplication of override methods. In DataPoint and DataPoints generated
 * method's name for output can be same but never use same named method twice
 * that is declared in parent class. By using .contains in List it could be
 * ignored but due to DataPoint or DataPoints field length, it was not possible.
 * Because total number of DataPoint or DataPoints fields data is used in both
 * SUB and SUPER class whether both contains same named method or not;
 * </p>
 * <br/>
 * 
 * @author Shohel Shamim
 */
public class TheoryMethodContainer {
    /* Keep declared class */
    private Class<?> clas;
    /* Keep declared method */
    private Method method;
    /* Keep list of theory methods */
    private List<String> theoryMethodList = new ArrayList<String>();

    /**
     * <li><strong><i>TheoryMethodContainer</i></strong></li>
     * 
     * <pre>
     * public TheoryMethodContainer(Class<?> clas, Method method)
     * </pre>
     * 
     * <p>
     * Create TheoryMethodContainer to keep distinguished methods.
     * </p>
     * 
     * @param clas
     *            - a Class type.
     * 
     * @param method
     *            - a Method type.
     * 
     * @author Shohel Shamim
     */
    public TheoryMethodContainer(Class<?> clas, Method method) {
	this.clas = clas;
	this.method = method;
    }

    /**
     * <li><strong><i>addMethod</i></strong></li>
     * 
     * <pre>
     * public void addMethod(String method)
     * </pre>
     * 
     * <p>
     * Add generated method for output.
     * </p>
     * 
     * @param method
     *            - take method name.
     * 
     * @author Shohel Shamim
     */
    public void addMethod(String method) {
	this.theoryMethodList.add(method);
    }

    /**
     * <li><strong><i>getEntryClass</i></strong></li>
     * 
     * <pre>
     * public Class<?> getEntryClass()
     * </pre>
     * 
     * <p>
     * Return entry class.
     * </p>
     * 
     * @return Class - return a class type.
     * 
     * @author Shohel Shamim
     */
    public Class<?> getEntryClass() {
	return this.clas;
    }

    /**
     * <li><strong><i>getMethod</i></strong></li>
     * 
     * <pre>
     * public Method getMethod()
     * </pre>
     * 
     * <p>
     * Return actual method.
     * </p>
     * 
     * @return Method - return method type.
     * 
     * @author Shohel Shamim
     */
    public Method getMethod() {
	return this.method;
    }

    /**
     * <li><strong><i>getTheoryMethodList</i></strong></li>
     * 
     * <pre>
     * public List<String> getTheoryMethodList()
     * </pre>
     * 
     * <p>
     * Return generated methods from current method.
     * </p>
     * 
     * @return List<String> - return list of method.
     * 
     * @author Shohel Shamim
     */
    public List<String> getTheoryMethodList() {
	return this.theoryMethodList;
    }
}
