package com.practikum.tracker.model;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private String title;
    private String description;
    private long id;
    private Status status;

    private LocalDateTime startTime;
    private Duration duration;

    private static final long DEFAULT_DURATION_MINUTES = 15l;

    protected static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = -1L;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.id = -1L;
    }

    public long getId() {
        return id;
    }

    public boolean isNotSetId() {
        return id < 0L;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;

        if (startTime != null && duration != null) {
            return id == task.getId() &&
                    title.equals(task.getTitle()) &&
                    description.equals(task.getDescription()) &&
                    status.equals(task.getStatus()) &&
                    startTime.equals(task.getStartTime()) &&
                    duration.equals(task.getDuration());
        } else if (task.getStartTime() != null && task.getDuration() != null) {
            return false;
        } else {
            if (status != null) {
                return id == task.getId() &&
                        title.equals(task.getTitle()) &&
                        description.equals(task.getDescription()) &&
                        status.equals(task.getStatus());
            } else {
                return id == task.getId() &&
                        title.equals(task.getTitle()) &&
                        description.equals(task.getDescription()) &&
                        status.equals(task.getStatus());
            }
        }
    }

    @Override
    public String toString() {
        if (startTime != null && duration != null) {
            return id + "," + Type.TASK + "," + title + "," + status + ","+ description +","+ duration + "," +
                    startTime.format(format) + ",\n";
        } else {
            return id + "," + Type.TASK + "," + title + "," + status + ","+ description +",\n";
        }
    }

    static public Task fromString(String value) {
        if (value != null) {
            String[] data = value.split(",");
            Task t = new Task(data[2], data[4], Status.valueOf(data[3]));
            t.setId(Long.parseLong(data[0]));
            if (data.length > 5) {
                t.setDuration(Duration.parse(data[5]));
                t.setStartTime(LocalDateTime.parse(data[6], format));
                return t;
            }
            return t;
        } else {
            return null;
        }
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime(){
        if (startTime != null) {
            return startTime.plus(duration);
        } else {
            return null;
        }
    }

    public void setDefaultTimeAndDuration(){

        startTime = LocalDateTime.now().withNano(0);

        duration = Duration.ofMinutes(DEFAULT_DURATION_MINUTES);
    }

    public static Gson getObjectJSON() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                new LocalDateAdapter()).registerTypeAdapter(Duration.class,
                new DurationAdapter()).create();
    }

    public String toJSON(){
        return getObjectJSON().toJson(this);
    }

    public static Task fromJSON(String json){
        return getObjectJSON().fromJson(json, Task.class);
    }

    public static List<Task> tasksFromJSON(String json){
        if (json.isEmpty()) {
            return new ArrayList<>();
        } else {
            return List.of(getObjectJSON().fromJson(json, Task[].class));
        }
    }
}

class LocalDateAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime != null) {
            jsonWriter.value(localDateTime.format(Task.format));
        } else {
            jsonWriter.value("null");
        }
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String json = jsonReader.nextString();
        if (json.equals("null")) {
            return null;
        } else {
            return LocalDateTime.parse(json, Task.format);
        }
    }
}

class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration != null) {
            jsonWriter.value(duration.toMinutes());
        } else {
            jsonWriter.value("null");
        }
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        String json = jsonReader.nextString();
        if (json.equals("null")) {
            return null;
        }  else {
            return Duration.ofMinutes(Long.parseLong(json));
        }
    }
}
