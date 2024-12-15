package org.schedule;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Doctor {

    private String name;
    private ArrayList<Pair<String, Boolean>> modules;

    public ArrayList<Pair<String, Boolean>> getModules() {
        return modules;
    }

    public int getNumberOfModules() {
        return numberOfModules;
    }

    private int numberOfModules;
    private double wageRate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWageRate() {
        return wageRate;
    }

    public void setWageRate(double wageRate) {
        this.wageRate = wageRate;
    }

    public Doctor(String name, double wageRate) {
        this.name = name;
        this.wageRate = wageRate;

        modules = new ArrayList<>();
        modules.add(new Pair<>("Денситометрия", false));
        modules.add(new Pair<>("КТ", false));
        modules.add(new Pair<>("ММГ", false));
        modules.add(new Pair<>("МРТ", false));
        modules.add(new Pair<>("РГ", false));
        modules.add(new Pair<>("ФЛГ", false));

        numberOfModules = 0;
    }

    public void changeModules(String key, Boolean a){
        for (int i = 0; i < modules.size(); i++) {
            if(Objects.equals(modules.get(i).getKey(), key)){
                modules.set(i, new Pair<>(key, a));
                if(a){
                    ++numberOfModules;
                }
            }
        }
    }
}
