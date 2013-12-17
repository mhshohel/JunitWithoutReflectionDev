/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName Descriptions.java
 * 
 * @FileCreated Oct 3, 2013
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
package tools.staticcallgraph;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

/**
 * <li><strong>Description</strong></li>
 * 
 * <pre>
 * public class Description
 * </pre>
 * <p>
 * This class contains all basic information of a class. For example, list of
 * methods, how many method does it contains, what is the type of this class,
 * what are the JUnit test classes used this class, is it interface or other
 * type of class etc. to create a report.
 * </p>
 * <br/>
 * 
 * @author Shohel Shamim
 */
public class Description implements Comparable<Description> {
    /**
     * <li><strong>ClassCategory</strong></li>
     * 
     * <pre>
     * public enum ClassCategory
     * </pre>
     * <p>
     * This class used to mark class category as Regular, Test and Generated
     * Class.
     * </p>
     * <br/>
     * 
     * @author Shohel Shamim
     */
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

    /* Which classes are calling this class classes that used this class */
    private Set<Class<?>> calledByClasses = new HashSet<Class<?>>();
    /* Keep Original Class */
    private Class<?> clas = null;
    /*
     * classCategory: isIt a JUnit Test Class? Generated Code for a Test Class?
     * or Regular Class?
     */
    private ClassCategory classCategory = null;
    /*
     * Keep all class Description, actually it is an instance of actual
     * classDescriptions of MainClass
     */
    private List<Description> classDescriptions = null;
    /* Keep class location as inputstream */
    private InputStream classInputStream = null;
    /* Return type of class: abstract, final, enum, final, interface */
    private String classType = "";
    /*
     * Keep list of inner classes. Key: Class Name, Value: counter (how many
     * times it used)
     */
    private Set<Class<?>> innerClasses = new HashSet<Class<?>>();
    /* Keep Java Class */
    private JavaClass javaClass = null;
    /*
     * list of Methods, Key: Method name, Value: counter (how many times it
     * used)
     */
    private Map<Method, SimpleObject> methods = new HashMap<Method, SimpleObject>();
    /* Keep class package name */
    private String packageName = "";
    /* Keep class name with physical location */
    private String resourceName = "";

    /* Keep opcode of class */
    private OPCodeDescription myOpCode = new OPCodeDescription(this);
    private List<String> node = new ArrayList<String>();

    public void addNode(String node) {
	if (!this.node.contains(node)) {
	    this.node.add(node);
	}
    }

    public List<String> getNode() {
	Collections.sort(this.node);
	return this.node;
    }

    public String printNode() {
	StringBuilder sb = new StringBuilder();
	List<String> nodes = getNode();
	for (int i = 0; i < nodes.size(); i++) {
	    sb.append(((i + 1) != nodes.size()) ? nodes.get(i) + "\n" : nodes
		    .get(i));

	}
	return sb.toString();
    }

    /**
     * <li><strong><i>Description</i></strong></li>
     * 
     * <pre>
     * public Description(Class<?> clas, boolean isTestClass)
     * </pre>
     * 
     * <p>
     * Write something here...
     * </p>
     * 
     * @param clas
     *            - a class name with .class extension or Class Type
     * 
     * @param isTestClass
     *            - boolean mark it if it is JUnit test class or not.
     * 
     * @author Shohel Shamim
     * @throws Exception
     */
    public Description(Class<?> clas, ClassCategory classCategory,
	    List<Description> classDescriptions) throws Exception {
	this.classDescriptions = classDescriptions;
	this.clas = clas;
	try {
	    this.resourceName = clas.getName().replace('.', '/') + ".class";
	    InputStream classInputStream = clas.getClassLoader()
		    .getResourceAsStream(resourceName);
	    this.javaClass = new ClassParser(classInputStream, resourceName)
		    .parse();
	} catch (Exception e) {
	    throw new Exception("Cannot parse Class to JavaClass.");
	}
	this.packageName = this.clas.getPackage().getName();
	this.classCategory = classCategory;
	// Modifier
	int modifier = this.clas.getModifiers();
	if (this.clas.isInterface()) {
	    this.classType = "interface";
	} else if (Modifier.isAbstract(modifier)) {
	    this.classType = "abstract";
	} else if (Modifier.isFinal(modifier)) {
	    this.classType = "final";
	} else if (this.clas.isEnum()) {
	    this.classType = "enum";
	} else {
	    this.classType = "class";
	}
	// Inner Class as Application
	for (Class<?> cls : this.clas.getDeclaredClasses()) {
	    this.classDescriptions.add(new Description(cls,
		    ClassCategory.REGULAR, this.classDescriptions));
	    this.innerClasses.add(cls);
	}
	// Methods
	for (Method method : this.javaClass.getMethods()) {
	    String node = "";
	    //if (!this.clas.isInterface()) {
		Type[] types = method.getArgumentTypes();
		int length = types.length;
		String type = "(";
		for (int i = 0; i < length; i++) {
		    type += ((i + 1) == length) ? types[i] : types[i] + ",";
		}
		type += ")";
		node = this.javaClass.getClassName() + "." + method.getName()
			+ type + method.getReturnType();
		addNode(node);
	    //}

	    // remove if method name begins with '<'
	    String firstChar = method.getName().substring(0, 1);
	    // if (!firstChar.equalsIgnoreCase("<")) {
	    this.methods.put(method, new SimpleObject(node));
	    // }
	}
	Collections.sort(this.node);
    }

