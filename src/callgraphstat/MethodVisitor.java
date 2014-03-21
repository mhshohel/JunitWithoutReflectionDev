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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

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
			if (localVariableGen.getName().equalsIgnoreCase(Description.THIS)) {
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

	public Object start(String source, List<Object> params, boolean isStaticCall) {
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
					System.err.println("\t\t\t\t STATIC VALS FOUND: "
							+ this.description);
					this.description.getMethodVisitorByName("<clinit>").get(0)
							.start(null, new ArrayList<Object>(), true);
				}
			} catch (Exception e) {
				System.err
						.println("SOME ERROR FOUND: public Object start(String source, List<Object> params, boolean isStaticCall");
			}
		}
		// ------------------------------------------------------------------------------

		// ------------------------Read Parameters----------------------------
		this.isStaticCall = isStaticCall;
		this.temporalVariables = new Stack<Object>();
		Type returnType = this.method.getReturnType();
		int length = params.size();
		if (length > 0) {
			int k = -1;
			Stack<Object> object = null;
			for (Entry<String, Stack<Object>> key : this.localVariables
					.entrySet()) {
				k++;
				if (key.getKey().equalsIgnoreCase(Description.THIS)) {
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
		System.err.println("\t\tSTART METHOD:\n\t\t\t-----" + source
				+ "\n\t\t\t\t--->" + target + "\n");

		if (methodGen.isAbstract() || methodGen.isNative()) {
			return returnType;
		}

		// InstructionList instructionList = methodGen.getInstructionList();
		for (InstructionHandle ihInstructionHandle = methodGen
				.getInstructionList().getStart(); ihInstructionHandle != null; ihInstructionHandle = ihInstructionHandle
				.getNext()) {
			Instruction i = ihInstructionHandle.getInstruction();
			// TODO: Remove me
			System.out.println(i.getName());
			System.out.println("\t\tBefore\n-------------------");
			System.out.println("\t\tStack: " + this.temporalVariables);
			System.out.println("\t\tLocal:" + this.localVariables);
			System.out.println("\t\tField:" + this.classVisitor.fields);
			System.out.println("\t\tStaticField:"
					+ this.description.getStaticFields());
			if (!visitInstruction(i)) {
				if (i instanceof ConstantPushInstruction || i instanceof LDC
						|| i instanceof LDC_W || i instanceof LDC2_W) {
					this.temporalVariables.add(Description.PRIMITIVE);
					System.out.println(Description.PRIMITIVE);
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
					this.temporalVariables.add(Description.NULL);
				} else if (i instanceof ArrayInstruction) {
					arrayInstructions((ArrayInstruction) i);
				}
			}
			// TODO:Remove me
			System.out.println("\t\tAfter\n-------------------");
			System.out.println("\t\tStack: " + this.temporalVariables);
			System.out.println("\t\tLocal:" + this.localVariables);
			System.out.println("\t\tField:" + this.classVisitor.fields);
			System.out.println("\t\tStaticField:"
					+ this.description.getStaticFields());
		}
		System.err.println("\t\t-----END METHOD:\n\t\t\t" + source
				+ "\n\t\t\t\t--->" + target + "\n");
		// ------------------------------------------------------------------------------

		// -------------------Initialize ReturnValues--------------------
		// TODO verify return type
		if (returnType.toString() != "void") {
			Object value = (this.temporalVariables != null && !this.temporalVariables
					.isEmpty()) ? this.temporalVariables.pop() : null;
			// TODO:Remove me
			System.out.println("RETURN TYPE: " + value);
			try {
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
					// return value;
					// }
				}
			} catch (Exception e) {
				return returnType;
			}
		}
		return returnType;
		// ----------------------------------------------------------
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
				System.out.println("\t\t\t\tCollections or Map type: TRUE");
				return true;
			} else {
				// TODO Remove me
				System.out.println("\t\t\t\tCollections or Map type: FALSE");
				return false;
			}
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	private void storeValues(String flag, String variableName, Object type,
			ReferenceType referenceType) {
		try {
			Object value = null;
			boolean isArrayType = false;
			if (type.toString().contains("[]")) {
				isArrayType = true;
			} else {
				isArrayType = isCollectionsOrMap(type.toString());
			}
			try {
				if (this.description.getDescriptionByClassName(type.toString()) == null) {
					if (!isArrayType) {
						// if (this.castType != null
						// && this.temporalVariables.peek() instanceof
						// ArrayObjectProvider) {
						// ArrayObjectProvider arrayObjectProvider =
						// (ArrayObjectProvider) this.temporalVariables
						// .peek();
						// value = arrayObjectProvider.arrayObjects
						// .get(this.castType);
						// } else

						{
							if (this.hasToLoadOnlyValueFromArray) {
								value = getSingleDataFromArray(
										this.temporalVariables.pop(),
										type.toString(), this.castType);
								this.hasToLoadOnlyValueFromArray = false;
							} else {
								value = this.temporalVariables.pop();
							}
						}
					} else {
						// if (this.temporalVariables.peek() instanceof
						// ArrayObjectProvider) {
						// String arrayType = ((ArrayObjectProvider)
						// this.temporalVariables
						// .peek()).getArrayType();
						// String currentType = type.toString();
						// if (arrayType == null) {
						// ArrayObjectProvider arrayObjectProvider =
						// (ArrayObjectProvider) this.temporalVariables
						// .pop();
						// arrayObjectProvider.setType(type.toString());
						// value = arrayObjectProvider;
						// } else if (arrayType.replace("[]", "")
						// .equalsIgnoreCase(
						// currentType.replace("[]", ""))) {
						// if (this.hasToLoadOnlyValueFromArray) {
						// value = getSingleDataFromArray(
						// this.temporalVariables.pop(),
						// type.toString(), this.castType);
						// this.hasToLoadOnlyValueFromArray = false;
						// } else {
						// value = this.temporalVariables.pop();
						// }
						// } else {
						// try {
						// Description desOne = this.description
						// .getDescriptionByClassName(arrayType
						// .replace("[]", ""));
						// Description desTwo = this.description
						// .getDescriptionByClassName(currentType
						// .replace("[]", ""));
						// if ((desOne != null && desTwo != null)
						// && (desOne.getActualClass()
						// .isAssignableFrom(desTwo
						// .getActualClass()))
						// || (desTwo.getActualClass()
						// .isAssignableFrom(desOne
						// .getActualClass()))) {
						// value = this.temporalVariables.pop();
						// } else {
						// // value = new ArrayObjectProvider(
						// // type.toString());
						// if (!this.temporalVariables.isEmpty()) {
						// if (isCollectionsOrMap(this.temporalVariables
						// .peek().toString())) {
						// this.temporalVariables.pop();
						// }
						// }
						// }
						// } catch (Exception e) {
						// // value = new ArrayObjectProvider(
						// // type.toString());
						// if (!this.temporalVariables.isEmpty()) {
						// if (isCollectionsOrMap(this.temporalVariables
						// .peek().toString())) {
						// this.temporalVariables.pop();
						// }
						// }
						// }
						// }
						// } else {
						// // value = new ArrayObjectProvider(type.toString());
						// if (!this.temporalVariables.isEmpty()) {
						// if (isCollectionsOrMap(this.temporalVariables
						// .peek().toString())) {
						// this.temporalVariables.pop();
						// }
						// }
						// }
					}
				} else {
					if (this.hasToLoadOnlyValueFromArray) {
						value = getSingleDataFromArray(
								this.temporalVariables.pop(), type.toString(),
								this.castType);
						this.hasToLoadOnlyValueFromArray = false;
					} else {
						value = this.temporalVariables.pop();
					}
				}
				this.hasToLoadOnlyValueFromArray = false;
			} catch (Exception e) {
				value = type;
				this.hasToLoadOnlyValueFromArray = false;
			}
			if (value == null) {
				value = this.temporalVariables.pop();
			}
			switch (flag) {
			case "ASTORE":
				addToLoaclVariable(variableName, value, referenceType);
				break;
			case "PUTFIELD":
			// get most used class and verify by cast
			// if (temporalVariables.peek() instanceof ArrayObjectProvider) {
			// ArrayObjectProvider arrayObjectProvider = (ArrayObjectProvider)
			// temporalVariables
			// .peek();
			// if (isSameType(referenceType.toString().replace("[]", ""),
			// arrayObjectProvider.getType())) {
			// this.temporalVariables.pop();
			// for (Data data : arrayObjectProvider.arrayObjects
			// .values()) {
			// this.classVisitor.addValueToField(
			// this.classVisitor, variableName, value,
			// referenceType, data.object);
			// }
			// }
			// } else

			{
				this.classVisitor
						.addValueToField(
								this.classVisitor,
								variableName,
								value,
								referenceType,
								((this.temporalVariables != null && !this.temporalVariables
										.isEmpty()) ? (isSameType(referenceType
										.toString(), this.temporalVariables
										.peek().toString())) ? this.temporalVariables
										.pop() : this.temporalVariables.peek()
										: null));
			}
				break;
			case "PUTSTATIC":
				this.description.addValueToStaticField(this.description,
						variableName, value, referenceType);
				break;
			}
			this.castType = null;
			System.out.println("STACK: " + this.temporalVariables);
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
				// if (temporalVariables.peek() instanceof ArrayObjectProvider)
				// {
				// ArrayObjectProvider arrayObjectProvider =
				// (ArrayObjectProvider) temporalVariables
				// .peek();
				// if (isSameType(
				// referenceType.toString().replace("[]", ""),
				// arrayObjectProvider.getType())) {
				// this.temporalVariables.pop();
				// value = this.classVisitor
				// .getValueFromField(
				// this.classVisitor,
				// variableName,
				// referenceType,
				// arrayObjectProvider.mostCountedObjectObject);
				// }
				// } else

				{
					value = this.classVisitor
							.getValueFromField(
									this.classVisitor,
									variableName,
									referenceType,
									((this.temporalVariables != null && !this.temporalVariables
											.isEmpty()) ? (isSameType(
											referenceType.toString(),
											this.temporalVariables.peek()
													.toString())) ? this.temporalVariables
											.pop() : this.temporalVariables
											.peek()
											: null));
				}
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
			System.out.println("STACK: " + this.temporalVariables);
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
				System.out.println("STORE: " + name + "   "
						+ this.localVariableGens[index].getType()
						+ "\t\tIs ArrayType: "
						+ obj.getType(constantPoolGen).getClass().isArray());
				storeValues("ASTORE", name,
						this.localVariableGens[index].getType(), null);
				// removePrimitiveData();
			}
		} catch (Exception e) {
			System.err.println("FOUND IN ASTORE");
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
				System.out
						.println("LOAD: "
								+ name
								+ "   "
								+ this.localVariableGens[index].getType()
								+ "\t"
								+ ((this.temporalVariables != null && !this.temporalVariables
										.isEmpty()) ? this.temporalVariables
										.peek() : "CHECK ME"));
			}
		} catch (Exception e) {
			System.err.println("FOUND IN ALOAD");
		}
	}

	private void fieldValueInstructor(FieldInstruction obj) {
		try {
			// TODO Remove me
			System.out.println("\t\t" + obj.getName() + "   --->   "
					+ obj.getType(constantPoolGen).getSignature());

			System.out.println("\t\t" + obj.getFieldName(constantPoolGen));
			System.out.println("\t\t" + obj.getFieldType(constantPoolGen));
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
			System.err
					.println("FOUND IN private void fieldValueInstructor(FieldInstruction obj)");
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
			System.err.println("ERROR IN AASTORE");
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
			System.out.println("\t\t" + obj.getName() + "   --->   "
					+ obj.getType(constantPoolGen).getSignature());
			System.out.println("\t\t" + obj.getType(constantPoolGen));
			//
		} catch (Exception e) {
			System.err.println("ERROR IN AALOAD");
		}
	}

	private boolean isPrimitiveType(Type type) {
		if (type == Type.BOOLEAN || type == Type.BYTE || type == Type.CHAR
				|| type == Type.DOUBLE || type == Type.FLOAT
				|| type == Type.INT || type == Type.LONG || type == Type.SHORT) {
			return true;
		}
		return false;
	}

	// private int isPrimitiveLastStackValueForParam = 0;

	private List<Object> getParameters(Type[] types, String methodName) {
		List<Object> params = new ArrayList<Object>();
		try {
			// TODO Remove ME its for test
			Stack<Object> temp = this.temporalVariables;
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
								.isEmpty()) ? this.temporalVariables.peek()
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

						if (value == null) {
							if (isPrimitiveType(type)) {
								object = Description.PRIMITIVE;
							} else {
								object = type;
							}
						} else if (type.toString().contains("[]")) {
							// if (value instanceof ArrayObjectProvider) {
							// if (((ArrayObjectProvider) value).getType() ==
							// null) {
							// ((ArrayObjectProvider) value).setType(type
							// .toString());
							// }
							// if (isSameType(type.toString(),
							// ((ArrayObjectProvider) value).getType())) {
							// object = value;
							// } else {
							// object = type;
							// }
							// } else

							if (value.toString().equalsIgnoreCase(
									Description.NULL)) {
								object = value;
							} else {
								object = type;
							}
						} else if (isPrimitiveType(type)) {
							if (value.toString().equalsIgnoreCase(
									Description.PRIMITIVE)) {
								object = value;
							} else if (value instanceof Boolean
									|| value instanceof Byte
									|| value instanceof Character
									|| value instanceof Double
									|| value instanceof Float
									|| value instanceof Integer
									|| value instanceof Long
									|| value instanceof Short
									|| value instanceof Number) {
								object = value;
							} else {
								object = Description.PRIMITIVE;
							}
						} else if (type.equals(Type.STRING)
								|| type.equals(Type.STRINGBUFFER)) {
							if (value.toString().equalsIgnoreCase(
									Description.NULL)) {
								object = value;
							} else if (value.toString().equalsIgnoreCase(
									Description.PRIMITIVE)) {
								object = value;
							} else if (value instanceof String) {
								object = Description.PRIMITIVE;
							} else {
								object = Description.PRIMITIVE;
							}
						} else if (type.equals(Type.CLASS)) {
							if (value.toString().equalsIgnoreCase(
									Description.NULL)) {
								object = value;
							} else if (value.toString().equalsIgnoreCase(
									Description.PRIMITIVE)) {
								object = value;
							} else {
								object = Description.PRIMITIVE;
							}
						} else if (type.equals(Type.OBJECT)) {
							object = value;
						} else {
							result = isSameType(type.toString(),
									value.toString());
							if (result) {
								object = value;
							} else {
								if (value.toString().equalsIgnoreCase(
										Description.NULL)) {
									object = value;
								}
							}
						}
						if (this.temporalVariables != null
								&& !this.temporalVariables.isEmpty()
								&& object.equals(value)) {
							this.temporalVariables.pop();
						}
					} catch (Exception e) {
						System.err
								.println("SOME ERROR IN PARAM getParameters()");
						object = types[c];
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
		System.out.println("\t\t\t\tParams:   " + params);
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
						.equalsIgnoreCase(Description.PRIMITIVE)) {
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
		System.out
				.println(String.format(format, flag, referenceType, methodName)
						+ " "
						+ type
						+ "\n------------------------------------------------------------------------------------------\n");
		// -------------------------------------
	}

	private Description getInvokedDescription(String classType) {
		// especially for Interface, it will match the type first to send values
		try {
			Object stackObject = (this.temporalVariables != null && !this.temporalVariables
					.isEmpty()) ? this.temporalVariables.pop()
					: Description.NULL;
			String stackType = stackObject.toString();
			if (isSameType(classType, stackType)) {
				classType = stackType;
			}
			if (stackObject instanceof Description) {
				return (Description) stackObject;
			}
		} catch (Exception e) {
			return null;
		}
		// no problem to return null, this null value should handle by invoked
		// method
		return null;
	}

	@Override
	public void visitCHECKCAST(CHECKCAST obj) {
		System.err.println("CAST");
		System.out.println("\t\t" + obj.getName() + "   --->   "
				+ obj.getType(constantPoolGen).getSignature());
		System.out.println("\t\t" + obj.getType(constantPoolGen));
		System.out.println("\t\t" + obj.getLoadClassType(constantPoolGen));
		this.castType = obj.getType(constantPoolGen).toString();
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
			Description description = getInvokedDescription(referenceTpe
					.toString());
			System.out.println(referenceTpe.toString());
			MethodVisitor methodVisitor = null;
			Object returnType = null;
			if (description != null) {
				methodVisitor = description.getMethodVisitorByNameAndTypeArgs(
						description, methodName, types, true);
				if (methodVisitor != null) {
					returnType = methodVisitor.start(this.node, params, false);
				}
			}
			createEdgeIfMethodNotFound(description, methodVisitor, this.node,
					referenceTpe.toString(), methodName, params);
			if (returnType != null
					&& !returnType.toString().equalsIgnoreCase("void")) {
				this.temporalVariables.add(returnType);
			}
			System.out.println("------------------------");
		} catch (Exception e) {
			System.err
					.println("SOME ERROR FOUND: public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i)");
		}
	}

	@Override
	public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
		try {
			Type[] types = i.getArgumentTypes(constantPoolGen);
			String methodName = i.getMethodName(constantPoolGen);
			// TODO: decide as output requirement
			printObjectInvoke(types, i.getReferenceType(constantPoolGen),
					methodName, "O");
			String cast = this.castType;
			List<Object> params = getParameters(types, methodName);
			ReferenceType referenceTpe = i.getReferenceType(constantPoolGen);
			Description description = getInvokedDescription(referenceTpe
					.toString());
			System.out.println(referenceTpe.toString());
			MethodVisitor methodVisitor = null;
			Object returnType = null;
			if (description != null) {
				methodVisitor = description.getMethodVisitorByNameAndTypeArgs(
						description, methodName, types, true);
				if (methodVisitor != null) {
					returnType = methodVisitor.start(this.node, params, false);
				}
			}
			createEdgeIfMethodNotFound(description, methodVisitor, this.node,
					referenceTpe.toString(), methodName, params);
			if (returnType != null
					&& !returnType.toString().equalsIgnoreCase("void")) {
				this.temporalVariables.add(returnType);
			}
			System.out.println("------------------------");
		} catch (Exception e) {
			System.err
					.println("SOME ERROR FOUND: public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i)");
		}
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
				System.out.println("STACK: " + this.temporalVariables);
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
					methodVisitor.start(this.node, params, this.isStaticCall);
				}
			} else {
				this.temporalVariables.add(i.getReferenceType(constantPoolGen));
			}
			createEdgeIfMethodNotFound(description, methodVisitor, this.node,
					referenceTpe.toString(), methodName, params);
			System.out.println("------------------------");
		} catch (Exception e) {
			System.err
					.println("SOME ERROR FOUND: public void visitINVOKESPECIAL(INVOKESPECIAL i) ");
		}
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
					returnType = methodVisitor.start(this.node, params, false);
				}
			}
			createEdgeIfMethodNotFound(description, methodVisitor, this.node,
					referenceTpe.toString(), methodName, params);
			if (returnType != null
					&& !returnType.toString().equalsIgnoreCase("void")) {
				this.temporalVariables.add(returnType);
			}
			System.out.println("------------------------");
		} catch (Exception e) {
			System.err
					.println("SOME ERROR FOUND: public void visitINVOKESTATIC(INVOKESTATIC i)");
		}
	}

	// generate edges which has no description or method visitor recorded
	private void createEdgeIfMethodNotFound(Description description,
			MethodVisitor methodVisitor, String source, String referenceTpe,
			String methodName, List<Object> params) {
		if (description == null || methodVisitor == null) {
			String target = referenceTpe + "." + methodName;
			String types = "(";
			if (params != null && !params.isEmpty()) {
				int length = params.size();
				String actualParam = "";
				for (int i = 0; i < length; i++) {
					actualParam = params.get(i).toString();
					types += ((i + 1) == length) ? actualParam : actualParam
							+ ",";
				}
			}
			types += ")";
			target += types;
			Description.addEdge(source, target);
		}
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

	// final class ArrayObjectProvider {
	// // change key as object instead of string to keep all objects -- not
	// // done
	// // here
	// private Map<String, Data> arrayObjects = new LinkedHashMap<String,
	// Data>();
	// private String arrayType = null; // trace counter to get high
	// private int mostCounted = 0;
	// private Object mostCountedObjectObject = null;
	//
	// public String getArrayType() {
	// if (this.arrayType == null) {
	// return null;
	// }
	// return this.arrayType;
	// }
	//
	// public void setType(String arrayType) {
	// this.arrayType = arrayType;
	// }
	//
	// public String getType() {
	// if (this.arrayType == null) {
	// return null;
	// }
	// return this.arrayType.replace("[]", "");
	// }
	//
	// public void add(Object object, Stack<Object> temp) {
	// // check type before add
	// try {
	// if (object.toString().equalsIgnoreCase(Description.PRIMITIVE)) {
	// add(object.toString(), Description.PRIMITIVE);
	// } else if (isSameType(getType(), object.toString())) {
	// add(object.toString(), object);
	// }
	// temp.pop();
	// } catch (Exception e) {
	// }
	// }
	//
	// private void add(String key, Object value) {
	// Data data = this.arrayObjects.get(key);
	// if (data == null) {
	// data = new Data(value);
	// this.arrayObjects.put(key, data);
	// } else {
	// if (data.object.toString().equalsIgnoreCase(Description.NULL)) {
	// data.object = value;
	// } else {
	// data.counter++;
	// }
	// }
	// if (data.counter > mostCounted) {
	// mostCounted = data.counter;
	// mostCountedObjectObject = data.object;
	// }
	// }
	//
	// public Object getByKey(String key) {
	// Object object = this.arrayObjects.get(key);
	// if (object == null) {
	// object = Description.NULL;
	// }
	// return object;
	// }
	//
	// public ArrayObjectProvider(String arrayType) {
	// this.arrayType = arrayType;
	// }
	//
	// public ArrayObjectProvider() {
	// }
	//
	// class Data {
	// public Object object = null;
	// public int counter = 0;
	//
	// public Data(Object object) {
	// this.object = object;
	// counter++;
	// }
	// }
	// }
}