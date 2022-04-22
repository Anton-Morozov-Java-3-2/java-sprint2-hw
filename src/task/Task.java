package task;

import java.util.ArrayList;

public class Task {
    private String title;
    private String description;
    private long id;
    private Status status;

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
    public String toString() {
        return id + "," + Type.TASK + "," + title + "," + status + ","+ description + ",\n";
    }

    static public Task fromString(String value) {
        if (value != null) {
            String[] data = value.split(",");
            Task t = new Task(data[2], data[4], Status.valueOf(data[3]));
            t.setId(Long.parseLong(data[0]));
            return t;
        } else {
            return null;
        }
    }
}
