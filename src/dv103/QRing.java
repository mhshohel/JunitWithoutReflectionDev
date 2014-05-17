/*
 * File: QRing.java
 * Author: Jonas Lundberg
 * Date: 23 jan 2010
 */
package dv103;

/**
 * This is an interface describing a generic quasi-ring element. In addition 
 * to the usual quasi-ring operations (mult, plus,null,one) it also
 * encourages any implementation to provide basic Java support (getCopy(),
 * toString(), hashCode(), and equals().
 * 
 * @author jonasl
 *
 */
public interface QRing<E> {
	/* Basic Ring Operators */
	
	/**
	 * The binary multiplication operation. It creates a new quasi-ring element
	 * without changing the two involved elements. A NotCompatibleException
	 * is raised if the two elemnts are not compatible.
	 */

	
	public E mult(E e);
	
	/**
	 * The ring addition operation. It creates a new quasi-ring element
	 * without changing the two involved elements.  A NotCompatibleException
	 * is raised if the two elemnts are not compatible.
	 */
	public E plus(E e);
	
	/**
	 * Returns the quasi-ring null element.
	 *
	 */
	public E getNull();
	
	/**
	 * Returns the quasi-ring one element.
	 *
	 */
	public E getOne();
	
	/* Basic Java Support */
	/**
	 * Returns a copy (same quasi-ring value) of this element.
	 */
	public E getCopy();
	
	
	/**
	 * Returns a (nice looking) string representation of this element
	 * suitable for diagnostic print-outs.
	 */
	public String toString();
	
	/**
	 * Returns a hash value for this element. Remember, two elements
	 * representing the same value should have identical hash values. 
	 */
	public int hashCode();
	/**
	 * Returns <tt>true</tt> if the two elements
	 * represent the same value. 
	 */
	public boolean equals(Object o);

}
