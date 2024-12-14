package org.example;

import javafx.util.Pair;

import java.io.File;
import java.util.*;
import java.util.List;

import org.parsers.MatrixToCSV;
import org.parsers.Parsers;
import org.timeseries.arima.Arima;
import org.timeseries.arima.struct.ArimaParams;
import org.timeseries.arima.struct.ForecastResult;


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
          Pair<List<String>, List<List<Integer>>> a = Parsers.predParse("pred.xlsx");
          Parsers.parseToCSV("pred.xlsx");
          System.out.println("finish");
          File z = new File("a");
          MatrixToCSV abc = new MatrixToCSV();
          abc.convert(a);



        double[] dataArray = new double[] {0, 17, 1026, 910, 679, 571, 698, 637, 556, 1050, 691, 1209, 1248, 1418, 1370, 1436, 1314, 1172, 933, 990, 1203, 1176, 1154, 1112, 1013, 127, 963, 1034, 1026, 1050, 975, 934, 899, 951, 1059, 896, 974, 1020, 1219, 1020, 1230, 1055, 1124, 1199, 936, 1168, 1367, 1523, 1428, 1525, 1419, 1504, 1302, 266, 1479, 1470, 1552, 1598, 1783, 1648, 1119, 1718, 1316, 1691, 1701, 1656, 1828, 1837, 1801, 1780, 1545, 1216, 1726, 1799, 1801, 1853, 1671, 1810, 1854, 1414, 1456, 1425, 1499, 1500, 1539, 1433, 1480, 1487, 1481, 1474, 1158, 1633, 1625, 1722, 1807, 1828, 1810, 1956, 1995, 2002, 1885, 1902, 1895, 1726, 1294, 84, 1427, 1816, 2102};

        int p = 3;
        int d = 0;
        int q = 3;
        int P = 1;
        int D = 1;
        int Q = 0;
        int m = 0;
        int forecastSize = 12;

        ArimaParams params = new ArimaParams(p,d,q,P,D,Q,m);

        ForecastResult forecastResult = Arima.forecast_arima(dataArray, forecastSize, params);

        double[] forecastData = forecastResult.getForecast(); // in this example, it will return { 2 }

        double[] uppers = forecastResult.getForecastUpperConf();
        double[] lowers = forecastResult.getForecastLowerConf();

        double rmse = forecastResult.getRMSE();

        double maxNormalizedVariance = forecastResult.getMaxNormalizedVariance();

        String log = forecastResult.getLog();


        //
/*        Thread plotThread = new Thread(() -> {
            List<Date> xAxis = new ArrayList(timeSeries.observationTimes().size());
            Iterator var4 = timeSeries.observationTimes().iterator();

            while(var4.hasNext()) {
                OffsetDateTime dateTime = (OffsetDateTime)var4.next();
                xAxis.add(Date.from(dateTime.toInstant()));
            }

            XYChart chart = ((XYChartBuilder)((XYChartBuilder)((XYChartBuilder)((XYChartBuilder)(new XYChartBuilder()).theme(Styler.ChartTheme.GGPlot2)).height(1000)).width(1200)).title("Сравнение")).build();

            chart.addSeries("Исходные данные", xAxis, seriesList);
            XYSeries series1 = chart.addSeries("Полное предсказание", xAxis, predictionList);
            XYSeries nowSeries = chart.addSeries("Текущее предсказание", xAxis, predictionNow);
            XYSeries highSeries = chart.addSeries("Верхняя граница", xAxis, high);
            XYSeries lowSeries = chart.addSeries("Нижняя грацница", xAxis, low);
            series1.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            nowSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            highSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            lowSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            series1.setMarkerColor(Color.BLUE);
            nowSeries.setMarkerColor(Color.RED);
            highSeries.setLineColor(Color.BLACK);
            highSeries.setMarkerColor(Color.BLACK);
            lowSeries.setLineColor(Color.GRAY);
            lowSeries.setMarkerColor(Color.GRAY);

            JPanel panel = new XChartPanel(chart);
            JFrame frame = new JFrame("Сравнение");
            frame.setDefaultCloseOperation(2);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
            });
        plotThread.start();*///

        /*Thread plotThread2 = new Thread(() -> {
            List<Date> xAxis = new ArrayList(timeSeries.observationTimes().size());
            Iterator var4 = timeSeries.observationTimes().iterator();

            while(var4.hasNext()) {
                OffsetDateTime dateTime = (OffsetDateTime)var4.next();
                xAxis.add(Date.from(dateTime.toInstant()));
            }

            XYChart chart = ((XYChartBuilder)((XYChartBuilder)((XYChartBuilder)((XYChartBuilder)(new XYChartBuilder()).theme(Styler.ChartTheme.GGPlot2)).height(1000)).width(1200)).title("Дельты")).build();

            chart.addSeries("Low", xAxis, deltaLow);
            XYSeries series1 = chart.addSeries("High", xAxis, deltaHigh);
           // series1.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            series1.setMarkerColor(Color.BLUE);
            series1.setLineColor(Color.BLUE);

            JPanel panel = new XChartPanel(chart);
            JFrame frame = new JFrame("Дельты");
            frame.setDefaultCloseOperation(2);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        });
        plotThread2.start();

         */


    }
}
