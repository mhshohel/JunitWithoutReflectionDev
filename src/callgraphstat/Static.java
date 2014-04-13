/**
 *
 * @ProjectName StaticCallGraph
 *
 * @PackageName callgraphstat
 *
 * @FileName Static.java
 * 
 * @FileCreated Mar 29, 2014
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.bcel.generic.Type;

public class Static {
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
	public static List<String> noAccessedClasses = new ArrayList<String>();
	private static int id = 0;
	// keep Description objects that once is initialized, to avoid duplicate
	public static Map<String, Description> initializedDescriptions = new LinkedHashMap<String, Description>();
	// keep values that is not Description type, before us it please clear all
	// values
	public static Stack<Object> someValues = new Stack<Object>();

	public final static List<String> getUnsortedNodes() {
		return nodes;
	}

	public final static List<String> getSortedNodes() {
		Collections.sort(nodes);
		return nodes;
	}

	public final static boolean addEdge(String source, String target) {
		String edge = source.concat(" -- > ").concat(target).trim();
		Static.out("\t\t\tEdge: " + edge);
		if (edges.contains(edge)) {
			return true;
		}
		edges.add(edge);
		return false;
	}

	public final static boolean addLibraryEdge(String source, String target,
			String className) {
		String libEdge = source.concat(" -- > ").concat(target).trim();
		Static.out("\t\t\tLib Edge: " + libEdge);
		if (!noAccessedClassesOrMethod.contains(target)) {
			noAccessedClassesOrMethod.add(target);
		}
		if (!noAccessedClasses.contains(className)) {
			noAccessedClasses.add(className);
		}
		if (libEdges.contains(libEdge)) {
			return true;
		}
		libEdges.add(libEdge);
		return false;
	}

	// if no value matched or not found in stack then type should check into
	// Description list
	public static Object getDescriptionCopy(Description classDescription,
			Object name) {
		Description description = classDescription
				.getDescriptionByClassName(name.toString());
		if (description != null) {
			return description.copy();
		}

		return name;
	}

	public static boolean isPrimitiveType(Type type) {
		if (type == Type.BOOLEAN || type == Type.BYTE || type == Type.CHAR
				|| type == Type.DOUBLE || type == Type.FLOAT
				|| type == Type.INT || type == Type.LONG || type == Type.SHORT
				|| type.equals(Type.STRING) || type.equals(Type.STRINGBUFFER)
				|| type.equals(Type.CLASS)) {
			// || type.equals(Type.OBJECT)) {
			return true;
		}
		return false;
	}

	public static boolean isPrimitiveTypeString(String type) {
		switch (type) {
		case "boolean":
		case "byte":
		case "char":
		case "double":
		case "float":
		case "int":
		case "long":
		case "short":
		case "string":
		case "String":
		case "java.lang.String":
		case "Class":
		case "java.lang.Class":
			return true;
		default:
			return false;
		}
	}

	public static boolean isSameType(String classType, String stackType) {
		try {
			classType = classType.replace("[]", "");
			if (!classType.equalsIgnoreCase(stackType)) {
				Class<?> param = Class.forName(classType);
				Class<?> stack = Class.forName(stackType);
				if (param.isAssignableFrom(stack)
						|| stack.isAssignableFrom(param)) {
					return true;
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public static Object verifyTypeFromObjectsToStoreFromGOV(Object value,
			Type type, Description description) {
		try {
			if (value != null && value.toString().equals(Static.NULL)) {
				value = Static.NULL;
			} else {
				if (value != null) {
					boolean result = isSameType(type.toString(),
							value.toString());
					if (!result) {
						value = Static.NULL;
					}
				}
			}
		} catch (Exception e) {
		}
		return value;
	}

	public static Object verifyTypeFromObjectsToStore(Object value, Type type,
			Description description) {
		try {
			if (Static.isPrimitiveType(type)) {
				if (!value.toString().equalsIgnoreCase(Static.NULL)) {
					value = Static.PRIMITIVE;
				}
			} else if (value != null && value.toString().equals(Static.NULL)) {
				value = Static.NULL;
			} else {
				if (value != null) {
					if (value.toString().equalsIgnoreCase(Static.PRIMITIVE)
							|| value instanceof String) {
						if (Static.isPrimitiveType(type)) {
							value = Static.PRIMITIVE;
						} else {
							value = null;
						}
					} else {
						boolean result = isSameType(type.toString(),
								value.toString());
						if (!result) {
							value = Static.getDescriptionCopy(description,
									value);
							if (!isSameType(type.toString(), value.toString())) {
								value = Static.getDescriptionCopy(description,
										type);
							}
						}
					}
				}
			}
			if (value == null) {
				if (Static.isPrimitiveType(type)) {
					value = Static.PRIMITIVE;
				} else {
					value = Static.getDescriptionCopy(description, type);
				}
			}
		} catch (Exception e) {
			value = Static.getDescriptionCopy(description, type);
		}
		return value;
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

	public final static List<String> getSortdNoAccessLibraryClass() {
		Collections.sort(noAccessedClasses);
		return noAccessedClasses;
	}

	public final static List<String> getUnSortdNoAccessLibraryClass() {
		return noAccessedClasses;
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
