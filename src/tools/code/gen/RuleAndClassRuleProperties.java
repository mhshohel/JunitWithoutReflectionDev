/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName RuleAndClassRuleProperties.java
 * 
 * @FileCreated Feb 15, 2013
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.rules.ErrorCollector;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.rules.Verifier;

/**
 * <li><strong>RuleAndClassRuleProperties</strong></li>
 * 
 * <pre>
 * public class RuleAndClassRuleProperties <blockquote>implements</blockquote> AnnotaionList
 * </pre>
 * <p>
 * This class contains required properties for annotation Rule and ClassRule.
 * </p>
 * <br/>
 * 
 * @author Shohel Shamim
 */
public class RuleAndClassRuleProperties implements AnnotaionList {
    /* Compare method name after() */
    private final String AFTER = "after";
    /*
     * Get list of methods those need to write after execution of After for Rule
     * and after AfterClass for ClassRule
     */
    private List<String> afterTest;
    /* Compare method name before() */
    private final String BEFORE = "before";
    /*
     * Get list of methods those need to write before execution of Before for
     * Rule and after BeforeClass for ClassRule
     */
    private List<String> beforeTest;
    /* Keep class name */
    private String className;
    /* Keep current class that contains filed */
    private Class<?> claz;
    /* Compare method name failed() */
    private final String FAILED = "failed";
    /* Keep field */
    private Field field;
    /* Keep field's annotations */
    private Annotation[] fieldAnnotaiton;
    /* Keep Class of field */
    private Class<?> fieldClass;
    /* Keep field class name as String */
    private String fieldClassName;
    /* Keep field name as String */
    private String fieldName;
    /* Compare method name finished() */
    private final String FINISHED = "finished";
    /* True if required Description */
    private boolean hasDesctiption;
    /* True if required Throwable */
    private boolean hasThrowable;
    /* True if field is Static */
    private boolean isStatic;
    /* Keep generated object as String */
    private String object;
    /* Keep generated object's value */
    private String objectValue;
    /* Concatenation of object and objectValue */
    private String objectWithField;
    /* Compare method name starting() */
    private final String STARTING = "starting";
    /* Compare method name succeeded() */
    private final String SUCCEEDED = "succeeded";
    /* Compare method name verify() */
    private final String VERIFY = "verify";

    /**
     * <li><strong><i>RuleAndClassRuleProperties</i></strong></li>
     * 
     * <pre>
     * public RuleAndClassRuleProperties(Class<?> clz, Field field, String object, String objectValue)
     * </pre>
     * 
     * <p>
     * This class takes field that is annotated by Rule or ClassRule. Creates
     * unique object for them.
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * @param field
     *            - a Field Type.
     * @param object
     *            - String Type object name.
     * @param objectValue
     *            - String Type value of object.
     * 
     * 
     * @author Shohel Shamim
     */
    public RuleAndClassRuleProperties(Class<?> clz, Field field, String object,
	    String objectValue) {
	this.claz = clz;
	this.field = field;
	this.fieldName = field.getName();
	this.object = object;
	// Provide object; ex: ABC. or abc. if Class is ABC
	this.objectWithField = object + "." + this.fieldName + ".";
	// Provide initializer; ex: new ABC(); if class is ABC
	this.objectValue = objectValue;
	this.isStatic = Modifier.isStatic(this.field.getModifiers());
	initializeAllrequredProperties();
    }

    /**
     * <li><strong><i>getAfterTest</i></strong></li>
     * 
     * <pre>
     * public List<String> getAfterTest()
     * </pre>
     * 
     * <p>
     * Return list of methods those will be placed after After or AfterClass.
     * </p>
     * 
     * @return List<String> - a list of String contains method.
     * 
     * @author Shohel Shamim
     */
    public List<String> getAfterTest() {
	return this.afterTest;
    }

    /**
     * <li><strong><i>getBeforeTest</i></strong></li>
     * 
     * <pre>
     * public List<String> getBeforeTest()
     * </pre>
     * 
     * <p>
     * Return list of methods those will be placed before Before or BeforeClass.
     * </p>
     * 
     * @return List<String> - a list of String contains method.
     * 
     * @author Shohel Shamim
     */
    public List<String> getBeforeTest() {
	return this.beforeTest;
    }

