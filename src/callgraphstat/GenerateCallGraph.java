package callgraphstat;

import java.util.ArrayList;
import java.util.List;

public class GenerateCallGraph {
	private Description mainClass = null;
	private MethodVisitor mainMethod = null;

	public GenerateCallGraph(Description description) throws Exception {
		this.mainClass = description;
		// ---------------------
		// get main class fields info
		// ---------------------
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

	private void start() {
		// // try to keep values from start --- not done
		// in class visitor pass params, maybe static var too, check that
		this.mainClass.getClassVisitor().start();
		// pass source and params
		this.mainMethod.start(null, new ArrayList<Object>());
		this.mainMethod = null;
		this.mainClass = null;
	}
}
