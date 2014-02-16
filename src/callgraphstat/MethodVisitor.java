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
 * Linnaeus University, V�xj�, Sweden
 *
 */
package callgraphstat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.bcel.classfile.JavaClass;
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
import org.apache.bcel.generic.RETURN;
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
	// private ExtractMethod extractMethod = null;
	private Stack<Object> temporalVariables;
	// String: key=var name, value=Description
	private Map<String, Stack<Object>> localVariables = null;

	public MethodVisitor(Description description, ClassVisitor classVisitor,
			Method method, MethodGen methodGen) {
		this.description = description;
		this.classVisitor = classVisitor;
		this.method = method;
		this.methodGen = methodGen;
		this.javaClass = this.description.getJavaClass();
		this.constantPoolGen = methodGen.getConstantPool();
		initialize();
		this.format = "M:" + this.javaClass.getClassName() + ":"
				+ methodGen.getName() + " " + "(%s)%s:%s";
		this.localVariableGens = methodGen.getLocalVariables();
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
		// if ((InstructionConstants.INSTRUCTIONS[opcode] != null)
		// && i instanceof ArrayInstruction) {
		// return false;
		// }
		return ((InstructionConstants.INSTRUCTIONS[opcode] != null)
				&& !(i instanceof ConstantPushInstruction) && !(i instanceof ReturnInstruction));
	}

	private boolean isStaticCall = false;

	public void start(String source, List<Object> params, boolean isStaticCall) {
		// if (!isStaticCall)
		if (!this.description.isVisitedToCheckStaticField) {
			this.description.isVisitedToCheckStaticField = true;
			try {
				List<MethodVisitor> list = this.description
						.getMethodVisitorByName("<clinit>");
				if (list.get(0) != null) {
					System.err.println("\t\t\t\t STATIC VALS FOUND: "
							+ this.description);
					this.description.getMethodVisitorByName("<clinit>").get(0)
							.start(null, new ArrayList<Object>(), true);
				}
			} catch (Exception e) {
				// Do Nothing
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
		System.err.println("\t\t\t\t-----START METHOD: " + source + " -- > "
				+ target + " -----\n");
		// if (!result) {
		if (methodGen.isAbstract() || methodGen.isNative())
			return;
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
					+ this.description.staticFields);

			// boolean sssss = visitInstruction(i);

			if (i.getName().equalsIgnoreCase("aaload")) {
				i.accept(this);
			}

			if (i.getName().equalsIgnoreCase("aastore")) {
				i.accept(this);
			} else if (!visitInstruction(i)) {
				i.accept(this);
			} else {
				if (i.getName().equalsIgnoreCase("aconst_null")) {
					this.temporalVariables.add(Description.NULL);
					System.err.println("\t\t\t\t\tNULL");
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
					+ this.description.staticFields);
		}
		// }
		System.err.println("\t\t\t\t-----END METHOD: " + source + " -- > "
				+ target + " -----\n");
	}

	// private boolean addSource(String source, String target) {
	// if (source != null) {
	// Description.addEdge(source, target);
	// }
	// return false;
	// }

	private void addToLoaclVariable(String variableName, Object currentValue) {
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

	private Object getLocalVariableByVarialbleName(String variableName) {
		Stack<Object> objects = getLocalVariablesByVarialbleName(variableName);
		if (objects != null) {
			return objects.peek();
		}
		return null;
	}

	private void storeValues(String flag, String variableName, Object type) {
		Object value = null;
		boolean isArrayType = type.toString().contains("[]");
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
								value = new ArrayObjectProvider(type.toString());
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
					value = arrayObjectProvider.arrayObjects.get(this.castType);
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
			addToLoaclVariable(variableName, value);
			break;
		case "PUTFIELD":
			this.classVisitor.addValueToField(this.classVisitor, variableName,
					value);
			break;
		case "PUTSTATIC":
			this.description.addValueToStaticField(this.description,
					variableName, value);
			break;
		}
		this.castType = null;
		System.out.println("STACK: " + this.temporalVariables);
	}

	private void loadValues(String flag, String variableName, Object type) {
		Object value = null;
		// boolean isArrayType = type.toString().contains("[]");
		try {
			switch (flag) {
			case "ALOAD":
				value = getLocalVariableByVarialbleName(variableName);
				break;
			case "GETFIELD":
				value = this.classVisitor.getValueFromField(this.classVisitor,
						variableName);
				break;
			case "GETSTATIC":
				value = this.description.getValueFromStaticField(
						this.description, variableName);
				break;
			}
		} catch (Exception e) {
			value = type;
		}
		if (value != null) {
			this.temporalVariables.add(value);
		}
		System.out.println("STACK: " + this.temporalVariables);
	}

	private String castType = null;

	@Override
	public void visitCHECKCAST(CHECKCAST obj) {
		System.out.println("\t\t" + obj.getName() + "   --->   "
				+ obj.getType(constantPoolGen).getSignature());
		System.out.println("\t\t" + obj.getType(constantPoolGen));
		System.out.println("\t\t" + obj.getLoadClassType(constantPoolGen));
		this.castType = obj.getType(constantPoolGen).toString();
	}

	@Override
	public void visitAASTORE(AASTORE obj) {
		try {
			int size = this.temporalVariables.size();
			Object dataObject = this.temporalVariables.peek();
			Object arrayObjcet = this.temporalVariables.get(size - 2);
			if (arrayObjcet instanceof ArrayObjectProvider) {
				ArrayObjectProvider arrayObjectProvider = (ArrayObjectProvider) arrayObjcet;
				arrayObjectProvider.arrayObjects.put(dataObject.toString(),
						dataObject);
				this.temporalVariables.pop();
				this.temporalVariables.pop();
			}
		} catch (Exception e) {

		}
	}

	@Override
	public void visitAALOAD(AALOAD obj) {
		System.out.println("\t\t" + obj.getName() + "   --->   "
				+ obj.getType(constantPoolGen).getSignature());
		System.out.println("\t\t" + obj.getType(constantPoolGen));
	}

	// @Override
	// public void visitANEWARRAY(ANEWARRAY obj) {
	// System.out.println("\t\t" + obj.getName() + "   --->   "
	// + obj.getLength());
	// System.out.println("\t\t" + obj.getLoadClassType(constantPoolGen));
	// }

	@Override
	public void visitARETURN(ARETURN obj) {
		System.out.println("\t\t" + obj.getName() + "   --->   "
				+ obj.getType(constantPoolGen).getSignature());
		System.out.println("\t\t" + obj.getType(constantPoolGen));
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

	@Override
	public void visitALOAD(ALOAD obj) {
		String name = this.localVariableGens[obj.getIndex()].getName();
		System.out.println("LOAD: " + name + "   "
				+ this.localVariableGens[obj.getIndex()].getType());
		loadValues("ALOAD", name,
				this.localVariableGens[obj.getIndex()].getType());
	}

	@Override
	public void visitASTORE(ASTORE obj) {
		String name = this.localVariableGens[obj.getIndex()].getName();
		System.out.println("STORE: " + name + "   "
				+ this.localVariableGens[obj.getIndex()].getType());
		storeValues("ASTORE", name,
				this.localVariableGens[obj.getIndex()].getType());
	}

	@Override
	public void visitPUTFIELD(PUTFIELD obj) {
		System.out.println("\t\t" + obj.getName() + "   --->   "
				+ obj.getType(constantPoolGen).getSignature());

		System.out.println("\t\t" + obj.getFieldName(constantPoolGen));
		System.out.println("\t\t" + obj.getFieldType(constantPoolGen));
		storeValues("PUTFIELD", obj.getFieldName(constantPoolGen),
				obj.getFieldType(constantPoolGen));
	}

	@Override
	public void visitGETFIELD(GETFIELD obj) {
		System.out.println("\t\t" + obj.getName() + "   --->   "
				+ obj.getType(constantPoolGen).getSignature());
		System.out.println("\t\t" + obj.getFieldName(constantPoolGen));
		System.out.println("\t\t" + obj.getFieldType(constantPoolGen));
		loadValues("GETFIELD", obj.getFieldName(constantPoolGen),
				obj.getFieldType(constantPoolGen));
	}

	@Override
	public void visitPUTSTATIC(PUTSTATIC obj) {
		System.out.println("\t\t" + obj.getName() + "   --->   "
				+ obj.getType(constantPoolGen).getSignature());

		System.out.println("\t\t" + obj.getFieldName(constantPoolGen));
		System.out.println("\t\t" + obj.getFieldType(constantPoolGen));
		storeValues("PUTSTATIC", obj.getFieldName(constantPoolGen),
				obj.getFieldType(constantPoolGen));
	}

	@Override
	public void visitGETSTATIC(GETSTATIC obj) {
		System.out.println("\t\t" + obj.getName() + "   --->   "
				+ obj.getType(constantPoolGen).getSignature());
		System.out.println("\t\t" + obj.getFieldName(constantPoolGen));
		System.out.println("\t\t" + obj.getFieldType(constantPoolGen));
		loadValues("GETSTATIC", obj.getFieldName(constantPoolGen),
				obj.getFieldType(constantPoolGen));
	}

	@Override
	public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
		System.out.println(String.format(format, "M",
				i.getReferenceType(constantPoolGen),
				i.getMethodName(constantPoolGen)));
		System.out.println("------------------------");
	}

	@Override
	public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
		// String s = i.getType()(constantPoolGen).toString();
		LocalVariableGen[] f = methodGen.getLocalVariables();
		LineNumberGen[] lns = methodGen.getLineNumbers();
		System.out.println(String.format(format, "I",
				i.getReferenceType(constantPoolGen),
				i.getMethodName(constantPoolGen)));
		System.out.println("------------------------");
	}

	// @Override
	// public void visitICONST(ICONST obj) {
	// System.out.println("\t\t" + obj.getName() + "   --->   "
	// + obj.getType(constantPoolGen).getSignature());
	// System.out.println("\t\t" + obj.getValue());
	// }

	@Override
	public void visitINVOKESPECIAL(INVOKESPECIAL i) {
		Type[] types = i.getArgumentTypes(constantPoolGen);// this.method.getArgumentTypes();
		int length = types.length;
		String type = "(";
		for (int j = 0; j < length; j++) {
			type += ((j + 1) == length) ? types[j] : types[j] + ",";
		}
		type += ")";
		String methodName = i.getMethodName(constantPoolGen);
		System.out.println(String.format(format, "O",
				i.getReferenceType(constantPoolGen), methodName)
				+ " " + type);
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
					result = isSameType(types[c], object);
					if (result) {
						object = this.temporalVariables.pop();
					} else {
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
		Description description = this.description.getDescriptionByClassName(i
				.getReferenceType(constantPoolGen).toString());
		MethodVisitor methodVisitor = null;
		if (description != null) {
			Description copiedDescription = description.copy();
			this.temporalVariables.add(copiedDescription);
			System.out.println("STACK: " + this.temporalVariables);
			methodVisitor = copiedDescription
					.getMethodVisitorByNameAndTypeArgs(methodName, types);
			if (!this.description.isSuperClassObjectInitiated) {
				if (this.description.getSuperClassDescription().toString()
						.equalsIgnoreCase(copiedDescription.toString())) {
					this.description.isSuperClassObjectInitiated = true;
					this.description
							.setSuperClassDescription(copiedDescription);
				}
			}
			methodVisitor.start(this.node, params, this.isStaticCall);
		} else {
			this.temporalVariables.add(i.getReferenceType(constantPoolGen));
		}

		System.out.println("------------------------");
	}

	@Override
	public void visitINVOKESTATIC(INVOKESTATIC i) {
		System.out.println(String.format(format, "S",
				i.getReferenceType(constantPoolGen),
				i.getMethodName(constantPoolGen)));
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
