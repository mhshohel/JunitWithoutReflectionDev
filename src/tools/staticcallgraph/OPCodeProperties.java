/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.staticcallgraph
 *
 * @FileName OPCodeProperties.java
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

import java.util.HashSet;
import java.util.Set;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

public class OPCodeProperties {
    private Set<INVOKEProperties> interfaceCall = new HashSet<INVOKEProperties>();
    // keep method name with parameters type, this method is the method which is
    // inside the class
    // ex: TestClass.method(); here method()
    private INVOKEMehtodProperties method = null;
    private Set<INVOKEProperties> methodCall = new HashSet<INVOKEProperties>();
    private Set<INVOKEProperties> objectCall = new HashSet<INVOKEProperties>();
    private Set<INVOKEProperties> staticCall = new HashSet<INVOKEProperties>();

    public String getEdges() {
	StringBuilder sb = new StringBuilder();
	if (!interfaceCall.isEmpty())
	    sb.append(readINVOKEProperties(interfaceCall));
	if (!methodCall.isEmpty())
	    sb.append(readINVOKEProperties(methodCall));
	if (!objectCall.isEmpty())
	    sb.append(readINVOKEProperties(objectCall));
	if (!staticCall.isEmpty())
	    sb.append(readINVOKEProperties(staticCall));
	return sb.toString();
    }

    private String readINVOKEProperties(Set<INVOKEProperties> ip) {
	StringBuilder sb = new StringBuilder();
	String val = "";
	int count = 0;
	for (INVOKEProperties i : ip) {
	    val = i.getEdge();
	    sb.append((count + 1 == ip.size()) ? val : val + "\n");
	    count++;
	}
	return sb.toString();
    }

    public void addInterfaceCall(Description parentDescription,
	    Description callingDescription, JavaClass javaClass,
	    MethodGen methodGen, ConstantPoolGen constantPoolGen,
	    INVOKEINTERFACE interfaceCall) {
	INVOKEProperties ip = new INVOKEINTERFACEProperties(parentDescription,
		callingDescription, javaClass, methodGen, constantPoolGen,
		interfaceCall);
	for (INVOKEProperties i : this.interfaceCall) {
	    if (i.contains(ip)) {
		return;
	    }
	}
	this.interfaceCall.add(ip);
    }

    public void addMethod(Description description, String name, Type[] types) {
	this.method = new INVOKEMehtodProperties(description, name, types);
    }

    public void addObjectCall(Description parentDescription,
	    Description callingDescription, JavaClass javaClass,
	    MethodGen methodGen, ConstantPoolGen constantPoolGen,
	    INVOKESPECIAL objectCall) {
	INVOKEProperties ip = new INVOKESPECIALProperties(parentDescription,
		callingDescription, javaClass, methodGen, constantPoolGen,
		objectCall);
	for (INVOKEProperties i : this.objectCall) {
	    if (i.contains(ip)) {
		return;
	    }
	}
	this.objectCall.add(ip);
    }

    public void addStaticCall(Description parentDescription,
	    Description callingDescription, JavaClass javaClass,
	    MethodGen methodGen, ConstantPoolGen constantPoolGen,
	    INVOKESTATIC staticCall) {
	INVOKEProperties ip = new INVOKESTATICProperties(parentDescription,
		callingDescription, javaClass, methodGen, constantPoolGen,
		staticCall);
	for (INVOKEProperties i : this.staticCall) {
	    if (i.contains(ip)) {
		return;
	    }
	}
	this.staticCall.add(ip);
    }

    public void addtMethodCall(Description parentDescription,
	    Description callingDescription, JavaClass javaClass,
	    MethodGen methodGen, ConstantPoolGen constantPoolGen,
	    INVOKEVIRTUAL methodCall) {
	INVOKEProperties ip = new INVOKEVIRTUALProperties(parentDescription,
		callingDescription, javaClass, methodGen, constantPoolGen,
		methodCall);
	for (INVOKEProperties i : this.methodCall) {
	    if (i.contains(ip)) {
		return;
	    }
	}
	this.methodCall.add(ip);
    }

    public Set<INVOKEProperties> getInterfaceCall() {
	return this.interfaceCall;
    }

    public INVOKEMehtodProperties getMethod() {
	return this.method;
    }

    public Set<INVOKEProperties> getMethodCall() {
	return this.methodCall;
    }

    public Set<INVOKEProperties> getObjectCall() {
	// SPECIAL
	return this.objectCall;
    }

    public Set<INVOKEProperties> getStaticCall() {
	return this.staticCall;
    }
}