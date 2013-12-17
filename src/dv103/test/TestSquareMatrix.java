/*
 * File: TestSquareMatrix.java
 * Author: Nadeem Abbas
 * Date: 30 jan 2010
 */
package dv103.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dreamer.Matrix;
import dreamer.SquareMatrix;
import dv103.Int;
import dv103.NotCompatibleException;
import dv103.Real;

/**
 * @author Nadeem
 * 
 */

public class TestSquareMatrix {
    private SquareMatrix<Int> one, m1, m2;
    private SquareMatrix<Real> oneR, m1R, m2R;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	one = new SquareMatrix<Int>(1);
	one.setElementAt(new Int(5), 0, 0);
	oneR = new SquareMatrix<Real>(1);
	oneR.setElementAt(new Real(5.0), 0, 0);

	m1 = new SquareMatrix<Int>(3);
	m2 = new SquareMatrix<Int>(3);

	int count = 1;
	for (int i = 0; i < 3; i++)
	    for (int j = 0; j < 3; j++) {
		m1.setElementAt(new Int(count), i, j);
		m2.setElementAt(new Int(count++), i, j);
	    }

	m1R = new SquareMatrix<Real>(3);
	m2R = new SquareMatrix<Real>(3);

	count = 1;
	for (int i = 0; i < 3; i++)
	    for (int j = 0; j < 3; j++) {
		m1R.setElementAt(new Real(count), i, j);
		m2R.setElementAt(new Real(count++), i, j);
	    }
    }

    @After
    public void tearDown() throws Exception {
    }

    /* ***********************************************************
     * IntSquareMatrix tests
     * ***********************************************************
     */

    @Test
    public void testSquareMatrixBasicOps() {
	System.out.println("testInitSize");
	System.out.println("Int matrices used during the test");
	System.out.println(one);
	System.out.println(m1);
	System.out.println(m2);

	System.out.println("Real valued matrices used during the test");
	System.out.println(oneR);
	System.out.println(m1R);
	System.out.println(m2R);

	assertEquals(m1.size(), 3);
	assertEquals(one.size(), 1);
    }

    @Test
    public void testBasicJavaSupport() {
	SquareMatrix<Int> m3 = (SquareMatrix<Int>) m1.getCopy();
	m3.setElementAt(new Int(99), 1, 2);

	Int[][] a4 = { { new Int(1), new Int(2) }, { new Int(4), new Int(5) } };
	SquareMatrix<Int> m4 = new SquareMatrix<Int>(a4);

	/* Test equals() */
	assertTrue(m2R.equals(m1R));
	assertTrue(m1.equals(m2));
	assertFalse(m2.equals(m3));
	assertFalse(m2.equals(m4));
	assertTrue(m4.equals(m4));
    }

    @Test
    public void testConstructors() {
	/* Test SquareMatrix Constructor() */
	boolean excep = false; // Check that constructor throws exception
	try {
	    new SquareMatrix<Int>(0);
	} catch (IllegalArgumentException n1) {
	    excep = true;
	}
	assertTrue(excep);

	excep = false; // Check that constructor throws exception
	try {
	    new SquareMatrix<Real>(new Real[0][3]);
	} catch (IndexOutOfBoundsException n1) {
	    excep = true;
	}
	assertTrue(excep);
    }

    @Test
    public void testSetAndGetElementAt() {
	/* Test get & set */
	boolean excep = false; // Check that accessors throws exception
	try {
	    m1.getElementAt(5, 1);
	} catch (IndexOutOfBoundsException n1) {
	    excep = true;
	}
	assertTrue(excep);

	excep = false; // Check that setter throws exception
	try {
	    m1.setElementAt(new Int(0), 1, 5);
	} catch (IndexOutOfBoundsException n1) {
	    excep = true;
	}
	assertTrue(excep);

	// To check that changes to the element returned by getElement has no
	// affect on the source matrix.
	Int n = m1.getElementAt(1, 0);
	n = n.plus(n);
	assertEquals(8, n.value());
	assertEquals(4, m1.getElementAt(1, 0).value());

	// To check that the element passed to setElement is independent of the
	// changes in the target matrix.
	one.setElementAt(n, 0, 0);
	assertTrue(n.value() == one.getElementAt(0, 0).value());
	one = (SquareMatrix<Int>) one.plus(one);
	assertFalse(n.value() == one.getElementAt(0, 0).value());
	// System.out.println("mm: "+one);

	m1.setElementAt(new Int(99), 1, 1);
	assertEquals(99, m1.getElementAt(1, 1).value());

    }

    @Test
    public void testGetRow() {
	/* Test getRow() */
	Matrix<Int> row = (Matrix<Int>) m1.getRow(0);
	assertEquals(3, row.columns());
	assertEquals(1, row.rows());
	assertEquals(3, row.getElementAt(0, 2).value());
	assertTrue(row.getElementAt(0, 1).equals(m1.getElementAt(0, 1)));
	row.setElementAt(new Int(10), 0, 2); // Check independent results
	assertFalse(row.getElementAt(0, 2).equals(m1.getElementAt(0, 2)));

	// test IndexOutOfBounds
	boolean excep = false;
	try {
	    m1.getRow(-1);
	} catch (IndexOutOfBoundsException n1) {
	    excep = true;
	}
	assertTrue(excep);

	excep = false;
	try {
	    m1.getRow(m1.size() + 3);
	} catch (IndexOutOfBoundsException n1) {
	    excep = true;
	}
	assertTrue(excep);
    }

    @Test
    public void testGetColumn() {
	/* Test getColumn() */
	Matrix<Real> col = (Matrix<Real>) m1R.getColumn(1);
	assertEquals(1, col.columns());
	assertEquals(3, col.rows());
	assertEquals(new Real(8), col.getElementAt(2, 0));
	assertTrue(col.getElementAt(0, 0).equals(m1R.getElementAt(0, 1)));
	col.setElementAt(new Real(90), 0, 0); // Check independent results
	assertFalse(col.getElementAt(0, 0).equals(m1R.getElementAt(0, 2)));

	// Test IndexOutOfBounds
	boolean excep = false;
	try {
	    m1.getColumn(5);
	} catch (IndexOutOfBoundsException n1) {
	    excep = true;
	}
	assertTrue(excep);

	excep = false;
	try {
	    m2R.getColumn(m1.size() + 5);
	} catch (IndexOutOfBoundsException n1) {
	    excep = true;
	}
	assertTrue(excep);
    }

    @Test
    public void testGetCopy() {
	/* Test getCopy() */
	SquareMatrix<Int> copy = (SquareMatrix<Int>) m1.getCopy();
	assertEquals(copy, m1);
	copy = (SquareMatrix<Int>) one.getCopy();
	assertTrue(copy.getElementAt(0, 0).equals(one.getElementAt(0, 0)));

	one.setElementAt(new Int(25), 0, 0); // Check independent results
	assertFalse(copy.getElementAt(0, 0).equals(one.getElementAt(0, 0)));

	copy = (SquareMatrix<Int>) m2.getCopy();
	copy.setElementAt(new Int(10), 0, 1);
	assertFalse(copy.getElementAt(0, 1).equals(m2.getElementAt(0, 1)));
    }

    @Test
    public void testHashCode() {
	SquareMatrix<Int> copy = (SquareMatrix<Int>) m1.getCopy();
	SquareMatrix<Real> copyR = (SquareMatrix<Real>) m2R.getCopy();

	/**
	 * If two objects are equal according to the equals(Object) method, then
	 * calling the hashCode method on each of the two objects must produce
	 * the same integer result.
	 */
	assertTrue(m1.equals(copy));
	assertEquals(m1.hashCode(), copy.hashCode());

	assertTrue(m2R.equals(copyR));
	assertEquals(m2R.hashCode(), copyR.hashCode());

	/**
	 * Whenever it is invoked on the same object more than once during an
	 * execution of a Java application, the hashCode method must
	 * consistently return the same integer
	 */
	int n = m2.hashCode();
	assertTrue(n == m2.hashCode());

    }

    @Test
    public void testTranspose() {
	Int vals[][] = { { new Int(1), new Int(4), new Int(7), },
		{ new Int(2), new Int(5), new Int(8) },
		{ new Int(3), new Int(6), new Int(9) } };
	SquareMatrix<Int> m = new SquareMatrix<Int>(vals);
	SquareMatrix<Int> t = (SquareMatrix<Int>) m1.getTranspose();
	assertEquals(t, m);

	// t is the transpose of m1, so Tij==Mji
	assertEquals(t.getElementAt(1, 2), m1.getElementAt(2, 1));
	m.setElementAt(new Int(200), 1, 0);
	assertFalse(t.equals(m));

	t = (SquareMatrix<Int>) one.getTranspose();
	assertEquals(t, one);
	t.setElementAt(new Int(1050), 0, 0); // To check independence of value
					     // returned by getTranspose
	assertFalse(t.equals(one));

	// to test: tanspose of transpose of a matrix is equal to the original
	// matrix
	assertEquals(m2, m2.getTranspose().getTranspose());

	// transpose(A.B)=tanspose(B).transpose(A)
	SquareMatrix<Real> tm1R = (SquareMatrix<Real>) m1R.getTranspose();
	SquareMatrix<Real> tm2R = (SquareMatrix<Real>) m2R.getTranspose();
	assertEquals(m1R.mult(m2R).getTranspose(), tm2R.mult(tm1R));
    }

    @Test
    public void testIsSymmetric() {
	Int vals[][] = { { new Int(1), new Int(2), new Int(3) },
		{ new Int(2), new Int(5), new Int(6) },
		{ new Int(3), new Int(6), new Int(9) } };
	SquareMatrix<Int> m = new SquareMatrix<Int>(vals);
	assertEquals(m, m.getTranspose());
	assertTrue(m.isSymmetric());
	assertFalse(m2R.isSymmetric());
	assertTrue(oneR.isSymmetric());
    }

    @Test
    public void testIsDiagonal() {
	/* Construct an 3x3 integer SquareMatrix */
	Int[][] vals = new Int[3][3];
	int n = 0;
	for (int i = 0; i < vals.length; i++) { // Constructing input value
						// SquareMatrix
	    Int[] row = vals[i];
	    for (int j = 0; j < row.length; j++)
		row[j] = (i == j) ? new Int(n++) : new Int(0);
	}

	SquareMatrix<Int> m = new SquareMatrix<Int>(vals);
	assertTrue(m.isDiagonal());

	// A diagonal matrix is also a symmetric matrix as well.
	assertEquals(m.isDiagonal(), m.isSymmetric());

	m.setElementAt(new Int(3), 1, 2);
	assertFalse(m.isDiagonal());

	// zero matrix is also a diagonal matrix
	SquareMatrix<Real> mR = (SquareMatrix<Real>) m1R.getNull();
	assertTrue(mR.isDiagonal());

	// one dimensional matrix is always a diagonal matrix
	assertTrue(oneR.isDiagonal());
    }

    @Test
    public void testSparseness() {
	// fully sparse
	SquareMatrix<Int> m = (SquareMatrix<Int>) m1.getNull();
	assertEquals(1, m.sparseness(), 0.005);

	// partially sparse
	m.setElementAt(new Int(2), 1, 0);
	m.setElementAt(new Int(3), 1, 1);
	m.setElementAt(new Int(2), 1, 2);
	assertEquals((double) 6 / 9, m.sparseness(), 0.005);

	// fully dense
	assertEquals(0, m2R.sparseness(), 0.005);

	assertEquals(0, one.sparseness(), 0.005);
    }

    @Test
    public void testGetNull() {
	SquareMatrix<Int> m = (SquareMatrix<Int>) m2.getNull();
	assertEquals(1, m.sparseness(), 0.005);

	SquareMatrix<Real> mR = (SquareMatrix<Real>) oneR.getNull();
	assertEquals(1, mR.sparseness(), 0.005);
    }

    @Test
    public void testGetOne() {
	SquareMatrix<Real> identity = (SquareMatrix<Real>) m1R.getOne();
	assertEquals((double) 6 / 9, identity.sparseness(), 0.005);
	assertEquals(new Real(1), identity.getElementAt(0, 0));
	assertEquals(new Real(1), identity.getElementAt(1, 1));
	assertEquals(new Real(1), identity.getElementAt(2, 2));

    }

    @Test
    public void testPlus() {
	// Check if uncompatible SquareMatrix addition throws exception
	boolean excep = false;
	try {
	    m1R.plus(oneR);
	} catch (NotCompatibleException e) {
	    excep = true;
	}
	assertTrue(excep);

	Int elem1 = m1.getElementAt(1, 0);
	Int elem2 = m2.getElementAt(2, 0);

	SquareMatrix<Int> sum = (SquareMatrix<Int>) m1.plus(m2);
	assertEquals(m1.size(), sum.size());
	assertEquals(sum.getElementAt(2, 2), new Int(18));

	// to test plus operation does not modifies input matrices
	assertEquals(m1.getElementAt(1, 0), elem1);
	assertEquals(m2.getElementAt(2, 0), elem2);

	// on a matrix with 1 row and column
	sum = (SquareMatrix<Int>) one.plus(one);
	assertEquals(2 * one.getElementAt(0, 0).value(), sum.getElementAt(0, 0)
		.value());

	// Test with Real valued Matrices
	Real e1 = m1R.getElementAt(1, 0);
	Real e2 = m2R.getElementAt(2, 0);

	SquareMatrix<Real> sumR = (SquareMatrix<Real>) m1R.plus(m2R);
	assertEquals(m1R.size(), sumR.size());
	assertEquals(sumR.getElementAt(1, 1), new Real(10));

    }

    @Test
    public void testMult() {
	// Check if incompatible SquareMatrix multiplication throws exception
	boolean excep = false;
	try {
	    m1.mult(one);
	} catch (NotCompatibleException e) {
	    excep = true;
	}
	assertTrue(excep);

	Int elem1 = m1.getElementAt(1, 0);
	Int elem2 = m2.getElementAt(2, 0);

	SquareMatrix<Int> mult = (SquareMatrix<Int>) m1.mult(m2);
	assertEquals(3, mult.size());
	assertEquals(30, mult.getElementAt(0, 0).value());

	// to test mult operation does not modifies input matrices
	assertEquals(m1.getElementAt(1, 0), elem1);
	assertEquals(m2.getElementAt(2, 0), elem2);

	// to test: changes in the source does not impacts results earlier
	// returned by mult operation
	int n = mult.getElementAt(1, 1).value();
	m1.setElementAt(new Int(15), 1, 1);
	assertEquals(n, mult.getElementAt(1, 1).value());

	// to test: changes in the product result returned by mult does not
	// impacts source matrices
	n = m2.getElementAt(1, 1).value();
	mult.setElementAt(new Int(15), 1, 1);
	assertEquals(n, m2.getElementAt(1, 1).value());

	// To test associative property: m1(m2.m3)=(m1.m2)m3
	SquareMatrix<Int> m3 = (SquareMatrix<Int>) m2.getTranspose();
	assertEquals(m1.mult(m2.mult(m3)), m1.mult(m2).mult(m3));

	// Matrix multiplication is distributive over matrix addition:
	// m1.(m2+m3)=(m1.m2)+(m1.m3)
	assertEquals(m1.mult(m2.plus(m3)), m1.mult(m2).plus(m1.mult(m3)));

	// one dimension matrices multiplication
	mult = (SquareMatrix<Int>) one.mult(one);
	assertEquals(25, mult.getElementAt(0, 0).value());
	assertEquals(5, one.getElementAt(0, 0).value());

	// test for Real valued matrices
	Real e1 = m1R.getElementAt(1, 0);
	Real e2 = m2R.getElementAt(2, 0);

	SquareMatrix<Real> multR = (SquareMatrix<Real>) m1R.mult(m2R);
	assertEquals(3, multR.size());
	assertEquals(new Real(30.0), multR.getElementAt(0, 0));

	// to test mult operation does not modifies input matrices
	assertEquals(m1R.getElementAt(1, 0), e1);
	assertEquals(m2R.getElementAt(2, 0), e2);

	// To test associative property: m1(m2.m3)=(m1.m2)m3
	SquareMatrix<Real> m3R = (SquareMatrix<Real>) m2R.getTranspose();
	assertEquals(m1R.mult(m2R.mult(m3R)), m1R.mult(m2R).mult(m3R));

	// one dimension matrices multiplication
	multR = (SquareMatrix<Real>) oneR.mult(oneR);
	assertEquals(new Real(25), multR.getElementAt(0, 0));
	assertEquals(new Real(5), oneR.getElementAt(0, 0));

    }

}
