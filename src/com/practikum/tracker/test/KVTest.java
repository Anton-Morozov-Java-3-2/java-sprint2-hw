package com.practikum.tracker.test;

import com.practikum.tracker.server.KVServer;
import com.practikum.tracker.server.KVTaskClient;
import org.junit.jupiter.api.*;

import java.io.IOException;


public class KVTest {
    private KVServer server;

    @BeforeEach
    void runKVServer() {
        try {
            server = new KVServer();
            server.start();
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e);
        }
    }

    @Test
    void standardActionSaveAndLoad() {
        try {
            KVTaskClient client = new KVTaskClient("http://localhost:" + KVServer.PORT);
            String  expected = "test";
            client.put("1", "test");
            String actual = client.load("1");
            Assertions.assertEquals(expected, actual);
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
    }

    @Test
    void ActionSaveAndLoadByErrorKey() {
        try {
            KVTaskClient client = new KVTaskClient("http://localhost:" + KVServer.PORT);
            String  expected = "test";
            client.put("1", "test");
            String actual = client.load("2");
            Assertions.assertEquals("", actual);
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
    }

    @Test
    void standardActionUpdate() {
        try {
            KVTaskClient client = new KVTaskClient("http://localhost:" + KVServer.PORT);
            String  expected = "test2";
            client.put("1", "test");
            client.put("1", "test2");
            String actual = client.load("1");
            Assertions.assertEquals(expected, actual);
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
    }
}
