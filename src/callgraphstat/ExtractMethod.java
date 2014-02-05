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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

public class ExtractMethod implements Comparable<ExtractMethod> {
	private Method method = null;
	// node as target of edge
	private String node = "";
	// keep list source
	private Set<String> sources = new HashSet<String>();
	private Map<String, Set<Description>> values = new HashMap<String, Set<Description>>();

	// save values with param and field name

	public ExtractMethod(Method method, String javaClassName) {
		this.method = method;
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

	public Method getMethod() {
		return this.method;
	}

	public String toString() {
		return this.node;
	}

	public String getNode() {
		return this.node;
	}

	public void addSource(String source) {
		this.sources.add(source);
	}

	public Set<String> getSources() {
		return this.sources;
	}

	public List<String> getEdges() {
		List<String> edges = new ArrayList<String>();
		for (String source : this.sources) {
			edges.add(this.node + " --> " + source);
		}
		return edges;
	}

	public void addValues(String var, Description description) {
		Set<Description> value = this.values.get(var);
		value.add(description);
	}

	@Override
	public int compareTo(ExtractMethod node) {
		return getMethod().getName().compareTo(node.getMethod().getName());
	}
}
