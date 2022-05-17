package com.practikum.tracker.test;

import com.practikum.tracker.manager.HTTPTaskManager;
import com.practikum.tracker.model.Epic;
import com.practikum.tracker.model.Status;
import com.practikum.tracker.model.Subtask;
import com.practikum.tracker.model.Task;
import com.practikum.tracker.server.KVServer;
import org.junit.jupiter.api.*;

import java.io.IOException;

public class HTTPTaskManagerTest {
    private KVServer server;

    @BeforeEach
    void runServer() {
        try {
            server = new KVServer();
            server.start();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @AfterEach
    void stopServer(){
        server.stop();
    }

    @Test
    void standardActionSaveState(){
        //тест на восстановление менеджера задач

        HTTPTaskManager manager = new HTTPTaskManager("http://localhost:" + KVServer.PORT);
        Task task0 = new Task("task", "test", Status.NEW);
        Epic epic1 = new Epic("epic1", "test");
        Subtask subtask2 = new Subtask("subtask", "test",Status.NEW, 1L);
        subtask2.setDefaultTimeAndDuration();
        Epic epic3 = new Epic("epic3", "test");

        manager.createTask(task0);
        manager.createEpic(epic1);
        manager.createSubtask(subtask2);
        manager.createEpic(epic3);

        manager.getTask(0L);
        manager.getEpic(1L);

        HTTPTaskManager newManager = new HTTPTaskManager("http://localhost:" + KVServer.PORT, manager.getAPI_TOKEN());

        Assertions.assertArrayEquals(manager.getHistory().toArray(), newManager.getHistory().toArray());
        Assertions.assertEquals(manager.getTask(0L), newManager.getTask(0L));
        Assertions.assertEquals(manager.getSubtask(2L), newManager.getSubtask(2L));
        Assertions.assertEquals(manager.getEpic(3L), newManager.getEpic(3L));
        Assertions.assertEquals(manager.getEpic(1L), newManager.getEpic(1L));
    }
}
