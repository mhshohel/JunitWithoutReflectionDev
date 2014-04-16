/**
 *
 * @ProjectName StaticCallGraph
 *
 * @PackageName callgraphstat
 *
 * @FileName GroupOfValues.java
 * 
 * @FileCreated Mar 24, 2014
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

import java.util.Collection;
import java.util.Stack;

import org.apache.bcel.generic.Type;

public class GroupOfValues {
	private Stack<Object> values = null;
	public boolean isOpen = true;
	private int id = -1;
	private int endLineNumber = -1;

	private Stack<Object> allValues = new Stack<Object>();

	public GroupOfValues() {
		this.isOpen = true;
		values = new Stack<Object>();
		this.id = Static.getNewID();
	}

	// if isOpen false then value will be not added
	public void add(Object value) {
		if (isOpen) {
			// do not check contains here, because it will keep values like
			// tempStack, it should keep multiple values of same type

			// do not check contains or remove same type of value, because it
			// can be used for conditions, conditions need type values to remove
			// before creating "if", if object of same type removed then it will
			// miss important things, better compare it when try to store.
			this.values.add(value);
		}
	}

	// Object fail to add normal way
	public void addAtLast(Object object) {
		if (!this.values.isEmpty()) {
			this.add(object);
		} else {
			if (this.values.peek() instanceof GroupOfValues) {
				GroupOfValues gov = (GroupOfValues) this.values.peek();
				if (gov.isOpen) {
					gov.addAtLast(object);
				} else {
					this.add(object);
				}
			} else {
				this.add(object);
			}
		}
	}

	// add values that needs GroupValues close
	// public void forceAddz(Object value) {
	// open();
	// this.add(value);
	// close();
	// }

	public void close() {
		this.isOpen = false;
	}

	public void close(int number) {
		if (this.endLineNumber <= number) {
			close();
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof GroupOfValues) {
			return ((GroupOfValues) object).id == this.id;
		}
		return false;
	}

	public Stack<Object> getAllValues(Type type, Description description) {
		// change here for collection
		this.allValues = new Stack<Object>();
		if (Static.isPrimitiveType(type)) {
			this.allValues.add(Static.PRIMITIVE);
			return this.allValues;
		}
		getValuesFromGroup(this, type, description);
		return (this.allValues.isEmpty()) ? null : this.allValues;
	}

	public int getEndlineNumber() {
		return this.endLineNumber;
	}

	public Stack<Object> getValues() {
		return this.values;
	}

	private Object getValuesFromGroup(GroupOfValues gov, Type type,
			Description description) {
		for (Object object : gov.values) {
			if (object instanceof GroupOfValues) {
				getValuesFromGroup(((GroupOfValues) object), type, description);
			}
			if (object instanceof Collection) {
				for (Object stackValues : (Collection<?>) object) {
					if (!(stackValues.toString()
							.equalsIgnoreCase(Static.PRIMITIVE))) {
						Object thisValue = null;
						if (!Static.isCollectionsOrMap(type.toString())) {
							thisValue = Static
									.verifyTypeFromObjectsToStoreFromGOV(
											stackValues, type, description);
						} else {
							thisValue = stackValues;
						}
						if (!(allValues.contains(thisValue))) {
							allValues.add(thisValue);
						}
					}
				}
			} else if (!this.allValues.contains(object)) {
				if (!object.toString().equalsIgnoreCase(Static.PRIMITIVE)) {
					if (!Static.isCollectionsOrMap(type.toString())) {
						object = Static.verifyTypeFromObjectsToStoreFromGOV(
								object, type, description);
					}
					if (object != null && !this.allValues.contains(object)) {
						this.allValues.add(object);
					}
				}
			}
		}
		return this.allValues;
	}

	@Override
	public int hashCode() {
		return this.values.hashCode() + this.id;
	}

	public boolean isEmpty() {
		return this.values.isEmpty();
	}

	public void open() {
		this.isOpen = true;
	}

	public Object peek() {
		return (this.values.isEmpty()) ? null : this.values.peek();
	}

	public Object pop() {
		return (this.values.isEmpty()) ? null : this.values.pop();
	}

	public void reopen() {
		this.isOpen = true;
	}

	public void setEndLineNumber(int number) {
		this.endLineNumber = number;
	}

	public int size() {
		return this.values.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String msg = "(id=" + this.id + ",endline=" + this.endLineNumber + ","
				+ this.isOpen + "): ";
		if (this.values.isEmpty()) {
			sb.append("[" + msg + "none]");
		} else {
			String prefix = "";
			sb.append("[").append(msg);
			for (Object object : this.values) {
				sb.append(prefix);
				prefix = ",";
				sb.append(object);
			}
			sb.append("]");
		}
		return sb.toString();
	}
}