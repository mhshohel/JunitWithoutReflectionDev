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
import org.apache.bcel.generic.CHECKCAST;
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
	private String castType = null;
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

	@Override
	public int compareTo(MethodVisitor node) {
		return getMethod().getName().compareTo(node.getMethod().getName());
	}

	private boolean visitInstruction(Instruction i) {
		short opcode = i.getOpcode();
		return ((InstructionConstants.INSTRUCTIONS[opcode] != null)
				&& !(i instanceof ConstantPushInstruction) && !(i instanceof ReturnInstruction));
	}

	private void initializeStaticFields(Description description, String source,
			Set<String> exceptions, boolean forGetStatic) {
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
									new ArrayList<Object>(), true, exceptions,
									false);
				} else if (forGetStatic) {
					if (description.getSuperClassDescription() != null) {
						initializeStaticFields(
								description.getSuperClassDescription(), source,
								exceptions, forGetStatic);
					}
				}
			} catch (Exception e) {
				Static.err("SOME ERROR FOUND: public Object start(String source, List<Object> params, boolean isStaticCall");
			}
		}
		// ------------------------------------------------------------------------------
	}

	public Object start(String source, List<Object> params,
			boolean isStaticCall, Set<String> exceptions, boolean alreadyHas) {
		// initialize temp group values
		this.tempGroupValues = new Stack<GroupOfValues>();
		this.tempVariablesGroupValues = new Stack<GroupOfValues>();
		this.castType = null;
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
		// if (!isStaticCall) {
		if (source != null) {
			Static.addEdge(source, target);
		}

		if (!alreadyHas) {
			// }
			// ------------------------------------------------------------------------------

			// ---------------------Read Instructions------------------
			Static.err("\t\tSTART METHOD:\n\t\t\t-----" + source
					+ "\n\t\t\t\t--->" + target + "\n");

			if (this.methodGen.isAbstract() || this.methodGen.isNative()) {
				return Static.getDescriptionCopy(this.description, returnType);
			}

			// InstructionList instructionList = methodGen.getInstructionList();
			for (InstructionHandle ihInstructionHandle = this.methodGen
					.getInstructionList().getStart(); ihInstructionHandle != null; ihInstructionHandle = ihInstructionHandle
					.getNext()) {
				Instruction i = ihInstructionHandle.getInstruction();

				this.currentLineNumber = ihInstructionHandle.getPosition();
				if (this.currentLineNumber >= this.conditionLineNumber
						&& this.isConditons) {
					while (!this.tempGroupValues.isEmpty()
							&& this.tempGroupValues.peek().getEndlineNumber() <= this.currentLineNumber) {
						this.tempGroupValues.pop().close();
					}
					if (this.tempGroupValues.isEmpty()) {
						// if last value is GroupOfValue then if its close and
						// no data left, then delete it from temporalVariables
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
						this.conditionLineNumber = this.tempGroupValues.peek()
								.getEndlineNumber();
					}
					Static.err("\t\t\t\t\tIN-ACTIVATED");
				}
				if (this.isConditons) {
					Static.err("Current Line: " + this.currentLineNumber);
					Static.err("Condition Line: " + this.conditionLineNumber);
					Static.err("Is: " + this.isConditons);
				} else {
					Static.err("\t\t\tCurrent Line: " + this.currentLineNumber);
					Static.err("\t\t\tCondition Line: "
							+ this.conditionLineNumber);
					Static.err("\t\t\tIs: " + this.isConditons);
				}
				// TODO: Remove me
				Static.out(i.getName());
				Static.out("\t\tBefore\n-------------------");
				Static.out("\t\tStack: " + this.temporalVariables);
				Static.out("\t\tLocal:" + this.localVariables);
				Static.out("\t\tField:" + this.classVisitor.fields);
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
						Static.err("INDEX: " + ((LoadInstruction) i).getIndex());
						loadValueFromLocalVariable((LoadInstruction) i);
					} else if (i instanceof FieldInstruction) {
						Static.err(lineMsg);
						fieldValueInstructor((FieldInstruction) i);
					} else if (i instanceof NEWARRAY || i instanceof ANEWARRAY
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
							if (this.tempGroupValues.peek().getEndlineNumber() > this.conditionLineNumber) {
								GroupOfValues gov = new GroupOfValues();
								gov.setEndLineNumber(this.conditionLineNumber);
								addToTemporalVariable(gov);
								this.tempGroupValues.add(gov);
								Static.err("\t\t\t\t\tACTIVATED");
							} else {
								this.tempGroupValues.peek().setEndLineNumber(
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
						if ((i instanceof DRETURN) || (i instanceof FRETURN)
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
				Static.out("\t\tField:" + this.classVisitor.fields);
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
				String[] excepList = this.exceptionTable.getExceptionNames();
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
		// TODO verify return type
		// Object value = null;

		if (returnType.toString() != "void") {
			// value = (this.temporalVariables != null &&
			// !this.temporalVariables
			// .isEmpty()) ? this.temporalVariables.pop() : null;
			// TODO:Remove me
			// Static.out("RETURN TYPE: " + value);
			// if (value != null) {
			// return value;
			// // }
			// }
			if (this.returnValues.isEmpty()) {
				addReturnValues(Static.getDescriptionCopy(this.description,
						returnType));
			}
			return this.returnValues;
		}
		// return value will return only Description type values or Same type of
		// Description or Type, whether it contains null, no matter
		return null;// Static.getDescriptionCopy(this.description, returnType);
		// ----------------------------------------------------------
	}

	// add values from params to local variable of Method
	private void addValuesToLocalVariableFromParameters(int length,
			List<Object> params) {
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
	}

	// remove value(s) that contains by if, if can one value or twos depending
	// of category
	private void fixIfConsitions(Instruction i) {
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
		} else if (i instanceof IFEQ || i instanceof IFGE || i instanceof IFGT
				|| i instanceof IFLE || i instanceof IFLT || i instanceof IFNE
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
	}

	private void addToLoaclVariable(String variableName, Object currentValue,
			ReferenceType referenceType, Type type) {
		try {
			List<Object> values = this.localVariables.get(variableName);
			// keep previous value if and only if condition is open, otherwise
			// remove all
			if (values != null) {
				int size = values.size();
				if (!this.isConditons) {
					if (!values.isEmpty()) {
						values.clear();
					}
				}
				// for (int i = 0; i < size; i++) {
				// if (values.get(i).hashCode() == currentValue.hashCode()) {
				// values.remove(i);
				// break;
				// }
				// }
				// Object lastValue = (values.isEmpty()) ? null : values
				// .get(values.size() - 1);
				// currentValue = getSingleValueOrGroupOfValues(currentValue,
				// lastValue);

				// do not save group value
				if (currentValue instanceof Collection) {
					for (Object stackValues : (Collection<?>) currentValue) {
						if (!(stackValues instanceof GroupOfValues)) {
							Object thisValue = Static
									.verifyTypeFromObjectsToStore(stackValues,
											type, description);
							if (!(values.contains(thisValue))) {
								values.add(thisValue);
							}
						} else {
							GroupOfValues gv = (GroupOfValues) stackValues;
							for (Object gvv : gv
									.getAllValues(type, description)) {
								if (!(values.contains(gvv))) {
									values.add(gvv);
								}
							}
						}
					}
				} else {
					if (!(currentValue instanceof GroupOfValues)) {
						if (!(values.contains(currentValue))) {
							values.add(currentValue);
						}
					} else {
						GroupOfValues gv = (GroupOfValues) currentValue;
						for (Object gvv : gv.getAllValues(type, description)) {
							if (!(values.contains(gvv))) {
								values.add(gvv);
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	// get single value or group of values depending on required conditions
	private Object getSingleValueOrGroupOfValues(Object currentValue,
			Object lastValue) {
		// need a closed GroupOfValues so that it can keep values without
		// creating any problem for global stack
		GroupOfValues gov = new GroupOfValues();
		gov.setEndLineNumber(this.currentLineNumber);
		// if (this.isConditons) {
		if (lastValue instanceof GroupOfValues) {
			for (Object object : ((GroupOfValues) lastValue).getValues()) {
				gov.add(object);
			}
		} else {
			if (lastValue != null) {
				gov.add(lastValue);
			}
		}
		// }
		if (currentValue instanceof List) {
			if (!((ArrayList<?>) currentValue).isEmpty()) {
				for (Object object : ((ArrayList<?>) currentValue)) {
					if (!(gov.getValues().contains(object))) {
						gov.add(object);
					}
				}
			}
		} else {
			gov.add(currentValue);
		}
		gov.close();
		// tempVariablesGroupValues will keep value till the end of method
		// Invocation
		if (this.isConditons || (currentValue instanceof List)
				|| (lastValue instanceof GroupOfValues)) {
			this.tempVariablesGroupValues.add(gov);
			return gov;
		} else {
			gov = null;
			return currentValue;
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
			// return objects.peek();
			if (variableName.equalsIgnoreCase("this")) {
				return objects.peek();
			}
			return objects;
		}
		return null;
	}

	private boolean isCollectionsOrMap(String classWithPackage) {
		try {
			Class<?> cls = Class.forName(classWithPackage);
			if (Collection.class.isAssignableFrom(cls)
					|| Map.class.isAssignableFrom(cls)) {
				// TODO Remove me
				Static.out("\t\t\t\tCollections or Map type: TRUE");
				return true;
			} else {
				// TODO Remove me
				Static.out("\t\t\t\tCollections or Map type: FALSE");
				return false;
			}
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	// // keep current loaded target classes
	// private Stack<Object> tempLoadVals = new Stack<Object>();
	// // keep the stack size when tempLoadVals takes target value
	// private Stack<Integer> temporalVariableSize = new Stack<Integer>();

	private void storeValues(String flag, String variableName, Type type,
			ReferenceType referenceType) {
		try {
			Object value = getValue(type);
			// to store pass stack or group value that will converted to stack
			// only, no gov directly
			// Object value = null;
			// boolean isArrayType = false;
			// boolean isCollectionType = false;
			// if (type.toString().contains("[]")) {
			// isArrayType = true;
			// } else {
			// isCollectionType = isCollectionsOrMap(type.toString());
			// }
			// GroupOfValues gov = null;
			// boolean isOpenGroupFound = false;
			// try {
			// value = (this.temporalVariables != null &&
			// !this.temporalVariables
			// .isEmpty()) ? this.temporalVariables.peek() : null;
			// if (value instanceof GroupOfValues) {
			// gov = (GroupOfValues) value;
			// if (gov.isOpen) {
			// isOpenGroupFound = true;
			// // not close get last group and marge
			// if (!this.tempGroupValues.isEmpty()) {
			// value = this.tempGroupValues.peek().pop();
			// if (Static.isPrimitiveType(type)) {
			// value = Static.PRIMITIVE;
			// } else {
			// if (value instanceof GroupOfValues) {
			// // get all values including child
			// value = ((GroupOfValues) value)
			// .getAllValues(type,
			// this.description);
			// } else if (value instanceof Collection) {
			// Stack<Object> allValues = new Stack<Object>();
			// for (Object stackValues : (Collection<?>) value) {
			// Object thisValue = Static
			// .verifyTypeFromObjectsToStore(
			// stackValues, type,
			// description);
			// if (!(allValues.contains(thisValue))) {
			// allValues.add(thisValue);
			// }
			// }
			// value = allValues;
			// }
			// }
			// } else {
			// value = gov.getAllValues(type, this.description);
			// }
			// } else {
			// // if close
			// // if close merge all data
			// if (Static.isPrimitiveType(type)) {
			// value = Static.PRIMITIVE;
			// } else {
			// value = gov.getAllValues(type, this.description);
			// }
			// this.temporalVariables.pop();
			// }
			// } else {
			// this.temporalVariables.pop();
			// }
			//
			// // List<Object> values = new ArrayList<Object>();
			// if (value instanceof List) {
			// if (((List<?>) value).isEmpty()
			// || ((List<?>) value) == null) {
			// value = Static.NULL;
			// }
			// } else {
			// value = Static.verifyTypeFromObjectsToStore(value, type,
			// this.description);
			// }
			// if (value == null) {
			// if (Static.isPrimitiveType(type)) {
			// value = Static.PRIMITIVE;
			// } else {
			// value = Static.getDescriptionCopy(this.description,
			// type);
			// }
			// }
			// } catch (Exception e) {
			// value = Static.getDescriptionCopy(this.description, type);
			// }
			Object targetClass = null;
			switch (flag) {
			case "ASTORE":
				addToLoaclVariable(variableName, value, referenceType, type);
				break;
			case "PUTFIELD":
				// if (this.temporalVariables != null
				// && !this.temporalVariables.isEmpty()) {
				// if (!((this.temporalVariables.peek()) instanceof
				// GroupOfValues)) {
				// targetClass = this.temporalVariables.pop();
				// } else {
				// // if (!((GroupOfValues)
				// // this.temporalVariables.peek()).isOpen) {
				// // this.temporalVariables.pop();
				// // }
				// }
				// }

				// targetClass = this.localVariables.get(tempLoad.peek());
				//
				// targetClass = (this.temporalVariables != null &&
				// !this.temporalVariables
				// .isEmpty()) ? (Static.isSameType(referenceType
				// .toString(), this.temporalVariables.peek().toString())) ?
				// this.temporalVariables
				// .pop() : this.temporalVariables.peek()
				// : null;
				// targetClass = tempLoad.pop();
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
			this.castType = null;
			Static.out("STACK: " + this.temporalVariables);
		} catch (Exception e) {
		}
	}

	private void addReturnValues(boolean isPrimitive, Type returnType) {
		// TODO Auto-generated method stub
		// must remove last value of group or other
		// not save as group or collection
		Object value = getValue(returnType);
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
	}

	private void addReturnValues(Object value) {
		if (!this.returnValues.contains(value)) {
			this.returnValues.add(value);
		}
	}

	private Object getValue(Type type) {
		// to store pass stack or group value that will converted to stack
		// only, no gov directly
		Object value = null;
		boolean isArrayType = false;
		boolean isCollectionType = false;
		if (type.toString().contains("[]")) {
			isArrayType = true;
		} else {
			isCollectionType = isCollectionsOrMap(type.toString());
		}
		GroupOfValues gov = null;
		boolean isOpenGroupFound = false;
		try {
			value = (this.temporalVariables != null && !this.temporalVariables
					.isEmpty()) ? this.temporalVariables.peek() : null;
			if (value instanceof GroupOfValues) {
				gov = (GroupOfValues) value;
				if (gov.isOpen) {
					isOpenGroupFound = true;
					// not close get last group and marge
					if (!this.tempGroupValues.isEmpty()) {
						value = this.tempGroupValues.peek().pop();
						if (Static.isPrimitiveType(type)) {
							value = Static.PRIMITIVE;
						} else {
							if (value instanceof GroupOfValues) {
								// get all values including child
								value = ((GroupOfValues) value).getAllValues(
										type, this.description);
							} else if (value instanceof Collection) {
								Stack<Object> allValues = new Stack<Object>();
								for (Object stackValues : (Collection<?>) value) {
									Object thisValue = Static
											.verifyTypeFromObjectsToStore(
													stackValues, type,
													description);
									if (!(allValues.contains(thisValue))) {
										allValues.add(thisValue);
									}
								}
								value = allValues;
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
					this.temporalVariables.pop();
				}
			} else {
				this.temporalVariables.pop();
			}

			// List<Object> values = new ArrayList<Object>();
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
		}
		return value;
	}

	private Object getTargetClass(Type type) {
		Object target = null;
		if (this.temporalVariables != null && !this.temporalVariables.isEmpty()) {
			if (!((this.temporalVariables.peek()) instanceof GroupOfValues)) {
				return this.temporalVariables.pop();
			} else {
				GroupOfValues gov = (GroupOfValues) this.temporalVariables
						.peek();
				if (gov.isOpen) {
					if (!this.tempGroupValues.isEmpty()) {
						target = this.tempGroupValues.peek().pop();
						if (target instanceof GroupOfValues) {
							// assume if group found then that will be close.
							// So, pop that group and then pop another to get
							// target. It's because if there is a group found
							// that will be collected as value of that target,
							// so, it will be empty that we don not need to take
							target = this.tempGroupValues.peek().pop();
						}
					} else {
						// TODO: Verify it
						target = null;
					}
				} else {
					// TODO: verify it
					this.temporalVariables.pop();
					if (!((this.temporalVariables.peek()) instanceof GroupOfValues)) {
						return this.temporalVariables.pop();
					}
				}
			}
		}

		return target;
	}

	private void loadValues(String flag, String variableName, Type type,
			ReferenceType referenceType) {
		try {
			Object value = null;
			// boolean isArrayType = type.toString().contains("[]");
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
										if (!values.contains(val)) {
											values.add(val);
										}
									}
								} else {
									if (!values.contains(value)) {
										values.add(value);
									}
								}
							}
							// if (values.isEmpty()) {
							// value = Static.getDescriptionCopy(
							// this.description, type);
							// }
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
						value = Static.NULL;
					}
					break;
				}
			} catch (Exception e) {
				value = type;
			}
			// TODO: verify by array instance and others core factor
			if (value != null) {
				// this.temporalVariables.add(value);
				addToTemporalVariable(value);
			}
			// TODO: may not required
			this.castType = null;
			Static.out("STACK: " + this.temporalVariables);
		} catch (Exception e) {
		}
	}

	private final LocalVariableGen getLocalVariablesName(int index) {
		// keep the line number for same indexed variable
		List<Integer> list = new ArrayList<Integer>();
		String name = null;
		int currentIndex = -1;
		int size = this.localVariableArray.length;
		LocalVariable currentVariable = null;
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

				// TODO: MAKE IT PARFECT
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

				// if (i + 1 < size && list.size() < 3) {
				// if (this.localVariableArray[i + 1].getIndex() != index) {
				// name = currentVariable.getName();
				// break;
				// }
				// }

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
						lv = this.localVariableTable.getLocalVariable(index,
								val);
						if (lv != null) {
							name = lv.getName();
							break;
						}
					}
				}
			}
		}
		Static.err(name);
		LocalVariableGen localVariableGen = null;
		for (LocalVariableGen lvg : this.localVariableGens) {
			if (lvg.getName().equalsIgnoreCase(name)) {
				localVariableGen = lvg;
				break;
			}
		}

		return localVariableGen;
	}

	private void storeValueToLocalVariable(StoreInstruction obj) {
		// int index = -1;
		LocalVariableGen localVariableGen = null;
		if (obj instanceof ISTORE) {
			localVariableGen = getLocalVariablesName(((ISTORE) obj).getIndex());
		} else if (obj instanceof LSTORE) {
			localVariableGen = getLocalVariablesName(((LSTORE) obj).getIndex());
		} else if (obj instanceof DSTORE) {
			localVariableGen = getLocalVariablesName(((DSTORE) obj).getIndex());
		} else if (obj instanceof FSTORE) {
			localVariableGen = getLocalVariablesName(((FSTORE) obj).getIndex());
		} else if (obj instanceof ASTORE) {
			localVariableGen = getLocalVariablesName(((ASTORE) obj).getIndex());
		}
		try {
			// removeUnknownValueIfPushInstruction(ASTORE.class);
			if (localVariableGen != null) {
				// String name = this.localVariableGens[index].getName();//
				// localVariable.getName();
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
			Static.err("FOUND IN ASTORE");
		}
	}

	private void loadValueFromLocalVariable(LoadInstruction obj) {
		// int index = -1;
		LocalVariableGen localVariableGen = null;
		if (obj instanceof ILOAD) {
			localVariableGen = getLocalVariablesName(((ILOAD) obj).getIndex());
		} else if (obj instanceof LLOAD) {
			localVariableGen = getLocalVariablesName(((LLOAD) obj).getIndex());
		} else if (obj instanceof DLOAD) {
			localVariableGen = getLocalVariablesName(((DLOAD) obj).getIndex());
		} else if (obj instanceof FLOAD) {
			localVariableGen = getLocalVariablesName(((FLOAD) obj).getIndex());
		} else if (obj instanceof ALOAD) {
			localVariableGen = getLocalVariablesName(((ALOAD) obj).getIndex());
		}
		try {
			// removeUnknownValueIfPushInstruction(ALOAD.class);
			if (localVariableGen != null) {
				// String name = this.localVariableGens[index].getName();//
				// localVariable.getName();
				loadValues("ALOAD", localVariableGen.getName(),
						localVariableGen.getType(), null);
				Object refObj = ((this.temporalVariables != null && !this.temporalVariables
						.isEmpty()) ? this.temporalVariables.peek()
						: Static.NULL);
				// this.tempLoadVals.add(refObj);
				// temporalVariableSize.add(this.temporalVariables.size());
				// loaded value can be stack
				Static.out("LOAD: " + localVariableGen.getName() + "   "
						+ localVariableGen.getType() + "\t" + refObj);
			}
		} catch (Exception e) {
			Static.err("FOUND IN ALOAD");
		}
	}

	private void fieldValueInstructor(FieldInstruction obj) {
		try {
			// TODO Remove me
			Static.out("\t\t" + obj.getName() + "   --->   "
					+ obj.getType(constantPoolGen).getSignature());

			Static.out("\t\t" + obj.getFieldName(constantPoolGen));
			Static.out("\t\t" + obj.getFieldType(constantPoolGen));

			// this.tempLoadVals.pop();
			// this.temporalVariableSize.pop();
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
			Static.err("FOUND IN private void fieldValueInstructor(FieldInstruction obj)");
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
			Static.err("ERROR IN AASTORE");
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
			Static.out("\t\t" + obj.getName() + "   --->   "
					+ obj.getType(constantPoolGen).getSignature());
			Static.out("\t\t" + obj.getType(constantPoolGen));
			//
		} catch (Exception e) {
			Static.err("ERROR IN AALOAD");
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
					Type type = types[c];
					Object value = getValue(type);
					// try {
					// GroupOfValues gov = null;
					// value = (this.temporalVariables != null &&
					// !this.temporalVariables
					// .isEmpty()) ? this.temporalVariables.peek()
					// : null;
					// if (value instanceof GroupOfValues) {
					// gov = (GroupOfValues) value;
					// if (gov.isOpen) {
					// // not close get last group and marge
					// if (!this.tempGroupValues.isEmpty()) {
					// value = this.tempGroupValues.peek().pop();
					// if (Static.isPrimitiveType(type)) {
					// value = Static.PRIMITIVE;
					// } else {
					// if (value instanceof GroupOfValues) {
					// // get all values including child
					// value = ((GroupOfValues) value)
					// .getAllValues(type,
					// this.description);
					// } else if (value instanceof Collection) {
					// Stack<Object> allValues = new Stack<Object>();
					// for (Object stackValues : (Collection<?>) value) {
					// Object thisValue = Static
					// .verifyTypeFromObjectsToStore(
					// stackValues,
					// type,
					// description);
					// if (!(allValues
					// .contains(thisValue))) {
					// allValues.add(thisValue);
					// }
					// }
					// value = allValues;
					// }
					// }
					// } else {
					// value = gov.getAllValues(type,
					// this.description);
					// }
					// } else {
					// // if close
					// // if close merge all data
					// if (Static.isPrimitiveType(type)) {
					// value = Static.PRIMITIVE;
					// } else {
					// value = gov.getAllValues(type,
					// this.description);
					// }
					// this.temporalVariables.pop();
					// }
					// } else {
					// this.temporalVariables.pop();
					// }
					//
					// // List<Object> values = new ArrayList<Object>();
					// if (value instanceof List) {
					// if (((List<?>) value).isEmpty()
					// || ((List<?>) value) == null) {
					// value = Static.NULL;
					// }
					// } else {
					// value = Static.verifyTypeFromObjectsToStore(value,
					// type, this.description);
					// }
					// if (value == null) {
					// if (Static.isPrimitiveType(type)) {
					// value = Static.PRIMITIVE;
					// } else {
					// value = Static.getDescriptionCopy(
					// this.description, type);
					// }
					// }
					// } catch (Exception e) {
					// value = Static.getDescriptionCopy(this.description,
					// type);
					// }
					params.add(value);

					// Object value = (this.temporalVariables != null &&
					// !this.temporalVariables
					// .isEmpty()) ? this.temporalVariables.pop()
					// : null;
					// if (value != null) {
					// if (value.toString().equalsIgnoreCase(
					// Static.PRIMITIVE)) {
					// object = value;
					// } else if (value.toString().equalsIgnoreCase(
					// Static.NULL)) {
					// object = value;
					// } else if (Static.isPrimitiveType(type)
					// || value instanceof String) {
					// object = Static.PRIMITIVE;
					// } else {
					// result = Static.isSameType(type.toString(),
					// value.toString());
					// if (result) {
					// object = value;
					// } else {
					// object = Static.getDescriptionCopy(
					// this.description, value);
					// if (!Static.isSameType(type.toString(),
					// object.toString())) {
					// object = Static.getDescriptionCopy(
					// this.description, type);
					// }
					// }
					// }
					// } else {
					// if (Static.isPrimitiveType(type)) {
					// object = Static.PRIMITIVE;
					// } else {
					// object = Static.getDescriptionCopy(
					// this.description, type);
					// }
					// }
					// // if (this.temporalVariables != null
					// // && !this.temporalVariables.isEmpty()
					// // && object.equals(value)) {
					// // this.temporalVariables.pop();
					// // }
					// } catch (Exception e) {
					// Static.err("SOME ERROR IN PARAM getParameters()");
					// object = Static.getDescriptionCopy(this.description,
					// types[c]);
					// }
					// params.add(object);
				}
			}
			if (params.size() > 1) {
				Collections.reverse(params);
			}
		} catch (Exception e) {
			this.castType = null;
		}
		// TODO Remove me
		Static.out("\t\t\t\tParams:   " + params);
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
						.equalsIgnoreCase(Static.PRIMITIVE)) {
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
		Static.out(String.format(format, flag, referenceType, methodName)
				+ " "
				+ type
				+ "\n------------------------------------------------------------------------------------------\n");
		// -------------------------------------
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
								if (!(allValues.contains(thisValue))) {
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
			System.err.println("ERROR");
		}
		return values;
	}

	private void invokedVirtualAndInterface(String flag, InvokeInstruction i) {
		// TODO: Remove flag at the end of everything
		try {
			Type[] types = i.getArgumentTypes(constantPoolGen);
			String methodName = i.getMethodName(constantPoolGen);
			// TODO: decide as output requirement
			printObjectInvoke(types, i.getReferenceType(constantPoolGen),
					methodName, flag);
			List<Object> params = getParameters(types, methodName);
			ReferenceType referenceType = i.getReferenceType(constantPoolGen);
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
										types);
							}
						}
						if (returnType != null
								&& !returnType.toString().equalsIgnoreCase(
										"void")) {
							// this.temporalVariables.add(returnType);
							if (returnType instanceof Collection<?>) {
								for (Object obj : (Collection<?>) returnType) {
									if (!allReturnValues.contains(obj)) {
										allReturnValues.add(obj);
									}
								}
							} else {
								if (!allReturnValues.contains(returnType)) {
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
								params, types);
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

	@Override
	public void visitCHECKCAST(CHECKCAST obj) {
		Static.err("CAST");
		Static.out("\t\t" + obj.getName() + "   --->   "
				+ obj.getType(constantPoolGen).getSignature());
		Static.out("\t\t" + obj.getType(constantPoolGen));
		Static.out("\t\t" + obj.getLoadClassType(constantPoolGen));
		this.castType = null;// obj.getType(constantPoolGen).toString();
	}

	@Override
	public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
		invokedVirtualAndInterface("I", i);
	}

	@Override
	public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
		invokedVirtualAndInterface("O", i);
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
				// already initialized Descriotions
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
					// copiedDescription = description.copy();
					// alreadyHas = false;
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
				if (isCollectionsOrMap(referenceTpe.toString())) {
					this.temporalVariables.add(new CollectionObjectProvider());
				} else {
					// this.temporalVariables
					// .add(getDescriptionCopy(referenceTpe));
					addToTemporalVariable(Static.getDescriptionCopy(
							this.description, referenceTpe));
				}
			}
			createEdgeIfMethodNotFound(description, methodVisitor, this.node,
					referenceTpe.toString(), methodName, params, types);
			Static.out("------------------------");
		} catch (Exception e) {
			Static.err("SOME ERROR FOUND: public void visitINVOKESPECIAL(INVOKESPECIAL i) ");
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
					referenceType.toString(), methodName, params, types);
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

	private void addToTemporalVariable(Object object) {
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
						// if somehow miss then it will add value, however, this
						// line will never use
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
	}

	// generate edges which has no description or method visitor recorded
	private boolean createEdgeIfMethodNotFound(Description description,
			MethodVisitor methodVisitor, String source, String referenceType,
			String methodName, List<Object> params, Type[] types) {
		collectionMethodReturnType = null;
		boolean hasDescription = this.description.hasDescription(referenceType
				.toString());
		if (description == null) {
			String target = referenceType + "." + methodName;
			String type = "(";
			if (params != null && !params.isEmpty()) {
				int length = params.size();
				String actualParam = "";
				for (int i = 0; i < length; i++) {
					actualParam = params.get(i).toString();
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
				if (object.toString().equalsIgnoreCase(Static.PRIMITIVE)) {
					add(object.toString(), Static.PRIMITIVE);
				} else if (Static.isSameType(getType(), object.toString())) {
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
				if (data.object.toString().equalsIgnoreCase(Static.NULL)) {
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
				object = Static.NULL;
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