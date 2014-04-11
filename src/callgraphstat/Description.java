/**
 *
 * @ProjectName StaticCallGraph
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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;

public final class Description implements Comparable<Description> {
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

	public int id = 0;
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
	private Description superClass = null;
	public Map<String, Stack<Object>> staticFields = new LinkedHashMap<String, Stack<Object>>();
	public boolean isSuperClassObjectInitiated = false;
	public boolean isVisitedToCheckStaticField = false;

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

	public List<Object> vals = new ArrayList<Object>();

	private void initialize() {
		this.id = Static.getNewID();
		this.isVisitedToCheckStaticField = false;
		this.classDescriptions = new LinkedHashMap<String, Description>();
		this.methods = new LinkedHashMap<Method, MethodVisitor>();
		this.interfaces = new ArrayList<Description>();
		this.superClass = null;
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
			if (!this.classDescriptions.containsKey(cls.getName())) {
				this.classDescriptions.put(cls.getName(), new Description(cls,
						ClassCategory.REGULAR, this.classDescriptions));
			}
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
					Static.nodes.add(methodVisitor.toString());
				}
				// Collections.sort(description.nodes);
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

	public final void initializeInterfacesAndSuperClasses() throws Exception {
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
				this.superClass = description;
			}
		}
		this.isSuperClassObjectInitiated = (this.superClass == null) ? true
				: false;
	}

	// make new copy
	public final Description copy() {
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
		dummy.staticFields = this.staticFields;
		dummy.isSuperClassObjectInitiated = (this.superClass == null) ? true
				: false;
		initializeMethodVisitor(dummy, true);
		dummy.isVisitedToCheckStaticField = this.isVisitedToCheckStaticField;
		return dummy;
	}

	public final Description copyAll() {
		Description dummy = new Description();
		dummy.clas = this.clas;
		dummy.javaClass = this.javaClass;
		dummy.classVisitor = this.classVisitor.copyAll();
		dummy.constants = this.constants;
		dummy.classDescriptions = this.classDescriptions;
		dummy.interfaces = this.interfaces;
		dummy.superClass = this.superClass;
		dummy.classCategory = this.classCategory;
		dummy.classInputStream = this.classInputStream;
		dummy.classType = this.classType;
		dummy.staticFields = this.staticFields;
		dummy.isSuperClassObjectInitiated = (this.superClass == null) ? true
				: false;
		initializeMethodVisitor(dummy, true);
		dummy.isVisitedToCheckStaticField = this.isVisitedToCheckStaticField;
		return dummy;
	}

	// add single value only not GroupOfValue class
	// TODO: FIX IT
	public void addValueToStaticField(Description description,
			String fieldName, Object currentValue, Type type,
			ReferenceType referenceType, boolean isConditions) {
		try {
			Description referanceDescription = this.classDescriptions
					.get(referenceType.toString());
			if (referanceDescription != null) {
				Stack<Object> field = getValuesFromStaticField(
						referanceDescription, fieldName);
				if (field != null) {
					if (!isConditions) {
						if (!field.isEmpty()) {
							field.clear();
						}
					}
					if (currentValue instanceof Collection) {
						for (Object stackValues : (Collection<?>) currentValue) {
							if (!(stackValues instanceof GroupOfValues)) {
								Object thisValue = Static
										.verifyTypeFromObjectsToStore(
												stackValues, type, description);
								if (!(field.contains(thisValue))) {
									field.add(thisValue);
								}
							} else {
								GroupOfValues gv = (GroupOfValues) stackValues;
								for (Object gvv : gv.getAllValues(type,
										description)) {
									if (!(field.contains(gvv))) {
										field.add(gvv);
									}
								}
							}
						}
					} else {
						if (!(currentValue instanceof GroupOfValues)) {
							if (!(field.contains(currentValue))) {
								field.add(currentValue);
							}
						} else {
							GroupOfValues gov = (GroupOfValues) currentValue;
							for (Object govObject : gov.getAllValues(type,
									description)) {
								if (!(field.contains(govObject))) {
									field.add(govObject);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public void addStaticFieldsValue(String key, Object value) {
		// if not found must look into super class
		if (!this.staticFields.containsKey(key)) {
			this.staticFields.put(key, new Stack<Object>());
		} else {
			this.staticFields.get(key).add(value);
		}
	}

	public Map<String, Stack<Object>> getStaticFields() {
		return this.staticFields;
	}

	public final Stack<Object> getStaticFieldValues(String key) {
		return this.staticFields.get(key);
	}

	private Stack<Object> getValuesFromStaticField(Description description,
			String fieldName) {
		Stack<Object> fields = description.getStaticFieldValues(fieldName);
		if (fields == null) {
			if (description.getSuperClassDescription() != null) {
				fields = getValuesFromStaticField(
						description.getSuperClassDescription(), fieldName);
			}
		}
		return fields;
	}

	public final Object getValueFromStaticField(Description description,
			String fieldName, ReferenceType referenceType) {
		Stack<Object> fields = getValuesFromStaticField(description, fieldName);
		if (fields != null) {
			return fields;
		} else {
			return Static.NULL;
		}
	}

	public final List<Description> getInterfaces() {
		return this.interfaces;
	}

	public final void setSuperClassDescription(Description description) {
		this.superClass = description;
	}

	public final Description getSuperClassDescription() {
		return this.superClass;
	}

	public final Description getDescriptionByJavaClass(JavaClass jc) {
		for (Entry<String, Description> entry : this.classDescriptions
				.entrySet()) {
			if (entry.getValue().getJavaClass().equals(jc)) {
				return entry.getValue();
			}
		}
		return null;
	}

	public final Description getDescriptionByClass(Class<?> cls) {
		for (Entry<String, Description> entry : this.classDescriptions
				.entrySet()) {
			if (entry.getValue().getActualClass().equals(cls)) {
				return entry.getValue();
			}
		}
		return null;
	}

	public final Description getDescriptionByClassName(String name) {
		for (Entry<String, Description> entry : this.classDescriptions
				.entrySet()) {
			if (entry.getValue().getActualClass().getName()
					.equalsIgnoreCase(name)) {
				return entry.getValue();
			}
		}
		return null;
	}

	public final Description getDescriptionByKey(String key) {
		return this.classDescriptions.get(key);
	}

	public boolean hasDescription(String key) {
		return this.classDescriptions.containsKey(key);
	}

	@Override
	public int compareTo(Description description) {
		return this.getActualClass().getName()
				.compareTo(description.getActualClass().getName());
	}

	@Override
	public int hashCode() {
		return clas.hashCode() + javaClass.hashCode() + classVisitor.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Description) {
			Description description = (Description) object;
			return this.hashCode() == description.hashCode();
		}
		return false;
	}

	public final ClassVisitor getClassVisitor() {
		return this.classVisitor;
	}

	public final ConstantPoolGen getConstantPoolGen() {
		return this.constants;
	}

	public final Class<?> getActualClass() {
		return this.clas;
	}

	public final void setClassCategory(ClassCategory classCategory) {
		this.classCategory = classCategory;
	}

	public final ClassCategory getClassCategory() {
		return this.classCategory;
	}

	public final InputStream getClassInputStream() {
		return classInputStream;
	}

	public final String getClassName() {
		return clas.getName();
	}

	public final ClassType getClassType() {
		return this.classType;
	}

	public final JavaClass getJavaClass() {
		return javaClass;
	}

	public final Method getMethodByNameAndTypeArgs(String methodName,
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

	public final MethodVisitor getMethodVisitorByNameAndTypeArgs(
			Description description, String methodName, Type[] methodTypeArgs,
			boolean isDeepSearch) {
		String name = null;
		Type[] types = null;
		Method method = null;
		if (methodName == null) {
			return null;
		}
		for (Entry<Method, MethodVisitor> entry : description.methods
				.entrySet()) {
			method = entry.getKey();
			name = method.getName();
			types = method.getArgumentTypes();
			if (methodName.equalsIgnoreCase(name)) {
				if (Arrays.deepEquals(methodTypeArgs, types)) {
					return entry.getValue();
				}
			}
		}
		MethodVisitor methodVisitor = null;
		if (isDeepSearch) {
			if (description.superClass != null) {
				methodVisitor = getMethodVisitorByNameAndTypeArgs(
						description.superClass, methodName, methodTypeArgs,
						isDeepSearch);
			}
			if (methodVisitor == null && !description.interfaces.isEmpty()) {
				for (Description des : description.interfaces) {
					methodVisitor = getMethodVisitorByNameAndTypeArgs(des,
							methodName, methodTypeArgs, isDeepSearch);
					if (methodVisitor != null) {
						return methodVisitor;
					}
				}
			}
		}
		return methodVisitor;
	}

	public final List<MethodVisitor> getMethodVisitorByName(String methodName) {
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

	public final String getMethodFullName(Method method) {
		return method.toString().substring(0,
				(method.toString().indexOf(')') + 1));
	}

	public final String getMethodName(Method method) {
		return method.getName();
	}

	public final Map<Method, MethodVisitor> getMethods() {
		return this.methods;
	}

	public final MethodVisitor getMethodVisitorByMethod(Method method) {
		return this.methods.get(method);
	}

	public final boolean isGeneratedCode() {
		return (this.classCategory == ClassCategory.GENERATED);
	}

	public final boolean isRegularClass() {
		return (this.classCategory == ClassCategory.REGULAR);
	}

	public final boolean isTestClass() {
		return (this.classCategory == ClassCategory.TEST);
	}

	public final Map<String, Description> getListOfClassDescriptions() {
		return this.classDescriptions;
	}

	public final String toString() {
		return this.getActualClass().getName();
	}
}