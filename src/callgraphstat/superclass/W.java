package callgraphstat.superclass;

public class W implements IInterface {
	static V val = null;
	int ival = 5;
	static int iiv = 10;

	void set(int o) {
		ival = o;
	}

	@Override
	public void foo() {
		// TODO Auto-generated method stub
		val = new V();
	}

	@Override
	public E eval() {
		// TODO Auto-generated method stub
		return null;
	}

	int wval;

	void pushval(int e) {
		this.wval = e;
		int v = this.wval;
	}

}
