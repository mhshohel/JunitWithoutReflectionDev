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
	// static H valsH = new H();
	// static J valsJ = new J();
	// static M bba = null;

	public static void main(String[] args) {
		// new R();
		// new G(2);
		M bba = new M();
		// A ba = G.valueOfB;
		// ba = new E();
		// G.valueOfB = new D();
		// new G(5, 6);
		//
		double val = 0.0;
		int a = 5;
		// F fval = new F(5);
		// N nval = fval.getN(val);
		// M mval = new M();
		// O oval = new O();

		// M fava = new F(5).getN(val).getM(new O());

		A b = new B(new F(5).getN(val).getM(new O()));
		E em = b.foos(null, 4, 1.5, new M(new N(new O(new P(bba)))));
		// int v = em.val();
		// E em = b.foos(new E(), 5, new M());

		// List<K> list = new ArrayList<K>();
		// list.add(new K());
		//
		// ArrayList<K> n = (ArrayList<K>) list;
		// K kval = new K();
		// ArrayList<Integer> nnn = new ArrayList();
		// nnn.add(5);
		//
		// ArrayList<K> nn = new ArrayList<K>();
		// nn.add(kval);
		// nn.add(kval);
		// nn.add(kval);
		// nn.add(kval);
		// K val1 = nn.get(0);
		// K val2 = nn.get(1);
		// K val3 = nn.get(2);
		//
		// nn.get(0).foo();

		// // check multi dimension array
		// K k = new K();
		// k.v = 100;
		// L lVal = new L();
		// L lvals = lVal;
		// L[] arr;
		// arr = new L[100];
		// arr[0] = new J();
		// arr[1] = new K();
		// arr[2] = new J();
		// arr[3] = k;
		//
		// // L[] arr2 = arr;
		//
		// Object kkk = (K) arr[3];
		// L kk = arr[3];
		// kk.foo();
		//
		// IInterface m = new M();
		// m.foo();

		// arr[13].foo();
		// L[] arrOne = new L[1000000];
		// for (int i = 0; i < 1000000; i++) {
		// arrOne[i] = new J();
		// }
		// arr[1] = new K();
		// arr[0].foo();
		// arr[1].foo();
		// int[] arr = new int[100];
		// arr[0] = 5;
		// arr[1] = 10;
		// new I(5, null);
		// F f = new F(4);
		// na = new A();
		// new C().foo();
		// B.st();
		// new B().st();
		// A v = new B().val();
		// A n = new B();
		// n.foo();
		// A a = null;
		// new C();
		// E ee = e;
		// String s = "sd";
		// new B(5, new A(), "", new A());
		// new B(5, new A(), "", new A());
		// String s = "sd";
		// // na = new A();
		// new A();
		// // new A();
		// A c1 = new C(
		// new B(new A(), new D(), new C(), new A(), new A(), new A()), s,
		// new D(), new Object());
		// A c2 = new B();
		// A a2 = new A();
		// B b1 = new B(new A(), new D(), new C(), c2, c2, a2);
		// // b1.foos(new B(), c1);
		//
		// // A a3 = new C();
		// f(b1);
		// g(b1);
		// a3.fos(b1);
		// a = new C();
		// a = b1;
		// A d = get();
	}

	// // static A na = null;
	// // static int i;
	// int v;
	// private A num;
	// public A bs = new B();
	//
	// // static A jn = new C();
	//
	static void f(A a2) {
		a2.foo();
	}

	static void g(B b3) {
		B b4 = b3;
		b4 = new C();
		b4.foo();
	}

	static A get() {
		return new D();
	}

}
