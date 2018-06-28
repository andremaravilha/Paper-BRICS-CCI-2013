package br.ufmg.ppgee.orcslab.code.tsp.algorithms;

import br.ufmg.ppgee.orcslab.code.datastructures.Matrix;
import br.ufmg.ppgee.orcslab.code.datastructures.SymmetricMatrix;
import br.ufmg.ppgee.orcslab.code.tsp.TSP;
import br.ufmg.ppgee.orcslab.code.tsp.TSPSolution;
import jopt.core.Algorithm;
import jopt.core.Solution;
import jopt.core.annotations.Parameter;
import jopt.core.sets.DefaultSetSolutions;
import jopt.core.sets.SetSolutions;
import jopt.core.utils.NumberComparator;

import java.util.*;


/**
 * This algorithm find a solution to traveling salesman problem with a hybrid
 * heuristic based on differential evolution with exact methods.
 * 
 * <p> This algorithm allows setting the following parameters:
 * <table border="1" cellspacing="0" cellpadding="3">
 *   <tr>
 *     <th>Attribute</th>
 *     <th>Type</th>
 *     <th>Info</th>
 *   </tr>
 *   <tr>
 *     <td>population-size</td>
 *     <td>{@link Integer}</td>
 *     <td>The population size of the algorithm. By default, the value used is 30.</td>
 *   </tr>
 *   <tr>
 *     <td>iterations-limit</td>
 *     <td>{@link Long}</td>
 *     <td>The maximum number of generations. By default, the value used is 50. For
 *         infinity generations, use the value -1.</td>
 *   </tr>
 *   <tr>
 *     <td>target-value</td>
 *     <td>{@link Double}</td>
 *     <td>When the evaluation of the objective function of any solution obtains a
 *         value less than or equal to {@code target}, the algorithm terminates.</td>
 *   </tr>
 *   <tr>
 *     <td>time-limit</td>
 *     <td>{@link Long}</td>
 *     <td>Sets the maximum run time (in milliseconds) of the algorithm. The time check
 *         is performed before the start of execution of each generation, so if the
 *         maximum time is reached during the execution of a generation, the
 *         algorithm will terminate its activity only at the end of the generation.</td>
 *   </tr>
 *   <tr>
 *     <td>gurobi-time-limit</td>
 *     <td>{@link Double}</td>
 *     <td>The crossover is performed through a sub-MIP obtained from the original
 *         problem. Each sub-MIP is solved by {@link Gurobi}, which finds the
 *         optimal solution for the sub-MIP. The parameter {@code gurobi-time-limit}
 *         allows to limit the maximum time (in milliseconds) for each execution of Gurobi.
 *         If this parameter is set, it is not guaranteed the sub-MIP is optimality solved.</td>
 *   </tr>
 *   <tr>
 *     <td>seed</td>
 *     <td>{@link Long}</td>
 *     <td>The seed used by the random number generator.</td>
 *   </tr>
 * </table>
 * 
 * @author	Andre L. Maravilha
 * @version	2013.04.03
 *
 */
public class CoDE extends Algorithm<TSP> {
	
	private Random random;

	@Parameter("seed")
	private long seed;

	@Parameter("population-size")
	private int populationSize;

	@Parameter("iterations-limit")
	private int iterationsLimit;

	@Parameter("time-limit")
	private long timeLimit;

	@Parameter("submip-time-limit")
	private long submipTimeLimit;

	@Parameter("target-value")
	private double targetValue;
	
	/**
	 * Sole constructor.
	 */
	public CoDE() {
		random = new Random();
		seed = 0;
		populationSize = 30;
		iterationsLimit = 50;
		timeLimit = Long.MAX_VALUE;
		submipTimeLimit = Long.MAX_VALUE;
		targetValue = -Double.MAX_VALUE;
	}

