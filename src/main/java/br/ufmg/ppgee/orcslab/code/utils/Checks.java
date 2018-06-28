package br.ufmg.ppgee.orcslab.code.utils;

/**
 * This class offers several static methods for verification of properties helping
 * the writing of other methods that require to perform these inspections.
 * 
 * @author	Andre L. Maravilha
 * @version	2013.03.12
 * 
 */
public final class Checks {
	
	/**
	 * Checks that the specified object reference is not {@code null} and
     * throws a customized {@link IllegalArgumentException} if it is. This method
     * is designed primarily for doing parameter validation in methods and
     * constructors.
	 * 
	 * @param	object
	 * 			the object reference to check for nullity
	 * 
	 * @return	{@code object} if not {@code null}
	 * 
	 * @throws	IllegalArgumentException
	 * 			if {@code object} is {@code null}
	 */
	public static final <T> T requireNonNullArgument(T object) {
		if (object == null) {
			throw new IllegalArgumentException("A non-null argument is required");
		}
		return object;
	}
	
	/**
	 * Checks that the specified object reference is not {@code null} and
     * throws a customized {@link NullPointerException} if it is.
     * 
	 * @param	object
	 * 			the object reference to check for nullity
	 * 
	 * @return	{@code object} if not {@code null}
	 * 
	 * @throws	NullPointerException
	 * 			if {@code object} is {@code null}
	 */
	public static final <T> T requireNonNull(T object) {
		if (object == null) {
			throw new NullPointerException("A non-null reference is required");
		}
		return object;
	}
	
	/**
	 * Checks if the index is valid. The index is valid if {@code lower} &le;
	 * {@code index} &le; {@code upper}.
	 * 
	 * @param	index
	 * 			the index to check
	 * 
	 * @param	lower
	 * 			the lower bound for the index			
	 * @param	upper
	 * 			the upper bound for the index
	 * 
	 * @throws	IndexOutOfBoundsException
	 * 			if the {@code index} is not valid, i.e., {@code index < lower} or
	 * 			{@code index > upper}
	 */
	public static final void checkIndex(int index, int lower, int upper) {
		if (index < lower || index > upper) {
			throw new IndexOutOfBoundsException("Index: " + index + ", valid range: [" +
					+ lower + ", " + upper + "]");
		}
	}
	
	/**
	 * Checks if the objective index is valid.
	 * 
	 * @param	index
	 * 			the index of the objective to check
	 * 			
	 * @param	numObjectives
	 * 			the number of objectives
	 * 
	 * @throws	IndexOutOfBoundsException
	 * 			if the {@code index} is not valid, i.e., {@code index < 0} or {@code
	 * 			index >= numObjectives}
	 */
	public static final void checkObjectiveIndex(int index, int numObjectives) {
		if (index < 0 || index >= numObjectives) {
			throw new IndexOutOfBoundsException("Index: " + index +
					", number of objectives: " + numObjectives);
		}
	}
	
	/**
	 * Checks the type of the object and casts it if its type is the expected type.
	 * 
	 * @param	object
	 * 			the object to check
	 * @param	clazz
	 * 			the class type expected
	 * 
	 * @return	the object cast
	 * 
	 * @throws	ClassCastException
	 * 			if {@code object} does not match the expected type
	 */
	public static final <T> T requireType(Object object, Class<T> clazz) {
		if (clazz.isInstance(object)) {
			return clazz.cast(object);
		} else {
			throw new ClassCastException("It is not possible to convert " +
							object.getClass() + " to the exptected type" + clazz);
		}
	}
	
}
