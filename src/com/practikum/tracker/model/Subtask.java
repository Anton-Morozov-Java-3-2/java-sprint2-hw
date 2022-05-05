package com.practikum.tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task{
    private long idEpic;

    public Subtask(String title, String description, Status status, long idEpic) {
        super(title, description, status);
        this.idEpic = idEpic;
    }

    public long getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        if (getStartTime() != null && getDuration() != null) {
            return  getId() + "," + Type.SUBTASK + "," +
                    getTitle() + "," + getStatus() + "," +
                    getDescription() + "," + getDuration() + "," + getStartTime().format(format)+ "," + getIdEpic() + ",\n";
        } else {
            return  getId() + "," + Type.SUBTASK + "," +
                    getTitle() + "," + getStatus() + "," +
                    getDescription() + "," + getIdEpic() + ",\n";
        }
    }

    static public Subtask fromString(String value) {
        if (value != null) {
            String[] data = value.split(",");
            Subtask subtask;
            if (data.length > 6) {
                subtask = new Subtask(data[2], data[4], Status.valueOf(data[3]), Long.parseLong(data[7]));
                subtask.setDuration(Duration.parse(data[5]));
                subtask.setStartTime(LocalDateTime.parse(data[6], format));
            } else {
                subtask = new Subtask(data[2], data[4], Status.valueOf(data[3]), Long.parseLong(data[5]));
            }
            subtask.setId(Long.parseLong(data[0]));
            return subtask;
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqualTask = super.equals(o);
        Subtask subtask = (Subtask) o;
        return isEqualTask && idEpic == subtask.getIdEpic();
    }
}


