package org.example;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.function.DoubleFunction;

import com.github.signaflo.math.operations.DoubleFunctions;
import com.github.signaflo.timeseries.TestData;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.forecast.Forecast;
import com.github.signaflo.timeseries.model.Model;
import com.github.signaflo.timeseries.model.arima.Arima;
import com.github.signaflo.timeseries.model.arima.ArimaOrder;

import static com.github.signaflo.data.visualization.Plots.plot;

public class App
{
    public static double meanAbsoluteError(ArrayList<Double> actual, ArrayList<Double> forecasted) {
        double error = 0.0;
        for (int i = 0; i < actual.size(); i++) {
            error += Math.abs(actual.get(i) - forecasted.get(i));
        }
        return error / actual.size();
    }

    public static double rootMeanSquaredError(ArrayList<Double> actual, ArrayList<Double> forecasted) {
        double squaredError = 0.0;
        for (int i = 0; i < actual.size(); i++) {
            squaredError += Math.pow(actual.get(i) - forecasted.get(i), 2);
        }
        return Math.sqrt(squaredError / actual.size());
    }

    public static void main( String[] args ) throws Exception {
/*          Pair<List<String>, List<List<Integer>>> a = Parsers.predParse("pred.xlsx");
            Parsers.parseToCSV("pred.xlsx");
            ArrayList<Classifier> result = new ArrayList<>();
            result = PredictAlgorithm.linearRegression(a);
            System.out.println("finish");
            File z = new File("a");
            MatrixToCSV abc = new MatrixToCSV();
            abc.convert(a);
            */
        TimeSeriesData tsData = new TimeSeriesData();
        tsData.loadData("Денситометр");
        ArrayList<Double> values = tsData.getValues();
        double a;
        TimeSeries timeSeries = TimeSeries.from(0,
                17,
                1026,
                910,
                679,
                571,
                698,
                637,
                556,
                1050,
                691,
                1209,
                1248,
                1418,
                1370,
                1436,
                1314,
                1172,
                933,
                990,
                1203,
                1176,
                1154,
                1112,
                1013,
                127,
                963,
                1034,
                1026,
                1050,
                975,
                934,
                899,
                951,
                1059,
                896,
                974,
                1020,
                1219,
                1020,
                1230,
                1055,
                1124,
                1199,
                936,
                1168,
                1367,
                1523,
                1428,
                1525,
                1419,
                1504,
                1302,
                266,
                1479,
                1470,
                1552,
                1598,
                1783,
                1648,
                1119,
                1718,
                1316,
                1691,
                1701,
                1656,
                1828,
                1837,
                1801,
                1780,
                1545,
                1216,
                1726,
                1799,
                1801,
                1853,
                1671,
                1810,
                1854,
                1414,
                1456,
                1425,
                1499,
                1500,
                1539,
                1433,
                1480,
                1487,
                1481,
                1474,
                1158,
                1633,
                1625,
                1722,
                1807,
                1828,
                1810,
                1956,
                1995,
                2002,
                1885,
                1902,
                1895,
                1726,
                1294,
                84,
                1427,
                1816,
                2102);

        ArimaOrder modelOrder = ArimaOrder.order(0, 2, 1, 0, 1, 1);
        Arima model = Arima.model(timeSeries, modelOrder);

        System.out.println(model.aic()); // Get and display the model AIC
        System.out.println(model.coefficients()); // Get and display the estimated coefficients
        System.out.println(java.util.Arrays.toString(model.stdErrors()));
        plot(model.predictionErrors());
        plot(model.observations());

        Forecast forecast = model.forecast(1);
        System.out.println(forecast);
    }
}
