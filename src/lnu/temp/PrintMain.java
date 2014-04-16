/**
 * 
 */
package lnu.temp;

import java.io.IOException;

import lnu.asm.ASM;
import lnu.asm.pretty_printing.MyPrinter;
import lnu.asm.pretty_printing.PrintClassVisitor;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * @author jlnmsi
 *
 */
public class PrintMain {


	public static void main(String[] args) throws IOException {
		
		//String curDir = System.getProperty("user.dir");
		//System.out.println("User Dir: "+curDir);
		//System.exit(-1);
		
		ASM.addPath("/Users/jonasl/Software/RTA/ASM_RTA/bin");
		
		//ClassNode clz = ASM.getClassNode("lnu/temp/Testing");
		ClassNode clz = ASM.getClassNode("lnu/temp/ClassA");
		MyPrinter.tracePrint(clz);


	}

}
