/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraph
 *
 * @FileName MethodVisitor.java
 * 
 * @FileCreated Oct 24, 2013
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
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Type;

public class MethodVisitor extends EmptyVisitor {
    public enum INVOKEType {
	INTERFACE("Interface"), SPECIAL("Object"), STATIC("Static"), VIRTUAL(
		"Method");
	private String category;

	private INVOKEType(String category) {
	    this.category = category;
	}

	public String toString() {
	    return this.category;
	}
    }

    private Description entryDescription;
    private String format = null;
    private boolean isGeneratedCode = false;
    private MethodGen methodGen = null;
    private OPCodeDescription opCodeDescription = null;
    private JavaClass visitedClass = null;
    private JCallGraph jCallGraph = null;
    private ConstantPoolGen constantPoolGen;

    public MethodVisitor(JCallGraph jCallGraph, JavaClass jc,
	    Description description, MethodGen methodGen) {
	this.isGeneratedCode = true;
	initialize(jCallGraph, description, jc, methodGen);
    }

    public MethodVisitor(JCallGraph jCallGraph,
	    OPCodeDescription opCodeDescription, Description description,
	    JavaClass jc, MethodGen methodGen) {
	initialize(jCallGraph, description, jc, methodGen);
	this.opCodeDescription = opCodeDescription;
	this.isGeneratedCode = false;
    }

    private void initialize(JCallGraph jCallGraph,
	    Description entryDescription, JavaClass jc, MethodGen methodGen) {
	this.jCallGraph = jCallGraph;
	this.entryDescription = entryDescription;
	this.visitedClass = jc;
	this.methodGen = methodGen;
	this.constantPoolGen = methodGen.getConstantPool();
	this.format = "METHOD:" + this.visitedClass.getClassName() + ":"
		+ this.methodGen.getName() + " " + "(%s)%s:%s";
    }

    public void start() {
	if (this.methodGen.isAbstract() || this.methodGen.isNative()) {
	    return;
	}
	for (InstructionHandle ih = this.methodGen.getInstructionList()
		.getStart(); ih != null; ih = ih.getNext()) {
	    Instruction i = ih.getInstruction();

	    if (!visitInstruction(i)) {
		i.accept(this);
	    }
	}
    }

