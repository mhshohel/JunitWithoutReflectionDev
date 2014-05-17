/*
 * File: Ring.java
 * Author: Jonas Lundberg
 * Date: 23 jan 2010
 */
package dv103;

/**
 * @author jonasl
 *
 */
public interface Ring<E> extends QRing<E> {
	/**
	 * The ring subtraction operation. It creates a new ring element
	 * without changing the two involved elements.  A NotCompatibleException
	 * is raised if the two elemnts are not compatible.
	 */
	public E minus(E e);
	
	/**
	 * The unary negation operator. It creates a new ring element
	 * without changing <tt>this</tt> element. 
	 */
	public E negative();

}
