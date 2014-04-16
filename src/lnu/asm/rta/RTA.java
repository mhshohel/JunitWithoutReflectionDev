package lnu.asm.rta;

import java.util.ArrayList;

import org.objectweb.asm.tree.ClassNode;

import lnu.asm.ASM;
import lnu.asm.rta.entities.RTAClass;
import lnu.asm.rta.entities.RTAMethod;

public class RTA {

    public static RTAMethod resolveExplicit(RTAClass rtaClass, String signature) {
    	RTAMethod tgt = null;
		do {
			 tgt = rtaClass.getMethodBySignature(signature);
			rtaClass = rtaClass.superClass;
		} while(tgt == null);
		return tgt;
    }
    
    public static ArrayList<RTAMethod> resolveVirtual(RTAClass rtaClass, String signature) {
    	
			ArrayList<RTAMethod> tgts = new ArrayList<RTAMethod>();
			RTAMethod primary = RTA.resolveExplicit(rtaClass,signature);
			tgts.add(primary);
	    	for (RTAClass subClass : rtaClass.subClasses)    // Search in subclasses
	    		resolveVirtual(subClass,signature,tgts);
	    	return tgts;
    }
    
    private static void resolveVirtual(RTAClass rtaClass, String signature,ArrayList<RTAMethod> tgts) {
    	RTAMethod tgt = rtaClass.getMethodBySignature(signature);
    	if (tgt != null) {
    		//System.out.println("\t"+tgt);
    		tgts.add(tgt);
    	}
    	
    	for (RTAClass subClass : rtaClass.subClasses)    // Recursive search in subclasses
    		resolveVirtual(subClass,signature,tgts);
    }
    
    public static ArrayList<RTAMethod> resolveInterface(RTAClass rtaClass, String signature) {
		/* Find first layer of initialized concrete subclasses */
		ArrayList<RTAClass> concrete_subs = new ArrayList<RTAClass>();
		for (RTAClass subClass : rtaClass.subClasses) {
			findSubClasses(subClass,concrete_subs);
		}
		//System.err.println(rtaClass+" --> "+concrete_subs);
		
		/* Collect targets starting in each class in first layer */
		ArrayList<RTAMethod> tgts = new ArrayList<RTAMethod>();
		for (RTAClass subClass : concrete_subs) {
			RTAMethod primary = RTA.resolveExplicit(subClass,signature);
			tgts.add(primary);
			for (RTAClass subsub : subClass.subClasses)    // Search in subclasses
	    		resolveVirtual(subsub,signature,tgts);
		}
		return tgts;
    }
    
    private static void findSubClasses(RTAClass rtaClass, ArrayList<RTAClass> concrete_subs) {   	
    	if (rtaClass.isInterface() || rtaClass.isAbstract()) {
    		for (RTAClass subClass : rtaClass.subClasses) {
				findSubClasses(subClass,concrete_subs);
			}
    	}
    	else // Concrete class
    		concrete_subs.add(rtaClass);
    }

}
