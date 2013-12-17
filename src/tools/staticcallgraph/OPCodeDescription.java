/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.staticcallgraph
 *
 * @FileName OPCodeDescription.java
 * 
 * @FileCreated Nov 5, 2013
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.bcel.generic.Type;

public class OPCodeDescription {
    private Description description = null;
    // whenever a class calls must call oneTimeUseOnly, means new ClassA(), it
    // means we have to call below method first and only once
    private OPCodeProperties oneTimeUseOnly = null;
    private List<OPCodeProperties> others = null;

    public OPCodeDescription(Description description) {
	this.description = description;
	// mostly constructor
	this.oneTimeUseOnly = new OPCodeProperties();
	this.others = new ArrayList<OPCodeProperties>();
    }

    public OPCodeProperties getOneTimeUseOnly() {
	return this.oneTimeUseOnly;
    }

    public OPCodeProperties getOtherMethodByNameAndType(String name,
	    Type[] types) {
	INVOKEMehtodProperties invokeMehtodProperties = new INVOKEMehtodProperties(
		this.description, name, types);
	for (OPCodeProperties other : others) {
	    if (other.getMethod().getMethodName()
		    .equalsIgnoreCase(invokeMehtodProperties.getMethodName())) {
		if (Arrays.deepEquals(other.getMethod().getTypes(),
			invokeMehtodProperties.getTypes())) {
		    return other;
		}
	    }
	}
	OPCodeProperties opCodeProperties = new OPCodeProperties();
	opCodeProperties.addMethod(this.description, name, types);
	this.others.add(opCodeProperties);

	return opCodeProperties;
    }

    public List<OPCodeProperties> getOtherMethodInvocations() {
	return this.others;
    }
}
