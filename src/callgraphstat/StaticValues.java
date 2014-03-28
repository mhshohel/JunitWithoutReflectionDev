package callgraphstat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StaticValues {
	public static final String UNKNOWN = "unknown";
	public static final String THIS = "this";
	public static final String NULL = "null";
	public static final String PRIMITIVE = "primitive";
	public static final String STRING = "string";
	public static final String CLASS = "class";
	public static final String OBJECT = "object";
	public static final String ARRAY_TYPE = "arr_typ";
	public static final String ARRAY_OBJECT = "arr_obj";
	public static final String COLLECTION_TYPE = "coll_typ";
	public static List<String> nodes = new ArrayList<String>();
	public static List<String> edges = new ArrayList<String>();
	public static List<String> libEdges = new ArrayList<String>();
	public static List<String> noAccessedClassesOrMethod = new ArrayList<String>();
	private static int id = 0;
	// keep Description objects that once is initialized, to avoid duplicate
	public static Map<String, Description> initializedDescriotions = new LinkedHashMap<String, Description>();

	public final static List<String> getNodes() {
		return nodes;
	}

	public final static boolean addEdge(String source, String target) {
		String edge = source.concat(" -- > ").concat(target).trim();
		StaticValues.out("\t\t\tEdge: " + edge);
		if (edges.contains(edge)) {
			return true;
		}
		edges.add(edge);
		return false;
	}

	public final static boolean addLibraryEdge(String source, String target) {
		String libEdge = source.concat(" -- > ").concat(target).trim();
		StaticValues.out("\t\t\tLib Edge: " + libEdge);
		if (!noAccessedClassesOrMethod.contains(target)) {
			noAccessedClassesOrMethod.add(target);
		}
		if (libEdges.contains(libEdge)) {
			return true;
		}
		libEdges.add(libEdge);
		return false;
	}

	public final static List<String> getSortedEdges() {
		Collections.sort(edges);
		return edges;
	}

	public final static List<String> getUnSortedEdges() {
		return edges;
	}

	public final static List<String> getSorteLibraryEdges() {
		Collections.sort(libEdges);
		return libEdges;
	}

	public final static List<String> getUnSortedLibraryEdges() {
		return libEdges;
	}

	public final static List<String> getSortdLibraryClassOrMethodNoAccess() {
		Collections.sort(noAccessedClassesOrMethod);
		return noAccessedClassesOrMethod;
	}

	public final static List<String> getUnSortdLibraryClassOrMethodNoAccess() {
		return noAccessedClassesOrMethod;
	}

	public static int getNewID() {
		id++;
		return id;
	}

	public static void out(Object obj) {
		System.out.println((obj == null) ? "null" : obj);
	}

	public static void err(Object obj) {
		System.err.println((obj == null) ? "null" : obj);
	}
}
