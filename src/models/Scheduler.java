package models;

import java.io.Serializable;
import java.time.LocalTime;

public class Scheduler implements Serializable {
    boolean is_scheduled;
    LocalTime startTime;
    LocalTime endTime;

    public Scheduler(boolean is_scheduled) {
        this.is_scheduled = is_scheduled;
    }

    public Scheduler(boolean is_scheduled, LocalTime startTime, LocalTime endTime) {
        this.is_scheduled = is_scheduled;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean getIs_scheduled() {
        return is_scheduled;
    }

    public void setIs_scheduled(boolean is_scheduled) {
        this.is_scheduled = is_scheduled;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
