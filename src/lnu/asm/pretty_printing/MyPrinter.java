package lnu.asm.pretty_printing;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.util.TraceMethodVisitor;

public class MyPrinter {

	public static void prettyPrint(ClassNode classNode) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("Class: "+classNode.name+"\n");
		buf.append("\tSource: "+classNode.sourceFile+"\n");
		buf.append("\tSignature: "+classNode.signature+"\n");
		buf.append("\tAccess: "+classNode.access+"\n");
		buf.append("\tExtends: "+classNode.superName+"\n");
		buf.append("\tImplements: "+classNode.interfaces+"\n");
		buf.append("\tOuterClass: "+classNode.outerClass+"\n");
		buf.append("\tOuterMethod: "+classNode.outerMethod+"\n");
		buf.append("\tOuterMethodDesc: "+classNode.outerMethodDesc+"\n");
		
		List<Attribute> attrs = classNode.attrs;
		if (attrs != null)
			for (Attribute attr : attrs) {
				buf.append("\tType: "+attr.type+"\n");
			}
		List<AnnotationNode> annots = classNode.visibleAnnotations;
		if (annots != null)
			for (AnnotationNode annot :annots) {
				buf.append("\tVisAnnot: "+annot.desc+"\n");
			}
		annots = classNode.invisibleAnnotations;
		if (annots != null)
			for (AnnotationNode annot :annots) {
				buf.append("\tInvisAnnot: "+annot.desc+"\n");
			}
		
		/* Fields */
		buf.append("\n");
		List<FieldNode> fields = classNode.fields;
		for (FieldNode field : fields) {
			buf.append("\tField: "+field.name+"\n");
			buf.append("\t\tSignature: "+field.signature+"\n");
			buf.append("\t\tDesc: "+field.desc+"\n");
			buf.append("\t\tAccess: "+field.access+"\n");
			buf.append("\t\tValue: "+field.value+"\n");
			List<Attribute> fattrs = field.attrs;
			if (fattrs != null)
				for (Attribute attr : fattrs) {
					buf.append("\t\tType: "+attr.type+"\n");
				}
		}
		
		/* Methods */
		buf.append("\n");
		List<MethodNode> meths = classNode.methods;
		for (MethodNode m : meths) {
			buf.append("\n\tMethod: "+m.name+"\n");
			buf.append("\t\tSignature: "+m.signature+"\n");
			buf.append("\t\tDesc: "+m.desc+"\n");
			buf.append("\t\tAccess: "+m.access+"\n");
			List<Attribute> mattrs = m.attrs;
			if (mattrs != null)
				for (Attribute attr : mattrs) {
					buf.append("\t\tType: "+attr.type+"\n");
				}
			
//			Textifier pr = new Textifier();
//			TraceMethodVisitor vis = new TraceMethodVisitor(pr,new PrintWriter(System.out));
//			m.accept(vis);
//			List<Object> text = pr.getText();
//			System.out.println(text);
			//pr.print(System.out);
			
			
			//buf.append(getBody(m));
		}
		
		System.out.println(buf.toString());
	}
	
	private static String getBody(MethodNode m) {
		StringBuffer buf = new StringBuffer();
		
		InsnList ilist = m.instructions;
		ListIterator<AbstractInsnNode> it = ilist.iterator();
		while (it.hasNext()) {
			AbstractInsnNode ins = it.next();
			buf.append("\t\t\t"+ins+"\n");
			//System.out.println(ins);
		}
		
		return buf.toString();
		
	}
	
	
	public static void tracePrint(ClassNode classNode) {
		String fileName = "/"+classNode.name+".class";
		InputStream is = MyPrinter.class.getResourceAsStream(fileName);
        ClassReader cr = null;
		try {
			cr = new ClassReader(is);
			cr.accept(new TraceClassVisitor(new PrintWriter(System.out)), 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
}
