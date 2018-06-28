package br.ufmg.ppgee.orcslab.code;

import br.ufmg.ppgee.orcslab.code.cli.CommandLineInterface;

/**
 * Main class.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // Parse input parameters and run the command
        CommandLineInterface command = new CommandLineInterface();
        command.run(args);

    }
}
