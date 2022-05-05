package com.practikum.tracker.test;

import com.practikum.tracker.manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void initManager() {
        manager = new InMemoryTaskManager();
    }

}
