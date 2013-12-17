package observer;

public class Main {
    public static void main(String[] args) {
	Bag bag = new Bag();

	Adder adder = new Adder(bag);
	Printer printer = new Printer(bag);

	bag.add(new Value(3));
	bag.add(new Value(5));
	System.out.println("Finish!");
    }
}
