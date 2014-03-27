/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraphstat.superclass
 *
 * @FileName Example.java
 * 
 * @FileCreated Dec 13, 2013
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
package callgraphstat.superclass;

public class Example {

	static SuperClassA getVal() {
		return new A();
	}

	// static List<SuperClassA> aLists = new ArrayList<SuperClassA>();

	static SuperClassA con(SuperClassA a, D d) {
		// SuperClassA b = a;
		// b.aValEx();
		return a;
	}

	static SuperClassA ca = null;

	static void con(SuperClassA a, D d, SuperClassA b) {
		SuperClassA vb = a;
	}

	public static void main(String[] args) {
		// A d = new A();
		// d.dval = new D();
		// D f = d.dval;
		// ca = new A();
		// ca = new B();
		// new D().aValEx();
		// con(ca, new D(), new C());

		int b = 0;
		if (b > 0) {
			new C();
		}

		if (b > 8) {
			new D();
		} else if (b < 7) {
			new E();
		} else {
			new F();
			ca = null;
		}

		A a = new A();
		SuperClassA sa = null;
		while (a != null) {
			sa = new A();
			if (b > 0) {
				sa = new C();
			}
			sa = (sa == null) ? new A() : sa;
		}

		while (a != null) {
			sa = new A();
		}
		int h = 9;
		SuperClassA aaa = null;
		switch (h) {
		case 9:
			aaa = new A();
			break;
		case 0:
			aaa = new B();
			C c = new C();
			c.aValEx();
			break;
		default:
			break;
		}

		// con((a == 9) ? new A() : new B());
		// Example.aLists.add(new C());
		// Example.aLists.add(new A());
		// Example.aLists.add(new B());
		// // Stack<A> stack = new Stack<A>();
		// // stack.add(new A());
		// // stack.peek();
		// // stack.get(0);
		// // stack.pop();
		// // SuperClassA a = new SuperClassA();
		// // a.add(new A());
		//
		// List<List<SuperClassA>> aList = new ArrayList<List<SuperClassA>>();
		// aList.add(aLists);
		//
		// // SuperClassA aval = aList.get(0);
		// // aval.aValEx();
		//
		// List<Integer> i = new ArrayList<Integer>();
		// i.add(1);
		// i.add(2);
		// List<List<Integer>> ii = new ArrayList<List<Integer>>();
		// ii.add(i);
		// i.add(3);
		// List<Integer> oo = ii.get(0);
		// System.out.println(ii.get(0));
	}
}
