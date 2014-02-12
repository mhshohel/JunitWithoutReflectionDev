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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;

public class ClassVisitor extends EmptyVisitor {
	private JavaClass javaClass;
	private ConstantPoolGen constants;
	private String classReferenceFormat;
	private Description description = null;
	private Map<String, Stack<Object>> fields = null;

	// public Map<String, List<Description>> values = new HashMap<String,
	// List<Description>>();
	// keep static var for each class

	public ClassVisitor() {

	}

	public ClassVisitor(JavaClass javaClass, Description description) {
		this.description = description;
		this.javaClass = javaClass;
		this.constants = new ConstantPoolGen(this.javaClass.getConstantPool());
		this.fields = new HashMap<String, Stack<Object>>();
		Field[] fields = javaClass.getFields();
		for (Field field : fields) {
			if (field.isStatic()) {
				description.addValueToStaticField(field.getName(),
						new Stack<Object>());
			} else {
				this.fields.put(field.getName(), new Stack<Object>());
			}
		}
		this.classReferenceFormat = "C:" + this.javaClass.getClassName() + "  "
				+ " %s";
	}

	public ClassVisitor copy() {
		ClassVisitor classVisitor = new ClassVisitor();
		classVisitor.description = this.description;
		classVisitor.javaClass = this.javaClass;
		classVisitor.constants = this.constants;
		classVisitor.fields = new HashMap<String, Stack<Object>>();
		Field[] fields = this.javaClass.getFields();
		for (Field field : fields) {
			if (!field.isStatic()) {
				this.fields.put(field.getName(), new Stack<Object>());
			}
		}
		classVisitor.classReferenceFormat = this.classReferenceFormat;

		// classVisitor.staticFields = this.staticFields;
		// this.values = new HashMap<String, List<Integer>>();
		return classVisitor;
	}

	public ClassVisitor clone() {
		ClassVisitor classVisitor = new ClassVisitor(this.javaClass,
				this.description);
		return classVisitor;
	}

	public Stack<Object> getFields(ClassVisitor classVisitor, String fieldName) {
		Stack<Object> fields = classVisitor.fields.get(fieldName);
		if (fields == null) {
			fields = classVisitor.description.getStaticFieldValues(fieldName);
		}
		if (fields == null) {
			if (classVisitor.description.getSuperClassDescription() != null) {
				fields = getFields(classVisitor.description
						.getSuperClassDescription().getClassVisitor(),
						fieldName);
			}
		}
		return fields;
	}

	public void visitJavaClass(JavaClass javaClass) {
		javaClass.getConstantPool().accept(this);
		// Field[] fields = javaClass.getFields();

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
		visitJavaClass(this.javaClass);
	}
}
