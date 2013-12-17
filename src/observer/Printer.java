package observer;

import java.util.ArrayList;

public class Printer implements Observer {
    private Bag bag;
    private int[] values = new int[10];

    public Printer(Bag bag) {
	this.bag = bag;
	bag.addObserver(this);
    }

    public void update(Subject o) {
	if (o == bag) {
	    ArrayList list = bag.getValueList();
	    for (int i = 0; i < list.size(); i++) {
		Value v = (Value) list.get(i);
		values[i] = v.getValue();
	    }
	}
    }

}
