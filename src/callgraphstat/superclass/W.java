package callgraphstat.superclass;

public class W implements IInterface {
	static V val = null;

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
