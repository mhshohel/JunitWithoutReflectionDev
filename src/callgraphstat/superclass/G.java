package callgraphstat.superclass;

public class G {
	protected int num = 0;
	public H hVal = null;
	static A valueOfB = new B();

	public G(int a) {
		num = a;
	}

	public G(int b, H a) {
		hVal = a;
	}
}
