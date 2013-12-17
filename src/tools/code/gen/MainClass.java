/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName MainClass.java
 * 
 * @FileCreated Jan 01, 2013
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
package tools.code.gen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import junit.framework.TestCase;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import tools.staticcallgraph.Description;
import tools.staticcallgraph.Description.ClassCategory;
import tools.staticcallgraph.JCallGraph;
import tools.staticcallgraph.OPCodeDescription;

public class MainClass {
    /*
     * Keep All classes that can be retrieve from class file, it's not
     * containing any inner class
     */
    private static Set<Class<?>> allClasses = new HashSet<Class<?>>();
    /*
     * Keep all the description of a Class file or Java file to create a report
     * of the process
     */
    private static List<Description> classDescriptions = new ArrayList<Description>();
    /*
     * Keep Objects those are needs to check for CallGraph
     */
    private static List<List<Description>> entryClassFiles = new ArrayList<List<Description>>();
    /*
     * Store file info
     */
    private static File file = null;
    /*
     * Character that separates components of a file path. This is "/" on UNIX
     * and "\" on Windows.
     */
    private static String fileSeparator = System.getProperty("file.separator");
    /*
     * Keep list of all generated .java file and Object List as test class, Main
     * file name of GenCode and package name
     */
    private static HashMap<List<String>, List<Object>> outputClassesDirectory = new HashMap<List<String>, List<Object>>();
    /*
     * Keep the list of all test classes
     */
    private static Set<Class<?>> testClasses = new HashSet<Class<?>>();
    /*
     * test classes list of Description to keep objects those are recently used,
     * so that searching can be faster than normal
     */
    private static List<Description> testDescriptionList = new ArrayList<Description>();

