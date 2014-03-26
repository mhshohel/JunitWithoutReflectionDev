package callgraphstat;

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
	private static int id = 0;

	public static int getNewID() {
		id++;
		return id;
	}
}
