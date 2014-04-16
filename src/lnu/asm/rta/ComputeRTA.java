/**
 * 
 */
package lnu.asm.rta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import lnu.asm.ASM;
import lnu.asm.rta.entities.RTAClass;
import lnu.asm.rta.entities.RTAMethod;
import lnu.asm.rta.entities.WorkList;
import lnu.util.Timer;
import lnu.vps.VpsSpec;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * 
 * @author jonasl
 *
 */

public class ComputeRTA extends MethodVisitor {
	private final VpsSpec vps;
	private final String[] application_filter;
	private WorkList<RTAMethod> worklist;
	private final HashSet<ClassNode> initialized = new HashSet<ClassNode>();
	private final HashSet<RTAMethod> processed = new HashSet<RTAMethod>();
	private HashSet<RTAMethod> virtual_methods = new HashSet<RTAMethod>();    // Contains virtual calls
	
	private RTAMethod current_method;
	private boolean contains_virtual;

	public ComputeRTA(VpsSpec vps) { 
		super(Opcodes.ASM4, null); 
		this.vps = vps;
		
		/* Setup application filter */
		String[] include = vps.includeStrings;
		application_filter = new String[include.length];
    	for (int i=0;i<include.length;i++) {
    		String str = include[i];
    		String filter = str.replace('.','/');
    		application_filter[i] = filter;
    		//System.out.println(str+"\t"+filter);
    	}
		
		setup_ASM();
		setup_worklist();
		start_working();   // The actual RTA computation
		traverse_methods(); // Update method information
		
		
//		Iterator<ClassNode> it = initialized.iterator();
//		while (it.hasNext()) {
//			ClassNode cNode = it.next();
//			RTAClass rtaClass = RTAClass.getClass(cNode.name);
//			if (rtaClass.isApplication)
//				System.out.println(rtaClass);
//		}
	
	}
	
	private void setup_ASM() {
		/* Setup Class Path */
		String[] paths = vps.classPaths;
		System.out.println("*** ASM Class Paths ***");
		for (String path : paths) {
			System.out.println(path);
			ASM.addPath(path);
		}	
		System.out.println();
	}
	
	private void traverse_methods() {
		int count = 0;
		PostRTAVisitor post_visitor = new PostRTAVisitor(initialized);
		Iterator<RTAMethod> it = processed.iterator();
		System.out.println("\nPost analysis traversal of reachable methods");
		Timer rta_timer = new Timer();
		rta_timer.tic();   // Start timer
		while (it.hasNext()) {
			RTAMethod rtaMethod = it.next();
			if (!rtaMethod.isApplication) continue;
			
			System.out.println((++count)+"\t"+rtaMethod);
			post_visitor.setCurrentMethod(rtaMethod);
			MethodNode mNode = rtaMethod.node;
			//System.out.println("\t"+mNode.);
			mNode.accept(post_visitor);
		}
		System.out.println("Post analysis traversal: "+rta_timer.toc()+"ms");
		post_visitor.saveCGData();
	}
	
	private void setup_worklist() {
		worklist = new WorkList<RTAMethod>();
		
		/* Find/Handle java.lang.Object Class */		
		ClassNode classNode = ASM.getClassNode("java/lang/Object");
		initialize(classNode);  // Adds clinit to worklist
		
		/* Find Object <init> Method */
		List<MethodNode> meths = classNode.methods;
		for (MethodNode mNode : meths) {
			if (mNode.name.equals("<init>")) {
				//addToWorklist(mNode);
		    	RTAMethod rta = RTAMethod.getRTAMethod(mNode);
		    	worklist.add(rta);
			}
		}
		
		/* Find/Handle java.lang.System Class */		
		classNode = ASM.getClassNode("java/lang/System");
		initialize(classNode);  // Add clinit to worklist
		
		/* Find System.initializeSystemClass Method */
		meths = classNode.methods;
		for (MethodNode mNode : meths) {
			if (mNode.name.equals("initializeSystemClass")) {
				RTAMethod rta = RTAMethod.getRTAMethod(mNode);
		    	worklist.add(rta);
			}
		}
		
		/* Find/Handle Program Entry Points */
		String[] ePoints = vps.entryPoints;
		
		for (String main_class : ePoints) {
			main_class = main_class.replace('.',File.separatorChar);		
			classNode = ASM.getClassNode(main_class);
			initialize(classNode);

			/* Find Main Method */
			meths = classNode.methods;
			for (MethodNode mNode : meths) {
				if (mNode.name.equals("main")) {
					RTAMethod rta = RTAMethod.getRTAMethod(mNode);
					worklist.add(rta);
					//PrettyPrinter.print(mNode);
				}
			}
		}
	}
	
