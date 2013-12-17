/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraph
 *
 * @FileName JCallGraph.java
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
package tools.staticcallgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.bcel.classfile.JavaClass;

public class JCallGraph {
    private List<Description> testDescriptionList = new ArrayList<Description>();
    private List<Description> classDescriptions = new ArrayList<Description>();

    // all description of classes and testclasses list
    public JCallGraph(List<Description> classDescriptions,
	    List<Description> testClassDescriptions) {
	if (classDescriptions != null) {
	    this.classDescriptions = classDescriptions;
	}
	if (testClassDescriptions != null) {
	    this.testDescriptionList = testClassDescriptions;
	}
    }

    private List<String> node = new ArrayList<String>();
    private List<String> edge = new ArrayList<String>();

    public void addNode(String node) {
	if (!this.node.contains(node)) {
	    this.node.add(node);
	}
    }

    public List<String> getNode() {
	Collections.sort(this.node);
	return this.node;
    }

    public void lookInsideClass(JavaClass javaClass,
	    Description entryDescription, boolean isGeneratedCode) {
	ClassVisitor visitor = new ClassVisitor(this, javaClass,
		entryDescription, true);
	visitor.start();
    }

    public void lookInsideClass(OPCodeDescription opCodeDescription,
	    Description entryDescription, JavaClass javaClass) {
	ClassVisitor visitor = new ClassVisitor(this, opCodeDescription,
		entryDescription, javaClass);
	visitor.start();
    }

    public Description getDescriptionByActualClassName(String name) {
	if (!testDescriptionList.isEmpty()) {
	    for (Description description : classDescriptions) {
		if (description.getClassName().equals(name)) {
		    return description;
		}
	    }
	}
	for (Description description : classDescriptions) {
	    if (description.getClassName().equals(name)) {
		testDescriptionList.add(description);
		return description;
	    }
	}
	return null;
    }
}
