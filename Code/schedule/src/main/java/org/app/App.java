package org.app;

import javafx.util.Pair;

import java.util.*;

import org.analysis.Analysis;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.schedule.Doctor;
import org.schedule.GenerateSchedule;
import org.schedule.WorkSchedule;


public class App
{
    public static void main( String[] args ) throws Exception {

        System.out.println("Используйте команды:\n1)'getAnalysis p d q P D Q m' чтобы получить файл АнализДанных.xlsx с анализом работы алгоритма АРИМА (p,d... - параметры алгоритма - числа 0...3 - рекомендуется использовать параметры 3 0 3 1 1 0 0)" +
                "\n2)'getDoctors' чтобы получить файл АнализДокторов.xlsx с анализом модальностей и часов работы докторов" +
                "\n3)'forecast p d q P D Q m' чтобы получить предсказания на следующий месяц - оно запишется в файл ПредсказаниеНаМесяц.xlsx" +
                "\n4)'getSchedule md' чтобы получить расписание врачей на месяц в файле РасписаниеВрачей.xlsx (md - количество дней в предсказуемом месяце)" +
                "\n5)'end' чтобы закончить работу программы");
        boolean prog = true;
        while(prog){
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();
            String[] command = input.split(" ");
            switch(command[0]){
                case "getAnalysis": {
                    int p ,d, q, P, D, Q, m;
                    try{
                        p = Integer.parseInt(command[1]);
                        d = Integer.parseInt(command[2]);
                        q = Integer.parseInt(command[3]);
                        P = Integer.parseInt(command[4]);
                        D = Integer.parseInt(command[5]);
                        Q = Integer.parseInt(command[6]);
                        m = Integer.parseInt(command[7]);
                        if(p < 0 || d < 0 || q < 0 || P < 0 || D < 0 || Q < 0 || m < 0 || p > 3 || d > 3 || q > 3 || P > 3 || D > 3 || Q > 3 || m > 3){
                            throw new Exception("");
                        }
                        Analysis.analysisIntoExcel("pred.xlsx", p,d,q,P,D,Q,m);
                    } catch (Exception e) {
                        System.out.println("Команда не распознана/неподходящие параметры");
                        break;
                    }
                    System.out.println("Команда выполнена. Файл АнализДанных.xlsx обновлён");
                    break;
                }
                case "getDoctors": {
                    Analysis.doctorsToExcel("doc.xlsx");
                    System.out.println("Команда выполнена. Файл АнализДокторов.xlsx обновлён");
                    break;
                }
                case "forecast":{
                    int p ,d, q, P, D, Q, m;
                    try{
                        p = Integer.parseInt(command[1]);
                        d = Integer.parseInt(command[2]);
                        q = Integer.parseInt(command[3]);
                        P = Integer.parseInt(command[4]);
                        D = Integer.parseInt(command[5]);
                        Q = Integer.parseInt(command[6]);
                        m = Integer.parseInt(command[7]);
                        if(p < 0 || d < 0 || q < 0 || P < 0 || D < 0 || Q < 0 || m < 0 || p > 3 || d > 3 || q > 3 || P > 3 || D > 3 || Q > 3 || m > 3){
                            throw new Exception("");
                        }
                        Analysis.nextMonthForecastToExcel("pred.xlsx",p,d,q,P,D,Q,m);
                    } catch (Exception e){
                        System.out.println("Команда не распознана/неподходящие параметры");
                        break;
                    }
                    System.out.println("Команда выполнена. Файл ПредсказаниеНаМесяц.xlsx обновлён");
                    break;
                }
                case "getSchedule":{
                    int num;
                    try{
                        num = Integer.parseInt(command[1]);
                        if(num > 31 || num < 28){
                            throw new Exception("");
                        }
                        GenerateSchedule.toExcel(num);
                    } catch (Exception e) {
                        System.out.println("Команда не распознана");
                        break;
                    }
                    System.out.println("Команда выполнена. Файл РасписаниеВрачей.xlsx обновлён");
                    break;
                }
                case "end":{
                    System.out.println("Закрытие программы...");
                    prog = false;
                    break;
                }
                default:{
                    System.out.println("Команда не распознана");
                    break;
                }
            }
        }

    }
}
