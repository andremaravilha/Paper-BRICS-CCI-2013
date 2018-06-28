package br.ufmg.ppgee.orcslab.code.tsp.algorithms;

import java.util.*;

import br.ufmg.ppgee.orcslab.code.tsp.TSP;
import br.ufmg.ppgee.orcslab.code.tsp.TSPSolution;
import jopt.core.Algorithm;
import jopt.core.Solution;
import jopt.core.annotations.Parameter;
import jopt.core.sets.DefaultSetSolutions;
import jopt.core.sets.SetSolutions;
import jopt.core.utils.NumberComparator;

/**
 * This algorithm find a solution to traveling salesman problem with an algorithm
 * based on differential evolution. It uses the list of movements approach,
 * introduced in [1].
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
 *     <td>mutation-factor</td>
 *     <td>{@link Double}</td>
 *     <td>The rate of mutation used to control the size of the list of movements.
 *         It must be between 0 and 1. By default, the value used is 0.5.</td>
 *   </tr>
 *   <tr>
 *     <td>mutation-type</td>
 *     <td>{@link Integer}</td>
 *     <td>Specified the method used to modify the list of movements. Considering F
 *         as the mutation factor and S the size of the list, use:
 *         <ol>
 *           <li>for building a list with the first &lceil;F&times;S&rceil;
 *               movements;</li>
 *           <li>for building a list where, for each movement in the original list,
 *               it will be present in the modified list with probability F;</li>
 *           <li>for building a list with &lceil;F&times;S&rceil; randomly chosen
 *               movements, at a random sequence.</li>
 *         </ol>
 *         By default, it is used the value 2.
 *     </td>
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
 *     <td>seed</td>
 *     <td>{@link Long}</td>
 *     <td>The seed used by the random number generator.</td>
 *   </tr>
 * </table>
 * 
 * <p> <b>References:</b>
 * <p> [1] Prado, R. S; Silva, R. C. P; Guimarães, F. G; Neto, O. M. "Using
 * differential evolution for combinatorial optimization: a general approach",
 * in 2010 IEEE International Conference on Systems Man and Cybernetics, 2010,
 * pp. 11-18.
 * 
 * 
 * @author	Andre L. Maravilha
 * @version	2013.05.08
 *
 */
public class DEListMovements extends Algorithm<TSP> {

	private Random random;

	@Parameter("seed")
	private long seed;

	@Parameter("population-size")
	private int populationSize;

	@Parameter("iterations-limit")
	private int iterationsLimit;

	@Parameter("mutation-factor")
	private double mutationFactor;

	@Parameter("mutation-type")
	private int mutationType;

	@Parameter("time-limit")
	private long timeLimit;

	@Parameter("target-value")
	private double targetValue;
	
	/**
	 * Sole constructor.
	 */
	public DEListMovements() {
		random = new Random();
		seed = 0;
		populationSize = 30;
		iterationsLimit = 50;
		mutationFactor = 0.5;
		mutationType = 2;
		timeLimit = Long.MAX_VALUE;
		targetValue = -Double.MAX_VALUE;
	}

	@Override
	protected SetSolutions<? extends Solution> doSolve(TSP problem, Map<String, Object> data) {

		// Initialize the random number generator
		random.setSeed(seed);

		// Gets the start time of the algorithm
		long startTime = System.currentTimeMillis();

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
				int idx3 = random.nextInt(populationSize);
				while (idx1 == i) idx1 = random.nextInt(populationSize);
				while (idx2 == i || idx2 == idx1) idx2 = random.nextInt(populationSize);
				while (idx3 == i || idx3 == idx1 || idx3 == idx2) idx3 = random.nextInt(populationSize);

				TSPSolution solution1 = mainPopulation.get(idx1);
				TSPSolution solution2 = mainPopulation.get(idx2);
				TSPSolution solution3 = mainPopulation.get(idx3);

				List<Movement> movements = createListOfMovements(solution3, solution2);
				switch (mutationType) {
					case 1:
						movements = modifyListOfMovements1(movements, mutationFactor);
						break;
					case 2:
						movements = modifyListOfMovements2(movements, mutationFactor);
						break;
					case 3:
						movements = modifyListOfMovements3(movements, mutationFactor);
						break;
				}

				TSPSolution trialSolution = applyMovements(solution1, movements);
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

	private List<Movement> createListOfMovements(TSPSolution solution1, TSPSolution solution2) {
		List<Movement> movements = new LinkedList<>();
		TSPSolution aux = new TSPSolution(solution1);
		for (int i = 0; i < solution2.size(); ++i) {
			int currentNode = solution2.get(i);
			for (int j = i; j < aux.size(); ++j) {
				if (aux.get(j) == currentNode && j != i) {
					Movement mv = new Movement();
					mv.source = j;
					mv.target = i;
					movements.add(mv);
					int nodeSource = aux.get(j);
					int nodeTarget = aux.get(i);
					aux.set(j, nodeTarget);
					aux.set(i, nodeSource);
					break;
				}
			}
		}

		return movements;
	}

	private TSPSolution applyMovements(TSPSolution solution, List<Movement> movements) {
		TSPSolution mutated = new TSPSolution(solution);
		for (Movement e : movements) {
			int nodeSource = mutated.get(e.source);
			int nodeTarget = mutated.get(e.target);
			mutated.set(e.source, nodeTarget);
			mutated.set(e.target, nodeSource);
		}
		return mutated;
	}

	private List<Movement> modifyListOfMovements1(List<Movement> movements, double mutationFactor) {
		int listSize = (int) Math.ceil(mutationFactor * movements.size());
		if (listSize > movements.size()) {
			listSize = movements.size();
		}
		return movements.subList(0, listSize + 1);
	}

	private List<Movement> modifyListOfMovements2(List<Movement> movements, double mutationFactor) {
		Iterator<Movement> iterator = movements.iterator();
		while (iterator.hasNext()) {
			iterator.next();
			if (random.nextDouble() > mutationFactor) {
				iterator.remove();
			}
		}
		return movements;
	}

	private List<Movement> modifyListOfMovements3(List<Movement> movements, double mutationFactor) {
		Collections.shuffle(movements, random);
		int listSize = (int) Math.ceil(mutationFactor * movements.size());
		if (listSize > movements.size()) {
			listSize = movements.size();
		}
		return movements.subList(0, listSize + 1);
	}

	private TSPSolution createRandomTour(List<Integer> nodes) {
		Collections.shuffle(nodes, random);
		TSPSolution solution = new TSPSolution();
		for (int i = 0; i < nodes.size(); ++i) {
			solution.add(nodes.get(i));
		}
		return solution;
	}


	// --------------------------------------------------------------------------------------------
	// Auxiliary classes
	// --------------------------------------------------------------------------------------------

	private class Movement {
		public int source;
		public int target;
	}

}
