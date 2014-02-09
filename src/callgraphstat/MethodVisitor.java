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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
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
import org.apache.bcel.generic.InstructionList;
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
	private LocalVariable[] localVariables = null;
	private LocalVariableGen[] localVariableGens = null;
	// private ExtractMethod extractMethod = null;
	private Description currentValue = null;
	private Map<String, List<Description>> values = null;
	private List<Description> parametersValue = null;

	// public MethodVisitor(MethodGen m, JavaClass jc, ExtractMethod
	// extractMethod) {
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
		this.localVariables = new LocalVariable[this.localVariableGens.length];
		for (int i = 0; i < this.localVariableGens.length; i++) {
			localVariables[i] = this.localVariableGens[i]
					.getLocalVariable(this.constantPoolGen);
		}
		this.lvt = methodGen.getLocalVariableTable(this.constantPoolGen);
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
				+ type + " - " + this.method.getReturnType();
		this.values = new HashMap<String, List<Description>>();
		this.parametersValue = new ArrayList<Description>();
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

	@Override
	public int compareTo(MethodVisitor node) {
		return getMethod().getName().compareTo(node.getMethod().getName());
	}

	LocalVariableTable lvt;
	int v = 5;

	public void prints() {
		System.out.println(v);
	}

	public void print() {
		// System.out.println(this.extractMethod);
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
		LocalVariableGen l = localVariableGens[obj.getIndex()];
		System.out.println("LOAD: " + l.getName());
		List<Description> list = this.values.get(l.getName());
		if (list != null) {
			parametersValue.add(list.get(list.size() - 1));
		}
	}

	@Override
	public void visitASTORE(ASTORE obj) {
		// System.out.println(obj.getName() + "   --->   "
		// + obj.getType(constantPoolGen).getSignature());
		// System.out.println("\t\t" + localVariables[obj.getIndex()]);
		LocalVariableGen l = localVariableGens[obj.getIndex()];
		System.out.println("STORE: " + l.getName());
		if (currentValue != null) {
			addValues(l.getName(), currentValue);
		}
		currentValue = null;
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

	public void start(String source) {
		// empty values
		this.values.clear();
		boolean result = addSource(source);
		if (!result) {
			if (methodGen.isAbstract() || methodGen.isNative())
				return;
			InstructionList lis = methodGen.getInstructionList();
			for (InstructionHandle ih = methodGen.getInstructionList()
					.getStart(); ih != null; ih = ih.getNext()) {
				Instruction i = ih.getInstruction();
				// System.out.println("\t" + i + "     "
				// + i.toString(constantPoolGen.getConstantPool()));
				// }

				if (!visitInstruction(i)) {
					i.accept(this);
				}
				String a1 = "";
			}
		}
		System.out.println("Somethings");
	}

	public boolean addSource(String source) {
		// if (source != null) {
		// String target = extractMethod.getNode();
		// return description.addEdge(source, target);
		// }
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
		System.out.println(String.format(format, "O",
				i.getReferenceType(constantPoolGen),
				i.getMethodName(constantPoolGen)));
		Description description = this.description.getDescriptionByClassName(i
				.getReferenceType(constantPoolGen).toString());
		if (description != null) {
			this.currentValue = description.copy();
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

	private void addValues(String key, Description value) {
		List<Description> values = this.values.get(key);
		if (values == null) {
			values = new ArrayList<Description>();
			values.add(value);
			this.values.put(key, values);
		} else if (!values.contains(description)) {
			values.add(description);
		}
	}
}
