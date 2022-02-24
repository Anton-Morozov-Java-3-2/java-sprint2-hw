package task;

public class Subtask extends Task{
    private long idEpic;

    public Subtask(String title, String description, long id, Status status, long idEpic) {
        super(title, description, id, status);
        this.idEpic = idEpic;
    }

    public long getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "task.Subtask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", idEpic=" + getIdEpic() +
                '}';
    }
}
