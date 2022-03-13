package manager;

import task.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private final ArrayList<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            history.add(task);
        }

        if (history.size() > MAX_TASK) {
            history.remove(0);
        }
    }

    @Override
    public ArrayList<Task> getHistory(){
        return history;
    }
}
