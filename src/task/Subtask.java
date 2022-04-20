package task;

import java.util.ArrayList;

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
        return  getId() + "," + Type.SUBTASK + "," +
                getTitle() + "," + getStatus() + "," +
                getDescription() + "," + getIdEpic() + ",\n";
    }


    static public Subtask fromString(String value) {
        if (value != null) {
            String[] data = value.split(",");

            return new Subtask(data[2], data[4], Long.parseLong(data[0]),
                    Status.valueOf(data[3]), Long.parseLong(data[5]));
        } else {
            return null;
        }
    }
}


