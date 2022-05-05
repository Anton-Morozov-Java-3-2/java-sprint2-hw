package com.practikum.tracker.test;

import com.practikum.tracker.history.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryHistoryManagerTest extends HistoryManagerTest{
    @BeforeEach
    public void initManager() {
        manager = new InMemoryHistoryManager();
    }
}
