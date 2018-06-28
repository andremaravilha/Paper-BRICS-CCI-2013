package br.ufmg.ppgee.orcslab.code.tsp.algorithms;

import br.ufmg.ppgee.orcslab.code.datastructures.Matrix;
import br.ufmg.ppgee.orcslab.code.datastructures.SymmetricMatrix;
import br.ufmg.ppgee.orcslab.code.tsp.TSP;
import br.ufmg.ppgee.orcslab.code.tsp.TSPSolution;
import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import jopt.core.Algorithm;
import jopt.core.Solution;
import jopt.core.annotations.Parameter;
import jopt.core.sets.DefaultSetSolutions;
import jopt.core.sets.SetSolutions;

import java.util.Map;

/**
 * This algorithm solves a instance of the traveling salesman problem ({@link TSP})
 * at optimality using the Gurobi solver.
 * 
 * <p> This algorithm allows setting the following parameters:
 * <table border="1" cellspacing="0" cellpadding="3">
 *   <tr>
 *     <th>Attribute</th>
 *     <th>Type</th>
 *     <th>Info</th>
 *   </tr>
 *   <tr>
 *     <td>iterations-limit</td>
 *     <td>{@link Long}</td>
 *     <td>The maximum number of nodes explored by Gurobi solver.</td>
 *   </tr>
 *   <tr>
 *     <td>time-limit</td>
 *     <td>{@link Long}</td>
 *     <td>Sets the maximum run time (in milliseconds) of Gurobi.</td>
 *   </tr>
 *   <tr>
 *     <td>verbose</td>
 *     <td>{@link Integer}</td>
 *     <td>Use 1 for display on standard output the progress of Gurobi
 *         solver, or use 0 (zero) otherwise.</td>
 *   </tr>
 * </table>
 * 
 * @author	Andre L. Maravilha
 * @version	2013.04.03
 *
 */
public class Gurobi extends Algorithm<TSP> {

	@Parameter("seed")
	private long seed;

	@Parameter("verbose")
	private int verbose;

	@Parameter("time-limit")
	private long timeLimit;

	@Parameter("iterations-limit")
	private long iterationsLimit;
	
	private GRBEnv env;
	
