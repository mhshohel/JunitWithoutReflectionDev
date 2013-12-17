/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName GenerateCode.java
 * 
 * @FileCreated Jan 03, 2013
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
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.Test.None;
import org.junit.experimental.categories.Category;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.runner.Description;

/**
 * <li><strong>GenerateCode</strong></li>
 * 
 * <pre>
 * public class GenerateCode <blockquote>implements</blockquote> AnnotaionList
 * </pre>
 * <p>
 * This class is responsible for generating output code from Junit.
 * </p>
 * <br/>
 * 
 * @author Shohel Shamim
 */
public class GenerateCode implements AnnotaionList {
    /* classList: Contains all classes */
    private List<Class<?>> classList = new ArrayList<Class<?>>();
    /* classRecords: has all essential properties of class */
    private ClassRecords classRecords;
    private long codeLength = 1;
    /*
     * codeSnap: Contains output code; all code that is required as output
     * inside main class; Ex: Methods
     */
    private List<String> codeSnap = new ArrayList<String>();
    /* for static field counter */
    public int counter = 0;
    /* Keep fields of entry class only, it means only instance of that class */
    private List<FieldsProperty> entryClassFields = new ArrayList<FieldsProperty>();
    /* Used to let generator output know */
    private boolean hasParameters = false;
    /*
     * True is there is any Parameters annotation in a class for output class
     */
    private boolean hasParametersForOutputClass = false;
    /* True is there is any ParameterSuppliedBy annotation in a class */
    private boolean hasParameterSuppliedBy = false;
    /*
     * True is there is any ParameterSuppliedBy annotation in a class for output
     * class
     */
    private boolean hasParameterSuppliedByForOutputClass = false;
    /* true to sort method list otherwise unsorted */
    private final boolean methodShouldSort;
    /*
     * Keep object name as key and value as new instance; this is used to keep
     * other fields information
     */
    private List<FieldsProperty> otherFields = new ArrayList<FieldsProperty>();
    /* Keep output class name withour extension */
    private final String outputClassName;
    /* output code directory */
    private final String outputDirectory;
    /*
     * packages: Contains all required packages; used in output code Ex:
     * tools.code.gen
     */
    private HashMap<String, String> packages = new HashMap<String, String>();
    /*
     * Class name, can be change. This is used to support ParameterSuppliedBy
     * class
     */
    private final String paramSuppClassName = "PS";
    /*
     * Method name, can be change. This is used to support ParameterSuppliedBy
     * class
     */
    private final String paramSuppMethodName = "pa";
    /* static Rule variables; placed in output class */
    private List<String> staticRuleFields = new ArrayList<String>();

    /**
     * <li><strong><i>GenerateCode</i></strong></li>
     * 
     * <pre>
     * public GenerateCode(ClassRecords classRecords, String outputClassName,
     * 	    boolean methodShouldSort, String directory)
     * </pre>
     * 
     * <p>
     * Generates output code by using ClassRecords object and save them to the
     * file named that is passed by parameter outputClassName.
     * </p>
     * 
     * @author Shohel Shamim
     */
    public GenerateCode(ClassRecords classRecords, String outputClassName,
	    boolean methodShouldSort, String directory) {
	this.classRecords = classRecords;
	this.outputClassName = outputClassName;
	this.methodShouldSort = methodShouldSort;
	this.outputDirectory = directory;
	// Lookup class for output
	traverseClasses(this.classRecords);
    }

    /**
     * <li><strong><i>addClassToList</i></strong></li>
     * 
     * <pre>
     * private void addClassToList(Class<?> clas)
     * </pre>
     * 
     * <p>
     * Add all unique classes that is required to generate output
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * 
     * @author Shohel Shamim
     */
    private void addClassToList(Class<?> clas) {
	if (!this.classList.contains(clas)) {
	    this.classList.add(clas);
	}
    }

    /**
     * <li><strong><i>addCode</i></strong></li>
     * 
     * <pre>
     * private void addCode(String code)
     * </pre>
     * 
     * <p>
     * Add actual code for output file.
     * </p>
     * 
     * @param code
     *            - String formated generated code.
     * 
     * @author Shohel Shamim
     */
    private void addCode(String code) {
	this.codeSnap.add(code);
	this.codeLength += code.length();
    }

    /**
     * <li><strong><i>addEntryClassFields</i></strong></li>
     * 
     * <pre>
     * private void addEntryClassFields(FieldsProperty fieldsProperty)
     * </pre>
     * 
     * <p>
     * Add fields from entry class.
     * </p>
     * 
     * @param fieldsProperty
     *            - FieldsProperty Type of object.
     * 
     * @author Shohel Shamim
     */
    private void addEntryClassFields(FieldsProperty fieldsProperty) {
	boolean hasSameObject = false;
	for (FieldsProperty fp : this.entryClassFields) {
	    if (fp.equals(fieldsProperty)) {
		hasSameObject = true;
		break;
	    }
	}
	// no duplicate object allowed
	if (!hasSameObject) {
	    this.entryClassFields.add(fieldsProperty);
	}
    }

    /**
     * <li><strong><i>addOtherFields</i></strong></li>
     * 
     * <pre>
     * private void addOtherFields(FieldsProperty fieldsProperty)
     * </pre>
     * 
     * <p>
     * Add other classes field.
     * </p>
     * 
     * @param fieldsProperty
     *            - a FieldsProperty type object.
     * 
     * @author Shohel Shamim
     */
    private void addOtherFields(FieldsProperty fieldsProperty) {
	boolean hasSameObject = false;
	for (FieldsProperty fp : this.otherFields) {
	    if (fp.equals(fieldsProperty)) {
		hasSameObject = true;
		break;
	    }
	}
	// no duplicate object allowed
	if (!hasSameObject) {
	    this.otherFields.add(fieldsProperty);
	}
    }

    /**
     * <li><strong><i>addPackage</i></strong></li>
     * 
     * <pre>
     * private void addPackage(String key, String value)
     * </pre>
     * 
     * <p>
     * Add all unique packages as String; Each package is the package of output
     * class; Ex: "tools.code.gen" as String; Key: className
     * </p>
     * 
     * @param key
     *            - class name as Key
     * @param value
     *            - package as String.
     * 
     * @author Shohel Shamim
     */
    private void addPackage(String key, String value) {
	if (!this.packages.containsKey(key)) {
	    this.packages.put(key, value);
	}
    }

    /**
     * <li><strong><i>addStaticFieldsForRules</i></strong></li>
     * 
     * <pre>
     * public void addStaticFieldsForRules(String field)
     * </pre>
     * 
     * <p>
     * Ass Static fields, if a field is annotated by Rule but that field is
     * static then that will be store by this method. Because a static field
     * will be act like RuleClass annotation.
     * </p>
     * 
     * @param field
     *            - field name.
     * 
     * @author Shohel Shamim
     */
    public void addStaticFieldsForRules(String field) {
	this.staticRuleFields.add(field);
    }

    /**
     * <li><strong><i>addTestFullTestCode</i></strong></li>
     * 
     * <pre>
     * private void addTestFullTestCode(OutputCodeProperties outputCodeProperties)
     * </pre>
     * 
     * <p>
     * Add Test code with Before and After method.
     * </p>
     * 
     * @param outputCodeProperties
     *            - a OutputCodeProperties type object.
     * 
     * @author Shohel Shamim
     */
    private void addTestFullTestCode(OutputCodeProperties outputCodeProperties) {
	for (TestProperties testProperties : outputCodeProperties
		.getTestMethods()) {
	    writeBefore(outputCodeProperties);
	    if (testProperties.getExpected().equalsIgnoreCase("")) {
		addCode(testProperties.getFullMethodName());
	    } else {
		addCode("try{");
		addCode("    " + testProperties.getFullMethodName());
		addCode("}catch(" + testProperties.getExpected() + " e){}");
	    }
	    writeAfter(outputCodeProperties);
	}
    }

