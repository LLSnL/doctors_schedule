package org.timeseries.arima;

import org.timeseries.matrix.InsightsMatrix;
import org.timeseries.matrix.InsightsVector;
import org.timeseries.arima.struct.ArimaParams;
import org.timeseries.arima.struct.BackShift;
import org.timeseries.timeseriesutil.ForecastUtil;

public final class HannanRissanen {

    private HannanRissanen() {
    }

    public static void estimateARMA(final double[] data_orig, final ArimaParams params,
        final int forecast_length, final int maxIteration) {
        final double[] data = new double[data_orig.length];
        final int total_length = data.length;
        System.arraycopy(data_orig, 0, data, 0, total_length);
        final int r = (params.getDegreeP() > params.getDegreeQ()) ?
            1 + params.getDegreeP() : 1 + params.getDegreeQ();
        final int length = total_length - forecast_length;
        final int size = length - r;
        if (length < 2 * r) {
            throw new RuntimeException("not enough data points: length=" + length + ", r=" + r);
        }

        final double[] errors = new double[length];
        final double[] yuleWalkerParams = applyYuleWalkerAndGetInitialErrors(data, r, length,
            errors);
        for (int j = 0; j < r; ++j) {
            errors[j] = 0;
        }

        final double[][] matrix = new double[params.getNumParamsP() + params.getNumParamsQ()][size];

        double bestRMSE = -1;
        int remainIteration = maxIteration;
        InsightsVector bestParams = null;
        while (--remainIteration >= 0) {
            final InsightsVector estimatedParams = iterationStep(params, data, errors, matrix, r,
                length,
                size);
            final InsightsVector originalParams = params.getParamsIntoVector();
            params.setParamsFromVector(estimatedParams);

            final double[] forecasts = ArimaSolver.forecastARMA(params, data, length, data.length);
            final double anotherRMSE = ArimaSolver
                .computeRMSE(data, forecasts, length, 0, forecast_length);
            final double[] train_forecasts = ArimaSolver.forecastARMA(params, data, r, data.length);
            for (int j = 0; j < size; ++j) {
                errors[j + r] = data[j + r] - train_forecasts[j];
            }
            if (bestRMSE < 0 || anotherRMSE < bestRMSE) {
                bestParams = estimatedParams;
                bestRMSE = anotherRMSE;
            }
        }
        params.setParamsFromVector(bestParams);
    }

    private static double[] applyYuleWalkerAndGetInitialErrors(final double[] data, final int r,
        final int length, final double[] errors) {
        final double[] yuleWalker = YuleWalker.fit(data, r);
        final BackShift bsYuleWalker = new BackShift(r, true);
        bsYuleWalker.initializeParams(false);
        for (int j = 0; j < r; ++j) {
            bsYuleWalker.setParam(j + 1, yuleWalker[j]);
        }
        int m = 0;
        while (m < r) {
            errors[m++] = 0;
        }
        while (m < length) {
            errors[m] = data[m] - bsYuleWalker.getLinearCombinationFrom(data, m);
            ++m;
        }
        return yuleWalker;
    }

    private static InsightsVector iterationStep(
        final ArimaParams params,
        final double[] data, final double[] errors,
        final double[][] matrix, final int r, final int length, final int size) {

        int rowIdx = 0;
        final int[] offsetsAR = params.getOffsetsAR();
        for (int pIdx : offsetsAR) {
            System.arraycopy(data, r - pIdx, matrix[rowIdx], 0, size);
            ++rowIdx;
        }
        final int[] offsetsMA = params.getOffsetsMA();
        for (int qIdx : offsetsMA) {
            System.arraycopy(errors, r - qIdx, matrix[rowIdx], 0, size);
            ++rowIdx;
        }

        final InsightsMatrix zt = new InsightsMatrix(matrix, false);
        final double[] vector = new double[size];
        System.arraycopy(data, r, vector, 0, size);
        final InsightsVector x = new InsightsVector(vector, false);

        final InsightsVector ztx = zt.timesVector(x);
        final InsightsMatrix ztz = zt.computeAAT();
        final InsightsVector estimatedVector = ztz
            .solveSPDIntoVector(ztx, ForecastUtil.maxConditionNumber);

        return estimatedVector;
    }
}
