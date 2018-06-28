package br.ufmg.ppgee.orcslab.code.tsp;

import java.util.LinkedList;
import java.util.List;

import br.ufmg.ppgee.orcslab.code.utils.Checks;
import jopt.core.Solution;

/**
 * A solution for traveling salesman problem (TSP) is a tour. A tour is the sequence
 * in which the nodes are visited. Each node is represented by a integer.
 * 
 * @author	Andre L. Maravilha
 * @version	2013.04.03
 *
 */
public class TSPSolution extends Solution {
	
	private List<Integer> tour;
	
	/**
	 * Default constructor.
	 */
	public TSPSolution() {
		tour = new LinkedList<>();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param	solution
	 * 			the object that will be copied
	 */
	public TSPSolution(TSPSolution solution) {
		Checks.requireNonNullArgument(solution);
		tour = new LinkedList<>();
		tour.addAll(solution.tour);
	}
	
	/**
	 * Returns the node in the i-th position of the tour.
	 * 
	 * @param	index
	 * 			the index
	 * 
	 * @return	a integer that represents the node
	 */
	public int get(int index) {
		Checks.checkIndex(index, 0, tour.size()-1);
		return tour.get(index);
	}
	
	/**
	 * Sets the node in the i-th position of the tour.
	 * 
	 * @param	index
	 * 			the index
	 * @param	node
	 * 			the node to be set
	 */
	public void set(int index, int node) {
		Checks.checkIndex(index, 0, tour.size()-1);
		tour.set(index, node);
	}
	
	/**
	 * Adds a node at the end of the tour.
	 * 
	 * @param	node
	 * 			the node to be added
	 */
	public void add(int node) {
		tour.add(node);
	}
	
	/**
	 * Adds a node at the specified position of the tour.
	 * 
	 * @param	index
	 * 			the position
	 * @param	node
	 * 			the node to be added
	 */
	public void add(int index, int node) {
		Checks.checkIndex(index, 0, tour.size());
		tour.add(index, node);
	}
	
	/**
	 * Removes the node at the specified position of the tour.
	 * 
	 * @param	index
	 * 			the position
	 * 
	 * @return	the integer that represents the node removed
	 */
	public int remove(int index) {
		Checks.checkIndex(index, 0, tour.size()-1);
		return tour.remove(index);
	}
	
	/**
	 * Returns the number of nodes in the tour.
	 * 
	 * @return	the number of nodes in the tour
	 */
	public int size() {
		return tour.size();
	}

	@Override
	protected String doDescription() {
		return tour.toString();
	}

	@Override
	protected Solution doClone() {
		return new TSPSolution(this);
	}

}
