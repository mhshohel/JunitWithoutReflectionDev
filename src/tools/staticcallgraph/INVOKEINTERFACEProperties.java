/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.staticcallgraph
 *
 * @FileName INVOKEINTERFACEProperties.java
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

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.MethodGen;

import tools.staticcallgraph.MethodVisitor.INVOKEType;

public class INVOKEINTERFACEProperties extends INVOKEProperties {
    private INVOKEINTERFACE invokeinterface = null;

    public INVOKEINTERFACEProperties(Description parentDescription,
	    Description callingDescription, JavaClass javaClass,
	    MethodGen methodGen, ConstantPoolGen constantPoolGen,
	    INVOKEINTERFACE invokeinterface) {
	super(javaClass, methodGen, invokeinterface.getReferenceType(
		constantPoolGen).toString());
	this.invokeinterface = invokeinterface;
	this.type = INVOKEType.INTERFACE;
	addDescription(callingDescription);
	addMethodCalling(parentDescription);
	addMethodCall(callingDescription,
		this.invokeinterface.getName(constantPoolGen),
		this.invokeinterface.getArgumentTypes(constantPoolGen));
    }

    public INVOKEINTERFACE getInvokeinterface() {
	return this.invokeinterface;
    }

    @Override
    public INVOKEType getType() {
	return this.type;
    }
}
