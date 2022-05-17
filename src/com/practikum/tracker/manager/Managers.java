package com.practikum.tracker.manager;

import com.practikum.tracker.history.HistoryManager;
import com.practikum.tracker.history.InMemoryHistoryManager;
import com.practikum.tracker.server.KVServer;

import java.io.File;

public class Managers {
    public static TaskManager getDefault(){
        return new HTTPTaskManager("http://localhost:" + KVServer.PORT);
    }
    public static HistoryManager getHistory() {
        return new InMemoryHistoryManager();
    }
}
