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
import org.apache.bcel.generic.MethodGen;
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
	private Map<String, Description> classDescriptions = null;
	private Map<Method, MethodVisitor> methods = null;
	private List<Description> interfaces = null;
	private List<Description> superClass = null;
	private List<String> nodes = null;
	private List<String> edges = null;

	private Description() {
		initialize();
	}

	public Description(Class<?> clas, ClassCategory classCategory,
			Map<String, Description> classDescriptions) throws Exception {
		initialize();
		this.classCategory = classCategory;
		initialize(clas, classDescriptions);
	}

	public Description(Class<?> clas, Map<String, Description> classDescriptions)
			throws Exception {
		initialize();
		this.classCategory = ClassCategory.REGULAR;
		initialize(clas, classDescriptions);
	}

	private void initialize() {
		this.classDescriptions = new HashMap<String, Description>();
		this.methods = new HashMap<Method, MethodVisitor>();
		this.interfaces = new ArrayList<Description>();
		this.superClass = new ArrayList<Description>();
		this.nodes = new ArrayList<String>();
		this.edges = new ArrayList<String>();
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
		initializeMethodVisitor(this, false);
	}

	// if it a copy then true else false
	private void initializeMethodVisitor(Description description, boolean isCopy) {
		if (!description.clas.isInterface()) {
			MethodVisitor methodVisitor = null;
			MethodGen methodGen = null;
			if (!isCopy) {
				for (Method method : description.javaClass.getMethods()) {
					methodGen = new MethodGen(method, description
							.getJavaClass().getClassName(),
							description.getConstantPoolGen());
					methodVisitor = new MethodVisitor(description,
							description.getClassVisitor(), method, methodGen);
					description.methods.put(method, methodVisitor);
					description.nodes.add(methodVisitor.toString());
				}
				Collections.sort(description.nodes);
			} else {
				for (Entry<Method, MethodVisitor> entry : this.getMethods()
						.entrySet()) {
					methodVisitor = new MethodVisitor(description,
							description.getClassVisitor(), entry.getKey(),
							entry.getValue().getMethodGen());
					description.methods.put(entry.getKey(), methodVisitor);
				}
			}
		}
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

	// make new copy
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
		initializeMethodVisitor(dummy, true);
		dummy.nodes = this.nodes;
		dummy.edges = this.edges;
		return dummy;
	}

	// // make same copy
	// public Description clone() {
	// Description dummy = new Description();
	// dummy.clas = this.clas;
	// dummy.javaClass = this.javaClass;
	// // need to have clone
	// dummy.classVisitor = this.classVisitor.clone();
	// dummy.constants = this.constants;
	// dummy.classDescriptions = this.classDescriptions;
	// dummy.interfaces = this.interfaces;
	// dummy.superClass = this.superClass;
	// dummy.classCategory = this.classCategory;
	// dummy.classInputStream = this.classInputStream;
	// dummy.classType = this.classType;
	// dummy.methods = new HashMap<Method, ExtractMethod>(this.methods);
	// dummy.nodes = this.nodes;
	// dummy.edges = this.edges;
	// return dummy;
	// }

	public List<String> getNodes() {
		List<String> nodes = new ArrayList<String>();
		Description description = null;
		for (Entry<String, Description> entry : this.classDescriptions
				.entrySet()) {
			description = entry.getValue();
			for (Entry<Method, MethodVisitor> methodEntry : description
					.getMethods().entrySet()) {
				nodes.add(methodEntry.getValue().toString());
			}
		}
		return nodes;
	}

	public boolean addEdge(String source, String target) {
		String edge = source.concat(" -- > ").concat(target).trim();
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

	public Description getDescriptionByClassName(String name) {
		for (Entry<String, Description> entry : this.classDescriptions
				.entrySet()) {
			if (entry.getValue().getActualClass().getName()
					.equalsIgnoreCase(name)) {
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
		for (Entry<Method, MethodVisitor> entry : this.methods.entrySet()) {
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

	public List<MethodVisitor> getMethodVisitorByName(String methodName) {
		String name = null;
		List<MethodVisitor> methods = new ArrayList<MethodVisitor>();

		for (Entry<Method, MethodVisitor> entry : this.methods.entrySet()) {
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

	public Map<Method, MethodVisitor> getMethods() {
		return this.methods;
	}

	public MethodVisitor getMethodVisitorByMethod(Method method) {
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
