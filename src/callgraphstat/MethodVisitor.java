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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
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
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.PUTSTATIC;
import org.apache.bcel.generic.RETURN;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Type;

public class MethodVisitor extends EmptyVisitor implements
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
	private Description currentValue = null;
	// String: key=var name, value=Description
	private Map<String, List<Object>> localVariables = null;
	private List<Object> parametersValue = null;
	public static final String UNKNOWN = "Not a class or not found in Description or Library class";
	public static final String THIS = "this";

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
			if (localVariableGen.getName().equalsIgnoreCase(THIS)) {
				List<Object> object = new ArrayList<Object>();
				object.add(description);
				this.localVariables.put(localVariableGen.getName(), object);
			} else {
				this.localVariables.put(localVariableGen.getName(),
						new ArrayList<Object>());
			}
		}
		// this.localVariables = new
		// LocalVariable[this.localVariableGens.length];
		// for (int i = 0; i < this.localVariableGens.length; i++) {
		// localVariables[i] = this.localVariableGens[i]
		// .getLocalVariable(this.constantPoolGen);
		// }
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
		this.localVariables = new HashMap<String, List<Object>>();
		this.parametersValue = new ArrayList<Object>();
	}

	public Description getDescription() {
		return this.description;
	}

	public ClassVisitor getClassVisitor() {
		return this.classVisitor;
	}

	public MethodGen getMethodGen() {
		return this.methodGen;
	}

	public Method getMethod() {
		return this.method;
	}

	public String toString() {
		return this.node;
	}

	public String getNode() {
		return this.node;
	}

	private void addValue(String variableName, Object currentValue) {
		try {
			List<Object> values = this.localVariables.get(variableName);
			if (values != null) {
				values.add(currentValue);
			}
		} catch (Exception e) {
		}
	}

	private List<Object> getValues(String variableName) {
		return this.localVariables.get(variableName);
	}

	private Object getLastValue(String variableName) {
		List<Object> values = this.localVariables.get(variableName);
		int size = (values.size() > 0) ? values.size() - 1 : values.size();
		return values.get(size);
	}

	@Override
	public int compareTo(MethodVisitor node) {
		return getMethod().getName().compareTo(node.getMethod().getName());
	}

	@Override
	public void visitNEW(NEW obj) {
		// System.out.println(obj + "   --->   "
		// + obj.getLoadClassType(constantPoolGen).getClassName());

	}

	@Override
	public void visitALOAD(ALOAD obj) {
		// System.out.println("\t\t" + obj.getName() + "   --->   "
		// + obj.getType(constantPoolGen).getSignature());
		// System.out.println("\t\t" + lv[obj.getIndex()]);
		String name = this.localVariableGens[obj.getIndex()].getName();
		System.out.println("LOAD: " + name + "   "
				+ this.localVariableGens[obj.getIndex()].getType());
		try {
			// List<Object> list = this.localVariables.get(name);
			// int size = (list.size() > 0) ? list.size() - 1 : list.size();
			Object object = getLastValue(name);// list.get(size);
			if (object != null) {
				this.temporalVariables.add(object);
			}
		} catch (Exception e) {
			this.temporalVariables.add(this.localVariableGens[obj.getIndex()]
					.getType());
		}
		System.out.println("STACK: " + this.temporalVariables);
	}

	@Override
	public void visitASTORE(ASTORE obj) {
		String name = this.localVariableGens[obj.getIndex()].getName();
		System.out.println("STORE: " + name + "   "
				+ this.localVariableGens[obj.getIndex()].getType());
		Object object;
		try {
			object = this.temporalVariables.pop();
		} catch (Exception e) {
			object = this.localVariableGens[obj.getIndex()].getType();
		}
		System.out.println("STACK: " + this.temporalVariables);
		// if (object instanceof Description) {
		addValue(name, object);
		// } else {
		// maybe required for param value
		// }
		// if (currentValue != null) {
		// addValues(name, currentValue);
		// }
		// currentValue = null;
	}

	@Override
	public void visitPUTSTATIC(PUTSTATIC obj) {
		// System.out.println("\t\t" + obj.getName() + "   --->   "
		// + obj.getType(constantPoolGen).getSignature());

		// System.out.println("\t\t" + obj.getFieldName(constantPoolGen));
		// System.out.println("\t\t" + obj.getFieldType(constantPoolGen));
	}

	@Override
	public void visitPUTFIELD(PUTFIELD obj) {
		// System.out.println("\t\t" + obj.getName() + "   --->   "
		// + obj.getType(constantPoolGen).getSignature());

		// System.out.println("\t\t" + obj.getFieldName(constantPoolGen));
		// System.out.println("\t\t" + obj.getFieldType(constantPoolGen));
	}

	@Override
	public void visitGETSTATIC(GETSTATIC obj) {
		// System.out.println("\t\t" + obj.getName() + "   --->   "
		// + obj.getType(constantPoolGen).getSignature());
		// System.out.println("\t\t" + obj.getFieldName(constantPoolGen));
		// System.out.println("\t\t" + obj.getFieldType(constantPoolGen));
	}

	@Override
	public void visitGETFIELD(GETFIELD obj) {
		// System.out.println("\t\t" + obj.getName() + "   --->   "
		// + obj.getType(constantPoolGen).getSignature());
		// System.out.println("\t\t" + obj.getFieldName(constantPoolGen));
		// System.out.println("\t\t" + obj.getFieldType(constantPoolGen));
	}

	@Override
	public void visitRETURN(RETURN obj) {
		// System.out.println("\t\t" + obj.getName() + "   --->   "
		// + obj.getType(constantPoolGen).getSignature());
		// System.out.println("\t\t" + obj.getType(constantPoolGen));
	}

	public void start(String source, List<Object> params) {
		// empty values
		this.temporalVariables = new Stack<Object>();
		this.parametersValue = params;
		this.currentValue = null;
		Type returnType = this.method.getReturnType();
		// empty localvar value
		for (Entry<String, List<Object>> key : this.localVariables.entrySet()) {
			if (!key.getKey().equalsIgnoreCase(THIS)) {
				key.setValue(new ArrayList<Object>());
			}
		}

		int length = params.size();
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
		boolean result = addSource(source, target);
		if (!result) {
			if (methodGen.isAbstract() || methodGen.isNative())
				return;
			// InstructionList instructionList = methodGen.getInstructionList();
			for (InstructionHandle ihInstructionHandle = methodGen
					.getInstructionList().getStart(); ihInstructionHandle != null; ihInstructionHandle = ihInstructionHandle
					.getNext()) {
				Instruction i = ihInstructionHandle.getInstruction();
				// System.out.println("\t" + i + "     "
				// + i.toString(constantPoolGen.getConstantPool()));
				// }
				System.out.println(i.getName());
				if (!visitInstruction(i)) {
					i.accept(this);
				}
				String a1 = "";
			}
		}
		System.out.println("Somethings");
	}

	public boolean addSource(String source, String target) {
		if (source != null) {
			String edge = source.concat(" -- > ").concat(target).trim();
			if (!JCallGraph.edges.contains(edge)) {
				JCallGraph.edges.add(edge);
			}

			return this.description.addEdge(source, target);
		}
		return false;
	}

	void print(Object s) {
		// System.out.println("\t" + s);
	}

	private boolean visitInstruction(Instruction i) {
		short opcode = i.getOpcode();

		return ((InstructionConstants.INSTRUCTIONS[opcode] != null)
				&& !(i instanceof ConstantPushInstruction) && !(i instanceof ReturnInstruction));
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

		// return type: i.getReturnType(constantPoolGen)

		// System.out.println(i.getArgumentTypes(constantPoolGen));

		// Stack<Object> params = new Stack<Object>();
		List<Object> params = new ArrayList<Object>();
		if (length > 0) {
			// keep in params, reverse order from temp stack
			Object object;
			for (int j = 0; j < length; j++) {
				try {
					object = this.temporalVariables.pop();
				} catch (Exception e) {
					object = types[j];
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
			// this.currentValue = description.copy();
			this.temporalVariables.add(description.copy());
			System.out.println("STACK: " + this.temporalVariables);
			methodVisitor = description.getMethodVisitorByNameAndTypeArgs(
					methodName, types);
			description.getClassVisitor().start();
			methodVisitor.start(this.node, params);
		} else {
			this.temporalVariables.add(UNKNOWN);
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
