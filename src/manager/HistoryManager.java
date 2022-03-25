package manager;

import java.util.Collection;
import task.Task;


public interface HistoryManager {
    public void add(Task task);
    void remove(long id);
    public Collection<Task> getHistory();
}
