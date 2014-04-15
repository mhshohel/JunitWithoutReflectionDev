/**
 *
 * @ProjectName StaticCallGraph
 *
 * @PackageName callgraphstat
 *
 * @FileName JCallGraph.java
 * 
 * @FileCreated Jan 02, 2014
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class JCallGraph {

	public static void main(String[] args) {
		try {
			// String loc =
			// "G:\\lnu\\5DV001 - Thesis Project\\Thesis - Jonas\\ThesisBackup\\JunitWithoutReflection.git.second\\JUnitWithoutReflection.Second\\bin\\callgraphstat";
			// String loc =
			// "G:\\lnu\\5DV001 - Thesis Project\\Thesis - Jonas\\ThesisBackup\\JunitWithoutReflection.git.second\\JUnitWithoutReflection.Second\\bin\\observer";
			String loc = "D:\\TestClass\\callgraphstat";
			// String mainClass = "callgraphstat.superclass.Example";
			// String mainClass = "observer.Main";
			String mainClass = "callgraphstat.JCallGraph";
			File file = new File(loc);
			JCallGraph jCallGraph = new JCallGraph(file, "callgraphstat");
			// JCallGraph jCallGraph = new JCallGraph(file, "observer");
			Description des = jCallGraph.getClassDescriptions().get(mainClass);
			// make single call means check edges first
			// Get the Java runtime
			Runtime runtime = Runtime.getRuntime();
			// Run the garbage collector
			runtime.gc();
			long start = System.nanoTime();
			if (des != null) {
				// des.getClassVisitor().start();
				GenerateCallGraph generateCallGraph = new GenerateCallGraph(
						des.copy());
				generateCallGraph = null;
			}
			long end = System.nanoTime();
			// Calculate the used memory
			long memory = runtime.totalMemory() - runtime.freeMemory();

			// // // print nodes
			// System.out.println("NODES\n_____________________");
			// java.util.List<String> nodes = Static.getSortedNodes();
			// for (int i = 0; i < nodes.size(); i++) {
			// System.out.println((i + 1) + ".\t" + nodes.get(i) + "\n");
			// }
			// print edges
			System.out.println("EDGES\n_____________________");
			java.util.List<String> edges = Static.getUnSortedEdges();
			System.out.println("All Edges...\n");
			for (int i = 0; i < edges.size(); i++) {
				System.out.println((i + 1) + ".\t" + edges.get(i) + "\n");
			}

			// System.out.println("\n\nLIBRARY EDGES\n_____________________");
			// java.util.List<String> libedges =
			// Static.getUnSortedLibraryEdges();
			// System.out
			// .println("Edges...(That has no further access to the class or assume library classes)\n");
			// for (int i = 0; i < libedges.size(); i++) {
			// System.out.println((i + 1) + ".\t" + libedges.get(i) + "\n");
			// }
			//
			// System.out
			// .println("\n\nLIBRARY CLASS OR METHOD NO ACCESS\n_____________________");
			// java.util.List<String> noAccessLibedges = Static
			// .getUnSortdLibraryClassOrMethodNoAccess();
			// System.out
			// .println("List of Classes or Methods that has no access, assume library classes\n");
			// for (int i = 0; i < noAccessLibedges.size(); i++) {
			// System.out.println((i + 1) + ".\t" + noAccessLibedges.get(i)
			// + "\n");
			// }
			//
			// System.out.println("\n\nNO ACCESSED CLASS\n_____________________");
			// java.util.List<String> noAccessClass = Static
			// .getSortdNoAccessLibraryClass();
			// System.out
			// .println("List of Classes that has no access, assume library classes\n");
			// for (int i = 0; i < noAccessClass.size(); i++) {
			// System.out.println((i + 1) + ".\t" + noAccessClass.get(i)
			// + "\n");
			// }
			System.out.println("\n\n\n\t\t\t\t\tElapsed Time: "
					+ (double) (end - start) / 1000000000.0 + "s");
			System.out.println("\t\t\t\t\tUsed memory is bytes: " + memory);
			System.out.println("\t\t\t\t\tUsed memory is megabytes: "
					+ bytesToMegabytes(memory));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final long MEGABYTE = 1024L * 1024L;

	public static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}

	private Map<String, Description> classDescriptions = new LinkedHashMap<String, Description>();
	private File file = null;

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
				// if (fileEntry.getName().equalsIgnoreCase("superclass")) {
				// if (fileEntry.getName().equalsIgnoreCase("superclass")) {
				readFiles(fileEntry, pac.concat(fileEntry.getName()));
				// }
			} else {
				// if (pac.equalsIgnoreCase("callgraphstat.superclass.")) {
				// if (pac.equalsIgnoreCase("observer.")) {
				if (pac.equalsIgnoreCase("callgraphstat.")) {
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
