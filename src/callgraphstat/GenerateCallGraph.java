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
					new HashSet<String>());
			this.mainMethod = null;
			this.mainClass = null;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}