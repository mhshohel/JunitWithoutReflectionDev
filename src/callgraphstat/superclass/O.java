package callgraphstat.superclass;

public class O implements IInterface {
	P p = null;

	O() {
	}

	public O(P p) {
		// TODO Auto-generated constructor stub
	}

	P getP() {
		this.igp();
		return this.p;
	}

	void igp() {
		p = new P();
	}

	@Override
	public void foo() {
		// TODO Auto-generated method stub
		OOP();
	}

	void OOP() {
	}

	@Override
	public E eval(IInterface e) {
		// TODO Auto-generated method stub
		return null;
	}
}
