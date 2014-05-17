/*
 * File: MatrixGenerator.java
 * Author: Jonas Lundberg
 * Date: 25 feb 2010
 */
package dv103.benchmark;

import java.util.Arrays;
import java.util.Random;

import dreamer.SquareMatrix;
import dv103.Int;

/**
 * @author jonasl
 * 
 */

public class SquareMatrixGenerator {
    private Int NULL = new Int(7).getNull();
    private int min_entry = -99;
    private int max_entry = 99;
    private Random random = new Random();

    public SquareMatrixGenerator() {
    }

    /* Matrix creation */
    public SquareMatrix<Int> createMatrix(Int[][] m) {
	return new SquareMatrix<Int>(m);
    }

    public SquareMatrix<Int> createMatrix(int size) {
	return new SquareMatrix<Int>(size);
    }

    /* Random square matrices and input arrays */
    public SquareMatrix<Int> getRandomMatrix(int size) {
	Int[][] m = getRandomArray(size);
	return new SquareMatrix<Int>(m);
    }

    public Int[][] getRandomArray(int size) {
	Int[][] m = new Int[size][size];
	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		m[i][j] = randomInt();
	    }
	}
	return m;
    }

    /* Random symmetric matrices and input arrays */
    public SquareMatrix<Int> getSymmetricMatrix(int size) {
	Int[][] m = getSymmetricArray(size);
	return new SquareMatrix<Int>(m);
    }

    public Int[][] getSymmetricArray(int size) {
	Int[][] m = new Int[size][size];
	for (int i = 0; i < size; i++) {
	    for (int j = i; j < size; j++) {
		Int n = randomInt();
		if (i == j) // Diagonal elements
		    m[i][j] = n;
		else {
		    m[i][j] = n;
		    m[j][i] = n;
		}
	    }
	}
	return m;
    }

    /* Random sparse matrices and input arrays */
    public SquareMatrix<Int> getSparseMatrix(int size) {
	Int[][] m = getSparseArray(size);
	return new SquareMatrix<Int>(m);
    }

    public Int[][] getSparseArray(int size) {
	Int[][] m = new Int[size][size];
	for (int i = 0; i < size; i++)
	    // Create NULL matrix
	    Arrays.fill(m[i], NULL);

	for (int i = 0; i < size; i++) { // Add random elements
	    int x = random.nextInt(size);
	    int y = random.nextInt(size);
	    m[x][y] = randomInt();
	}
	return m;
    }

    /* Random Int Generator */
    public Int randomInt() {
	int r = random.nextInt(max_entry - min_entry + 1) + min_entry;
	return new Int(r);
    }
}
