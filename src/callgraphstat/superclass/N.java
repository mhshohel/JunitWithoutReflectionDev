package callgraphstat.superclass;

import java.util.Iterator;

public class N implements Iterator<Integer> {
	static M valM = new M();

	public N(O o) {
	}

	public N() {
		// TODO Auto-generated constructor stub
	}

	M getM(O o) {
		O oo = o;
		return valM;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}
}
