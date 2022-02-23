package task;

import java.util.ArrayList;

public class Epic extends Task{
    ArrayList<Integer> subtasks;

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

    public Epic(String title, String description, int id, String status, ArrayList<Integer> subtasks) {
        super(title, description, id, status);
        if (subtasks != null) {
            this.subtasks = subtasks;
        } else {
            this.subtasks = new ArrayList<>();
        }
    }

    public void addSubtask(Integer id) {
        if (subtasks != null && !subtasks.contains(id)) {
            subtasks.add(id);
        }
    }

    public void removeSubtaskById(Integer id) {
        if (subtasks != null) subtasks.remove(id);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }
}