    /**
     * <li><strong><i>getClassName</i></strong></li>
     * 
     * <pre>
     * public String getClassName()
     * </pre>
     * 
     * <p>
     * Return Class name that has the field with annotation of Rule or
     * ClassRule.
     * </p>
     * 
     * @return String - return class name.
     * 
     * @author Shohel Shamim
     */
    public String getClassName() {
	return this.className;
    }

    /**
     * <li><strong><i>getClaz</i></strong></li>
     * 
     * <pre>
     * public Class<?> getClaz()
     * </pre>
     * 
     * <p>
     * Return Class that has the field with annotation of Rule or ClassRule.
     * </p>
     * 
     * @return Class<?> - return class Type.
     * 
     * @author Shohel Shamim
     */
    public Class<?> getClaz() {
	return this.claz;
    }

    /**
     * <li><strong><i>getField</i></strong></li>
     * 
     * <pre>
     * public Field getField()
     * </pre>
     * 
     * <p>
     * Return field that has annotation Rule or ClassRule
     * </p>
     * 
     * @return Field - return Field Type.
     * 
     * @author Shohel Shamim
     */
    public Field getField() {
	return this.field;
    }

    /**
     * <li><strong><i>getFieldAnnotaiton</i></strong></li>
     * 
     * <pre>
     * public Annotation[] getFieldAnnotaiton()
     * </pre>
     * 
     * <p>
     * Return list of annotation of current field.
     * </p>
     * 
     * @return Annotation[] - return Array Type of Annotation.
     * 
     * @author Shohel Shamim
     */
    public Annotation[] getFieldAnnotaiton() {
	return this.fieldAnnotaiton;
    }

    /**
     * <li><strong><i>getFieldClass</i></strong></li>
     * 
     * <pre>
     * public Class<?> getFieldClass()
     * </pre>
     * 
     * <p>
     * Retrun Field's Type as Class. Field can be String class or other class
     * Type.
     * </p>
     * 
     * @return Class<?> - return Class Type.
     * 
     * @author Shohel Shamim
     */
    public Class<?> getFieldClass() {
	return this.fieldClass;
    }

    /**
     * <li><strong><i>getFieldClassName</i></strong></li>
     * 
     * <pre>
     * public String getFieldClassName()
     * </pre>
     * 
     * <p>
     * Return Field's class name as String.
     * </p>
     * 
     * @return String - String formated field class name.
     * 
     * @author Shohel Shamim
     */
    public String getFieldClassName() {
	return this.fieldClassName;
    }

    /**
     * <li><strong><i>getFieldName</i></strong></li>
     * 
     * <pre>
     * public String getFieldName()
     * </pre>
     * 
     * <p>
     * Return Field name as String.
     * </p>
     * 
     * @return String - String formated field name.
     * 
     * @author Shohel Shamim
     */
    public String getFieldName() {
	return this.fieldName;
    }

    /**
     * <li><strong><i>getObjectValue</i></strong></li>
     * 
     * <pre>
     * public String getObjectValue()
     * </pre>
     * 
     * <p>
     * Return generated object value. This method is used to initiate object.
     * </p>
     * 
     * @return String - return String Type.
     * 
     * @author Shohel Shamim
     */
    public String getObjectValue() {
	return this.objectValue;
    }

    /**
     * <li><strong><i>getObjectWithField</i></strong></li>
     * 
     * <pre>
     * public String getObjectWithField()
     * </pre>
     * 
     * <p>
     * Return object with object type. for ClassRule especially.
     * </p>
     * 
     * @return String - Return object with object Type.
     * 
     * @author Shohel Shamim
     */
    public String getObjectWithField() {
	return this.objectWithField;
    }

    /**
     * <li><strong><i>getObjectWithoutField</i></strong></li>
     * 
     * <pre>
     * public String getObjectWithoutField()
     * </pre>
     * 
     * <p>
     * Return object name without object Type.
     * </p>
     * 
     * @return String - Return object without object Type.
     * 
     * @author Shohel Shamim
     */
    public String getObjectWithoutField() {
	return this.object;
    }

