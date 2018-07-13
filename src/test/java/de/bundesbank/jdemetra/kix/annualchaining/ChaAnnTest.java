package de.bundesbank.jdemetra.kix.annualchaining;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import ec.tstoolkit.timeseries.regression.TsVariable;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.utilities.DefaultNameValidator;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Deutsche Bundesbank
 */
public class ChaAnnTest {

    public ChaAnnTest() {
    }
    private final String VALIDATOR = ",= +-";
    private final double[] i1Data = {95.9455678781636, 101.5235478245180, 99.1155647893106, 103.4153195080070,
                                     102.4501592599300, 105.9303326930090, 109.0691592281930, 114.3861870989490,
                                     106.8300670944030, 110.3457312146300, 112.8987184226080, 124.0662022472970,
                                     104.9318938491780, 106.4883823302890, 108.0821970617710, 113.5583078139040,
                                     103.9447394183710, 103.3979307247260, 102.1310926275610, 99.0086432985022,
                                     84.6808550674616, 83.7602791011846, 86.3681995465272, 90.7496460061146};
    private final TsVariables indices = new TsVariables("i", new DefaultNameValidator(VALIDATOR));
    private final TsVariables weights = new TsVariables("w", new DefaultNameValidator(VALIDATOR));

    @Before
    public void clearTsVariables() {
        indices.clear();
        weights.clear();
    }

    @Test
    public void testCompute_ChainIndex() {
        System.out.println("Start Compute_ChainIndex");
        indices.set("i1", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, i1Data, true)));

        String formula = "Cha.ANN,i1";
        ChaAnn instance = new ChaAnn();
        TsData expResult = new TsData(TsFrequency.Quarterly, 2004, 0, new double[]{95.9455678781636, 101.5235478245180, 99.1155647893106, 103.4153195080070,
                                                                                   102.4501592599300, 105.9303326930090, 109.0691592281930, 114.3861870989490,
                                                                                   115.3326289430720, 119.1281033492460, 121.8842817769350, 133.9405811242200,
                                                                                   128.6164900765570, 130.5242997799890, 132.4778607906210, 139.1900063392590,
                                                                                   137.9369188286730, 137.2112918579180, 135.5301702854790, 131.3866124483150,
                                                                                   114.7563178624400, 113.5087878496830, 117.0429437972970, 122.9805156630170},
                                      true);

        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);

        System.out.println("End Compute_ChainIndex");
    }

    @Test
    public void testCompute_ChainIndexWithRefYear() {
        System.out.println("Start Compute_ChainIndexWithRefYear");
        indices.set("i1", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, i1Data, true)));

        String formula = "Cha.ANN,i1,2005";
        ChaAnn instance = new ChaAnn();
        TsData expResult = new TsData(TsFrequency.Quarterly, 2004, 0, new double[]{88.87226059, 94.03902022, 91.80855872, 95.79132656,
                                                                                   94.89731993, 98.12092773, 101.0283534, 105.9533989,
                                                                                   106.8300671, 110.3457312, 112.8987184, 124.0662022,
                                                                                   119.1346143, 120.9017763, 122.7113167, 128.9286289,
                                                                                   127.7679216, 127.0957894, 125.5386036, 121.7005175,
                                                                                   106.2962429, 105.1406834, 108.414294, 113.9141357},
                                      true);

        TsData result = instance.compute(formula, indices, weights);

        TsDataAsserter.assertTsDataEquals(expResult, result,
                                          0.00000005);

        System.out.println(
                "End Compute_ChainIndexWithRefYear");
    }

    @Test
    public void testCompute_ChainWeight() {
        System.out.println("Start Compute_ChainWeight");
        weights.set("w1", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, i1Data, true)));

        String formula = "Cha.ANN,w1";
        ChaAnn instance = new ChaAnn();
        TsData expResult = new TsData(TsFrequency.Quarterly, 2004, 0, new double[]{95.9455678781636, 101.5235478245180, 99.1155647893106, 103.4153195080070,
                                                                                   102.4501592599300, 105.9303326930090, 109.0691592281930, 114.3861870989490,
                                                                                   115.3326289430720, 119.1281033492460, 121.8842817769350, 133.9405811242200,
                                                                                   128.6164900765570, 130.5242997799890, 132.4778607906210, 139.1900063392590,
                                                                                   137.9369188286730, 137.2112918579180, 135.5301702854790, 131.3866124483150,
                                                                                   114.7563178624400, 113.5087878496830, 117.0429437972970, 122.9805156630170},
                                      true);

        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);

        System.out.println("End Compute_ChainWeight");
    }

    @Test
    public void testCompute_ChainNotFound() {
        System.out.println("Start Compute_ChainNotFound");

        String formula = "CHA.ANN,w1";
        ChaAnn instance = new ChaAnn();

        TsData expResultTsData = null;
        TsData resultTsData = instance.compute(formula, indices, weights);

        String expResultErrorMessage = "w1 doesn't exist";
        TsDataAsserter.assertTsDataEquals(expResultTsData, resultTsData, 0.005);
        assertEquals(expResultErrorMessage, instance.getErrorMessage());

        System.out.println("End Compute_ChainNotFound");
    }

    @Test
    public void testGetValidControlCharacter() {
        System.out.println("getValidControlCharacter");
        ChaAnn instance = new ChaAnn();
        String[] expResult = {"cha.ann"};
        String[] result = instance.getValidControlCharacter();
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testCompute_ChainWrongFormula() {
        System.out.println("Start Compute_ChainWrongFormula");

        String formula = "UNC.ANN,w1,2005";
        ChaAnn instance = new ChaAnn();

        TsData expResultTsData = null;
        TsData resultTsData = instance.compute(formula, indices, weights);

        String expResultErrorMessage = "Formula doesn't match required syntax";
        TsDataAsserter.assertTsDataEquals(expResultTsData, resultTsData, 0.005);
        assertEquals(expResultErrorMessage, instance.getErrorMessage());

        System.out.println("End Compute_ChainWrongFormula");
    }

    @Test
    public void testCompute_ChainWhitespacesInFormula() {
        System.out.println("Start Compute_ChainWhitespacesInFormula");

        weights.set("w1", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, i1Data, true)));

        String formula = "Cha.ANN,   w1";
        ChaAnn instance = new ChaAnn();
        TsData expResult = new TsData(TsFrequency.Quarterly, 2004, 0, new double[]{95.9455678781636, 101.5235478245180, 99.1155647893106, 103.4153195080070,
                                                                                   102.4501592599300, 105.9303326930090, 109.0691592281930, 114.3861870989490,
                                                                                   115.3326289430720, 119.1281033492460, 121.8842817769350, 133.9405811242200,
                                                                                   128.6164900765570, 130.5242997799890, 132.4778607906210, 139.1900063392590,
                                                                                   137.9369188286730, 137.2112918579180, 135.5301702854790, 131.3866124483150,
                                                                                   114.7563178624400, 113.5087878496830, 117.0429437972970, 122.9805156630170},
                                      true);

        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);

        System.out.println("End Compute_ChainWhitespacesInFormula");
    }

}
