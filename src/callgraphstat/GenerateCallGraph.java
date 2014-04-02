/**
 *
 * @ProjectName StaticCallGraph
 *
 * @PackageName callgraphstat
 *
 * @FileName GenerateCallGraph.java
 * 
 * @FileCreated Dec 24, 2013
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
package callgraphstat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class GenerateCallGraph {
	private Description mainClass = null;
	private MethodVisitor mainMethod = null;

	public GenerateCallGraph(Description description) throws Exception {
		this.mainClass = description;
		// ---------------------
		// get main class fields info
		// ---------------------
		// initiate static - not done
		List<MethodVisitor> methods = this.mainClass
				.getMethodVisitorByName("main");
		if (methods.size() > 1) {
			throw new Exception(
					"Plase make sure class contains only one main method.");
		} else if (methods.size() == 0) {
			throw new Exception("No main method found.");
		} else {
			mainMethod = methods.get(0);
			start();
		}
	}

	private void start() throws Exception {
		try {
			this.mainMethod.start(null, new ArrayList<Object>(), false,
					new HashSet<String>(), false);
			this.mainMethod = null;
			this.mainClass = null;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}