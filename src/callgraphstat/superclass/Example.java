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

import callgraphstat.superclass.Enam.EnamCat;

public class Example {
	// // static H valsH = new H();
	// static J valsJ = new J();
	//
	// // static M bba = null;
	// // static V v;
	// // public static M m;
	// // static T tt;
	// //
	// // public static void setVal(int a, V vv, M mm, float f, T t, String sv,
	// // String dn) {
	// // T tv = t;
	// // tt = tv;
	// // v = vv;
	// // m = mm;
	// // int aa = a;
	// // float ff = f;
	// // String svv = sv;
	// // svv = null;
	// // }
	// //
	// // static V vval;
	// //
	// // static void setVval(V v) {
	// // vval = v;
	// // }
	// // static int aa;
	//
	// // static Object getVal(Object val) {
	// // return val;
	// // }
	// static Object getVal() {
	// return valsJ;
	// }
	//
	// //
	// // static void getVal(IInterface val) {
	// //
	// // }
	// //
	// // static int sval = 0;

	static void ma(int a, M m, String v, EnamCat c, int[] ai, M[] mi, int fi) {

	}

	static void av(int s, M o, Object oo, int[] aa, String p) {

	}

	static void av(int aaaa, int a, int b, int[] aa, int[][] bb, M[][] m,
			int c, int d, int[] cc) {

	}

	static void av(M a, M b, M[] aa) {

	}

	static void av(String a, int b, int[][] c, int d, int e, M[][][] f, int g,
			int h, int[][][][] i, int j, M[] k, M l, String v, String vv) {

	}

	static int avs;

	// static M mals[] = new M[10];
	static W av() {
		V m = new V();
		U mm = new U();
		W mmm = new V();
		W[] i = new W[5];
		i[0] = mm;
		i[1] = mm;
		i[2] = mm;
		i[3] = mmm;
		return i[0];
	}

	static int gete() {
		return 6;
	}

	static void mc(IInterface a, IInterface b) {

	}

