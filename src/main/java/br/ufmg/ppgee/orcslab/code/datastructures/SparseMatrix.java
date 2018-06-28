package br.ufmg.ppgee.orcslab.code.datastructures;

import br.ufmg.ppgee.orcslab.code.utils.Checks;

import java.util.HashMap;
import java.util.Map;

/**
 * This class implements a matrix of dynamic dimension that stores only the non-null
 * elements.
 * 
 * @author	Andre L. Maravilha
 * @version	2013.04.03
 * 
 * @param	<T>
 * 			the type of elements stored in the matrix
 * 
 */
public class SparseMatrix<T> implements Matrix<T> {
	
	private class Key {
		public int row;
		public int column;
		
		public Key(int row, int column) {
			this.row = row;
			this.column = column;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + column;
			result = prime * result + row;
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (column != other.column)
				return false;
			if (row != other.row)
				return false;
			return true;
		}

		@SuppressWarnings("rawtypes")
		private SparseMatrix getOuterType() {
			return SparseMatrix.this;
		}
	}
	
	private Map<Key, T> elements;
	private T defaultElement;
	private int rows;
	private int columns;
	
	public SparseMatrix() {
		this(null, 0, 0);
	}
	
	public SparseMatrix(T defaultElement) {
		this(defaultElement, 0, 0);
	}
	
	public SparseMatrix(int rows, int columns) {
		this(null, rows, columns);
	}
	
	public SparseMatrix(T defaultElement, int rows, int columns) {
		this.elements = new HashMap<>();
		this.defaultElement = defaultElement;
		this.rows = rows;
		this.columns = columns;
	}

	@Override
	public T get(int row, int column) {
		Checks.checkIndex(row, 0, rows-1);
		Checks.checkIndex(column, 0, columns-1);
		Key key = new Key(row, column);
		if (elements.containsKey(key)) {
			return elements.get(key);
		} else {
			return defaultElement;
		}
	}

	@Override
	public void set(T element, int row, int column) {
		if (row > rows) rows = row;
		if (column > columns) columns = column;
		elements.put(new Key(row, column), element);
	}

	@Override
	public int rows() {
		return rows;
	}

	@Override
	public int columns() {
		return columns;
	}
	
}