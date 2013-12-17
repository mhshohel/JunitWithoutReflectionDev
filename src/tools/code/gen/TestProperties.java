/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName TestProperties.java
 * 
 * @FileCreated Mar 22, 2013
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

import java.util.Collection;

/**
 * <li><strong>TestProperties</strong></li>
 * 
 * <pre>
 * public class TestProperties
 * </pre>
 * <p>
 * Keep all the properties of Test annotation. Test annotation contains expected
 * and timeout method. This class keep them so that in code generation it can
 * use.
 * </p>
 * <br/>
 * 
 * @author Shohel Shamim
 */
public class TestProperties {
    /* Keep method name */
    private String methodName;
    /* Keep method with object and parameters and closing brace; */
    private String fullMethodName;
    /* Keep expected value (Exception), with .class extension */
    private String expected;
    /* Keep timeout value */
    private long timeout;

    /**
     * <li><strong>TestProperties</strong></li>
     * 
     * <pre>
     * public TestProperties(String methodName, String fullMethodName,
     * 	    String expected, int timeout)
     * </pre>
     * <p>
     * Constructor of TestProperties contains Method name, Method name with
     * object, Expected exception as value, and timeout value.
     * </p>
     * <br/>
     * 
     * @param methodName
     *            - method name.
     * @param fullMethodName
     *            - method full name.
     * @param expected
     *            - expected exception.
     * @param timeout
     *            - timeout value.
     * @author Shohel Shamim
     */
    public TestProperties(String methodName, String fullMethodName,
	    String expected, long timeout) {
	this.methodName = methodName;
	this.fullMethodName = fullMethodName;
	this.expected = expected;
	this.timeout = timeout;
    }

    /**
     * <li><strong>getMethodName</strong></li>
     * 
     * <pre>
     * public String getMethodName()
     * </pre>
     * <p>
     * Return method name.
     * </p>
     * <br/>
     * 
     * @return String - method name.
     * @author Shohel Shamim
     */
    public String getMethodName() {
	return methodName;
    }

    /**
     * <li><strong>getFullMethodName</strong></li>
     * 
     * <pre>
     * public String getFullMethodName()
     * </pre>
     * <p>
     * Return method full specification. Example: object.TestMethid().
     * </p>
     * <br/>
     * 
     * @return String - method's full specification.
     * @author Shohel Shamim
     */
    public String getFullMethodName() {
	return fullMethodName;
    }

    /**
     * <li><strong>getExpected</strong></li>
     * 
     * <pre>
     * public String getExpected()
     * </pre>
     * <p>
     * Return expected exception class. Example: java.lang.Exception.class.
     * </p>
     * <br/>
     * 
     * @return String - expected value.
     * @author Shohel Shamim
     */
    public String getExpected() {
	return expected;
    }

    /**
     * <li><strong>getTimeout</strong></li>
     * 
     * <pre>
     * public String getTimeout()
     * </pre>
     * <p>
     * Return timeout value. Default is 0.
     * </p>
     * <br/>
     * 
     * @return long - timeout value.
     * @author Shohel Shamim
     */
    public long getTimeout() {
	return timeout;
    }

    /**
     * <li><strong>equals</strong></li>
     * 
     * <pre>
     * public boolean equals(TestProperties testProperties)
     * </pre>
     * <p>
     * Return true if fullMethodName match with other object.
     * </p>
     * <br/>
     * 
     * @param testProperties
     *            - object of TestProperties.
     * @return boolean - true or false.
     * @author Shohel Shamim
     */
    public boolean equals(TestProperties testProperties) {
	if (this.fullMethodName.equalsIgnoreCase(testProperties.fullMethodName)) {
	    return true;
	}
	return false;
    }

    /**
     * <li><strong>contains</strong></li>
     * 
     * <pre>
     * public boolean contains(TestProperties testProperties)
     * </pre>
     * <p>
     * Return true if fullMethodName match with other object.
     * </p>
     * <br/>
     * 
     * @param testProperties
     *            - Collection of TestProperties object.
     * @return boolean - true or false.
     * @author Shohel Shamim
     */
    public boolean contains(Collection<TestProperties> testProperties) {
	for (TestProperties property : testProperties) {
	    if (equals(property)) {
		return true;
	    }
	}
	return false;
    }
}