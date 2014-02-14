package callgraphstat.superclass;

public class G {
	protected int num = 0;
	public H hVal = null;

	public G(int a) {
		num = a;
	}

	public G(H a, int b) {
		hVal = a;
	}
}
