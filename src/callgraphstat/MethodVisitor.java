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

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.FieldGen;
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

public class MethodVisitor extends EmptyVisitor {
    JavaClass visitedClass;
    private MethodGen mg;
    private ConstantPoolGen cp;
    private String format;
    private LocalVariableGen[] lv;
    private Field[] fl;
    private FieldGen[] flg;

    public MethodVisitor(MethodGen m, JavaClass jc) {
	visitedClass = jc;
	mg = m;
	cp = mg.getConstantPool();
	format = "M:" + visitedClass.getClassName() + ":" + mg.getName() + " "
		+ "(%s)%s:%s";
	lv = mg.getLocalVariables();
	fl = jc.getFields();
	flg = new FieldGen[fl.length];
	for (int i = 0; i < fl.length; i++) {
	    flg[i] = new FieldGen(fl[i], cp);
	}
    }

    @Override
    public void visitNEW(NEW obj) {
	System.out.println(obj + "   --->   "
		+ obj.getLoadClassType(cp).getClassName());

    }

    @Override
    public void visitALOAD(ALOAD obj) {
	System.out.println("\t\t" + obj.getName() + "   --->   "
		+ obj.getType(cp).getSignature());
	System.out.println("\t\t" + lv[obj.getIndex()]);
	LocalVariableGen l = lv[obj.getIndex()];
	System.err.println(l.getName());
    }

    @Override
    public void visitASTORE(ASTORE obj) {
	System.out.println(obj.getName() + "   --->   "
		+ obj.getType(cp).getSignature());
	System.out.println("\t\t" + lv[obj.getIndex()]);
	LocalVariableGen l = lv[obj.getIndex()];
	System.err.println(l.getName());
    }

    @Override
    public void visitPUTSTATIC(PUTSTATIC obj) {
	System.out.println("\t\t" + obj.getName() + "   --->   "
		+ obj.getType(cp).getSignature());

	System.out.println("\t\t" + obj.getFieldName(cp));
	System.out.println("\t\t" + obj.getFieldType(cp));
    }

    @Override
    public void visitPUTFIELD(PUTFIELD obj) {
	System.out.println("\t\t" + obj.getName() + "   --->   "
		+ obj.getType(cp).getSignature());

	System.out.println("\t\t" + obj.getFieldName(cp));
	System.out.println("\t\t" + obj.getFieldType(cp));
    }

    @Override
    public void visitGETSTATIC(GETSTATIC obj) {
	System.out.println("\t\t" + obj.getName() + "   --->   "
		+ obj.getType(cp).getSignature());
	System.out.println("\t\t" + obj.getFieldName(cp));
	System.out.println("\t\t" + obj.getFieldType(cp));
    }

    @Override
    public void visitGETFIELD(GETFIELD obj) {
	System.out.println("\t\t" + obj.getName() + "   --->   "
		+ obj.getType(cp).getSignature());
	System.out.println("\t\t" + obj.getFieldName(cp));
	System.out.println("\t\t" + obj.getFieldType(cp));
    }

    @Override
    public void visitRETURN(RETURN obj) {
	System.out.println("\t\t" + obj.getName() + "   --->   "
		+ obj.getType(cp).getSignature());
	System.out.println("\t\t" + obj.getType(cp));
    }

    public void start() {
	if (mg.isAbstract() || mg.isNative())
	    return;
	for (InstructionHandle ih = mg.getInstructionList().getStart(); ih != null; ih = ih
		.getNext()) {
	    Instruction i = ih.getInstruction();
	    System.out.println("\t" + i + "     "
		    + i.toString(cp.getConstantPool()));
	    // }
	    if (!visitInstruction(i)) {
		i.accept(this);
	    }
	    String a1 = "";

	}
    }

    void print(Object s) {
	System.out.println("\t" + s);
    }

    private boolean visitInstruction(Instruction i) {
	short opcode = i.getOpcode();

	return ((InstructionConstants.INSTRUCTIONS[opcode] != null)
		&& !(i instanceof ConstantPushInstruction) && !(i instanceof ReturnInstruction));
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
	System.out.println(String.format(format, "M", i.getReferenceType(cp),
		i.getMethodName(cp)));
	System.out.println("------------------------");
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
	// String s = i.getType()(cp).toString();
	LocalVariableGen[] f = mg.getLocalVariables();
	LineNumberGen[] lns = mg.getLineNumbers();
	System.out.println(String.format(format, "I", i.getReferenceType(cp),
		i.getMethodName(cp)));
	System.out.println("------------------------");
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL i) {
	System.out.println(String.format(format, "O", i.getReferenceType(cp),
		i.getMethodName(cp)));
	System.out.println("------------------------");
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC i) {
	System.out.println(String.format(format, "S", i.getReferenceType(cp),
		i.getMethodName(cp)));
	System.out.println("------------------------");
    }
}
