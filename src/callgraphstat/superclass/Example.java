/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraphstat.superclass
 *
 * @FileName Example.java
 * 
 * @FileCreated Dec 13, 2013
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

import java.util.ArrayList;
import java.util.List;

public class Example {

	static SuperClassA getVal() {
		return new A();
	}

	public static void main(String[] args) {
		// Stack<A> stack = new Stack<A>();
		// stack.add(new A());
		// stack.peek();
		// stack.get(0);
		// stack.pop();
		// SuperClassA a = new SuperClassA();
		// a.add(new A());

		List<SuperClassA> aList = new ArrayList<SuperClassA>();
		aList.add(new A());
		aList.add(new B());

		SuperClassA aval = aList.get(0);
		aval.aValEx();

	}
}
