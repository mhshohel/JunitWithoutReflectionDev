/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName GenerateOutput.java
 * 
 * @FileCreated Jan 03, 2013
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <li><strong><i>GenerateOutput</i></strong></li>
 * 
 * <pre>
 * public class GenerateOutput
 * </pre>
 * 
 * <p>
 * This is the main class where you can set the location of output file and set
 * the name. And this the execution point to generate output code by using all
 * required classes.
 * </p>
 * 
 * @author Shohel Shamim
 */
public class GenerateOutput {
    /*
     * A method's type and access modifier to support some code. initially it is
     * "protected static void". you can change the method name from constructor.
     */
    private final String externalMethodCode;
    /*
     * A method's name. initially it is "extendedMethod". you can change the
     * method name from constructor. it will be use by connect with
     * externalMethodCode,
     */
    private final String externalMethodName;
    /* keep file location. */
    private String fileLocation;
    /* format package name, add "package" before packages */
    private String formatPackageName;
    /* this object contains all generated code. */
    private GenerateCode generateCode;
    /* True if file code writing complete */
    private boolean isWritingComplete = false;
    /*
     * Max length of 65536 bytes equivalent of 524288 length of String; So, safe
     * site I have used 350000; after that length of text it will create an
     * extended class
     */
    private final int MAX_CODE_LENGTH = 350000;// 350000;
    /*
     * If multiple class create for Max code length then each class will be
     * separated by an additional incremental number
     */
    private int newClassCounter = 1;
    /* Keep output class file name with ".java" sign */
    private String outputClassFile;
    /* Keep output file name without ".java" sign */
    private String outputClassName;
    /* Keep package name */
    private String packageName;
    /* Keep list of all required packages */
    private List<String> packages = new ArrayList<String>();
    /* Keep generated .java file with directory location */
    private List<String> genJavaFileListWithLocation = new ArrayList<String>();

    /**
     * <li><strong><i>GenerateOutput</i></strong></li>
     * 
     * <pre>
     * public GenerateOutput(Class<?> clss, String packageName,
     * 	    String outputClassName, boolean methodShouldSort, String directory)
     * 	    throws Exception
     * </pre>
     * 
     * <p>
     * Gets Test class name, file location, package name and output class name;
     * This class is the main source to generate all related output. directory
     * contains the physical path of output folder
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * @param packageName
     *            - a Package name.
     * @param outputClassName
     *            - a class name.
     * @param methodShouldSort
     *            - true or false.
     * @param directory
     *            - physical path of output class
     * @throws Exception
     * 
     * @author Shohel Shamim
     */
    public GenerateOutput(Class<?> clss, String packageName,
	    String outputClassName, boolean methodShouldSort, String directory)
	    throws Exception {
	this.fileLocation = directory;
	this.packageName = packageName;
	this.formatPackageName = "package " + this.packageName + ";\n\n";
	this.outputClassName = outputClassName;
	this.outputClassFile = outputClassName + ".java";
	this.generateCode = new GenerateCode(new ClassRecords(clss),
		this.outputClassName, methodShouldSort, directory);
	this.externalMethodName = "extendedMethod";
	this.externalMethodCode = "protected static void "
		+ this.externalMethodName + "("
		+ this.generateCode.getMethodParameter(false) + ")";
	// }
    }

    /**
     * <li><strong><i>addPackage</i></strong></li>
     * 
     * <pre>
     * private void addPackage(String value)
     * </pre>
     * 
     * <p>
     * Add all unique packages as String; Each package is the package of output
     * class; Ex: "tools.code.gen".
     * </p>
     * 
     * @param value
     *            - package name with "." sign.
     * 
     * @author Shohel Shamim
     */
    private void addPackage(String value) {
	if (!this.packages.contains(value)) {
	    this.packages.add(value);
	}
    }

    /**
     * <li><strong><i>createMethodForParameterized</i></strong></li>
     * 
     * <pre>
     * private String createMethodForParameterized(String stab)
     * </pre>
     * 
     * <p>
     * Additional method if class has annotation Parameterized. stab is nothing
     * but 4 spaces.
     * </p>
     * 
     * @param stab
     *            - combination of 4 spaces
     * @return String - generated method.
     * @author Shohel Shamim
     */
    private String createMethodForParameterized(String stab) {
	String method = "";
	method += stab + "public static Object "
		+ this.generateCode.getParamSuppMethodName()
		+ "(Collection<?> data, int number, int pos) {\n";
	method += stab + stab + "Object[] value = null;\n";
	method += stab + stab + "try {\n";
	method += stab + stab + stab
		+ "value = (Object[]) ((List<?>) data).get(number)" + ";\n";
	method += stab + stab + "} catch (Exception e) {\n";
	method += stab + stab + stab + "e.printStackTrace();\n";
	method += stab + stab + "}\n";
	method += stab + stab + "return value[pos];\n";
	method += stab + "}\n\n\n";
	return method;
    }

