package callgraphstat;

import java.util.Stack;

public class GroupOfValues {
	private Stack<Object> values = new Stack<Object>();
	private Object type = null;

	public Stack<Object> getValues() {
		return this.values;
	}

	public Object getType() {
		// actualParam = (this.description.hasDescription(params
		// .get(i).toString())) ? actualParam = params.get(i)
		// .toString() : this.types[i].toString();

		return this.type;
	}
}
