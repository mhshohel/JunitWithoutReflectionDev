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
	private File file = null;
	private static File logFile = null;

	private static void getAndSetInfo() {
		String[] args = new String[10];
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			while (Options.path.trim().equals("")) {
				System.out
						.println("Please provide package folder path, not the package name.\n"
								+ "ex: C:\\Temp\\src\n");
				Options.path = br.readLine();
			}

			while (Options.parentPackage.trim().equals("")) {
				System.out.println("Please provide parent package name\n"
						+ "ex: package of package.some");
				Options.parentPackage = br.readLine();
			}

			while (Options.mainClass.trim().equals("")) {
				System.out
						.println("Please provide main class name with package and without .class\n"
								+ "ex: package.some.Main");
				Options.mainClass = br.readLine();
			}

			System.out.println("Please provide main method name.\n "
					+ "ex: main");
			String main = br.readLine().trim();
			Options.mainMethod = (main.equalsIgnoreCase("")) ? "main" : main;

			System.out
					.print("Do you want to do deep search, deep search will create and traverse each object whether it can find same edges or not\n"
							+ "otherwise it will not traverse if duplicate edges found.\n"
							+ "Warning: Deep search can through Stackoverflow exception for big applocation. Default: deep search false.\n"
							+ "Write anything to do deep search, else just enter for next");
			if (!br.readLine().equalsIgnoreCase("")) {
				Options.deepSearch = true;
			}

			System.out
					.println("Do you want unsorted report? Type anything below it will generate unsorted report. (Default: Sorted)");
			if (!br.readLine().equalsIgnoreCase("")) {
				Options.sortedReport = false;
			}
		} catch (Exception e) {
			System.exit(-1);
		}
	}

	public static void main(String[] args) {
		String location = null;
		try {
			getAndSetInfo();
			System.out.println("\n\n\nCreating Static Callgraph... "
					+ ((Options.deepSearch) ? "by Searching deeply."
							: "without deep search") + " \n\n\n");
			location = Options.path.trim().concat(File.separator)
					.concat(Options.parentPackage.trim());
			File file = new File(location);
			JCallGraph jCallGraph = new JCallGraph(file, Options.parentPackage);
			Description des = jCallGraph.getClassDescriptions().get(
					Options.mainClass);
			// make single call means check edges first
			// Get the Java runtime
			Runtime runtime = Runtime.getRuntime();
			// Run the garbage collector
			runtime.gc();
			long start = System.nanoTime();
			if (des != null) {
				new GenerateCallGraph(des.copy(), Options.mainMethod);
			}
			long end = System.nanoTime();
			// Calculate the used memory
			long memory = runtime.totalMemory() - runtime.freeMemory();

			writerOpen();
			printAndWrite();
			writerClose();
			// ---------------------------------------------------------
			System.out.println("\n\n\n\t\t\t\t\tFinish... "
					+ ((Options.deepSearch) ? "by Searching deeply."
							: "without deep search") + " \n\n\n");
			System.out.println("\t\t\t\t\tElapsed Time: "
					+ (double) (end - start) / 1000000000.0 + "s");
			System.out.println("\t\t\t\t\tUsed memory is bytes: " + memory);
			System.out.println("\t\t\t\t\tUsed memory is megabytes: "
					+ bytesToMegabytes(memory));

		} catch (Exception e) {
			System.err.println("--------------PROVIDED INFO-------------");
			System.err.println("Path: " + Options.path);
			System.err.println("Parent Package: " + Options.parentPackage);
			System.err.println("Location: " + location);
			System.err.println("Main Class: " + Options.mainClass);
			System.err.println("Main Method: " + Options.mainMethod);
			writerClose();
			e.printStackTrace();
		}
	}

	private static void printAndWrite() {
		System.out.println(write("Report Type: "
				+ ((Options.sortedReport) ? "Sorted" : "Unsorted")
				+ "\n_________________________\n\n"));
		// // print nodes
		System.out.println(write("NODES\n_____________________"));
		List<String> nodes = (Options.sortedReport) ? Static.getSortedNodes()
				: Static.getUnSortedNodes();
		for (int i = 0; i < nodes.size(); i++) {
			System.out.println((i + 1) + ".\t" + write(nodes.get(i)) + "\n");
		}
		// print edges
		System.out.println(write("\n\nEDGES\n_____________________"));
		List<String> edges = (Options.sortedReport) ? Static.getSortedEdges()
				: Static.getUnSortedEdges();
		System.out.println("All Edges...\n");
		for (int i = 0; i < edges.size(); i++) {
			System.out.println((i + 1) + ".\t" + write(edges.get(i)) + "\n");
		}

		//
		System.out.println(write("\n\nLIBRARY EDGES\n_____________________"));
		List<String> libedges = (Options.sortedReport) ? Static
				.getSortedLibraryEdges() : Static.getUnSortedLibraryEdges();
		System.out

				.println(write("Edges...(That has no further access to the class or assume library classes)\n"));
		for (int i = 0; i < libedges.size(); i++) {
			System.out.println((i + 1) + ".\t" + write(libedges.get(i)) + "\n");
		}

		System.out

				.println(write("\n\nLIBRARY CLASS OR METHOD NO ACCESS\n_____________________"));
		List<String> noAccessLibedges = (Options.sortedReport) ? Static
				.getSortedLibraryClassOrMethodNoAccess() : Static
				.getUnSortedLibraryClassOrMethodNoAccess();
		System.out

				.println(write("List of Classes or Methods that has no access, assume library classes\n"));
		for (int i = 0; i < noAccessLibedges.size(); i++) {
			System.out.println((i + 1) + ".\t" + write(noAccessLibedges.get(i))
					+ "\n");
		}

		System.out
				.println(write("\n\nNO ACCESSED CLASSES\n_____________________"));
		List<String> noAccessClass = (Options.sortedReport) ? Static
				.getSortdNoAccessLibraryClass() : Static
				.getUnSortdNoAccessLibraryClass();
		System.out

				.println(write("List of Classes that has no access, assume library classes\n"));
		for (int i = 0; i < noAccessClass.size(); i++) {
			System.out.println((i + 1) + ".\t" + write(noAccessClass.get(i))
					+ "\n");
		}

		try {
			System.out.println("\n\n\n\tFile saved in: "
					+ logFile.getCanonicalPath());
		} catch (IOException e) {
		}
	}

	private static void writerOpen() {
		try {
			String fileName = Options.parentPackage + "_"
					+ ((Options.sortedReport) ? "sorted" : "unsorted") + "_"
					+ timeLog;
			logFile = new File(fileName);
			writer = new BufferedWriter(new FileWriter(logFile));

			// System.out.println(logFile.getCanonicalPath());
			// writer.write("Hello world!");
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}

	public JCallGraph(File file, String pack) throws Exception {
		this.file = file;
		readFiles(file, pack);
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
				if (pac.equalsIgnoreCase((Options.parentPackage + "."))) {
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