    /**
     * <li><strong><i>createMethodForParamSuppBy</i></strong></li>
     * 
     * <pre>
     * private String createMethodForParamSuppBy(String stab)
     * </pre>
     * 
     * <p>
     * Additional method if class has annotation ParamSuppliedBy.
     * </p>
     * 
     * @param stab
     *            - combination of 4 spaces
     * @return String - generated method.
     * @author Shohel Shamim
     */
    private String createMethodForParamSuppBy(String stab) {
	String method = "";
	method += stab + "public static Object "
		+ this.generateCode.getParamSuppMethodName()
		+ "(Object data, int number) {\n";
	method += stab + stab + "Object value = null;\n";
	method += stab + stab + "try {\n";
	method += stab
		+ stab
		+ stab
		+ "value = ((PotentialAssignment) ((List) data).get(number)).getValue();\n";
	method += stab + stab
		+ "} catch (CouldNotGenerateValueException e) {\n";
	method += stab + stab + stab + "e.printStackTrace();\n";
	method += stab + stab + "}\n";
	method += stab + stab + "return value;\n";
	method += stab + "}\n\n\n";
	return method;
    }

    /**
     * <li><strong><i>createNewClasss</i></strong></li>
     * 
     * <pre>
     * private void createNewClasss(String codes, String className,
     * 	    String additionalClass, boolean defaultMethod)
     * </pre>
     * 
     * <p>
     * If a class file cannot contains all code then it will create new classes
     * as extended class with additional methods. Otherwise it will create
     * default class for output. If defaultMethod is true then it will create
     * default method not additional method.
     * </p>
     * 
     * @param codes
     *            - generated code.
     * @param className
     *            - class name.
     * @param additionalClass
     *            - additional class name.
     * @param defaultMethod
     *            - true or false
     * 
     * @author Shohel Shamim
     */
    private void createNewClasss(String codes, String className,
	    String additionalClass, boolean defaultMethod) {
	try {
	    String fileName = (this.fileLocation + className).trim();
	    this.genJavaFileListWithLocation.add(fileName);
	    PrintWriter output = new PrintWriter(fileName.concat(".java"));

	    String stab = "    ";
	    headerCodes(output, className);
	    StringBuilder sb = new StringBuilder();
	    sb.append(this.formatPackageName);
	    if (className.equalsIgnoreCase(this.generateCode
		    .getParamSuppClassName())) {
		if (this.generateCode.hasParameterSuppliedByForOutputClass()
			|| this.generateCode.hasParametersForOutputClass()) {
		    getAdditionalPackages();
		}
	    }
	    for (String name : this.packages) {
		sb.append("import " + name + ";\n");
	    }
	    sb.append("\npublic class " + className + " " + additionalClass
		    + " {\n");
	    if (defaultMethod) {
		sb.append(stab + this.externalMethodCode + " {\n");
		sb.append(stab + stab + "try {\n");
		sb.append(codes);
		sb.append(stab + stab + "}catch(Exception e) {\n");
		sb.append(stab + stab + stab + "e.printStackTrace();\n");
		sb.append(stab + stab + "}catch(Throwable t) {\n");
		sb.append(stab + stab + stab + "t.printStackTrace();\n");
		sb.append(stab + stab + "}\n");
		sb.append(stab + "}\n");
	    } else {
		sb.append(codes);
	    }
	    sb.append("}");
	    output.print(sb.toString());
	    output.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

    /**
     * <li><strong><i>deleteExistingOutputFilesBeforeRun</i></strong></li>
     * 
     * <pre>
     * private void deleteExistingOutputFilesBeforeRun()
     * </pre>
     * 
     * <p>
     * Delete only the file that is already in the folder.
     * </p>
     * 
     * 
     * @author Shohel Shamim
     */
    private void deleteExistingOutputFilesBeforeRun() {
	try {
	    File directory = new File(this.fileLocation);
	    File[] files = directory.listFiles();
	    if (files != null) {
		for (File file : files) {
		    if (file.isFile()) {
			if (file.getName().equals(outputClassName)) {
			    file.delete();
			}
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * <li><strong><i>execute</i></strong></li>
     * 
     * <pre>
     * public List<String> execute() <blockquote>throws</blockquote> FileNotFoundException
     * </pre>
     * 
     * <p>
     * Execute output; Write code in file, format code and return code as output
     * </p>
     * 
     * @return List<String> - return list of Generated .java files name and
     *         directory
     * 
     * @author Shohel Shamim
     */
    public List<String> execute() throws FileNotFoundException {
	deleteExistingOutputFilesBeforeRun();
	for (String str : this.generateCode.getPackages()) {
	    this.packages.add(str);
	}
	String stab = "    ";
	StringBuilder sb = new StringBuilder();
	boolean isExternalClassCreated = false;
	String mainEntry = stab + "public static void main(String[] args) {\n"
		+ stab + stab + "try {\n";
	PrintWriter output = null;
	try {
	    this.isWritingComplete = false;
	    String fileName = (this.fileLocation + this.outputClassFile).trim();
	    this.genJavaFileListWithLocation
		    .add((this.fileLocation + this.outputClassName).trim());
	    output = new PrintWriter(fileName);
	    headerCodes(output, this.outputClassName);
	    output.print(this.formatPackageName);
	    int length = this.generateCode.getCodes().size();
	    List<String> newClasses = new ArrayList<String>();
	    for (int i = 0; i < length; i++) {
		sb.append(stab + stab + stab
			+ this.generateCode.getCodes().get(i) + "\n");
		if (sb.length() > MAX_CODE_LENGTH) {
		    isExternalClassCreated = true;
		    newClasses.add((this.outputClassName + String
			    .valueOf(this.newClassCounter)).trim());
		    if (i + 1 != length) {
			createNewClasss(
				sb.toString(),
				(this.outputClassName + String
					.valueOf(this.newClassCounter)).trim(),
				"extends "
					+ (this.outputClassName + String
						.valueOf(this.newClassCounter + 1))
						.trim(), true);
		    } else {
			createNewClasss(sb.toString(),
				(this.outputClassName + String
					.valueOf(this.newClassCounter)).trim(),
				"", true);
		    }
		    sb = new StringBuilder();
		    this.newClassCounter++;
		}
	    }
	    if (isExternalClassCreated && sb.length() != 0) {
		createNewClasss(sb.toString(),
			(this.outputClassName + String
				.valueOf(this.newClassCounter)).trim(), "",
			true);
		newClasses.add((this.outputClassName + String
			.valueOf(this.newClassCounter)).trim());
	    }
	    for (String name : this.packages) {
		output.print("import " + name + ";\n");
	    }
	    if (isExternalClassCreated) {
		output.print("\npublic class " + this.outputClassName
			+ " extends " + this.outputClassName.concat("1")
			+ " {\n");
		generateStaticFields(output, stab);
		output.print(mainEntry);
		writeFields(output, stab);
		for (String obj : newClasses) {
		    output.print(stab + stab + stab + obj + "."
			    + this.externalMethodName + "("
			    + this.generateCode.getMethodParameter(true)
			    + ");\n");
		}

	    } else {
		output.print("\npublic class " + this.outputClassName + " {\n");
		generateStaticFields(output, stab);
		output.print(mainEntry);
		writeFields(output, stab);
		output.print(sb.toString());
	    }
	    output.print(stab + stab + "}catch(Exception e) {\n");
	    output.print(stab + stab + stab + "e.printStackTrace();\n");
	    output.print(stab + stab + "}catch(Throwable t) {\n");
	    output.print(stab + stab + stab + "t.printStackTrace();\n");
	    output.print(stab + stab + "}\n");
	    output.print(stab + "}\n");
	    String codes = "";
	    if (this.generateCode.hasParameterSuppliedByForOutputClass()
		    || this.generateCode.hasParametersForOutputClass()) {
		if (this.generateCode.hasParameterSuppliedByForOutputClass()) {
		    codes += createMethodForParamSuppBy(stab);
		}
		if (this.generateCode.hasParametersForOutputClass()) {
		    codes += createMethodForParameterized(stab);
		}
		createNewClasss(codes,
			this.generateCode.getParamSuppClassName(), "", false);
	    }
	    output.print("}\n");
	} catch (FileNotFoundException e) {
	    throw new FileNotFoundException("File location not found...");
	} finally {
	    output.close();
	    this.isWritingComplete = true;
	}
	return this.genJavaFileListWithLocation;
    }

    /**
     * <li><strong><i>generateStaticFields</i></strong></li>
     * 
     * <pre>
     * private void generateStaticFields(PrintWriter output, String stab)
     * </pre>
     * 
     * <p>
     * Generate static fields, used for Rules. Static fields required so that
     * other class can use them without creating object.
     * </p>
     * 
     * @param output
     *            - PrintWriter
     * @param stab
     *            - combination of 4 spaces.
     * 
     * @author Shohel Shamim
     */
    private void generateStaticFields(PrintWriter output, String stab) {
	for (String field : this.generateCode.getStaticFieldsForRules()) {
	    output.print(stab + "public static " + field + ";\n");
	}
	if (!this.generateCode.getStaticFieldsForRules().isEmpty()) {
	    output.print("\n");
	}
    }

    /**
     * <li><strong><i>getAdditionalPackages</i></strong></li>
     * 
     * <pre>
     * private void getAdditionalPackages()
     * </pre>
     * 
     * <p>
     * If there is some classes called to support other class then package of
     * that class need to be count. Especially used for Rule, ClassRules
     * annotated fields.
     * </p>
     * 
     * @author Shohel Shamim
     */
    private void getAdditionalPackages() {
	// for List
	addPackage("java.util.List");
	// for Collection
	addPackage("java.util.Collection");
	if (this.generateCode.hasParameterSuppliedByForOutputClass()) {
	    addPackage("org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException");
	}
    }

    /**
     * <li><strong><i>headerCodes</i></strong></li>
     * 
     * <pre>
     * private void headerCodes(PrintWriter output, String className)
     * </pre>
     * 
     * <p>
     * Simple header text contains Developer's information.
     * </p>
     * 
     * @param output
     *            - PrintWriter
     * @param stab
     *            - combination of 4 spaces.
     * 
     * @author Shohel Shamim
     */
    private void headerCodes(PrintWriter output, String className) {
	Date dateTime = new Date();
	String date = new SimpleDateFormat("MMM dd, YYYY - HH:mm:ss")
		.format(dateTime.getTime());
	StringBuilder sb = new StringBuilder();
	sb.append("/**\n");
	sb.append(" *\n");
	sb.append(" * @ProjectName JUnitWithoutReflection\n");
	sb.append(" *\n");
	sb.append(" * @FileName " + className + ".java\n");
	sb.append(" *\n");
	sb.append(" * @FileCreated " + date + "\n");
	sb.append(" *\n");
	sb.append(" * @Author MD. SHOHEL SHAMIM\n");
	sb.append(" *\n");
	sb.append(" * @CivicRegistration 19841201-0533\n");
	sb.append(" *\n");
	sb.append(" * MSc. in Software Technology\n");
	sb.append(" *\n");
	sb.append(" * Linnaeus University, Växjö, Sweden\n");
	sb.append(" *\n");
	sb.append(" */\n");
	output.print(sb.toString());
    }

    /**
     * <li><strong><i>isWritingComplete</i></strong></li>
     * 
     * <pre>
     * public boolean isWritingComplete()
     * </pre>
     * 
     * <p>
     * Return true if code writing in file is complete
     * </p>
     * 
     * @return boolean - true or false
     * 
     * @author Shohel Shamim
     */
    public boolean isWritingComplete() {
	return this.isWritingComplete;
    }

    /**
     * <li><strong><i>writeFields</i></strong></li>
     * 
     * <pre>
     * private void writeFields(PrintWriter output, String stab)
     * </pre>
     * 
     * <p>
     * Generate fields to output class.
     * </p>
     * 
     * @param output
     *            - PrintWriter
     * @param stab
     *            - combination of 4 spaces.
     * 
     * @author Shohel Shamim
     */
    private void writeFields(PrintWriter output, String stab) {
	for (String entryField : this.generateCode.getEntryClassFields()) {
	    output.print(stab + stab + stab + entryField + "\n");
	}
	for (String otherField : this.generateCode.getOtherFields()) {
	    output.print(stab + stab + stab + otherField + "\n");
	}
    }
}