package com.practikum.tracker.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.practikum.tracker.model.Status;
import com.practikum.tracker.model.Task;

public class TaskTest {

    @Test
    public void testEquals() {
        Task t1 = new Task("com.practikum.tracker.task 1", "test 1", Status.NEW);
        t1.setId(1);
        Task t2 = new Task("com.practikum.tracker.task 1", "test 1", Status.NEW);
        t2.setId(1);

        Assertions.assertEquals(t2, t1, "объекты должны быть равны");

        Task t3 = new Task("com.practikum.tracker.task 2", "test 2", Status.NEW);
        t2.setId(1);

        Assertions.assertNotEquals(t3, t1, "объекты не должны быть равны");
    }

    @Test
    public void testToString() {
        Task t1 = new Task("com.practikum.tracker.task 1", "test 1", Status.NEW);
        t1.setId(1);
        Assertions.assertEquals("1,TASK,com.practikum.tracker.task 1,NEW,test 1,\n", t1.toString());

    }
}
