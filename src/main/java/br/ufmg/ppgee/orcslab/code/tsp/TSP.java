package br.ufmg.ppgee.orcslab.code.tsp;

import br.ufmg.ppgee.orcslab.code.datastructures.Matrix;
import br.ufmg.ppgee.orcslab.code.utils.Checks;
import jopt.core.Loader;
import jopt.core.Problem;
import jopt.core.Solution;
import jopt.exceptions.AttributeNotFoundException;
import jopt.exceptions.FeasibilityException;

import java.util.Arrays;

/**
 * This class defines the traveling salesman problem (TSP).
 * 
 * <p> Given a set of nodes (e.g., cities or customers), the objective of the TSP is
 * to find the lowest cost route possible that visits each node exactly once and
 * returns to the origin node.
 * 
 * <p> Loader to this problem must provides the following attributes:
 * <table border="1" cellspacing="0" cellpadding="3">
 *   <tr>
 *     <th>Attribute</th>
 *     <th>Type</th>
 *     <th>Info</th>
 *   </tr>
 *   <tr>
 *     <td>cost-matrix</td>
 *     <td>{@code {@link Matrix}<{@link Double}>}</td>
 *     <td>The (i,j)-element of the cost matrix is the cost of visit the node j
 *         immediately after the node i. If does not exist a direct path from i to
 *         j, the (i,j)-element is equal to {@code null}.</td>
 *   </tr>
 * </table>
 * 
 * @author	Andre L. Maravilha
 * @version	2013.04.03
 *
 */
public class TSP extends Problem {
	
	private Matrix<Double> costMatrix;
	private int numberOfNodes;
	
	/**
	 * Sole constructor.
	 */
	public TSP() {
		costMatrix = null;
		numberOfNodes = 0;
	}

	/**
	 * Returns the cost matrix of the problem.
	 * 
	 * @return	the cost matrix
	 */
	public Matrix<Double> getCostMatrix() {
		return costMatrix;
	}
	
	/**
	 * Returns the cost of visiting the {@code target} customer immediately after
	 * the {@code source} customer.
	 * 
	 * @param	source
	 * 			the source customer
	 * @param	target
	 * 			the target customer
	 * 
	 * @return	cost of visiting the {@code target} customer immediately after the
	 * 			{@code source} customer 
	 */
	public double getCost(int source, int target) {
		Checks.checkIndex(source, 0, costMatrix.rows());
		Checks.checkIndex(source, 0, costMatrix.columns());
		return costMatrix.get(source, target);
	}
	
	/**
	 * Returns the dimension of the problem, i.e., the number of nodes in the
	 * problem.
	 * 
	 * @return	the dimension of the problem
	 */
	public int numberOfNodes() {
		return numberOfNodes;
	}

	@Override
	public int countObjectives() {
		return 1;
	}

	@Override
	public String[] getObjectiveNames() {
		String[] names = new String[] { "cost" };
		return names;
	}

	@Override
	protected void doInitialize(Loader loader) throws AttributeNotFoundException {
		costMatrix = (Matrix<Double>) loader.get("cost-matrix");
		numberOfNodes = costMatrix.rows();
	}

	@Override
	protected void doCheckFeasibility(Solution solution) throws FeasibilityException {

		// Checks for nullity
		Checks.requireNonNullArgument(solution);

		// Checks the type of solution
		TSPSolution tour = null;
		if (solution instanceof TSPSolution) {
			tour = (TSPSolution) solution;
		} else {
			throw new FeasibilityException("Expected a " + TSP.class.getName() + ", but found a " + solution.getClass().getName());
		}

		// Checks the constraints of the problem
		if (tour.size() != numberOfNodes) {
			throw new FeasibilityException("Expected a tour with " + numberOfNodes + " nodes, but the tour has " + tour.size() + " nodes");
		}

		int[] counter = new int[numberOfNodes];
		for (int i = 0; i < tour.size(); ++i) {
			int node = tour.get(i);
			if (node < 0 || node >= numberOfNodes) {
				throw new FeasibilityException("Node " + node + " is not a valid node");
			}
			++counter[node];
		}

		for (int i = 0; i < counter.length; ++i) {
			if (counter[i] != 1) {
				throw new FeasibilityException("Node " + i + " appears " + counter[i] + " times in this solution");
			}
		}
	}

	@Override
	protected double doEvaluate(Solution solution, int index) {
		TSPSolution tspSolution = Checks.requireType(solution, TSPSolution.class);
		Checks.checkObjectiveIndex(index, countObjectives());
		double cost = 0.0;
		if (tspSolution.size() > 1) {
			for (int i = 0; i < tspSolution.size(); ++i) {
				int source = tspSolution.get(i);
				int target = tspSolution.get((i + 1) % tspSolution.size());
				Double w = costMatrix.get(source, target);
				cost += (w != null) ? w : 0.0;
			}
		}
		return cost;
	}

}
