/*
 * File: QRMatrix.java
 * Author: Jonas Lundberg
 * Date: 23 jan 2010
 */
package dv103;

/**
 * A two dimensional matrix interface where each element is a quasi-ring member. 
 * <p/>
 * Indexing: We follow the standard Java approach and let all
 * indices start at 0. Hence, the index of the element in the top-left
 * corner is (0,0).
 *  
 * @author jonasl
 *
 */


public interface QRMatrix<E extends QRing<E>>  {
	
	/**
	 * Returns the number of rows in <tt>this</tt> matrix.
	 */
	public int rows();
	
	/**
	 * Returns the number of columns in <tt>this</tt> matrix.
	 */
	public int columns();
	
	/**
	 * Returns the sparseness (No. of zero elements/ No. of elements) of <tt>this</tt> matrix.
	 * 
	 * @return a double in the range 0.0 to 1.0 indicating the sparseness of <tt>this</tt> matrix.
	 */
	public double sparseness();

	/**
	 * Returns the element in position (row,column). Exception (java.lang.IndexOutOfBoundsException)
	 * is thrown if any of the indices are out of range.
	 */
	public E getElementAt(int row, int column);
	
	/**
	 * Insert a new element in position (row,column). Exception (java.lang.IndexOutOfBoundsException)
	 * is thrown if any of the indices are out of range.
	 */
	public void setElementAt(E elem, int row, int column);
	
	/**
	 * Given an MxN matrix, this method returns a column (an Mx1 matrix). 
	 * Exception (java.lang.IndexOutOfBoundsException)
	 * is thrown if the given column number is out of range.
	 */
	public QRMatrix<E> getColumn(int column);	
	
	/**
	 * Given an MxN matrix, this method returns a row (an 1xN matrix). 
	 * Exception (java.lang.IndexOutOfBoundsException)
	 * is thrown if the given column number is out of range.
	 */
	public QRMatrix<E> getRow(int row);
	
	/**
	 * Given an MxN matrix, this method returns the so-called
	 * transpose (an NxM matrix). 
	 */
	public QRMatrix<E> getTranspose();
	
	/**
	 * Returns <tt>true</tt> if this matrix is a <em>square</em>
	 * matrix. Otherwise <tt>false</tt>. 
	 */
	public boolean isSquare();
	
	/**
	 * Returns <tt>true</tt> if this matrix is a <em>diagonal</em>
	 * matrix. Otherwise <tt>false</tt>. 
	 */
	public boolean isDiagonal();
	
	/**
	 * Returns <tt>true</tt> if this matrix is a <em>symmetric</em>
	 * matrix. Otherwise <tt>false</tt>. 
	 */
	public boolean isSymmetric();
	
	/**
	 * The matrix multiplication operation. It creates a new matrix
	 * without changing the two involved matrices. A NotCompatibleException
	 * is raised if the two matrices are not compatible.
	 */
	public QRMatrix<E> mult(QRMatrix<E> e);
	
	/**
	 * The matrix addition operation. It creates a new matrix
	 * without changing the two involved matrices. A NotCompatibleException
	 * is raised if the two matrices are not compatible.
	 */
	public QRMatrix<E> plus(QRMatrix<E> e);
	
	/* Basic Java Support */
	
	/**
	 * Returns a copy (same size and quasi-ring values) of this matrix.
	 */
	public QRMatrix<E> getCopy();
	/**
	 * Returns a (nice looking) string representation of this matrix
	 * suitable for diagnostic print-outs.
	 */
	public String toString();
	/**
	 * Returns a hash value for this matrix. Remember, two matrices
	 * that are equal (accorrding to equals()) should have identical hash values. 
	 */
	public int hashCode();
	/**
	 * Returns <tt>true</tt> if the two matrices have the same size
	 * and the same entries (defined by QRing.equals()). 
	 */
	public boolean equals(Object o);
}
