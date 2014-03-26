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

	public static void main(String[] args) {
		// ca = new A();
		// ca = new B();
		SuperClassA ca = new A();
		if (!(ca instanceof A)) {
			new A();
		} else {
			new B();
			B b = new B();
			if (b instanceof SuperClassA) {
				new C();
			} else {
				new E();
			}
			D d = new D();
		}
		int o = 9;

		int p = 1;
		con((o == 9) ? new A() : (p == 1) ? new B() : new C(), new D());
		SuperClassA aaa = null;
		switch (o) {
		case 9:
			aaa = new A();
			break;
		case 0:
			aaa = new B();
			C c = new C();
			c.aValEx();
			break;
		}

		// D d = new D();
		// SuperClassA dss = d.getVals(ca);
		// if (dss != null)
		// dss.aValEx();

		// A d = new A();
		// ca = d;
		// SuperClassA ne = con(ca);
		// ca.aValEx();
		// C c = new C();
		// c.aValEx();
		// ((A) ne).claA();
		// ne.exam();

		//
		// int a = 9;
		//
		// // SuperClassA aa = new A();
		// // SuperClassA aaa = aa;
		//
		// // aa = null;
		// // aaa.aValEx();
		//
		// A aaass = new A();
		// SuperClassA aaa = null;
		// switch (a) {
		// case 9:
		// aaa = new A();
		// break;
		// case 0:
		// aaa = new B();
		// C c = new C();
		// c.aValEx();
		// break;
		// }
		//
		// A aaasdss = new A();
		//
		// if (a == 9) {
		// aaa = new A();
		// } else {
		// aaa = new B();
		// C c = new C();
		// c.aValEx();
		// }
		//
		// aaa.aValEx();

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
