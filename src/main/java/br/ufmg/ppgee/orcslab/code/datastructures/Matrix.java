package br.ufmg.ppgee.orcslab.code.datastructures;

/**
 * This interface defines a matrix data structure.
 * 
 * @author	Andre L. Maravilha
 * @version	2013.04.03
 * 
 * @param	<T>
 * 			the type of elements stored in the matrix
 * 
 * @see		DefaultMatrix
 * @see		SymmetricMatrix
 * 
 */
public interface Matrix<T> {
	
	/**
	 * Returns the element located at the specified row and column.
	 * 
	 * @param	row
	 * 			the row index
	 * @param	column
	 * 			the column index
	 * 
	 * @return	the element at the specified row and column
	 * 
	 * @throws	IndexOutOfBoundsException
	 * 			if the any index is invalid, ie, {@code row < 0} or
	 * 			{@code row >= {@link #row()}} or {@code column < 0} or
	 * 			{@code column >= {@link #columns()}}
	 */
	public T get(int row, int column);
	
	/**
	 * Set the value of the element located at the specified row and column.
	 * 
	 * @param	element
	 * 			the new value to the element
	 * @param	row
	 * 			the row index
	 * @param	column
	 * 			the column index
	 * 
	 * @throws	IndexOutOfBoundsException
	 * 			if the any index is invalid, ie, {@code row < 0} or
	 * 			{@code row >= {@link #row()}} or {@code column < 0} or
	 * 			{@code column >= {@link #columns()}}
	 */
	public void set(T element, int row, int column);
	
	/**
	 * Returns the number of rows in this matrix.
	 * 
	 * @return	the number of rows
	 */
	public int rows();
	
	/**
	 * Returns the number of columns in this matrix.
	 * 
	 * @return	the number of columns
	 */
	public int columns();
	
}