	private void start_working() {
		int iteration_count = 0;
		Timer rta_timer = new Timer();
		rta_timer.tic();   // Start timer
		
		/* First iteration */
		process_worklist(worklist);
		System.out.println("---- Iteration: "+(++iteration_count));
		System.out.println("\nFirst Iteration: "+rta_timer.toc()+"ms");
		System.out.println("Processed Methods: "+processed.size());
		System.out.println("Worklist Methods: "+worklist.countAdded());
		System.out.println("Virtual Methods: "+virtual_methods.size());
		System.out.println("Initialized Classes: "+initialized.size());
		
		/* Repeat until no new classes are initialized */
		int before, after;
		rta_timer.tic();  // Start timer again
		do {
			before = initialized.size();
			worklist = new WorkList<RTAMethod>();
			Iterator<RTAMethod> it =virtual_methods.iterator();
			while (it.hasNext())
				worklist.add(it.next());

			process_worklist(worklist);
			after = initialized.size();
			iteration_count++;
			//System.out.println("---- Iteration: "+iteration_count);
		} while (before != after);
		
		System.out.println("\nIteration 2 - "+iteration_count+": "+rta_timer.toc()+"ms");
		System.out.println("Processed Methods: "+processed.size());
		System.out.println("Worklist Methods: "+worklist.countAdded());
		System.out.println("Virtual Methods: "+virtual_methods.size());
		System.out.println("Initialized Classes: "+initialized.size());
		
		System.out.println("\nMem "+rta_timer.currentMem());
		System.out.println("Mem (after GC)"+rta_timer.gcMem());
		
	}
	
	private void process_worklist(WorkList<RTAMethod> worklist) {
		while (!worklist.isEmpty()) {
			current_method = worklist.getNext();
			processed.add(current_method);
			MethodNode mNode = current_method.node;
			//System.out.println("Processing "+rta);
			contains_virtual = false;
			mNode.accept(this);
			//rta_visitor.afterTraversal();
			if (contains_virtual)
				virtual_methods.add(current_method);
		}
	}
	
	
    // -------------------------------------------------------------------------
	// Overriding certain methods in MethodVisitor
    // Normal instructions
    // -------------------------------------------------------------------------

    /**
     * Visits a zero operand instruction.
     * 
     * @param opcode
     *            the opcode of the instruction to be visited. This opcode is
     *            either NOP, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1,
     *            ICONST_2, ICONST_3, ICONST_4, ICONST_5, LCONST_0, LCONST_1,
     *            FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD,
     *            LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
     *            IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE,
     *            SASTORE, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1,
     *            DUP2_X2, SWAP, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB,
     *            IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM,
     *            FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR,
     *            IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR, I2L, I2F, I2D,
     *            L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S,
     *            LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN,
     *            DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER,
     *            or MONITOREXIT.
     */
    @Override
    public void visitInsn(final int opcode) { }
    
    /**
     * Visits an instruction with a single int operand.
     * 
     * @param opcode
     *            the opcode of the instruction to be visited. This opcode is
     *            either BIPUSH, SIPUSH or NEWARRAY.
     * @param operand
     *            the operand of the instruction to be visited.<br>
     *            When opcode is BIPUSH, operand value should be between
     *            Byte.MIN_VALUE and Byte.MAX_VALUE.<br>
     *            When opcode is SIPUSH, operand value should be between
     *            Short.MIN_VALUE and Short.MAX_VALUE.<br>
     *            When opcode is NEWARRAY, operand value should be one of
     *            {@link Opcodes#T_BOOLEAN}, {@link Opcodes#T_CHAR},
     *            {@link Opcodes#T_FLOAT}, {@link Opcodes#T_DOUBLE},
     *            {@link Opcodes#T_BYTE}, {@link Opcodes#T_SHORT},
     *            {@link Opcodes#T_INT} or {@link Opcodes#T_LONG}.
     */
    @Override
    public void visitIntInsn(final int opcode, final int operand) {    }

    /**
     * Visits a local variable instruction. A local variable instruction is an
     * instruction that loads or stores the value of a local variable.
     * 
     * @param opcode
     *            the opcode of the local variable instruction to be visited.
     *            This opcode is either ILOAD, LLOAD, FLOAD, DLOAD, ALOAD,
     *            ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
     * @param var
     *            the operand of the instruction to be visited. This operand is
     *            the index of a local variable.
     */
    @Override
    public void visitVarInsn(final int opcode, final int var) {  }

