package lnu.asm.pretty_printing;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class PrettyPrinter {

	public PrettyPrinter() {
		// TODO Auto-generated constructor stub
	}
	
	public static void print(MethodNode m) {
		System.out.println("\n"+m.name+m.desc);
		m.accept(new PrintMethodVisitor());	
	}
	
	public static void print(ClassNode c) {
		c.accept(new PrintClassVisitor());	
	}

}