    @Override
    public int compareTo(Description description) {
	return this.getActualClass().getName()
		.compareTo(description.getActualClass().getName());
    }

    public Description(Description description) {
	this.classDescriptions = description.classDescriptions;
	this.resourceName = description.resourceName;
	this.classInputStream = description.classInputStream;
	this.clas = description.clas;
	this.javaClass = description.javaClass;
	this.classType = description.classType;
	this.packageName = description.packageName;
	this.classCategory = description.classCategory;
	this.innerClasses = description.innerClasses;
	this.methods = description.methods;
	this.calledByClasses = description.calledByClasses;
    }

    /**
     * <li><strong><i>addClassToCalledByTestClasses</i></strong></li>
     * 
     * <pre>
     * public void addClassToCalledByTestClasses(Class<?> clss)
     * </pre>
     * 
     * <p>
     * Add class list that comes from opcode.
     * </p>
     * 
     * @param Class
     *            <?> class - provide class
     * 
     * @author Shohel Shamim
     */
    public void addClassToCalledByClasses(Class<?> clss) {
	this.calledByClasses.add(clss);
    }

    public void addOPCodeDescription(OPCodeDescription opCodeDescription) {
	this.myOpCode = opCodeDescription;
    }

    public Description copy() {
	return new Description(this);
    }

    public Class<?> getActualClass() {
	return this.clas;
    }

    public ClassCategory getClassCategory() {
	return this.classCategory;
    }

    public InputStream getClassInputStream() {
	return classInputStream;
    }

    /**
     * <li><strong><i>getClassName</i></strong></li>
     * 
     * <pre>
     * public String getClassName()
     * </pre>
     * 
     * <p>
     * Return Ooriginal class name.
     * </p>
     * 
     * @return Class<?> - a class type.
     * 
     * @author Shohel Shamim
     */
    public String getClassName() {
	return clas.getName();
    }

    /**
     * <li><strong><i>getClassType</i></strong></li>
     * 
     * <pre>
     * public String getClassType()
     * </pre>
     * 
     * <p>
     * Return type of class: abstract, final, enum, final, interface
     * </p>
     * 
     * @return String - a String format class type.
     * 
     * @author Shohel Shamim
     */
    public String getClassType() {
	return this.classType;
    }

    // // return total size of all classes called
    // private int getCountedSizeCalledByTestClasses() {
    // Object[] values = this.calledByClasses.values().toArray();
    // int size = 0;
    // for (Object value : values) {
    // size += (Integer) value;
    // }
    // return size;
    // }

    public Set<Class<?>> getInnerClasses() {
	return this.innerClasses;
    }

    public JavaClass getJavaClass() {
	return javaClass;
    }

