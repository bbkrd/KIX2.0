/*
 * Copyright 2017 Deutsche Bundesbank
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
public final class CtgAnnCalc implements ICalc {

    private final TsData wholeIndex, wholeWeight;
    private TsData tsWBTDataLagDiff;
    private final int lag;

    /**
     *
     * @param index
     * @param weight
     * @param lag
     */
    public CtgAnnCalc(final TsData index, final TsData weight, final int lag) {

        this.wholeIndex = index.index(TsPeriod.year(index.getStart().getYear()), 100);
        this.wholeWeight = mid(weight, true);
        if (lag > 0) {
            this.lag = lag;
        } else {
            this.lag = wholeIndex.getFrequency().intValue() * lag * -1;
        }
    }

    @Override
    public void plus(final TsData index, final TsData weight) {
        TsData averageWeight = mid(weight, true);

        TsData tsRemainData = weightsum(unchain(wholeIndex), wholeWeight,
                                        unchain(index), averageWeight, true);

        TsData tsRemainWeights = wholeWeight.minus(averageWeight);

        tsWBTDataLagDiff = weightsum(unchain(index.lead(lag), index),
                                     averageWeight, tsRemainData, tsRemainWeights, false);
    }

    @Override
    public void minus(final TsData index, final TsData weight) {
        TsData averageWeight = mid(weight, true);

        TsData tsRemainData = weightsum(unchain(wholeIndex), wholeWeight,
                                        unchain(index), averageWeight, false);

        TsData tsRemainWeights = wholeWeight.plus(averageWeight);

        tsWBTDataLagDiff = weightsum(unchain(index.lead(lag), index),
                                     averageWeight, tsRemainData, tsRemainWeights, true);
    }

    @Override
    public TsData getResult() {
        if (tsWBTDataLagDiff == null) {
            return new TsData(wholeIndex.getStart(), 0);
        }
        TsData a = wholeIndex.lead(lag);

        TsData tsAllDataLagDiff = wholeIndex.minus(a).div(a).times(100);

        TsData chainedTsWBTDataLagDiff = chainSum(tsWBTDataLagDiff, unchain(wholeIndex));
        chainedTsWBTDataLagDiff = chainedTsWBTDataLagDiff.minus(a).div(a).times(100);

        return tsAllDataLagDiff.minus(chainedTsWBTDataLagDiff);

    }

    private TsData weightsum(final TsData weightedSumD, final TsData weightedSumW, final TsData addD, final TsData addW, final boolean minus) {
        if (minus) {
            return weightedSumD.times(weightedSumW)
                    .minus(addD.times(addW))
                    .div(weightedSumW.minus(addW));
        } else {
            return weightedSumD.times(weightedSumW)
                    .plus(addD.times(addW))
                    .div(weightedSumW.plus(addW));
        }

    }
}
