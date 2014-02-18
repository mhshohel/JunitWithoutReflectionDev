package callgraphstat.superclass;

public class R extends U {
	T tval = null;
	S sval = null;

	public R() {
		sval = new S();
		// U v = new U();
		// V vvals = v.vval;
		T tval = new T();
		this.tval = tval;// new T();
		sval.tval = tval;
		tval = sval.tval;
		// T tv = sval.tval;
		// T tval1 = sval.tval;
		// sval.tval;
		// this.tval = tval1;
	}
}
