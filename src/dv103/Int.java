/*
 * File: Int.java
 * Author: Jonas Lundberg
 * Date: 22 jan 2010
 */
package dv103;


/**
 * This class implements the Ring interface and models
 * an element (an integer) of an ordinary integer ring. It will 
 * later be used as an element in integer matrices.
 * 
 * @author jonasl
 *
 */
public class Int implements Ring<Int> {
	private static final Int NULL = new Int(0); 
	private static final Int ONE = new Int(1); 
	private final int val;
	
	public Int(int n) {val = n;}
	public int value() {return val;}
	
	/* Basic Ring Operators */
	@Override
	public Int mult(Int e) { return new Int(val * e.val); }
	@Override
	public Int plus(Int e){ return new Int(val + e.val); }
	@Override
	public Int minus(Int e){ return new Int(val - e.val); }
	@Override
	public Int negative() { return new Int(-val); }

	/* Ring Elements */
	@Override
	public Int getNull() {return NULL;}
	@Override
	public Int getOne() { return ONE;}
	@Override
	public Int getCopy() {return new Int(val); }
	
	/* Basic Java Support */
	@Override 
	public String toString() { return Integer.toString(val);}
	@Override 
	public int hashCode() {return val; }
	@Override 
	public boolean equals(Object o) {
		if (o instanceof Int) {
			Int n = (Int) o;
			return val == n.val;
		}
		return false;
	}
	
}
