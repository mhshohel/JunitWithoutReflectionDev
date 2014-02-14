package callgraphstat.superclass;

public class F extends G {

	public F(int ac) {
		super(ac);
		System.out.println(super.num);
		G g = new G(88, 89);
		System.out.println(g.num);
	}

}
