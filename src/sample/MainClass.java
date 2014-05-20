/**
 *
 * @ProjectName StaticCallGraph
 *
 * @PackageName sample
 *
 * @FileName MainClass.java
 * 
 * @FileCreated May 19, 2014
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
package sample;

import java.util.ArrayList;
import java.util.List;

public class MainClass extends B implements IInterface, ITInterface {

	public static void main(String[] args) {
		D dval = new D();
		dval.gn();
		Object o = new Object();
		List as = new ArrayList<>();
		A a = new A();
		a.foo();
	}

	@Override
	public void fac() {
		// TODO Auto-generated method stub

	}

	@Override
	public String too() {
		// TODO Auto-generated method stub
		return null;
	}

}
