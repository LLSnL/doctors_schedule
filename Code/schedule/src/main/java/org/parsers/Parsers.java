package org.parsers;

import com.opencsv.CSVWriter;
import javafx.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class Parsers {

    public static void parseToCSV(String fileName) throws IOException {
        File file = new File("Модальности");
        FileWriter outfile = new FileWriter(file);
        CSVWriter writer = new CSVWriter(outfile);
        InputStream inputStream = null;
        XSSFWorkbook workBook = null;

        inputStream = new FileInputStream(fileName);
        workBook = new XSSFWorkbook(inputStream);

        Sheet sheet = workBook.getSheetAt(0);
        Iterator<Row> it = sheet.iterator();

        Row headerRow = sheet.getRow(0);
        List<String> headerList = new ArrayList<>();

        int a = headerRow.getLastCellNum();
        for (int cellNum = 0; cellNum < headerRow.getLastCellNum(); cellNum++) {
            headerList.add(headerRow.getCell(cellNum).getStringCellValue());
        }

        String[] info = new String[12];
        headerList.toArray(info);
        writer.writeNext(info);

        List<String> rowInfo;
        Row row = it.next();
        //проходим по всему листу
        while (it.hasNext()) {
            rowInfo = new ArrayList<>();
            row = it.next();
            Iterator<Cell> cells = row.iterator();
            while (cells.hasNext()) {
                Cell cell = cells.next();
                Integer cellfix = (int) cell.getNumericCellValue();
                rowInfo.add(cellfix.toString());
            }
            rowInfo.toArray(info);
            writer.writeNext(info);
        }
        writer.close();
    }

    public static Pair<List<String>, List<String>> docParse(String filePath) {
        //инициализируем потоки
        InputStream inputStream = null;
        XSSFWorkbook workBook = null;
        try {
            inputStream = new FileInputStream(filePath);
            workBook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sheet sheet = workBook.getSheetAt(0);
        Iterator<Row> it = sheet.iterator();

        Row headerRow = sheet.getRow(0);
        List<String> columnNames = new ArrayList<>();

        int a = headerRow.getLastCellNum();
        for (int cellNum = 0; cellNum < 4; cellNum++) {
            columnNames.add(headerRow.getCell(cellNum).getStringCellValue());
        }

        List<String> info = new ArrayList<>();
        Row row = it.next();

        //проходим по всему листу
        while (it.hasNext()) {
            row = it.next();
            Iterator<Cell> cells = row.iterator();
            Cell cell = cells.next();
            if(cell.getCellType() == CellType.BLANK){
                continue;
            }

            for(int cellNum = 0; cellNum < 4; cellNum++) {
                if(cell.getCellType() != CellType.NUMERIC) {
                    info.add(cell.getStringCellValue());
                } else {
                    info.add(Double.toString(cell.getNumericCellValue()));
                }
                cell = cells.next();
            }

        }
        Pair<List<String>, List<String>> res;
        res = new Pair<>(columnNames, info);

        return res;
    }
    public static Pair<List<String>, List<List<Integer>>> predParse(String filePath) {
        //инициализируем потоки
        InputStream inputStream = null;
        XSSFWorkbook workBook = null;
        try {
            inputStream = new FileInputStream(filePath);
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