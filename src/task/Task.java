package task;

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
        return "task.Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
