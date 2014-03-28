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
import org.apache.bcel.generic.ReferenceType;

public final class ClassVisitor extends EmptyVisitor {
	private JavaClass javaClass;
	private ConstantPoolGen constants;
	private Description description = null;
	// TODO: make me private
	public Map<String, Stack<Object>> fields = null;
	// TODO: remove me
	private String classReferenceFormat;

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
				description.addStaticFieldsValue(field.getName(),
						new Stack<Object>());
			} else {
				this.fields.put(field.getName(), new Stack<Object>());
			}
		}
		// TODO remove me
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

	// check for other type not description
	public Object getValueFromField(Object targetClass, String fieldName,
			ReferenceType referenceType) {
		Stack<Object> field = null;
		Object value = null;
		if (targetClass != null && targetClass instanceof Description) {
			Description description = (Description) targetClass;
			ClassVisitor classVisitor = description.getClassVisitor();
			field = classVisitor.fields.get(fieldName);
			if (field != null) {
				return field.peek();
			} else {
				// no need to have copy of Description for Super Class
				if (description.getSuperClassDescription() != null) {
					value = getValueFromField(
							description.getSuperClassDescription(), fieldName,
							referenceType);
				}
			}
		}
		// return (value == referenceType.toString()) ? StaticValues.NULL :
		// value;
		return (value == null) ? StaticValues.NULL : value;
	}

	// check for other type not description
	public void addValueToField(Object targetClass, String fieldName,
			Object value, ReferenceType referenceType) {
		Stack<Object> field = null;
		if (targetClass != null && targetClass instanceof Description) {
			Description description = (Description) targetClass;
			ClassVisitor classVisitor = description.getClassVisitor();
			field = classVisitor.fields.get(fieldName);
			if (field != null) {
				int size = field.size();
				for (int i = 0; i < size; i++) {
					if (field.get(i).hashCode() == value.hashCode()) {
						field.remove(i);
						break;
					}
				}
				field.add(value);
			} else {
				// no need to have copy of Description for Super Class
				if (description.getSuperClassDescription() != null) {
					addValueToField(description.getSuperClassDescription(),
							fieldName, value, referenceType);
				}
			}
		}
	}

	@Override
	public int hashCode() {
		return fields.hashCode() + classReferenceFormat.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof ClassVisitor) {
			ClassVisitor classVisitor = (ClassVisitor) object;
			return this.hashCode() == classVisitor.hashCode();
		}
		return false;
	}
}