    /**
     * <li><strong><i>getAllFieldsWithDataType</i></strong></li>
     * 
     * <pre>
     * public Collection<String> getAllFieldsWithDataType()
     * </pre>
     * 
     * <p>
     * Return all fields with data type; example: String a;
     * </p>
     * 
     * @return Collection<String> - return fields name.
     * 
     * @author Shohel Shamim
     */
    public Collection<String> getAllFieldsWithDataType() {
	String attribute = "";
	List<String> fields = new ArrayList<String>();
	// get all fields from entryClassFields
	for (FieldsProperty field : this.entryClassFields) {
	    attribute = field.getDataType() + " " + field.getObjectName();
	    fields.add(attribute);
	}
	// get all fields from otherFields
	for (FieldsProperty field : this.otherFields) {
	    attribute = field.getDataType() + " " + field.getObjectName();
	    fields.add(field.getFieldWithValues());
	}
	return fields;
    }

    /**
     * <li><strong><i>getCodeLength</i></strong></li>
     * 
     * <pre>
     * public long getCodeLength()
     * </pre>
     * 
     * <p>
     * Return the length of code; used to make different class file by Comparing
     * the limit.
     * </p>
     * 
     * @return long - length of code.
     * 
     * @author Shohel Shamim
     */
    public long getCodeLength() {
	return this.codeLength;
    }

    /**
     * <li><strong><i>getCodes</i></strong></li>
     * 
     * <pre>
     * public List<String> getCodes()
     * </pre>
     * 
     * <p>
     * Return output code that will be inside main class; it contains Fields and
     * Methods; Example: "obj.methodName();" as String
     * </p>
     * 
     * @return List<String> - return code.
     * 
     * @author Shohel Shamim
     */
    public List<String> getCodes() {
	return this.codeSnap;
    }

    /**
     * <li><strong><i>getDataPointContainerObject</i></strong></li>
     * 
     * <pre>
     * private DataPointProperties getDataPointContainerObject(
     * 	    List<DataPointProperties> dataPointPropertiesList, String dataType)
     * </pre>
     * 
     * <p>
     * Return DataPointProperties objects, if object is already not in list it
     * will return new object
     * </p>
     * 
     * @param dataPointPropertiesList
     *            - list of dataPointPropertiesList.
     * @param dataType
     *            - variable data type as String format.
     * @return DataPointProperties - return object of DataPointProperties.
     * @author Shohel Shamim
     */
    private DataPointProperties getDataPointContainerObject(
	    List<DataPointProperties> dataPointPropertiesList, String dataType) {
	if (dataPointPropertiesList.isEmpty()) {
	    return new DataPointProperties();
	} else {
	    DataPointProperties dpp = new DataPointProperties();
	    // return object that previously added otherwise return new object.
	    for (DataPointProperties dp : dataPointPropertiesList) {
		if (dp.equals(dataType)) {
		    dpp = dp;
		    break;
		}
	    }
	    return dpp;
	}
    }

    /**
     * <li><strong><i>getEntryClassFields</i></strong></li>
     * 
     * <pre>
     * public Collection<String> getEntryClassFields()
     * </pre>
     * 
     * <p>
     * Return list of fields from entry class.
     * </p>
     * 
     * @return Collection<String> - return fields.
     * 
     * @author Shohel Shamim
     */
    public Collection<String> getEntryClassFields() {
	List<String> fields = new ArrayList<String>();
	for (FieldsProperty field : this.entryClassFields) {
	    fields.add(field.getFieldWithValues());
	}
	return fields;
    }

    /**
     * <li><strong><i>getEntryClassFields</i></strong></li>
     * 
     * <pre>
     * public FieldsProperty getEntryClassFields(Class<?> clz)
     * </pre>
     * 
     * <p>
     * Return list of entry class fields by comparing class. If field does not
     * match then it will return null. The method that calls this method must
     * handle null.
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * @return FieldsProperty - return object of FieldsProperty
     * @author Shohel Shamim
     */
    public FieldsProperty getEntryClassFields(Class<?> clz) {
	for (FieldsProperty field : this.entryClassFields) {
	    if (field.getEntryClass().equals(clz)) {
		return field;
	    }
	}
	return null;
    }

    /**
     * <li><strong><i>getMethodParameter</i></strong></li>
     * 
     * <pre>
     * public String getMethodParameter(boolean witoutType)
     * </pre>
     * 
     * <p>
     * Return method's parameter with its data type for output code generator;
     * example: withoutType false: method(String a, String b); withoutType true:
     * method( a , b );
     * </p>
     * 
     * @param String
     *            - method parameters as String
     * 
     * @author Shohel Shamim
     */
    public String getMethodParameter(boolean witoutType) {
	String parameters = "";
	String attribute = "";
	for (FieldsProperty field : this.entryClassFields) {
	    if (witoutType) {
		parameters += " " + field.getObjectName() + ",";
	    } else {
		attribute = " " + field.getDataType() + " "
			+ field.getObjectName();
		parameters += attribute + ",";
	    }
	}
	for (FieldsProperty field : this.otherFields) {
	    if (witoutType) {
		parameters += " " + field.getObjectName() + ",";
	    } else {
		attribute = " " + field.getDataType() + " "
			+ field.getObjectName();
		parameters += attribute + ",";
	    }
	}
	parameters = parameters.trim();
	// remove last ","
	if (parameters != "") {
	    parameters = parameters.substring(0, parameters.length() - 1);
	}
	return parameters;
    }

    /**
     * <li><strong><i>getOtherFields</i></strong></li>
     * 
     * <pre>
     * public Collection<String> getOtherFields()
     * </pre>
     * 
     * <p>
     * Return list of extra fields.
     * </p>
     * 
     * @return Collection<String> - return fields of other classes.
     * 
     * @author Shohel Shamim
     */
    public Collection<String> getOtherFields() {
	List<String> fields = new ArrayList<String>();
	for (FieldsProperty field : this.otherFields) {
	    fields.add(field.getFieldWithValues());
	}
	return fields;
    }

    /**
     * <li><strong><i>getOutputDirectoryAsSring</i></strong></li>
     * 
     * <pre>
     * public String getOutputDirectoryAsSring()
     * </pre>
     * 
     * <p>
     * Return Output Directory.
     * </p>
     * 
     * @return String - return output package folder.
     * 
     * @author Shohel Shamim
     */
    public String getOutputDirectoryAsSring() {
	return this.outputDirectory;
    }

    /**
     * <li><strong><i>getPackageName</i></strong></li>
     * 
     * <pre>
     * private String getPackageName(Class<?> clz)
     * </pre>
     * 
     * <p>
     * Return package name with "." sign, Example: "tool.code.gen".
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * @return String - return package name.
     * @author Shohel Shamim
     */
    private String getPackageName(Class<?> clz) {
	String pack = "";
	try {
	    pack = clz.getPackage().getName();
	    if (pack.equalsIgnoreCase("java.lang")) {
		pack = "";
	    }
	} catch (Exception e) {
	    pack = "";
	}
	return pack;
    }

    /**
     * <li><strong><i>getPackages</i></strong></li>
     * 
     * <pre>
     * public Collection<String> getPackages()
     * </pre>
     * 
     * <p>
     * Return list of all required packages that is required for output;
     * Example: "tools.code.gen" as String
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * 
     * @author Shohel Shamim
     */
    public Collection<String> getPackages() {
	return this.packages.values();
    }

