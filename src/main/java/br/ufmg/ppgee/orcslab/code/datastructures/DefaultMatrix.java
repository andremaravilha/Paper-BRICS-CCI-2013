package br.ufmg.ppgee.orcslab.code.datastructures;


import br.ufmg.ppgee.orcslab.code.utils.Checks;

/**
 * This class implements a matrix of fixed dimensions.
 * 
 * @author	Andre L. Maravilha
 * @version	2013.04.03
 * 
 * @param	<T>
 * 			the type of elements stored in the matrix
 * 
 */
public class DefaultMatrix<T> implements Matrix<T> {
	
	private T[][] elements;
	private int nRows;
	private int nColumns;
	
	/**
	 * Creates a (row x columns)-matrix. All elements are initialized with
	 * @code{null} values.
	 * 
	 * @param	rows
	 * 			number of rows
	 * @param	columns
	 * 			number of columns
	 */
	@SuppressWarnings("unchecked")
	public DefaultMatrix(int rows, int columns) {
		elements = (T[][]) new Object[rows][columns];
		nRows = rows;
		nColumns = columns;
	}
	
	/**
	 * Creates a (row x columns)-matrix All elements are initialized with {@value}.
	 * 
	 * @param	rows
	 * 			number of rows
	 * @param	columns
	 * 			number of columns
	 * @param	value
	 * 			the start value
	 */
	public DefaultMatrix(int rows, int columns, T value) {
		this(rows, columns);
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < columns; ++j) {
				elements[i][j] = value;
			}
		}
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param	matrix
	 * 			the matrix to be copied
	 */
	@SuppressWarnings("unchecked")
	public DefaultMatrix(Matrix<T> matrix) {
		nRows = matrix.rows();
		nColumns = matrix.columns();
		elements = ((T[][]) new Object[nRows][nColumns]);
		for (int i = 0; i < nRows; ++i) {
			for (int j = 0; j < nColumns; ++j) {
				elements[i][j] = matrix.get(i, j);
			}
		}
	}

	@Override
	public T get(int row, int column) {
		return elements[row][column];
	}

	@Override
	public void set(T element, int row, int column) {
		Checks.checkIndex(row, 0, nRows-1);
		Checks.checkIndex(column, 0, nColumns-1);
		elements[row][column] = element;
	}

	@Override
	public int rows() {
		return nRows;
	}

	@Override
	public int columns() {
		return nColumns;
	}
	
}