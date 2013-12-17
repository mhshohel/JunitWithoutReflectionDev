/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.staticcallgraph
 *
 * @FileName INVOKESPECIALProperties.java
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
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.MethodGen;

import tools.staticcallgraph.MethodVisitor.INVOKEType;

public class INVOKESPECIALProperties extends INVOKEProperties {
    private INVOKESPECIAL invokespecial = null;

    public INVOKESPECIALProperties(Description parentDescription,
	    Description callingDescription, JavaClass javaClass,
	    MethodGen methodGen, ConstantPoolGen constantPoolGen,
	    INVOKESPECIAL invokespecial) {
	super(javaClass, methodGen, invokespecial.getReferenceType(
		constantPoolGen).toString());
	this.invokespecial = invokespecial;
	this.type = INVOKEType.SPECIAL;
	addDescription(callingDescription);
	addMethodCalling(parentDescription);
	addMethodCall(callingDescription,
		this.invokespecial.getName(constantPoolGen),
		this.invokespecial.getArgumentTypes(constantPoolGen));
    }

    public INVOKESPECIAL getInvokespecial() {
	return this.invokespecial;
    }

    @Override
    public INVOKEType getType() {
	return this.type;
    }
}