    /**
     * Visits a type instruction. A type instruction is an instruction that
     * takes the internal name of a class as parameter.
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
     * @param type
     *            the operand of the instruction to be visited. This operand
     *            must be the internal name of an object or array class (see
     *            {@link Type#getInternalName() getInternalName}).
     */
    @Override
    public void visitTypeInsn(final int opcode, final String type) {  
    	if (opcode == Opcodes.NEW) {
    		//System.out.println("NEW\t"+type);
    		ClassNode tgtClass = ASM.getClassNode(type);
    		initialize(tgtClass);
    	}
    	
    }

    /**
     * Visits a field instruction. A field instruction is an instruction that
     * loads or stores the value of a field of an object.
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
     * @param owner
     *            the internal name of the field's owner class (see
     *            {@link Type#getInternalName() getInternalName}).
     * @param name
     *            the field's name.
     * @param desc
     *            the field's descriptor (see {@link Type Type}).
     */
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {    
    	if (opcode == Opcodes.GETSTATIC || opcode == Opcodes.PUTSTATIC) {
    		//System.out.println("STATIC ACCESS\t"+owner);
    		ClassNode tgtClass = ASM.getClassNode(owner);
    		initialize(tgtClass);
    	}
    }

    /**
     * Visits a method instruction. A method instruction is an instruction that
     * invokes a method.
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
     *            INVOKEINTERFACE.
     * @param owner
     *            the internal name of the method's owner class (see
     *            {@link Type#getInternalName() getInternalName}).
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type Type}).
     */
    @Override
    public void visitMethodInsn(final int opcode, final String owner,
            final String name, final String desc) {
    	if (opcode == Opcodes.INVOKESPECIAL) {
    		//System.out.println("INVOKESPECIAL\t"+owner+"\t"+name+"\t"+desc);
    		ClassNode tgtClass = ASM.getClassNode(owner);
    		if (initialized.contains(tgtClass)) {
    			RTAClass rtaClass = RTAClass.getClass(owner);
    			RTAMethod tgt = RTA.resolveExplicit(rtaClass, name+desc);
        		worklist.add(tgt);  
    		}
    		else {
    			System.out.println("No target for INVOKESPECIAL\t"+owner+"\t"+name+"\t"+desc);
    		}
    	}
    	else if (opcode == Opcodes.INVOKESTATIC) {
    		//System.out.println("INVOKESTATIC\t"+owner+"\t"+name+"\t"+desc);
    		ClassNode tgtClass = ASM.getClassNode(owner);
    		initialize(tgtClass);
    		RTAClass rtaClass = RTAClass.getClass(owner);
    		RTAMethod tgt = RTA.resolveExplicit(rtaClass, name+desc);
    		worklist.add(tgt);             
    	}
    	else if (opcode == Opcodes.INVOKEVIRTUAL) {
    		//System.out.println("INVOKEVIRTUAL\t"+owner+"\t"+name+"\t"+desc);
    		if (ASM.isArrayType(owner)) {
    			RTAClass rtaClass = RTAClass.getClass("java/lang/Object");
    			RTAMethod tgt = rtaClass.getMethodBySignature(name+desc);
    			worklist.add(tgt);
    		}
    		else {
        		contains_virtual = true;
            	ClassNode tgtClass = ASM.getClassNode(owner);
            	if (initialized.contains(tgtClass)) {
        			RTAClass rtaClass = RTAClass.getClass(owner);
        			ArrayList<RTAMethod> tgts = RTA.resolveVirtual(rtaClass, name+desc);
        	    	
        	    	for (RTAMethod rta : tgts)
        				worklist.add(rta);
        		}
//    			else {
//    				System.out.println("No initialized objects for INVOKEVIRTUAL\t"+owner+"\t"+name+"\t"+desc);
//    			}  	
    		}
    	}
    	else if (opcode == Opcodes.INVOKEINTERFACE) {
    		contains_virtual = true;
        	ClassNode tgtClass = ASM.getClassNode(owner);
        	if (initialized.contains(tgtClass)) {
    			RTAClass rtaClass = RTAClass.getClass(owner);
    			ArrayList<RTAMethod> tgts = RTA.resolveInterface(rtaClass, name+desc);
    			for (RTAMethod rta : tgts)
    				worklist.add(rta);
        	}
//    		else { // tgts = null ==> non-initialized interface
//    			System.out.println("No initialized objects for INVOKEINTERFACE\t"+owner+"\t"+name+"\t"+desc);
//    		}  		
    		
    	}
    }
    /**
     * Visits an invokedynamic instruction.
     * 
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type Type}).
     * @param bsm
     *            the bootstrap method.
     * @param bsmArgs
     *            the bootstrap method constant arguments. Each argument must be
     *            an {@link Integer}, {@link Float}, {@link Long},
     *            {@link Double}, {@link String}, {@link Type} or {@link Handle}
     *            value. This method is allowed to modify the content of the
     *            array so a caller should expect that this array may change.
     */
    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm,
            Object... bsmArgs) {
    	System.out.println("INVOKEVIRTUAL\t"+name+"\t"+desc);
    }

    /**
     * Visits a jump instruction. A jump instruction is an instruction that may
     * jump to another instruction.
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ,
     *            IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE,
     *            IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL.
     * @param label
     *            the operand of the instruction to be visited. This operand is
     *            a label that designates the instruction to which the jump
     *            instruction may jump.
     */
    @Override
    public void visitJumpInsn(int opcode, Label label) {    }

    /**
     * Visits a label. A label designates the instruction that will be visited
     * just after it.
     * 
     * @param label
     *            a {@link Label Label} object.
     */
    @Override
    public void visitLabel(final Label label) {  }
	
	
    // -------------------------------------------------------------------------
	// Visitor Help Methods
    // -------------------------------------------------------------------------	
	
	 private int init_count = 0;
	    public void initialize(ClassNode cNode) {
	    	if (initialized.contains(cNode)) return;  // Already initialized
	    	
	    	init_count++;
	    	 	
	    	String superName = cNode.superName;
	    	ClassNode superClass = null;
	    	RTAClass rtaSuper = null;
	    	if (superName != null)  {  // java.lang.Object
	    		/* Start recursive initialization of super classes */
	    		superClass = ASM.getClassNode(superName);
	    		initialize(superClass);
	    		rtaSuper = RTAClass.getClass(superName);  		
	    	}    	
	    	boolean isApplication = isApplication(cNode);
	    	RTAClass rtaClass = new RTAClass(cNode,rtaSuper,isApplication);
	    	if (rtaSuper != null)
	    		rtaSuper.addSubClass(rtaClass);
	    	
	    	List<String> ifaces = cNode.interfaces;  // Initialize super ifaces
	    	for (String iface : ifaces) {         
	    		//System.out.println(cNode.name +" --> "+iface); 
	    		RTAClass iClass = initializeInterface(iface);
	    		iClass.addSubClass(rtaClass);
	    	}
	    	
	    	RTAMethod clinit = rtaClass.getMethodBySignature("<clinit>()V");
	    	if (clinit!=null)
	    		worklist.add(clinit);
	    	initialized.add(cNode);
	    	String access = "";
	    	if (rtaClass.isAbstract())
				access = "(abstract)";
	    	System.out.println(init_count+"\tInitializing "+cNode.name+"\t"+access);
	    	    	
	    }

	    
	    private RTAClass initializeInterface(String iface_name) {
	    	ClassNode iNode = ASM.getClassNode(iface_name);
			if (initialized.contains(iNode)) 
				return RTAClass.getClass(iface_name);    // Already initialized
			else {
				init_count++;
				
				List<String> ifaces = iNode.interfaces;
				if (ifaces.size()>1)
					throw new RuntimeException("Interface extending more than one interface?");
				RTAClass superClass = null;
				if (ifaces.size()>0) {
					String superIface = ifaces.get(0);
					superClass = initializeInterface(superIface);
					//System.err.println(iface_name +" --> "+superIface);
				}
				boolean isApplication = isApplication(iNode);
				RTAClass rtaClass = new RTAClass(iNode,superClass,isApplication);
				if (superClass != null) {
					superClass.addSubClass(rtaClass);
					
				}
				initialized.add(iNode);
						
				System.out.println(init_count+"\tInitializing "+iNode.name+"\t(interface)");
				return rtaClass;
			}   	
	    }
	    
	    private boolean isApplication(ClassNode cNode) {
	    	String className =  cNode.name;
	    	for (String filter : application_filter) {
	    		if (className.startsWith(filter))
	    			return true;
	    			//System.out.println(str);
	    	}
	    	//System.exit(1);
	    	return false;
	    }
}
