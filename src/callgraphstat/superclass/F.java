package callgraphstat.superclass;

public class F extends G {
	G gval = null;
	H hval = null;

	public F(int ac) {
		super(ac);
		int n = super.num;
		int o = num;
		G gval = new G(null, 6);
		hval = gval.hVal;
	}

}
