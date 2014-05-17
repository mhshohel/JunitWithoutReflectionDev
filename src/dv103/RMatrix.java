/*
 * File: RMatrix.java
 * Author: Jonas Lundberg
 * Date: 12 feb 2010
 */

package dv103;

/**
 * A two dimensional <em>square</em> matrix interface where each element is a 
 * ring member. Notice, each matrix (of size NxN) is in itself an element of a 
 * ring over all NxN-matrices.
 * <p/>
 * 
 
 * Indexing: We follow the standard Java approach and let all
 * indices start at 0. Hence, the index of the element in the top-left
 * corner is (0,0).
 * <p/>
 * 
 * Methods that are returning matrices (e.g. getCopy(), mult() ) should 
 * return matrices that are independent of the input matrices. That is, they 
 * should not share any interior parts (except possibly the entries) and further 
 * updates of the input matrices should not affect the result matrix (and vice versa).
 *  
 * @author jonasl
 *
 */

public interface RMatrix<E extends Ring<E>> extends Ring<RMatrix<E>> {

	/**
	 * Returns the number of rows (or columns) in <tt>this</tt> square matrix.
	 */
	public int size();
	
	
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
	 * Given an NxN square matrix, this method returns a column (an Nx1 matrix). 
	 * Exception (java.lang.IndexOutOfBoundsException)
	 * is thrown if the given column number is out of range.
	 */
	public QRMatrix<E> getColumn(int column);	
	
	/**
	 * Given an NxN square matrix, this method returns a row (an 1xN matrix). 
	 * Exception (java.lang.IndexOutOfBoundsException)
	 * is thrown if the given column number is out of range.
	 */
	public QRMatrix<E> getRow(int row);
	
	/**
	 * Given an NxN square matrix Aij, this method returns the so-called
	 * transpose (another NxN matrix B where Bij = Aji). 
	 */
	public RMatrix<E> getTranspose();
	
	
	/**
	 * Returns <tt>true</tt> if this square matrix is a <em>diagonal</em>
	 * matrix. Otherwise <tt>false</tt>. 
	 */
	public boolean isDiagonal();
	
	/**
	 * Returns <tt>true</tt> if this matrix is a <em>symmetric</em>
	 * matrix. Otherwise <tt>false</tt>. 
	 */
	public boolean isSymmetric();
	
    /*
     * Additional multiplication algorithms
     */
	
	
	/* Basic Java Support */
	
	/**
	 * Returns a copy (same size and entries) of this matrix.
	 */
	public RMatrix<E> getCopy();
	/**
	 * Returns a (nice looking) string representation of this matrix
	 * suitable for diagnostic print-outs. The return String object should
	 * represent the entire Matrix, not just few of the entries there.
	 */
	public String toString();
	/**
	 * Returns a hash value for this matrix. Remember, two matrices
	 * that are equal (accorrding to equals())  should have identical hash values.
	 * Note: Hash value for a Matrix should be computed based on all the member elements. 
	 */
	public int hashCode();
	/**
	 * Returns <tt>true</tt> if the two matrices have the same size
	 * and the same entries (defined by Ring.equals()). 
	 */
	public boolean equals(Object o);
}
