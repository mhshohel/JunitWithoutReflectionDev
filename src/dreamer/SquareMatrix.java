package dreamer;

import java.util.Arrays;

import dv103.NotCompatibleException;
import dv103.QRMatrix;
import dv103.QRing;
import dv103.RMatrix;
import dv103.Ring;

public class SquareMatrix<E extends Ring<E>> implements RMatrix<E> {
    private final int size;
    private final Ring<E>[][] elements;
    private boolean isNull;

    public SquareMatrix(int elem) {
	if (elem < 1)
	    throw new IllegalArgumentException("Error: size");

	elements = new Ring[elem][elem];
	size = elem;
	isNull = false;
    }

    public SquareMatrix(Ring[][] elems) {
	size = elems.length;

	if (size < 1)
	    throw new IndexOutOfBoundsException("Error: size");

	elements = new Ring[size][size];

	for (int i = 0; i < size; i++)
	    System.arraycopy(elems[i], 0, elements[i], 0, size);

	isNull = isNull();
    }

    @Override
    public RMatrix<E> getCopy() {
	return new SquareMatrix<E>(elements);
    }

    @Override
    public int size() {
	return size;
    }

    @Override
    public E getElementAt(int row, int column) {
	if (row > size || row < 0)
	    throw new IndexOutOfBoundsException("Error: size");
	else if (column > size || column < 0)
	    throw new IndexOutOfBoundsException("Error: size");

	E element = (E) elements[row][column];

	return element;
    }

    @Override
    public void setElementAt(E elem, int row, int column) {
	if (row > size || row < 0)
	    throw new IndexOutOfBoundsException("Error: size");
	else if (column > size || column < 0)
	    throw new IndexOutOfBoundsException("Error: size");

	elements[row][column] = elem;
	isNull = false;
    }

    @Override
    public QRMatrix<E> getColumn(int column) {
	if (column < 0 || size <= column)
	    throw new IndexOutOfBoundsException("Error: size");

	QRing<E>[][] matrix = new QRing[size][1];

	for (int i = 0; i < size; i++)
	    matrix[i][0] = elements[i][column];

	return new Matrix<E>(matrix);
    }

    @Override
    public QRMatrix<E> getRow(int row) {
	if (row < 0 || size <= row)
	    throw new IndexOutOfBoundsException("Error: size");

	QRing<E>[][] matrix = new QRing[1][size];

	matrix[0] = elements[row];

	return new Matrix<E>(matrix);
    }

    @Override
    public RMatrix<E> getNull() {
	SquareMatrix<E> elem = new SquareMatrix<E>(size);

	E nullValue = elements[0][0].getNull();

	for (int i = 0; i < size; i++)
	    Arrays.fill(elem.elements[i], nullValue);

	isNull = true;

	return (RMatrix<E>) elem;
    }

    @Override
    public RMatrix<E> getOne() {
	SquareMatrix<E> elem = (SquareMatrix<E>) this.getNull();

	E oneValue = elements[0][0].getOne();

	for (int i = 0; i < size; i++)
	    elem.setElementAt(oneValue, i, i);

	return (RMatrix<E>) elem;
    }

    @Override
    public RMatrix<E> getTranspose() {
	SquareMatrix<E> matrix = new SquareMatrix<E>(size);
	for (int i = 0; i < size; i++)
	    for (int j = 0; j < size; j++)
		matrix.elements[j][i] = elements[i][j];

	return matrix;
    }

    @Override
    public boolean isDiagonal() {
	for (int i = 0; i < size; i++)
	    for (int j = 0; j < size; j++)
		if (i != j)
		    if (!elements[i][j].equals(elements[i][j].getNull()))
			return false;

	return true;
    }

    @Override
    public boolean isSymmetric() {
	for (int i = 0; i < size; i++) {
	    for (int j = i; j < size; j++) {
		if (i != j) {
		    if (!(elements[i][j]).equals(elements[j][i])) {
			return false;
		    }
		}
	    }
	}

	return true;
    }

    public boolean isNull() {
	E zeroElement = elements[0][0].getNull();

	for (int i = 0; i < size; i++)
	    for (int j = 0; j < size; j++)
		if (!zeroElement.getNull().equals(elements[i][j]))
		    return false;

	return true;
    }

