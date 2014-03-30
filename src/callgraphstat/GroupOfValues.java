package callgraphstat;

import java.util.Stack;

public class GroupOfValues {
	private Stack<Object> values = null;
	public boolean isOpen = true;
	private int id = -1;

	// Type will be added only once, when it will create first time
	// private String baseType = null;
	//
	// public void setType(String type) {
	// if (this.baseType == null) {
	// this.baseType = type.replace("[]", "");
	// }
	// }

	public GroupOfValues() {
		this.isOpen = true;
		values = new Stack<Object>();
		this.id = StaticValues.getNewID();
	}

	// if isOpen false then value will be not added
	public void add(Object value) {
		if (isOpen) {
			for (int i = 0; i < values.size(); i++) {
				if (values.get(i).hashCode() == value.hashCode()) {
					values.remove(i);
					break;
				}
			}
			if (!value.toString().equalsIgnoreCase(StaticValues.NULL)
					&& !value.toString().equalsIgnoreCase(
							StaticValues.PRIMITIVE)) {
				// if (isSameType(value.toString()))
				this.values.add(value);
			}
		}
	}

	// private boolean isSameType(String currentValueType) {
	// try {
	// if (!this.baseType.equalsIgnoreCase(currentValueType)) {
	// Class<?> stack = Class.forName(this.baseType);
	// Class<?> param = Class.forName(currentValueType);
	// if (param.isAssignableFrom(stack)
	// || stack.isAssignableFrom(param)) {
	// return true;
	// }
	// } else {
	// return true;
	// }
	// } catch (Exception e) {
	// return false;
	// }
	// return false;
	// }

	// public void forceAdd(Object value) {
	// this.isOpen = true;
	// this.add(value);
	// close();
	// }

	public int size() {
		return this.values.size();
	}

	public void close() {
		this.isOpen = false;
	}

	public void reopen() {
		this.isOpen = true;
	}

	public Stack<Object> getValues() {
		return this.values;
	}

	public boolean isEmpty() {
		return this.values.isEmpty();
	}

	public Object peek() {
		return (this.values.isEmpty()) ? null : this.values.peek();
	}

	public Object pop() {
		return (this.values.isEmpty()) ? null : this.values.pop();
	}

	@Override
	public int hashCode() {
		return this.values.hashCode() + this.id;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof GroupOfValues) {
			return ((GroupOfValues) object).id == this.id;
		}
		return false;
	}
}