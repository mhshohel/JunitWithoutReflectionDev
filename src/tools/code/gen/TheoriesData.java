/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName TheoriesData.java
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
 * <li><strong>TheoriesData</strong></li>
 * 
 * <pre>
 * public class TheoriesData
 * </pre>
 * <p>
 * This class used to keep information of those class that has annotation
 * Theories.
 * </p>
 * <br/>
 * 
 * @author Shohel Shamim
 */
public class TheoriesData {
    /* Keep annotation type */
    private String annotationType;
    /* Keep theories values */
    private Object[] datas;
    /* Keep data type */
    private String dataType;

    /**
     * <li><strong> TheoriesData</strong></li>
     * 
     * <pre>
     * TheoriesData(String dataType, String annotationType)
     * </pre>
     * 
     * <p>
     * Take Data type and Values from fields that has annotation @DataPoint or @DataPoints
     * </p>
     * 
     * 
     * @param String
     *            Data Type Parameter
     * 
     * @param String
     *            Type Name of Annotation
     * 
     * @author Shohel Shamim
     */
    public TheoriesData(String dataType, String annotationType) {
	this.dataType = dataType;
	this.annotationType = annotationType;
    }

    /**
     * <li><strong><i>getAnnotationTypeName</i></strong></li>
     * 
     * <pre>
     * public String getAnnotationTypeName()
     * </pre>
     * 
     * <p>
     * Return annotation type. Annotation type can be DataPoint, DataPoints,
     * Parameters
     * </p>
     * 
     * @return String - annotation type as String
     * 
     * @author Shohel Shamim
     */
    public String getAnnotationTypeName() {
	return this.annotationType;
    }

    /**
     * <li><strong><i>getDatas</i></strong></li>
     * 
     * <pre>
     * public Object[] getDatas()
     * </pre>
     * 
     * <p>
     * Return values as Object array.
     * </p>
     * 
     * @return Object[] - return values.
     * 
     * @author Shohel Shamim
     */
    public Object[] getDatas() {
	return this.datas;
    }

    /**
     * <li><strong><i>getDataTypeName</i></strong></li>
     * 
     * <pre>
     * public String getDataTypeName()
     * </pre>
     * 
     * <p>
     * Return data type name as String format.
     * </p>
     * 
     * @return String - return data type.
     * 
     * @author Shohel Shamim
     */
    public String getDataTypeName() {
	return this.dataType;
    }

    /**
     * <li><strong><i>setDatas</i></strong></li>
     * 
     * <pre>
     * public void setDatas(Object[] values)
     * </pre>
     * 
     * <p>
     * Set values as Object arrays.
     * </p>
     * 
     * @param values
     *            - object arrays contains values
     * 
     * @author Shohel Shamim
     */
    public void setDatas(Object[] values) {
	this.datas = values;
    }
}
