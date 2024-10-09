package org.example;

import javafx.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Parsers {

    public static Pair<List<String>, List<List<Integer>>> predParse(String fileName) {
        //инициализируем потоки
        InputStream inputStream = null;
        XSSFWorkbook workBook = null;
        try {
            inputStream = new FileInputStream(fileName);
            workBook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sheet sheet = workBook.getSheetAt(0);
        Iterator<Row> it = sheet.iterator();

        Row headerRow = sheet.getRow(0);
        List<String> columnNames = new ArrayList<>();

        int a = headerRow.getLastCellNum();
        for (int cellNum = 0; cellNum < headerRow.getLastCellNum(); cellNum++) {
            columnNames.add(headerRow.getCell(cellNum).getStringCellValue());
        }

        List<List<Integer>> info = new ArrayList<>();
        List<Integer> rowInfo;
        Row row = it.next();
        //проходим по всему листу
        while (it.hasNext()) {
            rowInfo = new ArrayList<>();
            row = it.next();
            Iterator<Cell> cells = row.iterator();
            while (cells.hasNext()) {
                Cell cell = cells.next();
                rowInfo.add((int) cell.getNumericCellValue());
                }
            info.add(rowInfo);
            }
        Pair<List<String>, List<List<Integer>>> res;
        res = new Pair<>(columnNames, info);

        return res;
    }

}