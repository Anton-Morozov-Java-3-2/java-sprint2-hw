package task;

public class Subtask extends Task{
    private int idEpic;

    public Subtask(String title, String description, int id, String status, int idEpic) {
        super(title, description, id, status);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
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
