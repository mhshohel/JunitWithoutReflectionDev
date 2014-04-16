/**
 * PostVisitor.java
 * 1 nov 2013
 */
package lnu.asm.rta;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import lnu.asm.ASM;
import lnu.asm.rta.entities.RTAClass;
import lnu.asm.rta.entities.RTAMethod;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author jonasl
 *
 */
public class PostRTAVisitor extends MethodVisitor {
	private final HashSet<ClassNode> initialized;
	private RTAMethod current_method;
	private final HashSet<String> cg_edges = new HashSet<String>();
	private final HashSet<String> reachable_methods = new HashSet<String>();
	
	public PostRTAVisitor(HashSet<ClassNode> initialized) {
		super(Opcodes.ASM4, null);
		this.initialized = initialized;
	}
	
	public void setCurrentMethod(RTAMethod current) {
		current_method = current;
		reachable_methods.add(current_method.toString());
	}
	
	@Override
	public void visitMethodInsn(final int opcode, final String owner,
			final String name, final String desc) {
		//System.out.println("\t"+owner+"\t"+name);
		

		if (opcode == Opcodes.INVOKESPECIAL) {
			//System.out.println("INVOKESPECIAL\t"+owner+"\t"+name+"\t"+desc);
			ClassNode tgtClass = ASM.getClassNode(owner);
			if (!initialized.contains(tgtClass)) {
				System.err.println("No initialized target for INVOKESPECIAL\t"+owner+"\t"+name+"\t"+desc);
			}
			else {
				RTAClass rtaClass = RTAClass.getClass(owner);
				RTAMethod tgt = RTA.resolveExplicit(rtaClass, name+desc);
				if (tgt.isApplication)
					addCGEdge(tgt);
				//System.out.println("\t  MCall "+owner+"\t"+name);
			}
		}
		else if (opcode == Opcodes.INVOKESTATIC) {
			//System.out.println("INVOKESTATIC\t"+owner+"\t"+name+"\t"+desc);
			RTAClass rtaClass = RTAClass.getClass(owner);
			RTAMethod tgt = RTA.resolveExplicit(rtaClass, name+desc);
			if (tgt.isApplication)
				addCGEdge(tgt);
			//System.out.println("\t  SCall "+owner+"\t"+name);        
		}
		else if (opcode == Opcodes.INVOKEVIRTUAL) {
			//System.out.println("INVOKEVIRTUAL\t"+owner+"\t"+name+"\t"+desc);
			if (ASM.isArrayType(owner)) {  // Calls targeting arrays
				RTAClass rtaClass = RTAClass.getClass("java/lang/Object");
				RTAMethod tgt = rtaClass.getMethodBySignature(name+desc);
				if (tgt.isApplication)
					addCGEdge(tgt);
				//System.out.println("\t  ACall "+owner+"\t"+name); 
				return;
			}
			
			ClassNode tgtClass = ASM.getClassNode(owner);
			if (!initialized.contains(tgtClass)) {
				System.err.println("No initialized target for INVOKEVIRTUALL\t"+owner+"\t"+name+"\t"+desc);
			}
			else {
				RTAClass rtaClass = RTAClass.getClass(owner);
				ArrayList<RTAMethod> tgts = RTA.resolveVirtual(rtaClass, name+desc);
				for (RTAMethod tgt : tgts)
					if (tgt.isApplication)
						addCGEdge(tgt);
				//System.out.println("\t  VCall "+owner+"\t"+name+"\t"+tgts.size()); 
			}
		}
		else if (opcode == Opcodes.INVOKEINTERFACE) {
			ClassNode tgtClass = ASM.getClassNode(owner);
			if (!initialized.contains(tgtClass)) {
				System.err.println("No initialized target for INVOKEINTERFACE\t"+owner+"\t"+name+"\t"+desc);
			}
			else {
				RTAClass rtaClass = RTAClass.getClass(owner);
				ArrayList<RTAMethod> tgts = RTA.resolveInterface(rtaClass, name+desc);
				for (RTAMethod tgt : tgts)
					if (tgt.isApplication)
						addCGEdge(tgt);
				//System.out.println("\t  ICall "+owner+"\t"+name+"\t"+tgts.size()); 
			}

		}
	}
	
	
	private void addCGEdge(RTAMethod tgtMethod) {
		String edge = current_method.toString()+" --> "+tgtMethod.toString();
		cg_edges.add(edge);
	}
	
	public void saveCGData() {
		// Setup output directory
		String curDir = System.getProperty("user.dir");
		File outDir = new File(curDir+File.separatorChar+"output");
		if (!outDir.exists())
			outDir.mkdirs();
		
		// Print edges
		String[] edges = cg_edges.toArray(new String[0]);
		Arrays.sort(edges);
		File outFile = new File(outDir.getAbsolutePath()+File.separatorChar+"cg_edges.txt");
		saveText(edges,outFile);
//		for (int i=0;i<edges.length;i++)
//			System.out.println((i+1)+"\t"+edges[i]);		
		System.out.println("\nSaved "+edges.length+" edges in "+outFile.getAbsolutePath());
		
		// Print nodes
		String[] nodes = reachable_methods.toArray(new String[0]);
		Arrays.sort(nodes);
		outFile = new File(outDir.getAbsolutePath()+File.separatorChar+"cg_nodes.txt");
		saveText(nodes,outFile);
//		for (int i=0;i<nodes.length;i++)
//			System.out.println((i+1)+"\t"+nodes[i]);		
		System.out.println("Saved "+nodes.length+" nodes in "+outFile.getAbsolutePath());
	}
	
	private void saveText(String[] text, File text_file) {
		try {
			PrintWriter writer = new PrintWriter(text_file);
			for (String str : text)
				writer.println(str);
			writer.close();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}
	
}
