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
import org.junit.Assert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Deutsche Bundesbank
 */
public class CtgAnnTest {

    public CtgAnnTest() {
    }

    private final String VALIDATOR = ",= +-";
    double[] i1Data = {78.0876494023904, 79.6812749003984, 79.6812749003984, 81.2749003984064,
        86.0557768924303, 89.2430278884462, 90.8366533864542, 92.4302788844621,
        95.6175298804781, 98.8047808764940, 103.5856573705180, 101.9920318725100,
        107.4103585657370, 110.5976095617530, 113.9442231075700, 117.4501992031870,
        121.1155378486060, 124.7808764940240, 128.4462151394420, 132.4302788844620,
        136.2549800796810, 140.5577689243030, 144.7011952191230, 149.0039840637450};

    double[] w1Data = {392.0, 400.0, 400.0, 408.0, 496.8, 442.4, 461.7, 290.0,
        456.0, 446.4, 442.0, 416.0, 411.14, 395.58, 378.95, 368.50,
        342.00, 336.69, 306.28, 290.85, 290.70, 273.42, 254.24, 252.45};

    double[] i2Data = {85.9460112133865, 96.9568880948168, 101.7486585895130, 86.9655368505560,
        89.1065406886119, 100.6271803886270, 106.2345713930590, 91.4514496541017,
        92.7668279813041, 103.4411654246170, 109.4984829918590, 94.2935236022205,
        96.5776233160815, 106.5605420449900, 112.0604990089810, 99.6010948882151,
        99.9401584839644, 109.0750515454860, 113.9548226021790, 101.9086661372410,
        101.7462386055560, 110.5700305355260, 115.0658729354940, 103.2949213935240};

    double[] w2Data = {843.00, 951.00, 998.00, 853.00, 951.60, 1114.40, 1091.70, 906.00,
        978.25, 1196.40, 1145.46, 1096.50, 1121.94, 1306.64, 1230.05, 1176.50,
        1184.78, 1386.89, 1260.42, 1189.41, 1205.70, 1378.52, 1293.22, 1266.85};

    private final TsVariables indices = new TsVariables("i", new DefaultNameValidator(VALIDATOR));
    private final TsVariables weights = new TsVariables("w", new DefaultNameValidator(VALIDATOR));
    CtgAnn instance = new CtgAnn();

    private void quarterlyData() {
        indices.clear();
        weights.clear();
        indices.set("i1", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, i1Data, true)));
        indices.set("i2", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, i2Data, true)));
        weights.set("w1", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, w1Data, true)));
        weights.set("w2", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, w2Data, true)));
    }

    @Test
    public void testCompute_ctgAdd() {
        String formula = "ctg.ann,i1,w1,+,i2,w2,1";
        quarterlyData();
        double[] expResultData = {0.948991696322642, 0.000000000000000, 0.801603206412826, 2.813599062133630,
            1.830663615560650, 0.810536980749720, 0.767754318618030, 1.566877038470200,
            1.544659656000100, 2.077893883101840, -0.654315807317998, 2.290369914939000,
            1.315412790230850, 1.251789925690840, 1.247035184493320, 1.091800973294730,
            1.088096857056910, 0.996970166865571, 1.037258620651170, 0.799454776733306,
            0.900822403184864, 0.798233032353246, 0.796546263782846};
        TsData expResult = new TsData(TsFrequency.Quarterly, 2004, 1, expResultData, false);
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);
    }

    @Test
    public void testCompute_ctgMinus() {
        String formula = "ctg.ann,i1,w1,-,i2,w2,1";
        quarterlyData();
        double[] expResultData = {-0.948991696322642, 0.000000000000000, -0.801603206412826, -2.813599062133630,
            -1.830663615560650, -0.810536980749720, -0.767754318618030, -1.566877038470200,
            -1.544659656000100, -2.077893883101840, 0.654315807317998, -2.290369914939000,
            -1.315412790230850, -1.251789925690840, -1.247035184493320, -1.091800973294730,
            -1.088096857056910, -0.996970166865571, -1.037258620651170, -0.799454776733306,
            -0.900822403184864, -0.798233032353246, -0.796546263782846};
        TsData expResult = new TsData(TsFrequency.Quarterly, 2004, 1, expResultData, false);
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);
    }

    @Test
    public void testCompute_MissingData() {
        String formula = "WBG,i1,w1,+,i2,w2,1";
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
        String formula = "ctg.ann,i1,w1,+,i2,w2,1";

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
        String formula = "ctg.ann,i1,w1,+,i2,w2,1";

        indices.clear();
        weights.clear();
        indices.set("i1", new TsVariable(new TsData(TsFrequency.Quarterly, 2003, 0, i1Data, true)));
        indices.set("i2", new TsVariable(new TsData(TsFrequency.Quarterly, 2003, 0, i2Data, true)));
        weights.set("w1", new TsVariable(new TsData(TsFrequency.Quarterly, 2003, 0, w1Data, true)));
        weights.set("w2", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, w2Data, true)));

        TsData expResult = null;
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);

        String expErrorMessage = "The index series i2 begins before the corresponding weight series w2.";
        String resultErrorMessage = instance.getErrorMessage();
        Assert.assertEquals(expErrorMessage, resultErrorMessage);
    }

    @Test
    public void testCompute_DataNotMeetingAssumptions3() {
        String formula = "ctg.ann,i1,w1,+,i2,w2,1";

        indices.clear();
        weights.clear();
        indices.set("i1", new TsVariable(new TsData(TsFrequency.Quarterly, 2005, 0, i1Data, true)));
        indices.set("i2", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, i2Data, true)));
        weights.set("w1", new TsVariable(new TsData(TsFrequency.Quarterly, 2005, 0, w1Data, true)));
        weights.set("w2", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, w2Data, true)));

        TsData expResult = null;
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);

        String expErrorMessage = "The contributing index series (i1) should not begin after the total index series (i2).";
        String resultErrorMessage = instance.getErrorMessage();
        Assert.assertEquals(expErrorMessage, resultErrorMessage);
    }

    @Test
    public void testCompute_DataNotMeetingAssumptionsAll() {
        String formula = "ctg.ann,i1,w1,+,i2,w2,1";

        indices.clear();
        weights.clear();
        indices.set("i1", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, i1Data, true)));
        indices.set("i2", new TsVariable(new TsData(TsFrequency.Quarterly, 2003, 0, i2Data, true)));
        weights.set("w1", new TsVariable(new TsData(TsFrequency.Quarterly, 2005, 0, w1Data, true)));
        weights.set("w2", new TsVariable(new TsData(TsFrequency.Quarterly, 2004, 0, w2Data, true)));

        TsData expResult = null;
        TsData result = instance.compute(formula, indices, weights);
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.005);

        String expErrorMessage = "The index series i1 begins before the corresponding weight series w1.\n"
                + "The contributing index series (i1) should not begin after the total index series (i2).\n"
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
        String[] expResult = {"wbg", "ctg.ann"};
        String[] result = instance.getValidControlCharacter();
        assertArrayEquals(expResult, result);
    }

}