    private boolean visitInstruction(Instruction i) {
	short opcode = i.getOpcode();
	return ((InstructionConstants.INSTRUCTIONS[opcode] != null)
		&& !(i instanceof ConstantPushInstruction) && !(i instanceof ReturnInstruction));
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE invokeinterface) {
	String methodGenName = this.methodGen.getName();
	String referenceType = invokeinterface.getReferenceType(
		this.constantPoolGen).toString();
	String methodName = invokeinterface.getMethodName(this.constantPoolGen)
		.toString();
	Type[] types = invokeinterface.getArgumentTypes(constantPoolGen);
	Description description = this.jCallGraph
		.getDescriptionByActualClassName(referenceType);
	INVOKEMehtodProperties methodCall = null;
	String log = String.format(this.format, "INTERFACE",
		invokeinterface.getReferenceType(this.constantPoolGen),
		invokeinterface.getMethodName(this.constantPoolGen));
	if (description != null) {
	    if (this.isGeneratedCode) {
		// System.out.println(log);
		methodCall = new INVOKEMehtodProperties(description,
			methodName, types);
		write(this.entryDescription, description, methodCall,
			INVOKEType.INTERFACE);
	    } else {
		if (methodGenName.charAt(0) == '<') {
		    this.opCodeDescription.getOneTimeUseOnly()
			    .addInterfaceCall(this.entryDescription,
				    description, this.visitedClass,
				    this.methodGen, this.constantPoolGen,
				    invokeinterface);
		    // System.out.println(log);
		} else {
		    this.opCodeDescription.getOtherMethodByNameAndType(
			    this.methodGen.getName(),
			    this.methodGen.getArgumentTypes())
			    .addInterfaceCall(this.entryDescription,
				    description, this.visitedClass,
				    this.methodGen, this.constantPoolGen,
				    invokeinterface);
		    // System.out.println(log);
		}
	    }
	}
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL invokespecial) {
	String methodGenName = this.methodGen.getName();
	String referenceType = invokespecial.getReferenceType(
		this.constantPoolGen).toString();
	String methodName = invokespecial.getMethodName(this.constantPoolGen)
		.toString();
	Type[] types = invokespecial.getArgumentTypes(constantPoolGen);
	Description description = this.jCallGraph
		.getDescriptionByActualClassName(referenceType);
	INVOKEMehtodProperties methodCall = null;
	String log = String.format(this.format, "OBJECT",
		invokespecial.getReferenceType(this.constantPoolGen),
		invokespecial.getMethodName(this.constantPoolGen));
	if (description != null) {
	    if (this.isGeneratedCode) {
		// System.out.println(log);
		methodCall = new INVOKEMehtodProperties(description,
			methodName, types);
		write(this.entryDescription, description, methodCall,
			INVOKEType.SPECIAL);
	    } else {
		if (methodGenName.charAt(0) == '<') {
		    this.opCodeDescription.getOneTimeUseOnly().addObjectCall(
			    this.entryDescription, description,
			    this.visitedClass, this.methodGen,
			    this.constantPoolGen, invokespecial);
		    // System.out.println(log);
		} else {
		    this.opCodeDescription.getOtherMethodByNameAndType(
			    this.methodGen.getName(),
			    this.methodGen.getArgumentTypes()).addObjectCall(
			    this.entryDescription, description,
			    this.visitedClass, this.methodGen,
			    this.constantPoolGen, invokespecial);
		    // System.out.println(log);
		}
	    }
	}
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC invokestatic) {
	String methodGenName = this.methodGen.getName();
	String referenceType = invokestatic.getReferenceType(
		this.constantPoolGen).toString();
	String methodName = invokestatic.getMethodName(this.constantPoolGen)
		.toString();
	Type[] types = invokestatic.getArgumentTypes(constantPoolGen);
	Description description = this.jCallGraph
		.getDescriptionByActualClassName(referenceType);
	INVOKEMehtodProperties methodCall = null;
	String log = String.format(this.format, "STATIC",
		invokestatic.getReferenceType(this.constantPoolGen),
		invokestatic.getMethodName(this.constantPoolGen));
	if (description != null) {
	    if (this.isGeneratedCode) {
		// System.out.println(log);
		methodCall = new INVOKEMehtodProperties(description,
			methodName, types);
		write(this.entryDescription, description, methodCall,
			INVOKEType.STATIC);
	    } else {
		if (methodGenName.charAt(0) == '<') {
		    this.opCodeDescription.getOneTimeUseOnly().addStaticCall(
			    this.entryDescription, description,
			    this.visitedClass, this.methodGen,
			    this.constantPoolGen, invokestatic);
		    // System.out.println(log);
		} else {
		    this.opCodeDescription.getOtherMethodByNameAndType(
			    this.methodGen.getName(),
			    this.methodGen.getArgumentTypes()).addStaticCall(
			    this.entryDescription, description,
			    this.visitedClass, this.methodGen,
			    this.constantPoolGen, invokestatic);
		    // System.out.println(log);
		}
	    }
	}
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL invokevirtual) {
	String methodGenName = this.methodGen.getName();
	String referenceType = invokevirtual.getReferenceType(
		this.constantPoolGen).toString();
	String methodName = invokevirtual.getMethodName(this.constantPoolGen)
		.toString();
	Type[] types = invokevirtual.getArgumentTypes(constantPoolGen);
	Description description = this.jCallGraph
		.getDescriptionByActualClassName(referenceType);
	INVOKEMehtodProperties methodCall = null;
	String log = String.format(this.format, "METHOD",
		invokevirtual.getReferenceType(this.constantPoolGen),
		invokevirtual.getMethodName(this.constantPoolGen));
	if (description != null) {
	    if (this.isGeneratedCode) {
		// System.out.println(log);
		methodCall = new INVOKEMehtodProperties(description,
			methodName, types);
		write(this.entryDescription, description, methodCall,
			INVOKEType.VIRTUAL);
	    } else {
		if (methodGenName.charAt(0) == '<') {
		    this.opCodeDescription.getOneTimeUseOnly().addtMethodCall(
			    this.entryDescription, description,
			    this.visitedClass, this.methodGen,
			    this.constantPoolGen, invokevirtual);
		    // System.out.println(log);
		} else {
		    this.opCodeDescription.getOtherMethodByNameAndType(
			    this.methodGen.getName(),
			    this.methodGen.getArgumentTypes()).addtMethodCall(
			    this.entryDescription, description,
			    this.visitedClass, this.methodGen,
			    this.constantPoolGen, invokevirtual);
		    // System.out.println(log);
		}
	    }
	}
    }

