package manager;

import java.util.Collection;
import task.Task;


public interface HistoryManager {
    void add(Task task);
    void remove(long id);
    Collection<Task> getHistory();
}
