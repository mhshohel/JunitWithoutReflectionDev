/*
 * File: MatrixBenchmark.java
 * Author: Jonas Lundberg
 * Date: 25 feb 2010
 */
package dv103.benchmark;

import dv103.Int;
import dv103.QRMatrix;
import dv103.RMatrix;

/**
 * @author jonasl
 * 
 */
public class MatrixBenchmark {
    private final int SIZE;
    private SquareMatrixGenerator gen = new SquareMatrixGenerator();

    public MatrixBenchmark(int size) {
	SIZE = size;
	System.out.println("Problem Size: " + SIZE);

	/* Used Matrix Implementation */
	RMatrix<Int> m = gen.getRandomMatrix(2);
	System.out
		.println("Implementation: " + m.getClass().getCanonicalName());

	/* JVM Heap Memory Settings */
	long heap_size = Runtime.getRuntime().maxMemory();
	System.out.println("Heap Memory: " + heap_size / 1000000 + " Mbytes");
    }

    /* *****************************************************
     * General Performance ****************************************************
     */
    public double runGeneralPerformance() {
	System.out.println("\n\tGeneral Matrix Performance");
	int repeat = 15;
	int sz = 20 * SIZE;
	System.out.println("\t\tMatrix Sizes: " + sz);

	/* Input Int Values */
	Int[] ints = new Int[2 * sz]; // Random Ints
	for (int i = 0; i < ints.length; i++)
	    ints[i] = gen.randomInt();
	Int[][] sparse = gen.getSparseArray(sz);
	Int[][] symmetric = gen.getSymmetricArray(sz);

	/* Start time measurement */
	gcMem();
	long start = System.currentTimeMillis();

	/* Matrix Creation */
	System.out.print("\t\t\tMatrix Creation ... ");
	RMatrix<Int> sym = null, spa = null, ran = null;
	for (int i = 0; i < repeat; i++) {
	    sym = gen.createMatrix(symmetric); // Symmetric
	    spa = gen.createMatrix(sparse); // Sparse
	    ran = gen.createMatrix(sz); // Empty
	    for (int j = 0; j < ran.size(); j++) {
		for (int k = 0; k < ran.size(); k++)
		    ran.setElementAt(ints[j + k], j, k); // Add elements
	    }
	}
	long stop = System.currentTimeMillis();
	double time = (double) (stop - start) / 1000;
	System.out.println("\t" + mem() + ", " + time + "s");
	gcMem(); // Force garbage collection

	/* Traverse and access matrices */
	start = System.currentTimeMillis();
	System.out.print("\t\t\tTraverse/Access ... ");
	for (int i = 0; i < repeat / 2; i++) {
	    for (int j = 0; j < ran.size(); j++) { // Access elements
		for (int k = 0; k < ran.size(); k++) {
		    sym.getElementAt(j, k);
		    spa.getElementAt(j, k);
		    ran.getElementAt(j, k);
		}
	    }
	    int hash = sym.hashCode(); // Traverses matrix to compute hash
	    hash = spa.hashCode();
	    hash = ran.hashCode();
	}

	//
	String str = sym.toString(); // Traverses matrix to build string
	str = spa.toString();
	str = ran.toString();
	stop = System.currentTimeMillis();
	double tmp = (double) (stop - start) / 1000;
	time += tmp;
	System.out.println("\t" + mem() + ", " + tmp + "s");
	gcMem(); // Force garbage collection

	/* Misc. Ops. */
	start = System.currentTimeMillis();
	System.out.print("\t\t\tMisc. Operators ...");
	for (int i = 0; i < repeat / 2; i++) {
	    applyMisc(sym);
	    applyMisc(spa);
	    applyMisc(ran);
	}
	stop = System.currentTimeMillis();
	tmp = (double) (stop - start) / 1000;
	time += tmp;
	System.out.println("\t" + mem() + ", " + tmp + "s");
	gcMem(); // Force garbage collection

	/* Plus, Minus, NULL, ... */
	start = System.currentTimeMillis();
	System.out.print("\t\t\tPlus, Minus, NULL, ...");
	for (int i = 0; i < repeat / 5; i++) {
	    applyOps(sym);
	    applyOps(spa);
	    applyOps(ran);
	}
	stop = System.currentTimeMillis();
	tmp = (double) (stop - start) / 1000;
	time += tmp;
	System.out.println("\t" + mem() + ", " + tmp + "s");
	gcMem(); // Force garbage collection
	System.out.println("\tTime: " + time);

	return time;
    }

