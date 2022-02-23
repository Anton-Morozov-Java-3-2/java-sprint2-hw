package task;

public class Task {
    private String title;
    private String description;
    private int id;
    private String status;

    public static final String NEW = "NEW";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String DONE = "DONE";

    public Task(String title, String description, int id, String status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    protected void setStatus(String status){
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
