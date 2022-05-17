package com.practikum.tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{
    private final ArrayList<Long> subtasks;
    private LocalDateTime endTime;

    @Override
    public String toString() {
        if (getDuration() != null && getStartTime() != null) {
            return  getId() + "," + Type.EPIC + "," +
                    getTitle() + "," + getStatus() + "," +
                    getDescription()  + "," + getDuration() + "," + getStartTime().format(format) + ",\n";
        } else {
            return  getId() + "," + Type.EPIC + "," +
                    getTitle() + "," + getStatus() + "," +
                    getDescription()  + ",\n";
        }
    }

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Long id) {
        if (subtasks != null && !subtasks.contains(id)) {
            subtasks.add(id);
        }
    }

    public void removeSubtaskById(Long id) {
        if (subtasks != null) subtasks.remove(id);
    }

    public ArrayList<Long> getSubtasks() {
        return subtasks;
    }

    public void removeAllSubtasks(){
        subtasks.clear();
    }

    static public Epic fromString(String value) {
        if (value != null) {
            String[] data = value.split(",");
            Epic e = new Epic(data[2], data[4]);
            e.setId(Long.parseLong(data[0]));
            e.setStatus(Status.valueOf(data[3]));
            if (data.length > 5) {
                e.setDuration(Duration.parse(data[5]));
                e.setStartTime(LocalDateTime.parse(data[6], format));
                return e;
            }
            return e;
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqualTask = super.equals(o);
        Epic epic = (Epic) o;
        List<Long> epicSubtasks = epic.getSubtasks();

        for (Long id: subtasks) {
            if (!epicSubtasks.contains(id)) return false;
        }
        return isEqualTask;
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    @Override
    public LocalDateTime getEndTime(){
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String toJSON(){
        return getObjectJSON().toJson(this);
    }

    public static Task fromJSON(String json){
        return getObjectJSON().fromJson(json, Epic.class);
    }

    public static List<Task> tasksFromJSON(String json){
        if (json.isEmpty()) {
            return new ArrayList<>();
        } else {
            return List.of(getObjectJSON().fromJson(json, Epic[].class));
        }
    }
}
