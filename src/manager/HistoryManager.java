package manager;

import java.util.List;
import task.Task;


public interface HistoryManager {
    public void add(Task task);
    void remove(long id);
    public List<Task> getHistory();
}