    @Override
    public RMatrix<E> negative() {
	SquareMatrix<E> matrix = new SquareMatrix(size);

	for (int i = 0; i < size; i++)
	    for (int j = 0; j < size; j++)
		matrix.elements[i][j] = elements[i][j].negative();

	return matrix;
    }

    @Override
    public double sparseness() {
	double zero = 0.0;
	E zeroElement = elements[0][0].getNull();

	for (int i = 0; i < size; i++)
	    for (int j = 0; j < size; j++)
		if (zeroElement.getNull().equals(elements[i][j]))
		    zero++;

	zero = zero / (double) (size * size);
	return zero;
    }

    @Override
    public RMatrix<E> plus(RMatrix<E> e) {
	if (size != e.size())
	    throw new NotCompatibleException("Error: size");

	if (this.isNull)
	    return e;
	else if (((SquareMatrix<E>) e).isNull)
	    return this;

	SquareMatrix<E> matrix = new SquareMatrix<E>(size);

	for (int i = 0; i < size; i++)
	    for (int j = 0; j < size; j++)
		matrix.elements[i][j] = elements[i][j]
			.plus((E) ((SquareMatrix<E>) e).elements[i][j]);

	return matrix;
    }

    @Override
    public RMatrix<E> minus(RMatrix<E> e) {
	if (size != e.size())
	    throw new NotCompatibleException("Error: size");

	SquareMatrix<E> matrix = new SquareMatrix<E>(size);

	for (int i = 0; i < size; i++)
	    for (int j = 0; j < size; j++)
		matrix.elements[i][j] = elements[i][j]
			.minus((E) ((SquareMatrix<E>) e).elements[i][j]);

	return matrix;
    }

    @Override
    public RMatrix<E> mult(RMatrix<E> e) {
	if (size != e.size())
	    throw new NotCompatibleException("Error: size");

	if (this.isNull)
	    return this;
	else if (((SquareMatrix<E>) e).isNull)
	    return e;

	if (this.sparseness() > .5 || e.sparseness() > .5) {
	    return sparseMult(e);
	}

	SquareMatrix<E> matrix = new SquareMatrix<E>(size);

	E nullValue = elements[0][0].getNull();

	Ring[] column = new Ring[size];
	for (int j = 0; j < size; j++) {
	    for (int k = 0; k < size; k++)
		column[k] = elements[k][j];

	    for (int i = 0; i < size; i++) {
		final Ring[] row = ((SquareMatrix<E>) e).elements[i];

		E value = elements[0][0].getNull();
		for (int k = 0; k < size; k++) {
		    value = value.plus((E) row[k].mult(column[k]));
		}
		matrix.setElementAt(value, i, j);
	    }
	}

	return matrix;
    }

    private SquareMatrix<E> sparseMult(RMatrix<E> e) {
	SquareMatrix<E> matrix = (SquareMatrix<E>) this.getNull();
	int[] row = new int[size];
	int[] column = new int[size];

	E nullValue = elements[0][0].getNull();

	int counter = 0;
	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		if (!((SquareMatrix<E>) e).elements[i][j].equals(nullValue)) {
		    row[counter] = i;
		    column[counter++] = j;
		}
	    }
	}

	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		if (!elements[i][j].equals(nullValue)) {
		    for (int k = 0; k < row.length; k++) {
			if (k > counter) {
			    break;
			}

			if (row[k] == j) {
			    matrix.elements[i][column[k]] = matrix.elements[i][column[k]]
				    .plus(elements[i][j]
					    .mult((E) ((SquareMatrix<E>) e).elements[j][column[k]]));
			}
		    }
		}
	    }
	}

	return matrix;
    }

    @Override
    public int hashCode() {
	int hash = 0;

	for (int i = 0; i < size; i++)
	    for (int j = 0; j < size; j++)
		hash += hash + elements[i][j].hashCode();

	return hash;
    }

    @Override
    public String toString() {
	StringBuilder string = new StringBuilder();

	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		string.append(elements[i][j]);
		string.append("\t");
	    }
	    string.append("\n");
	}

	return string.toString();
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;

	if (o instanceof SquareMatrix) {
	    if (this.size != ((RMatrix<E>) o).size())
		return false;

	    for (int i = 0; i < size; i++)
		for (int j = 0; j < size; j++)
		    if (!elements[i][j].equals(((RMatrix<E>) o).getElementAt(i,
			    j)))
			return false;
	}

	return true;
    }
}