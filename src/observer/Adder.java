package observer;
import java.util.*;

public class Adder extends Object implements Observer {
   private Bag bag;
   private int sum;

   public Adder( Bag bag ) {
      this.bag = bag;               
      bag.addObserver( this );
   }

   public void update( Subject o ) {
         int counter = 0;
         ArrayList list = bag.getValueList();
         for(int i=0;i<list.size();i++) {
            Value val = ( Value ) list.get(i);
            counter+=val.getValue();
         }
         sum = counter;
      }
}