    /**
     * <li><strong><i>hasDesctiption</i></strong></li>
     * 
     * <pre>
     * public boolean hasDesctiption()
     * </pre>
     * 
     * <p>
     * This is a especial checker. Some of the method contains Description from
     * Junit. If a method has Description then this method will set the property
     * true so that code can do required action for that.
     * </p>
     * 
     * @return boolen - return true or false.
     * 
     * @author Shohel Shamim
     */
    public boolean hasDesctiption() {
	return this.hasDesctiption;
    }

    /**
     * <li><strong><i>hasThrowable</i></strong></li>
     * 
     * <pre>
     * public boolean hasThrowable()
     * </pre>
     * 
     * <p>
     * This is a especial checker. Some of the method contains Throwable from
     * Junit. If a method has Throwable then this method will set the property
     * true so that code can do required action for that.
     * </p>
     * 
     * @return boolen - return true or false.
     * 
     * @author Shohel Shamim
     */
    public boolean hasThrowable() {
	return this.hasThrowable;
    }

    /**
     * <li><strong><i>initializeAllrequredProperties</i></strong></li>
     * 
     * <pre>
     * private void initializeAllrequredProperties()
     * </pre>
     * 
     * <p>
     * Initialize all necessary properties before invoke other methods.
     * </p>
     * 
     * 
     * @author Shohel Shamim
     */
    private void initializeAllrequredProperties() {
	this.className = this.claz.getSimpleName();
	this.fieldAnnotaiton = this.field.getAnnotations();
	this.fieldClass = this.field.getType();
	this.fieldClassName = this.fieldClass.getSimpleName();
	this.hasDesctiption = false;
	this.hasThrowable = false;
	this.beforeTest = new ArrayList<String>();
	this.afterTest = new ArrayList<String>();
	List<Method> methodList = new ArrayList<Method>();
	Method[] methods = this.field.getType().getDeclaredMethods();
	// not all method can be consider, because method can be overloaded
	for (Method meth : methods) {
	    String name = meth.getName();
	    switch (name) {
	    case VERIFY:
	    case BEFORE:
	    case AFTER:
	    case STARTING:
	    case SUCCEEDED:
	    case FAILED:
	    case FINISHED:
		methodList.add(meth);
		break;
	    }
	}
	matchClass(this.field.getType(), methodList, false);
    }

    /**
     * <li><strong><i>isStatic</i></strong></li>
     * 
     * <pre>
     * public boolean isStatic()
     * </pre>
     * 
     * <p>
     * Return true if a filed is Static. Normally Field with ClassRule
     * annotation is static.
     * </p>
     * 
     * @return boolen - return true or false.
     * 
     * @author Shohel Shamim
     */
    public boolean isStatic() {
	return this.isStatic;
    }

    // check class and mark if it is a superclass so that it can it do...

    /**
     * <li><strong><i>matchClass</i></strong></li>
     * 
     * <pre>
     * private void matchClass(Class<?> fieldClass, List<Method> methodList, boolean isSuperClass)
     * </pre>
     * 
     * <p>
     * This method is used to verify some method like
     * "verify(), after(), before(), succeeded(), finished(), failed(), starting()"
     * by comparing their super classes. It will traverse all the super classes
     * of the field type that is annotated by Rule or ClassRule.
     * </p>
     * 
     * @param fieldClass
     *            - class Type of Field.
     * 
     * @param methodList
     *            - List of methods gets from traversal.
     * 
     * @param isSuperClass
     *            - true or false if class has SuperClass.
     * 
     * @author Shohel Shamim
     */
    private void matchClass(Class<?> fieldClass, List<Method> methodList,
	    boolean isSuperClass) {
	if (fieldClass.equals(ErrorCollector.class)
		|| fieldClass.equals(Verifier.class)) {
	    // @Overrider verify(); Position: After Test
	    if (isSuperClass) {
		methodVerifier(methodList,
			ErrorCollector.class.getDeclaredMethods());
	    }
	} else if (fieldClass.equals(ExternalResource.class)
		|| fieldClass.equals(TemporaryFolder.class)) {
	    if (isSuperClass) {
		// @Overrider before(); Position: Before Test
		// @Overrider after(); Position: After Test
		methodVerifier(methodList,
			TemporaryFolder.class.getDeclaredMethods());
	    }
	} else if (fieldClass.equals(TestName.class)
		|| fieldClass.equals(TestWatcher.class)) {
	    if (isSuperClass) {
		// EX: Parameters: Description and Throwable
		// -------------------------------------------------------------
		// Description.createSuiteDescription(CLASSNAME.class);
		// new Throwable();
		// -------------------------------------------------------------
		// WARNING: PLEASE DO NOT CHANGE BELOW ORDER
		// @Overrider starting(Description description); Position:
		// Before Test
		// @Overrider succeeded(Description description); Position:
		// After Test
		// @Overrider failed(Throwable e, Description description);
		// Position: After Test
		// @Overrider finished(Description description) ; Position:
		// After Test and After succeeded and failed
		this.hasDesctiption = true;
		this.hasThrowable = true;
		methodVerifier(methodList,
			TestWatcher.class.getDeclaredMethods());
	    }
	} else {
	    Class<?> superClass = fieldClass.getSuperclass();
	    if (superClass != null) {
		// Ignore Object class
		if (superClass != java.lang.Object.class) {
		    matchClass(superClass, methodList, true);
		}
	    }
	}
    }

