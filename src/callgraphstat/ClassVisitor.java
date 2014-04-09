/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraphstat
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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;

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

	public final ClassVisitor copyAll() {
		ClassVisitor classVisitor = new ClassVisitor();
		classVisitor.description = this.description;
		classVisitor.javaClass = this.javaClass;
		classVisitor.constants = this.constants;
		classVisitor.fields = new LinkedHashMap<String, Stack<Object>>();
		Field[] fields = this.javaClass.getFields();
		for (Field field : fields) {
			if (!field.isStatic()) {
				Stack<Object> values = new Stack<Object>();
				Stack<Object> oldValues = this.fields.get(field.getName());
				for (int i = 0; i < oldValues.size(); i++) {
					values.add(oldValues.get(i));
				}

				classVisitor.fields.put(field.getName(), values);
			}
		}
		classVisitor.classReferenceFormat = this.classReferenceFormat;
		return classVisitor;
	}

	// check for other type not description
	public Object getValueFromField(Object targetClass, String fieldName,
			ReferenceType referenceType) {
		Stack<Object> field = null;
		// if target class is gov make sure to get as list and return as gov
		Object value = null;
		if (targetClass != null && targetClass instanceof Description) {
			Description description = (Description) targetClass;
			ClassVisitor classVisitor = description.getClassVisitor();
			field = classVisitor.fields.get(fieldName);
			if (field != null) {
				return field;// return whole stack
				// return field.peek();
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
		return (value == null) ? Static.NULL : value;
	}

	// check for other type not description
	public void addValueToField(Object targetClass, String fieldName,
			Object currentValue, Type type, ReferenceType referenceType,
			boolean isConditions) {
		Stack<Object> field = null;
		if (targetClass != null && targetClass instanceof Description) {
			Description description = (Description) targetClass;
			ClassVisitor classVisitor = description.getClassVisitor();
			field = classVisitor.fields.get(fieldName);
			if (field != null) {
				int size = field.size();
				if (!isConditions) {
					if (!field.isEmpty()) {
						field.clear();
					}
				}
				if (currentValue instanceof Collection) {
					for (Object stackValues : (Collection<?>) currentValue) {
						Object thisValue = Static.verifyTypeFromObjectsToStore(
								stackValues, type, description);
						if (!(field.contains(thisValue))) {
							field.add(thisValue);
						}
					}
				} else {
					if (!(field.contains(currentValue))) {
						field.add(currentValue);
					}
				}
				// if (!(field.contains(currentValue))) {
				// field.add(currentValue);
				// }

				// int size = field.size();
				// for (int i = 0; i < size; i++) {
				// if (field.get(i).hashCode() == currentValue.hashCode()) {
				// field.remove(i);
				// break;
				// }
				// }
				// Object lastValue = (field.isEmpty()) ? null : field.get(field
				// .size() - 1);
				// currentValue = Static.getSingleValueOrGroupOfValues(
				// currentValue, lastValue, 0);
				// field.add(currentValue);

				// field.add(currentValue);
			} else {
				// no need to have copy of Description for Super Class
				if (description.getSuperClassDescription() != null) {
					addValueToField(description.getSuperClassDescription(),
							fieldName, currentValue, type, referenceType,
							isConditions);
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