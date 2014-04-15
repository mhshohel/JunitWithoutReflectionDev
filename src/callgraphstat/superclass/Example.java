package callgraphstat.superclass;

public class Example {
	// public static void notifier() {
	// for (int i = 0; i < superList.size(); i++) {
	// IInterface io = (IInterface) superList.get(i);
	// io.foo();
	// }
	// }
	//
	// static ArrayList superList = new ArrayList();

	public static void main(String[] args) {
		// A a = new A();
		// B b = new B(null);
		//
		// superList.add(a);
		// superList.add(b);
		//
		// notifier();

		// // Observer[] list = new Observer[10];
		// // list[0] = null;
		// // ObserverArray obsList = new ObserverArray();
		//
		Bag bag = new Bag();
		Adder adder = new Adder(bag);
		Printer printer = new Printer(bag);
		bag.add(new Value(3));
		bag.add(new Value(5));
		System.out.println("Finish!");
	}
}