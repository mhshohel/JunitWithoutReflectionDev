package observer;
import java.util.*;
public class Bag implements Subject {
   private ArrayList valueList = new ArrayList();
   private ObserverArray obsList = new ObserverArray();

 
   public void add( Value v ) {                
      valueList.add( v );                              
      notifyObs();
   }
   

    public ArrayList getValueList() {
	return valueList;
    }                  
  
    public void addObserver( Observer o ) {
      obsList.add( o );
   }

   private void notifyObs() {
      for(int i=0;i<obsList.size();i++ ) {
         Observer o = ( Observer ) obsList.get(i);
         o.update( this );
      }
   }
}
