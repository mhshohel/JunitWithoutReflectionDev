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
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;

public class ClassVisitor extends EmptyVisitor {
	private JavaClass clazz;
	private ConstantPoolGen constants;
	private String classReferenceFormat;
	private Description description = null;

	// public Map<String, List<Description>> values = new HashMap<String,
	// List<Description>>();
	// keep static var for each class

	public ClassVisitor(JavaClass jc, Description description) {
		this.description = description;
		this.clazz = jc;
		this.constants = new ConstantPoolGen(this.clazz.getConstantPool());
		this.classReferenceFormat = "C:" + this.clazz.getClassName() + "  "
				+ " %s";
	}

	public ClassVisitor copy() {
		ClassVisitor classVisitor = new ClassVisitor(this.clazz,
				this.description);
		// this.values = new HashMap<String, List<Integer>>();
		return classVisitor;
	}

	public ClassVisitor clone() {
		ClassVisitor classVisitor = new ClassVisitor(this.clazz,
				this.description);
		return classVisitor;
	}

	public void visitJavaClass(JavaClass jc) {
		jc.getConstantPool().accept(this);
		Field[] fields = jc.getFields();
		// Method[] methods = jc.getMethods();
		// for (int i = 0; i < methods.length; i++) {
		// String name = methods[i].getName();
		// System.out.println(name);
		// Method m = methods[i];
		// LocalVariableTable s = m.getLocalVariableTable();
		// // if (name.charAt(0) != '<')
		// methods[i].accept(this);
		// System.out.println("\t\t\t---------------Fin----------------");
		// }
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
		// MethodGen mg = new MethodGen(method, clazz.getClassName(),
		// constants);
		// MethodVisitor visitor = new MethodVisitor(mg, clazz,
		// currentWorkingMethod);
		// visitor.print();
		// visitor.start();
		// visitor = null;
		// mg = null;
	}

	public void start() {
		visitJavaClass(this.clazz);
	}
}
