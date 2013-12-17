/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName FieldsProperty.java
 * 
 * @FileCreated Feb 19, 2013
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

/**
 * <li><strong>FieldsProperty</strong></li>
 * 
 * <pre>
 * class FieldsProperty
 * </pre>
 * <p>
 * This class contains the field's type, name, parameters, class of the field
 * type and class that contains the field.
 * </p>
 * <br/>
 * 
 * @author Shohel Shamim
 */
public class FieldsProperty {
    /* Take the entry class */
    private Class<?> entryClass;
    /* Keep field's value */
    private String fieldAssignValue;
    /* Keep class of field's type */
    private Class<?> fieldClass;
    /* Keep data type as String */
    private String fieldDataType;
    /* Keep field name as String */
    private String fieldName;

    private int number = 0;
    private String parameters = "";

    /**
     * <li><strong><i>FieldsProperty</i></strong></li>
     * 
     * <pre>
     * public FieldsProperty(Class<?> entryClass, Class<?> fieldClass,
     * 	    String fieldName, boolean hasDuplicate, String prefix, int number,
     * 	    String parameters)
     * </pre>
     * 
     * <p>
     * Keep basic information of a field by taking required values.
     * </p>
     * 
     * @param entryClass
     *            - entry class (.class) for Parameterized it will be
     *            ParameterSignature.class
     * @param fieldClass
     *            - class of field type (.class) for Parameterized it will be
     *            ParameterSignature.class
     * @param fieldName
     *            - name of Field
     * @param hasDuplicate
     *            - if same field found in superclass then it will be true else
     *            false
     * @param prefix
     *            - get prefix of field's name
     * @param number
     *            - int number to add additional number in field name
     * @param parameters
     *            - generated new parameters
     * 
     * @author Shohel Shamim
     */
    public FieldsProperty(Class<?> entryClass, Class<?> fieldClass,
	    String fieldName, boolean hasDuplicate, String prefix, int number,
	    String parameters) {
	// keep the entry class
	this.entryClass = entryClass;
	// keep class of field type
	this.fieldClass = fieldClass;
	setParameters(parameters);
	// if duplicate field found (from superclass) then it will create field
	// type differently
	if (hasDuplicate) {
	    this.fieldDataType = fieldClass.getPackage().getName() + "."
		    + fieldClass.getSimpleName();
	    setObjectPrefix(number);
	    setObjectName(fieldName + prefix + Integer.toString(this.number));
	} else {
	    this.fieldDataType = fieldClass.getSimpleName();
	    setObjectName(fieldName);
	}
    }

    /**
     * <li><strong><i>equals</i></strong></li>
     * 
     * <pre>
     * public boolean equals(FieldsProperty fp)
     * </pre>
     * 
     * <p>
     * Compare two FieldsProperty
     * </p>
     * 
     * @param fp
     *            - compare another FieldsProperty type object
     * @return boolean - return true or false
     * 
     * @author Shohel Shamim
     */
    public boolean equals(FieldsProperty fp) {
	if (this.getObjectName().equalsIgnoreCase(fp.getObjectName())) {
	    return true;
	}
	return false;
    }

    /**
     * <li><strong><i>getDataType</i></strong></li>
     * 
     * <pre>
     * public String getDataType()
     * </pre>
     * 
     * <p>
     * Return custom data type of the field. It's actually return the value of
     * the field. Example: ABC abc = new ABC("HI"); Here, this method returns
     * ABC("HI").
     * </p>
     * 
     * @return String - return data type.
     * 
     * @author Shohel Shamim
     */
    public String getDataType() {
	return this.fieldDataType;
    }

    /**
     * <li><strong><i>getEntryClass</i></strong></li>
     * 
     * <pre>
     * public Class<?> getEntryClass()
     * </pre>
     * 
     * <p>
     * Return the entry class that contains this field. For Parameterized it
     * will return ParameterSignature.class
     * </p>
     * 
     * @return Class - return class
     * 
     * @author Shohel Shamim
     */
    public Class<?> getEntryClass() {
	return this.entryClass;
    }

