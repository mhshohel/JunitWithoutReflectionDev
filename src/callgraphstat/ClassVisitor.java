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

	// private final Stack<Object> getValuesFromField(ClassVisitor classVisitor,
	// String fieldName) {
	// Stack<Object> fields = null;
	// try {
	// fields = classVisitor.fields.get(fieldName);
	// if (fields == null) {
	// if (classVisitor.description.getSuperClassDescription() != null) {
	// fields = getValuesFromField(classVisitor.description
	// .getSuperClassDescription().getClassVisitor(),
	// fieldName);
	// }
	// }
	// } catch (Exception e) {
	// }
	// return fields;
	// }
	//
	// public final Object getValueFromField(ClassVisitor classVisitor,
	// String fieldName, ReferenceType referenceType, Object object) {
	// boolean isSame = (referenceType.toString()
	// .equalsIgnoreCase(classVisitor.description.getClassName())) ? true
	// : false;
	// Stack<Object> fields = null;
	// try {
	// if (isSame) {
	// fields = getValuesFromField(classVisitor, fieldName);
	// } else {
	// if (referenceType.toString()
	// .equalsIgnoreCase(object.toString())) {
	// if (object instanceof Description) {
	// Description description = (Description) object;
	// fields = description.getClassVisitor()
	// .getValuesFromField(
	// description.getClassVisitor(),
	// fieldName);
	// }
	// }
	// }
	// if (fields != null && !fields.isEmpty()) {
	// return fields.peek();
	// }
	// } catch (Exception e) {
	// }
	// return fields;
	// }

	// public final Object getValueFromFieldByFieldName(String key) {
	// Object value = null;
	// try {
	// Stack<Object> values = this.fields.get(key);
	// if (values != null && !values.isEmpty()) {
	// return values.peek();
	// }
	// } catch (Exception e) {
	// }
	// return value;
	// }

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
		return (value == referenceType.toString()) ? Description.NULL : value;
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

	// public void addValueToField(ClassVisitor classVisitor, String fieldName,
	// Object value, ReferenceType referenceType, Object object) {
	//
	// Stack<Object> field = classVisitor.fields.get(fieldName);
	// field.add(value);
	// // boolean isSame = (referenceType.toString()
	// // .equalsIgnoreCase(classVisitor.description.getClassName())) ? true
	// // : false;
	// // Stack<Object> fields = null;
	// // try {
	// // // if (isSame) {
	// // // fields = getValuesFromField(classVisitor, fieldName);
	// // // } else {
	// // // if (object instanceof Description) {
	// // // Description description = (Description) object;
	// // // fields = description.getClassVisitor().getValuesFromField(
	// // // description.getClassVisitor(), fieldName);
	// // // }
	// // // }
	// // // if (fields != null) {
	// // // fields.add(value);
	// // // }
	// // } catch (Exception e) {
	// // }
	// }

}