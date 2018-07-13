/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bundesbank.jdemetra.kix.annualchaining;

import de.bundesbank.jdemetra.kix.annualchaining.core.AnnualOverlapMethods;
import de.bundesbank.kix.parser.AbstractParser;
import de.bundesbank.kix.parser.IParser;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.regex.Pattern;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Deutsche Bundesbank
 */
@ServiceProvider(service = IParser.class)
public final class ChaAnn extends AbstractParser {

    private static final String[] VALID_CONTROL_CHARACTER = {"cha.ann"};
    private static final Pattern SYNTAX = Pattern.compile("cha\\.ann,[iw]\\d+(,\\d{4})?");

    @Override
    public TsData compute(final String formula, final TsVariables indices, final TsVariables weights) {
        String lowerCaseFormula = formula.toLowerCase().replaceAll("\\s*", "");
        if (!SYNTAX.matcher(lowerCaseFormula).matches()) {
            setErrorMessage("Formula doesn't match required syntax");
            return null;
        }

        String[] splitFormula = lowerCaseFormula.split(",");
        TsData timeSeries;
        if (indices.contains(splitFormula[1])) {
            timeSeries = extractData(indices.get(splitFormula[1]));
        } else if (weights.contains(splitFormula[1])) {
            timeSeries = extractData(weights.get(splitFormula[1]));
        } else {
            setErrorMessage(splitFormula[1] + " doesn't exist");
            return null;
        }
        if (splitFormula.length > 2) {
            return AnnualOverlapMethods.chainSum(timeSeries).index(TsPeriod.year(Integer.parseInt(splitFormula[2])), 100);
        }
        return AnnualOverlapMethods.chainSum(timeSeries);
    }

    @Override
    public String[] getValidControlCharacter() {
        return VALID_CONTROL_CHARACTER.clone();
    }

}
