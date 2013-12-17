/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName DataPointProperties.java
 * 
 * @FileCreated Jan 19, 2013
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

import java.util.ArrayList;
import java.util.List;

/**
 * <li><strong>DataPointProperties</strong></li>
 * 
 * <pre>
 * public class DataPointProperties
 * </pre>
 * <p>
 * This class is used to store data type and its values from annotated fields
 * DataPoint or DataPoints. Each type can have only one instance of the class to
 * follow the rules of DataPoint or DataPoints annotation.
 * </p>
 * <br/>
 * 
 * @author Shohel Shamim
 */
public class DataPointProperties {
    /*
     * Keep field type name as String format. Example: if field is int type then
     * it will keep int as String format, only name.
     */
    private String dataType = null;
    /* Keep values of field according to its type as object. */
    private List<Object> values = new ArrayList<Object>();

    /**
     * <li><strong><i>addValues</i></strong></li>
     * 
     * <pre>
     * public void addValues(Object value)
     * </pre>
     * 
     * <p>
     * Add values of current field type.
     * </p>
     * 
     * @param value
     *            - take value as an object
     * 
     * @author Shohel Shamim
     */
    public void addValues(Object value) {
	this.values.add(value);
    }

    /**
     * <li><strong><i>equals</i></strong></li>
     * 
     * <pre>
     * public boolean equals(String type)
     * </pre>
     * 
     * <p>
     * Return true or false by comparing two field types name.
     * </p>
     * 
     * @return boolen - true or false.
     * 
     * @author Shohel Shamim
     */
    public boolean equals(String type) {
	return (getDataType().equalsIgnoreCase(type));
    }

    /**
     * <li><strong><i>getDataType</i></strong></li>
     * 
     * <pre>
     * public String getDataType()
     * </pre>
     * 
     * <p>
     * Return field type name as String.
     * </p>
     * 
     * @return String - field type name.
     * 
     * @author Shohel Shamim
     */
    public String getDataType() {
	return this.dataType;
    }

    /**
     * <li><strong><i>getTypeSize</i></strong></li>
     * 
     * <pre>
     * public int getTypeSize()
     * </pre>
     * 
     * <p>
     * Return total length of values of current filed type.
     * </p>
     * 
     * @return int - size of values.
     * 
     * @author Shohel Shamim
     */
    public int getTypeSize() {
	return this.values.size();
    }

    /**
     * <li><strong><i>getValues</i></strong></li>
     * 
     * <pre>
     * public List<Object> getValues()
     * </pre>
     * 
     * <p>
     * Return list of values of current field type.
     * </p>
     * 
     * @return List<Object> - list of values of all field types.
     * 
     * @author Shohel Shamim
     */
    public List<Object> getValues() {
	return this.values;
    }

    /**
     * <li><strong><i>setDataType</i></strong></li>
     * 
     * <pre>
     * public void setDataType(String type)
     * </pre>
     * 
     * <p>
     * Set field type name as String format.
     * </p>
     * 
     * @param type
     *            - field type simple name.
     * 
     * @author Shohel Shamim
     */
    public void setDataType(String type) {
	if (this.dataType == null) {
	    this.dataType = type;
	}
    }
}