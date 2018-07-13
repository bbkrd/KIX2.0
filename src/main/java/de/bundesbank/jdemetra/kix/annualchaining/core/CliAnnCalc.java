/*
 * Copyright 2016 Deutsche Bundesbank
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package de.bundesbank.jdemetra.kix.annualchaining.core;

import static de.bundesbank.jdemetra.kix.annualchaining.core.AnnualOverlapMethods.chainSum;
import static de.bundesbank.jdemetra.kix.annualchaining.core.AnnualOverlapMethods.mid;
import static de.bundesbank.jdemetra.kix.annualchaining.core.AnnualOverlapMethods.unchain;
import de.bundesbank.kix.parser.ICalc;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsPeriod;

/**
 *
 * @author Thomas Witthohn
 */
public final class CliAnnCalc implements ICalc {

    private double factor, factorWeight;
    private final boolean displayFirstYear;
    private final int referenzYear;
    private TsData weightedSumData;
    private TsData weightedSumWeights;

    public CliAnnCalc(final TsData indexData, final TsData indexWeights, final int inputYear) {
        this(indexData, indexWeights, inputYear, false);
    }

    public CliAnnCalc(final TsData indexData, final TsData indexWeights, final int inputYear, final boolean inputDisplayFirstYear) {
        weightedSumData = unchain(indexData);
        weightedSumWeights = mid(indexWeights, true);

        displayFirstYear = inputDisplayFirstYear;
        referenzYear = inputYear;

        TsPeriod referenzYearPeriod = new TsPeriod(indexData.getFrequency(), referenzYear, 1);
        factor = mid(indexData, false).get(referenzYearPeriod);
        factorWeight = calculateFactorWeight(indexData, indexWeights);
    }

    @Override
    public void plus(final TsData addData, final TsData addWeights) {
        addFactor(addData, addWeights);
        addWeightsum(addData, addWeights);
    }

    @Override
    public TsData getResult() {
        TsPeriod referenzYearPeriod = new TsPeriod(weightedSumData.getFrequency(), referenzYear, 0);
        double meanInRefYear = mid(chainSum(weightedSumData), false).get(referenzYearPeriod);

        TsData indexData = chainSum(weightedSumData).times(factor).div(meanInRefYear);
        if (displayFirstYear) {
            return indexData;
        }
        return indexData.drop(indexData.getFrequency().intValue() - indexData.getStart().getPosition(), 0);

    }

    @Override
    public void minus(final TsData subtractData, final TsData subtractWeights) {
        subtractFactor(subtractData, subtractWeights);
        subtractWeightsum(subtractData, subtractWeights);
    }

    private void addFactor(final TsData addData, final TsData addWeights) {
        TsPeriod referenzYearPeriod = new TsPeriod(addData.getFrequency(), referenzYear, 1);
        double midIndexAtRefYear = mid(addData, false).get(referenzYearPeriod);
        double addFactorWeight = calculateFactorWeight(addData, addWeights);

        factor = (factor * factorWeight + midIndexAtRefYear * addFactorWeight) / (factorWeight + addFactorWeight);
        factorWeight += addFactorWeight;
    }

    /**
     *
     * @param addData
     * @param addWeights
     */
    private void addWeightsum(final TsData addData, final TsData addWeights) {

        TsData unchainedData = unchain(addData);
        TsData averagedWeights = mid(addWeights, true);
        weightedSumData = weightedSumData.times(weightedSumWeights)
                .plus(unchainedData.times(averagedWeights))
                .div(weightedSumWeights.plus(averagedWeights));
        weightedSumWeights = weightedSumWeights.plus(averagedWeights);

    }

    private double calculateFactorWeight(final TsData data, final TsData weights) {
        double sumDataRefYear = 0, sumDataPreYear = 0, sumWeightPreYear = 0;
        for (int i = 0; i < data.getFrequency().intValue(); i++) {
            TsPeriod referenzYearPeriod = new TsPeriod(data.getFrequency(), referenzYear, i);
            TsPeriod previousYearPeriod = new TsPeriod(data.getFrequency(), referenzYear - 1, i);

            sumDataRefYear += data.get(referenzYearPeriod);
            sumDataPreYear += data.get(previousYearPeriod);
            sumWeightPreYear += weights.get(previousYearPeriod);
        }
        return sumDataRefYear * sumWeightPreYear / sumDataPreYear;
    }

    private void subtractFactor(final TsData addData, final TsData addWeights) {
        TsPeriod referenzYearPeriod = new TsPeriod(addData.getFrequency(), referenzYear, 1);
        double midIndexAtRefYear = mid(addData, false).get(referenzYearPeriod);
        double subtractFactorWeight = calculateFactorWeight(addData, addWeights);

        factor = (factor * factorWeight - midIndexAtRefYear * subtractFactorWeight) / (factorWeight - subtractFactorWeight);
        factorWeight -= subtractFactorWeight;
    }

    /**
     *
     * @param subtractData
     * @param subtractWeights
     */
    private void subtractWeightsum(final TsData subtractData, final TsData subtractWeights) {
        TsData unchainedData = unchain(subtractData);
        TsData averagedWeights = mid(subtractWeights, true);
        weightedSumData = weightedSumData.times(weightedSumWeights)
                .minus(unchainedData.times(averagedWeights))
                .div(weightedSumWeights.minus(averagedWeights));
        weightedSumWeights = weightedSumWeights.minus(averagedWeights);
    }
}
