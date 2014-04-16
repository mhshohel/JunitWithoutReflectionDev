/**
 * 
 */
package lnu;

import java.io.IOException;
import java.io.InputStream;

import lnu.asm.rta.ComputeRTA;
import lnu.vps.VpsParser;
import lnu.vps.VpsSpec;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author jonasl
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		String curDir = System.getProperty("user.dir");
		System.out.println("User Dir: "+curDir);
//		System.exit(-1);
		
		/* VPS files Directory */
		//String vps_path = "/Users/jonasl/Software/vizz_suite/vizz_project_specs/";
		String vps_path = "/Users/jlnmsi/Software/ASM/ASM_RTA/src/lnu/test/";
			
		/* Select VPS file */
		//String path = vps_path+"vps_files/asm_rta.vps";
		//String path = vps_path+"vps_files/javac1.3.vps";
		//String path = vps_path+"vps_files/observer.vps";
		String path = vps_path+"small_programs/vps_files/observer.vps";
    	
		/* Read VPS File */
		VpsSpec vps = VpsParser.parseFile(path);
		
		/* Compute RTA Call Graph */
		new ComputeRTA(vps);
		System.exit(-1);
		

	}
	

}
