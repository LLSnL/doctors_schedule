package org.timeseries.arima;

import org.matrix.InsightsMatrix;
import org.matrix.InsightsVector;
import org.timeseries.timeseriesutil.ForecastUtil;

import java.util.Arrays;

public final class YuleWalker {

    private YuleWalker() {
    }

    public static double[] fit(final double[] data, final int p) {

        int length = data.length;
        if (length == 0 || p < 1) {
            throw new RuntimeException(
                "fitYuleWalker - Invalid Parameters" + "length=" + length + ", p = " + p);
        }

        double[] r = new double[p + 1];
        for (double aData : data) {
            r[0] += Math.pow(aData, 2);
        }
        r[0] /= length;

        for (int j = 1; j < p + 1; j++) {
            for (int i = 0; i < length - j; i++) {
                r[j] += data[i] * data[i + j];
            }
            r[j] /= (length);
        }

        final InsightsMatrix toeplitz = ForecastUtil.initToeplitz(Arrays.copyOfRange(r, 0, p));
        final InsightsVector rVector = new InsightsVector(Arrays.copyOfRange(r, 1, p + 1), false);

        return toeplitz.solveSPDIntoVector(rVector, ForecastUtil.maxConditionNumber).deepCopy();
    }
}
