package callgraphstat.superclass;

import java.util.ArrayList;
import java.util.List;

public class Example {
	// static A getA() {
	// return new A();
	// }
	//
	// static SuperClassA whatMethod() {
	// int i = 0;
	// if (i == 0) {
	// return new A();
	// } else if (i > 0) {
	// return new B(null);
	// }
	// // getA()
	// return (i < 0) ? getA() : new D(null);
	// }
	//
	// static int addition() {
	// int i = 0;
	// int k = 5;
	// return i + 6 - k;
	// }

	public static void main(String[] args) {
		// A aval = new A();
		// B bval = new B(null);
		List<E> values = new ArrayList<E>();
		// values.add(aval);
		// values.add(bval);
		// values.add(new C(new E()));
		// // values.add(2);
		// // values.add(4);
		//
		// SuperClassA vv = values.get(0);
		// values.get(0).aValEx();

		// E ev = new E();
		// B bv = new B(ev);
		// try {
		// A av = new A().getAVal(bv);
		// } catch (IOException ee) {
		// // TODO Auto-generated catch block
		// ee.printStackTrace();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// int i = 99;
		// SuperClassA val = null;
		//
		// if (i < 0) {
		// val = new A();
		// } else if (i > 99) {
		// val = new A().getB();
		// } else {
		// val = C.getC();
		// }
		//
		// val.aValEx();

		// SuperClassA someValues = whatMethod();
		// int hhh = addition();

		// int b = 0;
		// int c = 9;
		// String dd = "sd";
		// A a = new A();
		// try {
		// A dfdf = new A("sds", new D(), (b > 0) ? new C() : new D(), null,
		// (b == 0 && c > 9 || dd != null) ? new A()
		// : (b > 4) ? new B() : (b < 4) ? new C() : null);
		// } catch (Exception e) {
		// System.err.println(e.getMessage());
		// }
		// SuperClassA kkk = null;
		// if (b != 0) {
		// SuperClassA sd = ((b < 0) ? new B() : new C());
		// if (b > 22) {
		// kkk = new A(sd);
		// }
		// }

		// callgraphstat.externalclasses.A.lol();
		// SuperClassA.lol();
		//

		// int j = 9;
		// SuperClassA dddd = null;
		// if (j < 3) {
		// dddd = (j == 3) ? new A() : new B();
		// }
		// SuperClassA sdsd = dddd;

		// A sdf = new A();
		//
		// B sdsd = sdf.b;
		// SuperClassA kdl = new A();
		// kdl.aValEx();

		// callgraphstat.externalclasses.IInterface ol = new
		// callgraphstat.externalclasses.J();
		// ol.foo();
		//
		// // callgraphstat.externalclasses.B av = new
		// // callgraphstat.externalclasses.A();
		// // av.abc();
		// int bs = 11;
		// if (bs > 0) {
		// ol = new callgraphstat.externalclasses.I();
		// ol.foo();
		// // av = new callgraphstat.externalclasses.C();
		// }
		//
		// IInterfacePart lo = new J();
		// lo.foofoos();
		// if (bs < 99) {
		// lo = null;
		// }
		// lo = null;
		//
		// // av.abc();
		// SuperClassA b = new A();
		// if (bs > 1) {
		// b = new B();
		// b.aValEx();
		// }

		//
		// // SuperClassA s = A.b;
		// int b = 11;
		// if (b < 1) {
		// A.b = new B();
		// } else {
		// A.b = new C();
		// }
		// SuperClassA bol = A.b;
		// A.b = new D();
		// bol = A.b;

		// // int b = 1;
		// SuperClassA a = new A();
		// a.val = new B();
		// SuperClassA c = a.val;
		// c.val = a.val;
		// SuperClassA cc = new B();
		// SuperClassA jjj = new A();
		// jjj = a.val;
		// if (b == 0) {
		// a = new C();
		// } else {
		// a.val = new B();
		// }
		// a.val = new D();
		// SuperClassA ggg = new D();
		// ggg.val = a.val;
		// E e = new E();
		//
		// a.val = new D();
		// SuperClassA bval = null;
		//
		// if (e != null && a != null) {
		// a = new D();
		// new F();
		// bval = (e != new E() || a.fval != new F() && a.fval != null) ? new
		// D()
		// : new C();
		// e = null;
		// }
		// a.val = (a.fval != new F() && a.fval != null) ? new B() : bval;
		// cc.val = a.val;

		// a = new D();
		// a.val = new C();
		// if (b == 0) {
		// a.val = new A();
		// }
		// a.fval = new F();
		// if (b == 10) {
		// a = new C();
		// } else {
		// a = null;
		// }
		//
		// a.val = (a.fval != new F() && a.fval != null) ? new B() : bval;
		//
		// SuperClassA how = a.val;
		// SuperClassA hows = how;
		// SuperClassA bo = null;
		// // bo = (b == 10 && bo instanceof A) ? new C() : (b < 8) ? new D() :
		// // null;
		// if (b == 0) {
		// bo = (b == 10 && bo instanceof A) ? new C() : (b < 8) ? new D()
		// : null;
		// a = bo;
		// if (b > 8) {
		// int u = 9;
		// b = u;
		// } else {
		// a = new D();
		// }
		// } else {
		// a = new C();
		// }
		//
		// a = new D();
		// bo = a;
		// bo = null;
		// int h = 9;
		// // int b = 10;
		// // A abs = null;
		// // SuperClassA aaaa = null;
		// SuperClassA aaa = null;
		// //
		// // // if (aaa == null) {
		// // // if (abs != null) {
		// switch (h) {
		// case 9:
		// aaa = new A();
		// break;
		// case 0:
		// aaa = new B();
		// C c = new C();
		// break;
		// case 7:
		// case 8:
		// case 19:
		// if (aaa instanceof SuperClassA) {
		// aaa = new C();
		// }
		// break;
		// default:
		// break;
		// }
		// }
		// }

		// abs = null;
		// for (int i = 0; i < 3; i--) {
		// if (b == 0) {
		// abs = new A();
		// }
		// }
		// abs = new A();
		// int i = 0;
		// int bs = 10;
		// for (int k = 0; k < bs; k++) {
		// if (aaa == null) {
		// for (int j = 0; j < b; j++) {
		// String s = "SHK";
		// }
		// aaa = new A();
		// } else {
		// aaa = new B();
		// }
		// }
		// aaa = (i == 0) ? new A() : (i > 9) ? new B()
		// : (aaa != null && i != 0) ? new C() : new D();
		// //
		// if (i == 0) {
		// aaaa = new A();
		// aaa = aaaa;
		// A aaasa = new A();
		// if (i > 0) {
		// B bbbb = new B();
		// }
		// }
		//
		// if (aaa instanceof SuperClassA) {
		// aaa = new C();
		// }
		//
		// if (aaa instanceof SuperClassA) {
		// if (aaa != null) {
		// if (i > 0) {
		// if (b == 10) {
		// aaa = null;
		// }
		// }
		// }
		// }
		//
		// if (i == 0) {
		// b = 11;
		// }
		//
		// aaa = (i == 0) ? new A() : (i > 9) ? new B()
		// : (aaa != null && i != 0) ? new C() : new D();
		//
		// for (int k = 0; k < 10; k++) {
		// if (aaa == null) {
		// for (int j = 0; j < b; j++) {
		// String s = "SHK";
		// }
		// aaa = new A();
		// } else {
		// aaa = new B();
		// }
		// }
		//
		// E e = new E();
		// int cg = 0;
		// E v = e;
		// SuperClassA dataAB = (i == 0) ? new A() : (e == null) ? new B()
		// : (e != null && i != 0) ? new C() : new D();
		// if (i == 0) {
		// A aa = new A();
		// if (i > 0) {
		// B nb = new B();
		// } else {
		// C c = new C();
		// }
		// new D();
		// SuperClassA dataABn = (i == 0) ? new A() : null;
		// }
		// SuperClassA a = null;
		// SuperClassA sa = null;
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

	}
}
