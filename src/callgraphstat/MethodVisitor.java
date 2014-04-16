/**
 *
 * @ProjectName StaticCallGraph
 *
 * @PackageName callgraphstat
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
import org.apache.bcel.generic.ARETURN;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.BALOAD;
import org.apache.bcel.generic.BASTORE;
import org.apache.bcel.generic.CALOAD;
import org.apache.bcel.generic.CASTORE;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.DALOAD;
import org.apache.bcel.generic.DASTORE;
import org.apache.bcel.generic.DLOAD;
import org.apache.bcel.generic.DRETURN;
import org.apache.bcel.generic.DSTORE;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.FALOAD;
import org.apache.bcel.generic.FASTORE;
import org.apache.bcel.generic.FLOAD;
import org.apache.bcel.generic.FRETURN;
import org.apache.bcel.generic.FSTORE;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.IALOAD;
import org.apache.bcel.generic.IASTORE;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.IFGE;
import org.apache.bcel.generic.IFGT;
import org.apache.bcel.generic.IFLE;
import org.apache.bcel.generic.IFLT;
import org.apache.bcel.generic.IFNE;
import org.apache.bcel.generic.IFNONNULL;
import org.apache.bcel.generic.IFNULL;
import org.apache.bcel.generic.IF_ACMPEQ;
import org.apache.bcel.generic.IF_ACMPNE;
import org.apache.bcel.generic.IF_ICMPEQ;
import org.apache.bcel.generic.IF_ICMPGE;
import org.apache.bcel.generic.IF_ICMPGT;
import org.apache.bcel.generic.IF_ICMPLE;
import org.apache.bcel.generic.IF_ICMPLT;
import org.apache.bcel.generic.IF_ICMPNE;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.IRETURN;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.LALOAD;
import org.apache.bcel.generic.LASTORE;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.LDC_W;
import org.apache.bcel.generic.LLOAD;
import org.apache.bcel.generic.LRETURN;
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
import org.apache.bcel.generic.Select;
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
	private boolean isStaticCall = false;
	private ExceptionTable exceptionTable = null;
	private Set<String> exceptionClassList = null;
	// keep return type of unknown or restricted objects
	private Class<?> collectionMethodReturnType = null;
	// keep temp Group value, so that it can close them after each method
	// traverse, it requires so that field can get the flag to store value
	// without group
	private Stack<GroupOfValues> tempGroupValues = new Stack<GroupOfValues>();
	// it will keep alive group values of each variable add field till the end
	// of method
	private Stack<GroupOfValues> tempVariablesGroupValues = new Stack<GroupOfValues>();
	private String source = null;
	private Set<String> exceptions = null;
	private boolean isConditons = false;
	private int conditionLineNumber = -1;
	private int currentLineNumber = -1;
	private Stack<Object> returnValues = new Stack<Object>();

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
			if (localVariableGen.getName().equalsIgnoreCase(Static.THIS)) {
				Stack<Object> object = new Stack<Object>();
				object.add(description);
				this.localVariables.put(localVariableGen.getName(), object);
			} else {
				this.localVariables.put(localVariableGen.getName(),
						new Stack<Object>());
			}
		}
	}

	private void addReturnValues(boolean isPrimitive, Type returnType) {
		try {
			// must remove last value of group or other
			// not save as group or collection
			Object value = getValue(returnType, true);
			if (isPrimitive) {
				addReturnValues(Static.PRIMITIVE);
			} else {
				Static.err("Return Value for Referance Type");
				if (value instanceof Collection<?>) {
					for (Object object : (Collection<?>) value) {
						addReturnValues(object);
					}
				} else {
					addReturnValues(value);
				}
			}
		} catch (Exception e) {
			Static.err("ERROR: addReturnValues");
		}
	}

	private void addReturnValues(Object value) {
		if (!Static.containsElementInCollection(this.returnValues, value)) {
			this.returnValues.add(value);
		}
	}

	private void addToLoaclVariable(String variableName, Object currentValue,
			ReferenceType referenceType, Type type) {
		try {
			List<Object> values = this.localVariables.get(variableName);
			// keep previous value if and only if condition is open, otherwise
			// remove all
			if (values != null) {
				if (!this.isConditons) {
					if (!values.isEmpty()) {
						values.clear();
					}
				}
				// do not save group value
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
							if (!Static.containsElementInCollection(values,
									thisValue)) {
								values.add(thisValue);
							}
						} else {
							GroupOfValues gv = (GroupOfValues) stackValues;
							for (Object gvv : gv
									.getAllValues(type, description)) {
								if (!Static.containsElementInCollection(values,
										gvv)) {
									values.add(gvv);
								}
							}
						}
					}
				} else {
					if (!(currentValue instanceof GroupOfValues)) {
						if (!Static.containsElementInCollection(values,
								currentValue)) {
							values.add(currentValue);
						}
					} else {
						GroupOfValues gv = (GroupOfValues) currentValue;
						for (Object gvv : gv.getAllValues(type, description)) {
							if (!Static
									.containsElementInCollection(values, gvv)) {
								values.add(gvv);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Static.err("ERROR: addToLoaclVariable");
		}
	}

	private void addToTemporalVariable(Object object) {
		try {
			Object lastValue = (!this.temporalVariables.isEmpty()) ? this.temporalVariables
					.peek() : null;
			if (lastValue != null) {
				if (lastValue instanceof GroupOfValues) {
					GroupOfValues gov = (GroupOfValues) lastValue;
					if (gov.isOpen) {
						if (!this.tempGroupValues.isEmpty()) {
							// peek the last group object and add value
							this.tempGroupValues.peek().add(object);
						} else {
							// if somehow miss then it will add value, however,
							// this line will never use
							gov.addAtLast(object);
						}
					} else {
						this.temporalVariables.add(object);
					}
				} else {
					this.temporalVariables.add(object);
				}
			} else {
				this.temporalVariables.add(object);
			}
		} catch (Exception e) {
			Static.err("ERROR in addToTemporalVariable ");
		}
	}

	// add values from params to local variable of Method
	private void addValuesToLocalVariableFromParameters(int length,
			List<Object> params) {
		try {
			if (length > 0) {
				int k = -1;
				Stack<Object> object = null;
				for (Entry<String, Stack<Object>> key : this.localVariables
						.entrySet()) {
					k++;
					if (key.getKey().equalsIgnoreCase(Static.THIS)) {
						k--;
					} else {
						if (k < length) {
							object = new Stack<Object>();
							if (params.get(k) instanceof Collection<?>) {
								for (Object obj : (Collection<?>) params.get(k)) {
									object.add(obj);
								}
							} else {
								object.add(params.get(k));
							}
							key.setValue(object);
						} else {
							key.setValue(new Stack<Object>());
						}
					}
				}
			}
		} catch (Exception e) {
			Static.err("ERROR: addValuesToLocalVariableFromParameters");
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

	@Override
	public int compareTo(MethodVisitor node) {
		return getMethod().getName().compareTo(node.getMethod().getName());
	}

	// generate edges which has no description or method visitor recorded
	private boolean createEdgeIfMethodNotFound(Description description,
			MethodVisitor methodVisitor, String source, String referenceType,
			String methodName, List<Object> params, Type[] types,
			boolean isCollectionType, Object collectionVariableObject) {
		this.collectionMethodReturnType = null;
		boolean hasDescription = this.description.hasDescription(referenceType
				.toString());
		if (description == null) {
			String target = referenceType + "." + methodName;
			String type = "(";
			if (params != null && !params.isEmpty()) {
				int length = params.size();
				Object param = null;
				String actualParam = "";
				for (int i = 0; i < length; i++) {
					param = params.get(i);
					if (param instanceof Collection<?>) {
						if (((Collection<?>) param).size() == 1) {
							param = ((List<?>) param).get(0);
						} else {
							param = type;
						}
					}
					actualParam = param.toString();
					if (actualParam.equalsIgnoreCase(Static.PRIMITIVE)) {
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
					result = true;
				} else {
					try {
						// should set new target
						Class<?> clas = Class.forName(referenceType.toString());
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
						Static.err("ERROR in createEdgeIfMethodNotFound ");
					}
				}
				if (this.collectionMethodReturnType != null
						&& !(this.collectionMethodReturnType.toString()
								.equalsIgnoreCase("void"))) {
					if (Static
							.isPrimitiveTypeString(this.collectionMethodReturnType
									.toString())) {
						addToTemporalVariable(Static.PRIMITIVE);
					} else {
						if (isCollectionType
								&& collectionVariableObject != null) {
							addToTemporalVariable(collectionVariableObject);
						} else {
							addToTemporalVariable(this.collectionMethodReturnType);
						}
					}
				}
				if (result) {
					Static.addLibraryEdge(source, target, referenceType);
					return Static.addEdge(source, target);
				} else {
					return false;
				}
			}
			return Static.addEdge(source, target);
		}
		return false;
		// make a return type
	}

	private void createNewArrayProviderObject() {
		try {
			// removePrimitiveData();
			// this.temporalVariables.add(new ArrayObjectProvider());
		} catch (Exception e) {
			Static.err("ERROR: createNewArrayProviderObject");
		}
	}

	private void fieldValueInstructor(FieldInstruction obj) {
		try {
			// TODO Remove me
			Static.out("\t\t" + obj.getName() + "   --->   "
					+ obj.getType(constantPoolGen).getSignature());

			Static.out("\t\t" + obj.getFieldName(constantPoolGen));
			Static.out("\t\t" + obj.getFieldType(constantPoolGen));

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
			Static.err("ERROR: fieldValueInstructor");
		}
	}

	// remove value(s) that contains by if, if can one value or twos depending
	// of category
	private void fixIfConsitions(Instruction i) {
		try {
			if (i instanceof IF_ACMPEQ || i instanceof IF_ACMPNE
					|| i instanceof IF_ICMPEQ || i instanceof IF_ICMPGE
					|| i instanceof IF_ICMPGT || i instanceof IF_ICMPLE
					|| i instanceof IF_ICMPLT || i instanceof IF_ICMPNE) {
				// remove last two values from stack
				for (int j = 0; j < 2; j++) {
					if (!temporalVariables.isEmpty()) {
						if (this.temporalVariables.peek() instanceof GroupOfValues) {
							if (!this.tempGroupValues.isEmpty()) {
								GroupOfValues gov = this.tempGroupValues.peek();
								gov.pop();
							}
						} else {
							this.temporalVariables.pop();
						}
					}
				}
			} else if (i instanceof IFEQ || i instanceof IFGE
					|| i instanceof IFGT || i instanceof IFLE
					|| i instanceof IFLT || i instanceof IFNE
					|| i instanceof IFNONNULL || i instanceof IFNULL) {
				// remove one value from stack
				if (!temporalVariables.isEmpty()) {
					if (this.temporalVariables.peek() instanceof GroupOfValues) {
						if (!this.tempGroupValues.isEmpty()) {
							GroupOfValues gov = this.tempGroupValues.peek();
							gov.pop();
						}
					} else {
						this.temporalVariables.pop();
					}
				}
			}
		} catch (Exception e) {
			Static.err("ERROR: fixIfConsitions");
		}
	}

	public final ClassVisitor getClassVisitor() {
		return this.classVisitor;
	}

	public final Description getDescription() {
		return this.description;
	}

	private Stack<Description> getInvokedDescription(ReferenceType type) {
		// especially for Interface, it will match the type first to send values
		String classType = type.toString();
		Stack<Description> values = new Stack<Description>();
		GroupOfValues gov = null;
		try {
			Object value = (this.temporalVariables != null && !this.temporalVariables
					.isEmpty()) ? this.temporalVariables.peek() : Static.NULL;

			if (value instanceof GroupOfValues) {
				gov = (GroupOfValues) value;
				if (gov.isOpen) {
					// not close get last group and marge
					if (!this.tempGroupValues.isEmpty()) {
						value = this.tempGroupValues.peek().pop();
						if (value instanceof GroupOfValues) {
							// get all values including child
							value = ((GroupOfValues) value).getAllValues(type,
									this.description);
						} else if (value instanceof Collection) {
							Stack<Object> allValues = new Stack<Object>();
							for (Object stackValues : (Collection<?>) value) {
								Object thisValue = Static
										.verifyTypeFromObjectsToStore(
												stackValues, type, description);
								if (!Static.containsElementInCollection(
										allValues, thisValue)) {
									allValues.add(thisValue);
								}
							}
							value = allValues;
						}
					}
				} else {
					// if close
					value = gov.getAllValues(type, this.description);
					this.temporalVariables.pop();
				}
			} else {
				this.temporalVariables.pop();
			}
			if (!Static.someValues.isEmpty()) {
				Static.someValues.clear();
			}
			if (value instanceof Collection) {
				for (Object stackObject : (Collection<?>) value) {
					String stackType = stackObject.toString();
					if (Static.isSameType(classType, stackType)) {
						if (stackObject instanceof Description) {
							values.add((Description) stackObject);
						} else {
							Static.someValues.add(stackObject);
						}
					}
				}
			} else {
				if (value instanceof Description) {
					values.add((Description) value);
				} else {
					Static.someValues.add(value);
				}
			}

		} catch (Exception e) {
			Static.err("ERROR: getInvokedDescription");
		}
		return values;
	}

	private Stack<Object> getLocalVariablesByVarialbleName(String variableName) {
		Stack<Object> objects = null;
		try {
			objects = this.localVariables.get(variableName);
		} catch (Exception e) {
			Static.err("ERROR: getLocalVariablesByVarialbleName");
		}
		return objects;
	}

	private final LocalVariableGen getLocalVariablesName(int index) {
		LocalVariableGen localVariableGen = null;
		// keep the line number for same indexed variable
		List<Integer> list = new ArrayList<Integer>();
		String name = null;
		int currentIndex = -1;
		int size = this.localVariableArray.length;
		LocalVariable currentVariable = null;
		try {
			for (int i = 0; i < size; i++) {
				currentVariable = this.localVariableArray[i];
				currentIndex = currentVariable.getIndex();
				if (currentIndex == index) {
					if (currentVariable.getStartPC() == this.currentLineNumber) {
						name = currentVariable.getName();
						break;
					} else {
						list.add(currentVariable.getStartPC());
						// have to be less than the end_pc
						list.add(currentVariable.getStartPC()
								+ currentVariable.getLength() - 1);
					}
					if (list.size() < 3) {
						if (i + 1 < size) {
							if (this.localVariableArray[i + 1].getIndex() != index) {
								name = currentVariable.getName();
								break;
							}
						} else {
							name = currentVariable.getName();
							break;
						}
					}
				} else if (currentIndex > index) {
					break;
				}
			}
			if (!list.isEmpty() && list.size() != 1 && name == null) {
				Collections.sort(list);
			}
			if (name == null) {
				if (list.size() == 1) {
					name = this.localVariableTable.getLocalVariable(index,
							list.get(0)).getName();
				} else {
					LocalVariable lv = null;
					for (int val : list) {
						if (val > this.currentLineNumber) {
							lv = this.localVariableTable.getLocalVariable(
									index, val);
							if (lv != null) {
								name = lv.getName();
								break;
							}
						}
					}
				}
			}
			Static.err(name);
			for (LocalVariableGen lvg : this.localVariableGens) {
				if (lvg.getName().equalsIgnoreCase(name)) {
					localVariableGen = lvg;
					break;
				}
			}
		} catch (Exception e) {
			Static.err("ERROR: getLocalVariablesName");
		}
		return localVariableGen;
	}

	private Object getLocalVariableValueByVarialbleName(String variableName,
			ReferenceType referenceType) {
		Stack<Object> objects = getLocalVariablesByVarialbleName(variableName);
		if (objects != null && !objects.isEmpty()) {
			if (variableName.equalsIgnoreCase("this")) {
				return objects.peek();
			}
			return objects;
		}
		return null;
	}

	public final Method getMethod() {
		return this.method;
	}

	public final MethodGen getMethodGen() {
		return this.methodGen;
	}

	public final String getNode() {
		return this.node;
	}

	private List<Object> getParameters(Type[] types, String methodName) {
		List<Object> params = new ArrayList<Object>();
		try {
			int length = types.length;
			int c = types.length;
			if (length > 0) {
				for (int j = 0; j < length; j++) {
					c--;
					Type type = types[c];
					Object value = getValue(type, true);
					params.add(value);
				}
			}
			if (params.size() > 1) {
				Collections.reverse(params);
			}
		} catch (Exception e) {
			Static.err("ERROR: getParameters");
		}
		// TODO Remove me
		Static.out("\t\t\t\tParams:   " + params);
		return params;
	}

	private Object getTargetClass(Type type) {
		Object target = null;
		try {
			if (this.temporalVariables != null
					&& !this.temporalVariables.isEmpty()) {
				if (!((this.temporalVariables.peek()) instanceof GroupOfValues)) {
					return this.temporalVariables.pop();
				} else {
					GroupOfValues gov = (GroupOfValues) this.temporalVariables
							.peek();
					if (gov.isOpen) {
						if (!this.tempGroupValues.isEmpty()) {
							target = this.tempGroupValues.peek().pop();
							if (target instanceof GroupOfValues) {
								// assume if group found then that will be
								// close.
								// So, pop that group and then pop another to
								// get
								// target. It's because if there is a group
								// found
								// that will be collected as value of that
								// target,
								// so, it will be empty that we don not need to
								// take
								target = this.tempGroupValues.peek().pop();
							}
						} else {
							target = null;
						}
					} else {
						this.temporalVariables.pop();
						if (!((this.temporalVariables.peek()) instanceof GroupOfValues)) {
							return this.temporalVariables.pop();
						}
					}
				}
			}
		} catch (Exception e) {
			Static.err("ERROR: getTargetClass");
		}
		return target;
	}

	@SuppressWarnings("unchecked")
	private Object getValue(Type type, boolean shouldPOP) {
		// to store pass stack or group value that will converted to stack
		// only, no gov directly
		Object value = null;
		boolean isCollectionType = Static.isCollectionsOrMap(type.toString());
		GroupOfValues gov = null;
		try {
			value = (this.temporalVariables != null && !this.temporalVariables
					.isEmpty()) ? this.temporalVariables.peek() : null;
			if (value instanceof GroupOfValues) {
				gov = (GroupOfValues) value;
				if (gov.isOpen) {
					// not close get last group and merge
					if (!this.tempGroupValues.isEmpty()) {
						if (shouldPOP) {
							value = this.tempGroupValues.peek().pop();
						} else {
							value = this.tempGroupValues.peek().peek();
						}
						if (Static.isPrimitiveType(type)) {
							value = Static.PRIMITIVE;
						} else {
							if (value instanceof GroupOfValues) {
								// get all values including child
								value = ((GroupOfValues) value).getAllValues(
										type, this.description);
							} else if (value instanceof Collection) {
								if (!((Stack<Object>) value).isEmpty()) {
									if (!((Stack<Object>) value).get(0)
											.toString().contains("[]")) {
										if (!isCollectionType) {
											Stack<Object> allValues = new Stack<Object>();
											for (Object stackValues : (Collection<?>) value) {
												Object thisValue = Static
														.verifyTypeFromObjectsToStore(
																stackValues,
																type,
																description);
												if (!Static
														.containsElementInCollection(
																allValues,
																thisValue)) {
													allValues.add(thisValue);
												}
											}
											value = allValues;
										}
									}
								}
							}
						}
					} else {
						value = gov.getAllValues(type, this.description);
					}
				} else {
					// if close
					// if close merge all data
					if (Static.isPrimitiveType(type)) {
						value = Static.PRIMITIVE;
					} else {
						value = gov.getAllValues(type, this.description);
					}
					if (shouldPOP) {
						this.temporalVariables.pop();
					}
				}
			} else {
				if (shouldPOP) {
					this.temporalVariables.pop();
				}
			}

			if (value instanceof List) {
				if (((List<?>) value).isEmpty() || ((List<?>) value) == null) {
					value = Static.NULL;
				}
			} else {
				value = Static.verifyTypeFromObjectsToStore(value, type,
						this.description);
			}
			if (value == null) {
				if (Static.isPrimitiveType(type)) {
					value = Static.PRIMITIVE;
				} else {
					value = Static.getDescriptionCopy(this.description, type);
				}
			}
		} catch (Exception e) {
			value = Static.getDescriptionCopy(this.description, type);
			if (Static.isPrimitiveTypeString(value.toString())) {
				value = Static.PRIMITIVE;
			}
			Static.err("SOLVED: getValue; for any exception its solved inside the exception");
		}
		return value;
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

	private void initializeStaticFields(Description description, String source,
			Set<String> exceptions, boolean forGetStatic) {
		try {
			// -------------------Initialize Static Values--------------------
			if (!description.isVisitedToCheckStaticField) {
				description.isVisitedToCheckStaticField = true;
				// set value to the parent, because its for static field
				description.getDescriptionByClassName(description.toString()).isVisitedToCheckStaticField = true;
				try {
					List<MethodVisitor> list = description
							.getMethodVisitorByName("<clinit>");
					if (list != null && !list.isEmpty() && list.get(0) != null) {
						// TODO Remove me
						Static.err("\t\t\t\t STATIC VALS FOUND: " + description);
						description
								.getMethodVisitorByName("<clinit>")
								.get(0)
								.start((source != null) ? source
										: description.getClassName(),
										new ArrayList<Object>(), true,
										exceptions, false);
					} else if (forGetStatic) {
						if (description.getSuperClassDescription() != null) {
							initializeStaticFields(
									description.getSuperClassDescription(),
									source, exceptions, forGetStatic);
						}
					}
				} catch (Exception e) {
					Static.err("SOME ERROR FOUND: public Object start(String source, List<Object> params, boolean isStaticCall");
				}
			}
			// ------------------------------------------------------------------------------
		} catch (Exception e) {
			Static.err("ERROR: initializeStaticFields");
		}
	}

	@SuppressWarnings("unchecked")
	private void invokedVirtualAndInterface(String flag, InvokeInstruction i) {
		// TODO: Remove flag at the end of everything, this flag is just to
		// Print I, O, C, M
		try {
			boolean isCollectionType = false;
			Type[] types = i.getArgumentTypes(constantPoolGen);
			String methodName = i.getMethodName(constantPoolGen);
			// TODO: decide as output requirement
			printObjectInvoke(types, i.getReferenceType(constantPoolGen),
					methodName, flag);
			List<Object> params = getParameters(types, methodName);
			ReferenceType referenceType = i.getReferenceType(constantPoolGen);
			// -------------For Collection or Map ----------------------
			isCollectionType = Static.isCollectionsOrMap(referenceType
					.toString());
			Stack<Object> cols = null;
			if (isCollectionType) {
				Object obj = getValue(referenceType, false);
				if (obj instanceof Collection) {
					cols = (Stack<Object>) obj;
					for (Object para : params) {
						if (!Static.containsElementInCollection(cols, para)) {
							if (para instanceof Collection<?>) {
								for (Object par : (Collection<?>) para) {
									if (!Static.containsElementInCollection(
											cols, par)) {
										cols.add(par);
									}
								}
							} else {
								if (!Static.containsElementInCollection(cols,
										para)) {
									cols.add(para);
								}
							}
						}
					}
				}
			}
			// -------------------------------------------------
			// check Exception class and then keep them
			if (Static.isSameType(referenceType.toString(),
					"java.lang.Exception")) {
				if (!referenceType.toString().equalsIgnoreCase(
						"java.lang.Object")) {
					this.exceptionClassList.add(referenceType.toString());
				}
			}
			List<Description> listDescriptions = getInvokedDescription(referenceType);
			if (listDescriptions != null && !listDescriptions.isEmpty()) {
				Stack<Object> allReturnValues = new Stack<Object>();
				Object returnType = null;
				for (Description des : listDescriptions) {
					if (!des.toString().equalsIgnoreCase(Static.NULL)) {
						Description description = des;// getInvokedDescription(referenceType);
						Static.out(referenceType.toString());
						MethodVisitor methodVisitor = null;
						if (description != null) {
							methodVisitor = description
									.getMethodVisitorByNameAndTypeArgs(
											description, methodName, types,
											true);
							if (methodVisitor != null) {
								returnType = methodVisitor.start(this.node,
										params, false, this.exceptionClassList,
										false);
							}
						}
						// check Exception class and then print edges, print all
						if (Static.isSameType(referenceType.toString(),
								"java.lang.Exception")) {
							for (String ex : this.exceptionClassList) {
								createEdgeIfMethodNotFound(null, null,
										this.node, ex, methodName, params,
										types, false, null);
							}
						}
						if (returnType != null
								&& !returnType.toString().equalsIgnoreCase(
										"void")) {
							// this.temporalVariables.add(returnType);
							if (returnType instanceof Collection<?>) {
								for (Object obj : (Collection<?>) returnType) {
									if (!Static.containsElementInCollection(
											allReturnValues, obj)) {
										allReturnValues.add(obj);
									}
								}
							} else {
								if (!Static.containsElementInCollection(
										allReturnValues, returnType)) {
									allReturnValues.add(returnType);
								}
							}
						}
						Static.out("------------------------");
					}
					if (returnType != null
							&& !returnType.toString().equalsIgnoreCase("void")) {
						addToTemporalVariable(allReturnValues);
					}
				}
			} else {
				if (!Static.someValues.isEmpty()) {
					while (!Static.someValues.isEmpty()) {
						createEdgeIfMethodNotFound(null, null, this.node,
								Static.someValues.pop().toString(), methodName,
								params, types, isCollectionType, cols);
					}
				}
			}
			if (!Static.someValues.isEmpty()) {
				Static.someValues.clear();
			}
		} catch (Exception e) {
			Static.err("SOME ERROR FOUND: public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i)");
		}
		// Verify and remove remaining primitive data
		removePrimitiveData();
	}

	private void loadValueFromLocalVariable(LoadInstruction obj) {
		try { // int index = -1;
			LocalVariableGen localVariableGen = null;
			if (obj instanceof ILOAD) {
				localVariableGen = getLocalVariablesName(((ILOAD) obj)
						.getIndex());
			} else if (obj instanceof LLOAD) {
				localVariableGen = getLocalVariablesName(((LLOAD) obj)
						.getIndex());
			} else if (obj instanceof DLOAD) {
				localVariableGen = getLocalVariablesName(((DLOAD) obj)
						.getIndex());
			} else if (obj instanceof FLOAD) {
				localVariableGen = getLocalVariablesName(((FLOAD) obj)
						.getIndex());
			} else if (obj instanceof ALOAD) {
				localVariableGen = getLocalVariablesName(((ALOAD) obj)
						.getIndex());
			}
			if (localVariableGen != null) {
				loadValues("ALOAD", localVariableGen.getName(),
						localVariableGen.getType(), null);
				Object refObj = ((this.temporalVariables != null && !this.temporalVariables
						.isEmpty()) ? this.temporalVariables.peek()
						: Static.NULL);
				// loaded value can be stack
				Static.out("LOAD: " + localVariableGen.getName() + "   "
						+ localVariableGen.getType() + "\t" + refObj);
			}
		} catch (Exception e) {
			Static.err("ERROR: loadValueFromLocalVariable");
		}
	}

	private void loadValues(String flag, String variableName, Type type,
			ReferenceType referenceType) {
		try {
			Object value = null;
			try {
				Object targetClass = null;
				switch (flag) {
				case "ALOAD":
					value = getLocalVariableValueByVarialbleName(variableName,
							referenceType);
					break;
				case "GETFIELD":
					targetClass = getTargetClass(type);
					Static.err("Targets PULL = " + targetClass);
					Stack<Object> values = new Stack<Object>();
					if (targetClass != null) {
						if (targetClass instanceof Collection<?>) {
							for (Object object : (Collection<?>) targetClass) {
								value = this.classVisitor.getValueFromField(
										object, variableName, referenceType);
								if (value instanceof Collection<?>) {
									for (Object val : (Collection<?>) value) {
										if (!Static
												.containsElementInCollection(
														values, val)) {
											values.add(val);
										}
									}
								} else {
									if (!Static.containsElementInCollection(
											values, value)) {
										values.add(value);
									}
								}
							}
							value = values;
						} else {
							value = this.classVisitor.getValueFromField(
									targetClass, variableName, referenceType);
						}
					}
					break;
				case "GETSTATIC":
					Description description = this.description
							.getDescriptionByClassName(referenceType.toString());
					if (description != null) {
						// get the actual link
						initializeStaticFields(
								description,
								(this.source != null) ? this.source : this.node,
								this.exceptions, true);

						value = this.description.getValueFromStaticField(
								description, variableName, referenceType);
					} else {
						value = type;
					}
					break;
				}
			} catch (Exception e) {
				value = type;
			}
			if (value != null) {
				addToTemporalVariable(value);
			}
			Static.out("STACK: " + this.temporalVariables);
		} catch (Exception e) {
			Static.err("ERROR: loadValues");
		}
	}

	// TODO: REMOVE ME - I am for PRINT
	private void printObjectInvoke(Type[] types, ReferenceType referenceType,
			String methodName, String flag) {
		// ------------Only To Show-------------
		String type = "(";
		for (int j = 0; j < types.length; j++) {
			type += ((j + 1) == types.length) ? types[j] : types[j] + ",";
		}
		type += ")";
		Static.out(String.format(format, flag, referenceType, methodName)
				+ " "
				+ type
				+ "\n------------------------------------------------------------------------------------------\n");
		// -------------------------------------
	}

	// remove primitive type data from stack until the last value is other type
	private void removePrimitiveData() {
		try {
			if (this.temporalVariables != null
					&& !this.temporalVariables.isEmpty()) {
				if (this.temporalVariables.peek().toString()
						.equalsIgnoreCase(Static.PRIMITIVE)) {
					this.temporalVariables.pop();
					removePrimitiveData();
				}
			}
		} catch (Exception e) {
			Static.err("ERROR: removePrimitiveData");
		}
	}

	public Object start(String source, List<Object> params,
			boolean isStaticCall, Set<String> exceptions, boolean alreadyHas) {
		// initialize temp group values
		this.tempGroupValues = new Stack<GroupOfValues>();
		this.tempVariablesGroupValues = new Stack<GroupOfValues>();
		this.source = source;
		this.exceptions = exceptions;
		// -------------------Initialize Static Values--------------------
		initializeStaticFields(this.description, this.source, this.exceptions,
				false);
		// ------------------------------------------------------------------------------

		// ----------------------Read Parameters---------------------
		this.isStaticCall = isStaticCall;
		this.temporalVariables = new Stack<Object>();
		Type returnType = this.method.getReturnType();
		int length = params.size();
		// store values into local variable from parameters, note: each
		// variable
		// name of parameters is a local variable
		addValuesToLocalVariableFromParameters(length, params);
		// generate parameters for edges
		String target = "";
		String type = "(";
		if (length != 0) {
			String actualParam = "";
			Object param = null;
			for (int i = 0; i < length; i++) {
				param = params.get(i);
				if (param instanceof Collection<?>) {
					if (((Collection<?>) param).size() == 1) {
						param = ((List<?>) param).get(0);
					}
				}
				actualParam = (this.description
						.hasDescription(param.toString())) ? actualParam = param
						.toString() : this.types[i].toString();
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
		boolean found = false;
		if (source != null) {
			found = Static.addEdge(source, target);
		}

		if (Options.deepSearch) {
			found = false;
		}
		// for deep search
		if (!found) {
			// if already init Description
			if (!alreadyHas) {
				// ------------------------------------------------------------------------------

				// ---------------------Read Instructions------------------
				Static.err("\t\tSTART METHOD:\n\t\t\t-----" + source
						+ "\n\t\t\t\t--->" + target + "\n");

				if (this.methodGen.isAbstract() || this.methodGen.isNative()) {
					return Static.getDescriptionCopy(this.description,
							returnType);
				}

				// InstructionList instructionList =
				// methodGen.getInstructionList();
				for (InstructionHandle ihInstructionHandle = this.methodGen
						.getInstructionList().getStart(); ihInstructionHandle != null; ihInstructionHandle = ihInstructionHandle
						.getNext()) {
					Instruction i = ihInstructionHandle.getInstruction();

					this.currentLineNumber = ihInstructionHandle.getPosition();
					if (this.currentLineNumber >= this.conditionLineNumber
							&& this.isConditons) {
						while (!this.tempGroupValues.isEmpty()
								&& this.tempGroupValues.peek()
										.getEndlineNumber() <= this.currentLineNumber) {
							this.tempGroupValues.pop().close();
						}
						if (this.tempGroupValues.isEmpty()) {
							// if last value is GroupOfValue then if its close
							// and
							// no data left, then delete it from
							// temporalVariables
							if (this.temporalVariables.peek() instanceof GroupOfValues) {
								GroupOfValues gov = (GroupOfValues) this.temporalVariables
										.peek();
								if (!gov.isOpen && gov.isEmpty()) {
									this.temporalVariables.pop();
								}
							}
						}
						if (this.tempGroupValues.isEmpty()) {
							this.isConditons = false;
							this.conditionLineNumber = -1;
						} else {
							this.conditionLineNumber = this.tempGroupValues
									.peek().getEndlineNumber();
						}
						Static.err("\t\t\t\t\tIN-ACTIVATED");
					}
					if (this.isConditons) {
						Static.err("Current Line: " + this.currentLineNumber);
						Static.err("Condition Line: "
								+ this.conditionLineNumber);
						Static.err("Is: " + this.isConditons);
					} else {
						Static.err("\t\t\tCurrent Line: "
								+ this.currentLineNumber);
						Static.err("\t\t\tCondition Line: "
								+ this.conditionLineNumber);
						Static.err("\t\t\tIs: " + this.isConditons);
					}
					// TODO: Remove me
					if (Static.num == 63000) {
						System.err.println("");
					}
					Static.printNum();
					Static.out(i.getName());
					Static.out("\t\tBefore\n-------------------");
					Static.out("\t\tStack: " + this.temporalVariables);
					Static.out("\t\tLocal:" + this.localVariables);
					Static.out("\t\tField:" + this.classVisitor.getFields());
					Static.out("\t\tStaticField:"
							+ this.description.getStaticFields());
					// TODO: Remove me
					String lineMsg = "\t\t\t======? LINE: "
							+ this.currentLineNumber + " ?======";
					if (!visitInstruction(i)) {
						if (i instanceof ConstantPushInstruction
								|| i instanceof LDC || i instanceof LDC_W
								|| i instanceof LDC2_W) {
							Static.err(lineMsg);
							addToTemporalVariable(Static.PRIMITIVE);
							Static.out(Static.PRIMITIVE);
						} else if (i instanceof StoreInstruction) {
							Static.err(lineMsg);
							Static.err("LINE: " + this.currentLineNumber);
							Static.err("INDEX: "
									+ ((StoreInstruction) i).getIndex());
							storeValueToLocalVariable((StoreInstruction) i);
						} else if (i instanceof LoadInstruction) {
							Static.err(lineMsg);
							Static.err("LINE: " + this.currentLineNumber);
							Static.err("INDEX: "
									+ ((LoadInstruction) i).getIndex());
							loadValueFromLocalVariable((LoadInstruction) i);
						} else if (i instanceof FieldInstruction) {
							Static.err(lineMsg);
							fieldValueInstructor((FieldInstruction) i);
						} else if (i instanceof NEWARRAY
								|| i instanceof ANEWARRAY
								|| i instanceof MULTIANEWARRAY) {
							Static.err(lineMsg);
							createNewArrayProviderObject();
						} else if (i instanceof IfInstruction) {
							Static.err(lineMsg);
							this.conditionLineNumber = (i instanceof IfInstruction) ? ((IfInstruction) i)
									.getTarget().getPosition() : ((Select) i)
									.getTarget().getPosition();
							if (this.currentLineNumber <= this.conditionLineNumber) {
								fixIfConsitions(i);
								GroupOfValues gov = new GroupOfValues();
								gov.setEndLineNumber(this.conditionLineNumber);
								addToTemporalVariable(gov);
								this.tempGroupValues.add(gov);
								Static.err("\t\t\t\t\tACTIVATED");
								this.isConditons = true;
							}
						} else if (i instanceof Select) {
							Static.err(lineMsg);
							this.conditionLineNumber = (i instanceof IfInstruction) ? ((IfInstruction) i)
									.getTarget().getPosition() : ((Select) i)
									.getTarget().getPosition();
							if (this.currentLineNumber <= this.conditionLineNumber) {
								GroupOfValues gov = new GroupOfValues();
								gov.setEndLineNumber(this.conditionLineNumber);
								addToTemporalVariable(gov);
								this.tempGroupValues.add(gov);
								Static.err("\t\t\t\t\tACTIVATED");
								this.isConditons = true;
							}
						} else if (i instanceof GotoInstruction) {
							Static.err(lineMsg);
							this.conditionLineNumber = ((GotoInstruction) i)
									.getTarget().getPosition();
							if (!this.tempGroupValues.isEmpty()) {
								if (this.tempGroupValues.peek()
										.getEndlineNumber() > this.conditionLineNumber) {
									GroupOfValues gov = new GroupOfValues();
									gov.setEndLineNumber(this.conditionLineNumber);
									addToTemporalVariable(gov);
									this.tempGroupValues.add(gov);
									Static.err("\t\t\t\t\tACTIVATED");
								} else {
									this.tempGroupValues.peek()
											.setEndLineNumber(
													this.conditionLineNumber);
								}
							} else {
								if (!this.isConditons
										&& this.currentLineNumber <= this.conditionLineNumber) {
									GroupOfValues gov = new GroupOfValues();
									gov.setEndLineNumber(this.conditionLineNumber);
									addToTemporalVariable(gov);
									this.tempGroupValues.add(gov);
									Static.err("\t\t\t\t\tACTIVATED");
								}
								this.isConditons = true;
							}
						} else if (i instanceof ReturnInstruction) {
							Static.err("A RETURN TYPE");
							if ((i instanceof DRETURN)
									|| (i instanceof FRETURN)
									|| (i instanceof IRETURN)
									|| (i instanceof LRETURN)) {
								// return or store PRIMITIVE
								addReturnValues(true, returnType);
							} else if (i instanceof ARETURN) {
								// store values
								addReturnValues(false, returnType);
							}// RETURN is VOID
						} else {
							Static.err(lineMsg);
							i.accept(this);
						}
					} else {
						if (i instanceof ACONST_NULL) {
							Static.err(lineMsg);
							addToTemporalVariable(Static.NULL);
						} else if (i instanceof ArrayInstruction) {
							Static.err(lineMsg);
							arrayInstructions((ArrayInstruction) i);
						}
					}
					// TODO:Remove me
					Static.out("\t\tAfter\n-------------------");
					Static.out("\t\tStack: " + this.temporalVariables);
					Static.out("\t\tLocal:" + this.localVariables);
					Static.out("\t\tField:" + this.classVisitor.getFields());
					Static.out("\t\tStaticField:"
							+ this.description.getStaticFields());
				}
				Static.err("\t\t-----END METHOD:\n\t\t\t" + source
						+ "\n\t\t\t\t--->" + target + "\n");
				// -----------------------FixException---------
				for (String ex : this.exceptionClassList) {
					exceptions.add(ex);
				}
				// only for caller class not for current class (contains throws
				// Exception)
				if (this.exceptionTable != null) {
					String[] excepList = this.exceptionTable
							.getExceptionNames();
					for (String exc : excepList) {
						exceptions.add(exc);
					}
				}
			}
			// --------------------------------
			// Remove tempVariableGroup
			if (!this.tempVariablesGroupValues.isEmpty()) {
				for (GroupOfValues gov : this.tempVariablesGroupValues) {
					gov.close();
				}
			}
			// --------------------

			// -------------------Initialize ReturnValues--------------------
			if (returnType.toString() != "void") {
				if (this.returnValues.isEmpty()) {
					addReturnValues(Static.getDescriptionCopy(this.description,
							returnType));
				}
				return this.returnValues;
			}
		}
		// return value will return only Description type values or Same type of
		// Description or Type, whether it contains null, no matter
		return null;// Static.getDescriptionCopy(this.description, returnType);
		// ----------------------------------------------------------
	}

	private void storeValues(String flag, String variableName, Type type,
			ReferenceType referenceType) {
		try {
			Object value = getValue(type, true);
			Object targetClass = null;
			switch (flag) {
			case "ASTORE":
				addToLoaclVariable(variableName, value, referenceType, type);
				break;
			case "PUTFIELD":
				targetClass = getTargetClass(type);
				Static.err("Targets PUSH = " + targetClass);

				if (targetClass != null) {
					if (targetClass instanceof Collection<?>) {
						for (Object object : (Collection<?>) targetClass) {
							this.classVisitor.addValueToField(object,
									variableName, value, type, referenceType,
									this.isConditons);
						}
					} else {
						this.classVisitor.addValueToField(targetClass,
								variableName, value, type, referenceType,
								this.isConditons);
					}
				}
				break;
			case "PUTSTATIC":
				this.description.addValueToStaticField(this.description,
						variableName, value, type, referenceType,
						this.isConditons);
				break;
			}
			Static.out("STACK: " + this.temporalVariables);
		} catch (Exception e) {
			Static.err("ERROR: storeValues");
		}
	}

	private void storeValueToLocalVariable(StoreInstruction obj) {
		try { // int index = -1;
			LocalVariableGen localVariableGen = null;
			if (obj instanceof ISTORE) {
				localVariableGen = getLocalVariablesName(((ISTORE) obj)
						.getIndex());
			} else if (obj instanceof LSTORE) {
				localVariableGen = getLocalVariablesName(((LSTORE) obj)
						.getIndex());
			} else if (obj instanceof DSTORE) {
				localVariableGen = getLocalVariablesName(((DSTORE) obj)
						.getIndex());
			} else if (obj instanceof FSTORE) {
				localVariableGen = getLocalVariablesName(((FSTORE) obj)
						.getIndex());
			} else if (obj instanceof ASTORE) {
				localVariableGen = getLocalVariablesName(((ASTORE) obj)
						.getIndex());
			}
			if (localVariableGen != null) {
				Static.out("STORE: " + localVariableGen.getName() + "   "
						+ localVariableGen.getType() + "\t\tIs ArrayType: "
						+ obj.getType(constantPoolGen).getClass().isArray());
				storeValues("ASTORE", localVariableGen.getName(),
						localVariableGen.getType(), null);
				// must remove primitive values, because storeValue not deleted
				// primitive values
				removePrimitiveData();
			}
		} catch (Exception e) {
			Static.err("ERROR: storeValueToLocalVariable");
		}
	}

	public final String toString() {
		return this.node;
	}

	@SuppressWarnings("unchecked")
	private void visitArrayLoad(ArrayInstruction obj) {
		try {
			Type type = obj.getType(constantPoolGen);

			Object arrayObjcet = null;
			String verifyArray = "";
			// to prevent unlimited loop, after 20 times it will not continue
			int limit = 20;
			while (!verifyArray.toString().contains("[]") && limit > 0) {
				arrayObjcet = getValue(type, true);
				if (arrayObjcet instanceof Collection<?>) {
					if (!((Collection<?>) arrayObjcet).isEmpty()) {
						verifyArray = ((Stack<Object>) arrayObjcet).get(0)
								.toString();
					}
				}
				limit--;
				if (!verifyArray.toString().contains("[]")) {
					arrayObjcet = null;
				} else {
					break;
				}
			}
			// first element contains array typed value
			// do not remove that, otherwise other value cannot recognize that
			// as array
			addToTemporalVariable(arrayObjcet);
		} catch (Exception e) {
			Static.err("ERROR: visitArrayLoad");
		}
	}

	@SuppressWarnings("unchecked")
	private void visitArrayStore(ArrayInstruction obj) {
		try {
			Type type = obj.getType(constantPoolGen);
			Object value = getValue(type, true);
			Object arrayObjcet = null;
			String verifyArray = "";
			// to prevent unlimited loop, after 20 times it will not continue
			int limit = 20;
			while (!verifyArray.toString().contains("[]") && limit > 0) {
				arrayObjcet = getValue(type, true);
				if (arrayObjcet instanceof Collection<?>) {
					if (!((Collection<?>) arrayObjcet).isEmpty()) {
						verifyArray = ((Stack<Object>) arrayObjcet).get(0)
								.toString();
					}
				}
				limit--;
				if (!verifyArray.toString().contains("[]")) {
					arrayObjcet = null;
				} else {
					break;
				}
			}
			if (arrayObjcet != null) {
				if (arrayObjcet instanceof Collection<?>) {
					Stack<Object> arrayCollection = (Stack<Object>) arrayObjcet;
					if (value instanceof Collection) {
						for (Object val : (Stack<Object>) value) {
							if (!Static.containsElementInCollection(
									arrayCollection, val)) {
								if (val instanceof Collection<?>) {
									for (Object par : (Collection<?>) val) {
										if (!Static
												.containsElementInCollection(
														arrayCollection, par)) {
											arrayCollection.add(par);
										}
									}
								} else {
									if (!Static.containsElementInCollection(
											arrayCollection, val)) {
										arrayCollection.add(val);
									}
								}
							}
						}
					} else {
						if (!Static.containsElementInCollection(
								arrayCollection, value)) {
							arrayCollection.add(value);
						}
					}
				}
			}
			Static.err(arrayObjcet);
		} catch (Exception e) {
			Static.err("ERROR IN visitArrayStore");
		}
	}

	private boolean visitInstruction(Instruction i) {
		short opcode = i.getOpcode();
		return ((InstructionConstants.INSTRUCTIONS[opcode] != null)
				&& !(i instanceof ConstantPushInstruction) && !(i instanceof ReturnInstruction));
	}

	@Override
	public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
		try {
			invokedVirtualAndInterface("I", i);
		} catch (Exception e) {
			Static.err("SOME ERROR FOUND: public void visitINVOKEINTERFACE(INVOKEINTERFACE i) ");
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
			// check Exception class and then keep them
			if (Static.isSameType(referenceTpe.toString(),
					"java.lang.Exception")) {
				if (!referenceTpe.toString().equalsIgnoreCase(
						"java.lang.Object")) {
					this.exceptionClassList.add(referenceTpe.toString());
				}
			}
			Description description = this.description
					.getDescriptionByClassName(referenceTpe.toString());
			MethodVisitor methodVisitor = null;
			if (description != null) {
				String initKey = description.getClassName() + "." + methodName
						+ "(" + params + ")";
				// already initialized Descriptions
				boolean alreadyHas = false;
				Description copiedDescription = null;
				if (methodName.equalsIgnoreCase("<init>")) {
					if (params.isEmpty()) {
						alreadyHas = Static.initializedDescriptions
								.containsKey(initKey);
					}
					if (alreadyHas) {
						copiedDescription = Static.initializedDescriptions.get(
								initKey).copyAll();
					} else {
						copiedDescription = description.copy();
					}
				} else {
					copiedDescription = this.description;
				}
				addToTemporalVariable(copiedDescription);
				// TODO Remove me
				Static.out("STACK: " + this.temporalVariables);
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
							this.exceptionClassList, alreadyHas);
					if (!alreadyHas && params.isEmpty()) {
						Static.initializedDescriptions.put(initKey,
								copiedDescription);
					}
				}
			} else {
				addToTemporalVariable(Static.getDescriptionCopy(
						this.description, referenceTpe));
			}
			createEdgeIfMethodNotFound(description, methodVisitor, this.node,
					referenceTpe.toString(), methodName, params, types, false,
					null);
			Static.out("------------------------");
		} catch (Exception e) {
			Static.err("SOME ERROR FOUND: public void visitINVOKESPECIAL(INVOKESPECIAL i) ");
		}
		// Verify and remove remaining primitive data
		removePrimitiveData();
	}

	// @Override
	// public void visitCHECKCAST(CHECKCAST obj) {
	// try {
	// Static.err("CAST");
	// Static.out("\t\t" + obj.getName() + "   --->   "
	// + obj.getType(constantPoolGen).getSignature());
	// Static.out("\t\t" + obj.getType(constantPoolGen));
	// Static.out("\t\t" + obj.getLoadClassType(constantPoolGen));
	// } catch (Exception e) {
	// Static.err("SOME ERROR FOUND: public void visitCHECKCAST(CHECKCAST obj) ");
	// }
	// }

	@Override
	public void visitINVOKESTATIC(INVOKESTATIC i) {
		try {
			Type[] types = i.getArgumentTypes(constantPoolGen);
			String methodName = i.getMethodName(constantPoolGen);
			// TODO: decide as output requirement
			printObjectInvoke(types, i.getReferenceType(constantPoolGen),
					methodName, "S");
			List<Object> params = getParameters(types, methodName);
			ReferenceType referenceType = i.getReferenceType(constantPoolGen);
			Description description = this.description
					.getDescriptionByKey(referenceType.toString());
			MethodVisitor methodVisitor = null;
			Object returnType = null;
			if (description != null) {
				methodVisitor = description.getMethodVisitorByNameAndTypeArgs(
						description, methodName, types, true);
				if (methodVisitor != null) {
					// do something for array type, try to capture all values
					returnType = methodVisitor.start(this.node, params, false,
							this.exceptionClassList, false);
				}
			}
			createEdgeIfMethodNotFound(description, methodVisitor, this.node,
					referenceType.toString(), methodName, params, types, false,
					null);
			if (returnType != null
					&& !returnType.toString().equalsIgnoreCase("void")) {
				addToTemporalVariable(returnType);
			}
			Static.out("------------------------");
		} catch (Exception e) {
			Static.err("SOME ERROR FOUND: public void visitINVOKESTATIC(INVOKESTATIC i)");
		}
		// Verify and remove remaining primitive data
		removePrimitiveData();
	}

	@Override
	public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
		try {
			invokedVirtualAndInterface("O", i);
		} catch (Exception e) {
			Static.err("SOME ERROR FOUND: public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) ");
		}
	}
}