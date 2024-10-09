package org.example;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class App
{
        public static void main( String[] args )  {
            Pair<List<String>, List<List<Integer>>> a = Parsers.predParse("pred.xlsx");
            ArrayList<Double> result;
            try {
                int year, week;
                year = 2024;
                week = 7;
                result = PredictAlgorithm.linearRegression(a,year,week);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println(result.toString());
        }
}
