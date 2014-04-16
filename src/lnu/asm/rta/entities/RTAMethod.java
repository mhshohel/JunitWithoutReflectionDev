package lnu.asm.rta.entities;

import java.util.HashMap;

import org.objectweb.asm.tree.MethodNode;

public class RTAMethod {
	private static HashMap<MethodNode,RTAMethod> node2method = new HashMap<MethodNode,RTAMethod>();
	
	public final MethodNode node;
	public final String signature;
	public final RTAClass ownerClass;
	public final boolean isApplication;
	
	public RTAMethod(MethodNode mNode, RTAClass owner, boolean isApplication) {
		node = mNode;
		signature = mNode.name+mNode.desc;
		ownerClass = owner;
		this.isApplication = isApplication;
		node2method.put(mNode, this);
	}
	
	public String toString() { return ownerClass.classNode.name+"."+signature; }
	
	public static RTAMethod getRTAMethod(MethodNode mNode) { return node2method.get(mNode); }

}
