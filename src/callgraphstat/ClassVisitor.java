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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;

public final class ClassVisitor extends EmptyVisitor {
	private JavaClass javaClass;
	private ConstantPoolGen constants;
	private String classReferenceFormat;
	private Description description = null;
	public Map<String, Stack<Object>> fields = null;

	// public Map<String, List<Description>> values = new LinkedHashMap<String,
	// List<Description>>();
	// keep static var for each class

	public ClassVisitor() {

	}

	public ClassVisitor(JavaClass javaClass, Description description) {
		this.description = description;
		this.javaClass = javaClass;
		this.constants = new ConstantPoolGen(this.javaClass.getConstantPool());
		this.fields = new LinkedHashMap<String, Stack<Object>>();
		Field[] fields = javaClass.getFields();
		for (Field field : fields) {
			if (field.isStatic()) {
				description.staticFields.put(field.getName(),
						new Stack<Object>());
			} else {
				this.fields.put(field.getName(), new Stack<Object>());
			}
		}
		this.classReferenceFormat = "C:" + this.javaClass.getClassName() + "  "
				+ " %s";
	}

	public final ClassVisitor copy() {
		ClassVisitor classVisitor = new ClassVisitor();
		classVisitor.description = this.description;
		classVisitor.javaClass = this.javaClass;
		classVisitor.constants = this.constants;
		classVisitor.fields = new LinkedHashMap<String, Stack<Object>>();
		Field[] fields = this.javaClass.getFields();
		for (Field field : fields) {
			if (!field.isStatic()) {
				classVisitor.fields.put(field.getName(), new Stack<Object>());
			}
		}
		classVisitor.classReferenceFormat = this.classReferenceFormat;
		return classVisitor;
	}

	public final Stack<Object> getValuesFromField(ClassVisitor classVisitor,
			String fieldName) {
		Stack<Object> fields = classVisitor.fields.get(fieldName);
		if (fields == null) {
			if (classVisitor.description.getSuperClassDescription() != null) {
				fields = getValuesFromField(classVisitor.description
						.getSuperClassDescription().getClassVisitor(),
						fieldName);
			}
		}
		return fields;
	}

	public final Object getValueFromField(ClassVisitor classVisitor,
			String fieldName) {
		Stack<Object> fields = getValuesFromField(classVisitor, fieldName);
		if (fields != null) {
			return fields.peek();
		}
		return fields;
	}

	public void addValueToField(ClassVisitor classVisitor, String fieldName,
			Object value) {
		Stack<Object> fields = getValuesFromField(classVisitor, fieldName);
		if (fields != null) {
			fields.add(value);
		}
	}

	// public void visitConstantPool(ConstantPool constantPool) {
	// for (int i = 0; i < constantPool.getLength(); i++) {
	// Constant constant = constantPool.getConstant(i);
	// if (constant == null)
	// continue;
	// if (constant.getTag() == 7) {
	// constantPool.constantToString(constant);
	// }
	// }
	// }
	//
	// public void start() {
	// // this.javaClass.getConstantPool().accept(this);
	// }
}
