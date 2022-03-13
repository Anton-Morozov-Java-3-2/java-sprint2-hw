package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    public static final int MAX_TASKS = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            history.add(task);
        }

        if (history.size() > MAX_TASKS) {
            history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory(){
        return history;
    }
}
