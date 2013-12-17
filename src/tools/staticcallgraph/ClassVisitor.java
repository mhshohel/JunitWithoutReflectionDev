/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName ClassVisitor.java
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

import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

public class ClassVisitor extends EmptyVisitor {
    private ConstantPoolGen constants = null;
    private Description entryDescription = null;
    private boolean isGeneratedCode = false;
    private JavaClass javaClass = null;
    private OPCodeDescription opCodeDescription = null;
    private JCallGraph jCallGraph = null;

    public ClassVisitor(JCallGraph jCallGraph, JavaClass jc,
	    Description description, boolean isGeneratedCode) {
	initialize(jCallGraph, jc, description);
	this.constants = new ConstantPoolGen(this.javaClass.getConstantPool());
	this.isGeneratedCode = isGeneratedCode;
    }

    public ClassVisitor(JCallGraph jCallGraph,
	    OPCodeDescription opCodeDescription, Description description,
	    JavaClass jc) {
	initialize(jCallGraph, jc, description);
	this.opCodeDescription = opCodeDescription;
	this.constants = new ConstantPoolGen(this.javaClass.getConstantPool());
    }

    private void initialize(JCallGraph jCallGraph, JavaClass jc,
	    Description description) {
	this.entryDescription = description;
	this.javaClass = jc;
	this.jCallGraph = jCallGraph;
    }

    public void start() {
	visitJavaClass(this.javaClass);
    }

    public void visitJavaClass(JavaClass jc) {
	Method[] methods = jc.getMethods();
	for (int i = 0; i < methods.length; i++) {
	    methods[i].accept(this);
	}
    }

    @Override
    public void visitMethod(Method method) {
	MethodGen methodGen = new MethodGen(method,
		this.javaClass.getClassName(), this.constants);
	MethodVisitor visitor = null;
	if (this.isGeneratedCode) {
	    visitor = new MethodVisitor(this.jCallGraph, this.javaClass,
		    this.entryDescription, methodGen);
	} else {
	    visitor = new MethodVisitor(this.jCallGraph,
		    this.opCodeDescription, this.entryDescription,
		    this.javaClass, methodGen);
	}
	visitor.start();
    }
}
