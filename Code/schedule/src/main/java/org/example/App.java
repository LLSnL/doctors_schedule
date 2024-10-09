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
                result = PredictAlgorithm.linearRegression(a,2024, 5);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println(result.toString());
        }
}
