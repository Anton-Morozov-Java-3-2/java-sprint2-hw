package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.util.List;

abstract class HistoryManagerTest  {
    public HistoryManager manager;

    @Test
    public void standardActionAdd() {
        Task task = new Task("task", "test", Status.NEW);
        task.setId(1L);

        manager.add(task);

        List<Task> history = manager.getHistory();

        Assertions.assertNotNull(history, "история не пустая");
        Assertions.assertEquals(1, history.size(),"история не пустая");
        Assertions.assertEquals(task, history.get(0), "объекты не совпадают");
    }

    @Test
    public void actionAddDuplicateTask() {
        Task task = new Task("task", "test", Status.NEW);
        task.setId(1L);

        manager.add(task);
        manager.add(task);

        List<Task> history = manager.getHistory();

        Assertions.assertNotNull(history, "история не пустая");
        Assertions.assertEquals(1, history.size(),"история не пустая");
        Assertions.assertEquals(task, history.get(0), "объекты не совпадают");
    }

    @Test
    public void actionGetEmptyHistory() {
        Assertions.assertDoesNotThrow(
                () -> manager.getHistory(), "вызвано исключение"
        );
        Assertions.assertNotNull(manager.getHistory(), "получен null");
        Assertions.assertTrue(manager.getHistory().isEmpty(), "список не пустой");
    }

    @Test
    public void standardActionRemoveFirst(){
        Task task1 = new Task("task 1", "test", Status.NEW);
        task1.setId(1L);
        manager.add(task1);

        Task task2 = new Task("task 2", "test", Status.NEW);
        task2.setId(2L);
        manager.add(task2);

        Task task3 = new Task("task 3", "test", Status.NEW);
        task3.setId(3L);
        manager.add(task3);

        manager.remove(task1.getId());

        List<Task> history = manager.getHistory();
        Assertions.assertNotNull(history, "история не пустая");
        Assertions.assertEquals(2, history.size(), "в истории есть 2 элемента");
        Assertions.assertEquals(task2, history.get(0), "объект не совпадает");
        Assertions.assertEquals(task3, history.get(1), "объект не совпадает");
    }

    @Test
    public void standardActionRemoveLast(){
        Task task1 = new Task("task 1", "test", Status.NEW);
        task1.setId(1L);
        manager.add(task1);

        Task task2 = new Task("task 2", "test", Status.NEW);
        task2.setId(2L);
        manager.add(task2);

        Task task3 = new Task("task 3", "test", Status.NEW);
        task3.setId(3L);
        manager.add(task3);

        manager.remove(task3.getId());

        List<Task> history = manager.getHistory();
        Assertions.assertNotNull(history, "история не пустая");
        Assertions.assertEquals(2, history.size(), "в истории есть 2 элемента");
        Assertions.assertEquals(task1, history.get(0), "объект не совпадает");
        Assertions.assertEquals(task2, history.get(1), "объект не совпадает");
    }

    @Test
    public void standardActionRemoveMiddle(){
        Task task1 = new Task("task 1", "test", Status.NEW);
        task1.setId(1L);
        manager.add(task1);

        Task task2 = new Task("task 2", "test", Status.NEW);
        task2.setId(2L);
        manager.add(task2);

        Task task3 = new Task("task 3", "test", Status.NEW);
        task3.setId(3L);
        manager.add(task3);

        manager.remove(task2.getId());

        List<Task> history = manager.getHistory();
        Assertions.assertNotNull(history, "история не пустая");
        Assertions.assertEquals(2, history.size(), "в истории есть 2 элемента");
        Assertions.assertEquals(task1, history.get(0), "объект не совпадает");
        Assertions.assertEquals(task3, history.get(1), "объект не совпадает");
    }

    @Test
    public void actionRemoveFromEmpty(){
        Assertions.assertDoesNotThrow(
                ()->manager.remove(1L), "вызвано исключение при удалении по несуществующему id"
        );
    }


}
