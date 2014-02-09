package callgraphstat;

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
		// this.mainClass.getClassVisitor().start();
		// List<Integer> n = new ArrayList<Integer>();
		// n.add(1);
		// n.add(2);
		// n.add(3);
		// this.mainClass.getClassVisitor().values.put("A", n);
		// n = new ArrayList<Integer>();
		// n.add(5);
		// n.add(6);
		// n.add(7);
		// this.mainClass.getClassVisitor().values.put("B", n);
		//
		// this.mainClass.getClassVisitor().prints();
		// Description d = this.mainClass.clone();
		// d.getClassVisitor().prints();
		// n.add(500);
		// this.mainClass.getClassVisitor().values.put("B", n);
		// n = new ArrayList<Integer>();
		// n.add(8);
		// d.getClassVisitor().values.put("B", n);
		// d.getClassVisitor().prints();
		// this.mainClass.getClassVisitor().prints();

		// ExtractMethod m = d.getExtractedMethodByName("main").get(0);
		// m.getMethodVisitor().prints();

		// this.mainMethod.getMethodVisitor().start(null);
	}
}
