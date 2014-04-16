package lnu.asm.rta.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lnu.asm.ASM;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class RTAClass {
	public final ClassNode classNode;
	public final RTAClass superClass;
	public final RTAMethod[] methods;
	public final ArrayList<RTAClass> subClasses = new ArrayList<RTAClass>();
	public final boolean isApplication;
	//public final boolean isInterface;
	private static final HashMap<String,RTAClass> name2class = new HashMap<String,RTAClass>();	

	//public RTAClass(ClassNode cNode,RTAClass superClass, boolean isInterface) {
	public RTAClass(ClassNode cNode,RTAClass superClass, boolean isApplication) {
		classNode = cNode;
		this.superClass = superClass;
		this.isApplication = isApplication;
		methods = addMethods();
		name2class.put(cNode.name, this);
	}
	
	public boolean isInterface() {return (classNode.access & Opcodes.ACC_INTERFACE) != 0;}
	public boolean isAbstract() {return (classNode.access & Opcodes.ACC_ABSTRACT) != 0;}
	
	public void addSubClass(RTAClass sub) { subClasses.add(sub);}
	
	public RTAMethod getMethodBySignature(String signature) {
		for (RTAMethod rta : methods) {
			if (signature.equals(rta.signature))
				return rta;
		}
		return null;
	}
	
	@Override
	public String toString() { return classNode.name; }
	
		
	public static RTAClass getClass(String className) { 
		RTAClass clz = name2class.get(className);
		if (clz == null) {
			throw new RuntimeException("Accessing non-initialized class: "+className);
		}
		return clz;
	}
	
	private RTAMethod[] addMethods() {
		List<MethodNode> meths = classNode.methods;
		RTAMethod[] methods = new RTAMethod[meths.size()];
		for (int i=0;i<methods.length;i++) {					
			MethodNode m = meths.get(i);
			methods[i] = new RTAMethod(m,this,isApplication);
		}
		return methods;
	}

}
