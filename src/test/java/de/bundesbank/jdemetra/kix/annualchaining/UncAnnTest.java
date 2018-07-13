/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bundesbank.jdemetra.kix.annualchaining;

import ec.tstoolkit.timeseries.regression.TsVariable;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.utilities.DefaultNameValidator;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Deutsche Bundesbank
 */
public class UncAnnTest {

    public UncAnnTest() {
    }
    private final String VALIDATOR = ",= +-";
    private final double[] i1Data = {89.78, 93.71, 90.85, 95.20, 95.47, 98.29, 100.15, 104.76, 107.69, 109.16, 112.40, 122.26,
                                     119.73, 120.83, 122.45, 128.06, 128.39, 127.53, 125.12, 118.38, 102.32, 100.80, 105.21, 109.90};
    private final TsVariables indices = new TsVariables("i", new DefaultNameValidator(VALIDATOR));
    private final TsVariables weights = new TsVariables("w", new DefaultNameValidator(VALIDATOR));

    @Test
    public void testCompute_UnchainIndex() {
        System.out.println("Start Compute_UnchainIndex");
        indices.clear();
        weights.clear();
        indices.set("i1", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, i1Data, true)));

        String formula = "UNC.ANN,i1";
        UncAnn instance = new UncAnn();
        TsData expResult = new TsData(TsFrequency.Quarterly, 2004, 0, new double[]{97.18, 101.43, 98.34, 103.05, 103.34, 106.39, 108.41, 113.40,
                                                                                   108.05, 109.52, 112.77, 122.67, 106.07, 107.05, 108.48, 113.45,
                                                                                   104.58, 103.88, 101.92, 96.43, 81.95, 80.73, 84.27, 88.02},
                                      true);

        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);

        System.out.println("End Compute_UnchainIndex");
    }

    @Test
    public void testCompute_UnchainWeight() {
        System.out.println("Start Compute_UnchainWeight");
        indices.clear();
        weights.clear();
        weights.set("w1", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, i1Data, true)));

        String formula = "UNC.ANN,w1";
        UncAnn instance = new UncAnn();
        TsData expResult = new TsData(TsFrequency.Quarterly, 2004, 0, new double[]{97.18, 101.43, 98.34, 103.05, 103.34, 106.39, 108.41, 113.40,
                                                                                   108.05, 109.52, 112.77, 122.67, 106.07, 107.05, 108.48, 113.45,
                                                                                   104.58, 103.88, 101.92, 96.43, 81.95, 80.73, 84.27, 88.02},
                                      true);

        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);

        System.out.println("End Compute_UnchainWeight");
    }

    @Test
    public void testCompute_UnchainNotFound() {
        System.out.println("Start Compute_UnchainNotFound");
        indices.clear();
        weights.clear();

        String formula = "UNC.ANN,w1";
        UncAnn instance = new UncAnn();

        TsData expResultTsData = null;
        TsData resultTsData = instance.compute(formula, indices, weights);

        String expResultErrorMessage = "w1 doesn't exist";
        TsDataAsserter.assertTsDataEquals(expResultTsData, resultTsData, 0.005);
        assertEquals(expResultErrorMessage, instance.getErrorMessage());

        System.out.println("End Compute_UnchainNotFound");
    }

    @Test
    public void testCompute_UnchainWrongFormula() {
        System.out.println("Start Compute_UnchainNotFound");
        indices.clear();
        weights.clear();

        String formula = "UNC.ANN,w1,2005";
        UncAnn instance = new UncAnn();

        TsData expResultTsData = null;
        TsData resultTsData = instance.compute(formula, indices, weights);

        String expResultErrorMessage = "Formula doesn't match required syntax";
        TsDataAsserter.assertTsDataEquals(expResultTsData, resultTsData, 0.005);
        assertEquals(expResultErrorMessage, instance.getErrorMessage());

        System.out.println("End Compute_UnchainNotFound");
    }

    @Test
    public void testGetValidControlCharacter() {
        System.out.println("getValidControlCharacter");
        UncAnn instance = new UncAnn();
        String[] expResult = {"unc.ann"};
        String[] result = instance.getValidControlCharacter();
        assertArrayEquals(expResult, result);
    }

}
