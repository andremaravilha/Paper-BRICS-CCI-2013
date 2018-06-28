package br.ufmg.ppgee.orcslab.code.datastructures;

import java.util.Arrays;

/**
 * A symmetric matrix is a square-matrix where the (i,j)-element and the
 * (j,i)-element are the same.
 * 
 * @author	Andre L. Maravilha
 * @version	2013.04.03
 * 
 * @param	<T>
 * 			the type of elements stored in the matrix
 * 
 */
public class SymmetricMatrix<T> implements Matrix<T> {
	
	private T[] elements;
	private int dimension;
	private int numberOfElements;
	
	/**
	 * Creates a symmetric matrix with dimensions of size {@code dimension}.
	 * 
	 * @param	dimension
	 			the size of dimensions
	 */
	@SuppressWarnings("unchecked")
	public SymmetricMatrix(int dimension) {
		this.dimension = dimension;
		numberOfElements = (dimension * (dimension + 1)) / 2;
		elements = (T[]) new Object[numberOfElements];
	}
	
	/**
	 * Creates a symmetric matrix with dimension of size {@code dimension}. All
	 * elements of the matrix are initialized with {@value}.
	 * 
	 * @param	dimension
	 * 			the size of dimension
	 * @param	value
	 * 			the start value
	 */
	public SymmetricMatrix(int dimension, T value) {
		this(dimension);
		for (int i = 0; i < elements.length; ++i) {
			elements[i] = value;
		}
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param	matrix
	 * 			the matrix to be copied
	 */
	public SymmetricMatrix(SymmetricMatrix<T> matrix) {
		elements = Arrays.copyOf(matrix.elements, matrix.elements.length);
		dimension = matrix.dimension;
		numberOfElements = matrix.numberOfElements;
	}

	@Override
	public T get(int row, int column) {
		return elements[calculatePosition(row, column)];
	}

	@Override
	public void set(T element, int row, int column) {
		elements[calculatePosition(row, column)] = element;
	}

	@Override
	public int rows() {
		return dimension;
	}

	@Override
	public int columns() {
		return dimension;
	}
	
	private int calculatePosition(int row, int column) {
		if (row >= column) {
			return ((row * (row + 1)) / 2) + column;
		} else {
			return ((column * (column + 1)) / 2) + row;
		}
	}
	
}

