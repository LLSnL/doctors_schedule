package org.analysis;

import javafx.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.parsers.Parsers;
import org.timeseries.arima.Arima;
import org.timeseries.arima.struct.ArimaParams;
import org.timeseries.arima.struct.ForecastResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Analysis {
    private static double[][] getForecasts(double[] data, ArimaParams params){

        ForecastResult forecastResult = Arima.forecast_arima(data, 1, params);

        ArrayList<Double> spliceForecastData = new ArrayList<>();
        ArrayList<Double> Uppers = new ArrayList<>();
        ArrayList<Double> Lowers = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            spliceForecastData.add(data[i]);
            Uppers.add(data[i]);
            Lowers.add(data[i]);
        }
        for (int i = 10; i < data.length - 1; i++) {
            double[] dataSplice = Arrays.copyOfRange(data, 0, i);
            ForecastResult spliceForecast = Arima.forecast_arima(dataSplice, 1, params);
            spliceForecastData.add(spliceForecast.getForecast()[0]);
            Uppers.add(spliceForecast.getForecastUpperConf()[0]);
            Lowers.add(spliceForecast.getForecastLowerConf()[0]);

        }
        double[] splice = spliceForecastData.stream().mapToDouble(Double::doubleValue).toArray();
        double[] uppers = Uppers.stream().mapToDouble(Double::doubleValue).toArray();
        double[] lowers = Lowers.stream().mapToDouble(Double::doubleValue).toArray();

        double[][] allData = {splice, lowers, uppers};

        return allData;
    }

    public static void analysisIntoExcel(String filePath, int p, int d, int q, int P, int D, int Q, int m) throws IOException {

        Pair<List<String>, List<List<Integer>>> a = Parsers.predParse(filePath);
        ArimaParams params = new ArimaParams(p,d,q,P,D,Q,m);

        Workbook book = new XSSFWorkbook();
        for (int i = 2; i < a.getValue().get(0).size(); i++) {

            ArrayList<Double> Data = new ArrayList<>();
            for (int j = 0; j < a.getValue().size(); j++) {
                Data.add(Double.valueOf(a.getValue().get(j).get(i)));
            }
            double[] data = Data.stream().mapToDouble(Double::doubleValue).toArray();
            double[][] forecasts = getForecasts(data,params);

            Sheet sheet = book.createSheet(a.getKey().get(i));
            Row row = sheet.createRow(0);
            Cell name = row.createCell(0);
            name.setCellValue(a.getKey().get(i));

            for (int j = 0; j < a.getValue().size(); j++) {
                Cell cell = row.createCell(j + 1);
                cell.setCellValue(a.getValue().get(j).get(i));
            }

            row = sheet.createRow(1);
            name = row.createCell(0);
            name.setCellValue("Предсказания");

            for (int j = 0; j < a.getValue().size(); j++) {
                Cell cell = row.createCell(j + 1);
                cell.setCellValue(forecasts[0][j]);
            }

            row = sheet.createRow(2);
            name = row.createCell(0);
            name.setCellValue("Low");

            for (int j = 0; j < a.getValue().size(); j++) {
                Cell cell = row.createCell(j + 1);
                cell.setCellValue(forecasts[1][j]);
            }

            row = sheet.createRow(3);
            name = row.createCell(0);
            name.setCellValue("High");

            for (int j = 0; j < a.getValue().size(); j++) {
                Cell cell = row.createCell(j + 1);
                cell.setCellValue(forecasts[2][j]);
            }

            XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 6, 20, 35);

            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText(a.getKey().get(i));
            chart.setTitleOverlay(false);

            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.TOP_RIGHT);

            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxis.setTitle("Недели");
            bottomAxis.setMinorUnit(1.0);
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setTitle("Кол-во исследований");

            XDDFDataSource<String> dataSource = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) sheet,
                    new CellRangeAddress(0, 4, 0, 0));

            XDDFNumericalDataSource<Double> raw = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet,
                    new CellRangeAddress(0, 0, 0, a.getValue().size()));

            XDDFNumericalDataSource<Double> splice = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet,
                    new CellRangeAddress(1, 1, 0, a.getValue().size()));

            XDDFNumericalDataSource<Double> low = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet,
                    new CellRangeAddress(2, 2, 0, a.getValue().size()));

            XDDFNumericalDataSource<Double> high = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet,
                    new CellRangeAddress(3, 3, 0, a.getValue().size()));

            XDDFLineChartData chartData = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);

            XDDFLineChartData.Series series1 = (XDDFLineChartData.Series) chartData.addSeries(dataSource, raw);
            series1.setTitle("Исходные данные", null);
            series1.setMarkerStyle(MarkerStyle.DIAMOND);

            XDDFLineChartData.Series series2 = (XDDFLineChartData.Series) chartData.addSeries(dataSource, splice);
            series2.setTitle("Предсказания", null);
            series2.setMarkerStyle(MarkerStyle.SQUARE);

            XDDFLineChartData.Series series3 = (XDDFLineChartData.Series) chartData.addSeries(dataSource, low);
            series3.setTitle("Low", null);
            series3.setMarkerStyle(MarkerStyle.STAR);

            XDDFLineChartData.Series series4 = (XDDFLineChartData.Series) chartData.addSeries(dataSource, high);
            series4.setTitle("High", null);
            series4.setMarkerStyle(MarkerStyle.STAR);

            chart.plot(chartData);

            sheet.autoSizeColumn(0);
        }
        book.write(Files.newOutputStream(Paths.get("АнализДанных.xlsx")));
        book.close();
    }

    public static void nextMonthForecastToExcel(String filePath, int p, int d, int q, int P, int D, int Q, int m) throws IOException {

        ArimaParams params = new ArimaParams(p,d,q,P,D,Q,m);
        Pair<List<String>, List<List<Integer>>> a = Parsers.predParse(filePath);
        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet("Предсказания на следующий месяц");
        for (int i = 2; i < a.getValue().get(0).size(); i++) {

            ArrayList<Double> Data = new ArrayList<>();
            for (int j = 0; j < a.getValue().size(); j++) {
                Data.add(Double.valueOf(a.getValue().get(j).get(i)));
            }
            double[] data = Data.stream().mapToDouble(Double::doubleValue).toArray();

            ForecastResult forecastResult = Arima.forecast_arima(data, 4, params);
            double[] forecastData = forecastResult.getForecast();

            Row row = sheet.createRow(i - 2);
            Cell name = row.createCell(0);
            name.setCellValue(a.getKey().get(i));

            double sum = 0.0;
            for (int j = 0; j < forecastData.length ; j++) {
                sum += forecastData[j];
            }
            sum = sum * 15 / 14;
            name = row.createCell(1);
            name.setCellValue((int) sum);
        }

        sheet.autoSizeColumn(0);

        book.write(Files.newOutputStream(Paths.get("ПредсказаниеНаМесяц.xlsx")));
        book.close();

    }

    public static void doctorsToExcel(String filePath) throws IOException {
        Pair<List<String>, List<String>> a = Parsers.docParse(filePath);
        int k = 1;
        Workbook book = new XSSFWorkbook();

        Map<String, Integer> modules = new LinkedHashMap<>();
        Map<String, Integer> extraModules = new LinkedHashMap<>();
        Map<Integer, Integer> workHours = new LinkedHashMap<>();

        for (int i = 1; i < a.getValue().size(); i += 4) {

            if(!modules.containsKey(a.getValue().get(i))){
                modules.put(a.getValue().get(i), 1);

            } else {
                modules.replace(a.getValue().get(i), modules.get(a.getValue().get((i))) + 1);
            }

            if(!extraModules.containsKey(a.getValue().get(i + 1))){
                extraModules.put(a.getValue().get(i + 1), 1);

            } else {
                extraModules.replace(a.getValue().get(i + 1), extraModules.get(a.getValue().get((i + 1))) + 1);
            }

            if(!workHours.containsKey((int) (Double.parseDouble(a.getValue().get(i + 2)) * 100))){
                workHours.put((int) (Double.parseDouble(a.getValue().get(i + 2)) * 100), 1);
            } else {
                workHours.replace((int) (Double.parseDouble(a.getValue().get(i + 2)) * 100), Integer.sum(workHours.get((int) (Double.parseDouble(a.getValue().get(i + 2)) * 100)), 1));
            }
        }

        Sheet sheet = book.createSheet("Распределение основных модальностей");
        Iterator<String> modulesIterator;
        modulesIterator = modules.keySet().iterator();
        Row row = sheet.createRow(0);
        Row secondRow = sheet.createRow(1);
        Cell name = row.createCell(0);
        Cell data = secondRow.createCell(0);
        name.setCellValue("Название модальности");
        data.setCellValue("Кол-во докторов");

        while(modulesIterator.hasNext()) {
            name = row.createCell(k);
            data = secondRow.createCell(k);
            String iter = modulesIterator.next();
            name.setCellValue(iter);
            data.setCellValue(modules.get(iter));
            ++k;
        }
        sheet.autoSizeColumn(0);

        k = 1;
        sheet = book.createSheet("Распределение дополнительных модальностей");
        Iterator<String> extraModulesIterator;
        extraModulesIterator = extraModules.keySet().iterator();
        row = sheet.createRow(0);
        secondRow = sheet.createRow(1);
        name = row.createCell(0);
        data = secondRow.createCell(0);
        name.setCellValue("Названия модальностей");
        data.setCellValue("Кол-во докторов");

        while(extraModulesIterator.hasNext()) {
            name = row.createCell(k);
            data = secondRow.createCell(k);
            String iter = extraModulesIterator.next();
            name.setCellValue(iter);
            data.setCellValue(extraModules.get(iter));
            ++k;
        }
        sheet.autoSizeColumn(0, true);

        k = 1;
        sheet = book.createSheet("Распределение ставки врачей (1 к 100)");
        Iterator<Integer> workHoursIterator;
        workHoursIterator = workHours.keySet().iterator();
        row = sheet.createRow(0);
        secondRow = sheet.createRow(1);
        name = row.createCell(0);
        data = secondRow.createCell(0);
        name.setCellValue("Ставка");
        data.setCellValue("Кол-во докторов");

        while(workHoursIterator.hasNext()) {
            name = row.createCell(k);
            data = secondRow.createCell(k);
            Integer iter = workHoursIterator.next();
            name.setCellValue(iter);
            data.setCellValue(workHours.get(iter));
            ++k;
        }
        sheet.autoSizeColumn(0, true);

        book.write(Files.newOutputStream(Paths.get("АнализДокторов.xlsx")));
        book.close();
    }
}
