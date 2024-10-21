package org.example;

import javafx.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TimeSeriesData {
    private ArrayList<Double> values = new ArrayList<>();

    public void loadData(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();  // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String a = parts[1].substring(1,parts[1].length() - 1);
                values.add(Double.parseDouble(a));
                System.out.println(parts[1]);
            }
        }
    }

    public ArrayList<Double> getValues() {
        return values;
    }
}
