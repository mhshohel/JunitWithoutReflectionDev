/**
 *
 * @ProjectName JUnitWithoutReflection.Second
 *
 * @PackageName callgraphstat
 *
 * @FileName Options.java
 * 
 * @FileCreated Apr 15, 2014
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

import java.util.ArrayList;
import java.util.List;

public class Options {
	// if deepSearch is true search deeply, otherwise do normal search
	public static boolean deepSearch = false;
	public static boolean sortedReport = true;
	public static String path = "";
	public static List<String> packages = new ArrayList<String>();
	public static String mainClass = "";
	public static String mainMethod = "";
	public static String appName = "";
}