    /**
     * <li><strong><i>methodVerifier</i></strong></li>
     * 
     * <pre>
     * private void methodVerifier(List<Method> originalClassMehtods, Method[] superClassMethods)
     * </pre>
     * 
     * <p>
     * Verify method by comparing current Class methods with Method's of any
     * Class from org.junit.rules Rules Package.
     * </p>
     * 
     * @param originalClassMehtods
     *            - List of methods from Test Class.
     * 
     * @param superClassMethods
     *            - Methods from superclass.
     * 
     * @author Shohel Shamim
     */
    private void methodVerifier(List<Method> originalClassMehtods,
	    Method[] superClassMethods) {
	boolean hasSucceeded = false;
	boolean hasFailed = false;
	boolean hasFinished = false;
	// creating Description
	String descrtiption = "Description.createSuiteDescription("
		+ this.claz.getSimpleName() + ".class)";
	for (Method method : superClassMethods) {
	    String name = method.getName();
	    for (Method methd : originalClassMehtods) {
		if (method.getName().equalsIgnoreCase(methd.getName())) {
		    if (Arrays.deepEquals(method.getParameterTypes(),
			    (methd.getParameterTypes()))) {
			// WARNING: Any changes will be generate wrong output
			switch (name) {
			case VERIFY:
			    this.afterTest.add(this.objectWithField + VERIFY
				    + "();");
			    break;
			case BEFORE:
			    this.beforeTest.add(this.objectWithField + BEFORE
				    + "();");
			    break;
			case AFTER:
			    this.afterTest.add(this.objectWithField + AFTER
				    + "();");
			    break;
			case STARTING:
			    this.beforeTest.add(this.objectWithField + STARTING
				    + "(" + descrtiption + ");");
			    this.hasDesctiption = true;
			    break;
			case SUCCEEDED:
			    hasSucceeded = true;
			    this.hasDesctiption = true;
			    break;
			case FAILED:
			    /*
			     * failed() will not work in our case; because we
			     * always check weather the test case success or not
			     * before generate code
			     */
			    this.hasDesctiption = true;
			    hasFailed = true;
			    break;
			case FINISHED:
			    this.hasDesctiption = true;
			    hasFinished = true;
			    break;
			}
		    }
		    break;
		}
	    }
	}
	if (hasSucceeded) {
	    this.afterTest.add(this.objectWithField + SUCCEEDED + "("
		    + descrtiption + ");");
	}
	if (hasFailed) {
	    /* Required only of we consider FAILED */
	    // String throwable = "new Throwable()";
	    // this.afterTest.add(this.object + FAILED + "("
	    // + throwable + ", " + descrtiption + ");");
	    // this.hasDesctiption = true;
	    // this.hasThrowable = true;
	}
	if (hasFinished) {
	    this.afterTest.add(this.objectWithField + FINISHED + "("
		    + descrtiption + ");");
	}
    }
}