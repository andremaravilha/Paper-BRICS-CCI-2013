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
 * This algorithm find a solution to traveling salesman problem with a method
 * based on differential evolution (DE). It uses a permutation matrix that
 * maps one solution to another. This permutation matrix defines swap movements
 * that are applied to a third solution. For more detail, it can be seen in [1] and
 * [2].
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
 *     <td>seed</td>
 *     <td>{@link Long}</td>
 *     <td>The seed used by the random number generator.</td>
 *   </tr>
 * </table>
 * 
 * <p> <b>References:</b>
 * <p> [1] Price, K.; Storn, R. M.; Lampinen, J. "Differential Evolution: a
 * practical approach to a global optimization, ser. Natural Computing Series.
 * Springer, 2005.
 * <p> [2] Onwubolu, G.; Davendra, D. "Motivation for differential evolution for
 * permutative-based combinatorial problems", in Differential Evolution: a handbook
 * for global permutation-based combinatorial optimization", ser. Studies in
 * Computational Intelligence, G. C. Onwubolu and D. Devandra, Eds. Springer, 2009,
 * vol. 175, pp. 13-34.
 * 
 * @author	Andre L. Maravilha
 * @version	2013.05.08
 *
 */
public class DEPermutationMatrix extends Algorithm<TSP> {
	
	private Random random;

	@Parameter("seed")
	private long seed;

	@Parameter("population-size")
	private int populationSize;

	@Parameter("iterations-limit")
	private int iterationsLimit;

	@Parameter("time-limit")
	private long timeLimit;

	@Parameter("target-value")
	private double targetValue;
	
	/**
	 * Sole constructor.
	 */
	public DEPermutationMatrix() {
		random = new Random();
		seed = 0;
		populationSize = 30;
		iterationsLimit = 50;
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
		
		// Data structures and values used by this algorithm
		int[] map = new int[problem.numberOfNodes()];
		int[] permutation = new int[problem.numberOfNodes()];
		
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
				
				map = mappping(solution2, solution3, map);
				TSPSolution trialSolution = mutate(solution1, map, permutation);
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
	
	private int[] mappping(TSPSolution solution1, TSPSolution solution2, int[] map) {
		for (int i = 0; i < solution1.size(); ++i) {
			int currentNode = solution1.get(i);
			map[i] = i;
			for (int j = 0; j < solution2.size(); ++j) {
				if (currentNode == solution2.get(j)) {
					map[i] = j;
					break;
				}
			}
		}
		return map;
	}
	
	private TSPSolution mutate(TSPSolution solution, int[] map, int[] permutation) {
		for (int i = 0; i < map.length; ++i) {
			permutation[map[i]] = solution.get(i);
		}
		
		TSPSolution mutatedSolution = new TSPSolution();
		for (int e : permutation) {
			mutatedSolution.add(e);
		}
		return mutatedSolution;
	}

}
