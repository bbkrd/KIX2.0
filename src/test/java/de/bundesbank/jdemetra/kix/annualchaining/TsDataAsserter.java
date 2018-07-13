/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bundesbank.jdemetra.kix.annualchaining;

import ec.tstoolkit.timeseries.simplets.TsData;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Deutsche Bundesbank
 */
public class TsDataAsserter {

    static public void assertTsDataEquals(TsData expected, TsData actual, double delta) {
        if (expected == null && actual == null) {
            return;
        }
        assertEquals(expected.getDomain(), actual.getDomain());
        assertArrayEquals(expected.internalStorage(), actual.internalStorage(), delta);
    }

    private TsDataAsserter() {
    }
}
