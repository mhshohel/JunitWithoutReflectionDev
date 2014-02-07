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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

public class ExtractMethod implements Comparable<ExtractMethod> {
	private Description description = null;
	private ClassVisitor classVisitor = null;
	private Method method = null;
	private MethodGen methodGen = null;
	private MethodVisitor methodVisitor = null;
	// node as target of edge
	private String node = "";
	// keep list source
	private Map<String, Set<Description>> values = new HashMap<String, Set<Description>>();

	// save values with param and field name

	public ExtractMethod(Description description, ClassVisitor classVisitor,
			Method method, String javaClassName) {
		this.description = description;
		this.classVisitor = classVisitor;
		this.method = method;

		this.methodGen = new MethodGen(this.method, this.description
				.getJavaClass().getClassName(),
				this.description.getConstantPoolGen());
		this.methodVisitor = new MethodVisitor(description, this);

		Type[] types = method.getArgumentTypes();
		int length = types.length;
		String type = "(";
		for (int i = 0; i < length; i++) {
			type += ((i + 1) == length) ? types[i] : types[i] + ",";
		}
		type += ")";
		node = javaClassName + "." + method.getName() + type
				+ method.getReturnType();
	}

	public Description getDescription() {
		return this.description;
	}

	public ClassVisitor getClassVisitor() {
		return this.classVisitor;
	}

	public MethodGen getMethodGen() {
		return this.methodGen;
	}

	public MethodVisitor getMethodVisitor() {
		return this.methodVisitor;
	}

	public Method getMethod() {
		return this.method;
	}

	public String toString() {
		return this.node;
	}

	public String getNode() {
		return this.node;
	}

	public List<String> getEdges() {
		List<String> edges = new ArrayList<String>();
		for (String source : this.methodVisitor.getSources()) {
			edges.add(this.node + " --> " + source);
		}
		return edges;
	}

	//
	// public void addValues(String var, Description description) {
	// Set<Description> value = this.values.get(var);
	// if (value != null) {
	// value.add(description);
	// } else {
	// value = new HashSet<Description>();
	// value.add(description);
	// this.values.put(var, value);
	// }
	// }

	@Override
	public int compareTo(ExtractMethod node) {
		return getMethod().getName().compareTo(node.getMethod().getName());
	}
}