    public static void main(String[] args) {
	// This version can only take one directory location
	if (args.length == 0) {
	    try {
		String location = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(
			System.in));
		while (location.trim().equals("")) {
		    System.out
			    .println("Please provide the folder name with path,\nnot the package name. "
				    + "ex: C:\\Temp\\src\n");
		    location = br.readLine();
		}
		System.out
			.print("You can also provide a specific Test class name as second parameter."
				+ "\nWarrning: To get risk free result you should leave this field blank. "
				+ "\nex: a.b.Test (package name but without .class extension)\n");
		String specificTestClass = br.readLine();
		if (specificTestClass.trim().equals("")) {
		    args = new String[1];
		    args[0] = location.trim();
		} else {
		    args = new String[2];
		    args[0] = location.trim();
		    args[1] = specificTestClass.trim();
		}
		System.out.println(location + "   " + specificTestClass);
	    } catch (Exception e) {
		System.exit(-1);
	    }
	}
	if (args.length == 0) {
	    System.err
		    .println("You must provide the folder name with path, not the package name. "
			    + "ex: C:\\Temp\\src");
	    System.err
		    .println("You can also provide a specific Test class name as second parameter. "
			    + "\nex: a.b.Test (package name but without .class extension)\n");
	    System.exit(-1);
	}
	// remove first and last "" sign if any
	if (args[0].charAt(0) == '"') {
	    args[0] = args[0].substring(1, args[0].length());
	}
	if (args[0].charAt(args[0].length() - 1) == '"') {
	    args[0] = args[0].substring(0, args[0].length() - 1);
	}
	try {
	    System.out
		    .println("----------------------------------------------");
	    System.out
		    .println("****** Part One: Loading and Generating ******");
	    System.out
		    .println("----------------------------------------------");
	    /* Generating code */
	    codeGen(args, true);
	    System.out
		    .println("----------------------------------------------------------------------");
	    System.out
		    .println("****** Part Two: Compiling Generated Java File and Load Classes ******");
	    System.out
		    .println("----------------------------------------------------------------------");
	    for (Entry<List<String>, List<Object>> entry : outputClassesDirectory
		    .entrySet()) {
		System.out.println("Compiling Gen Code File for:  "
			+ ((Class<?>) entry.getValue().get(0)).getName());
		System.out.println("Main Class Name:  "
			+ entry.getValue().get(1));
		/* Compiling and loading */
		compileGeneratedCodeAndLoad(args[0], entry.getKey(), entry
			.getValue().get(1).toString(), entry.getValue().get(2)
			.toString());
		System.out
			.println("----------------------------------------------------------------------\n");
	    }
	    System.out
		    .println("--------------------------------------------------");
	    System.out
		    .println("****** Part Three: Simple Static Call Graph ******");
	    System.out
		    .println("--------------------------------------------------");
	    // read op code of classes
	    System.out.println("Reading OPCODE of Test Classes");
	    JCallGraph jcg = new JCallGraph(classDescriptions,
		    testDescriptionList);
	    readOpCodeOfClasses(jcg);
	    System.out.println("Read Succesful!");
	    System.out
		    .println("--------------------------------------------------");
	    System.out
		    .println("Collecting Call Graph Data from Generated Code Classes");
	    // each list contains list of generated code for specific TestClass
	    for (List<Description> classObjects : entryClassFiles) {
		lookupToGetStaticCallGraph(classObjects);
	    }
	    // generate reports in .csv format
	    System.out.println("Static Data Collection Complete!");
	    System.out
		    .println("--------------------------------------------------");
	    System.out.println("Writing Report in .CSV file");
	    System.out.print("Please Wait.");
	    long start = System.currentTimeMillis();
	    Runnable runnable = new Progress();
	    Thread myThread = new Thread(runnable);
	    myThread.start();
	    print("Static_Report_", args[0], myThread);
	    long end = System.currentTimeMillis();
	    double res = (end - start) / 1000;
	    System.out.println("\tElapsed Time: " + res + "s");
	    System.out
		    .println("--------------------------------------------------");
	    System.out.println("****** All Task Completed. Thank You. ******");
	    System.out
		    .println("--------------------------------------------------");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * <li><strong><i>codeGen</i></strong></li>
     * 
     * <pre>
     * public static void codeGen(String[] args, boolean isMethodSorted)
     * </pre>
     * 
     * <p>
     * To make the code generation available while using it as a library file. A
     * String array should pass which can take any number of parameters but it
     * will count only first two. args[0] as folder name with path (not the
     * package name) ex: c:\temp and args[1] is optional as a specific name
     * filter. ex: a.t.Test, means codeGen will look for Test class of to
     * generate code, other test classes will be escaped.
     * </p>
     * 
     * @param args
     *            - String[], args[0] as location, args[1] as specific Test
     *            class.
     * @param isMethodSorted
     *            - boolean if test class should provide sorted method then
     *            true.
     * 
     * @author Shohel Shamim
     */
    public static void codeGen(String[] args, boolean isMethodSorted) {
	List<String> generatedCodeFileList = null;
	try {
	    String fileLocation = args[0].trim();
	    // Get specific class name
	    String nameFilter = (args.length > 1) ? args[1].trim() : "";
	    file = new File(fileLocation);

	    System.out.println("Loading Classes...");

	    readFiles(file, nameFilter, "");
	    System.out
		    .println("------------------------------------------------\n");

	    System.out.println("---------------------------------------------");
	    System.out.println("****** Verify Test Classes - JUnit 4.0 ******");
	    System.out.println("---------------------------------------------");

	    Set<Class<?>> classes = (testClasses.isEmpty()) ? allClasses
		    : testClasses;
	    for (Class<?> cls : classes) {
		System.out.println("\nClass " + cls.getName());
		System.out
			.println("-------------------------------------------");
		// Verify JUnit Test class first, if there JUnit fails to
		// run Test case then it will not generate the output, only
		// JUnit 4.0-4.9 can be accepted
		Result result = null;
		Class<?> testCase = TestCase.class;
		try {
		    Class<?> superclass = cls.getSuperclass();
		    if (superclass != null && superclass.equals(testCase)) {
			System.err
				.println("Warning: "
					+ cls.getName()
					+ " is not JUnit 4.0 or later version. \n"
					+ "It can generate wrong output code. Please change the code.\n"
					+ "Removee Superclass TestCase and add annotations.\n"
					+ "Code generation faild...");
			System.exit(-1);
		    } else {
			result = JUnitCore.runClasses(cls);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}

		if (result != null && !result.getFailures().isEmpty()) {
		    classDescriptions.add(new Description(cls,
			    ClassCategory.REGULAR, classDescriptions));
		    System.out.println("\tis not a Test Class");
		} else {
		    testClasses.add(cls);
		    Description description = new Description(cls,
			    ClassCategory.TEST, classDescriptions);
		    classDescriptions.add(description);
		    testDescriptionList.add(description);
		    System.out
			    .println("-------------------------------------------");
		    System.out.println(cls.getName() + " is a Test Class");
		}

	    }

	    System.out.println("\n-------------------------------------------");

	    // Class<?> clss = null;
	    // BasicTest.class;
	    // BasicTest.class;
	    // SuppliedByTest.class
	    // ParameterSuppliedByClassTest.class;
	    // ParametarizedTest.class
	    // BigOutPut.class;
	    // DataPointsTestClass.class;
	    // TheoryTest.class;
	    // SuiteClassTest.class;
	    // JunitTest1.class; 1-10
	    // BasicTest.class;
	    // ParametarizedTest.class;
	    // CategoryTest.class;
	    // AccountTest.class;
	    // AccountIntegrationTest.class;
	    // MyStuffJunitTest.class;
	    // TestSquareMatrix.class;
	    // AfterAfterClassBeforeBeforeClassIgnore.class;

	    // TestAlgorithms
	    // TestDFS
	    // TestDirectedGraph

	    if (testClasses.isEmpty()) {
		System.out.println("No Test classes found to Generate code.");
	    } else {
		// If looking for only one test class then other class record
		// needs to describe, if specified a JUnit test class then it
		// will mark other test class as normal class
		for (Class<?> cls : allClasses) {
		    if (!testClasses.contains(cls)) {
			classDescriptions.add(new Description(cls,
				ClassCategory.REGULAR, classDescriptions));
		    }
		}
	    }
	    for (Class<?> clss : testClasses) {
		/*
		 * Changed to class original name instead of hard coded
		 * "OutputClass"
		 */
		String outputClassName = clss.getSimpleName().concat("Output");
		String packageName = clss.getPackage().getName();

		// Physical Path; Decode path to avoid unwanted chars,
		// removed first char,
		String directory = URLDecoder.decode(clss.getResource("")
			.getFile().substring(1), "UTF-8");
		directory = fileLocation
			.concat(fileSeparator)
			.concat(packageName.replace('.', (fileSeparator
				.equalsIgnoreCase("/") ? '/' : '\\')))
			.concat(fileSeparator);

		/*
		 * If isMethodSorted is true then method will be sorted
		 * otherwise it can be unsorted. In future Junit 4.11 there is a
		 * updated to sort method. However, 4.1 - 4.10 it is not
		 * available. If you run Junit then you may see method is
		 * sometimes read according to alphabetical order but most of
		 * the time unsorted. That's why for long list of methods it is
		 * often creates confusion because order of method not match,
		 * until you run 2-3 time you will get sorted.
		 */
		boolean methodShouldSort = isMethodSorted; // or false

		System.out
			.println("\n------------------------------------------------------------");
		System.out
			.println("****** Generating Code for "
				+ clss.getName()
				+ " ******\n------------------------------------------------------------");
		System.out.print("Please Wait");
		long start = System.currentTimeMillis();
		Runnable runnable = new Progress();
		Thread myThread = new Thread(runnable);
		myThread.start();
		// Output generation begins;
		GenerateOutput outputClass = new GenerateOutput(clss,
			packageName, outputClassName, methodShouldSort,
			directory);

		// clss = BigOutPut.class;
		// GenerateOutput outputClass = new GenerateOutput(clss,
		// clss
		// .getPackage().getName(), outputClassName,
		// methodShouldSort, directory);
		generatedCodeFileList = outputClass.execute();
		if (outputClass.isWritingComplete()) {
		    System.out
			    .println("\n\tCode Generation Completed. Main Class File is: "
				    + outputClassName + ".java");
		    List<Object> objects = new ArrayList<Object>();
		    objects.add(clss);
		    objects.add(outputClassName);
		    objects.add(clss.getPackage().getName());
		    // Key is list of .java files and value is class
		    outputClassesDirectory.put(generatedCodeFileList, objects);
		    // Description genCodeDesc = new Description(clss,
		    // false);
		    // classDescriptions.add(genCodeDesc);
		}
		outputClass = null;
		myThread.interrupt();
		myThread = null;
		long end = System.currentTimeMillis();
		double res = (end - start) / 1000;
		System.out.println("\tElapsed Time: " + res + "s");
	    }
	} catch (Exception e) {
	    System.err.println(e.getMessage());
	} finally {
	    System.out
		    .println("\n--------------------------------------------------------");
	    System.out
		    .println("****** List of Generated code with physical path ******");
	    System.out
		    .println("--------------------------------------------------------");

	    for (Entry<List<String>, List<Object>> entry : outputClassesDirectory
		    .entrySet()) {
		System.out.println("Test Class: "
			+ ((Class<?>) entry.getValue().get(0)).getName());
		for (String keys : entry.getKey()) {
		    System.out.println("\t" + keys);
		}
		System.out.println();
	    }
	    System.out
		    .println("--------------------------------------------------------");
	}
    }

    /* Compile Gen Code */
    private static void compileGeneratedCodeAndLoad(String directory,
	    List<String> generatedJavaFile, String mainClassFileName,
	    String packageName) {
	String classpath = System.getProperty("java.class.path");
	String testpath = directory.concat(";") + classpath;
	List<String> optionList = new ArrayList<String>();
	optionList.addAll(Arrays.asList("-classpath", testpath));
	try {
	    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	    /*
	     * To run JavaCompiler System should run in JDK not in JRE. For that
	     * java.home property should indicate to JDK main directory
	     */
	    if (compiler == null) {
		/* try to run with guessed location */
		String jdkVersion = System.getProperty("java.version");
		String jreLocation = System.getProperty("java.home");
		String jdkLocation = jreLocation.substring(0,
			jreLocation.lastIndexOf(fileSeparator));
		String lastPart = jdkLocation.substring(jdkLocation
			.lastIndexOf(fileSeparator) + 1);
		if (lastPart.equalsIgnoreCase("Java")) {
		    jdkLocation = jdkLocation + fileSeparator + "jdk"
			    + jdkVersion;
		}
		System.setProperty("java.home", jdkLocation);
		compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
		    /* try to run with provided jdk directory */
		    System.out.println("Please run system in JDK not in JRE.");
		    System.out
			    .print("Ener JDK directory location. (ex: D:/Program Files/Java/jdk1.7.0_40) :\n");
		    BufferedReader br = new BufferedReader(
			    new InputStreamReader(System.in));
		    String path = null;
		    path = br.readLine();
		    System.setProperty("java.home", path);
		    compiler = ToolProvider.getSystemJavaCompiler();
		    if (compiler == null) {
			/* no more try */
			System.err
				.println("Sorry, could not set JAVA_HOME to JDK. Try to run again in JDK.");
			System.exit(-1);
		    }
		}
	    }
	    StandardJavaFileManager sjfm = compiler.getStandardFileManager(
		    null, null, null);
	    File[] javaFiles = new File[generatedJavaFile.size()];
	    for (int i = 0; i < generatedJavaFile.size(); i++) {
		javaFiles[i] = new File(generatedJavaFile.get(i)
			.concat(".java"));
	    }

	    Iterable<? extends JavaFileObject> fileObjects = sjfm
		    .getJavaFileObjects(javaFiles);
	    JavaCompiler.CompilationTask task = compiler.getTask(null, null,
		    null, optionList, null, fileObjects);
	    task.call();
	    sjfm.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
	System.out.println("!!!Compilation Successful!!!");

	Description description = null;
	List<Description> classObjects = new ArrayList<Description>();
	try {
	    for (int i = 0; i < generatedJavaFile.size(); i++) {
		URLClassLoader classLoader = null;
		try {
		    classLoader = URLClassLoader
			    .newInstance(new URL[] { new File(directory)
				    .toURI().toURL() });
		} catch (MalformedURLException e) {
		    e.printStackTrace();
		}
		String name = generatedJavaFile.get(i)
			.substring(
				generatedJavaFile.get(i).lastIndexOf(
					fileSeparator) + 1);
		boolean isMainClass = (name.equalsIgnoreCase(mainClassFileName)) ? true
			: false;
		name = (packageName + "." + name).trim();
		Class<?> clss = null;
		try {
		    clss = Class.forName(name, true, classLoader);
		    allClasses.add(clss);
		    description = new Description(clss, ClassCategory.REGULAR,
			    classDescriptions);
		    classDescriptions.add(description);
		    // make sure that main class object is at the top position
		    if (isMainClass) {
			if (classObjects.isEmpty()) {
			    classObjects.add(description);
			} else {
			    classObjects.add(classObjects.get(0));
			    classObjects.add(0, description);
			}
		    } else {
			classObjects.add(description);
		    }
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		}
	    }
	    entryClassFiles.add(classObjects);
	    System.out.println("!!!Class Successfully Loaded!!!");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /*
     * Sample code for 3rd part of the Thesis
     */
    public static void lookupToGetStaticCallGraph(List<Description> classObjects) {
	if (!classObjects.isEmpty()) {
	    for (Description entryDescription : classObjects) {
		new JCallGraph(classDescriptions, testDescriptionList)
			.lookInsideClass(entryDescription.getJavaClass(),
				entryDescription, true);
		System.out.println("\t"
			+ entryDescription.getActualClass().getName()
			+ ": DONE!");
	    }
	}
    }

    public static void print(String fileNamePref, String location,
	    Thread myThread) {
	try {
	    String fileName = fileNamePref + (System.currentTimeMillis())
		    + ".csv";
	    String lastChar = location.trim().substring(location.length() - 1);
	    if (!lastChar.equalsIgnoreCase(fileSeparator)) {
		location += fileSeparator;
	    }
	    String file = location + fileName;
	    PrintWriter output = new PrintWriter(file);
	    for (Description description : classDescriptions) {
		output.print(description);
	    }
	    output.print("\n");
	    output.close();
	    System.out.println("\n\tReport Writing Successfully Completed"
		    + "\n\t-------------------------" + "\n\tFile Name: "
		    + fileName + "\n\tLocation: " + location + "\n\tFile: "
		    + file + "\n\tFile Size: "
		    + (new File(file).length() / 1024) + "KB.");
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} finally {
	    myThread.interrupt();
	    myThread = null;
	}
    }

    /**
     * <li><strong><i>readFiles</i></strong></li>
     * 
     * <pre>
     * private static void readFiles(final File folder, final String pack)
     * </pre>
     * 
     * <p>
     * Save the list of .class files.
     * </p>
     * 
     * @param folder
     *            - entry point of the folder.
     * @param nameFilter
     *            - provide a specific class name.
     * @param pack
     *            - contains package names.
     * 
     * @author Shohel Shamim
     */
    private static void readFiles(final File folder, final String nameFilter,
	    final String pack) {
	try {
	    String pac = (pack.trim().equalsIgnoreCase("") ? "" : pack
		    .concat("."));
	    for (final File fileEntry : folder.listFiles()) {
		if (fileEntry.isDirectory()) {
		    readFiles(fileEntry, nameFilter,
			    pac.concat(fileEntry.getName()));
		} else {
		    if (fileEntry.getName().endsWith(".class")) {
			try {
			    URL url = file.toURI().toURL();
			    URL[] urls = new URL[] { url };
			    String cls = pac.concat(fileEntry.getName()
				    .replaceAll(".class", ""));
			    @SuppressWarnings("resource")
			    Class<?> clas = new URLClassLoader(urls)
				    .loadClass(cls);
			    System.out.println("\t" + clas.getName());
			    allClasses.add(clas);
			    if (cls.equalsIgnoreCase(nameFilter)) {
				testClasses.add(clas);
			    }
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static void readOpCodeOfClasses(JCallGraph jcg) {
	// test classes from OPCode
	for (Description description : testDescriptionList) {
	    System.out.print("\t" + description.getActualClass().getName()
		    + ": ");
	    // for each test class
	    OPCodeDescription opCodeDescription = new OPCodeDescription(
		    description);
	    jcg.lookInsideClass(opCodeDescription, description,
		    description.getJavaClass());
	    description.addOPCodeDescription(opCodeDescription);
	    System.out.print("DONE!\n");
	}
    }
}

class Progress implements Runnable {
    @Override
    public void run() {
	while (!Thread.currentThread().isInterrupted()) {
	    try {
		System.out.print(".");
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		Thread.currentThread().interrupt();
	    } catch (Exception e) {
	    }
	}
    }
}