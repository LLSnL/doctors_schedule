
package org.timeseries.timeseriesutil;

import org.timeseries.matrix.InsightsMatrix;

public final class ForecastUtil {


    public static final double testSetPercentage = 0.15;
    public static final double maxConditionNumber = 100;
    public static final double confidence_constant_95pct = 1.959963984540054;

    private ForecastUtil() {
    }

    public static InsightsMatrix initToeplitz(double[] input) {
        int length = input.length;
        double toeplitz[][] = new double[length][length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (j > i) {
                    toeplitz[i][j] = input[j - i];
                } else if (j == i) {
                    toeplitz[i][j] = input[0];
                } else {
                    toeplitz[i][j] = input[i - j];
                }
            }
        }
        return new InsightsMatrix(toeplitz, false);
    }

    public static double[] ARMAtoMA(final double[] ar, final double[] ma, final int lag_max) {
        final int p = ar.length;
        final int q = ma.length;
        final double[] psi = new double[lag_max];

        for (int i = 0; i < lag_max; i++) {
            double tmp = (i < q) ? ma[i] : 0.0;
            for (int j = 0; j < Math.min(i + 1, p); j++) {
                tmp += ar[j] * ((i - j - 1 >= 0) ? psi[i - j - 1] : 1.0);
            }
            psi[i] = tmp;
        }
        final double[] include_psi1 = new double[lag_max];
        include_psi1[0] = 1;
        for (int i = 1; i < lag_max; i++) {
            include_psi1[i] = psi[i - 1];
        }
        return include_psi1;
    }

    public static double[] getCumulativeSumOfCoeff(final double[] coeffs) {
        final int len = coeffs.length;
        final double[] cumulativeSquaredCoeffSumVector = new double[len];
        double cumulative = 0.0;
        for (int i = 0; i < len; i++) {
            cumulative += Math.pow(coeffs[i], 2);
            cumulativeSquaredCoeffSumVector[i] = Math.pow(cumulative, 0.5);
        }
        return cumulativeSquaredCoeffSumVector;
    }

}
