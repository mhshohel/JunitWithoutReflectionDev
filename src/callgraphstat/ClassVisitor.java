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
package callgraphstat;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

public class ClassVisitor extends EmptyVisitor {
    private JavaClass clazz;
    private ConstantPoolGen constants;
    private String classReferenceFormat;

    public ClassVisitor(JavaClass jc) {
	clazz = jc;
	constants = new ConstantPoolGen(clazz.getConstantPool());
	classReferenceFormat = "C:" + clazz.getClassName() + "  " + " %s";
    }

    public void visitJavaClass(JavaClass jc) {
	jc.getConstantPool().accept(this);
	Field[] fields = jc.getFields();
	Method[] methods = jc.getMethods();
	for (int i = 0; i < methods.length; i++) {
	    String name = methods[i].getName();
	    Method m = methods[i];
	    LocalVariableTable s = m.getLocalVariableTable();
	    // if (name.charAt(0) != '<')
	    methods[i].accept(this);
	    System.out.println("\t\t\t---------------Fin----------------");
	}
    }

    public void visitConstantPool(ConstantPool constantPool) {
	for (int i = 0; i < constantPool.getLength(); i++) {
	    Constant constant = constantPool.getConstant(i);
	    if (constant == null)
		continue;
	    if (constant.getTag() == 7) {
		constantPool.constantToString(constant);
	    }
	}
    }

    @Override
    public void visitMethod(Method method) {
	MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
	MethodVisitor visitor = new MethodVisitor(mg, clazz);
	visitor.start();
    }

    public void start() {
	visitJavaClass(clazz);
    }
}