	public static void main(String[] args) {
		Y y = new Y();
		y.a();
		// IInterface i = new X();
		// IInterface j = new W();
		// mc((X) i, (W) j);
		// M[] m = new M[4];
		// for (int i = 0; i < m.length; i++) {
		// m[i] = new M();
		// }

		// // multiple cast
		// IInterface i = new W();
		// IInterface[] x = new IInterface[4];
		// x[0] = new X();
		// x[1] = new W();
		// ((X) x[0]).voo();

		// ((X) x[0]).eval(i);
		// // x[0].eval(i);
		// x[1].eval((W) i);

		// List<M> ml = new ArrayList<M>();
		// ml.add(new M());
		//
		// M m = ml.get(0);
		// m.foo();

		// Map<M, M> mll = new HashMap<>();
		// Stack<M> sm = new Stack<M>();
		// Collection<M> cm = new HashSet<M>();

		// Map<String, ArrayList<Integer>> map = new LinkedHashMap<String,
		// ArrayList<Integer>>();
		// List<Integer> in = new ArrayList<Integer>();
		// in.add(3);
		// map.put("item", (ArrayList<Integer>) in);
		// int l = in.size();
		// int a = in.get(0);

		// W w = av();
		// int[][][][] a = new int[3][3][4][4];
		// M[][][] b = new M[5][5][];
		// av(null, 3, new int[4][], 5, 5, b, 5, 6, a, 6, new M[1], null, "s",
		// null);

		// int iiiii[] = new int[4];
		// iiiii[0] = 6;

		// V m = new V();
		// U mm = new U();
		// W mmm = new V();
		// W[] i = new W[5];
		// i[0] = mm;
		// i[1] = mm;
		// i[2] = mm;
		// i[3] = mmm;
		//
		// av(i[0]);
		// i[1] = mm;
		// i[2] = mmm;
		// i[3] = mmm;
		// W valss = i[1];
		// W vals = (U) i[1];

		// i[0] = m;// new M();
		// W[][] ii = new W[3][];
		// W[][] iii = new W[3][3];
		// ii[0] = new W[7];
		// ii[0][0] = null;
		// ii[0][1] = m;
		// ii[0][2] = mm;
		// ii[0][3] = mmm;
		// iii[0][0] = (W) mm;
		// W val = ii[0][0];
		// W w = ii[0][0];
		// i[1] = m;
		// i[2] = i[1];

		// M[] ii = i;
		// M j = i[0];
		// av(j, i[0], i);
		// int s = 5;
		// int[][] v = new int[s][];
		// v[0] = new int[3];
		// av(s, 1, v[0][0]);
		// av(s, 1, v[0][0], new int[11], new int[5][], new M[2][3], 4, 5,
		// null);
		// int s = 5;
		// int[] v = new int[s];
		// int[][] sss = new int[4][];
		// M[] mm = new M[3];
		//
		// M[][] mmmm = new M[3][s];
		// av(new Integer(8), new M(), new Object(), null, "");
		// av(String.class);
		// M mals[] = new M[10];
		// for (int i = 0; i < 5; i++) {
		// mals[i] = new M();
		// }
		// av(5);
		// String v = null;
		// String vs = v;
		// int[][] iii = new int[10][];
		// String vv = "";
		// int i[] = new int[5];
		// i[0] = 10;
		// M mal[] = new M[10];
		// mal[0] = null;
		// mals[0] = new M();
		// mals[1] = mals[0];
		// M[] mmm = mals;
		// i[0] = 9;
		// int ii[][] = new int[2][2];
		// ii[0][0] = i[0];
		// int[][][] mm = new int[2][2][2];
		// mm[0][0][0] = ii[0][0];
		// ma(5, null, "SD", EnamCat.REGULAR, i, new M[5], i[0]);
		// M m = new M();
		// M[] arr1 = new M[5];
		// arr1[0] = m;
		// M mm = arr1[0];
		// // arr1[1] = m;
		// ma(5, arr1[0], null);
		// int[] arr = new int[10];
		// // // for (int i = 0; i < 10; i++) {
		// // // arr[i] = i + 1;
		// // // }
		// arr[0] = 1;
		// arr[1] = 2;
		// arr[2] = 3;
		// int val = arr[8];

		// int a = 10;
		// int b = a;
		// sval = 5;
		// new W().pushval(sval);
		// new W().pushval(8);
		// getVal(new W());
		// getVal(new W());
		// getVal(new W());
		// getVal(new X());
		// W w = new W();
		// w.pushval(sval);
		// Object obj = getVal(null);
		// aa = 5;
		// long i = 500000000;
		// int[] arr = new int[50];
		// arr[48] = 5;

		// R.getT();
		// setVval(R.getT());

		// IInterface inter = new W();
		// inter.foo();

		// T tval = R.getT();
		// int n = 5;
		// M m = new M();
		// setVal(n, new V(), m, 5.2f, R.getT(), "Hi", null);
		// U u = new R();
		// v = u.getV();
		// new R();
		// new G(2);
		// A ba = G.valueOfB;
		// ba = new E();
		// G.valueOfB = new D();
		// new G(5, 6);
		//

		// String h = "JO";
		// byte b1 = 0;
		// byte b2 = b1;
		// short s1 = 0;
		// short s2 = s1;
		// int i1 = 0;
		// int i2 = i1;
		// boolean bo1 = false;
		// boolean bo2 = bo1;
		// float f1 = 0.0f;
		// float f2 = f1;
		// float f4 = (float) 0.5;
		// float f3 = f2;
		// double d1 = 0.0d;
		// double d3 = d1;
		// double d2 = 0.0;
		// double d4 = d2;
		// double d5 = 5;
		// double d6 = d5;
		// long l1 = 0L;
		// long l3 = l1;
		// long l2 = 555;
		// long l4 = l2;
		// char c1 = '\u0000';
		// char c2 = c1;
		// String st1 = null;
		// String st3 = st1;
		// String st2 = "Hi";
		// String st4 = st2;
		// int con = (int) d1;

		// Object kks = getVal();
		// M bba = new M();
		// double val = 0.0;
		// int a = 5;
		// F fval = new F(5);
		// N nval = fval.getN(val);
		// M mval = new M();
		// O oval = new O();
		// //
		// M mava = new F(5).getN(val).getM(new O());
		// double d3 = 5;
		// A b = new B(new F(5).getN(val).getM(new O()));
		// P p = new P(bba, 5, "KO", new M(), d3);
		// A bb = new B();
		// b = new B();
		// String vvv = "J";
		// E emm = bb.foos(null, 4, 1.5, null, "J");
		//
		// T nu = emm.valA();
		// int aaaa = 5;
		// double d = 4.5;
		// E em = b.foos(null, a, d, new M(new N(new O(new P(bba)))), vvv);
		// int v = em.val();
		// E ems = b.foos(new E(), 5, new M());
		//
		// // List<K> list = new ArrayList<K>();
		// // list.add(new K());
		// //
		// // ArrayList<K> n = (ArrayList<K>) list;
		// // K kval = new K();
		// // ArrayList<Integer> nnn = new ArrayList();
		// // nnn.add(5);
		// //
		// // ArrayList<K> nn = new ArrayList<K>();
		// // nn.add(kval);
		// // nn.add(kval);
		// // nn.add(kval);
		// // nn.add(kval);
		// // K val1 = nn.get(0);
		// // K val2 = nn.get(1);
		// // K val3 = nn.get(2);
		// //
		// // nn.get(0).foo();
		//
		// // // check multi dimension array
		// // K k = new K();
		// // k.v = 100;
		// // L lVal = new L();
		// // L lvals = lVal;
		// // L[] arr;
		// // arr = new L[100];
		// // arr[0] = new J();
		// // arr[1] = new K();
		// // arr[2] = new J();
		// // arr[3] = k;
		// //
		// // // L[] arr2 = arr;
		// //
		// // Object kkk = (K) arr[3];
		// // L kk = arr[3];
		// // kk.foo();
		// //
		// // IInterface m = new M();
		// // m.foo();
		//
		// // arr[13].foo();
		// // L[] arrOne = new L[1000000];
		// // for (int i = 0; i < 1000000; i++) {
		// // arrOne[i] = new J();
		// // }
		// // arr[1] = new K();
		// // arr[0].foo();
		// // arr[1].foo();
		// // int[] arr = new int[100];
		// // arr[0] = 5;
		// // arr[1] = 10;
		// // new I(5, null);
		// // F f = new F(4);
		// // na = new A();
		// // new C().foo();
		// // B.st();
		// // new B().st();
		// // A v = new B().val();
		// // A n = new B();
		// // n.foo();
		// // A a = null;
		// // new C();
		// // E ee = e;
		// // String s = "sd";
		// // new B(5, new A(), "", new A());
		// // new B(5, new A(), "", new A());
		// // String s = "sd";
		// // // na = new A();
		// // new A();
		// // // new A();
		// // A c1 = new C(
		// // new B(new A(), new D(), new C(), new A(), new A(), new A()), s,
		// // new D(), new Object());
		// // A c2 = new B();
		// // A a2 = new A();
		// // B b1 = new B(new A(), new D(), new C(), c2, c2, a2);
		// // // b1.foos(new B(), c1);
		// //
		// BB b1 = new BB();
		// AA a2 = new AA();
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
	static void f(AA a2) {
		a2.foo();
	}

	static void g(BB b3) {
		BB b4 = b3;
		b4 = new CC();
		b4.foo();
	}

	static A get() {
		return new D();
	}

}
