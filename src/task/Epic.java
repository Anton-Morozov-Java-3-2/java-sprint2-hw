package task;

import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Long> subtasks;

    @Override
    public String toString() {
        return "task.Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", subtasks.length=" + subtasks.size()  +
                '}';
    }

    public Epic(String title, String description, Long id, Status status, ArrayList<Long> subtasks) {
        super(title, description, id, status);
        if (subtasks != null) {
            this.subtasks = subtasks;
        } else {
            this.subtasks = new ArrayList<>();
        }
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
}
