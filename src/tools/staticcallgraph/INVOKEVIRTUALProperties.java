/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.staticcallgraph
 *
 * @FileName INVOKEVIRTUALProperties.java
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
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.MethodGen;

import tools.staticcallgraph.MethodVisitor.INVOKEType;

public class INVOKEVIRTUALProperties extends INVOKEProperties {
    private INVOKEVIRTUAL invokevirtual = null;

    public INVOKEVIRTUALProperties(Description parentDescription,
	    Description callingDescription, JavaClass javaClass,
	    MethodGen methodGen, ConstantPoolGen constantPoolGen,
	    INVOKEVIRTUAL invokevirtual) {
	super(javaClass, methodGen, invokevirtual.getReferenceType(
		constantPoolGen).toString());
	this.invokevirtual = invokevirtual;
	this.type = INVOKEType.VIRTUAL;
	addDescription(callingDescription);
	addMethodCalling(parentDescription);
	addMethodCall(callingDescription,
		this.invokevirtual.getName(constantPoolGen),
		this.invokevirtual.getArgumentTypes(constantPoolGen));
    }

    public INVOKEVIRTUAL getInvokevirtual() {
	return this.invokevirtual;
    }

    @Override
    public INVOKEType getType() {
	return this.type;
    }
}
