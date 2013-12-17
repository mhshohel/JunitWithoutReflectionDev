package observer;
import java.util.*;

public class ObserverArray {
    private Observer[] list = new Observer[10];
    private int size = 0; 

    public void add(Observer o) {
	list[size++] = o ;
    }

    public Object get(int index) {
        return list[index];
    }

    public int size() {
        return size;
    }
}
