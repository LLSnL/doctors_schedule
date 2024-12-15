package org.app;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import org.analysis.Analysis;
import org.schedule.GenerateSchedule;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        Options options = new Options();

        Option analysis = new Option("a", "analysis", true, "ARIMA parameters: p,d,q,P,D,Q,m");
        analysis.setArgs(7);
        options.addOption(analysis);

        Option doctors = new Option("d", "doctors", false, "Generate doctors analysis");
        options.addOption(doctors);

        Option forecast = new Option("f", "forecast", true, "ARIMA parameters for forecast: p,d,q,P,D,Q,m");
        forecast.setArgs(7);
        options.addOption(forecast);

        Option schedule = new Option("s", "schedule", true, "Generate schedule for given number of days");
        schedule.setArgs(1);
        options.addOption(schedule);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("analysis")) {
                String[] analysisParams = cmd.getOptionValues("analysis");
                try {
                    int p = Integer.parseInt(analysisParams[0]);
                    int d = Integer.parseInt(analysisParams[1]);
                    int q = Integer.parseInt(analysisParams[2]);
                    int P = Integer.parseInt(analysisParams[3]);
                    int D = Integer.parseInt(analysisParams[4]);
                    int Q = Integer.parseInt(analysisParams[5]);
                    int m = Integer.parseInt(analysisParams[6]);

                    if (p < 0 || d < 0 || q < 0 || P < 0 || D < 0 || Q < 0 || m < 0 || p > 3 || d > 3 || q > 3 || P > 3 || D > 3 || Q > 3 || m > 3) {
                        throw new Exception("");
                    }
                    Analysis.analysisIntoExcel("pred.xlsx", p, d, q, P, D, Q, m);
                    logger.info("Команда выполнена. Файл АнализДанных.xlsx обновлён");
                } catch (Exception e) {
                    logger.error("Команда не распознана/неподходящие параметры", e);
                }
            } else if (cmd.hasOption("doctors")) {
                try {
                    Analysis.doctorsToExcel("doc.xlsx");
                    logger.info("Команда выполнена. Файл АнализДокторов.xlsx обновлён");
                } catch (IOException e) {
                    logger.error("Ошибка при выполнении команды: " + e.getMessage(), e);
                }
            } else if (cmd.hasOption("forecast")) {
                String[] forecastParams = cmd.getOptionValues("forecast");
                try {
                    int p = Integer.parseInt(forecastParams[0]);
                    int d = Integer.parseInt(forecastParams[1]);
                    int q = Integer.parseInt(forecastParams[2]);
                    int P = Integer.parseInt(forecastParams[3]);
                    int D = Integer.parseInt(forecastParams[4]);
                    int Q = Integer.parseInt(forecastParams[5]);
                    int m = Integer.parseInt(forecastParams[6]);

                    if (p < 0 || d < 0 || q < 0 || P < 0 || D < 0 || Q < 0 || m < 0 || p > 3 || d > 3 || q > 3 || P > 3 || D > 3 || Q > 3 || m > 3) {
                        throw new Exception("");
                    }
                    Analysis.nextMonthForecastToExcel("pred.xlsx", p, d, q, P, D, Q, m);
                    logger.info("Команда выполнена. Файл ПредсказаниеНаМесяц.xlsx обновлён");
                } catch (Exception e) {
                    logger.error("Команда не распознана/неподходящие параметры", e);
                }
            } else if (cmd.hasOption("schedule")) {
                try {
                    int num = Integer.parseInt(cmd.getOptionValue("schedule"));
                    if (num > 31 || num < 28) {
                        throw new Exception("");
                    }
                    GenerateSchedule.toExcel(num);
                    logger.info("Команда выполнена. Файл РасписаниеВрачей.xlsx обновлён");
                } catch (Exception e) {
                    logger.error("Команда не распознана", e);
                }
            } else {
                formatter.printHelp("schedule-app", options);
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            formatter.printHelp("schedule-app", options);
        }
    }
}