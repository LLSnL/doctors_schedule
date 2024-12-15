package org.schedule;

public class WorkSchedule {
    private WorkSchedules schedule;
    private int dateOffset;

    public WorkSchedules getSchedule() {
        return schedule;
    }

    public void setSchedule(WorkSchedules schedule) {
        this.schedule = schedule;
    }

    public int getDateOffset() {
        return dateOffset;
    }

    public void setDateOffset(int dateOffset) {
        this.dateOffset = dateOffset;
    }

    public WorkSchedule(WorkSchedules schedule, int dateOffset) {
        this.schedule = schedule;
        this.dateOffset = dateOffset;
    }
}
