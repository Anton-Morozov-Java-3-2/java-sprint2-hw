package manager;

import java.util.List;
import task.Task;


public interface HistoryManager {
    public static final int MAX_TASK = 10;
    public void add(Task task);
    public List<Task> getHistory();
}
