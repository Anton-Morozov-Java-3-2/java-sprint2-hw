package task;

import java.util.ArrayList;

public class Subtask extends Task{
    private long idEpic;

    public Subtask(String title, String description, Status status, long idEpic) {
        super(title, description, status);
        this.idEpic = idEpic;
    }

    public long getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return  getId() + "," + Type.SUBTASK + "," +
                getTitle() + "," + getStatus() + "," +
                getDescription() + "," + getIdEpic() + ",\n";
    }

    static public Subtask fromString(String value) {
        if (value != null) {
            String[] data = value.split(",");
            Subtask s = new Subtask(data[2], data[4], Status.valueOf(data[3]), Long.parseLong(data[5]));
            s.setId(Long.parseLong(data[0]));
            return s;
        } else {
            return null;
        }
    }
}


