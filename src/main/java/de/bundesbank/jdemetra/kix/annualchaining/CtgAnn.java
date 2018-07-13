package de.bundesbank.jdemetra.kix.annualchaining;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static de.bundesbank.jdemetra.kix.annualchaining.CliAnn.REPLACEMENT;
import static de.bundesbank.jdemetra.kix.annualchaining.CliAnn.REPLACEMENT_REGEX;
import de.bundesbank.jdemetra.kix.annualchaining.core.CtgAnnCalc;
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
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Deutsche Bundesbank
 */
@ServiceProvider(service = IParser.class)
public final class CtgAnn extends AbstractParser {

    private static final int CONTRIBUTOR_POSITION = 1;
    private static final int CONTRIBUTOR_WEIGHTS_POSITION = 2;
    private static final int OPERATOR_POSITION = 3;
    private static final int TOTAL_POSITION = 4;
    private static final int TOTAL_WEIGHTS_POSITION = 5;

    private static final String[] VALID_CONTROL_CHARACTER = {"wbg", "ctg.ann"};
    private static final Pattern SYNTAX = Pattern.compile("(wbg|ctg\\.ann),i\\d*(,w\\d*)?,[+-],i\\d*(,w\\d*)?,\\d{1,3}");

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

        int lag = Integer.parseInt(splitFormula[splitFormula.length - 1]);
        TsData contributorData = extractData(indices.get(splitFormula[CONTRIBUTOR_POSITION]));
        TsData contributorWeights = extractData(weights.get(splitFormula[CONTRIBUTOR_WEIGHTS_POSITION]));
        TsData totalData = extractData(indices.get(splitFormula[TOTAL_POSITION]));
        TsData totalWeights = extractData(weights.get(splitFormula[TOTAL_WEIGHTS_POSITION]));

        ICalc calc = new CtgAnnCalc(totalData, totalWeights, lag);

        if (splitFormula[OPERATOR_POSITION].equalsIgnoreCase("-")) {
            calc.minus(contributorData, contributorWeights);
        } else {
            calc.plus(contributorData, contributorWeights);
        }

        return calc.getResult();
    }

    @Override
    public String[] getValidControlCharacter() {
        return VALID_CONTROL_CHARACTER.clone();
    }

    private String addMissingWeights(final String input) {
        return REPLACEMENT_REGEX.matcher(input).replaceAll(REPLACEMENT);
    }

    @NbBundle.Messages({
        "# {0} - contributing index series name",
        "# {1} - total index series name",
        "ERR_CHECKDATA_ContributorShouldNotBeginAfterTotal=The contributing index series ({0}) should not begin after the total index series ({1})."})
    /**
     *
     * @param formula
     * @param j
     *
     */
    private boolean isDataMeetingAssumptions(final String[] input, final TsVariables indices, final TsVariables weights) {
        Set<String> errors = new HashSet<>();

        TsPeriod contributorStart = indices.get(input[CONTRIBUTOR_POSITION]).getDefinitionDomain().getStart();
        TsPeriod contributorWeightsStart = weights.get(input[CONTRIBUTOR_WEIGHTS_POSITION]).getDefinitionDomain().getStart();
        TsPeriod totalStart = indices.get(input[TOTAL_POSITION]).getDefinitionDomain().getStart();
        TsPeriod totalWeightsStart = weights.get(input[TOTAL_WEIGHTS_POSITION]).getDefinitionDomain().getStart();

        if (contributorStart.isBefore(contributorWeightsStart)) {
            errors.add(Bundle.ERR_CHECKDATA_IndexStartsBeforeWeights(input[CONTRIBUTOR_POSITION], input[CONTRIBUTOR_WEIGHTS_POSITION]));
        }

        if (totalStart.isBefore(totalWeightsStart)) {
            errors.add(Bundle.ERR_CHECKDATA_IndexStartsBeforeWeights(input[TOTAL_POSITION], input[TOTAL_WEIGHTS_POSITION]));
        }
        if (contributorStart.isAfter(totalStart)) {
            errors.add(Bundle.ERR_CHECKDATA_ContributorShouldNotBeginAfterTotal(input[CONTRIBUTOR_POSITION], input[TOTAL_POSITION]));
        }

        if (!errors.isEmpty()) {
            setErrorMessage(errors.stream().collect(Collectors.joining("\n")));
        }
        return errors.isEmpty();

    }
}
