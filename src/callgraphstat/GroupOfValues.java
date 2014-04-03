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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.bcel.generic.Type;

public class GroupOfValues {
	private Stack<Object> values = null;
	public boolean isOpen = true;
	private int id = -1;
	private int endLineNumber = -1;

	public void setEndLineNumber(int number) {
		this.endLineNumber = number;
	}

	public int getEndlineNumber() {
		return this.endLineNumber;
	}

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

			// TODO verify it???
			this.values.remove(value);
			this.values.add(value);
		}
	}

	// add values that needs GroupValues close
	public void forceAdd(Object value) {
		open();
		this.add(value);
		close();
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

	private List<Object> allValues = new ArrayList<Object>();

	public List<Object> getAllValues(Type type, Description description) {
		this.allValues = new ArrayList<Object>();
		getValuesFromGroup(this, type, description);
		return (this.allValues.isEmpty()) ? null : this.allValues;
	}

	private Object getValuesFromGroup(GroupOfValues gov, Type type,
			Description description) {
		for (Object object : gov.values) {
			if (object instanceof GroupOfValues) {
				getValuesFromGroup(((GroupOfValues) object), type, description);
			} else if (!this.allValues.contains(object)) {
				if (!object.toString().equalsIgnoreCase(Static.NULL)
						&& !object.toString()
								.equalsIgnoreCase(Static.PRIMITIVE)) {
					object = Static.verifyTypeFromObjectsToStore(object, type,
							description);
					if (object != null && !this.allValues.contains(object)) {
						this.allValues.add(object);
					}
				}
			}
		}
		return this.allValues;
	}

	public int size() {
		return this.values.size();
	}

	public void close(int number) {
		if (this.endLineNumber <= number) {
			close();
		}
	}

	public void close() {
		this.isOpen = false;
	}

	public void open() {
		this.isOpen = true;
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