    public Method getMethodByNameAndTypeArgs(String methodName,
	    Type[] methodTypeArgs) {
	String name = null;
	Type[] types = null;
	Method method = null;
	for (Entry<Method, SimpleObject> entry : this.methods.entrySet()) {
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

    public String getMethodFullName(Method method) {
	return method.toString().substring(0,
		(method.toString().indexOf(')') + 1));
    }

    public String getMethodName(Method method) {
	return method.getName();
    }

    public Map<Method, SimpleObject> getMethods() {
	return this.methods;
    }

    public OPCodeDescription getOPCodeDescription() {
	return this.myOpCode;
    }

    /**
     * <li><strong><i>getPackageName</i></strong></li>
     * 
     * <pre>
     * public String getPackageName()
     * </pre>
     * 
     * <p>
     * Return package name.
     * </p>
     * 
     * @return String - a String format package name.
     * 
     * @author Shohel Shamim
     */
    public String getPackageName() {
	return this.packageName;
    }

    /**
     * @return the resourcename
     */
    public String getResourceName() {
	return resourceName;
    }

    public SimpleObject getSimpleObjectByNameAndTypeArgs(String methodName,
	    Type[] methodTypeArgs) {
	String name = null;
	Type[] types = null;
	Method method = null;
	for (Entry<Method, SimpleObject> entry : this.methods.entrySet()) {
	    if (methodName != null) {
		method = entry.getKey();
		name = method.getName();
		types = method.getArgumentTypes();
		if (methodName.equalsIgnoreCase(name)) {
		    if (Arrays.deepEquals(methodTypeArgs, types)) {
			return entry.getValue();
		    }
		}
	    } else {
		return null;
	    }
	}

	return null;
    }

    public SimpleObject getSimpleObjectByMethod(Method method) {
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

    public String getEdages() {
	StringBuilder sb = new StringBuilder();
	OPCodeProperties op = this.myOpCode.getOneTimeUseOnly();
	if (!op.getEdges().isEmpty()) {
	    sb.append(op.getEdges()).append("\n");
	}
	for (OPCodeProperties opc : this.myOpCode.getOtherMethodInvocations()) {
	    if (!opc.getEdges().isEmpty()) {
		sb.append(opc.getEdges()).append("\n");
	    }
	}

	return sb.toString();
    }

    /**
     * <li><strong><i>toString</i></strong></li>
     * 
     * <pre>
     * public String toString()
     * </pre>
     * 
     * <p>
     * Return formated output of this class.
     * </p>
     * 
     * @return String - a String format output.
     * 
     * @author Shohel Shamim
     */
    public String toString() {
	// int counter = 0;
	// int calledByTestClassSize = this.calledByTestClasses.size();
	// int innerClassSize = this.getInnerClasses().size();
	// int methodsSize = this.getMethods().size();
	// int maxCount = Math.max(Math.max(innerClassSize, methodsSize),
	// calledByTestClassSize);
	// String[] innerClasses = new String[innerClassSize];
	// String[] methods = new String[methodsSize];
	// StringBuffer sb = new StringBuffer();
	// sb.append("Package," + "Class Name," + "Class Type,"
	// + "Class Category," + "Called By Test Classes,"
	// + "Inner Class Size," + "Inner Classes," + "Methods Size,"
	// + "Methods Name," + " Method Called By," + "Is Test Class\n");
	// int i = 0;
	// for (Entry<Class<?>, Integer> entry :
	// this.getInnerClasses().entrySet()) {
	// innerClasses[i] = entry.getKey().getName() + "   count: ("
	// + entry.getValue() + ")";
	// i++;
	// }
	// i = 0;
	// String[] classesCalledBy = new String[calledByTestClassSize];
	// for (Entry<Class<?>, Integer> entry : this.calledByTestClasses
	// .entrySet()) {
	// classesCalledBy[i] = entry.getKey().getName() + "   count: ("
	// + entry.getValue() + ")";
	// i++;
	// }
	// i = 0;
	// String nameWithParam = null;
	// int index = 0;
	// Object[] methodsKey = null;
	// for (Entry<Method, SimpleObject> entry :
	// this.getMethods().entrySet()) {
	// nameWithParam = entry.getKey().toString();
	// index = nameWithParam.indexOf('[');
	// if (index > 0) {
	// nameWithParam = nameWithParam.substring(0, index).trim();
	// }
	// nameWithParam = "\"" + nameWithParam + "\"";
	// methods[i] = nameWithParam + "   count: ("
	// + entry.getValue().getCountedSizeOfEachMethodCall() + ")";
	// methodsKey = entry.getValue().getClasses().toArray();
	// for (int j = 0; j < methodsKey.length; j++) {
	// Class<?> cls = ((Class<?>) methodsKey[j]);
	// if (j == 0) {
	// methods[i] = methods[i]
	// + ","
	// + cls.getName()
	// + "   count: ("
	// + entry.getValue()
	// .getCountedSizeOfEachMethodCallByKey(cls)
	// + ")";
	// } else {
	// methods[i] = methods[i]
	// + "\n,,,,,,,,,"
	// + cls.getName()
	// + "   count: ("
	// + entry.getValue()
	// .getCountedSizeOfEachMethodCallByKey(cls)
	// + ")";
	// }
	// }
	// i++;
	// }
	// while (counter < maxCount) {
	// if (counter == 0) {
	// sb.append(this.getPackageName() + ",");
	// sb.append(this.getClassName() + " count: ("
	// + getCountedSizeCalledByTestClasses() + "),");
	// sb.append(this.getClassType() + ",");
	// sb.append(this.getClassCategory() + ",");
	// if (counter < calledByTestClassSize) {
	// sb.append(classesCalledBy[counter] + ",");
	// } else {
	// sb.append(",");
	// }
	// sb.append(innerClassSize + ",");
	// if (counter < innerClassSize) {
	// sb.append(innerClasses[counter] + ",");
	// } else {
	// sb.append(",");
	// }
	// sb.append(methodsSize + ",");
	// if (counter < methodsSize) {
	// sb.append(methods[counter] + ",");
	// } else {
	// sb.append(",");
	// }
	// } else {
	// sb.append(",,,,");
	// if (counter < calledByTestClassSize) {
	// sb.append(classesCalledBy[counter] + ",");
	// } else {
	// sb.append(",");
	// }
	// sb.append(",");
	// if (counter < innerClassSize) {
	// sb.append(innerClasses[counter] + ",");
	// } else {
	// sb.append(",");
	// }
	// sb.append(",");
	// if (counter < methodsSize) {
	// sb.append(methods[counter] + ",");
	// } else {
	// sb.append(",");
	// }
	// }
	// sb.append("\n");
	// counter++;
	// }
	// sb.append("\n");

	return this.getActualClass().getName();// sb.toString();
    }
}
