/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bundesbank.jdemetra.kix.annualchaining;

import de.bundesbank.jdemetra.kix.annualchaining.options.AnnOptionsPanelController;
import static de.bundesbank.jdemetra.kix.annualchaining.options.AnnOptionsPanelController.ANN_DEFAULT_METHOD;
import ec.tstoolkit.timeseries.regression.TsVariable;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.utilities.DefaultNameValidator;
import org.junit.Assert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.openide.util.NbPreferences;

/**
 *
 * @author Deutsche Bundesbank
 */
public class CliAnnTest {

    public CliAnnTest() {
    }

    private final String VALIDATOR = ",= +-";
    private final double[] i1Data = {89.78, 93.71, 90.85, 95.20, 95.47, 98.29, 100.15, 104.76, 107.69, 109.16, 112.40, 122.26,
        119.73, 120.83, 122.45, 128.06, 128.39, 127.53, 125.12, 118.38, 102.32, 100.80, 105.21, 109.90};
    private final double[] w1Data = {175.09, 184.14, 178.95, 187.45, 188.09, 194.33, 197.97, 207.50, 214.56, 219.10, 225.54, 245.62,
        241.17, 244.79, 247.22, 257.58, 259.71, 260.26, 256.27, 240.33, 204.06, 199.94, 208.43, 218.29};
    private final double[] i2Data = {81.19, 93.96, 95.71, 97.31, 89.17, 94.84, 104.22, 111.00, 99.15, 115.17, 113.44, 132.40, 112.77,
        118.62, 121.56, 131.39, 121.06, 121.56, 125.29, 139.41, 128.07, 129.15, 125.46, 135.75};
    private final double[] w2Data = {25.15, 29.51, 30.30, 30.85, 28.51, 30.68, 32.95, 36.13, 31.86, 37.94, 37.00, 42.43, 36.29,
        38.60, 40.18, 43.60, 40.62, 40.57, 42.46, 46.63, 40.28, 39.44, 39.73, 44.00};
    private final TsVariables indices = new TsVariables("i", new DefaultNameValidator(VALIDATOR));
    private final TsVariables weights = new TsVariables("w", new DefaultNameValidator(VALIDATOR));
    CliAnn instance = new CliAnn();

