package org.schedule;

import javafx.util.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.parsers.Parsers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GenerateSchedule  {
    public static ArrayList<Pair<Doctor,Integer>> generateDoctorsList(String filePath){
        Pair<List<String>, List<String>> a = Parsers.docParse(filePath);
        ArrayList<Pair<Doctor,Integer>> doctors = new ArrayList<>();
        int key = 0;
        for (int i = 0; i < a.getValue().size() / 4; i++) {

            key = i * 4 ;

            String name = a.getValue().get(key);
            double wageRate = Double.parseDouble(a.getValue().get(key + 3));
            Doctor doctor = new Doctor(name, wageRate);

            String module = a.getValue().get(key + 1);
            String extra = a.getValue().get(key + 2);
            String[] extraModule = extra.split(",");

            doctor.changeModules(module, true);

            for (int j = 0; j < extraModule.length; j++) {
                doctor.changeModules(extraModule[j], true);
            }
            doctors.add(new Pair<>(doctor, i));
        }

        return doctors;
    }
    public static ArrayList<Module> getDataInfo(){
        ArrayList<Module> info = new ArrayList<>();

        InputStream inputStream = null;
        XSSFWorkbook workBook = null;
        try {
            inputStream = Files.newInputStream(Paths.get("ПредсказаниеНаМесяц.xlsx"));
            workBook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sheet sheet = workBook.getSheetAt(0);
        Iterator<Row> it = sheet.iterator();

        Module Denc = new Module ("Денситометрия", 0);
        Module KT = new Module("КТ", 0);
        Module MRT = new Module("МРТ", 0);
        Module MMG = new Module("ММГ", 0);
        Module RG = new Module("РГ",0);
        Module FLG = new Module("ФЛГ", 0);

        while(it.hasNext()) {
            Row row = it.next();
            String name = row.getCell(0).getStringCellValue();
            Integer num = (int) row.getCell(1).getNumericCellValue();
            if(name.contains("КТ")){
                KT.setNumberOfResearches(KT.getNumberOfResearches() + num);
                continue;
            }
            if(name.contains("Денситометр")){
                Denc.setNumberOfResearches(Denc.getNumberOfResearches() + num);
                continue;
            }
            if(name.contains("ММГ")){
                MMG.setNumberOfResearches(MMG.getNumberOfResearches() + num);
                continue;
            }
            if(name.contains("МРТ")){
                MRT.setNumberOfResearches(MRT.getNumberOfResearches() + num);
                continue;
            }
            if(name.contains("Флюорограф")){
                FLG.setNumberOfResearches(FLG.getNumberOfResearches() + num);
            }
            if(name.contains("РГ")){
                RG.setNumberOfResearches(RG.getNumberOfResearches() + num);
            }
        }
        info.add(Denc);
        info.add(KT);
        info.add(MRT);
        info.add(MMG);
        info.add(RG);
        info.add(FLG);

        return info;
    }

    public static ArrayList<Pair<Pair<Doctor,Integer>, String>> getDoctorsModules(){
        ArrayList<Module> data = GenerateSchedule.getDataInfo();
        ArrayList<Pair<Doctor, Integer>> doctors = GenerateSchedule.generateDoctorsList("doc.xlsx");

        ArrayList<Pair<Pair<Doctor,Integer>, String>> doctorsModules = new ArrayList<>();

        double workHoursNum = 0.0;
        int researchNum = 0;
        for (Pair<Doctor,Integer> doctor : doctors) {
            workHoursNum += doctor.getKey().getWageRate();
        }
        for (Module datum : data) {
            researchNum += datum.getNumberOfResearches();
        }
        ArrayList<Pair<String,Double>> workHoursCoef = new ArrayList<>();
        for (Module datum : data) {
            double whc = datum.getNumberOfResearches() / (double) researchNum * workHoursNum;
            workHoursCoef.add(new Pair<>(datum.getName(), whc));
        }

        for (Pair<Doctor, Integer> doctor : doctors) {
            if (doctor.getKey().getNumberOfModules() == 1) {
                for (int j = 0; j < doctor.getKey().getModules().size(); j++) {
                    if (doctor.getKey().getModules().get(j).getValue()) {
                        doctorsModules.add(new Pair<>(doctor, doctor.getKey().getModules().get(j).getKey()));
                        for (int k = 0; k < workHoursCoef.size(); k++) {
                            if (Objects.equals(workHoursCoef.get(k).getKey(), doctor.getKey().getModules().get(j).getKey())) {
                                workHoursCoef.set(k, new Pair<>(workHoursCoef.get(k).getKey(), workHoursCoef.get(k).getValue() - doctor.getKey().getWageRate()));
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (Pair<Doctor,Integer> doctor : doctors) {
            if (doctor.getKey().getNumberOfModules() == 2) {
                ArrayList<String> mdls = new ArrayList<>();
                for (int j = 0; j < doctor.getKey().getModules().size(); j++) {
                    if (doctor.getKey().getModules().get(j).getValue()) {
                        mdls.add(doctor.getKey().getModules().get(j).getKey());
                    }
                }
                ArrayList<Pair<String, Double>> wkc = new ArrayList<>();
                for (Pair<String, Double> stringDoublePair : workHoursCoef) {
                    for (String mdl : mdls) {
                        if (Objects.equals(stringDoublePair.getKey(), mdl)) {
                            wkc.add(new Pair<>(mdl, stringDoublePair.getValue()));
                        }
                    }
                }
                Pair<String, Double> max = wkc.get(0);
                for (Pair<String, Double> stringDoublePair : wkc) {
                    if (max.getValue() < stringDoublePair.getValue()) {
                        max = stringDoublePair;
                    }
                }
                doctorsModules.add(new Pair<>(doctor, max.getKey()));
                int index = workHoursCoef.indexOf(max);
                workHoursCoef.set(index, new Pair<>(max.getKey(), max.getValue() - doctor.getKey().getWageRate()));
            }
        }

        for (Pair<Doctor,Integer> doctor : doctors) {
            if (doctor.getKey().getNumberOfModules() == 3) {
                ArrayList<String> mdls = new ArrayList<>();
                for (int j = 0; j < doctor.getKey().getModules().size(); j++) {
                    if (doctor.getKey().getModules().get(j).getValue()) {
                        mdls.add(doctor.getKey().getModules().get(j).getKey());
                    }
                }
                ArrayList<Pair<String, Double>> wkc = new ArrayList<>();
                for (Pair<String, Double> stringDoublePair : workHoursCoef) {
                    for (String mdl : mdls) {
                        if (Objects.equals(stringDoublePair.getKey(), mdl)) {
                            wkc.add(new Pair<>(mdl, stringDoublePair.getValue()));
                        }
                    }
                }
                Pair<String, Double> max = wkc.get(0);
                for (Pair<String, Double> stringDoublePair : wkc) {
                    if (max.getValue() < stringDoublePair.getValue()) {
                        max = stringDoublePair;
                    }
                }
                doctorsModules.add(new Pair<>(doctor, max.getKey()));
                int index = workHoursCoef.indexOf(max);
                workHoursCoef.set(index, new Pair<>(max.getKey(), max.getValue() - doctor.getKey().getWageRate()));
            }
        }

        for (Pair<Doctor,Integer> doctor : doctors) {
            if (doctor.getKey().getNumberOfModules() == 4) {
                ArrayList<String> mdls = new ArrayList<>();
                for (int j = 0; j < doctor.getKey().getModules().size(); j++) {
                    if (doctor.getKey().getModules().get(j).getValue()) {
                        mdls.add(doctor.getKey().getModules().get(j).getKey());
                    }
                }
                ArrayList<Pair<String, Double>> wkc = new ArrayList<>();
                for (Pair<String, Double> stringDoublePair : workHoursCoef) {
                    for (String mdl : mdls) {
                        if (Objects.equals(stringDoublePair.getKey(), mdl)) {
                            wkc.add(new Pair<>(mdl, stringDoublePair.getValue()));
                        }
                    }
                }
                Pair<String, Double> max = wkc.get(0);
                for (Pair<String, Double> stringDoublePair : wkc) {
                    if (max.getValue() < stringDoublePair.getValue()) {
                        max = stringDoublePair;
                    }
                }
                doctorsModules.add(new Pair<>(doctor, max.getKey()));
                int index = workHoursCoef.indexOf(max);
                workHoursCoef.set(index, new Pair<>(max.getKey(), max.getValue() - doctor.getKey().getWageRate()));
            }
        }

        for (Pair<Doctor,Integer> doctor : doctors) {
            if (doctor.getKey().getNumberOfModules() == 5) {
                ArrayList<String> mdls = new ArrayList<>();
                for (int j = 0; j < doctor.getKey().getModules().size(); j++) {
                    if (doctor.getKey().getModules().get(j).getValue()) {
                        mdls.add(doctor.getKey().getModules().get(j).getKey());
                    }
                }
                ArrayList<Pair<String, Double>> wkc = new ArrayList<>();
                for (Pair<String, Double> stringDoublePair : workHoursCoef) {
                    for (String mdl : mdls) {
                        if (Objects.equals(stringDoublePair.getKey(), mdl)) {
                            wkc.add(new Pair<>(mdl, stringDoublePair.getValue()));
                        }
                    }
                }
                Pair<String, Double> max = wkc.get(0);
                for (Pair<String, Double> stringDoublePair : wkc) {
                    if (max.getValue() < stringDoublePair.getValue()) {
                        max = stringDoublePair;
                    }
                }
                doctorsModules.add(new Pair<>(doctor, max.getKey()));
                int index = workHoursCoef.indexOf(max);
                workHoursCoef.set(index, new Pair<>(max.getKey(), max.getValue() - doctor.getKey().getWageRate()));
            }
        }

        for (Pair<Doctor,Integer> doctor : doctors) {
            if (doctor.getKey().getNumberOfModules() == 6) {
                ArrayList<String> mdls = new ArrayList<>();
                for (int j = 0; j < doctor.getKey().getModules().size(); j++) {
                    if (doctor.getKey().getModules().get(j).getValue()) {
                        mdls.add(doctor.getKey().getModules().get(j).getKey());
                    }
                }
                ArrayList<Pair<String, Double>> wkc = new ArrayList<>();
                for (Pair<String, Double> stringDoublePair : workHoursCoef) {
                    for (String mdl : mdls) {
                        if (Objects.equals(stringDoublePair.getKey(), mdl)) {
                            wkc.add(new Pair<>(mdl, stringDoublePair.getValue()));
                        }
                    }
                }
                Pair<String, Double> max = wkc.get(0);
                for (Pair<String, Double> stringDoublePair : wkc) {
                    if (max.getValue() < stringDoublePair.getValue()) {
                        max = stringDoublePair;
                    }
                }
                doctorsModules.add(new Pair<>(doctor, max.getKey()));
                int index = workHoursCoef.indexOf(max);
                workHoursCoef.set(index, new Pair<>(max.getKey(), max.getValue() - doctor.getKey().getWageRate()));
            }
        }

        doctorsModules.sort( (a,b) -> { return a.getValue().compareTo(b.getValue()); } );

        return doctorsModules;
    }

    public static ArrayList<Pair<Pair<Doctor,Integer>,WorkSchedule>> generate() {
        ArrayList<Pair<Pair<Doctor,Integer>, String>> doctorsModules = GenerateSchedule.getDoctorsModules();
        ArrayList<Pair<Pair<Doctor,Integer>, WorkSchedule>> generatedSchedule = new ArrayList<>();
        int a = 0, b = 0, c = 0, d = 0;
        int offsetFiveTwo = 0, offsetOneThree = 0, offsetOneFour = 0;
        for (int i = 0; i < doctorsModules.size(); i++) {
            double epsilon = 0.01;
            if (Math.abs(1 - doctorsModules.get(i).getKey().getKey().getWageRate()) < epsilon) {
                switch (a) {
                    case 0: {
                        WorkSchedule temp = new WorkSchedule(WorkSchedules.FullTimeEightHoursFiveTwoMorning, offsetFiveTwo);
                        generatedSchedule.add(new Pair<>(doctorsModules.get(i).getKey(),temp));
                        break;
                    }
                    case 1: {
                        WorkSchedule temp = new WorkSchedule(WorkSchedules.FullTimeEightHoursFiveTwoDay, offsetFiveTwo);
                        generatedSchedule.add(new Pair<>(doctorsModules.get(i).getKey(),temp));
                        break;
                    }
                    case 2: {
                        WorkSchedule temp = new WorkSchedule(WorkSchedules.FullTimeEightHoursFiveTwoNight, offsetFiveTwo);
                        generatedSchedule.add(new Pair<>(doctorsModules.get(i).getKey(),temp));
                        break;
                    }
                }
                a = (a + 1)%3;
                offsetFiveTwo = (offsetFiveTwo + 1) % 7;
            }
            if (Math.abs(0.75 - doctorsModules.get(i).getKey().getKey().getWageRate()) < epsilon){
                switch (b) {
                    case 0: {
                        WorkSchedule temp = new WorkSchedule(WorkSchedules.SeventyFivePercentSixHoursFiveTwoMorning, offsetFiveTwo);
                        generatedSchedule.add(new Pair<>(doctorsModules.get(i).getKey(),temp));
                        break;
                    }
                    case 1: {
                        WorkSchedule temp = new WorkSchedule(WorkSchedules.SeventyFivePercentSixHoursFiveTwoDay, offsetFiveTwo);
                        generatedSchedule.add(new Pair<>(doctorsModules.get(i).getKey(),temp));
                        break;
                    }
                    case 2: {
                        WorkSchedule temp = new WorkSchedule(WorkSchedules.SeventyFivePercentSixHoursFiveTwoEvening, offsetFiveTwo);
                        generatedSchedule.add(new Pair<>(doctorsModules.get(i).getKey(),temp));
                        break;
                    }
                    case 3: {
                        WorkSchedule temp = new WorkSchedule(WorkSchedules.SeventyFivePercentSixHoursFiveTwoNight, offsetFiveTwo);
                        generatedSchedule.add(new Pair<>(doctorsModules.get(i).getKey(),temp));
                        break;
                    }
                }
                b = (b + 1)%4;
                offsetFiveTwo = (offsetFiveTwo + 1) % 5;
            }
            if (Math.abs(0.5 - doctorsModules.get(i).getKey().getKey().getWageRate()) < epsilon){
                switch (c) {
                    case 0: {
                        WorkSchedule temp = new WorkSchedule(WorkSchedules.FiftyPercentTwelveHorsDay, offsetOneThree);
                        generatedSchedule.add(new Pair<>(doctorsModules.get(i).getKey(),temp));
                        break;
                    }
                    case 1: {
                        WorkSchedule temp = new WorkSchedule(WorkSchedules.FiftyPercentTwelveHoursNight, offsetOneThree);
                        generatedSchedule.add(new Pair<>(doctorsModules.get(i).getKey(),temp));
                        break;
                    }
                }
                c = (c + 1)%2;
                offsetOneThree = (offsetOneThree + 1) % 4;
            }
            if (Math.abs(0.25 - doctorsModules.get(i).getKey().getKey().getWageRate()) < epsilon){
                switch (d) {
                    case 0: {
                        WorkSchedule temp = new WorkSchedule(WorkSchedules.TwentyFivePercentEightHoursDay, offsetOneFour);
                        generatedSchedule.add(new Pair<>(doctorsModules.get(i).getKey(),temp));
                        break;
                    }
                    case 1: {
                        WorkSchedule temp = new WorkSchedule(WorkSchedules.TwentyFivePercentEightHoursNight, offsetOneFour);
                        generatedSchedule.add(new Pair<>(doctorsModules.get(i).getKey(),temp));
                        break;
                    }
                }
                d = (c + 1)%2;
                offsetOneFour = (offsetOneFour + 1) % 4;
            }
            if (Math.abs(0.1 - doctorsModules.get(i).getKey().getKey().getWageRate()) < epsilon){
                switch (d) {
                    case 0: {
                        WorkSchedule temp = new WorkSchedule(WorkSchedules.TenPercentEightHoursDay, 12);
                        generatedSchedule.add(new Pair<>(doctorsModules.get(i).getKey(),temp));
                        break;
                    }
                    case 1: {
                        WorkSchedule temp = new WorkSchedule(WorkSchedules.TenPercentEightHoursNight, 12);
                        generatedSchedule.add(new Pair<>(doctorsModules.get(i).getKey(),temp));
                        break;
                    }
                }
            }
        }
        generatedSchedule.sort( (m,l) -> { return m.getKey().getValue() - l.getKey().getValue(); } );

        return generatedSchedule;
    }

    public static void toExcel (int numberOfDays) {
        ArrayList<Pair<Pair<Doctor, Integer>, WorkSchedule>> schedule = GenerateSchedule.generate();

        String excelFilePath = "РасписаниеВрачей.xlsx";
        Workbook workbook;
        try {
            FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
            workbook = WorkbookFactory.create(inputStream);

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 0; i < schedule.size(); i++) {
                int off = 6, j;
                int value = schedule.get(i).getKey().getValue() * 4 + 2;
                Row row;
                Cell cell;
                switch(schedule.get(i).getValue().getSchedule()){
                    case FullTimeEightHoursFiveTwoMorning:{
                        CellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                        style.setFillPattern(FillPatternType.DIAMONDS);
                        style.setAlignment(HorizontalAlignment.CENTER);
                        for (int k = 0; k < 4; k++) {
                            row = sheet.getRow(value + k);
                            j = 0;
                            while (j < 15) {
                                cell = row.createCell(off + j);
                                if (((j + schedule.get(i).getValue().getDateOffset()) % 7 % 6 == 0) || ((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 == 0)) {
                                    ++j;
                                    continue;
                                }
                                switch (k) {
                                    case 0: {
                                        cell.setCellValue("0:00");
                                        break;
                                    }
                                    case 1: {
                                        cell.setCellValue("8:30");
                                        break;
                                    }
                                    case 2: {
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3: {
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                            ++j;
                            while (j < numberOfDays + 1) {
                                cell = row.createCell(off + j);
                                if ((((j + schedule.get(i).getValue().getDateOffset()) - 1) % 7 % 6 == 0) || (((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 - 1) == 0)) {
                                    ++j;
                                    continue;
                                }
                                switch (k) {
                                    case 0: {
                                        cell.setCellValue("0:00");
                                        break;
                                    }
                                    case 1: {
                                        cell.setCellValue("8:30");
                                        break;
                                    }
                                    case 2: {
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3: {
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                        }

                        break;
                    }
                    case FullTimeEightHoursFiveTwoDay: {
                        CellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                        style.setFillPattern(FillPatternType.DIAMONDS);
                        style.setAlignment(HorizontalAlignment.CENTER);
                        for (int k = 0; k < 4; k++) {
                            row = sheet.getRow(value + k);
                            j = 0;
                            while (j < 15) {
                                cell = row.createCell(off + j);
                                if (((j + schedule.get(i).getValue().getDateOffset()) % 7 % 6 == 0) || ((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 == 0)) {
                                    ++j;
                                    continue;
                                }
                                switch (k) {
                                    case 0: {
                                        cell.setCellValue("8:00");
                                        break;
                                    }
                                    case 1: {
                                        cell.setCellValue("16:30");
                                        break;
                                    }
                                    case 2: {
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3: {
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                            ++j;
                            while (j < numberOfDays + 1) {
                                cell = row.createCell(off + j);
                                if ((((j + schedule.get(i).getValue().getDateOffset()) - 1) % 7 % 6 == 0) || (((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 - 1) == 0)) {
                                    ++j;
                                    continue;
                                }
                                switch (k) {
                                    case 0: {
                                        cell.setCellValue("8:00");
                                        break;
                                    }
                                    case 1: {
                                        cell.setCellValue("16:30");
                                        break;
                                    }
                                    case 2: {
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3: {
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                        }

                        break;
                    }
                    case FullTimeEightHoursFiveTwoNight: {
                        CellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                        style.setFillPattern(FillPatternType.DIAMONDS);
                        style.setAlignment(HorizontalAlignment.CENTER);
                        for (int k = 0; k < 4; k++) {
                            row = sheet.getRow(value + k);
                            j = 0;
                            while (j < 15) {
                                cell = row.createCell(off + j);
                                if (((j + schedule.get(i).getValue().getDateOffset()) % 7 % 6 == 0) || ((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 == 0)) {
                                    ++j;
                                    continue;
                                }
                                switch (k) {
                                    case 0: {
                                        cell.setCellValue("16:00");
                                        break;
                                    }
                                    case 1: {
                                        cell.setCellValue("0:30");
                                        break;
                                    }
                                    case 2: {
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3: {
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                            ++j;
                            while (j < numberOfDays + 1) {
                                cell = row.createCell(off + j);
                                if ((((j + schedule.get(i).getValue().getDateOffset()) - 1) % 7 % 6 == 0) || (((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 - 1) == 0)) {
                                    ++j;
                                    continue;
                                }
                                switch (k) {
                                    case 0: {
                                        cell.setCellValue("16:00");
                                        break;
                                    }
                                    case 1: {
                                        cell.setCellValue("0:30");
                                        break;
                                    }
                                    case 2: {
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3: {
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                        }
                        break;
                    }

                    case SeventyFivePercentSixHoursFiveTwoMorning: {
                        CellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                        style.setFillPattern(FillPatternType.DIAMONDS);
                        style.setAlignment(HorizontalAlignment.CENTER);
                        for (int k = 0; k < 4; k++) {
                            row = sheet.getRow(value + k);
                            j = 0;
                            while (j < 15) {
                                cell = row.createCell(off + j);
                                if (((j + schedule.get(i).getValue().getDateOffset()) % 7 % 6 == 0) || ((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 == 0)) {
                                    ++j;
                                    continue;
                                }
                                switch (k) {
                                    case 0: {
                                        cell.setCellValue("0:00");
                                        break;
                                    }
                                    case 1: {
                                        cell.setCellValue("6:30");
                                        break;
                                    }
                                    case 2: {
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3: {
                                        cell.setCellValue(6);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                            ++j;
                            while (j < numberOfDays + 1) {
                                cell = row.createCell(off + j);
                                if ((((j + schedule.get(i).getValue().getDateOffset()) - 1) % 7 % 6 == 0) || (((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 - 1) == 0)) {
                                    ++j;
                                    continue;
                                }
                                switch (k) {
                                    case 0: {
                                        cell.setCellValue("0:00");
                                        break;
                                    }
                                    case 1: {
                                        cell.setCellValue("6:30");
                                        break;
                                    }
                                    case 2: {
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3: {
                                        cell.setCellValue(6);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                        }
                        break;
                    }
                    case SeventyFivePercentSixHoursFiveTwoDay: {
                        CellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                        style.setFillPattern(FillPatternType.DIAMONDS);
                        style.setAlignment(HorizontalAlignment.CENTER);
                        for (int k = 0; k < 4; k++) {
                            row = sheet.getRow(value + k);
                            j = 0;
                            while (j < 15) {
                                cell = row.createCell(off + j);
                                if (((j + schedule.get(i).getValue().getDateOffset()) % 7 % 6 == 0) || ((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 == 0)) {
                                    ++j;
                                    continue;
                                }
                                switch (k) {
                                    case 0: {
                                        cell.setCellValue("6:00");
                                        break;
                                    }
                                    case 1: {
                                        cell.setCellValue("12:30");
                                        break;
                                    }
                                    case 2: {
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3: {
                                        cell.setCellValue(6);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                            ++j;
                            while (j < numberOfDays + 1) {
                                cell = row.createCell(off + j);
                                if ((((j + schedule.get(i).getValue().getDateOffset()) - 1) % 7 % 6 == 0) || (((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 - 1) == 0)) {
                                    ++j;
                                    continue;
                                }
                                switch (k) {
                                    case 0: {
                                        cell.setCellValue("6:00");
                                        break;
                                    }
                                    case 1: {
                                        cell.setCellValue("12:30");
                                        break;
                                    }
                                    case 2: {
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3: {
                                        cell.setCellValue(6);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                        }
                        break;
                    }
                    case SeventyFivePercentSixHoursFiveTwoEvening: {
                        CellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                        style.setFillPattern(FillPatternType.DIAMONDS);
                        style.setAlignment(HorizontalAlignment.CENTER);
                        for (int k = 0; k < 4; k++) {
                            row = sheet.getRow(value + k);
                            j = 0;
                            while (j < 15) {
                                cell = row.createCell(off + j);
                                if (((j + schedule.get(i).getValue().getDateOffset()) % 7 % 6 == 0) || ((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 == 0)) {
                                    ++j;
                                    continue;
                                }
                                switch (k) {
                                    case 0: {
                                        cell.setCellValue("12:00");
                                        break;
                                    }
                                    case 1: {
                                        cell.setCellValue("18:30");
                                        break;
                                    }
                                    case 2: {
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3: {
                                        cell.setCellValue(6);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                            ++j;
                            while (j < numberOfDays + 1) {
                                cell = row.createCell(off + j);
                                if ((((j + schedule.get(i).getValue().getDateOffset()) - 1) % 7 % 6 == 0) || (((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 - 1) == 0)) {
                                    ++j;
                                    continue;
                                }
                                switch (k) {
                                    case 0: {
                                        cell.setCellValue("12:00");
                                        break;
                                    }
                                    case 1: {
                                        cell.setCellValue("18:30");
                                        break;
                                    }
                                    case 2: {
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3: {
                                        cell.setCellValue(6);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                        }
                        break;
                    }
                    case SeventyFivePercentSixHoursFiveTwoNight: {
                        CellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                        style.setFillPattern(FillPatternType.DIAMONDS);
                        style.setAlignment(HorizontalAlignment.CENTER);
                        for (int k = 0; k < 4; k++) {
                            row = sheet.getRow(value + k);
                            j = 0;
                            while(j < 15) {
                                cell = row.createCell(off + j);
                                if(((j + schedule.get(i).getValue().getDateOffset()) % 7 % 6 == 0) || ((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 == 0)){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("18:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("0:30");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(6);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                            ++j;
                            while(j < numberOfDays + 1){
                                cell = row.createCell(off + j);
                                if((((j + schedule.get(i).getValue().getDateOffset()) - 1) % 7 % 6 == 0) || (((j + schedule.get(i).getValue().getDateOffset()) % 7 % 7 - 1 ) == 0)){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("18:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("0:30");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(6);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                        }

                        break;
                    }

                    case FiftyPercentTwelveHorsDay: {
                        CellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        style.setAlignment(HorizontalAlignment.CENTER);
                        for (int k = 0; k < 4; k++) {
                            row = sheet.getRow(value + k);
                            j = 0;
                            while(j < 15) {
                                cell = row.createCell(off + j);
                                if(((j + schedule.get(i).getValue().getDateOffset()) % 4 != 0)){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("6:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("19:00");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("60");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(12);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                            ++j;
                            while(j < numberOfDays + 1){
                                cell = row.createCell(off + j);
                                if(((j + schedule.get(i).getValue().getDateOffset()) - 1) % 4 != 0){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("6:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("19:00");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("60");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(12);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                        }

                        break;
                    }
                    case FiftyPercentTwelveHoursNight: {
                        CellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
                        style.setFillPattern(FillPatternType.DIAMONDS);
                        style.setAlignment(HorizontalAlignment.CENTER);
                        for (int k = 0; k < 4; k++) {
                            row = sheet.getRow(value + k);
                            j = 0;
                            while(j < 15) {
                                cell = row.createCell(off + j);
                                if(((j + schedule.get(i).getValue().getDateOffset()) % 4 != 0)){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("18:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("7:00");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("60");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(12);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                            ++j;
                            while(j < numberOfDays + 1){
                                cell = row.createCell(off + j);
                                if(((j + schedule.get(i).getValue().getDateOffset()) - 1) % 4 != 0){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("18:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("7:00");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("60");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(12);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                        }
                        break;
                    }

                    case TwentyFivePercentEightHoursDay: {
                        CellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                        style.setFillPattern(FillPatternType.DIAMONDS);
                        style.setAlignment(HorizontalAlignment.CENTER);
                        for (int k = 0; k < 4; k++) {
                            row = sheet.getRow(value + k);
                            j = 0;
                            while(j < 15) {
                                cell = row.createCell(off + j);
                                if(((j + schedule.get(i).getValue().getDateOffset()) % 4 != 0)){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("8:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("16:30");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                            ++j;
                            while(j < numberOfDays + 1){
                                cell = row.createCell(off + j);
                                if(((j + schedule.get(i).getValue().getDateOffset()) - 1) % 4 != 0){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("8:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("16:30");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("60");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                        }
                        break;
                    }
                    case TwentyFivePercentEightHoursNight: {
                        CellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                        style.setFillPattern(FillPatternType.DIAMONDS);
                        style.setAlignment(HorizontalAlignment.CENTER);
                        for (int k = 0; k < 4; k++) {
                            row = sheet.getRow(value + k);
                            j = 0;
                            while(j < 15) {
                                cell = row.createCell(off + j);
                                if(((j + schedule.get(i).getValue().getDateOffset()) % 4 != 0)){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("18:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("6:30");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                            ++j;
                            while(j < numberOfDays + 1){
                                cell = row.createCell(off + j);
                                if(((j + schedule.get(i).getValue().getDateOffset()) - 1) % 4 != 0){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("18:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("6:30");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                        }
                        break;
                    }

                    case TenPercentEightHoursDay: {
                        CellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                        style.setFillPattern(FillPatternType.DIAMONDS);
                        style.setAlignment(HorizontalAlignment.CENTER);
                        for (int k = 0; k < 4; k++) {
                            row = sheet.getRow(value + k);
                            j = 0;
                            while(j < 15) {
                                cell = row.createCell(off + j);
                                if(((j + schedule.get(i).getValue().getDateOffset()) % 15 != 0)){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("8:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("16:30");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                            ++j;
                            while(j < numberOfDays + 1){
                                cell = row.createCell(off + j);
                                if(((j + schedule.get(i).getValue().getDateOffset()) - 1) % 15 != 0){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("8:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("16:30");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                        }
                        break;
                    }
                    case TenPercentEightHoursNight: {
                        CellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                        style.setFillPattern(FillPatternType.DIAMONDS);
                        style.setAlignment(HorizontalAlignment.CENTER);
                        for (int k = 0; k < 4; k++) {
                            row = sheet.getRow(value + k);
                            j = 0;
                            while(j < 15) {
                                cell = row.createCell(off + j);
                                if(((j + schedule.get(i).getValue().getDateOffset()) % 15 != 0)){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("18:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("6:30");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                            ++j;
                            while(j < numberOfDays + 1){
                                cell = row.createCell(off + j);
                                if(((j + schedule.get(i).getValue().getDateOffset()) - 1) % 15 != 0){
                                    ++j;
                                    continue;
                                }
                                switch(k){
                                    case 0: {
                                        cell.setCellValue("18:00");
                                        break;
                                    }
                                    case 1:{
                                        cell.setCellValue("6:30");
                                        break;
                                    }
                                    case 2:{
                                        cell.setCellValue("30");
                                        break;
                                    }
                                    case 3:{
                                        cell.setCellValue(8);
                                        break;
                                    }
                                }
                                cell.setCellStyle(style);
                                ++j;
                            }
                        }
                        break;
                    }
                }
            }

            FileOutputStream outputStream = new FileOutputStream("РасписаниеВрачей.xlsx");
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