	@Override
	protected SetSolutions<? extends Solution> doSolve(TSP problem, Map<String, Object> data) {

		// Initialize the random number generator
		random.setSeed(seed);
		
		// Gets the start time of the algorithm
		long startTime = System.currentTimeMillis();
		
		// Creates a gurobi instance to solve the subproblems
		Gurobi gurobi = new Gurobi();
		gurobi.setParameter("seed", seed);
		gurobi.setParameter("time-limit", submipTimeLimit);
		
		// Stores the main population
		List<TSPSolution> mainPopulation = new ArrayList<>(populationSize);
		List<Double> mainEvaluation = new ArrayList<>(populationSize);
		
		// Stores the offspring population
		List<TSPSolution> offspringPopulation = new ArrayList<>(populationSize);
		List<Double> offspringEvaluation = new ArrayList<>(populationSize);
		
		// Creates a list of nodes to generate random solutions
		List<Integer> nodes = new ArrayList<>(problem.numberOfNodes());
		for (int i = 0; i < problem.numberOfNodes(); ++i) {
			nodes.add(i);
		}
		
		// Matrices used in mutation and crossover operation
		Matrix<Boolean> matrix1 = new SymmetricMatrix<>(problem.numberOfNodes());
		Matrix<Boolean> matrix2 = new SymmetricMatrix<>(problem.numberOfNodes());
		Matrix<Boolean> matrix3 = new SymmetricMatrix<>(problem.numberOfNodes());
		Matrix<Boolean> xorMatrix = new SymmetricMatrix<>(problem.numberOfNodes());
		Matrix<Boolean> diffMatrix = new SymmetricMatrix<>(problem.numberOfNodes());
		Matrix<Boolean> targetMatrix = new SymmetricMatrix<>(problem.numberOfNodes());
		Matrix<Boolean> unionMatrix = new SymmetricMatrix<>(problem.numberOfNodes());
		
		// Initializes the main population randomly
		TSPSolution bestSolution = null;
		double bestEvaluation = Double.MAX_VALUE;
		for (int i = 0; i < populationSize; ++i) {
			TSPSolution solution = createRandomTour(nodes);
			double evaluation = problem.evaluate(solution, 0);
			mainPopulation.add(solution);
			mainEvaluation.add(evaluation);
			
			if (evaluation < bestEvaluation) {
				bestEvaluation = evaluation;
				bestSolution = solution;
			}
		}
		
		// Evolution of the population
		int currentGeneration = 0;
		while ((iterationsLimit == -1 || currentGeneration < iterationsLimit) &&
				NumberComparator.compare(bestEvaluation, targetValue, 0.01) > 0 &&
				System.currentTimeMillis() - startTime < timeLimit) {
			
			// Increments the generation counter
			if (iterationsLimit != -1) {
				++currentGeneration;
			}
			
			// Clears the previous population of offspring
			offspringPopulation.clear();
			offspringEvaluation.clear();
			
			for (int i = 0; i < populationSize; ++i) {
				
				// Step of differentiation (mutation)
				int idx1 = random.nextInt(populationSize);
				int idx2 = random.nextInt(populationSize);
				while (idx1 == i) idx1 = random.nextInt(populationSize);
				while (idx2 == i || idx2 == idx1) idx2 = random.nextInt(populationSize);
				createAdjacencyMatrix(mainPopulation.get(idx1), matrix1);
				createAdjacencyMatrix(mainPopulation.get(idx2), matrix2);
				createAdjacencyMatrix(createRandomTour(nodes), matrix3);
				xor(matrix1, matrix2, xorMatrix);
				union(matrix3, xorMatrix, diffMatrix);
				
				// Crossover
				createAdjacencyMatrix(mainPopulation.get(i), targetMatrix);
				union(targetMatrix, diffMatrix, unionMatrix);
				TSPSolution trialSolution = gurobi.solve(problem, unionMatrix, mainPopulation.get(i));
				double trialEvaluation = problem.evaluate(trialSolution, 0);
				
				// Selection
				if (trialEvaluation < mainEvaluation.get(i)) {
					offspringPopulation.add(trialSolution);
					offspringEvaluation.add(trialEvaluation);
					
					// Updates the best solution
					if (trialEvaluation < bestEvaluation) {
						bestEvaluation = trialEvaluation;
						bestSolution = trialSolution;
					}
					
				} else {
					offspringPopulation.add(mainPopulation.get(i));
					offspringEvaluation.add(mainEvaluation.get(i));
				}
			}
			
			// Updates the population
			mainPopulation.clear();
			mainEvaluation.clear();
			for (int i = 0; i < populationSize; ++i) {
				mainPopulation.add(offspringPopulation.get(i));
				mainEvaluation.add(offspringEvaluation.get(i));
			}
		}
		
		// Creates the result with the best solution found
		SetSolutions<TSPSolution> result = new DefaultSetSolutions<>();
		result.add(bestSolution, bestEvaluation);
		return result;
	}


	// --------------------------------------------------------------------------------------------
	// Auxiliary methods
	// --------------------------------------------------------------------------------------------
	
	private TSPSolution createRandomTour(List<Integer> nodes) {
		Collections.shuffle(nodes, random);
		TSPSolution solution = new TSPSolution();
		for (int i = 0; i < nodes.size(); ++i) {
			solution.add(nodes.get(i));
		}
		return solution;
	}
	
	private void createAdjacencyMatrix(TSPSolution tour, Matrix<Boolean> result) {
		for (int i = 0; i < result.rows(); ++i) {
			for (int j = i; j < result.columns(); ++j) {
				result.set(false, i, j);
			}
		}
		
		for (int i = 0; i < tour.size(); ++i) {
			result.set(true, tour.get(i), tour.get((i+1) % tour.size()));
		}
	}
	
	private void union(Matrix<Boolean> matrix1, Matrix<Boolean> matrix2, Matrix<Boolean> result) {
		for (int i = 0; i < matrix1.rows(); ++i) {
			for (int j = i; j < matrix1.columns(); ++j) {
				if (matrix1.get(i, j) || matrix2.get(i, j)) {
					result.set(true, i, j);
				} else {
					result.set(false, i, j);
				}
			}
		}
	}
	
	private void xor(Matrix<Boolean> matrix1, Matrix<Boolean> matrix2, Matrix<Boolean> result) {
		for (int i = 0; i < matrix1.rows(); ++i) {
			for (int j = i; j < matrix1.columns(); ++j) {
				if (matrix1.get(i, j).booleanValue() != matrix2.get(i, j).booleanValue()) {
					result.set(true, i, j);
				} else {
					result.set(false, i, j);
				}
			}
		}
	}

}
