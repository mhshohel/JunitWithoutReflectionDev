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
package callgraphstat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.AALOAD;
import org.apache.bcel.generic.AASTORE;
import org.apache.bcel.generic.ACONST_NULL;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.BALOAD;
import org.apache.bcel.generic.BASTORE;
import org.apache.bcel.generic.CALOAD;
import org.apache.bcel.generic.CASTORE;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.DALOAD;
import org.apache.bcel.generic.DASTORE;
import org.apache.bcel.generic.DLOAD;
import org.apache.bcel.generic.DSTORE;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.FALOAD;
import org.apache.bcel.generic.FASTORE;
import org.apache.bcel.generic.FLOAD;
import org.apache.bcel.generic.FSTORE;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.IALOAD;
import org.apache.bcel.generic.IASTORE;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.LALOAD;
import org.apache.bcel.generic.LASTORE;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.LDC_W;
import org.apache.bcel.generic.LLOAD;
import org.apache.bcel.generic.LSTORE;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MULTIANEWARRAY;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.PUTSTATIC;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.SALOAD;
import org.apache.bcel.generic.SASTORE;
import org.apache.bcel.generic.StoreInstruction;
import org.apache.bcel.generic.Type;

public final class MethodVisitor extends EmptyVisitor implements
		Comparable<MethodVisitor> {
	private Description description = null;
	private ClassVisitor classVisitor = null;
	private Method method = null;
	private MethodGen methodGen = null;
	private JavaClass javaClass = null;
	private Type[] types = null;
	private ConstantPoolGen constantPoolGen = null;
	private String format = "";
	private String node = "";
	private LocalVariableGen[] localVariableGens = null;
	private LocalVariableTable localVariableTable = null;
	private LocalVariable[] localVariableArray = null;
	// private ExtractMethod extractMethod = null;
	private Stack<Object> temporalVariables;
	// String: key=var name, value=Description
	private Map<String, Stack<Object>> localVariables = null;
	private String castType = null;
	private boolean isStaticCall = false;
	private ExceptionTable exceptionTable = null;
	private Set<String> exceptionClassList = null;
	// keep return type of unknown or restricted objects
	private Class<?> collectionMethodReturnType = null;

	public MethodVisitor(Description description, ClassVisitor classVisitor,
			Method method, MethodGen methodGen) {
		this.description = description;
		this.classVisitor = classVisitor;
		this.method = method;
		this.methodGen = methodGen;
		this.javaClass = this.description.getJavaClass();
		this.constantPoolGen = methodGen.getConstantPool();
		initialize();
		this.format = "\n------------------------------------------------------------------------------------------\nM:"
				+ this.javaClass.getClassName()
				+ ":"
				+ methodGen.getName()
				+ " " + "(%s)%s:%s";
		this.localVariableGens = methodGen.getLocalVariables();
		this.localVariableTable = methodGen
				.getLocalVariableTable(constantPoolGen);
		this.localVariableArray = this.localVariableTable
				.getLocalVariableTable();
		for (LocalVariableGen localVariableGen : this.localVariableGens) {
			if (localVariableGen.getName().equalsIgnoreCase(StaticValues.THIS)) {
				Stack<Object> object = new Stack<Object>();
				object.add(description);
				this.localVariables.put(localVariableGen.getName(), object);
			} else {
				this.localVariables.put(localVariableGen.getName(),
						new Stack<Object>());
			}
		}
	}

	private void initialize() {
		this.types = this.method.getArgumentTypes();
		int length = this.types.length;
		String type = "(";
		for (int i = 0; i < length; i++) {
			type += ((i + 1) == length) ? this.types[i] : this.types[i] + ",";
		}
		type += ")";
		this.node = this.javaClass.getClassName() + "." + this.method.getName()
				+ type + this.method.getReturnType();
		this.temporalVariables = new Stack<Object>();
		this.localVariables = new LinkedHashMap<String, Stack<Object>>();
		this.exceptionTable = this.method.getExceptionTable();
		this.exceptionClassList = new HashSet<String>();
	}

	public final Description getDescription() {
		return this.description;
	}

	public final ClassVisitor getClassVisitor() {
		return this.classVisitor;
	}

	public final MethodGen getMethodGen() {
		return this.methodGen;
	}

	public final Method getMethod() {
		return this.method;
	}

	public final String toString() {
		return this.node;
	}

	public final String getNode() {
		return this.node;
	}

	private boolean isSameType(String classType, String stackType) {
		try {
			classType = classType.replace("[]", "");
			if (!classType.equalsIgnoreCase(stackType)) {
				Class<?> param = Class.forName(classType);
				Class<?> stack = Class.forName(stackType);
				if (param.isAssignableFrom(stack)
						|| stack.isAssignableFrom(param)) {
					return true;
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	@Override
	public int compareTo(MethodVisitor node) {
		return getMethod().getName().compareTo(node.getMethod().getName());
	}

	private boolean visitInstruction(Instruction i) {
		short opcode = i.getOpcode();
		return ((InstructionConstants.INSTRUCTIONS[opcode] != null)
				&& !(i instanceof ConstantPushInstruction) && !(i instanceof ReturnInstruction));
	}

	public Object start(String source, List<Object> params,
			boolean isStaticCall, Set<String> exceptions) {
		this.castType = null;
		// -------------------Initialize Static Values--------------------
		if (!this.description.isVisitedToCheckStaticField) {
			this.description.isVisitedToCheckStaticField = true;
			// set value to the parent, because its for static field
			this.description.getDescriptionByJavaClass(this.javaClass).isVisitedToCheckStaticField = true;
			try {
				List<MethodVisitor> list = this.description
						.getMethodVisitorByName("<clinit>");
				if (list != null && !list.isEmpty() && list.get(0) != null) {
					// TODO Remove me
					StaticValues.err("\t\t\t\t STATIC VALS FOUND: "
							+ this.description);
					this.description
							.getMethodVisitorByName("<clinit>")
							.get(0)
							.start(null, new ArrayList<Object>(), true,
									exceptions);
				}
			} catch (Exception e) {
				StaticValues
						.err("SOME ERROR FOUND: public Object start(String source, List<Object> params, boolean isStaticCall");
			}
		}
		// ------------------------------------------------------------------------------

		// ------------------------Read Parameters----------------------------
		this.isStaticCall = isStaticCall;
		this.temporalVariables = new Stack<Object>();
		Type returnType = this.method.getReturnType();
		int length = params.size();
		// store values into local variable from parameters, note: each variable
		// name of parameters is a local variable
		if (length > 0) {
			int k = -1;
			Stack<Object> object = null;
			for (Entry<String, Stack<Object>> key : this.localVariables
					.entrySet()) {
				k++;
				if (key.getKey().equalsIgnoreCase(StaticValues.THIS)) {
					k--;
				} else {
					if (k < length) {
						object = new Stack<Object>();
						object.add(params.get(k));
						key.setValue(object);
					} else {
						key.setValue(new Stack<Object>());
					}
				}
			}
		}
		// generate parameters for edges
		String target = "";
		String type = "(";
		if (length != 0) {
			String actualParam = "";
			for (int i = 0; i < length; i++) {
				actualParam = (this.description.hasDescription(params.get(i)
						.toString())) ? actualParam = params.get(i).toString()
						: this.types[i].toString();
				type += ((i + 1) == length) ? actualParam : actualParam + ",";
			}
			type += ")";
			target = this.javaClass.getClassName() + "."
					+ this.method.getName() + type
					+ this.method.getReturnType();
		} else {
			target = this.node;
		}
		// ------------------------------------------------------------------------------

		// ------------------------Add Edges--------------------------------
		if (!isStaticCall) {
			if (source != null) {
				Description.addEdge(source, target);
			}
		}
		// ------------------------------------------------------------------------------

		// ---------------------Read Instructions------------------
		StaticValues.err("\t\tSTART METHOD:\n\t\t\t-----" + source
				+ "\n\t\t\t\t--->" + target + "\n");

		if (methodGen.isAbstract() || methodGen.isNative()) {
			return getDescriptionCopy(returnType);
		}

		// InstructionList instructionList = methodGen.getInstructionList();
		for (InstructionHandle ihInstructionHandle = methodGen
				.getInstructionList().getStart(); ihInstructionHandle != null; ihInstructionHandle = ihInstructionHandle
				.getNext()) {
			Instruction i = ihInstructionHandle.getInstruction();
			// TODO: Remove me
			StaticValues.out(i.getName());
			StaticValues.out("\t\tBefore\n-------------------");
			StaticValues.out("\t\tStack: " + this.temporalVariables);
			StaticValues.out("\t\tLocal:" + this.localVariables);
			StaticValues.out("\t\tField:" + this.classVisitor.fields);
			StaticValues.out("\t\tStaticField:"
					+ this.description.getStaticFields());
			if (!visitInstruction(i)) {
				if (i instanceof ConstantPushInstruction || i instanceof LDC
						|| i instanceof LDC_W || i instanceof LDC2_W) {
					this.temporalVariables.add(StaticValues.PRIMITIVE);
					StaticValues.out(StaticValues.PRIMITIVE);
				} else if (i instanceof StoreInstruction) {
					storeValueToLocalVariable((StoreInstruction) i);
				} else if (i instanceof LoadInstruction) {
					loadValueFromLocalVariable((LoadInstruction) i);
				} else if (i instanceof FieldInstruction) {
					fieldValueInstructor((FieldInstruction) i);
				} else if (i instanceof NEWARRAY || i instanceof ANEWARRAY
						|| i instanceof MULTIANEWARRAY) {
					createNewArrayProviderObject();
				} else {
					i.accept(this);
				}
			} else {
				if (i instanceof ACONST_NULL) {
					this.temporalVariables.add(StaticValues.NULL);
				} else if (i instanceof ArrayInstruction) {
					arrayInstructions((ArrayInstruction) i);
				}
			}
			// TODO:Remove me
			StaticValues.out("\t\tAfter\n-------------------");
			StaticValues.out("\t\tStack: " + this.temporalVariables);
			StaticValues.out("\t\tLocal:" + this.localVariables);
			StaticValues.out("\t\tField:" + this.classVisitor.fields);
			StaticValues.out("\t\tStaticField:"
					+ this.description.getStaticFields());
		}
		StaticValues.err("\t\t-----END METHOD:\n\t\t\t" + source
				+ "\n\t\t\t\t--->" + target + "\n");
		// ------------------------------------------------------------------------------
		// -----------FixException---------
		for (String ex : this.exceptionClassList) {
			exceptions.add(ex);
		}
		// only for caller class not for current class (contains throws
		// Exception)
		if (this.exceptionTable != null) {
			String[] excepList = this.exceptionTable.getExceptionNames();
			for (String exc : excepList) {
				exceptions.add(exc);
			}
		}
		// --------------------------------

		// -------------------Initialize ReturnValues--------------------
		// TODO verify return type
		Object value = null;

		if (returnType.toString() != "void") {
			value = (this.temporalVariables != null && !this.temporalVariables
					.isEmpty()) ? this.temporalVariables.pop() : null;
			// TODO:Remove me
			StaticValues.out("RETURN TYPE: " + value);
			if (value != null) {
				// ArrayObjectProvider returnObject = null;
				// if (value instanceof ArrayObjectProvider) {
				// returnObject = (ArrayObjectProvider) value;
				// if (returnObject.getType() == null) {
				// returnObject.setType(returnType.toString());
				// }
				// if (returnType.toString().contains("[]")) {
				// return returnObject;
				// } else {
				// Object object = getSingleDataFromArray(
				// returnObject, returnType.toString(),
				// this.castType);
				// return (object != null) ? object : returnType;
				// }
				// } else {
				return value;
				// }
			}
		}
		// return value will return only Description type values or Same type of
		// Description or Type, whether it contains null, no matter
		return getDescriptionCopy(returnType);
		// ----------------------------------------------------------
	}

	// if no value matched or not found in stack then type should check into
	// Description list
	private Object getDescriptionCopy(Object name) {
		Description description = this.description
				.getDescriptionByClassName(name.toString());
		if (description != null) {
			return description.copy();
		}

		return name;
	}

	private void addToLoaclVariable(String variableName, Object currentValue,
			ReferenceType referenceType) {
		try {
			List<Object> values = this.localVariables.get(variableName);
			if (values != null) {
				values.add(currentValue);
			}
		} catch (Exception e) {
		}
	}

	private Stack<Object> getLocalVariablesByVarialbleName(String variableName) {
		Stack<Object> objects = null;
		try {
			objects = this.localVariables.get(variableName);
		} catch (Exception e) {

		}
		return objects;
	}

	private Object getLocalVariableValueByVarialbleName(String variableName,
			ReferenceType referenceType) {
		Stack<Object> objects = getLocalVariablesByVarialbleName(variableName);
		if (objects != null && !objects.isEmpty()) {
			return objects.peek();
		}
		return null;
	}

	private boolean isCollectionsOrMap(String classWithPackage) {
		try {
			Class<?> cls = Class.forName(classWithPackage);
			if (Collection.class.isAssignableFrom(cls)
					|| Map.class.isAssignableFrom(cls)) {
				// TODO Remove me
				StaticValues.out("\t\t\t\tCollections or Map type: TRUE");
				return true;
			} else {
				// TODO Remove me
				StaticValues.out("\t\t\t\tCollections or Map type: FALSE");
				return false;
			}
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	private void storeValues(String flag, String variableName, Type type,
			ReferenceType referenceType) {
		try {
			Object value = null;
			boolean isArrayType = false;
			boolean isCollectionType = false;
			if (type.toString().contains("[]")) {
				isArrayType = true;
			} else {
				isCollectionType = isCollectionsOrMap(type.toString());
			}
			try {
				value = (this.temporalVariables != null && !this.temporalVariables
						.isEmpty()) ? this.temporalVariables.pop() : null;
				if (isCollectionType) {
					StaticValues.err("List found: " + variableName);
					if (!(value instanceof CollectionObjectProvider)) {
						value = new CollectionObjectProvider();
					}
				} else if (isPrimitiveType(type)) {
					if (!value.toString().equalsIgnoreCase(StaticValues.NULL)) {
						value = StaticValues.PRIMITIVE;
					}
				} else if (value != null
						&& value.toString().equals(StaticValues.NULL)) {
					value = StaticValues.NULL;
				} else {
					if (value != null) {
						if (value.toString().equalsIgnoreCase(
								StaticValues.PRIMITIVE)
								|| value instanceof String) {
							if (isPrimitiveType(type)) {
								value = StaticValues.PRIMITIVE;
							} else {
								value = null;
							}
						} else {
							boolean result = isSameType(type.toString(),
									value.toString());
							if (!result) {
								value = getDescriptionCopy(value);
								if (!isSameType(type.toString(),
										value.toString())) {
									value = getDescriptionCopy(type);
								}
							}
						}
					}
				}
				if (value == null) {
					if (isPrimitiveType(type)) {
						value = StaticValues.PRIMITIVE;
					} else {
						value = getDescriptionCopy(type);
					}
				}
			} catch (Exception e) {
				value = getDescriptionCopy(type);
			}
			switch (flag) {
			case "ASTORE":
				addToLoaclVariable(variableName, value, referenceType);
				break;
			case "PUTFIELD":
				this.classVisitor
						.addValueToField(
								((this.temporalVariables != null && !this.temporalVariables
										.isEmpty()) ? (isSameType(referenceType
										.toString(), this.temporalVariables
										.peek().toString())) ? this.temporalVariables
										.pop() : this.temporalVariables.peek()
										: null), variableName, value,
								referenceType);
				// }
				break;
			case "PUTSTATIC":
				this.description.addValueToStaticField(this.description,
						variableName, value, referenceType);
				break;
			}
			this.castType = null;
			StaticValues.out("STACK: " + this.temporalVariables);
		} catch (Exception e) {
		}
	}

	private void loadValues(String flag, String variableName, Object type,
			ReferenceType referenceType) {
		try {
			Object value = null;
			// boolean isArrayType = type.toString().contains("[]");
			try {
				switch (flag) {
				case "ALOAD":
					value = getLocalVariableValueByVarialbleName(variableName,
							referenceType);
					break;
				case "GETFIELD":
					value = this.classVisitor
							.getValueFromField(
									((this.temporalVariables != null && !this.temporalVariables
											.isEmpty()) ? (isSameType(
											referenceType.toString(),
											this.temporalVariables.peek()
													.toString())) ? this.temporalVariables
											.pop() : this.temporalVariables
											.peek()
											: null), variableName,
									referenceType);
					break;
				case "GETSTATIC":
					value = this.description.getValueFromStaticField(
							this.description, variableName, referenceType);
					break;
				}
			} catch (Exception e) {
				value = type;
			}
			// TODO: verify by array instance and others core factor
			if (value != null) {
				this.temporalVariables.add(value);
			}
			// TODO: may not required
			this.castType = null;
			StaticValues.out("STACK: " + this.temporalVariables);
		} catch (Exception e) {
		}
	}

	private final int getLocalVariablesIndex(int index) {
		for (int i = 0; i < this.localVariableArray.length; i++) {
			if (this.localVariableArray[i].getIndex() == index) {
				return i;
			}
		}
		return -1;
	}

	private void storeValueToLocalVariable(StoreInstruction obj) {
		int index = -1;
		if (obj instanceof ISTORE) {
			index = getLocalVariablesIndex(((ISTORE) obj).getIndex());
		} else if (obj instanceof LSTORE) {
			index = getLocalVariablesIndex(((LSTORE) obj).getIndex());
		} else if (obj instanceof DSTORE) {
			index = getLocalVariablesIndex(((DSTORE) obj).getIndex());
		} else if (obj instanceof FSTORE) {
			index = getLocalVariablesIndex(((FSTORE) obj).getIndex());
		} else if (obj instanceof ASTORE) {
			index = getLocalVariablesIndex(((ASTORE) obj).getIndex());
		}
		try {
			// removeUnknownValueIfPushInstruction(ASTORE.class);
			if (index != -1) {
				String name = this.localVariableGens[index].getName();// localVariable.getName();
				StaticValues.out("STORE: " + name + "   "
						+ this.localVariableGens[index].getType()
						+ "\t\tIs ArrayType: "
						+ obj.getType(constantPoolGen).getClass().isArray());
				storeValues("ASTORE", name,
						this.localVariableGens[index].getType(), null);
				// must remove primitive values, because storeValue not deleted
				// primitive values
				removePrimitiveData();
			}
		} catch (Exception e) {
			StaticValues.err("FOUND IN ASTORE");
		}
	}

	private void loadValueFromLocalVariable(LoadInstruction obj) {
		int index = -1;
		if (obj instanceof ILOAD) {
			index = getLocalVariablesIndex(((ILOAD) obj).getIndex());
		} else if (obj instanceof LLOAD) {
			index = getLocalVariablesIndex(((LLOAD) obj).getIndex());
		} else if (obj instanceof DLOAD) {
			index = getLocalVariablesIndex(((DLOAD) obj).getIndex());
		} else if (obj instanceof FLOAD) {
			index = getLocalVariablesIndex(((FLOAD) obj).getIndex());
		} else if (obj instanceof ALOAD) {
			index = getLocalVariablesIndex(((ALOAD) obj).getIndex());
		}
		try {
			// removeUnknownValueIfPushInstruction(ALOAD.class);
			if (index != -1) {
				String name = this.localVariableGens[index].getName();// localVariable.getName();
				loadValues("ALOAD", name,
						this.localVariableGens[index].getType(), null);
				StaticValues
						.out("LOAD: "
								+ name
								+ "   "
								+ this.localVariableGens[index].getType()
								+ "\t"
								+ ((this.temporalVariables != null && !this.temporalVariables
										.isEmpty()) ? this.temporalVariables
										.peek() : StaticValues.NULL));
			}
		} catch (Exception e) {
			StaticValues.err("FOUND IN ALOAD");
		}
	}

	private void fieldValueInstructor(FieldInstruction obj) {
		try {
			// TODO Remove me
			StaticValues.out("\t\t" + obj.getName() + "   --->   "
					+ obj.getType(constantPoolGen).getSignature());

			StaticValues.out("\t\t" + obj.getFieldName(constantPoolGen));
			StaticValues.out("\t\t" + obj.getFieldType(constantPoolGen));
			if (obj instanceof PUTFIELD) {
				storeValues("PUTFIELD",
						((PUTFIELD) obj).getFieldName(constantPoolGen),
						((PUTFIELD) obj).getFieldType(constantPoolGen),
						((PUTFIELD) obj).getReferenceType(constantPoolGen));
			} else if (obj instanceof GETFIELD) {
				loadValues("GETFIELD",
						((GETFIELD) obj).getFieldName(constantPoolGen),
						((GETFIELD) obj).getFieldType(constantPoolGen),
						((GETFIELD) obj).getReferenceType(constantPoolGen));
			} else if (obj instanceof PUTSTATIC) {
				storeValues("PUTSTATIC",
						((PUTSTATIC) obj).getFieldName(constantPoolGen),
						((PUTSTATIC) obj).getFieldType(constantPoolGen),
						((PUTSTATIC) obj).getReferenceType(constantPoolGen));
			} else if (obj instanceof GETSTATIC) {
				loadValues("GETSTATIC",
						((GETSTATIC) obj).getFieldName(constantPoolGen),
						((GETSTATIC) obj).getFieldType(constantPoolGen),
						((GETSTATIC) obj).getReferenceType(constantPoolGen));
			}
		} catch (Exception e) {
			StaticValues
					.err("FOUND IN private void fieldValueInstructor(FieldInstruction obj)");
		}
	}

	private boolean arrayInstructions(ArrayInstruction obj) {
		if (obj instanceof IALOAD || obj instanceof CALOAD
				|| obj instanceof BALOAD || obj instanceof SALOAD
				|| obj instanceof LALOAD || obj instanceof DALOAD
				|| obj instanceof FALOAD || obj instanceof AALOAD) {
			visitArrayLoad(obj);
			return true;
		} else if (obj instanceof IASTORE || obj instanceof CASTORE
				|| obj instanceof BASTORE || obj instanceof SASTORE
				|| obj instanceof LASTORE || obj instanceof DASTORE
				|| obj instanceof FASTORE || obj instanceof AASTORE) {
			visitArrayStore(obj);
			return true;
		}
		return false;
	}

	private Object getSingleDataFromArray(Object data, String type,
			String castType) {
		// if (data instanceof ArrayObjectProvider) {
		// ArrayObjectProvider value = (ArrayObjectProvider) data;
		// if (value.arrayObjects != null) {
		// // TODO load data by counter or index
		// // Check Cast
		// boolean typeIsArrayType = type.contains("[]");
		// if (castType != null) {
		// return (value.arrayObjects.containsKey(castType)) ?
		// value.arrayObjects
		// .get(castType).object : Description.UNKNOWN;
		// } else if (value.getType().equalsIgnoreCase(
		// type.replace("[]", "").trim())) {
		// if (typeIsArrayType) {
		// return value;
		// } else {
		// Object val = value.arrayObjects.get(type);
		// if (val == null) {
		// val = (value.arrayObjects
		// .containsKey(Description.PRIMITIVE)) ? value.arrayObjects
		// .get(Description.PRIMITIVE).object : null;
		// if (val == null) {
		// // it means it is calling super class type,
		// // return most counted obj
		// if (isSameType(value.getType(),
		// type.replace("[]", "").trim())) {
		// System.out
		// .println(value.mostCountedObjectObject);
		// // return mostly used object
		// val = value.mostCountedObjectObject;
		// }
		// }
		// } else {
		// // val = ((Data) val).object;
		// }
		// return (val != null) ? val
		// : (value.mostCountedObjectObject != null) ?
		// value.mostCountedObjectObject
		// : data;
		// // only value
		// }
		// }
		// // return value.arrayObjects.get(type);
		// }
		// } else if (data.toString().equalsIgnoreCase(Description.NULL)) {
		// return Description.NULL;
		// }
		return data;
	}

	private boolean hasToLoadOnlyValueFromArray = false;
	// count 2 required to store value in array from array
	private int loadCounter = 0;

	private void visitArrayStore(ArrayInstruction obj) {
		try {
			Object dataObject = this.temporalVariables.pop();
			removePrimitiveData();
			Object arrayObjcet = this.temporalVariables.peek();
			// if (arrayObjcet instanceof ArrayObjectProvider) {
			// ArrayObjectProvider arrayObjectProvider = (ArrayObjectProvider)
			// arrayObjcet;
			// // by check data type remove it from stack
			// Object data = null;
			// if (this.hasToLoadOnlyValueFromArray) {
			// if (this.loadCounter == 2) {
			// data = getSingleDataFromArray(dataObject,
			// arrayObjectProvider.getType(), this.castType);
			// }
			// data = dataObject;
			// this.hasToLoadOnlyValueFromArray = false;
			// } else {
			// data = dataObject;
			// }
			// arrayObjectProvider.add(data, this.temporalVariables);
			// }
		} catch (Exception e) {
			StaticValues.err("ERROR IN AASTORE");
			this.hasToLoadOnlyValueFromArray = false;
		}
		this.hasToLoadOnlyValueFromArray = false;
	}

	private void visitArrayLoad(ArrayInstruction obj) {
		try {
			// first remove primitives
			// then load one data only
			removePrimitiveData();
			if (this.loadCounter == 2 || !this.hasToLoadOnlyValueFromArray) {
				this.loadCounter = 0;
			}
			this.hasToLoadOnlyValueFromArray = true;
			this.loadCounter++;
			StaticValues.out("\t\t" + obj.getName() + "   --->   "
					+ obj.getType(constantPoolGen).getSignature());
			StaticValues.out("\t\t" + obj.getType(constantPoolGen));
			//
		} catch (Exception e) {
			StaticValues.err("ERROR IN AALOAD");
		}
	}

	private boolean isPrimitiveType(Type type) {
		if (type == Type.BOOLEAN || type == Type.BYTE || type == Type.CHAR
				|| type == Type.DOUBLE || type == Type.FLOAT
				|| type == Type.INT || type == Type.LONG || type == Type.SHORT
				|| type.equals(Type.STRING) || type.equals(Type.STRINGBUFFER)
				|| type.equals(Type.CLASS)) {
			// || type.equals(Type.OBJECT)) {
			return true;
		}
		return false;
	}

	private boolean isPrimitiveType(String type) {
		switch (type) {
		case "boolean":
		case "byte":
		case "char":
		case "double":
		case "float":
		case "int":
		case "long":
		case "short":
		case "string":
		case "String":
		case "java.lang.String":
		case "Class":
		case "java.lang.Class":
			return true;
		default:
			return false;
		}
	}

	// private int isPrimitiveLastStackValueForParam = 0;

	private List<Object> getParameters(Type[] types, String methodName) {
		// TODO: Must verify it
		List<Object> params = new ArrayList<Object>();
		try {
			// TODO Remove ME its for test
			int length = types.length;
			int c = types.length;
			if (length > 0) {
				Object object = null;
				boolean result = false;
				for (int j = 0; j < length; j++) {
					c--;
					try {
						Type type = types[c];
						Object value = (this.temporalVariables != null && !this.temporalVariables
								.isEmpty()) ? this.temporalVariables.pop()
								: null;
						// if (value != null
						// && value instanceof ArrayObjectProvider) {
						// if (this.hasToLoadOnlyValueFromArray) {
						// value = getSingleDataFromArray(
						// this.temporalVariables.pop(),
						// type.toString(), this.castType);
						// }
						// this.hasToLoadOnlyValueFromArray = false;
						// }

						if (value != null) {
							if (value.toString().equalsIgnoreCase(
									StaticValues.PRIMITIVE)) {
								object = value;
							} else if (value.toString().equalsIgnoreCase(
									StaticValues.NULL)) {
								object = value;
							} else if (isPrimitiveType(type)
									|| value instanceof String) {
								object = StaticValues.PRIMITIVE;
							} else {
								result = isSameType(type.toString(),
										value.toString());
								if (result) {
									object = value;
								} else {
									object = getDescriptionCopy(value);
									if (!isSameType(type.toString(),
											object.toString())) {
										object = getDescriptionCopy(type);
									}
								}
							}
						} else {
							if (isPrimitiveType(type)) {
								object = StaticValues.PRIMITIVE;
							} else {
								object = getDescriptionCopy(type);
							}
						}
						// if (this.temporalVariables != null
						// && !this.temporalVariables.isEmpty()
						// && object.equals(value)) {
						// this.temporalVariables.pop();
						// }
					} catch (Exception e) {
						StaticValues.err("SOME ERROR IN PARAM getParameters()");
						object = getDescriptionCopy(types[c]);
					}
					params.add(object);
				}
			}
			if (params.size() > 1) {
				Collections.reverse(params);
			}
		} catch (Exception e) {
			this.castType = null;
		}
		// TODO Remove me
		StaticValues.out("\t\t\t\tParams:   " + params);
		this.castType = null;
		return params;
	}

	private void createNewArrayProviderObject() {
		removePrimitiveData();
		// this.temporalVariables.add(new ArrayObjectProvider());
	}

	// remove primitive type data from stack until the last value is other type
	private void removePrimitiveData() {
		try {
			if (this.temporalVariables != null
					&& !this.temporalVariables.isEmpty()) {
				if (this.temporalVariables.peek().toString()
						.equalsIgnoreCase(StaticValues.PRIMITIVE)) {
					this.temporalVariables.pop();
					removePrimitiveData();
				}
			}
		} catch (Exception e) {
		}
	}

	private void printObjectInvoke(Type[] types, ReferenceType referenceType,
			String methodName, String flag) {
		// ------------Only To Show-------------
		String type = "(";
		for (int j = 0; j < types.length; j++) {
			type += ((j + 1) == types.length) ? types[j] : types[j] + ",";
		}
		type += ")";
		StaticValues
				.out(String.format(format, flag, referenceType, methodName)
						+ " "
						+ type
						+ "\n------------------------------------------------------------------------------------------\n");
		// -------------------------------------
	}

	private Description getInvokedDescription(String classType) {
		// especially for Interface, it will match the type first to send values
		Description value = null;
		try {
			Object stackObject = (this.temporalVariables != null && !this.temporalVariables
					.isEmpty()) ? this.temporalVariables.pop()
					: StaticValues.NULL;
			String stackType = stackObject.toString();
			if (isSameType(classType, stackType)) {
				classType = stackType;
			}
			if (stackObject instanceof Description) {
				value = (Description) stackObject;
			}
		} catch (Exception e) {
		}
		// no problem to return null, this null value should handle by invoked
		// method
		if (value == null) {
			Object object = getDescriptionCopy(classType);
			if (object instanceof Description) {
				value = (Description) object;
			}
		}
		return value;
	}

	@Override
	public void visitCHECKCAST(CHECKCAST obj) {
		StaticValues.err("CAST");
		StaticValues.out("\t\t" + obj.getName() + "   --->   "
				+ obj.getType(constantPoolGen).getSignature());
		StaticValues.out("\t\t" + obj.getType(constantPoolGen));
		StaticValues.out("\t\t" + obj.getLoadClassType(constantPoolGen));
		this.castType = null;// obj.getType(constantPoolGen).toString();
	}

	@Override
	public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
		try {
			Type[] types = i.getArgumentTypes(constantPoolGen);
			String methodName = i.getMethodName(constantPoolGen);
			// TODO: decide as output requirement
			printObjectInvoke(types, i.getReferenceType(constantPoolGen),
					methodName, "I");
			List<Object> params = getParameters(types, methodName);
			ReferenceType referenceTpe = i.getReferenceType(constantPoolGen);
			// keep value from stack so that it can compare for Collection Type
			Object instance = this.temporalVariables.peek();
			Description description = getInvokedDescription(referenceTpe
					.toString());
			StaticValues.out(referenceTpe.toString());
			MethodVisitor methodVisitor = null;
			Object returnType = null;
			if (description != null) {
				methodVisitor = description.getMethodVisitorByNameAndTypeArgs(
						description, methodName, types, true);
				if (methodVisitor != null) {
					returnType = methodVisitor.start(this.node, params, false,
							this.exceptionClassList);
				}
			}
			createEdgeIfMethodNotFound(description, methodVisitor, this.node,
					referenceTpe.toString(), methodName, params, types);
			if (description == null
					&& isCollectionsOrMap(referenceTpe.toString())) {// code for
				// collection
				// type
				// check for return type, if required, check void or other in
				// return
				StaticValues.err(this.collectionMethodReturnType);

				if (instance instanceof CollectionObjectProvider) {
					addValuesForCollection((CollectionObjectProvider) instance,
							params);
					// if true then keep value at return else not
					if (isPrimitiveType(this.collectionMethodReturnType
							.getName())) {
						returnType = StaticValues.PRIMITIVE;
					} else if (!this.collectionMethodReturnType.getName()
							.equalsIgnoreCase("void")) {
						returnType = null;
					} else {
						// keep values, if return has type means it should
						// return
						// values of current type, make a list of Values a new
						// class, if return type is Array type then make a list
						// of
						// array
					}
					StaticValues.err(returnType);
				}
			}
			if (returnType != null
					&& !returnType.toString().equalsIgnoreCase("void")) {
				this.temporalVariables.add(returnType);
			}
			StaticValues.out("------------------------");
		} catch (Exception e) {
			StaticValues
					.err("SOME ERROR FOUND: public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i)");
		}
		// Verify and remove remaining primitive data
		removePrimitiveData();
	}

	private void addValuesForCollection(CollectionObjectProvider instance,
			List<Object> params) {
		// instance.add(key, value);
		// TODO Auto-generated method stub

	}

	@Override
	public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
		try {
			Type[] types = i.getArgumentTypes(constantPoolGen);
			String methodName = i.getMethodName(constantPoolGen);
			// TODO: decide as output requirement
			printObjectInvoke(types, i.getReferenceType(constantPoolGen),
					methodName, "O");
			List<Object> params = getParameters(types, methodName);
			ReferenceType referenceTpe = i.getReferenceType(constantPoolGen);
			// check Exception class and then keep them
			if (isSameType(referenceTpe.toString(), "java.lang.Exception")) {
				if (!referenceTpe.toString().equalsIgnoreCase(
						"java.lang.Object")) {
					this.exceptionClassList.add(referenceTpe.toString());
				}
			}
			Description description = getInvokedDescription(referenceTpe
					.toString());
			StaticValues.out(referenceTpe.toString());
			MethodVisitor methodVisitor = null;
			Object returnType = null;
			if (description != null) {
				methodVisitor = description.getMethodVisitorByNameAndTypeArgs(
						description, methodName, types, true);
				if (methodVisitor != null) {
					returnType = methodVisitor.start(this.node, params, false,
							this.exceptionClassList);
				}
			}
			createEdgeIfMethodNotFound(description, methodVisitor, this.node,
					referenceTpe.toString(), methodName, params, types);
			// check Exception class and then print edges, print all
			if (isSameType(referenceTpe.toString(), "java.lang.Exception")) {
				for (String ex : this.exceptionClassList) {
					createEdgeIfMethodNotFound(null, null, this.node, ex,
							methodName, params, types);
				}
			}
			if (returnType != null
					&& !returnType.toString().equalsIgnoreCase("void")) {
				this.temporalVariables.add(returnType);
			}
			StaticValues.out("------------------------");
		} catch (Exception e) {
			StaticValues
					.err("SOME ERROR FOUND: public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i)");
		}
		// Verify and remove remaining primitive data
		removePrimitiveData();
	}

	@Override
	public void visitINVOKESPECIAL(INVOKESPECIAL i) {
		try {
			Type[] types = i.getArgumentTypes(constantPoolGen);
			String methodName = i.getMethodName(constantPoolGen);
			// TODO: decide as output requirement
			printObjectInvoke(types, i.getReferenceType(constantPoolGen),
					methodName, "C");
			List<Object> params = getParameters(types, methodName);
			ReferenceType referenceTpe = i.getReferenceType(constantPoolGen);
			// check Exception class and then keep them
			if (isSameType(referenceTpe.toString(), "java.lang.Exception")) {
				if (!referenceTpe.toString().equalsIgnoreCase(
						"java.lang.Object")) {
					this.exceptionClassList.add(referenceTpe.toString());
				}
			}
			Description description = this.description
					.getDescriptionByClassName(referenceTpe.toString());
			MethodVisitor methodVisitor = null;
			if (description != null) {
				Description copiedDescription = null;
				if (methodName.equalsIgnoreCase("<init>")) {
					copiedDescription = description.copy();
				} else {
					copiedDescription = this.description;
				}
				this.temporalVariables.add(copiedDescription);
				// TODO Remove me
				StaticValues.out("STACK: " + this.temporalVariables);
				methodVisitor = copiedDescription
						.getMethodVisitorByNameAndTypeArgs(copiedDescription,
								methodName, types, false);
				if (!this.description.isSuperClassObjectInitiated) {
					if (this.description.getSuperClassDescription().toString()
							.equalsIgnoreCase(copiedDescription.toString())) {
						this.description.isSuperClassObjectInitiated = true;
						this.description
								.setSuperClassDescription(copiedDescription);
					}
				}
				if (methodVisitor != null) {
					methodVisitor.start(this.node, params, this.isStaticCall,
							this.exceptionClassList);
				}
			} else {
				if (isCollectionsOrMap(referenceTpe.toString())) {
					this.temporalVariables.add(new CollectionObjectProvider());
				} else {
					this.temporalVariables.add(referenceTpe);
				}
			}
			createEdgeIfMethodNotFound(description, methodVisitor, this.node,
					referenceTpe.toString(), methodName, params, types);
			StaticValues.out("------------------------");
		} catch (Exception e) {
			StaticValues
					.err("SOME ERROR FOUND: public void visitINVOKESPECIAL(INVOKESPECIAL i) ");
		}
		// Verify and remove remaining primitive data
		removePrimitiveData();
	}

	@Override
	public void visitINVOKESTATIC(INVOKESTATIC i) {
		try {
			Type[] types = i.getArgumentTypes(constantPoolGen);
			String methodName = i.getMethodName(constantPoolGen);
			// TODO: decide as output requirement
			printObjectInvoke(types, i.getReferenceType(constantPoolGen),
					methodName, "S");
			List<Object> params = getParameters(types, methodName);
			ReferenceType referenceTpe = i.getReferenceType(constantPoolGen);
			Description description = this.description
					.getDescriptionByKey(referenceTpe.toString());
			MethodVisitor methodVisitor = null;
			Object returnType = null;
			if (description != null) {
				methodVisitor = description.getMethodVisitorByNameAndTypeArgs(
						description, methodName, types, true);
				if (methodVisitor != null) {
					// do something for array type, try to capture all values
					returnType = methodVisitor.start(this.node, params, false,
							this.exceptionClassList);
				}
			}
			createEdgeIfMethodNotFound(description, methodVisitor, this.node,
					referenceTpe.toString(), methodName, params, types);
			if (returnType != null
					&& !returnType.toString().equalsIgnoreCase("void")) {
				this.temporalVariables.add(returnType);
			}
			StaticValues.out("------------------------");
		} catch (Exception e) {
			StaticValues
					.err("SOME ERROR FOUND: public void visitINVOKESTATIC(INVOKESTATIC i)");
		}
		// Verify and remove remaining primitive data
		removePrimitiveData();
	}

	private Object addValuesForCollection(List<Object> params) {
		StaticValues.out(params);
		return null;
	}

	// generate edges which has no description or method visitor recorded
	private boolean createEdgeIfMethodNotFound(Description description,
			MethodVisitor methodVisitor, String source, String referenceTpe,
			String methodName, List<Object> params, Type[] types) {
		collectionMethodReturnType = null;
		boolean hasDescription = this.description.hasDescription(referenceTpe
				.toString());
		if (description == null) {
			String target = referenceTpe + "." + methodName;
			String type = "(";
			if (params != null && !params.isEmpty()) {
				int length = params.size();
				String actualParam = "";
				for (int i = 0; i < length; i++) {
					actualParam = params.get(i).toString();
					if (actualParam.equalsIgnoreCase(StaticValues.PRIMITIVE)) {
						actualParam = types[i].toString();
					}
					type += ((i + 1) == length) ? actualParam : actualParam
							+ ",";
				}
			}
			type += ")";
			target += type;
			// keep edges that is not part of Description or part of Library
			// Class
			if (!hasDescription) {
				boolean result = false;
				if (methodName.equalsIgnoreCase("<init>")) {
					target += "void";
				} else {
					try {
						// should set new target
						Class<?> clas = Class.forName(referenceTpe.toString());
						java.lang.reflect.Method[] methods = clas.getMethods();
						for (java.lang.reflect.Method method : methods) {
							if (method.getName().equalsIgnoreCase(methodName)) {
								result = true;
								Class<?>[] cTypes = method.getParameterTypes();
								for (int i = 0; i < cTypes.length; i++) {
									if (!cTypes[i].getName().equalsIgnoreCase(
											types[i].toString())) {
										result = false;
										break;
									}
								}
								if (result) {
									this.collectionMethodReturnType = method
											.getReturnType();
									target = method.getDeclaringClass()
											.getName()
											+ "."
											+ methodName
											+ type
											+ this.collectionMethodReturnType;
									for (Class<?> exType : method
											.getExceptionTypes()) {
										this.exceptionClassList.add(exType
												.getName());
									}
									break;
								}
							}
						}
					} catch (ClassNotFoundException e) {
					}
				}
				if (result) {
					Description.addLibraryEdge(source, target);
					return Description.addEdge(source, target);
				} else {
					return false;
				}
			}
			return Description.addEdge(source, target);
		}
		return false;
		// make a return type
	}

	final class Values {
		// make it something to keep data with data type, so that we can know
		// how to what is the data type easily, then we should take all the data
		// from the list, from the value checker we should check if the value is
		// instance of Values, in this way we can retrieve it from the Values
		// object for array, this class is actually used for array type or
		// collection type data.
		private List<Object> values = null;
		private Class<?> classType = null;
		private Class<?> containerClassType = null;
		private boolean isArrayOrCollectionType = false;

		public Values() {
			this.values = new ArrayList<Object>();
		}
	}

	final class CollectionObjectProvider {
		// private Map<String, Data> arrayObjects = new LinkedHashMap<String,
		// Data>();
		private Map<String, Object> values = new LinkedHashMap<String, Object>();
		private String classType = null; // trace counter to get high

		public void add(String key, Object value) {

		}

		public String getArrayType() {
			if (this.classType == null) {
				return null;
			}
			return this.classType;
		}

		public void setType(String classType) {
			this.classType = classType;
		}

		public String getType() {
			return this.classType;
		}
	}

	final class ArrayObjectProvider {
		// change key as object instead of string to keep all objects -- not
		// done
		// here
		private Map<String, Data> arrayObjects = new LinkedHashMap<String, Data>();
		private String arrayType = null; // trace counter to get high
		private int mostCounted = 0;
		private Object mostCountedObjectObject = null;

		public String getArrayType() {
			if (this.arrayType == null) {
				return null;
			}
			return this.arrayType;
		}

		public void setType(String arrayType) {
			this.arrayType = arrayType;
		}

		public String getType() {
			if (this.arrayType == null) {
				return null;
			}
			return this.arrayType.replace("[]", "");
		}

		public void add(Object object, Stack<Object> temp) {
			// check type before add
			try {
				if (object.toString().equalsIgnoreCase(StaticValues.PRIMITIVE)) {
					add(object.toString(), StaticValues.PRIMITIVE);
				} else if (isSameType(getType(), object.toString())) {
					add(object.toString(), object);
				}
				temp.pop();
			} catch (Exception e) {
			}
		}

		private void add(String key, Object value) {
			Data data = this.arrayObjects.get(key);
			if (data == null) {
				data = new Data(value);
				this.arrayObjects.put(key, data);
			} else {
				if (data.object.toString().equalsIgnoreCase(StaticValues.NULL)) {
					data.object = value;
				} else {
					data.counter++;
				}
			}
			if (data.counter > mostCounted) {
				mostCounted = data.counter;
				mostCountedObjectObject = data.object;
			}
		}

		public Object getByKey(String key) {
			Object object = this.arrayObjects.get(key);
			if (object == null) {
				object = StaticValues.NULL;
			}
			return object;
		}

		public ArrayObjectProvider(String arrayType) {
			this.arrayType = arrayType;
		}

		public ArrayObjectProvider() {
		}

		class Data {
			public Object object = null;
			public int counter = 0;

			public Data(Object object) {
				this.object = object;
				counter++;
			}
		}
	}
}