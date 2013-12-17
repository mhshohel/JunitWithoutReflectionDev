/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.staticcallgraph
 *
 * @FileName INVOKESTATICProperties.java
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
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.MethodGen;

import tools.staticcallgraph.MethodVisitor.INVOKEType;

public class INVOKESTATICProperties extends INVOKEProperties {
    private INVOKESTATIC invokestatic = null;

    public INVOKESTATICProperties(Description parentDescription,
	    Description callingDescription, JavaClass javaClass,
	    MethodGen methodGen, ConstantPoolGen constantPoolGen,
	    INVOKESTATIC invokestatic) {
	super(javaClass, methodGen, invokestatic.getReferenceType(
		constantPoolGen).toString());
	this.invokestatic = invokestatic;
	this.type = INVOKEType.STATIC;
	addDescription(callingDescription);
	addMethodCalling(parentDescription);
	addMethodCall(callingDescription,
		this.invokestatic.getName(constantPoolGen),
		this.invokestatic.getArgumentTypes(constantPoolGen));
    }

    public INVOKESTATIC getInvokestatic() {
	return this.invokestatic;
    }

    @Override
    public INVOKEType getType() {
	return this.type;
    }
}