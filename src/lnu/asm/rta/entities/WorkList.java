package lnu.asm.rta.entities;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class WorkList<E> {
	private LinkedList<E> current = new LinkedList<E>();
	private HashSet<E> added = new HashSet<E>();

	public void add(E o) { // Add if not already added
		if (o==null)
			throw new RuntimeException("Adding null to worklist");
		
		if (!added.contains(o)) {
			added.add(o);
			current.add(o);
		}
	}

	public int countAdded() { return added.size(); }
	public E getNext() {
		return current.removeFirst();
	}

	public Iterator<E> getAllAdded() {
		return added.iterator();
	}

	public boolean contains(Object o) {
		if (added.contains(o))
			return true;
		return false;
	}

	public boolean isEmpty() {
		return current.size() == 0;
	}
}
