package callgraphstat.superclass;

public class Example {
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
		int h = 9;
		int b = 10;
		A abs = null;
		SuperClassA aaaa = null;
		SuperClassA aaa = null;
		switch (h) {
		case 9:
			aaa = new A();
			break;
		case 0:
			aaa = new B();
			C c = new C();
			break;
		case 7:
		case 8:
		case 19:
			if (aaa instanceof SuperClassA) {
				aaa = new C();
			}
			break;
		default:
			break;
		}
		abs = null;
		for (int i = 0; i < 3; i--) {
			if (b == 0) {
				abs = new A();
			}
		}
		abs = new A();
		int i = 0;

		if (i == 0) {
			aaaa = new A();
			aaa = aaaa;
			A aaasa = new A();
			if (i > 0) {
				B bbbb = new B();
			}
		}

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
		int cg = 0;
		E v = e;
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

	}
}
