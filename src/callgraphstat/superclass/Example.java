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

	// static SuperClassA getVal() {
	// return new A();
	// }
	//
	// // static List<SuperClassA> aLists = new ArrayList<SuperClassA>();
	//
	// static SuperClassA con(SuperClassA a, D d) {
	// // SuperClassA b = a;
	// // b.aValEx();
	// return a;
	// }
	//
	// static SuperClassA ca = null;
	//
	// static void con(SuperClassA a, D d, SuperClassA b) {
	// SuperClassA vb = a;
	// }
	//
	// static SuperClassA getSome() {
	// int t = 0;
	// C c = new C();
	// if (t == 0) {
	// return new A();
	// } else if (t == 1) {
	// return new B();
	// }
	// return c;
	// }

	// static H hhh = new H();

	SuperClassA whatMethod() {
		int i = 0;
		if (i == 0) {
			return new A();
		} else if (i > 0) {
			return new B();
		}

		return (i < 0) ? null : new D();
	}

	public static void main(String[] args) {

		// G g = new G();
		// H h = new H();
		// h.a = new A();

		int i = 0;
		int b = 10;
		SuperClassA aaa = null;

		if (aaa instanceof SuperClassA) {
			aaa = new C();
		}

		if (aaa instanceof SuperClassA) {
			if (aaa != null) {
				if (i > 0) {
					if (b == 10) {
						aaa = null;
					}
				}
			}
		}

		if (i == 0) {
			b = 11;
		}

		aaa = (i == 0) ? new A() : (i > 9) ? new B()
				: (aaa != null && i != 0) ? new C() : new D();

		for (int k = 0; k < 10; k++) {
			if (aaa == null) {
				for (int j = 0; j < b; j++) {
					String s = "SHK";
				}
				aaa = new A();
			} else {
				aaa = new B();
			}
		}

		E e = new E();
		SuperClassA dataAB = (i == 0) ? new A() : (e == null) ? new B()
				: (e != null && i != 0) ? new C() : new D();
		if (i == 0) {
			A aa = new A();
			if (i > 0) {
				B nb = new B();
			} else {
				C c = new C();
			}
			new D();
			SuperClassA dataABn = (i == 0) ? new A() : null;
		}
		SuperClassA a = null;
		SuperClassA sa = null;

		while (a != null) {
			sa = new A();
			if (b > 0) {
				sa = new C();
			}
			sa = (sa == null) ? new A() : (sa != null) ? new B() : new C();
		}

		while (a != null) {
			sa = new A();
		}

		do {
			sa = new D();
		} while (a != null);

		int h = 9;
		SuperClassA aaaa = null;
		switch (h) {
		case 9:
			aaa = new A();
			break;
		case 0:
			aaa = new B();
			C c = new C();

			break;
		default:
			break;
		}

		// SuperClassA ds = null;
		// SuperClassA data = new C();
		// SuperClassA dd = null;
		// E c = null;
		// H h = null;
		// if (i == 5) {
		// new E();
		// F f = new F();
		// D d = new D();
		//
		// data = (i == 0) ? new A() : data;
		// SuperClassA dataA = (i == 0) ? new A() : new D();
		// SuperClassA dataAB = (i == 0) ? new A() : (e == null) ? new B()
		// : (e != null) ? new C() : new D();
		// c = new E();
		// data = dataAB;
		// ds = data;
		// } else if (i < 4) {
		// ds = null;
		// } else if (i > 9) {
		// dd = new A();
		// } else {
		// h = new H();
		// }
		//
		// h = null;
		// SuperClassA dataA = (i == 0) ? new A() : (e == null) ? new B()
		// : new C();
		// ds = data;
		// data = new A();

		// Stack<Object> vas = new Stack<Object>();
		// vas.add(new A());
		// vas.add(new B());
		// vas.add(new H());
		// vas.add(new H());
		//
		// B bs = (B) vas.get(1);
		// // vas.remove(1);
		//
		// List<Object> li = new ArrayList<Object>();
		// for (int i = 0; i < vas.size(); i++) {
		// li.add(vas.get(i));
		// }
		// li.remove(1);
		//
		// System.err.println("Hello.....");
		// A d = new A();
		// d.dval = new D();
		//
		// con((d == null) ? new A() : new B(), new D());

		// for (int i = 0; i < 5; i++) {
		// SuperClassA dd = new D();
		// for (int j = 0; j < 1; j++) {
		// if (dd == null) {
		// dd = new A();
		// }
		// }
		// }
		// SuperClassA dd = new D();
		//
		// int b = 0;
		// if (b > 0) {
		// new C();
		// }
		//
		// SuperClassA sa = null;
		//
		// if (b > 8) {
		// new D();
		// } else if (b < 7) {
		// new E();
		// sa = new A();
		// if (b > 0) {
		// sa = new C();
		// }
		// sa = (sa == null) ? new A() : sa;
		// } else {
		// new F();
		// ca = null;
		// }
		//
		// A a = new A();
		//
		// while (a != null) {
		// sa = new A();
		// if (b > 0) {
		// sa = new C();
		// }
		// sa = (sa == null) ? new A() : (sa != null) ? new B() : new C();
		// }
		//
		// while (a != null) {
		// sa = new A();
		// }
		//
		// do {
		// sa = new D();
		// } while (a != null);
		//
		// int h = 9;
		// SuperClassA aaa = null;
		// switch (h) {
		// case 9:
		// aaa = new A();
		// break;
		// case 0:
		// aaa = new B();
		// C c = new C();
		// c.aValEx();
		// break;
		// default:
		// break;
		// }
		//
		// SuperClassA gets = getSome();
		//
		// System.err.println("DONE");
	}
}
