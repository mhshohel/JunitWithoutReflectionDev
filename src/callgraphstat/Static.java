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
import java.util.Collection;
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
	// keep only nodes that is accessed by other class
	public static List<String> applicationNodes = new ArrayList<String>();
	public static List<String> nodes = new ArrayList<String>();
	public static List<String> edges = new ArrayList<String>();
	public static List<String> libEdges = new ArrayList<String>();
	public static List<String> noAccessedNodes = new ArrayList<String>();
	public static List<String> noAccessedClasses = new ArrayList<String>();
	private static int id = 0;
	// keep Description objects that once is initialized, to avoid duplicate
	public static Map<String, Description> initializedDescriptions = new LinkedHashMap<String, Description>();
	// keep values that is not Description type, before us it please clear all
	// values
	public static Stack<Object> someValues = new Stack<Object>();
	public static int num = 0;

	public static void addApplicationNodes(String node) {
		if (!applicationNodes.contains(node)) {
			applicationNodes.add(node);
		}
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
		if (!noAccessedNodes.contains(target)) {
			noAccessedNodes.add(target);
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

	public static boolean containsElementInCollection(Collection<?> elements,
			Object value) {
		for (Object element : elements) {
			if (element instanceof Description && value instanceof Description) {
				if (((Description) element).hashCode() == ((Description) value)
						.hashCode()) {
					return true;
				}
			} else if (element.toString().equalsIgnoreCase(value.toString())) {
				return true;
			}
		}
		return false;
	}

	public static Description createDescriptionOfClass(String className,
			Map<String, Description> classDescriptions) {
		Description description = null;
		try {
			Class<?> clas = Class.forName(className);
			Static.err("CLASS NAME:  " + clas.getName());
			// make sure about Description, if some problem found then it should
			// not create Description instance
			try {
				description = new Description(clas, classDescriptions);
			} catch (Exception e) {
				// ignore this error, it will be cannot parse JavaClass, so make
				// description = null, so that it can not preced next
				description = null;
			}
			if (description != null) {
				if (!classDescriptions.containsKey(clas.getName())) {
					classDescriptions.put(clas.getName(), description);
				}
				setSuperClassDescription(description, classDescriptions);
				setInterfaceForDescription(description, classDescriptions);
			}
		} catch (ClassNotFoundException e) {
			// ignore exception
			Static.err("Class Not Found: " + e.getMessage());
			description = null;
		} catch (Exception e) {
			// ignore exception
			Static.err(e.getMessage());
			description = null;
		}
		return description;
	}

	// TODO:REMOVE ME: PRINT ERR
	// ---------------------------------------
	public static void err(Object obj) {
		System.err.println((obj == null) ? "null" : obj);
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

	public static int getNewID() {
		id++;
		return id;
	}

	public final static List<String> getSortdNoAccessLibraryClass() {
		Collections.sort(noAccessedClasses);
		return noAccessedClasses;
	}

	public final static List<String> getSortedApplicationNodes() {
		Collections.sort(applicationNodes);
		return applicationNodes;
	}

	public final static List<String> getSortedEdges() {
		Collections.sort(edges);
		return edges;
	}

	public final static List<String> getSortedLibraryEdges() {
		Collections.sort(libEdges);
		return libEdges;
	}

	public final static List<String> getSortedLibraryNodesNoAccess() {
		Collections.sort(noAccessedNodes);
		return noAccessedNodes;
	}

	public final static List<String> getSortedNodes() {
		Collections.sort(nodes);
		return nodes;
	}

	public final static List<String> getUnSortdApplicationNodes() {
		return applicationNodes;
	}

	public final static List<String> getUnSortdNoAccessLibraryClass() {
		return noAccessedClasses;
	}

	public final static List<String> getUnSortedEdges() {
		return edges;
	}

	public final static List<String> getUnSortedLibraryEdges() {
		return libEdges;
	}

	public final static List<String> getUnSortedLibraryNodesNoAccess() {
		return noAccessedNodes;
	}

	public final static List<String> getUnSortedNodes() {
		return nodes;
	}

	public static boolean isCollectionsOrMap(String classWithPackage) {
		try {
			if (Static.isPrimitiveTypeString(classWithPackage)) {
				return false;
			} else if (classWithPackage != "") {
				Class<?> cls = Class.forName(classWithPackage);
				if (Collection.class.isAssignableFrom(cls)
						|| Map.class.isAssignableFrom(cls)) {
					// TODO Remove me
					Static.out("\t\t\t\tCollections or Map type: TRUE");
					return true;
				} else {
					// TODO Remove me
					Static.out("\t\t\t\tCollections or Map type: FALSE");
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			Static.err("ERROR: isCollectionsOrMap");
			return false;
		}
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

	// TODO:REMOVE ME: PRINT OUT
	// ---------------------------------------
	public static void out(Object obj) {
		System.out.println((obj == null) ? "null" : obj);
	}

	// TODO:REMOVE ME: JUST A TEST NUMBER FOR TRACE
	// ---------------------------------------
	public static void printNum() {
		num++;
		System.err.println("COUNTER: ?=========================? " + (num)
				+ " ?=========================?");
	}

	private static void setInterfaceForDescription(
			Description currentDescription,
			Map<String, Description> classDescriptions) {
		Class<?> currentClass = currentDescription.getActualClass();
		for (Class<?> cls : currentClass.getInterfaces()) {
			Description description = classDescriptions.get(cls.getName());
			if (description != null) {
				currentDescription.addInterfaceDescription(description);
			} else {
				Description newDescription = createDescriptionOfClass(
						cls.getName(), classDescriptions);
				if (newDescription != null) {
					currentDescription.addInterfaceDescription(newDescription);
				}
			}
		}
	}

	private static void setSuperClassDescription(
			Description currentDescription,
			Map<String, Description> classDescriptions) {
		Class<?> currentClass = currentDescription.getActualClass();
		Class<?> superClass = currentClass.getSuperclass();
		if (superClass != null) {
			Description description = classDescriptions.get(superClass
					.getName());
			if (description != null) {
				currentDescription.addSuperClassDescription(description);
			} else {
				Description newDescription = createDescriptionOfClass(
						superClass.getName(), classDescriptions);
				if (newDescription != null) {
					currentDescription.addSuperClassDescription(newDescription);
				}
			}
		}
		currentDescription.isSuperClassObjectInitiated = (superClass == null) ? true
				: false;
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

	// ------------------------------------
}
