package org.example;

import javafx.util.Pair;
import weka.classifiers.Classifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App
{
        public static void main( String[] args ) throws Exception {
            Pair<List<String>, List<List<Integer>>> a = Parsers.predParse("pred.xlsx");
            ArrayList<Classifier> result = new ArrayList<>();
            result = PredictAlgorithm.linearRegression(a);
            System.out.println("finish");
            File z = new File("a");
            MatrixToCSV abc = new MatrixToCSV();
            abc.convert(a);
        }
}
