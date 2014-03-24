package callgraphstat.superclass;

public class F {
	G h() {
		return i();
	}

	G i() {
		return j();
	}

	G j() {
		return k();
	}

	private G k() {
		G g = new G();
		return g;
	}
}
