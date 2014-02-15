package callgraphstat.superclass;

public class I {
	static int a = 0;
	static G gval = new G(5);
	static G hval = H.hValues;
	H k = new H();

	I(int aa, H hvals) {
		a = aa;
		// I.hval = hvals;
	}
}
