/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bundesbank.jdemetra.kix.annualchaining.core;

import de.bundesbank.jdemetra.kix.annualchaining.TsDataAsserter;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import org.junit.Test;

/**
 *
 * @author Deutsche Bundesbank
 */
public class CliAnnCalcTest {

    private final double[] i1Data = {89.78, 93.71, 90.85, 95.20, 95.47, 98.29, 100.15, 104.76, 107.69, 109.16, 112.40, 122.26,
        119.73, 120.83, 122.45, 128.06, 128.39, 127.53, 125.12, 118.38, 102.32, 100.80, 105.21, 109.90};
    private final double[] w1Data = {175.09, 184.14, 178.95, 187.45, 188.09, 194.33, 197.97, 207.50, 214.56, 219.10, 225.54, 245.62,
        241.17, 244.79, 247.22, 257.58, 259.71, 260.26, 256.27, 240.33, 204.06, 199.94, 208.43, 218.29};
    private final double[] i2Data = {81.19, 93.96, 95.71, 97.31, 89.17, 94.84, 104.22, 111.00, 99.15, 115.17, 113.44, 132.40, 112.77,
        118.62, 121.56, 131.39, 121.06, 121.56, 125.29, 139.41, 128.07, 129.15, 125.46, 135.75};
    private final double[] w2Data = {25.15, 29.51, 30.30, 30.85, 28.51, 30.68, 32.95, 36.13, 31.86, 37.94, 37.00, 42.43, 36.29,
        38.60, 40.18, 43.60, 40.62, 40.57, 42.46, 46.63, 40.28, 39.44, 39.73, 44.00};

    TsData i1 = new TsData(TsFrequency.Quarterly, 2004, 0, i1Data, true);
    TsData i2 = new TsData(TsFrequency.Quarterly, 2004, 0, i2Data, true);
    TsData w1 = new TsData(TsFrequency.Quarterly, 2004, 0, w1Data, true);
    TsData w2 = new TsData(TsFrequency.Quarterly, 2004, 0, w2Data, true);

    public CliAnnCalcTest() {
    }

    @Test
    public void testGetResultWithFirstYear() {
        System.out.println("GetResultWithFirstYear");
        CliAnnCalc instance = new CliAnnCalc(i1, w1, 2005, true);
        TsData expResult = i1;
        TsData result = instance.getResult();
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.0000005);
    }

    @Test
    public void testGetResultWithoutFirstYear() {
        System.out.println("GetResultWithoutFirstYear");
        CliAnnCalc instance = new CliAnnCalc(i1, w1, 2005, false);
        TsData expResult = i1.drop(4, 0);
        TsData result = instance.getResult();
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.0000005);
    }

    @Test
    public void testPlusWithFirstYear() {
        System.out.println("PlusWithFirstYear");
        TsData addData = i2;
        TsData addWeights = w2;
        CliAnnCalc instance = new CliAnnCalc(i1, w1, 2005, true);

        instance.plus(addData, addWeights);

        double[] expData = {88.59, 93.74, 91.52, 95.49, 94.60, 97.81, 100.71, 105.62, 106.50, 110.00, 112.55, 123.68,
            118.76, 120.52, 122.33, 128.52, 127.37, 126.70, 125.15, 121.32, 105.96, 104.81, 108.07, 113.56};
        TsData expResult = new TsData(TsFrequency.Quarterly, 2004, 0, expData, false);
        TsData result = instance.getResult();
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.05);
    }

    @Test
    public void testPlusWithoutFirstYear() {
        System.out.println("PlusWithoutFirstYear");
        TsData addData = i2;
        TsData addWeights = w2;
        CliAnnCalc instance = new CliAnnCalc(i1, w1, 2005, false);

        instance.plus(addData, addWeights);

        double[] expData = {94.60, 97.81, 100.71, 105.62, 106.50, 110.00, 112.55, 123.68, 118.76, 120.52,
            122.33, 128.52, 127.37, 126.70, 125.15, 121.32, 105.96, 104.81, 108.07, 113.56};
        TsData expResult = new TsData(TsFrequency.Quarterly, 2005, 0, expData, false);
        TsData result = instance.getResult();
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.05);
    }

    @Test
    public void testMinusWithFirstYear() {
        System.out.println("MinusWithFirstYear");
        TsData addData = i2;
        TsData addWeights = w2;
        CliAnnCalc instance = new CliAnnCalc(i1, w1, 2005, true);

        instance.minus(addData, addWeights);

        double[] expData = {91.42, 93.66, 89.92, 94.8, 96.67, 98.95, 99.37, 103.57, 109.35, 107.99, 112.2, 120.29, 121.07,
            121.25, 122.62, 127.42, 129.81, 128.68, 125.08, 114.3, 97.24, 95.21, 101.21, 104.8};
        TsData expResult = new TsData(TsFrequency.Quarterly, 2004, 0, expData, false);
        TsData result = instance.getResult();
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.05);
    }

    @Test
    public void testMinusWithoutFirstYear() {
        System.out.println("MinusWithoutFirstYear");
        TsData addData = i2;
        TsData addWeights = w2;
        CliAnnCalc instance = new CliAnnCalc(i1, w1, 2005, false);

        instance.minus(addData, addWeights);

        double[] expData = {96.67, 98.95, 99.37, 103.57, 109.35, 107.99, 112.2, 120.29, 121.07,
            121.25, 122.62, 127.42, 129.81, 128.68, 125.08, 114.3, 97.24, 95.21, 101.21, 104.8};
        TsData expResult = new TsData(TsFrequency.Quarterly, 2005, 0, expData, false);
        TsData result = instance.getResult();
        TsDataAsserter.assertTsDataEquals(expResult, result, 0.05);
    }

}
