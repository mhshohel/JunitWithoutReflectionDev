/**
 *
 * @ProjectName StaticCallGraph
 *
 * @PackageName simple
 *
 * @FileName CustomException.java
 * 
 * @FileCreated May 8, 2014
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
package simple;

public class CustomException extends Exception {
	public SomeClass getNewList() {
		System.err.println("Some information");
		if (getValue() > 6) {
			return new SomeClass();
		}
		return null;
	}

	private int getValue() {
		return 8 + 9;
	}

	class SomeClass {
		void noMethod() {

		}
	}
}
