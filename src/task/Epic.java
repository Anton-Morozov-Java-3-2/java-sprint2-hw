package task;

import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Long> subtasks;

    @Override
    public String toString() {
        return  getId() + "," + Type.EPIC + "," +
                getTitle() + "," + getStatus() + "," +
                getDescription() + ",\n";
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
            return e;
        } else {
            return null;
        }
    }
}