    private void quarterlyData() {
        indices.clear();
        weights.clear();
        indices.set("i1", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, i1Data, true)));
        indices.set("i2", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, i2Data, true)));
        weights.set("w1", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, w1Data, true)));
        weights.set("w2", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, w2Data, true)));
    }

    @Test
    public void testCompute_Add2TsPragmatic() {
        String formula = "KIX,i1,w1,+,i2,w2,2005";
        quarterlyData();
        showLastYear(true);
        double[] expResultData = {88.59, 93.74, 91.52, 95.49, 94.60, 97.81, 100.71, 105.62, 106.50, 110.00, 112.55, 123.68,
            118.76, 120.52, 122.33, 128.52, 127.37, 126.70, 125.15, 121.32, 105.96, 104.81, 108.07, 113.56};
        TsData expResult = new TsData(TsFrequency.Quarterly, 2004, 0, expResultData, false);
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);
    }

    @Test
    public void testCompute_Add2TsPuristic() {
        String formula = "KIX,i1,w1,+,i2,w2,2005";
        quarterlyData();
        showLastYear(false);
        double[] expResultData = {94.60, 97.81, 100.71, 105.62, 106.50, 110.00, 112.55, 123.68,
            118.76, 120.52, 122.33, 128.52, 127.37, 126.70, 125.15, 121.32, 105.96, 104.81, 108.07, 113.56};
        TsData expResult = new TsData(TsFrequency.Quarterly, 2005, 0, expResultData, false);
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);
    }

    @Test
    public void testCompute_Subtract2TsPragmatic() {
        String formula = "KIX,i1,w1,-,i2,w2,2005";
        quarterlyData();
        showLastYear(true);
        double[] expResultData = {91.42, 93.66, 89.92, 94.8, 96.67, 98.95, 99.37, 103.57, 109.35, 107.99, 112.2, 120.29, 121.07,
            121.25, 122.62, 127.42, 129.81, 128.68, 125.08, 114.3, 97.24, 95.21, 101.21, 104.8};
        TsData expResult = new TsData(TsFrequency.Quarterly, 2004, 0, expResultData, false);
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);
    }

    @Test
    public void testCompute_Subtract2TsPuristic() {
        String formula = "KIX,i1,w1,-,i2,w2,2005";
        quarterlyData();
        showLastYear(false);
        double[] expResultData = {96.67, 98.95, 99.37, 103.57, 109.35, 107.99, 112.2, 120.29, 121.07,
            121.25, 122.62, 127.42, 129.81, 128.68, 125.08, 114.3, 97.24, 95.21, 101.21, 104.8};
        TsData expResult = new TsData(TsFrequency.Quarterly, 2005, 0, expResultData, false);
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);
    }

    @Test
    public void testCompute_NullTestPragmatic() {
        String formula = "KIX,i1,w1,+,i2,w2,-,i2,w2,2005";
        quarterlyData();
        showLastYear(true);
        double[] expResultData = {89.78, 93.71, 90.85, 95.20, 95.47, 98.29, 100.15, 104.76, 107.69, 109.16, 112.40, 122.26,
            119.73, 120.83, 122.45, 128.06, 128.39, 127.53, 125.12, 118.38, 102.32, 100.80, 105.21, 109.90};
        TsData expResult = new TsData(TsFrequency.Quarterly, 2004, 0, expResultData, false);
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);
    }

    @Test
    public void testCompute_NullTestPuristic() {
        String formula = "KIX,i1,w1,+,i2,w2,-,i2,w2,2005";
        quarterlyData();
        showLastYear(false);
        double[] expResultData = {95.47, 98.29, 100.15, 104.76, 107.69, 109.16, 112.40, 122.26,
            119.73, 120.83, 122.45, 128.06, 128.39, 127.53, 125.12, 118.38, 102.32, 100.80, 105.21, 109.90};
        TsData expResult = new TsData(TsFrequency.Quarterly, 2005, 0, expResultData, false);
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);
    }

    @Test
    public void testCompute_MissingData() {
        String formula = "KIX,i1,w1,+,i2,w2,2005";
        indices.clear();
        weights.clear();
        TsData expResult = null;
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);

        String expErrorMessage = "The following data is not available: i1 i2 w1 w2";
        String resultErrorMessage = instance.getErrorMessage();
        Assert.assertEquals(expErrorMessage, resultErrorMessage);
    }

    @Test
    public void testCompute_DataNotMeetingAssumptions() {
        String formula = "KIX,i1,w1,+,i2,w2,2005";

        indices.clear();
        weights.clear();
        indices.set("i1", new TsVariable(new TsData(TsFrequency.Quarterly, 2003, 0, i1Data, true)));
        indices.set("i2", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, i2Data, true)));
        weights.set("w1", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, w1Data, true)));
        weights.set("w2", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, w2Data, true)));

        TsData expResult = null;
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);

        String expErrorMessage = "The index series i1 begins before the corresponding weight series w1.";
        String resultErrorMessage = instance.getErrorMessage();
        Assert.assertEquals(expErrorMessage, resultErrorMessage);
    }

    @Test
    public void testCompute_DataNotMeetingAssumptions2() {
        String formula = "KIX,i1,w1,+,i2,w2,2005";

        indices.clear();
        weights.clear();
        indices.set("i1", new TsVariable(new TsData(TsFrequency.Quarterly, 2003, 0, i1Data, true)));
        indices.set("i2", new TsVariable(new TsData(TsFrequency.Quarterly, 2003, 0, i2Data, true)));
        weights.set("w1", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, w1Data, true)));
        weights.set("w2", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, w2Data, true)));

        TsData expResult = null;
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);

        String expErrorMessage = "The index series i1 begins before the corresponding weight series w1.\n"
                + "The index series i2 begins before the corresponding weight series w2.";
        String resultErrorMessage = instance.getErrorMessage();
        Assert.assertEquals(expErrorMessage, resultErrorMessage);
    }

    @Test
    public void testCompute_WrongFormula() {
        System.out.println("Start Compute_WrongFormula");

        String formula = "UNC.ANN,w1,2005";

        TsData expResultTsData = null;
        TsData resultTsData = instance.compute(formula, indices, weights);

        String expResultErrorMessage = "Formula doesn't match required syntax";
        TsDataAsserter.assertTsDataEquals(expResultTsData, resultTsData, 0.005);
        assertEquals(expResultErrorMessage, instance.getErrorMessage());

        System.out.println("End Compute_WrongFormula");
    }

    @Test
    public void testGetValidControlCharacter() {
        String[] expResult = {"kix", "cli.ann"};
        String[] result = instance.getValidControlCharacter();
        assertArrayEquals(expResult, result);
    }

    private void showLastYear(boolean showLastYear) {
        NbPreferences.forModule(AnnOptionsPanelController.class).putBoolean(ANN_DEFAULT_METHOD, showLastYear);

    }

}
