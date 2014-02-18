package callgraphstat.superclass;

public class G {
	protected int num = 0;
	public H hVal = null;
	static A valueOfB = new B();
	static Q Qval1 = new Q();
	private Q Qval2 = Qval1;
	public P pval = null;

	public G(int a) {
		num = a;
		Q qval = new Q();
		P pval = qval.p;
		this.pval = pval;
		this.hVal = new H();
		G gval = H.hValues;
		P pval1 = Qval1.p;
		P pval2 = Qval2.p;
		this.hVal = null;
	}

	public G(int b, int a) {
		// hVal = a;
	}

	public G(int i, Object object) {
		// TODO Auto-generated constructor stub
	}
}
