package callgraphstat;

import java.util.List;

public class GenerateCallGraph {
	private Description mainClass = null;
	private ExtractMethod mainMethod = null;

	public GenerateCallGraph(Description description) throws Exception {
		this.mainClass = description;
		// ---------------------
		// get main class fields info
		// ---------------------
		List<ExtractMethod> methods = this.mainClass
				.getExtractedMethodByName("main");
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
		// try to keep values from start --- not done
		this.mainClass.getClassVisitor().start();
		this.mainMethod.getMethodVisitor().start(null);
	}
}
