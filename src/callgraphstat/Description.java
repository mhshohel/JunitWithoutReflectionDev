/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraphstat
 *
 * @FileName Description.java
 * 
 * @FileCreated Nov 28, 2013
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

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Type;

public class Description implements Comparable<Description> {
	public enum ClassCategory {
		GENERATED("Generated Code"), REGULAR("Regular Class"), TEST(
				"Test Class");
		private String category;

		private ClassCategory(String category) {
			this.category = category;
		}

		public String toString() {
			return this.category;
		}
	}

	public enum ClassType {
		INTERFACE("interface"), ABSTRACT("abstract"), FINAL("final"), ENUM(
				"enum"), CLASS("class");
		private String category;

		private ClassType(String category) {
			this.category = category;
		}

		public String toString() {
			return this.category;
		}
	}

	private Class<?> clas = null;
	private JavaClass javaClass = null;
	private ClassVisitor classVisitor = null;
	private ConstantPoolGen constants = null;
	private ClassCategory classCategory = null;
	private InputStream classInputStream = null;
	private ClassType classType = null;
	private Map<String, Description> classDescriptions = new HashMap<String, Description>();
	private Map<Method, ExtractMethod> methods = new HashMap<Method, ExtractMethod>();
	private List<Description> interfaces = new ArrayList<Description>();
	private List<Description> superClass = new ArrayList<Description>();
	private List<String> nodes = new ArrayList<String>();
	private List<String> edges = new ArrayList<String>();

	private Description() {
		// used to make a new copy of Description
	}

	public Description(Class<?> clas, ClassCategory classCategory,
			Map<String, Description> classDescriptions) throws Exception {
		this.classCategory = classCategory;
		initialize(clas, classDescriptions);
	}

	public Description(Class<?> clas, Map<String, Description> classDescriptions)
			throws Exception {
		this.classCategory = ClassCategory.REGULAR;
		initialize(clas, classDescriptions);
	}

	private void initialize(Class<?> clas,
			Map<String, Description> classDescriptions) throws Exception {
		this.clas = clas;
		this.classDescriptions = classDescriptions;
		try {
			String resourceName = clas.getName().replace('.',
					File.separatorChar)
					+ ".class";
			this.classInputStream = clas.getClassLoader().getResourceAsStream(
					resourceName);
			this.javaClass = new ClassParser(classInputStream, resourceName)
					.parse();
			this.classVisitor = new ClassVisitor(javaClass, this);
		} catch (Exception e) {
			throw new Exception("Cannot parse Class to JavaClass.");
		}
		this.constants = new ConstantPoolGen(this.javaClass.getConstantPool());
		int modifier = this.clas.getModifiers();
		if (this.clas.isInterface()) {
			this.classType = ClassType.INTERFACE;
		} else if (Modifier.isAbstract(modifier)) {
			this.classType = ClassType.ABSTRACT;
		} else if (Modifier.isFinal(modifier)) {
			this.classType = ClassType.FINAL;
		} else if (this.clas.isEnum()) {
			this.classType = ClassType.ENUM;
		} else {
			this.classType = ClassType.CLASS;
		}
		// Inner Class as Application
		for (Class<?> cls : this.clas.getDeclaredClasses()) {
			this.classDescriptions.put(cls.getName(), new Description(cls,
					ClassCategory.REGULAR, this.classDescriptions));
		}
		if (!this.clas.isInterface()) {
			ExtractMethod extractMethod = null;
			for (Method method : this.javaClass.getMethods()) {
				extractMethod = new ExtractMethod(this, this.classVisitor,
						method, this.javaClass.getClassName());
				this.methods.put(method, extractMethod);
				this.nodes.add(extractMethod.toString());
			}
		}
		Collections.sort(this.nodes);
	}

	public void initializeInterfacesAndSuperClasses() throws Exception {
		Description description = null;
		for (Class<?> cls : this.clas.getInterfaces()) {
			description = this.classDescriptions.get(cls.getName());
			if (description != null) {
				this.interfaces.add(description);
			}
		}
		Class<?> superClass = this.clas.getSuperclass();
		if (superClass != null) {
			description = this.classDescriptions.get(superClass.getName());
			if (description != null) {
				this.superClass.add(description);
			}
		}
	}

