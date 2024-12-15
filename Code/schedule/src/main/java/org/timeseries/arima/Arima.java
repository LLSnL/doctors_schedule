package org.timeseries.arima;
import org.timeseries.arima.struct.ArimaModel;
import org.timeseries.arima.struct.ArimaParams;
import org.timeseries.arima.struct.ForecastResult;
import org.timeseries.timeseriesutil.ForecastUtil;

public final class Arima {

    private Arima() {
    }

    /**
     * Функция предсказания ARIMA.
     *
     * @param data набор данных
     * @param forecastSize кол-во предсказаний
     * @param params ARIMA параметры
     */
    public static ForecastResult forecast_arima(final double[] data, final int forecastSize, ArimaParams params) {

        try {

            final ArimaParams paramsForecast = params;
            final ArimaParams paramsXValidation = params;

            final ArimaModel fittedModel = ArimaSolver.estimateARIMA(
                paramsForecast, data, data.length, data.length + 1);

            final double rmseValidation = ArimaSolver.computeRMSEValidation(
                data, ForecastUtil.testSetPercentage, paramsXValidation);
            fittedModel.setRMSE(rmseValidation);
            final ForecastResult forecastResult = fittedModel.forecast(forecastSize);

            forecastResult.setSigma2AndPredicationInterval(fittedModel.getParams());

            forecastResult.log("{" +
                               "\"Best ModelInterface Param\" : \"" + fittedModel.getParams().summary() + "\"," +
                               "\"Forecast Size\" : \"" + forecastSize + "\"," +
                               "\"Input Size\" : \"" + data.length + "\"" +
                               "}");

            return forecastResult;

        } catch (final Exception ex) {
            throw new RuntimeException("Failed to build ARIMA forecast: " + ex.getMessage());
        }
    }
}