    /**
     * <li><strong><i>getParameterForParameterized</i></strong></li>
     * 
     * <pre>
     * private String getParameterForParameterized(ClassRecords classRecord,
     * 	    Class<?> clas, int pos)
     * </pre>
     * 
     * <p>
     * Return generated Parameters for Method that has annotation Parameterized
     * </p>
     * 
     * @param classRecord
     *            - object of ClassRecord.
     * @param clz
     *            - a class name with .class extension or Class Type
     * @param pos
     *            - int value
     * @return String - parameters.
     * @author Shohel Shamim
     */
    private String getParameterForParameterized(ClassRecords classRecord,
	    Class<?> clas, int pos) {
	String parameters = "";
	for (int i = 0; i < classRecord.getConstructorsParametsTypeClass().length; i++) {
	    String pack = "";
	    Class<?> consType = classRecord.getConstructorsParametsTypeClass()[i];
	    pack = getPackageName(consType);
	    if (pack != "") {
		addPackage(consType.getSimpleName(),
			pack + "." + consType.getSimpleName());
	    }
	    parameters += "("
		    + classRecord.getConstructorsParametsTypeClass()[i]
			    .getSimpleName() + ") " + this.paramSuppClassName
		    + "." + this.paramSuppMethodName + "("
		    + clas.getSimpleName() + ".data()" + ", " + pos + ", " + i
		    + ")" + ", ";
	    // 0 is default value to make it easy and runnable
	}
	// removing last space and ","
	if (parameters != "") {
	    parameters = parameters.substring(0, parameters.length() - 2)
		    .trim();
	}
	return parameters;
    }

    /**
     * <li><strong><i>getParamSuppClassName</i></strong></li>
     * 
     * <pre>
     * public String getParamSuppClassName()
     * </pre>
     * 
     * <p>
     * Return class name to create for Parameter Supplied By
     * </p>
     * 
     * @return String - class name.
     * 
     * @author Shohel Shamim
     */
    public String getParamSuppClassName() {
	return this.paramSuppClassName;
    }

    /**
     * <li><strong><i>getParamSuppMethodName</i></strong></li>
     * 
     * <pre>
     * public String getParamSuppMethodName()
     * </pre>
     * 
     * <p>
     * Return method name to create method for class that has annotation
     * ParameterSuppliedBy
     * </p>
     * 
     * @return String - return method name.
     * 
     * @author Shohel Shamim
     */
    public String getParamSuppMethodName() {
	return this.paramSuppMethodName;
    }

    /**
     * <li><strong><i>getStaticFieldsForRules</i></strong></li>
     * 
     * <pre>
     * public List<String> getStaticFieldsForRules()
     * </pre>
     * 
     * <p>
     * Return Static fields.
     * </p>
     * 
     * @return List<String> - field name.
     * 
     * @author Shohel Shamim
     */
    public List<String> getStaticFieldsForRules() {
	return this.staticRuleFields;
    }

