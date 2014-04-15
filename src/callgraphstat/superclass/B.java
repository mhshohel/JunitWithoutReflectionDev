package callgraphstat.superclass;

import java.util.ArrayList;
import java.util.List;

public class B extends SuperClassA implements IInterface {
	private E evv = null;

	public B(E e) {
		super(e);
		this.evv = e;
	}

	public static List<SuperClassA> someList = new ArrayList<SuperClassA>();

	public static void add(SuperClassA value) {
		if (value != null)
			if (!someList.contains(value))
				someList.add(value);
	}

	public static List<SuperClassA> getList() {
		someList.add(new A());
		someList.add(new B(null));
		return someList;
	}

	static E e = new E();
	public A val = new A();

	// public void have() {
	// new F();
	// }
	//
	public void aValEx() {
		System.err.println("I am From B");
		A bv = new A();
		A h = bv;
	}

	public A retA() {
		aValEx();
		return this.val;
	}

	//
	// void exam() {
	// }

	@Override
	public void foo() {

	}
}
