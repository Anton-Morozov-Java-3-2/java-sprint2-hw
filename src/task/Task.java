package task;

import java.util.ArrayList;

public class Task {
    private String title;
    private String description;
    private long id;
    private Status status;

    public Task(String title, String description, long id, Status status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public long getId() {
        return id;
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
            return new Task(data[2], data[4], Long.parseLong(data[0]), Status.valueOf(data[3]));
        } else {
            return null;
        }
    }
}
