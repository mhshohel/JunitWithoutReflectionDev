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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
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

public class MethodVisitor extends EmptyVisitor {
	JavaClass visitedClass;
	private MethodGen methodGen;
	private ConstantPoolGen constantPoolGen;
	private String format;
	private LocalVariable[] localVariables;
	private LocalVariableGen[] localVariableGens;
	private ExtractMethod extractMethod = null;
	private Description description = null;
	private Set<String> sources = new HashSet<String>();
	private Map<Field, Set<Description>> values = new HashMap<Field, Set<Description>>();

	// public MethodVisitor(MethodGen m, JavaClass jc, ExtractMethod
	// extractMethod) {
	public MethodVisitor(Description description, ExtractMethod extractMethod) {
		this.extractMethod = extractMethod;
		this.description = description;
		visitedClass = this.description.getJavaClass();
		methodGen = this.extractMethod.getMethodGen();
		constantPoolGen = methodGen.getConstantPool();
		format = "M:" + visitedClass.getClassName() + ":" + methodGen.getName()
				+ " " + "(%s)%s:%s";
		localVariableGens = methodGen.getLocalVariables();
		localVariables = new LocalVariable[localVariableGens.length];
		for (int i = 0; i < localVariableGens.length; i++) {
			localVariables[i] = localVariableGens[i]
					.getLocalVariable(constantPoolGen);
		}
		lvt = methodGen.getLocalVariableTable(constantPoolGen);
	}

	LocalVariableTable lvt;

	public void print() {
		System.out.println(this.extractMethod);
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
	}

	@Override
	public void visitASTORE(ASTORE obj) {
		// System.out.println(obj.getName() + "   --->   "
		// + obj.getType(constantPoolGen).getSignature());
		// System.out.println("\t\t" + localVariables[obj.getIndex()]);
		LocalVariableGen l = localVariableGens[obj.getIndex()];
		System.out.println("STORE: " + l.getName());
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
		// System.out.println(localVariableGens[1].getName());
		// System.out.println(localVariables[1]);
		addSource(source);
		if (methodGen.isAbstract() || methodGen.isNative())
			return;
		InstructionList lis = methodGen.getInstructionList();
		for (InstructionHandle ih = methodGen.getInstructionList().getStart(); ih != null; ih = ih
				.getNext()) {
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

	public void addSource(String source) {
		if (source != null) {
			this.sources.add(source);
		}
	}

	public Set<String> getSources() {
		return this.sources;
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
