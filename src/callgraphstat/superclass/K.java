/**
 *
 * @ProjectName JUnitWithoutReflection.Second
 *
 * @PackageName callgraphstat.superclass
 *
 * @FileName K.java
 * 
 * @FileCreated Apr 15, 2014
 *
 * @Author MD. SHOHEL SHAMIM
 *
 * @CivicRegistration 19841201-0533
 *
 * MSc. in Software Technology
 *
 * Linnaeus University, Växjö, Sweden
 *
 */
package callgraphstat.superclass;

public class K {
	private K val = null;

	public K getVal() {
		add(this);
		return this.val;
	}

	private void add(K k) {
		this.val = k;
	}
}
