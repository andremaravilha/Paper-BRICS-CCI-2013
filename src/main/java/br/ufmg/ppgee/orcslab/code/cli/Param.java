package br.ufmg.ppgee.orcslab.code.cli;

/**
 * Auxiliary class to store a parameter.
 */
public class Param {

    /**
     * Name of the parameter.
     */
    public final String name;

    /**
     * Value of the parameter.
     */
    public final String value;

    /**
     * Constructor.
     * @param name Name of the parameter.
     * @param value Value of the parameter.
     */
    public Param(String name, String value) {
        this.name = name;
        this.value = value;
    }

}