    public boolean applyMisc(RMatrix<Int> m) {
	boolean result = m.isDiagonal();
	result = result || m.isSymmetric();
	RMatrix<Int> cpy = m.getCopy();
	result = result || cpy.equals(m);
	RMatrix<Int> trans = m.getTranspose();

	QRMatrix<Int> a, b, c;
	for (int i = 0; i < cpy.size() / 10; i++) { // Size reduced by 10!!
	    a = cpy.getRow(i); // Add rows
	    b = trans.getRow(i);
	    c = a.plus(b);
	    result = result || c.equals(c);

	    a = cpy.getColumn(i); // Add columns
	    b = trans.getColumn(i);
	    c = a.plus(b);
	    result = result || a.equals(c);
	}
	return result;
    }

    public RMatrix<Int> applyOps(RMatrix<Int> m) {
	RMatrix<Int> one = m.getOne();
	m.minus(one);

	RMatrix<Int> neg = m.negative();
	RMatrix<Int> nil = m.getNull();
	return neg.plus(nil).plus(m);
    }

    /* *****************************************************
     * Multiply Dense Matrices
     * ****************************************************
     */
    public double runMultiplyDense() {
	System.out.println("\n\tMultiply Dense");
	RMatrix<Int>[] matrices = createDenseMultiply();
	gcMem();

	RMatrix<Int> a, b;
	System.out.print("\t\tRuns: ");
	long start = System.currentTimeMillis();
	for (int i = 0; i < matrices.length - 1; i++) {
	    a = matrices[i];
	    b = matrices[i + 1];
	    a.mult(b);
	    System.out.print("  " + i);
	}
	long stop = System.currentTimeMillis();
	double time = (double) (stop - start) / 1000;
	System.out.println("\n\tMem: " + mem());
	System.out.println("\tTime: " + time);
	return time;
    }

    private RMatrix<Int>[] createDenseMultiply() {
	int count = 5;
	int sz = 5 * SIZE;

	/* Add NULL + "count" dense random + ONE */
	System.out.println("\t\tRandom Dense Matrices: SZ = " + sz);
	System.out.print("\t\tBuilding Matrices: NULL");

	RMatrix<Int>[] m = new RMatrix[count + 2];
	for (int i = 1; i < count + 1; i++) {
	    m[i] = gen.getRandomMatrix(sz);
	    System.out.print(" " + i);
	}
	m[0] = m[1].getNull(); // Add null as first element
	m[count + 1] = m[1].getOne(); // Add null as first element
	System.out.println(" ONE \tOK!");
	return m;
    }

    /* *****************************************************
     * Multiply Sparse Matrices
     * ****************************************************
     */
    public double runMultiplySparse() {
	System.out.println("\n\tMultiply Sparse");
	RMatrix<Int>[] matrices = createSparseMultiply();
	gcMem();

	RMatrix<Int> a, b;
	System.out.print("\t\tRuns: ");
	long start = System.currentTimeMillis();
	for (int i = 0; i < matrices.length - 1; i++) {
	    a = matrices[i];
	    b = matrices[i + 1];
	    a.mult(b);
	    System.out.print("  " + i);
	}
	long stop = System.currentTimeMillis();
	double time = (double) (stop - start) / 1000;
	System.out.println("\n\tMem: " + mem());
	System.out.println("\tTime: " + time);
	return time;
    }

    private RMatrix<Int>[] createSparseMultiply() {
	int count = 5;
	int sz = 5 * SIZE;
	double sparse = (double) (sz - 1) / sz;

	/* Add "count" dense random matrices */
	System.out.println("\t\tRandom Sparse Matrices: SZ = " + sz
		+ "  Sparseness: " + sparse);
	System.out.print("\t\tBuilding Matrices: NULL");

	RMatrix<Int>[] m = new RMatrix[count + 2];
	for (int i = 1; i < count + 1; i++) {
	    m[i] = gen.getSparseMatrix(sz);
	    System.out.print(" " + i);
	}
	m[0] = m[1].getNull(); // Add null as first element
	m[count + 1] = m[1].getOne(); // Add null as first element
	System.out.println(" ONE \tOK!");
	return m;
    }

    /*
     * Returns a string (e.g. 27 Mbytes) indicating the amount of memory used by
     * the JVM. It enforces a garbage collection before it measures the memory.
     * return a string containing memory size after garbage collection.
     */
    public String gcMem() {
	Runtime runtime = Runtime.getRuntime();
	long mem_size = runtime.totalMemory() - runtime.freeMemory();
	long prev;
	do {
	    prev = mem_size;
	    runtime.gc();
	    mem_size = runtime.totalMemory() - runtime.freeMemory();
	} while (prev != mem_size);
	return mem_size / 1000000 + " MB";
    }

    public String mem() {
	Runtime runtime = Runtime.getRuntime();
	long mem_size = runtime.totalMemory() - runtime.freeMemory();
	return mem_size / 1000000 + " MB";
    }
}
