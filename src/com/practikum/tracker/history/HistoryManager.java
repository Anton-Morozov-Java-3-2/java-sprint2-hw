package com.practikum.tracker.history;

import java.util.List;
import com.practikum.tracker.model.Task;


public interface HistoryManager {
    void add(Task task);
    void remove(long id);
    List<Task> getHistory();
}
