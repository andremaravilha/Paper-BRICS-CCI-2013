package br.ufmg.ppgee.orcslab.code.cli;

import br.ufmg.ppgee.orcslab.code.tsp.TSP;
import br.ufmg.ppgee.orcslab.code.tsp.TSPSolution;
import br.ufmg.ppgee.orcslab.code.tsp.algorithms.*;
import br.ufmg.ppgee.orcslab.code.tsp.loaders.TSPLIBLoader;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import jopt.core.Algorithm;
import jopt.core.Solution;
import jopt.core.sets.Entry;
import jopt.core.sets.SetSolutions;
import jopt.exceptions.FeasibilityException;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command line interface.
 */
@Parameters(commandDescription = "Algorithms based on Differential Evolution heuristic for the traveling salesman problem")
public class CommandLineInterface {

    @Parameter(names = {"-h", "--help"}, help = true, description = "Show the help message and exit.")
    public boolean help;

    @Parameter(names = "--print-solution", description = "Print the best solution found at the end of the optimization process.")
    public boolean print = false;

    @Parameter(names = "--seed", description = "Seed used to initialize the random number generator.")
    public Long seed = 0L;

    @Parameter(names = "--time-limit", description = "Maximum time for running the algorithm (in milliseconds).")
    public Long timelimit = Long.MAX_VALUE;

    @Parameter(names = "--iterations-limit", description = "Maximum number of iterations or running the algorithm.")
    public Long iterationsLimit = Long.MAX_VALUE;

    @Parameter(names = "--instance", description = "Path to the instance file.", required = true)
    public String instance = null;

    @Parameter(names = "--algorithm", description = "Algorithm used to solve the problem.", required = true)
    public String algorithmName = null;

    @Parameter(names = "--param", description = "Algorithm parameters.", converter = ParamConverter.class)
    public List<Param> parameters = new ArrayList<>();


    /**
     * Run the program from its input arguments.
     */
    public void run(String[] args) {

        // Initialize the parser
        JCommander cmd = JCommander.newBuilder()
                .addObject(this)
                .build();

        try {

            // Parse input arguments
            cmd.parse(args);

            // Show usage if required
            if (help) {
                cmd.usage();
                System.exit(0);
            }

            // Load instance file
            TSPLIBLoader loader = new TSPLIBLoader();
            loader.read(Paths.get(instance));

            // Intialize problem
            TSP problem = new TSP();
            problem.initialize(loader);

            // Instantiate the algorithm selected
            Algorithm<TSP> algorithm = null;
            if (algorithmName.equalsIgnoreCase("code")) {
                algorithm = new CoDE();
            } else if (algorithmName.equalsIgnoreCase("list-movements")) {
                algorithm = new DEListMovements();
            } else if (algorithmName.equalsIgnoreCase("permutation-matrix")) {
                algorithm = new DEPermutationMatrix();
            } else if (algorithmName.equalsIgnoreCase("relative-position-index")) {
                algorithm = new DERelativePositionIndex();
            } else if (algorithmName.equalsIgnoreCase("gurobi")) {
                algorithm = new Gurobi();
            } else {
                throw new RuntimeException("Algorithm is not valid");
            }

            // Set algorithm parameters
            Map<String, Object> params = parseAlgorithmParameters();
            params.put("seed", seed);
            params.put("time-limit", timelimit);
            params.put("iterations-limit", iterationsLimit);
            for (String name: params.keySet()) {
                algorithm.setParameter(name, params.get(name));
            }

            // Optimize
            SetSolutions<? extends Solution> solutions = algorithm.solve(problem);
            Entry<TSPSolution> entry = (Entry<TSPSolution>) solutions.iterator().next();

            // Check the status of the optimization process
            String status = "Feasible";
            String details = "A feasible solution has been found";
            try {
                problem.checkFeasibility(entry.solution());
            } catch (FeasibilityException e) {
                status = "Infeasible";
                details = e.getMessage();
            }

            // Print result
            if (print) {
                System.out.println(String.format("Feasibility: %s (%s)", status, details));
                System.out.println(String.format("Cost: %.4f", entry.evaluation(0)));
                System.out.println("Tour: " + entry.solution());
            } else {
                System.out.println(String.format("%s %.4f", status, entry.evaluation(0)));
            }

        } catch (ParameterException e) {

            // Get usage
            StringBuilder str = new StringBuilder();
            cmd.usage(str);

            // Show error and usage
            System.err.println("ERROR: " + e.getMessage());
            System.err.println(str.toString());

        } catch (Exception e) {
            System.err.println("UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // --------------------------------------------------------------------------------------------
    // Auxiliary methods
    // --------------------------------------------------------------------------------------------

    private Map<String, Object> parseAlgorithmParameters() {
        Map<String, Object> params = new HashMap<>();

        for (Param param: parameters) {
            switch (param.name) {
                case "population-size":
                    params.put(param.name, Integer.parseInt(param.value));
                    break;
                case "target-value":
                    params.put(param.name, Double.parseDouble(param.value));
                    break;
                case "submip-time-limit":
                    params.put(param.name, Long.parseLong(param.value));
                    break;
                case "mutation-type":
                    params.put(param.name, Integer.parseInt(param.value));
                    break;
                case "mutation-factor":
                    params.put(param.name, Double.parseDouble(param.value));
                    break;
                case "crossover-factor":
                    params.put(param.name, Double.parseDouble(param.value));
                    break;
                case "verbose":
                    params.put(param.name, Integer.parseInt(param.value));
                    break;
            }
        }

        return params;
    }
}
