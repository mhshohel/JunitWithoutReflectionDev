/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.staticcallgraph
 *
 * @FileName INVOKEProperties.java
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
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import tools.staticcallgraph.MethodVisitor.INVOKEType;

public abstract class INVOKEProperties {
    // means the class name that is called
    private String classCalling = null;
    private Description description = null;
    private JavaClass javaClass = null;
    // means method that is called by methodCall
    private INVOKEMehtodProperties methodCallingFrom = null;
    // means method that is currently using
    private INVOKEMehtodProperties methodCallTo = null;
    private MethodGen methodGen = null;
    protected INVOKEType type = null;
    private String methodCallingFromNode = "";
    private String methodCallToNode = "";

    public INVOKEProperties(JavaClass javaClass, MethodGen methodGen,
	    String classCalling) {
	this.javaClass = javaClass;
	this.methodGen = methodGen;
	this.classCalling = classCalling;
    }

    public boolean contains(INVOKEProperties ip) {
	return (ip.methodCallingFromNode
		.equalsIgnoreCase(this.methodCallingFromNode) && ip.methodCallToNode
		.equalsIgnoreCase(this.methodCallToNode));
    }

    // this is to get description of the class quickly, it the call object not
    // calling from
    protected void addDescription(Description description) {
	this.description = description;
    }

    protected void addMethodCall(Description description, String name,
	    Type[] types) {
	SimpleObject so = description.getSimpleObjectByNameAndTypeArgs(name,
		types);
	this.methodCallToNode = so.getNode();
	this.methodCallTo = new INVOKEMehtodProperties(description, name, types);
    }

    protected void addMethodCalling(Description description) {
	SimpleObject so = description.getSimpleObjectByNameAndTypeArgs(
		this.methodGen.getName(), this.methodGen.getArgumentTypes());
	this.methodCallingFromNode = so.getNode();
	this.methodCallingFrom = new INVOKEMehtodProperties(description,
		this.methodGen.getName(), this.methodGen.getArgumentTypes());
    }

    public String getClassNameCallFrom() {
	return this.javaClass.getClassName();
    }

    public String getClassNameCalling() {
	return this.classCalling;
    }

    public Description getDescription() {
	return this.description;
    }

    // call to
    public INVOKEMehtodProperties getMethodCallingFrom() {
	return this.methodCallingFrom;
    }

    // call from
    public INVOKEMehtodProperties getMethodCallTo() {
	return this.methodCallTo;
    }

    public String getEdge() {
	return this.methodCallingFromNode + " ---> " + this.methodCallToNode;
    }

    public abstract INVOKEType getType();
}