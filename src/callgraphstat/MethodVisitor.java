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
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ARETURN;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.LineNumberGen;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.PUTSTATIC;
import org.apache.bcel.generic.PushInstruction;
import org.apache.bcel.generic.RETURN;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.ReturnInstruction;
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

	// TODO: remove me if not required or used by anyone
	private boolean isSameType(Type paramType, Object objectFromStack) {
		try {
			if (objectFromStack instanceof Description) {
				Description description = (Description) objectFromStack;
				if (description.toString().equalsIgnoreCase(
						paramType.toString())) {
					return true;
				} else {
					boolean result = false;
					Description superDescription = description
							.getSuperClassDescription();
					if (superDescription == null) {
						result = false;
					} else {
						result = isSameType(paramType, superDescription);
					}
					if (result) {
						return true;
					} else {
						List<Description> interfaceDescription = description
								.getInterfaces();
						if (interfaceDescription == null
								|| interfaceDescription.isEmpty()) {
							return false;
						} else {
							for (Description obj : interfaceDescription) {
								result = isSameType(paramType, obj);
								if (result) {
									return true;
								}
							}
						}
					}
				}
			} else {
				if (paramType.toString().equalsIgnoreCase(
						objectFromStack.toString())) {
					return true;
				}
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

	private boolean isStaticCall = false;

	public Object start(String source, List<Object> params, boolean isStaticCall) {
		this.castType = null;
		if (!this.description.isVisitedToCheckStaticField) {
			this.description.isVisitedToCheckStaticField = true;
			// set value to the parent, because its for static field
			this.description.getDescriptionByJavaClass(this.javaClass).isVisitedToCheckStaticField = true;
			try {
				List<MethodVisitor> list = this.description
						.getMethodVisitorByName("<clinit>");
				if (list != null && !list.isEmpty() && list.get(0) != null) {
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
			for (int i = 0; i < length; i++) {
				type += ((i + 1) == length) ? params.get(i) : params.get(i)
						+ ",";
			}
			type += ")";
			target = this.javaClass.getClassName() + "."
					+ this.method.getName() + type
					+ this.method.getReturnType();
		} else {
			target = this.node;
		}
		// add edge
		// boolean result = false;
		if (!isStaticCall) {
			// result = addSource(source, target);
			if (source != null) {
				Description.addEdge(source, target);
			}
		}
		System.err.println("\t\tSTART METHOD:\n\t\t\t-----" + source
				+ "\n\t\t\t\t--->" + target + "\n");
		// if (!result) {
		if (methodGen.isAbstract() || methodGen.isNative()) {
			return returnType;
		}
		// InstructionList instructionList = methodGen.getInstructionList();
		for (InstructionHandle ihInstructionHandle = methodGen
				.getInstructionList().getStart(); ihInstructionHandle != null; ihInstructionHandle = ihInstructionHandle
				.getNext()) {
			Instruction i = ihInstructionHandle.getInstruction();
			System.out.println(i.getName());
			System.out.println("\t\tBefore\n-------------------");
			System.out.println("\t\tStack: " + this.temporalVariables);
			System.out.println("\t\tLocal:" + this.localVariables);
			System.out.println("\t\tField:" + this.classVisitor.fields);
			System.out.println("\t\tStaticField:"
					+ this.description.getStaticFields());

			// boolean sssss = visitInstruction(i);

			if (i.getName().equalsIgnoreCase("aaload")
					|| i.getName().equalsIgnoreCase("aastore")) {
				i.accept(this);
			} else if (!visitInstruction(i)) {
				System.out
						.println("\t\t\tPUSH INSTRUCTIONS: "
								+ InstructionConstants.INSTRUCTIONS[i
										.getOpcode()]
								+ ((i instanceof PushInstruction) ? " TRUE"
										: " FALSE"));

				if (i instanceof PushInstruction) {
					this.temporalVariables.add(Description.UNKNOWN);
				}

				i.accept(this);
			} else {
				if (i.getName().equalsIgnoreCase("aconst_null")) {
					this.temporalVariables.add(Description.UNKNOWN);
					System.err.println("\t\t\t\t\t" + Description.UNKNOWN);
				}
				// else {
				// this.temporalVariables.add(Description.UNKNOWN);
				// System.err.println("\t\t\t\t\tUNKNOWN");
				// }
			}
			System.out.println("\t\tAfter\n-------------------");
			System.out.println("\t\tStack: " + this.temporalVariables);
			System.out.println("\t\tLocal:" + this.localVariables);
			System.out.println("\t\tField:" + this.classVisitor.fields);
			System.out.println("\t\tStaticField:"
					+ this.description.getStaticFields());
		}
		// }
		System.err.println("\t\t-----END METHOD:\n\t\t\t" + source
				+ "\n\t\t\t\t--->" + target + "\n");

		if (returnType.toString() != "void") {
			System.out.println(returnType);
			Object stackObject = this.temporalVariables.peek();
			String classType = returnType.toString();
			String stackType = stackObject.toString();
			if (isSameType(classType, stackType)) {
				return this.temporalVariables.pop();
			}
		}
		return returnType;
	}

	// private boolean addSource(String source, String target) {
	// if (source != null) {
	// Description.addEdge(source, target);
	// }
	// return false;
	// }

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
				System.out.println("\t\t\t\tCollections or Map type: TRUE");
				return true;
			} else {
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
						if (this.castType != null
								&& this.temporalVariables.peek() instanceof ArrayObjectProvider) {
							ArrayObjectProvider arrayObjectProvider = (ArrayObjectProvider) this.temporalVariables
									.peek();
							value = arrayObjectProvider.arrayObjects
									.get(this.castType);
						} else if (value == null) {
							value = type;
						}

					} else {
						if (this.temporalVariables.peek() instanceof ArrayObjectProvider) {
							String arrayType = ((ArrayObjectProvider) this.temporalVariables
									.peek()).arrayType;
							String currentType = type.toString();
							if (arrayType.equalsIgnoreCase(currentType)) {
								value = this.temporalVariables.pop();
							} else {
								try {
									Description desOne = this.description
											.getDescriptionByClassName(arrayType
													.replace("[]", ""));
									Description desTwo = this.description
											.getDescriptionByClassName(currentType
													.replace("[]", ""));
									if ((desOne != null && desTwo != null)
											&& (desOne.getActualClass()
													.isAssignableFrom(desTwo
															.getActualClass()))
											|| (desTwo.getActualClass()
													.isAssignableFrom(desOne
															.getActualClass()))) {
										value = this.temporalVariables.pop();
									} else {
										value = new ArrayObjectProvider(
												type.toString());
									}
								} catch (Exception e) {
									value = new ArrayObjectProvider(
											type.toString());
								}
							}
						} else {
							value = new ArrayObjectProvider(type.toString());
						}
					}
				} else {
					if (this.castType != null
							&& this.temporalVariables.peek() instanceof ArrayObjectProvider) {
						ArrayObjectProvider arrayObjectProvider = (ArrayObjectProvider) this.temporalVariables
								.peek();
						value = arrayObjectProvider.arrayObjects
								.get(this.castType);
						if (value == null) {
							value = this.temporalVariables.pop();
						}
					} else {
						value = this.temporalVariables.pop();
					}
				}
			} catch (Exception e) {
				value = type;
			}
			switch (flag) {
			case "ASTORE":
				addToLoaclVariable(variableName, value, referenceType);
				break;
			case "PUTFIELD":
				this.classVisitor
						.addValueToField(
								this.classVisitor,
								variableName,
								value,
								referenceType,
								((this.temporalVariables != null && !this.temporalVariables
										.isEmpty()) ? this.temporalVariables
										.peek() : null));
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
					value = this.classVisitor
							.getValueFromField(
									this.classVisitor,
									variableName,
									referenceType,
									((this.temporalVariables != null && !this.temporalVariables
											.isEmpty()) ? this.temporalVariables
											.peek() : null));
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

	@Override
	public void visitCHECKCAST(CHECKCAST obj) {
		System.out.println("\t\t" + obj.getName() + "   --->   "
				+ obj.getType(constantPoolGen).getSignature());
		System.out.println("\t\t" + obj.getType(constantPoolGen));
		System.out.println("\t\t" + obj.getLoadClassType(constantPoolGen));
		this.castType = obj.getType(constantPoolGen).toString();
	}

	// @Override
	// public void visitANEWARRAY(ANEWARRAY obj) {
	// System.out.println("\t\t" + obj.getName() + "   --->   "
	// + obj.getLength());
	// System.out.println("\t\t" + obj.getLoadClassType(constantPoolGen));
	// }

	@Override
	public void visitARETURN(ARETURN obj) {
		// System.out.println("\t\t" + obj.getName() + "   --->   "
		// + obj.getType(constantPoolGen).getSignature());
		// System.out.println("\t\t" + obj.getType(constantPoolGen));
	}

	@Override
	public void visitRETURN(RETURN obj) {
		// System.out.println("\t\t" + obj.getName() + "   --->   "
		// + obj.getType(constantPoolGen).getSignature());
		// System.out.println("\t\t" + obj.getType(constantPoolGen));
	}

	// @Override
	// public void visitARRAYLENGTH(ARRAYLENGTH obj) {
	// System.out.println("\t\t" + obj.getName() + "   --->   "
	// + obj.getLength());
	// }

	// @Override
	// public void visitBIPUSH(BIPUSH obj) {
	// System.out.println("\t\t" + obj.getName() + "   --->   "
	// + obj.getType(constantPoolGen).getSignature());
	// System.out.println("\t\t" + obj.getType(constantPoolGen));
	// }

	// @Override
	// public void visitDUP(DUP obj) {
	// System.out.println("\t\t" + obj.getName() + "   --->   "
	// + obj.getType(constantPoolGen).getSignature());
	// System.out.println("\t\t" + obj.getType(constantPoolGen));
	// }

	// @Override
	// public void visitMULTIANEWARRAY(MULTIANEWARRAY obj) {
	// System.out.println("\t\t" + obj.getName() + "   --->   "
	// + obj.getType(constantPoolGen).getSignature());
	// System.out.println("\t\t" + obj.getType(constantPoolGen));
	// }

	public final int getLocalVariablesIndex(int index) {
		for (int i = 0; i < this.localVariableArray.length; i++) {
			if (this.localVariableArray[i].getIndex() == index) {
				return i;
			}
		}
		return -1;
	}

	// use this carefully, from start unknown added is push instruction found,
	// but is data loaded by push instruction implemented class then pop unknown
	private void removeUnknownValueIfPushInstruction(Class<?> cls) {
		if (PushInstruction.class.isAssignableFrom(cls)) {
			try {
				if (this.temporalVariables != null
						&& !this.temporalVariables.isEmpty()
						|| this.temporalVariables.peek().toString()
								.equalsIgnoreCase(Description.UNKNOWN)) {
					this.temporalVariables.pop();
					System.err.println("\t\t\tUNKNOWN VALUE FOUND AND REMOVED");
				}
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void visitAASTORE(AASTORE obj) {
		try {
			removeUnknownValueIfPushInstruction(AASTORE.class);
			int size = this.temporalVariables.size();
			Object dataObject = this.temporalVariables.peek();
			Object arrayObjcet = this.temporalVariables.get(size - 2);
			if (arrayObjcet instanceof ArrayObjectProvider) {
				ArrayObjectProvider arrayObjectProvider = (ArrayObjectProvider) arrayObjcet;
				arrayObjectProvider.arrayObjects.put(dataObject.toString(),
						dataObject);
				// TODO: verify me
				this.temporalVariables.pop();
				this.temporalVariables.pop();
			}
		} catch (Exception e) {
			System.err.println("FOUND IN AASTORE");
		}
	}

	@Override
	public void visitAALOAD(AALOAD obj) {
		try {
			removeUnknownValueIfPushInstruction(AALOAD.class);
			System.out.println("\t\t" + obj.getName() + "   --->   "
					+ obj.getType(constantPoolGen).getSignature());
			System.out.println("\t\t" + obj.getType(constantPoolGen));
		} catch (Exception e) {
			System.err.println("FOUND IN AALOAD");
		}
	}

	// obj.getIndex()
	@Override
	public void visitALOAD(ALOAD obj) {
		// String name = this.localVariableGens[obj.getIndex()].getName();
		try {
			removeUnknownValueIfPushInstruction(ALOAD.class);
			int index = getLocalVariablesIndex(obj.getIndex());
			if (index != -1) {
				String name = this.localVariableGens[index].getName();// localVariable.getName();
				System.out.println("LOAD: " + name + "   "
						+ this.localVariableGens[index].getType());
				loadValues("ALOAD", name,
						this.localVariableGens[index].getType(), null);
			}
		} catch (Exception e) {
			System.err.println("FOUND IN ALOAD");
		}
	}

	@Override
	public void visitASTORE(ASTORE obj) {
		try {
			removeUnknownValueIfPushInstruction(ASTORE.class);
			int index = getLocalVariablesIndex(obj.getIndex());
			if (index != -1) {
				String name = this.localVariableGens[index].getName();// localVariable.getName();
				System.out.println("STORE: " + name + "   "
						+ this.localVariableGens[index].getType());
				storeValues("ASTORE", name,
						this.localVariableGens[index].getType(), null);
			}
		} catch (Exception e) {
			System.err.println("FOUND IN ASTORE");
		}
	}

	@Override
	public void visitPUTFIELD(PUTFIELD obj) {
		try {
			removeUnknownValueIfPushInstruction(PUTFIELD.class);
			System.out.println("\t\t" + obj.getName() + "   --->   "
					+ obj.getType(constantPoolGen).getSignature());

			System.out.println("\t\t" + obj.getFieldName(constantPoolGen));
			System.out.println("\t\t" + obj.getFieldType(constantPoolGen));
			storeValues("PUTFIELD", obj.getFieldName(constantPoolGen),
					obj.getFieldType(constantPoolGen),
					obj.getReferenceType(constantPoolGen));
		} catch (Exception e) {
			System.err.println("FOUND IN PUTFIELD");
		}
	}

	@Override
	public void visitGETFIELD(GETFIELD obj) {
		try {
			removeUnknownValueIfPushInstruction(GETFIELD.class);
			System.out.println("\t\t" + obj.getName() + "   --->   "
					+ obj.getType(constantPoolGen).getSignature());
			System.out.println("\t\t" + obj.getFieldName(constantPoolGen));
			System.out.println("\t\t" + obj.getFieldType(constantPoolGen));
			loadValues("GETFIELD", obj.getFieldName(constantPoolGen),
					obj.getFieldType(constantPoolGen),
					obj.getReferenceType(constantPoolGen));
		} catch (Exception e) {
			System.err.println("FOUND IN GETFIELD");
		}
	}

	@Override
	public void visitPUTSTATIC(PUTSTATIC obj) {
		try {
			removeUnknownValueIfPushInstruction(PUTSTATIC.class);
			System.out.println("\t\t" + obj.getName() + "   --->   "
					+ obj.getType(constantPoolGen).getSignature());
			System.out.println("\t\t" + obj.getReferenceType(constantPoolGen));
			System.out.println("\t\t" + obj.getFieldName(constantPoolGen));
			System.out.println("\t\t" + obj.getFieldType(constantPoolGen));
			storeValues("PUTSTATIC", obj.getFieldName(constantPoolGen),
					obj.getFieldType(constantPoolGen),
					obj.getReferenceType(constantPoolGen));
		} catch (Exception e) {
			System.err.println("FOUND IN PUTSTATIC");
		}
	}

	@Override
	public void visitGETSTATIC(GETSTATIC obj) {
		try {
			removeUnknownValueIfPushInstruction(GETSTATIC.class);
			System.out.println("\t\t" + obj.getName() + "   --->   "
					+ obj.getType(constantPoolGen).getSignature());
			System.out.println("\t\t" + obj.getReferenceType(constantPoolGen));
			System.out.println("\t\t" + obj.getFieldName(constantPoolGen));
			System.out.println("\t\t" + obj.getFieldType(constantPoolGen));
			loadValues("GETSTATIC", obj.getFieldName(constantPoolGen),
					obj.getFieldType(constantPoolGen),
					obj.getReferenceType(constantPoolGen));
		} catch (Exception e) {
			System.err.println("FOUND IN GETSTATIC");
		}
	}

	@Override
	public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
		Type type = i.getReferenceType(constantPoolGen);
		boolean isCollectionOrMap = isCollectionsOrMap(type.toString());
		boolean isStackCollectionOrMap = false;
		Object object = null;
		try {
			object = this.temporalVariables.peek();
			if (object instanceof ArrayObjectProvider) {
				isStackCollectionOrMap = true;
			}
		} catch (Exception e) {
			isStackCollectionOrMap = false;
		}
		if (isCollectionOrMap && isStackCollectionOrMap) {
			// this.temporalVariables.add(e)
		}
		// String s = i.getType()(constantPoolGen).toString();
		LocalVariableGen[] f = methodGen.getLocalVariables();
		LineNumberGen[] lns = methodGen.getLineNumbers();
		System.out
				.println(String.format(format, "I", type,
						i.getMethodName(constantPoolGen))
						+ "\n------------------------------------------------------------------------------------------\n");
		System.out.println("------------------------");
	}

	@Override
	public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
		try {
			Type[] types = i.getArgumentTypes(constantPoolGen);// this.method.getArgumentTypes();
			int length = types.length;
			String type = "(";
			for (int j = 0; j < length; j++) {
				type += ((j + 1) == length) ? types[j] : types[j] + ",";
			}
			type += ")";
			String methodName = i.getMethodName(constantPoolGen);
			System.out
					.println(String.format(format, "O",
							i.getReferenceType(constantPoolGen), methodName)
							+ " "
							+ type
							+ "\n------------------------------------------------------------------------------------------\n");
			List<Object> params = new ArrayList<Object>();
			int c = types.length;
			if (length > 0) {
				// keep in params, reverse order from temp stack
				Object object;
				boolean result;
				for (int j = 0; j < length; j++) {
					c--;
					try {
						// object = this.temporalVariables.pop();
						object = this.temporalVariables.peek();
						// result = isSameType(types[c], object);
						result = isSameType(types[c].toString(),
								object.toString());
						if (result) {
							object = this.temporalVariables.pop();
						} else {
							if (object.toString().equalsIgnoreCase(
									Description.UNKNOWN)) {
								this.temporalVariables.pop();
							}
							object = types[c];
						}
					} catch (Exception e) {
						object = types[c];
					}
					params.add(object);
				}
			}
			if (params.size() > 1) {
				Collections.reverse(params);
			}

			// TODO: VERIFY MUST remove Description.NULL from stack
			Object stackObject = this.temporalVariables.peek();

			String classType = i.getReferenceType(constantPoolGen).toString();
			String stackType = stackObject.toString();
			if (isSameType(classType, stackType)) {
				classType = stackType;
			}

			Description description = null;
			try {
				if (stackObject instanceof Description) {
					description = (Description) stackObject;
				}
			} catch (Exception e) {

			}
			MethodVisitor methodVisitor = null;
			Object returnType = null;
			if (description != null) {
				// Description copiedDescription = description.copy();
				// this.temporalVariables.add(copiedDescription);
				// System.out.println("STACK: " + this.temporalVariables);
				methodVisitor = description.getMethodVisitorByNameAndTypeArgs(
						description, methodName, types, true);
				// if (!this.description.isSuperClassObjectInitiated) {
				// if (this.description.getSuperClassDescription().toString()
				// .equalsIgnoreCase(description.toString())) {
				// this.description.isSuperClassObjectInitiated = true;
				// this.description.setSuperClassDescription(description);
				// }
				// }
				if (methodVisitor != null) {
					returnType = methodVisitor.start(this.node, params, false);
				}
			} else {
				// this.temporalVariables.add(i.getReferenceType(constantPoolGen));
			}
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

	// @Override
	// public void visitICONST(ICONST obj) {
	// System.out.println("\t\t" + obj.getName() + "   --->   "
	// + obj.getType(constantPoolGen).getSignature());
	// System.out.println("\t\t" + obj.getValue());
	// }

	@Override
	public void visitINVOKESPECIAL(INVOKESPECIAL i) {
		try {
			Object returnType = null;
			Type[] types = i.getArgumentTypes(constantPoolGen);// this.method.getArgumentTypes();
			int length = types.length;
			String type = "(";
			for (int j = 0; j < length; j++) {
				type += ((j + 1) == length) ? types[j] : types[j] + ",";
			}
			type += ")";
			String methodName = i.getMethodName(constantPoolGen);
			System.out
					.println(String.format(format, "O",
							i.getReferenceType(constantPoolGen), methodName)
							+ " "
							+ type
							+ "\n------------------------------------------------------------------------------------------\n");
			List<Object> params = new ArrayList<Object>();
			int c = types.length;
			if (length > 0) {
				// keep in params, reverse order from temp stack
				Object object;
				boolean result;
				for (int j = 0; j < length; j++) {
					c--;
					try {
						// object = this.temporalVariables.pop();
						object = this.temporalVariables.peek();
						// result = isSameType(types[c], object);
						result = isSameType(types[c].toString(),
								object.toString());
						if (result) {
							object = this.temporalVariables.pop();
						} else {
							if (object.toString().equalsIgnoreCase(
									Description.UNKNOWN)) {
								this.temporalVariables.pop();
							}
							object = types[c];
						}
					} catch (Exception e) {
						object = types[c];
					}
					params.add(object);
				}
			}
			if (params.size() > 1) {
				Collections.reverse(params);
			}
			Description description = this.description
					.getDescriptionByClassName(i.getReferenceType(
							constantPoolGen).toString());
			// if (methodName.equalsIgnoreCase("<clinit>")
			// && description.isVisitedToCheckStaticField) {
			// return;
			// }
			MethodVisitor methodVisitor = null;
			if (description != null) {
				Description copiedDescription = description.copy();
				this.temporalVariables.add(copiedDescription);
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
					returnType = methodVisitor.start(this.node, params,
							this.isStaticCall);
				}
			} else {
				this.temporalVariables.add(i.getReferenceType(constantPoolGen));
			}

			System.out.println("------------------------");
		} catch (Exception e) {
			System.err
					.println("SOME ERROR FOUND: public void visitINVOKESPECIAL(INVOKESPECIAL i) ");
		}
	}

	@Override
	public void visitINVOKESTATIC(INVOKESTATIC i) {
		System.out
				.println(String.format(format, "S",
						i.getReferenceType(constantPoolGen),
						i.getMethodName(constantPoolGen))
						+ "\n------------------------------------------------------------------------------------------\n");
		System.out.println("------------------------");
	}
}

class ArrayObjectProvider {
	// change key as object instead of string to keep all objects -- not done
	// here
	public Map<String, Object> arrayObjects = new LinkedHashMap<String, Object>();
	public String arrayType = "";

	public ArrayObjectProvider(String arrayType) {
		this.arrayType = arrayType;
	}
}
