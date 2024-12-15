package org.schedule;

public class Module {
    private String name;
    private Integer numberOfResearches;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumberOfResearches() {
        return numberOfResearches;
    }

    public void setNumberOfResearches(Integer numberOfResearches) {
        this.numberOfResearches = numberOfResearches;
    }

    public Module(String name, Integer numberOfResearches) {
        this.name = name;
        this.numberOfResearches = numberOfResearches;
    }
}