	public Description copy() {
		Description dummy = new Description();
		dummy.clas = this.clas;
		dummy.javaClass = this.javaClass;
		dummy.classVisitor = this.classVisitor.copy();
		dummy.constants = this.constants;
		dummy.classDescriptions = this.classDescriptions;
		dummy.interfaces = this.interfaces;
		dummy.superClass = this.superClass;
		dummy.classCategory = this.classCategory;
		dummy.classInputStream = this.classInputStream;
		dummy.classType = this.classType;
		dummy.methods = null;
		if (!this.clas.isInterface()) {
			ExtractMethod extractMethod = null;
			for (Method method : dummy.javaClass.getMethods()) {
				extractMethod = new ExtractMethod(dummy, dummy.classVisitor,
						method, dummy.javaClass.getClassName());
				dummy.methods.put(method, extractMethod);
			}
		}
		dummy.nodes = this.nodes;
		dummy.edges = this.edges;
		return dummy;
	}

	public List<String> getNodes() {
		List<String> nodes = new ArrayList<String>();
		Description description = null;
		for (Entry<String, Description> entry : this.classDescriptions
				.entrySet()) {
			description = entry.getValue();
			for (Entry<Method, ExtractMethod> methodEntry : description
					.getMethods().entrySet()) {
				nodes.add(methodEntry.getValue().toString());
			}
		}
		return nodes;
	}

	public boolean addEdge(String edge) {
		if (this.edges.contains(edge)) {
			return true;
		}
		this.edges.add(edge);
		return false;
	}

	public List<String> getSortedEdges() {
		Collections.sort(this.edges);
		return this.edges;
	}

	public List<String> getUnsortedEdges() {
		return this.edges;
	}

	public List<Description> getInterfaces() {
		return this.interfaces;
	}

	public List<Description> getSuperClass() {
		return this.superClass;
	}

	public Description getDescriptionByJavaClass(JavaClass jc) {
		for (Entry<String, Description> entry : this.classDescriptions
				.entrySet()) {
			if (entry.getValue().getJavaClass().equals(jc)) {
				return entry.getValue();
			}
		}
		return null;
	}

	public Description getDescriptionByClass(Class<?> cls) {
		for (Entry<String, Description> entry : this.classDescriptions
				.entrySet()) {
			if (entry.getValue().getActualClass().equals(cls)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public int compareTo(Description description) {
		return this.getActualClass().getName()
				.compareTo(description.getActualClass().getName());
	}

	public ClassVisitor getClassVisitor() {
		return this.classVisitor;
	}

	public ConstantPoolGen getConstantPoolGen() {
		return this.constants;
	}

	public Class<?> getActualClass() {
		return this.clas;
	}

	public void setClassCategory(ClassCategory classCategory) {
		this.classCategory = classCategory;
	}

	public ClassCategory getClassCategory() {
		return this.classCategory;
	}

	public InputStream getClassInputStream() {
		return classInputStream;
	}

	public String getClassName() {
		return clas.getName();
	}

	public ClassType getClassType() {
		return this.classType;
	}

	public JavaClass getJavaClass() {
		return javaClass;
	}

	public Method getMethodByNameAndTypeArgs(String methodName,
			Type[] methodTypeArgs) {
		String name = null;
		Type[] types = null;
		Method method = null;
		for (Entry<Method, ExtractMethod> entry : this.methods.entrySet()) {
			if (methodName != null) {
				method = entry.getKey();
				name = method.getName();
				types = method.getArgumentTypes();
				if (methodName.equalsIgnoreCase(name)) {
					if (Arrays.deepEquals(methodTypeArgs, types)) {
						return method;
					}
				}
			} else {
				return null;
			}
		}
		return null;
	}

	public List<ExtractMethod> getExtractedMethodByName(String methodName) {
		String name = null;
		List<ExtractMethod> methods = new ArrayList<ExtractMethod>();

		for (Entry<Method, ExtractMethod> entry : this.methods.entrySet()) {
			if (methodName != null) {
				name = entry.getKey().getName();
				if (name.equalsIgnoreCase(methodName)) {
					methods.add(entry.getValue());
				}
			}
		}
		return methods;
	}

	public String getMethodFullName(Method method) {
		return method.toString().substring(0,
				(method.toString().indexOf(')') + 1));
	}

	public String getMethodName(Method method) {
		return method.getName();
	}

	public Map<Method, ExtractMethod> getMethods() {
		return this.methods;
	}

	public ExtractMethod getExtractMethodByMethod(Method method) {
		return this.methods.get(method);
	}

	public boolean isGeneratedCode() {
		return (this.classCategory == ClassCategory.GENERATED);
	}

	public boolean isRegularClass() {
		return (this.classCategory == ClassCategory.REGULAR);
	}

	public boolean isTestClass() {
		return (this.classCategory == ClassCategory.TEST);
	}

	public String toString() {
		return this.getActualClass().getName();
	}
}
