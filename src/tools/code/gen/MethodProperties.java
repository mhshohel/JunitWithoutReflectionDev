/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName MethodProperties.java
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

import java.lang.reflect.Method;
import java.util.List;

/**
 * <li><strong>MethodProperties</strong></li>
 * 
 * <pre>
 * public class MethodProperties
 * </pre>
 * <p>
 * This class used to get values from DataPoint or DataPoints annotated fields.
 * </p>
 * <br/>
 * 
 * @author Shohel Shamim
 */
public class MethodProperties {
    /*
     * dataPointList: Store type and values in list; no duplicate type will be
     * store; each type has a list of values in DataPointProperties class
     */
    private List<DataPointProperties> dataPointList;
    /* Take actual method */
    private Method method;
    /*
     * Take each parameter of method in actual order; instead of original type
     * it will take DataPointProperties type, that has actual type and other
     * information
     */
    private DataPointProperties[] parametersOrder;

    /**
     * <li><strong><i>MethodProperties</i></strong></li>
     * 
     * <pre>
     * public MethodProperties(Method method, List<DataPointProperties> dataPointList)
     * </pre>
     * 
     * <p>
     * Contains method and list of @DataPoint or @DataPoints fields wrapped
     * object type: DataPointProperties
     * </p>
     * 
     * @param method
     *            - a Mehtod Type.
     * 
     * @param dataPointList
     *            - List of DataPointProperties.
     * 
     * @author Shohel Shamim
     */
    public MethodProperties(Method method,
	    List<DataPointProperties> dataPointList) {
	this.method = method;
	this.dataPointList = dataPointList;
	// initialize parametersOrder according to method's parameter length
	this.parametersOrder = new DataPointProperties[method
		.getParameterTypes().length];
	// explore method's parameters
	lookUpParams();
    }

    /**
     * <li><strong><i>getOrderedParameter</i></strong></li>
     * 
     * <pre>
     * public DataPointProperties[] getOrderedParameter()
     * </pre>
     * 
     * <p>
     * Return ordered DataPointProperties array in same order of method's
     * original parameters
     * </p>
     * 
     * @return DataPointProperties[] - return parameters as Same order.
     * 
     * 
     * @author Shohel Shamim
     */
    public DataPointProperties[] getOrderedParameter() {
	return this.parametersOrder;
    }

    /**
     * <li><strong><i>lookUpParams</i></strong></li>
     * 
     * <pre>
     * public void lookUpParams()
     * </pre>
     * 
     * <p>
     * Look into method's parameters and store matched object of
     * DataPointProperties
     * </p>
     * 
     * 
     * @author Shohel Shamim
     */
    public void lookUpParams() {
	for (int i = 0; i < this.method.getParameterTypes().length; i++) {
	    String name = this.method.getParameterTypes()[i].getSimpleName();
	    for (DataPointProperties dpc : this.dataPointList) {
		// whenever matched the type, DataPointProperties type object
		// will be store and loop will stop
		if (dpc.getDataType().equalsIgnoreCase(name)) {
		    this.parametersOrder[i] = dpc;
		    break;
		}
	    }
	}
    }

    /**
     * <li><strong><i>paramSize</i></strong></li>
     * 
     * <pre>
     * public int paramSize()
     * </pre>
     * 
     * <p>
     * Return length of Parameters.
     * </p>
     * 
     * @return int - return parameter's size.
     * 
     * 
     * @author Shohel Shamim
     */
    public int paramSize() {
	return this.parametersOrder.length;
    }
}