    /**
     * <li><strong><i>getFieldAssignValue</i></strong></li>
     * 
     * <pre>
     * public String getFieldAssignValue()
     * </pre>
     * 
     * <p>
     * Return assign value of this field. After setting all the parameters and
     * data type this method will return assign value of that field. This value
     * is used to initiate field when it generates output code.
     * </p>
     * 
     * @return String - assign value for field.
     * 
     * @author Shohel Shamim
     */
    public String getFieldAssignValue() {
	return this.fieldAssignValue;
    }

    /**
     * <li><strong><i>getFieldClass</i></strong></li>
     * 
     * <pre>
     * public Class<?> getFieldClass()
     * </pre>
     * 
     * <p>
     * Return the class of field. For Parameterized it will return
     * ParameterSignature.class
     * </p>
     * 
     * @return Class - return class
     * 
     * @author Shohel Shamim
     */
    public Class<?> getFieldClass() {
	return this.fieldClass;
    }

    /**
     * <li><strong><i>getFieldWithValues</i></strong></li>
     * 
     * <pre>
     * public String getFieldWithValues()
     * </pre>
     * 
     * <p>
     * Return field with it's assigned value.
     * </p>
     * 
     * @return String - return field with its assigned value
     * 
     * @author Shohel Shamim
     */
    public String getFieldWithValues() {
	String field = this.fieldDataType + " " + this.fieldName + " = "
		+ getFieldAssignValue() + ";";
	return field;
    }

    /**
     * <li><strong><i>getObjectName</i></strong></li>
     * 
     * <pre>
     * public String getObjectName()
     * </pre>
     * 
     * <p>
     * Return field name as object name.
     * </p>
     * 
     * @return String - return field name.
     * 
     * @author Shohel Shamim
     */
    public String getObjectName() {
	return this.fieldName;
    }

    /**
     * <li><strong><i>getParameters</i></strong></li>
     * 
     * <pre>
     * public String getParameters()
     * </pre>
     * 
     * <p>
     * Return parameters of assigned value. If field is a class type then that
     * class may contains parameters. If it has parameters then it will return
     * parameters.
     * </p>
     * 
     * @return String - return parameters of a class type.
     * 
     * @author Shohel Shamim
     */
    public String getParameters() {
	return this.parameters;
    }

    /**
     * <li><strong><i>setFieldAssignValue</i></strong></li>
     * 
     * <pre>
     * public void setFieldAssignValue(String fieldAssignValue)
     * </pre>
     * 
     * <p>
     * Set assign value of field.
     * </p>
     * 
     * @param fieldAssignValue
     *            - set field's assign value
     * 
     * @author Shohel Shamim
     */
    public void setFieldAssignValue(String fieldAssignValue) {
	this.fieldAssignValue = fieldAssignValue + getParameters();
    }

    /**
     * <li><strong><i>setObjectName</i></strong></li>
     * 
     * <pre>
     * public void setObjectName(String objectName)
     * </pre>
     * 
     * <p>
     * Get object name as parameter.
     * </p>
     * 
     * @param objectName
     *            - set object name
     * 
     * @author Shohel Shamim
     */
    public void setObjectName(String objectName) {
	this.fieldName = objectName;
    }

    /**
     * <li><strong><i>setObjectPrefix</i></strong></li>
     * 
     * <pre>
     * public void setObjectPrefix(int val)
     * </pre>
     * 
     * <p>
     * Get integer value as an extension of object name to make unique object.
     * </p>
     * 
     * @param val
     *            - integer value as extension
     * 
     * @author Shohel Shamim
     */
    public void setObjectPrefix(int val) {
	this.number = val;
    }

    public void setParameters(String parameters) {
	this.parameters = parameters;
    }
}
