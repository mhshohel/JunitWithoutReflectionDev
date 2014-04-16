/**
 * 
 */
package lnu.temp;

/**
 * @author jlnmsi
 *
 */
public class Testing {


	public static void main(String[] args) {
		int a = 7;
		int b = 5;
		int c = a+b*b;				
		System.out.println(c);
		
		System.out.println( sum(10,2) );
	}

	public static int sum(int n, int d) {
		int sum = 0;
		
		for (int i=0;i<n;i+=d) {
			sum += i;
		}
		return sum;
	}
}
