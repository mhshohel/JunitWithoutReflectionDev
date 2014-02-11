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
package callgraphstat;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JCallGraph {

	public static void main(String[] args) {
		try {
			String loc = "G:\\lnu\\5DV001 - Thesis Project\\Thesis - Jonas\\ThesisBackup\\JunitWithoutReflection.git.second\\JUnitWithoutReflection.Second\\bin\\callgraphstat";
			String mainClass = "callgraphstat.superclass.Example";
			// String mainClass = "observer.Printer";
			File file = new File(loc);
			JCallGraph jCallGraph = new JCallGraph(file, "callgraphstat");
			// JCallGraph jCallGraph = new JCallGraph(file, "observer");
			// Map<String, Description> ssss =
			// jCallGraph.getClassDescriptions();
			// System.out.println(ssss.get(
			// "callgraphstat.testclasses.JonasTestMain").getClassName());
			Description des = jCallGraph.getClassDescriptions().get(mainClass);
			// System.out.println(des.printNode());
			// make single call means check edges first

			long start = System.nanoTime();
			if (des != null) {
				// des.getClassVisitor().start();
				new GenerateCallGraph(des.copy());
			}
			long end = System.nanoTime();
			System.out.println("\n\n\n\t\t\t\t\tElapsed Time: "
					+ (double) (end - start) / 1000000000.0 + "s");

			// des.getClassVisitor().print();
			// Description d = des.copy();
			// d.getClassVisitor().print();
			// d.getClassVisitor().num = 100;
			// des.getClassVisitor().print();

			// print nodes
			// System.out.println("NODES\n_____________________");
			// List<String> nodes = des.getNodes();
			// for (String node : nodes) {
			// System.out.println(node);
			// }
			// print edges
			// System.out.println("EDGES\n_____________________");
			// List<String> edges = des.getSortedEdges();
			// System.out.println("Edges...");
			// for (String edge : edges) {
			// System.out.println(edge);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, Description> classDescriptions = new HashMap<String, Description>();
	private File file = null;
	private Set<Node> nodes = new HashSet<Node>();
	// String as src node
	private Map<String, Edge> edges = new HashMap<String, Edge>();

	public JCallGraph(File file, String pack) throws Exception {
		this.file = file;
		readFiles(this.file, pack);
		initializeInterfacesAndSuperClasses();
	}

	public Map<String, Description> getClassDescriptions() {
		return this.classDescriptions;
	}

	private void initializeInterfacesAndSuperClasses() throws Exception {
		for (Entry<String, Description> description : this.classDescriptions
				.entrySet()) {
			description.getValue().initializeInterfacesAndSuperClasses();
		}
	}

	private void readFiles(final File folder, final String pack)
			throws Exception {
		String pac = (pack.trim().equalsIgnoreCase("") ? "" : pack.concat("."));
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				if (fileEntry.getName().equalsIgnoreCase("superclass")) {
					// if (fileEntry.getName().equalsIgnoreCase("superclass")) {
					readFiles(fileEntry, pac.concat(fileEntry.getName()));
				}
			} else {
				if (pac.equalsIgnoreCase("callgraphstat.superclass.")) {
					// if (pac.equalsIgnoreCase("observer.")) {
					if (fileEntry.getName().endsWith(".class")) {
						URL url = this.file.toURI().toURL();
						URL[] urls = new URL[] { url };
						String cls = pac.concat(fileEntry.getName().replaceAll(
								".class", ""));
						@SuppressWarnings("resource")
						Class<?> clas = new URLClassLoader(urls).loadClass(cls);
						this.classDescriptions.put(clas.getName(),
								new Description(clas, this.classDescriptions));
					}
				}
			}
		}
	}
}
