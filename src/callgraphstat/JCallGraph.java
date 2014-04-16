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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class JCallGraph {
	private static BufferedWriter writer = null;
	private static String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss")
			.format(Calendar.getInstance().getTime()) + ".txt";
	private static final long MEGABYTE = 1024L * 1024L;
	private Map<String, Description> classDescriptions = new LinkedHashMap<String, Description>();
	private static File file = null;
	private static File logFile = null;
	private static String[] output = new String[5];

	public static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}

	private static void getAndSetInfo() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			while (Options.appName.trim().equalsIgnoreCase("")) {
				System.out
						.println("Please provide a name for this test. This name will be used as prefix of generated report.\n\n");
				Options.appName = br.readLine();
			}

			while (Options.path.trim().equalsIgnoreCase("")) {
				System.out
						.println("Please provide package folder path, not the package name.\n"
								+ "ex: C:\\Temp\\src\n\n");
				Options.path = br.readLine();
			}

			System.out
					.println("Please provide packages name that should include, only parent package\n"
							+ "ex: lnu of lnu.some, org of org.some\n"
							+ "To exit please write 0\n\n");
			String packColl = "";
			while (packColl.equalsIgnoreCase("")) {
				packColl = br.readLine();
				if (packColl.equalsIgnoreCase("0")) {
					break;
				} else {
					if (!packColl.equalsIgnoreCase("")) {
						Options.packages.add(packColl.trim());
					}
				}
				packColl = "";
			}

			while (Options.mainClass.trim().equalsIgnoreCase("")) {
				System.out
						.println("Please provide main class name with package and without .class\n"
								+ "ex: package.some.Main\n\n");
				Options.mainClass = br.readLine();
			}

			System.out.println("Please provide main method name.\n"
					+ "ex: main\n\n");
			String main = br.readLine().trim();
			Options.mainMethod = (main.equalsIgnoreCase("")) ? "main" : main;

			System.out
					.print("Do you want to do deep search, deep search will create and traverse each object whether it can find same edges or not\n"
							+ "otherwise it will not traverse if duplicate edges found.\n"
							+ "Warning: Deep search can through Stackoverflow exception for big application. Default: deep search false.\n"
							+ "Write anything to do deep search, else just enter for next.\n\n");
			if (!br.readLine().equalsIgnoreCase("")) {
				Options.deepSearch = true;
			}

			System.out
					.println("Do you want unsorted report? Type anything below it will generate unsorted report. (Default: Sorted)\n\n");
			if (!br.readLine().equalsIgnoreCase("")) {
				Options.sortedReport = false;
			}
		} catch (Exception e) {
			System.exit(-1);
		}
	}

	public static void main(String[] args) {
		Thread myThread = null;
		try {
			getAndSetInfo();
			System.out.println("\n\n\nCreating Static Callgraph... "
					+ ((Options.deepSearch) ? "by Searching deeply."
							: "without deep search") + " \n\n\n");
			// make single call means check edges first
			// Get the Java runtime
			Runtime runtime = Runtime.getRuntime();
			// Run the garbage collector
			runtime.gc();
			long start = System.nanoTime();
			Runnable runnable = new Progress();
			myThread = new Thread(runnable);
			myThread.start();
			JCallGraph jCallGraph = new JCallGraph();
			Description des = jCallGraph.getClassDescriptions().get(
					Options.mainClass);
			if (des != null) {
				new GenerateCallGraph(des.copy(), Options.mainMethod);
			}
			myThread.interrupt();
			myThread = null;
			long end = System.nanoTime();
			// Calculate the used memory
			long memory = runtime.totalMemory() - runtime.freeMemory();
			// ---------------------------------------------------------
			printAndWrite();
			// ---------------------------------------------------------
			System.out.println("\n\n\n");
			for (int i = 0; i < output.length; i++) {
				System.out.println(output[i]);
			}
			System.out.println("\n\n\n");
			System.out.println("\n\n\n\t\t\t\t\tFinish... "
					+ ((Options.deepSearch) ? "by Searching deeply."
							: "without deep search") + " \n\n\n");
			System.out.println("\t\t\t\t\tElapsed Time: "
					+ (double) (end - start) / 1000000000.0 + "s");
			System.out.println("\t\t\t\t\tUsed memory is bytes: " + memory);
			System.out.println("\t\t\t\t\tUsed memory is megabytes: "
					+ bytesToMegabytes(memory));
			// ---------------------------------------------------------
		} catch (Exception e) {
			System.err.println("--------------PROVIDED INFO-------------");
			System.err.println("Path: " + Options.path);
			System.err.println("Packages: " + Options.packages);
			System.err.println("Main Class: " + Options.mainClass);
			System.err.println("Main Method: " + Options.mainMethod);
			writerClose();
			myThread.interrupt();
			myThread = null;
			e.printStackTrace();
		}
	}

	private static void printAndWrite() throws IOException {
		System.out.println("\n\n\nReport Type: "
				+ ((Options.sortedReport) ? "Sorted" : "Unsorted")
				+ "\n_________________________\n\n");
		// ------------------------------------------------------------------------------
		// print nodes
		// ------------------------------------------------------------------------------
		writerOpen("nodes");
		System.out.println(write("NODES\n_____________________"));
		List<String> nodes = (Options.sortedReport) ? Static.getSortedNodes()
				: Static.getUnSortedNodes();
		write("Total Nodes: " + nodes.size() + "\n");
		for (int i = 0; i < nodes.size(); i++) {
			System.out.println((i + 1) + ".\t" + write(nodes.get(i)) + "\n");
		}
		output[0] = "\nNODES File saved in: \n" + logFile.getCanonicalPath();
		writerClose();
		// ------------------------------------------------------------------------------
		// print edges
		// ------------------------------------------------------------------------------
		writerOpen("edges");
		System.out.println(write("EDGES\n_____________________"));
		List<String> edges = (Options.sortedReport) ? Static.getSortedEdges()
				: Static.getUnSortedEdges();
		System.out.println(write("All Edges...\n"));
		write("Total Edges: " + edges.size() + "\n");
		for (int i = 0; i < edges.size(); i++) {
			System.out.println((i + 1) + ".\t" + write(edges.get(i)) + "\n");
		}
		output[1] = "\nEDGES File saved in: \n" + logFile.getCanonicalPath();
		writerClose();
		// ------------------------------------------------------------------------------
		// LIBRARY EDGES
		// ------------------------------------------------------------------------------
		writerOpen("library_edges");
		System.out.println(write("LIBRARY EDGES\n_____________________"));
		List<String> libedges = (Options.sortedReport) ? Static
				.getSortedLibraryEdges() : Static.getUnSortedLibraryEdges();
		System.out
				.println(write("Edges...(That has no further access to the class or assume library classes)\n"));
		write("Total Library Edges: " + libedges.size() + "\n");
		for (int i = 0; i < libedges.size(); i++) {
			System.out.println((i + 1) + ".\t" + write(libedges.get(i)) + "\n");
		}
		output[2] = "\nLIB_EDGES File saved in: \n"
				+ logFile.getCanonicalPath();
		writerClose();
		// ------------------------------------------------------------------------------
		// LIBRARY CLASS NODES NO ACCESS
		// ------------------------------------------------------------------------------
		writerOpen("no_access_classes_nodes");
		System.out
				.println(write("NO ACCESS CLASSES NODES\n_____________________"));
		List<String> noAccessLibNodes = (Options.sortedReport) ? Static
				.getSortedLibraryNodesNoAccess() : Static
				.getUnSortedLibraryNodesNoAccess();
		System.out
				.println(write("Nodes that has no access to read, assume library classes\n"));
		write("Total No Access Library Nodes: " + noAccessLibNodes.size()
				+ "\n");
		for (int i = 0; i < noAccessLibNodes.size(); i++) {
			System.out.println((i + 1) + ".\t" + write(noAccessLibNodes.get(i))
					+ "\n");
		}
		output[3] = "\nNO_ACCESS_CLASS_METHOD File saved in: \n"
				+ logFile.getCanonicalPath();
		writerClose();
		// ------------------------------------------------------------------------------
		// NO ACCESSED CLASSES
		// ------------------------------------------------------------------------------
		writerOpen("no_access_classes");
		System.out.println(write("NO ACCESS CLASSES\n_____________________"));
		List<String> noAccessClass = (Options.sortedReport) ? Static
				.getSortdNoAccessLibraryClass() : Static
				.getUnSortdNoAccessLibraryClass();
		System.out
				.println(write("List of Classes that has no access, assume library classes\n"));
		write("Total No Access Classes: " + noAccessClass.size() + "\n");
		for (int i = 0; i < noAccessClass.size(); i++) {
			System.out.println((i + 1) + ".\t" + write(noAccessClass.get(i))
					+ "\n");
		}
		output[4] = "\nNO_ACCESS_CLASS File saved in: \n"
				+ logFile.getCanonicalPath();
		writerClose();
		// ------------------------------------------------------------------------------
	}

	private static String write(String str) {
		try {
			writer.write(str);
			writer.write("\n");
		} catch (IOException e) {
			writerClose();
		}
		return str;
	}

	private static void writerClose() {
		try {
			writer.close();
			writer = null;
		} catch (Exception e) {
		}
	}

	private static void writerOpen(String name) {
		try {
			String fileName = Options.appName + "_" + name + "_"
					+ ((Options.sortedReport) ? "sorted" : "unsorted") + "_"
					+ timeLog;
			logFile = new File(fileName);
			writer = new BufferedWriter(new FileWriter(logFile));
			write("Report Type: "
					+ ((Options.sortedReport) ? "Sorted" : "Unsorted")
					+ "\n_________________________\n\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JCallGraph() throws Exception {
		for (int i = 0; i < Options.packages.size(); i++) {
			file = new File(Options.path.trim().concat(File.separator)
					.concat(Options.packages.get(i).trim()));
			readFiles(file, Options.packages.get(i));
		}
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
				readFiles(fileEntry, pac.concat(fileEntry.getName()));
			} else {
				if (fileEntry.getName().endsWith(".class")) {
					URL url = file.toURI().toURL();
					URL[] urls = new URL[] { url };
					String cls = pac.concat(fileEntry.getName().replaceAll(
							".class", ""));
					@SuppressWarnings("resource")
					Class<?> clas = new URLClassLoader(urls).loadClass(cls);
					this.classDescriptions.put(clas.getName(), new Description(
							clas, this.classDescriptions));
				}
			}
		}
	}
}

class Progress implements Runnable {
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				System.out.print(".");
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (Exception e) {
			}
		}
	}
}