    /**
     * <li><strong><i>hasEntryClassFields</i></strong></li>
     * 
     * <pre>
     * private boolean hasEntryClassFields(Class<?> clas, String objectName)
     * </pre>
     * 
     * <p>
     * Return true if key is in the list otherwise false; Key: fieldName.
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * @param objectname
     *            - object name of class.
     * @return boolean - true or false.
     * @author Shohel Shamim
     */
    private boolean hasEntryClassFields(Class<?> clas, String objectName) {
	for (FieldsProperty fp : this.entryClassFields) {
	    if (fp.getObjectName().equalsIgnoreCase(objectName)) {
		if (fp.getEntryClass().equals(clas)) {
		    return false;
		} else {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * <li><strong><i>hasOtherFields</i></strong></li>
     * 
     * <pre>
     * private boolean hasOtherFields(Class<?> clas, String objectName)
     * </pre>
     * 
     * <p>
     * Return true if key is in the list otherwise false
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * @param objectname
     *            - object name of class.
     * @return boolean - true or false.
     * @author Shohel Shamim
     */
    private boolean hasOtherFields(Class<?> clas, String objectName) {
	for (FieldsProperty fp : this.entryClassFields) {
	    if (fp.getObjectName().equalsIgnoreCase(objectName)) {
		if (fp.getEntryClass().equals(clas)) {
		    return false;
		} else {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * <li><strong><i>hasParameters</i></strong></li>
     * 
     * <pre>
     * public boolean hasParameters()
     * </pre>
     * 
     * <p>
     * Return true of class has Parameterized annotation.
     * </p>
     * 
     * @return boolean - true or false.
     * @author Shohel Shamim
     */
    public boolean hasParameters() {
	return this.hasParameters;
    }

    /**
     * <li><strong><i>hasParametersForOutputClass</i></strong></li>
     * 
     * <pre>
     * public boolean hasParametersForOutputClass()
     * </pre>
     * 
     * <p>
     * Return true if any class has annotation Parameters.
     * </p>
     * 
     * @return boolean - true or false.
     * 
     * @author Shohel Shamim
     */
    public boolean hasParametersForOutputClass() {
	return this.hasParametersForOutputClass;
    }

    /**
     * <li><strong><i>hasParameterSuppliedBy</i></strong></li>
     * 
     * <pre>
     * public boolean hasParameterSuppliedBy()
     * </pre>
     * 
     * <p>
     * Return true if test class has annotation ParameterSuppliedBy.
     * </p>
     * 
     * @return boolean - true or false.
     * 
     * @author Shohel Shamim
     */
    public boolean hasParameterSuppliedBy() {
	return this.hasParameterSuppliedBy;
    }

    /**
     * <li><strong><i>hasParameterSuppliedByForOutputClass</i></strong></li>
     * 
     * <pre>
     * public boolean hasParameterSuppliedByForOutputClass()
     * </pre>
     * 
     * <p>
     * Return true if any class has annotation ParameterSuppliedBy.
     * </p>
     * 
     * @return boolean - true or false.
     * 
     * @author Shohel Shamim
     */
    public boolean hasParameterSuppliedByForOutputClass() {
	return this.hasParameterSuppliedByForOutputClass;
    }

    /**
     * <li><strong><i>hasRequiredCategories</i></strong></li>
     * 
     * <pre>
     * private boolean hasRequiredCategories(Class<?>[] catContains,
     * 	    List<Class<?>> listofCategories)
     * </pre>
     * 
     * <p>
     * Return true if category class match; catContains is array type of
     * category classes; and listofCategories means by looking up in
     * ClassRecords for Include or Exclude categoriy list.
     * </p>
     * 
     * @param catContains
     *            - Array type classes
     * @param List
     *            <Class<?>> - list- of category classes
     * @return boolean - true or false.
     * @author Shohel Shamim
     */
    private boolean hasRequiredCategories(Class<?>[] catContains,
	    List<Class<?>> listofCategories) {
	if (listofCategories.isEmpty() || catContains == null) {
	    return false;
	}
	List<Class<?>> categories = Arrays.asList(catContains);
	for (Class<?> clz : listofCategories) {
	    if (!(categories.contains(clz))) {
		return false;
	    }
	}
	return true;
    }

    /**
     * <li><strong><i>initializeFieldsofRules</i></strong></li>
     * 
     * <pre>
     * private void initializeFieldsofRules(
     * 	    OutputCodeProperties outputCodeProperties)
     * </pre>
     * 
     * <p>
     * Initialize field of Rules annotation.
     * </p>
     * 
     * @param outputCodeProperties
     *            - object of OutputCodeProperties
     * 
     * @author Shohel Shamim
     */
    private void initializeFieldsofRules(
	    OutputCodeProperties outputCodeProperties) {
	// initialize field
	String initalize = "";
	for (RuleAndClassRuleProperties ru : outputCodeProperties.getRules()) {
	    if (!ru.isStatic()) {
		initalize = ru.getObjectWithoutField() + " = "
			+ ru.getObjectValue() + ";";
		addCode(initalize);
	    }
	}
    }

    /**
     * <li><strong><i>initializePropertiesForOutputCode</i></strong></li>
     * 
     * <pre>
     * private void initializePropertiesForOutputCode(Class<?> clas,
     * 	    ClassRecords classRecord, OutputCodeProperties outputCodeProperties, 
     * 	    boolean hasParameterized)
     * </pre>
     * 
     * <p>
     * Initialize some properties then again traverse class with those
     * properties; Get object name, add package;
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * @param classRecord
     *            - ClassRecords object.
     * @param outputCodeProperties
     *            - OutputCodeProperties object.
     * @param hasParameterized
     *            - true or false.
     * @author Shohel Shamim
     */
    private void initializePropertiesForOutputCode(Class<?> clas,
	    ClassRecords classRecord,
	    OutputCodeProperties outputCodeProperties, boolean hasParameterized) {
	// Get class name to get object name;
	String className = clas.getSimpleName();
	// Java conventional object
	String objectName = toCamelCase(className);
	// Java conventional object with "." sign
	String object = objectName + ".";
	// Add class that is entry point of test class
	addClassToList(clas);
	addPackage(clas.getSimpleName(), clas.getPackage().getName() + "."
		+ clas.getSimpleName());
	outputCodeProperties.setClasz(clas);
	outputCodeProperties.setClassName(className);
	outputCodeProperties.setObjectName(objectName);
	outputCodeProperties.setObject(object);
	// dataPointPropertiesList: DataPoint method list; it contains all
	// generated method and actual method those are annotated by @DataPoint
	// or @DataPoints; This list pass to traverseClasses so that it can
	// avoid
	// duplication;
	List<DataPointProperties> dataPointPropertiesList = new ArrayList<DataPointProperties>();
	// Traverse class with new properties; Pass outputCodeProperties as
	// parameter so that it can store methods for output code and
	// dataPointPropertiesList to store unique generated methods from
	// @DataPoint or @DataPoints annotated methods dataPointsLookedUp: =
	// false; it means fields from super classes that
	// has @DataPoint or @DataPoints annotation not checked yet
	traverseClasses(clas, classRecord, false, outputCodeProperties,
		dataPointPropertiesList, hasParameterized);
    }

    /**
     * <li><strong><i>lookUpClassRuleOrRuleFields</i></strong></li>
     * 
     * <pre>
     * private void lookUpClassRuleOrRuleFields(Class<?> clas,
     * 	    ClassRecords classRecord, OutputCodeProperties outputCodeProperties)
     * </pre>
     * 
     * <p>
     * This method is used to look up filed those are annotated by Rule or
     * ClassRule. It will set required properties.
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * @param classRecord
     *            - ClassRecords object.
     * @param outputCodeProperties
     *            - OutputCodeProperties object.
     * 
     * @author Shohel Shamim
     */
    private void lookUpClassRuleOrRuleFields(Class<?> clas,
	    ClassRecords classRecord, OutputCodeProperties outputCodeProperties) {
	Field[] fields = clas.getDeclaredFields();
	RuleAndClassRuleProperties racrp = null;
	for (Field field : fields) {
	    Annotation[] annottions = field.getAnnotations();
	    for (Annotation annotation : annottions) {
		String annotationName = annotation.annotationType()
			.getSimpleName();
		// Add package of field class
		if (annotationName.equalsIgnoreCase(ClassRule)) {
		    addPackage(clas.getSimpleName(), clas.getPackage()
			    .getName() + "." + clas.getSimpleName());
		    racrp = outputCodeProperties.addClassRule(clas, field);
		} else if (annotationName.equalsIgnoreCase(Rule)) {
		    addPackage(clas.getSimpleName(), clas.getPackage()
			    .getName() + "." + clas.getSimpleName());
		    racrp = outputCodeProperties.addRule(this,
			    this.outputClassName, clas, field,
			    getEntryClassFields(classRecord.getEntryClass()),
			    toCamelCase(clas.getSimpleName()));
		}
		if (racrp != null && racrp.hasDesctiption()) {
		    addPackage(Description.class.getSimpleName(),
			    Description.class.getPackage().getName() + "."
				    + Description.class.getSimpleName());
		}
		if (racrp != null && racrp.hasThrowable()) {
		    addPackage(Throwable.class.getSimpleName(),
			    Throwable.class.getPackage().getName() + "."
				    + Throwable.class.getSimpleName());
		}
	    }
	}
    }

    /**
     * <li><strong><i>lookUpDataPoints</i></strong></li>
     * 
     * <pre>
     * private void lookUpDataPoints(Class<?> clas,
     * 	    List<DataPointProperties> dataPointPropertiesList)
     * </pre>
     * 
     * <p>
     * Collect list of data's from DataPoint or DataPoints fields; Traverse
     * super classes to get all values and their type
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * @param dataPointPropertiesList
     *            - list of DataPointProperties object.
     * @author Shohel Shamim
     */
    private void lookUpDataPoints(Class<?> clas,
	    List<DataPointProperties> dataPointPropertiesList) {
	Field[] fields = clas.getDeclaredFields();
	DataPointProperties dpc = null;
	for (Field field : fields) {
	    for (Annotation annotation : field.getAnnotations()) {
		try {
		    if (annotation.annotationType().getSimpleName()
			    .equalsIgnoreCase(DataPoint)
			    || annotation.annotationType().getSimpleName()
				    .equalsIgnoreCase(DataPoints)) {
			String dataType = field.getType().getSimpleName();
			// consider array as example: String as String, String[]
			// as String, String[][] as String[]; to save as data
			// type that is required to merge values of @DataPoint
			// or @DataPoints
			int index = dataType.lastIndexOf(']');
			if (index != -1) {
			    dataType = dataType.substring(0, index - 1);
			}
			String data = "";
			// Get object whether it is new or previously created
			dpc = getDataPointContainerObject(
				dataPointPropertiesList, dataType);
			dpc.setDataType(dataType);
			addPackage(clas.getSimpleName(), clas.getPackage()
				.getName() + "." + clas.getSimpleName());
			// read values from field if it is a array type
			if (field.getType().isArray()) {
			    int length = Array.getLength(field.get(field
				    .getName()));
			    for (int i = 0; i < length; i++) {
				data = clas.getSimpleName() + "."
					+ field.getName() + "[" + i + "]";
				dpc.addValues(data);
			    }
			} else {
			    data = clas.getSimpleName() + "." + field.getName();
			    dpc.addValues(data);
			}
			// check for duplicate object
			if (!dataPointPropertiesList.contains(dpc)) {
			    dataPointPropertiesList.add(dpc);
			}
		    }
		} catch (IllegalArgumentException e) {
		    e.printStackTrace();
		} catch (IllegalAccessException e) {
		    e.printStackTrace();
		}
	    }
	}
	// Visit super classes to get values from fields those are
	// annotated by @DataPoint or @DataPoints
	Class<?> superClass = clas.getSuperclass();
	if (superClass != null) {
	    // Ignore Object class
	    if (superClass != java.lang.Object.class) {
		lookUpDataPoints(superClass, dataPointPropertiesList);
	    }
	}
    }

    /**
     * <li><strong><i>lookUpParameterSuppliedBy</i></strong></li>
     * 
     * <pre>
     * private void lookUpParameterSuppliedBy(List<TheoriesData> datas,
     * 	    Annotation annots, ClassRecords classRecord,
     * 	    TheoriesData theoriesData, String dataType,
     * 	    Class<?> actualAnnotationClass)
     * </pre>
     * 
     * <p>
     * Traverse all methods those are annotated by ParameterSuppledBy and custom
     * annotation those interface has ParameterSuppliedBy annotation.
     * </p>
     * 
     * @param datas
     *            - list of TheoriesData object.
     * @param annots
     *            - a Annotation
     * @param classRecord
     *            - ClassRecords object.
     * @param theoriesData
     *            - current TheoriesData object.
     * @param dataType
     *            - a Data Type as String.
     * @param actualAnnotationClass
     *            - a class name with .class extension or Class Type
     * 
     * @author Shohel Shamim
     */
    private void lookUpParameterSuppliedBy(List<TheoriesData> datas,
	    Annotation annots, ClassRecords classRecord,
	    TheoriesData theoriesData, String dataType,
	    Class<?> actualAnnotationClass) {
	List<Object> collections = new ArrayList<Object>();
	Class<?> tempClass = ParameterSignature.class;
	// Required to pass as parameter to
	// getValueSources()
	ParameterSignature sig = null;
	// Required to read value from
	// getValueSources()
	addPackage(tempClass.getSimpleName(), tempClass.getPackage().getName()
		+ ".ParameterSignature");
	tempClass = PotentialAssignment.class;
	addPackage(tempClass.getSimpleName(), tempClass.getPackage().getName()
		+ ".PotentialAssignment");
	FieldsProperty fp = new FieldsProperty(ParameterSignature.class,
		ParameterSignature.class, "sig", false, "E",
		this.entryClassFields.size(), "");
	fp.setFieldAssignValue("null");
	addOtherFields(fp);
	ParametersSuppliedBy paramSuppBy = (ParametersSuppliedBy) annots;
	Class<?> paramSuppByClass = paramSuppBy.value();
	String obj = toCamelCase(paramSuppByClass.getSimpleName());
	String declaringClassName = "";
	if (paramSuppByClass.getDeclaringClass() != null) {
	    declaringClassName = paramSuppByClass.getDeclaringClass()
		    .getSimpleName() + ".";
	}
	// Add package to package list
	addPackage(paramSuppByClass.getSimpleName(), paramSuppByClass
		.getPackage().getName()
		+ "."
		+ declaringClassName
		+ paramSuppByClass.getSimpleName());
	boolean result = hasOtherFields(paramSuppByClass, obj);
	fp = new FieldsProperty(classRecord.getEntryClass(), paramSuppByClass,
		obj, result, "O", this.otherFields.size(), "()");
	fp.setFieldAssignValue("new " + fp.getDataType());
	obj = fp.getObjectName();
	addOtherFields(fp);
	try {
	    Method metd = paramSuppByClass.getDeclaredMethod("getValueSources",
		    ParameterSignature.class);
	    try {
		metd.invoke(paramSuppByClass.newInstance(), sig);
	    } catch (Exception e) {
		// only used if there is no way to resolve; if Annotated class
		// has some field or description then signature should not be
		// null; must have to initialize
		@SuppressWarnings("unchecked")
		Constructor<ParameterSignature> constructor = (Constructor<ParameterSignature>) ParameterSignature.class
			.getDeclaredConstructors()[0];
		constructor.setAccessible(true);
		sig = constructor.newInstance(actualAnnotationClass,
			actualAnnotationClass.getAnnotations());
	    }
	    if (metd.invoke(paramSuppByClass.newInstance(), sig) instanceof Collection) {
		Collection<?> data = (Collection<?>) metd.invoke(
			paramSuppByClass.newInstance(), sig);
		for (int i = 0; i < data.size(); i++) {
		    String param = "(" + dataType + ")"
			    + this.paramSuppClassName + "."
			    + this.paramSuppMethodName + "(" + obj
			    + ".getValueSources(sig), " + i + ")";
		    collections.add(param);
		}
		theoriesData = new TheoriesData(dataType, ParametersSuppliedBy);
		theoriesData.setDatas(collections.toArray());
		datas.add(theoriesData);
	    }
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	} catch (InvocationTargetException e) {
	    e.printStackTrace();
	} catch (InstantiationException e) {
	    e.printStackTrace();
	} catch (NoSuchMethodException e) {
	    e.printStackTrace();
	} catch (SecurityException e) {
	    e.printStackTrace();
	}
    }

    /**
     * <li><strong><i>setHasParametersForOutputClass</i></strong></li>
     * 
     * <pre>
     * private void setHasParametersForOutputClass(
     * 	    boolean hasParametersForOutputClass)
     * </pre>
     * 
     * <p>
     * Set true or false to hasParametersForOutputClass;
     * </p>
     * 
     * @param hasParametersForOutputClass
     *            - true or false.
     * 
     * @author Shohel Shamim
     */
    private void setHasParametersForOutputClass(
	    boolean hasParametersForOutputClass) {
	this.hasParametersForOutputClass = hasParametersForOutputClass;
    }

    /**
     * <li><strong><i>setHasParameterSuppliedByForOutputClass</i></strong></li>
     * 
     * <pre>
     * private void setHasParameterSuppliedByForOutputClass(
     * 	    boolean hasParameterSuppliedByForOutputClass)
     * </pre>
     * 
     * <p>
     * Set true or false to hasParameterSuppliedByForOutputClass;
     * </p>
     * 
     * @param hasParameterSuppliedByForOutputClass
     *            - true or false.
     * 
     * @author Shohel Shamim
     */
    private void setHasParameterSuppliedByForOutputClass(
	    boolean hasParameterSuppliedByForOutputClass) {
	this.hasParameterSuppliedByForOutputClass = hasParameterSuppliedByForOutputClass;
    }

    /**
     * <li><strong><i>theoryMethodGenerator</i></strong></li>
     * 
     * <pre>
     * private String theoryMethodGenerator(List<TheoriesData> datas, int current,
     * 	    int max, String output, OutputCodeProperties outputCodeProperties,
     * 	    ClassRecords classRecord, Method method, String object,
     * 	    String methodName)
     * </pre>
     * 
     * <p>
     * ParameterSuppliedBy: It works same as @TestedOn only difference it reads
     * value from class that has parent class ParameterSupplier. See below
     * description for annotation TestedOn.
     * 
     * Generating method using values from fields those are annotated by
     * DataPoint or DataPoints. Parameters Description: method is actual method
     * object, orderedParameter is a array type of DataPointProperties which has
     * actual data type and values, index number for creating recursion,
     * paramObject is a array that contains output values in each loop,
     * outputCodeProperties has method to add method of annotation DataPoint or
     * annotation DataPoints, object is String type instance name of class that
     * will give the access to the class (obj.metheodName() here obj. is the
     * object), mehtodName is method's simple name; Rules of generating methods:
     * For example a method has 4 parameters String, int, String, int; now, in
     * annotation DataPoint or DataPoints annotated fields if we get 3 values of
     * String type and 2 values of int type. (Be careful we have to follow the
     * parameters order of method to get actual output) so, loop will be 3 times
     * for String then 2 times for int, then again 3 times for String and 2
     * times for int, in actual parameters order. it is because of we have 4
     * parameters of 2 types, so, each type will create loop according to it's
     * value's length. Total method will be 3 * 2 * 3 * 2 = 36 methods with the
     * same name but with different parameters. It's works like a combination of
     * mathematics.
     * 
     * Creating nested loop for annotation TestedOn. Depth of loop depends on
     * parameters. parameters of this method -> ints contains Integer values, 0
     * start position, ints.length last position, "" as blank output,
     * outputCodeProperties generated method container, object as instance that
     * can access this method, and method name. Formula to generate method:
     * Example: a method has 3 parameters with Integer values
     * 
     * @TestedOn(ints = { 5, 6 }) int i; -> length: 2;
     * @TestedOn(ints = { 2, 3, 4 }) int j; -> length: 3;
     * @TestedOn(ints = { 5, 6, 7, 8 }) int k; -> length: 4;
     * 
     *                Total method will be 2 * 3 * 4 = 24; So, loops length are
     *                2 , 3, 4 respectively, last loop contains the sequence of
     *                parameters of a method
     *                </p>
     * 
     * @param datas
     *            - List of TheoriesData object.
     * @param current
     *            - current postion.
     * @param max
     *            - last postion.
     * @param output
     *            - outout parameter.
     * @param outputCodeProperties
     *            - object of OutputCodeProperties.
     * @param classRecord
     *            - object of ClassRecords.
     * @param method
     *            - a Method type.
     * @param object
     *            - object name.
     * @param methodName
     *            - generated method name.
     * @return Strign - outpur as parameter.
     * 
     * @author Shohel Shamim
     */
    private String theoryMethodGenerator(List<TheoriesData> datas, int current,
	    int max, String output, OutputCodeProperties outputCodeProperties,
	    ClassRecords classRecord, Method method, String object,
	    String methodName) {
	current++;
	if (current <= max) {
	    TheoriesData theoriesData = datas.get(current - 1);
	    String originl = output;
	    for (int i = 0; i < theoriesData.getDatas().length; i++) {
		output += theoriesData.getDatas()[i];
		if (current < max) {
		    output += ",";
		}
		// visit again to create nested loop
		output = theoryMethodGenerator(datas, current, max, output,
			outputCodeProperties, classRecord, method, object,
			methodName);
		if (current + 1 > max) {
		    String generatedMethod = (object + methodName + "("
			    + output + ");").trim();
		    // At the last loop parameters generates in output and by
		    // adding extra information to create workable method it
		    // stored list
		    outputCodeProperties.addTheoryMethods(this.classRecords,
			    method, generatedMethod);
		}
		output = originl;
	    }
	}
	return output;
    }

    /**
     * <li><strong><i>toCamelCase</i></strong></li>
     * 
     * <pre>
     * private String toCamelCase(String s)
     * </pre>
     * 
     * <p>
     * Convert string to Camel case; As Java code convention object name should
     * be camel case; This method used to convert object name in camel case; Ex:
     * ObjectName to objectName.
     * </p>
     * 
     * @param text
     *            - convert text as object name format.
     * 
     * @author Shohel Shamim
     */
    private String toCamelCase(String text) {
	return text.substring(0, 1).toLowerCase()
		+ text.substring(1, text.length());
    }

    /**
     * <li><strong><i>traverseClasses</i></strong></li>
     * 
     * <pre>
     * private void traverseClasses(Class<?> clas, ClassRecords classRecord,
     * 	    boolean dataPointsLookedUp,
     * 	    OutputCodeProperties outputCodeProperties,
     * 	    List<DataPointProperties> dataPointPropertiesList,
     * 	    boolean hasParameterized)
     * </pre>
     * 
     * <p>
     * This method used to lookup fields, methods and all super classes;
     * dataPointsLookedUp = true or false; if fields from super classes already
     * checked it will be true; then it will not check those fields again to
     * avoid duplication.
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * @param classRecord
     *            - a ClassRecords object.
     * @param dataPointsLookedUp
     *            - true or false.
     * @param outputCodeProperties
     *            - OutputCodeProperties object.
     * @param dataPointPropertiesList
     *            - List of DataPointProperties object.
     * @param hasParameterized
     *            - true or false.
     * 
     * @author Shohel Shamim
     */
    private void traverseClasses(Class<?> clas, ClassRecords classRecord,
	    boolean dataPointsLookedUp,
	    OutputCodeProperties outputCodeProperties,
	    List<DataPointProperties> dataPointPropertiesList,
	    boolean hasParameterized) {
	if (!dataPointsLookedUp) {
	    // If fields not check
	    lookUpDataPoints(clas, dataPointPropertiesList);
	    // for next traverseClasses, dataPointsLookedUp will be true,
	    // because, lookUpDataPoints already checked fields from super
	    // classes
	}
	try {
	    // create different instance, because if class is Parameterized then
	    // the constructor of that test class has some parameters
	    String parameters = "";
	    if (hasParameterized) {
		parameters = getParameterForParameterized(classRecord, clas, 0);
	    }
	    // Field that instantiate required class in output code;
	    boolean result = hasEntryClassFields(classRecord.getEntryClass(),
		    outputCodeProperties.getObjectWithDotSign());
	    parameters = "(" + parameters + ")";
	    FieldsProperty fp = new FieldsProperty(classRecord.getEntryClass(),
		    outputCodeProperties.getClasz(),
		    outputCodeProperties.getObjectWithDotSign(), result, "E",
		    this.entryClassFields.size(), parameters);
	    fp.setFieldAssignValue("new " + fp.getDataType());
	    outputCodeProperties.setClassName(fp.getDataType());
	    outputCodeProperties.setObjectName(fp.getObjectName());
	    outputCodeProperties.setObject(fp.getObjectName() + ".");
	    addEntryClassFields(fp);
	    // look up for Class Rule and Rule
	    lookUpClassRuleOrRuleFields(clas, classRecord, outputCodeProperties);
	    // Explorer methods; sort them by comparing each method
	    Method[] methods = clas.getDeclaredMethods();
	    // if true then sort method otherwise not
	    if (this.methodShouldSort) {
		Arrays.sort(methods, new Comparator<Method>() {
		    @Override
		    public int compare(Method method1, Method method2) {
			int result = method1.getName().compareTo(
				method2.getName());
			if (result == 0) {
			    if (method1.getParameterTypes().length < method2
				    .getParameterTypes().length) {
				result = -1;
			    }
			}
			return result;
		    }
		});
	    }
	    traverseMethods(methods, classRecord, dataPointPropertiesList,
		    outputCodeProperties);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	Class<?> superClass = clas.getSuperclass();
	if (superClass != null) {
	    // Ignore Object class
	    if (superClass != java.lang.Object.class) {
		// dataPointsLookedUp: true to ignore dataPointsLookedUp
		traverseClasses(superClass, classRecord, true,
			outputCodeProperties, dataPointPropertiesList,
			hasParameterized);
	    }
	}
    }

    /**
     * <li><strong><i>traverseClasses</i></strong></li>
     * 
     * <pre>
     * private void traverseClasses(ClassRecords classRecord)
     * </pre>
     * 
     * <p>
     * Traverse class with essential properties from ClassRecords; This method
     * used to traverse each entry point of all classes. Example: Entry point of
     * each Suite classes. Must use if test case has multiple entry points. No
     * need to traverse parent class if entry class has any. Generates object
     * name, verify some important JUnit Test rules, etc; After verifying all
     * required information this method store output code in codeSnap.
     * </p>
     * 
     * @param classRecord
     *            - a object of ClassRecords.
     * 
     * @author Shohel Shamim
     */
    private void traverseClasses(ClassRecords classRecord) {
	// Check whether class is annotated with @Ignore; no need to look into
	// other properties; If isIgnored() == true; output class only contains
	// main method;
	if (!classRecord.isIgnored()) {
	    OutputCodeProperties outputCodeProperties = new OutputCodeProperties();
	    Class<?> entryClass = classRecord.getEntryClass();
	    boolean hasSuiteClass = classRecord.hasSuiteClasses();
	    boolean hasTheories = classRecord.hasRunWithTheories();
	    boolean hasParameterized = classRecord.hasRunWithParameterized();
	    this.hasParameters = hasParameterized;
	    if (this.hasParameters) {
		setHasParametersForOutputClass(hasParameterized);
	    }
	    // Properties initialization; add object name; packages etc
	    initializePropertiesForOutputCode(entryClass, classRecord,
		    outputCodeProperties, hasParameterized);
	    // Setting the order of generated code
	    if (hasSuiteClass) {
		// if class has @Suite classes then it will traverse again by
		// passing the class to traverseClasses method
		writeBeforeClass(outputCodeProperties);
		for (ClassRecords classRecords : classRecord.getSuiteClasses()) {
		    if (!classRecords.isIgnored()) {
			traverseClasses(classRecords);
		    }
		}
		writeAfterClass(outputCodeProperties);
	    } else if (hasTheories) {
		writeBeforeClass(outputCodeProperties);
		// Normal Test Methods
		addTestFullTestCode(outputCodeProperties);
		// Theory Methods
		for (String theoryMethods : outputCodeProperties
			.getTheoryMethods()) {
		    writeBefore(outputCodeProperties);
		    addCode(theoryMethods);
		    writeAfter(outputCodeProperties);
		}
		writeAfterClass(outputCodeProperties);
	    } else {
		// get the length of parameters data if class has
		// @RunWith(Parametarized.class);if class has no
		// @RunWith(Parametarized.class) then length will 1 so that at
		// least once it can run for other test cases (will consider it
		// as a normal test class)
		int length = 1;
		String newInstanceForObject[] = null;
		if (hasParameterized) {
		    length = classRecord.getParameterizedMethodsData().size();
		    newInstanceForObject = new String[length];
		    for (int j = 0; j < length; j++) {
			String parameters = "";
			parameters = getParameterForParameterized(classRecord,
				classRecord.getEntryClass(), j);
			// Store new instance;
			newInstanceForObject[j] = outputCodeProperties
				.getObjectWithDotSign()
				+ " = "
				+ "new "
				+ outputCodeProperties.getClassName()
				+ "("
				+ parameters + ");";

		    }
		}
		writeBeforeClass(outputCodeProperties);
		for (int i = 0; i < length; i++) {
		    // Add new instance if class is Parameterized;
		    if (hasParameterized
			    && !outputCodeProperties.getTestMethods().isEmpty()) {
			addCode(newInstanceForObject[i]);
		    }
		    addTestFullTestCode(outputCodeProperties);
		}
		writeAfterClass(outputCodeProperties);
	    }
	}
    }

    /**
     * <li><strong><i>traverseMethods</i></strong></li>
     * 
     * <pre>
     * private void traverseMethods(Method[] methods, ClassRecords classRecord,
     * 	    List<DataPointProperties> dataPointPropertiesList,
     * 	    OutputCodeProperties outputCodeProperties) <blockquote>throws</blockquote> Exception
     * </pre>
     * 
     * <p>
     * Traverse methods and store each method to OutputCodeProperties by
     * comparing it's annotation all methods for code generation actually goes
     * from here through instance of OutputCodeProperties.
     * </p>
     * 
     * @param methods
     *            - arrays of Mehtod.
     * @param classRecord
     *            - object of ClassRecords.
     * @param dataPointPropertiesList
     *            - list of DataPointProperties.
     * @param outputCodeProperties
     *            - a object of )OutputCodeProperties.
     * 
     * @author Shohel Shamim
     */
    private void traverseMethods(Method[] methods, ClassRecords classRecord,
	    List<DataPointProperties> dataPointPropertiesList,
	    OutputCodeProperties outputCodeProperties) throws Exception {
	String classname = outputCodeProperties.getClassName();
	String closingString = "();";
	String object = outputCodeProperties.getObject();
	boolean isCategoryType = classRecord.hasRunWithCategoriesAtItsPath();
	boolean isEmptyIncludeCategory = (classRecord.getIncludeCategoryClass()
		.isEmpty());
	boolean isEmptyExcludeCategory = (classRecord.getExcludeCategoryClass()
		.isEmpty());
	// look into all method
	for (Method method : methods) {
	    String methodName = method.getName();
	    String name = "";
	    String annotName = "";
	    // if Test case has expected value, example: @Test(expected =
	    // Exception.class)
	    String expected = "";
	    // timeout value of annottion @Test
	    long timeout = 0;
	    Class<?>[] categoryClass = null;
	    // if any test method has @Ignore annotation it should be skip;
	    boolean ignored = false;
	    // This is the category marker, if it is true then method can be
	    // added otherwise false
	    boolean methoodAdd = true;
	    String fullMethodName = "";
	    TestProperties testProperties = null;
	    for (Annotation annotation : method.getAnnotations()) {
		annotName = annotation.annotationType().getSimpleName();
		switch (annotName) {
		case After:
		case AfterClass:
		case Before:
		case BeforeClass:
		case Parameters:
		case Theory:
		    name = annotName;
		    break;
		case Test:
		    Test test = (Test) annotation;
		    // no exception or has no expected value
		    if (!test.expected().equals(None.class)) {
			expected = (test.expected().getName()).trim();
		    }
		    timeout = test.timeout();
		    name = annotName;
		    break;
		case Category:
		    categoryClass = ((Category) annotation).value();
		    break;
		case Ignore:
		    ignored = true;
		    break;
		}
	    }
	    // Checke for Category Marker
	    if (isCategoryType
		    && (!isEmptyIncludeCategory || !isEmptyExcludeCategory)) {
		// initialize; so that it can only add method those are marked
		methoodAdd = false;
		boolean inResult = hasRequiredCategories(categoryClass,
			classRecord.getIncludeCategoryClass());
		boolean exResult = hasRequiredCategories(categoryClass,
			classRecord.getExcludeCategoryClass());
		// if both category matched then it will not add that method, if
		// include match then it will add method, if first 2 condition
		// false and if exclude not match and include is empty then it
		// will add that method
		if (inResult && exResult) {
		    methoodAdd = false;
		} else if (inResult) {
		    methoodAdd = true;
		} else if (!exResult && isEmptyIncludeCategory) {
		    methoodAdd = true;
		}
	    }
	    // @Ignore not work in Fields, Methods those are annotated by
	    // @Before, @BeforeClass, @After, @AfterClass, @Parameters
	    // all methods are stored in complete format, with it's object name,
	    // closing brace and parameters
	    if (name.equalsIgnoreCase(BeforeClass)) {
		outputCodeProperties.addBeforeClass((classname + "."
			+ methodName + closingString).trim());
	    } else if (name.equalsIgnoreCase(AfterClass)) {
		outputCodeProperties.addAfterClass((classname + "."
			+ methodName + closingString).trim());
	    } else if (name.equalsIgnoreCase(Before)
		    && !classRecord.hasRunWithSuiteOrCategories()) {
		outputCodeProperties
			.addBefore((object + methodName + closingString).trim());
	    } else if (name.equalsIgnoreCase(After)
		    && !classRecord.hasRunWithSuiteOrCategories()) {
		outputCodeProperties
			.addAfter((object + methodName + closingString).trim());
	    } else if (name.equalsIgnoreCase(Test) && !ignored
		    && !classRecord.hasRunWithSuiteOrCategories() && methoodAdd) {
		fullMethodName = (object + methodName + closingString).trim();
		testProperties = new TestProperties(methodName, fullMethodName,
			expected, timeout);
		outputCodeProperties.addTestMethod(testProperties);
	    } else if (name.equalsIgnoreCase(Parameters)
		    && !classRecord.hasRunWithSuiteOrCategories()) {
		if (methodName.equals("data")) {
		    try {
			if (method.invoke(null) instanceof Collection) {
			    Collection<?> data = (Collection<?>) method
				    .invoke(null);
			    classRecord.setParameterizedMethodsData(data);
			}
		    } catch (IllegalAccessException e) {
			e.printStackTrace();
		    } catch (IllegalArgumentException e) {
			e.printStackTrace();
		    } catch (InvocationTargetException e) {
			e.printStackTrace();
		    }
		}
	    } else if (name.equalsIgnoreCase(Theory) && !ignored
		    && !classRecord.hasRunWithSuiteOrCategories() && methoodAdd) {
		if (classRecord.hasRunWithTheories()) {
		    Annotation[][] parameterAnnotations = method
			    .getParameterAnnotations();
		    int parameterAnnotationsLength = parameterAnnotations.length;
		    if (parameterAnnotationsLength == 0) {
			String genMethod = (object + methodName + closingString)
				.trim();
			outputCodeProperties.addTheoryMethods(classRecord,
				method, genMethod);
		    } else {
			List<TheoriesData> datas = new ArrayList<TheoriesData>();
			TheoriesData theoriesData = null;
			int counter = 0;
			for (Annotation[] annots : parameterAnnotations) {
			    String dataType = method.getParameterTypes()[counter]
				    .getSimpleName();
			    if (annots.length != 0) {
				String annotationType = annots[0]
					.annotationType().getSimpleName();
				switch (annotationType) {
				case TestedOn:
				    TestedOn testedOn = (TestedOn) (annots)[0];
				    theoriesData = new TheoriesData(dataType,
					    TestedOn);
				    Object[] values = new Object[testedOn
					    .ints().length];
				    for (int i = 0; i < testedOn.ints().length; i++) {
					values[i] = testedOn.ints()[i];
				    }
				    theoriesData.setDatas(values);
				    datas.add(theoriesData);
				    break;
				case ParametersSuppliedBy:
				    this.hasParameterSuppliedBy = true;
				    if (this.hasParameterSuppliedBy) {
					setHasParameterSuppliedByForOutputClass(this.hasParameterSuppliedBy);
				    }
				    lookUpParameterSuppliedBy(datas, annots[0],
					    classRecord, theoriesData,
					    dataType,
					    annots[0].annotationType());
				    break;
				default:
				    this.hasParameterSuppliedBy = true;
				    if (this.hasParameterSuppliedBy) {
					setHasParameterSuppliedByForOutputClass(this.hasParameterSuppliedBy);
				    }
				    Class<?> clz = annots[0].annotationType();
				    for (Annotation annot : clz
					    .getAnnotations()) {
					if (annot
						.annotationType()
						.getSimpleName()
						.equalsIgnoreCase(
							ParametersSuppliedBy)) {
					    lookUpParameterSuppliedBy(datas,
						    annot, classRecord,
						    theoriesData, dataType, clz);
					    break;
					}
				    }
				    break;
				}
			    } else {
				DataPointProperties dataPointPropertie = null;
				for (DataPointProperties dpp : dataPointPropertiesList) {
				    if (dpp.getDataType().equalsIgnoreCase(
					    dataType)) {
					dataPointPropertie = dpp;
				    }
				}
				if (dataPointPropertie != null) {
				    theoriesData = new TheoriesData(dataType,
					    DataPoint);
				    theoriesData.setDatas(dataPointPropertie
					    .getValues().toArray());
				    datas.add(theoriesData);
				} else {
				    throw new Exception(
					    "traverseMethods: Please verify value of fields those are annotated by DataPoint or DataPoints");
				}
			    }
			    counter++;
			}
			theoryMethodGenerator(datas, 0, datas.size(), "",
				outputCodeProperties, classRecord, method,
				object, methodName);
		    }
		}
	    }
	}
    }

    /**
     * <li><strong><i>writeAfter</i></strong></li>
     * 
     * <pre>
     * private void writeAfter(OutputCodeProperties outputCodeProperties)
     * </pre>
     * 
     * <p>
     * Add all methods to codeSnap those are annotated by After and if any
     * method found from annotation Rule.
     * </p>
     * 
     * @param outputCodeProperties
     *            - a object of OutputCodeProperties.
     * 
     * @author Shohel Shamim
     */
    private void writeAfter(OutputCodeProperties outputCodeProperties) {
	for (String after : outputCodeProperties.getAfter()) {
	    addCode(after);
	}
	for (RuleAndClassRuleProperties ru : outputCodeProperties.getRules()) {
	    for (String method : ru.getAfterTest()) {
		addCode(method);
	    }
	}
    }

    /**
     * <li><strong><i>writeAfterClass</i></strong></li>
     * 
     * <pre>
     * private void writeAfterClass(OutputCodeProperties outputCodeProperties)
     * </pre>
     * 
     * <p>
     * Add all methods to codeSnap those are annotated by AfterClass and if any
     * method found from annotation ClassRule.
     * </p>
     * 
     * @param outputCodeProperties
     *            - a object of OutputCodeProperties.
     * 
     * @author Shohel Shamim
     */
    private void writeAfterClass(OutputCodeProperties outputCodeProperties) {
	for (String afterClass : outputCodeProperties.getAfterClass()) {
	    addCode(afterClass);
	}
	for (RuleAndClassRuleProperties ru : outputCodeProperties
		.getClassRules()) {
	    for (String method : ru.getAfterTest()) {
		addCode(method);
	    }
	}
    }

    /**
     * <li><strong><i>writeBefore</i></strong></li>
     * 
     * <pre>
     * private void writeBefore(OutputCodeProperties outputCodeProperties)
     * </pre>
     * 
     * <p>
     * If any method found from annotation Rule add them before adding all
     * methods to codeSnap those are annotated by Before.
     * </p>
     * 
     * @param outputCodeProperties
     *            - a object of OutputCodeProperties.
     * 
     * @author Shohel Shamim
     */
    private void writeBefore(OutputCodeProperties outputCodeProperties) {
	initializeFieldsofRules(outputCodeProperties);
	for (RuleAndClassRuleProperties ru : outputCodeProperties
		.getRulesBeforeTest()) {
	    for (String method : ru.getBeforeTest()) {
		addCode(method);
	    }
	}
	for (String before : outputCodeProperties.getBefore()) {
	    addCode(before);
	}
    }

    /**
     * <li><strong><i>writeBeforeClass</i></strong></li>
     * 
     * <pre>
     * private void writeBeforeClass(OutputCodeProperties outputCodeProperties)
     * </pre>
     * 
     * <p>
     * If any method found from annotation ClassRule add them before adding all
     * methods to codeSnap those are annotated by BeforeClass.
     * </p>
     * 
     * @param outputCodeProperties
     *            - a object of OutputCodeProperties.
     * 
     * @author Shohel Shamim
     */
    private void writeBeforeClass(OutputCodeProperties outputCodeProperties) {
	for (RuleAndClassRuleProperties ru : outputCodeProperties
		.getClassRulesBeforeTest()) {
	    for (String method : ru.getBeforeTest()) {
		addCode(method);
	    }
	}
	for (String beforeClass : outputCodeProperties.getBeforeClass()) {
	    addCode(beforeClass);
	}
    }
}