package dreamer;

import dv103.NotCompatibleException;
import dv103.QRMatrix;
import dv103.QRing;

public class Matrix<E extends QRing<E>> implements QRMatrix<E> {
    private final QRing<E>[][] elements;
    private final int row;
    private final int column;

    public Matrix(int rows, int columns) {
	if (rows < 1 || columns < 1)
	    throw new IllegalArgumentException("Error: Size");

	row = rows;
	column = columns;
	elements = new QRing[row][column];
    }

    public Matrix(QRing<E>[][] values) {
	row = values.length;
	column = values[0].length;

	if (row < 1 || column < 1)
	    throw new IllegalArgumentException("Error: Size");

	elements = new QRing[row][column];

	for (int i = 0; i < row; i++)
	    for (int j = 0; j < column; j++)
		elements[i][j] = values[i][j];

    }

    @Override
    public QRMatrix<E> getCopy() {
	return new Matrix<E>(elements);
    }

    @Override
    public int rows() {
	return row;
    }

    @Override
    public int columns() {
	return column;
    }

    @Override
    public boolean isSquare() {
	if (row == column)
	    return true;
	else
	    return false;
    }

    @Override
    public E getElementAt(int row, int column) {
	if (row < 0 || this.row <= row || column < 0 || this.column <= column)
	    throw new IndexOutOfBoundsException("Error: row or column");

	return (E) elements[row][column];
    }

    @Override
    public void setElementAt(E elem, int row, int column) {
	if (row < 0 || this.row <= row || column < 0 || this.column <= column)
	    throw new IndexOutOfBoundsException("Error: row or column");

	elements[row][column] = elem;
    }

    @Override
    public QRMatrix<E> getColumn(int column) {
	if (column < 0 || this.column <= column)
	    throw new IndexOutOfBoundsException("Error: Column");

	Matrix<E> matrix = new Matrix<E>(row, 1);

	for (int i = 0; i < row; i++)
	    matrix.elements[i][0] = elements[i][column];

	return matrix;
    }

    @Override
    public QRMatrix<E> getRow(int row) {
	if (row < 0 || this.row <= row)
	    throw new IndexOutOfBoundsException("Error: Row");

	Matrix<E> matrix = new Matrix<E>(1, column);

	for (int i = 0; i < column; i++)
	    matrix.elements[0][i] = elements[row][i];

	return matrix;
    }

    @Override
    public boolean isDiagonal() {
	E value = elements[0][0].getNull();

	for (int i = 0; i < row; i++)
	    for (int j = i; j < column; j++)
		if (i != j)
		    if (!value.equals(elements[i][j])
			    || !value.equals(elements[j][i]))
			return false;

	return true;
    }

    @Override
    public boolean isSymmetric() {
	if (this.equals(getTranspose()))
	    return true;

	return false;
    }

    @Override
    public QRMatrix<E> getTranspose() {
	Matrix<E> matrix = new Matrix<E>(column, row);

	for (int i = 0; i < row; i++)
	    for (int j = 0; j < column; j++)
		matrix.elements[j][i] = elements[i][j];

	return matrix;
    }

    @Override
    public double sparseness() {
	double zero = 0.0;

	E elem = elements[0][0].getNull();

	for (int i = 0; i < row; i++)
	    for (int j = 0; j < column; j++)
		if (elem.equals(elements[i][j]))
		    zero++;

	double size = (row * column);
	return (zero / size);
    }

    @Override
    public QRMatrix<E> plus(QRMatrix<E> e) {
	if (row != e.rows() || column != e.columns())
	    throw new NotCompatibleException("Error: wrong dimensions.");

	Matrix<E> matrix = new Matrix<E>(row, column);

	for (int i = 0; i < row; i++)
	    for (int j = 0; j < column; j++)
		matrix.elements[i][j] = elements[i][j]
			.plus((E) ((Matrix<E>) e).elements[i][j]);

	return matrix;
    }

    @Override
    public QRMatrix<E> mult(QRMatrix<E> e) {
	if (column != e.rows())
	    throw new NotCompatibleException("Error: wrong dimensions.");

	Matrix<E> matrix = new Matrix<E>(row, e.columns());

	for (int i = 0; i < matrix.rows(); i++)
	    for (int j = 0; j < matrix.columns(); j++) {
		E value = elements[0][0].getNull();

		for (int k = 0; k < column; k++)
		    value = value.plus(elements[i][k]
			    .mult((E) ((Matrix<E>) e).elements[k][j]));

		matrix.elements[i][j] = value;
	    }

	return matrix;
    }

    @Override
    public int hashCode() {
	int value = 0;

	for (int i = 0; i < row; i++)
	    for (int j = 0; j < column; j++)
		value += value + elements[i][j].hashCode();

	return value;
    }

    @Override
    public String toString() {
	StringBuilder content = new StringBuilder();
	for (QRing<E>[] row : elements)
	    for (QRing<E> val : row) {
		content.append(val);
		content.append("\t");
	    }
	content.append("\n");

	return content.toString();
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;

	if (o instanceof Matrix)
	    if ((row != ((Matrix<E>) o).row)
		    || (column != ((Matrix<E>) o).column))
		return false;

	if (hashCode() == o.hashCode())
	    return true;

	return false;
    }
}