    // pass src, det, method, invoke type, look into once or method depending on
    // method
    private void write(Description who, Description whom,
	    INVOKEMehtodProperties method, INVOKEType type) {
	String methodName = method.getMethodName();
	Type[] methoTypes = method.getTypes();
	Description methodsDescription = method.getDescription();
	// System.out.println("\t" + who + "\n\t\t" + whom + "\n\t\t\t"
	// + methodName);
	OPCodeProperties list = null;

	if (type == INVOKEType.SPECIAL) {
	    // System.out.println("!SPECIAL!");
	    if (method.getMethodName().charAt(0) == '<') {
		/*
		 * if a class creates its own object then escape that to avoid
		 * loop
		 */
		if (who.getActualClass().equals(whom.getActualClass())) {
		    return;
		} else {
		    whom.addClassToCalledByClasses(who.getActualClass());
		    list = whom.getOPCodeDescription().getOneTimeUseOnly();
		}
	    } else {
		write(whom, methodsDescription, method, INVOKEType.VIRTUAL);
		// nothing to do so return
		return;
	    }
	} else if (type == INVOKEType.VIRTUAL) {
	    // System.out.println("!VIRTUAL!");
	    SimpleObject simpleObject = methodsDescription
		    .getSimpleObjectByNameAndTypeArgs(methodName, methoTypes);
	    /*
	     * add class name to this method, so that user can know who called
	     * this method
	     */
	    if (simpleObject != null) {
		simpleObject.addClassForMethod(who.getActualClass());
	    }
	    list = whom.getOPCodeDescription().getOtherMethodByNameAndType(
		    methodName, methoTypes);
	} else if (type == INVOKEType.STATIC) {
	    // System.out.println("!STATIC!");
	    whom.addClassToCalledByClasses(who.getActualClass());
	    list = whom.getOPCodeDescription().getOtherMethodByNameAndType(
		    methodName, methoTypes);
	} else if (type == INVOKEType.INTERFACE) {
	    // System.out.println("!INTERFACE!");
	    whom.addClassToCalledByClasses(who.getActualClass());
	    list = whom.getOPCodeDescription().getOtherMethodByNameAndType(
		    methodName, methoTypes);
	}
	// must check iterations
	if (list != null) {
	    for (INVOKEProperties object : list.getObjectCall()) {
		write(object.getMethodCallingFrom().getDescription(), object
			.getMethodCallTo().getDescription(),
			object.getMethodCallTo(), INVOKEType.SPECIAL);
	    }

	    for (INVOKEProperties object : list.getMethodCall()) {
		write(object.getMethodCallingFrom().getDescription(), object
			.getMethodCallTo().getDescription(),
			object.getMethodCallTo(), INVOKEType.VIRTUAL);
	    }

	    for (INVOKEProperties object : list.getStaticCall()) {
		write(object.getMethodCallingFrom().getDescription(), object
			.getMethodCallTo().getDescription(),
			object.getMethodCallTo(), INVOKEType.STATIC);
	    }

	    for (INVOKEProperties object : list.getInterfaceCall()) {
		write(object.getMethodCallingFrom().getDescription(), object
			.getMethodCallTo().getDescription(),
			object.getMethodCallTo(), INVOKEType.INTERFACE);
	    }
	}
    }
}