	/**
	 * Sole constructor.
	 */
	public Gurobi() {
		seed = 0;
		verbose = 0;
		iterationsLimit = Long.MAX_VALUE;
		timeLimit = Long.MAX_VALUE;
		try {
			env = new GRBEnv();
			env.set(GRB.IntParam.OutputFlag, 0);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	protected void finalize() {
		if (env != null) {
			try {
				env.dispose();
			} catch (Exception e) {
				/* It does nothing here */
			}
		}
	}

	@Override
	protected SetSolutions<? extends Solution> doSolve(TSP problem, Map<String, Object> data) {
		
		// Activates all edges of the problem
		Matrix<Boolean> activeEdges = new SymmetricMatrix<>(problem.numberOfNodes(), true);
		for (int i = 0; i < problem.numberOfNodes(); ++i) {
			activeEdges.set(false, i, i);
		}
		
		// Solve the problem
		TSPSolution solution = solve(problem, activeEdges, null);
		double[] evaluation = problem.evaluate(solution);
		
		// Creates a set of solutions with the solution found
		SetSolutions<TSPSolution> result = new DefaultSetSolutions<>();
		result.add(solution, evaluation);
		return result;
	}
	
	/**
	 * Solves the traveling salesman problem considering only a subset of the edges
	 * of the original problem.
	 * 
	 * @param	problem
	 * 			the problem that will be solved
	 * @param	activeEdges
	 * 			the active edges
	 * 
	 * @return	a solution for the problem
	 */
	public TSPSolution solve(TSP problem, Matrix<Boolean> activeEdges) {
		return solve(problem, activeEdges, null);
	}
	
	/**
	 * Solves the traveling salesman problem considering only a subset of the edges
	 * of the original problem. It is possible to inform a starting solution and, if
	 * this solution is a valid solution, in the worst case, this solution will be
	 * returned.
	 * 
	 * @param	problem
	 * 			the problem that will be solved
	 * @param	activeEdges
	 * 			the active edges
	 * @param	start
	 * 			a starting solution
	 * 
	 * @return	a solution for the problem
	 */
	public TSPSolution solve(TSP problem, Matrix<Boolean> activeEdges, TSPSolution start) {

		// Store the final solution
		TSPSolution tour = new TSPSolution();
		
		try {
			
			// Gets the data of the problem
			Matrix<Double> c = problem.getCostMatrix();
			int n = problem.numberOfNodes();
			
			// Creates a model object
			GRBModel model = new GRBModel(env);

			// Set gurobi parameters
			model.set(GRB.IntParam.LazyConstraints, 1);
			model.getEnv().set(GRB.IntParam.Seed, (int) seed);
			model.getEnv().set(GRB.DoubleParam.NodeLimit, iterationsLimit);
			model.getEnv().set(GRB.DoubleParam.TimeLimit, timeLimit / (double) 1000);
			model.getEnv().set(GRB.IntParam.OutputFlag, verbose);
			
			// Creates the variables
			Matrix<GRBVar> vars = new SymmetricMatrix<>(n);
			for (int i = 0; i < n; ++i) {
				for (int j = i+1; j < n; ++j) {
					if (activeEdges.get(i, j)) {
						GRBVar var = model.addVar(0.0, 1.0, c.get(i,j), GRB.BINARY, "x_"+i+"_"+j);
						vars.set(var, i, j);
					}
				}
			}
			
			// Integrate variables
			model.update();
			
			// Set the start solution
			if (start != null) {
				for (int i = 0; i < start.size(); ++i) {
					int source = start.get(i);
					int target = start.get((i+1) % start.size());
					vars.get(source, target).set(GRB.DoubleAttr.Start, 1);
				}
			}
			
			// Degree-2 constraints
			for (int i = 0; i < vars.rows(); ++i) {
				GRBLinExpr expr = new GRBLinExpr();
				for (int j = 0; j < vars.columns(); ++j) {
					if (activeEdges.get(i, j)) {
						expr.addTerm(1.0, vars.get(i, j));
					}
				}
				model.addConstr(expr, GRB.EQUAL, 2, "deg_"+i);
			}
			
			// Set callback for sub-tour elimination
			Callback callback = new Callback(vars);
			model.setCallback(callback);
			
			// Optimize the model
			model.optimize();
			
			// Transforms the result to the format used
			if (callback.getSolution() != null) {
				for (int node : callback.getSolution()) {
					tour.add(node);
				}
			}
			
			model.dispose();
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		
		return tour;
	}


	// --------------------------------------------------------------------------------------------
	// Auxiliary methods
	// --------------------------------------------------------------------------------------------
	
	private class Callback extends GRBCallback {

		private Matrix<GRBVar> vars;
		private int[] solution;
		
		public Callback(Matrix<GRBVar> vars) {
			this.vars = vars;
			this.solution = null;
		}
		
		@Override
		protected void callback() {
			try {
				if (where == GRB.CB_MIPSOL) {
					int[] subtour = findSubtour(vars);
					
					if (subtour.length < vars.rows()) {
						GRBLinExpr expr = new GRBLinExpr();
						for (int i = 0; i < subtour.length; ++i) {
							for (int j = i+1; j < subtour.length; ++j) {
								if (vars.get(subtour[i], subtour[j]) != null) {
									expr.addTerm(1.0, vars.get(subtour[i], subtour[j]));
								}
							}
						}
						addLazy(expr, GRB.LESS_EQUAL, subtour.length-1);
					} else {
						solution = subtour;
					}
				}
			} catch (GRBException e) {
				// It does nothing
			}
		}
		
		private int[] findSubtour(Matrix<GRBVar> vars) throws GRBException {
			int n = vars.rows();
			boolean[] seen = new boolean[n];
			int[] tour = new int[n];
			int bestind, bestlen;
			int i, node, len, start;

			for (i = 0; i < n; i++)
				seen[i] = false;

			start = 0;
			bestlen = n+1;
			bestind = -1;
			node = 0;
			while (start < n) {
				for (node = 0; node < n; node++)
					if (!seen[node])
						break;
				if (node == n)
					break;
				for (len = 0; len < n; len++) {
					tour[start+len] = node;
					seen[node] = true;
					for (i = 0; i < n; i++) {
						if (vars.get(node, i) != null && getSolution(vars.get(node, i)) > 0.5 && !seen[i]) {
							node = i;
							break;
						}
					}
					if (i == n) {
						len++;
						if (len < bestlen) {
							bestlen = len;
							bestind = start;
						}
						start += len;
						break;
					}
				}
			}

			int result[] = new int[bestlen];
			for (i = 0; i < bestlen; i++)
				result[i] = tour[bestind+i];
			return result;
		}
		
		public int[] getSolution() {
			return solution;
		}
		
	}

}
