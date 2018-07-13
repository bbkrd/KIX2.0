/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bundesbank.jdemetra.kix.annualchaining;

import de.bundesbank.jdemetra.kix.annualchaining.core.CliAnnCalc;
import de.bundesbank.jdemetra.kix.annualchaining.options.AnnOptionsPanelController;
import de.bundesbank.kix.parser.AbstractParser;
import de.bundesbank.kix.parser.ICalc;
import de.bundesbank.kix.parser.IParser;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Deutsche Bundesbank
 */
@ServiceProvider(service = IParser.class)
public final class CliAnn extends AbstractParser {

    private static final String[] VALID_CONTROL_CHARACTER = {"kix", "cli.ann"};
    private static final Pattern SYNTAX = Pattern.compile("(kix|cli\\.ann)(,i\\d*(,w\\d*)?,[+-])+,i\\d*(,w\\d*)?,\\d{4}");
    static final Pattern REPLACEMENT_REGEX = Pattern.compile("i((?:\\d)+)(?=,(((([\\+\\-]){1}|i(\\d)+),)|(\\d)+))");
    static final String REPLACEMENT = "i$1,w$1";

    @Override
    public TsData compute(final String formula, final TsVariables indices, final TsVariables weights) {
        String lowerCaseFormula = formula.toLowerCase().replaceAll("\\s*", "");
        if (!SYNTAX.matcher(lowerCaseFormula).matches()) {
            setErrorMessage("Formula doesn't match required syntax");
            return null;
        }
        lowerCaseFormula = addMissingWeights(lowerCaseFormula);

        String[] splitFormula = lowerCaseFormula.split(",");

        if (!isDataAvailable(splitFormula, indices, weights)
                || !isDataMeetingAssumptions(splitFormula, indices, weights)) {
            return null;
        }

        int refYear = Integer.parseInt(splitFormula[splitFormula.length - 1]);

        boolean displayFirstYear = NbPreferences.forModule(AnnOptionsPanelController.class).getBoolean(AnnOptionsPanelController.ANN_DEFAULT_METHOD, true);

        ICalc calculator = null;
        for (int i = 1; i < splitFormula.length; i += 3) {
            TsData addData = extractData(indices.get(splitFormula[i]));
            TsData addWeights = extractData(weights.get(splitFormula[i + 1]));

            if (calculator == null) {
                calculator = new CliAnnCalc(addData, addWeights, refYear, displayFirstYear);
            } else if (splitFormula[i - 1].equals("+")) {
                calculator.plus(addData, addWeights);
            } else {
                calculator.minus(addData, addWeights);
            }
        }

        return calculator.getResult();
    }

    @Override
    public String[] getValidControlCharacter() {
        return VALID_CONTROL_CHARACTER.clone();
    }

    private String addMissingWeights(final String input) {
        return REPLACEMENT_REGEX.matcher(input).replaceAll(REPLACEMENT);
    }

    @NbBundle.Messages({
        "# {0} - index series name",
        "# {1} - weighted series name",
        "ERR_CHECKDATA_IndexStartsBeforeWeights=The index series {0} begins before the corresponding weight series {1}."})
    /**
     *
     * @param formula
     * @param j
     *
     */
    private boolean isDataMeetingAssumptions(final String[] input, final TsVariables indices, final TsVariables weights) {
        Set<String> errors = new HashSet<>();

        for (int i = 1; i < input.length; i += 3) {
            TsPeriod indexStart = indices.get(input[i]).getDefinitionDomain().getStart();
            TsPeriod weightStart = weights.get(input[i + 1]).getDefinitionDomain().getStart();

            if (indexStart.isBefore(weightStart)) {
                errors.add(Bundle.ERR_CHECKDATA_IndexStartsBeforeWeights(input[i], input[i + 1]));
            }
        }

        if (!errors.isEmpty()) {
            setErrorMessage(errors.stream().collect(Collectors.joining("\n")));
        }
        return errors.isEmpty();

    }

}
