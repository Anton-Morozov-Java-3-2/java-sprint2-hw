package com.practikum.tracker.manager;

import com.practikum.tracker.history.HistoryManager;
import com.practikum.tracker.history.InMemoryHistoryManager;

public class Managers {
    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }
    public static HistoryManager getHistory() {
        return new InMemoryHistoryManager();
    }
}
