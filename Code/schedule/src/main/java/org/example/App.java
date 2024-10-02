package org.example;

import javafx.util.Pair;

import java.util.List;

public class App
{
        public static void main( String[] args )  {
        Pair<List<String>, List<List<Integer>>> a = ExcelPredParser.parse("pred.xlsx");
    }
}
