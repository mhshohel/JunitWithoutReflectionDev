/*
 * File: Real.java
 * Author: Nadeem Abbas
 * Date: 15 March 2011
 */
package dv103;


/**
 * This class implements the Ring interface and models
 * an element (a double) of an ordinary integer ring, similar to the class Int. It will 
 * later be used as an element in matrices.
 * 
 * @author nadeem
 */
public class Real implements Ring<Real> {
	private static final Real NULL = new Real(0.0); 
	private static final Real ONE = new Real(1); 
	private final double val;
	
	public Real(double n) {val = n;}
	public double value() {return val;}
	
	/* Basic Ring Operators */
	@Override
	public Real mult(Real e) { return new Real(val * e.val); }
	@Override
	public Real plus(Real e){ return new Real(val + e.val); }
	@Override
	public Real minus(Real e){ return new Real(val - e.val); }
	@Override
	public Real negative() { return new Real(-val); }

	/* Ring Elements */
	@Override
	public Real getNull() {return NULL;}
	@Override
	public Real getOne() { return ONE;}
	@Override
	public Real getCopy() {return new Real(val); }
	
	/* Basic Java Support */
	@Override 
	public String toString() { return Double.toString(val);}
	@Override 
	public int hashCode() {return (int)val; }
	@Override 
	public boolean equals(Object o) {
		if (o instanceof Real) {
			Real n = (Real) o;
			return val == n.val;
		}
		return false;
	}
	
}
