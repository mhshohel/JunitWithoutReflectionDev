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
import java.util.Iterator;
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
	private Map<String, Stack<Object>> fields = null;
	// TODO: Remove me
	private String classReferenceFormat;

	private ClassVisitor() {
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
		// TODO Remove me
		this.classReferenceFormat = "C:" + this.javaClass.getClassName() + "  "
				+ " %s";
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
				if (!isConditions) {
					if (!field.isEmpty()) {
						field.clear();
					}
				}
				if (currentValue instanceof Collection) {
					for (Object stackValues : (Collection<?>) currentValue) {

						if (!(stackValues instanceof GroupOfValues)) {
							Object thisValue = null;
							if (!Static.isCollectionsOrMap(type.toString())) {
								thisValue = Static
										.verifyTypeFromObjectsToStore(
												stackValues, type, description);
							} else {
								thisValue = stackValues;
							}
							if (!Static.containsElementInCollection(field,
									thisValue)) {
								field.add(thisValue);
							}
						} else {
							GroupOfValues gv = (GroupOfValues) stackValues;
							for (Object gvv : gv
									.getAllValues(type, description)) {
								if (!Static.containsElementInCollection(field,
										gvv)) {
									field.add(gvv);
								}
							}
						}
					}
				} else {
					if (!(currentValue instanceof GroupOfValues)) {
						if (!Static.containsElementInCollection(field,
								currentValue)) {
							field.add(currentValue);
						}
					} else {
						GroupOfValues gov = (GroupOfValues) currentValue;
						for (Object govObject : gov.getAllValues(type,
								description)) {
							if (!Static.containsElementInCollection(field,
									govObject)) {
								field.add(govObject);
							}
						}
					}
				}
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

	@Override
	public boolean equals(Object object) {
		if (object instanceof ClassVisitor) {
			ClassVisitor classVisitor = (ClassVisitor) object;
			return this.hashCode() == classVisitor.hashCode();
		}
		return false;
	}

	public Map<String, Stack<Object>> getFields() {
		return this.fields;
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
				return field;
			} else {
				// no need to have copy of Description for Super Class
				if (description.getSuperClassDescription() != null) {
					value = getValueFromField(
							description.getSuperClassDescription(), fieldName,
							referenceType);
				}
			}
		}
		return (value == null) ? Static.NULL : value;
	}

	@Override
	public int hashCode() {
		int fieldHash = 0;
		for (Iterator<String> it = fields.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			fieldHash += key.hashCode();
			Stack<Object> value = fields.get(key);
			for (Object obj : value) {
				if (obj instanceof Description) {
					fieldHash += ((Description) obj).id;
				} else {
					fieldHash += obj.hashCode();
				}
			}
		}
		return fieldHash + classReferenceFormat.hashCode();
	}
}