package br.ufmg.ppgee.orcslab.code.cli;

import com.beust.jcommander.IStringConverter;

/**
 * Custom converter to parse algorithm parameters from command line ({@link Param}).
 */
public class ParamConverter implements IStringConverter<Param> {

    @Override
    public Param convert(String value) {
        String[] s = value.split("=");
        return new Param(s[0], s[1]);
    }

}
