/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraphstat
 *
 * @FileName ExtractMethod.java
 * 
 * @FileCreated Dec 12, 2013
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
package callgraphstat;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

public class ExtractMethod implements Comparable<ExtractMethod> {
    private Method method = null;
    private String extractMethod = "";

    public ExtractMethod(Method method, String javaClassName) {
	this.method = method;
	Type[] types = method.getArgumentTypes();
	int length = types.length;
	String type = "(";
	for (int i = 0; i < length; i++) {
	    type += ((i + 1) == length) ? types[i] : types[i] + ",";
	}
	type += ")";
	extractMethod = javaClassName + "." + method.getName() + type
		+ method.getReturnType();
    }

    public Method getMethod() {
	return this.method;
    }

    public String toString() {
	return this.extractMethod;
    }

    @Override
    public int compareTo(ExtractMethod node) {
	return getMethod().getName().compareTo(node.getMethod().getName());
    }
}
