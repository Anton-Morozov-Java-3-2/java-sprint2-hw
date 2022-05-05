package com.practikum.tracker.test;

import com.practikum.tracker.manager.FileBackedTasksManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.practikum.tracker.model.Epic;
import com.practikum.tracker.model.Status;
import com.practikum.tracker.model.Subtask;
import com.practikum.tracker.model.Task;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class FileBackendTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private File file;

    @BeforeEach
    public void initManager() {
        file = new File("file.csv");
        manager = new FileBackedTasksManager(file);
    }

    @Test
    public void standardActionSave(){
        StringBuilder savedString = new StringBuilder();

        Task task = new Task("com.practikum.tracker.task 0", "test", Status.NEW);
        task.setDefaultTimeAndDuration();
        manager.createTask(task);

        Epic epic = new Epic("epic 0", "test");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("subtask 1", "test", Status.NEW, epic.getId());
        subtask1.setDefaultTimeAndDuration();
        Subtask subtask2 = new Subtask("subtask 2", "test", Status.IN_PROGRESS, epic.getId());
        subtask2.setDefaultTimeAndDuration();
        Subtask subtask3 = new Subtask("subtask 3", "test", Status.DONE, epic.getId());
        subtask3.setDefaultTimeAndDuration();

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        // в строго таком порядке
        savedString.append(task.toString());
        savedString.append(epic.toString());
        savedString.append(subtask1.toString());
        savedString.append(subtask2.toString());
        savedString.append(subtask3.toString());
        savedString.append("\n");
        manager.getTask(task.getId());
        savedString.append(task.getId()).append(",");
        manager.getSubtask(subtask1.getId());
        savedString.append(subtask1.getId()).append(",");
        manager.getEpic(epic.getId());
        savedString.append(epic.getId());

        String expected = savedString.toString();

        String[] actual = new String[1];

        Assertions.assertDoesNotThrow(
                () -> {
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                    if (in.available() > 0) {
                        String s = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                        actual[0] = s.substring(s.indexOf("\n")+1);
                    }

                }, "ошибка ввода вывода в файле"
        );
        Assertions.assertNotNull(actual[0], "не сохранен");
        Assertions.assertEquals( expected, actual[0], "не совпадают");
    }

    @Test
    public void standardActionLoad(){

        Task task = new Task("com.practikum.tracker.task 0", "test", Status.NEW);
        task.setDefaultTimeAndDuration();
        manager.createTask(task);

        Epic epic = new Epic("epic 0", "test");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("subtask 1", "test", Status.NEW, epic.getId());
        subtask1.setDefaultTimeAndDuration();
        Subtask subtask2 = new Subtask("subtask 2", "test", Status.IN_PROGRESS, epic.getId());
        subtask2.setDefaultTimeAndDuration();
        Subtask subtask3 = new Subtask("subtask 3", "test", Status.DONE, epic.getId());
        subtask3.setDefaultTimeAndDuration();

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        manager.getTask(task.getId());
        manager.getSubtask(subtask1.getId());
        manager.getEpic(epic.getId());

        final FileBackedTasksManager[] serialize = new FileBackedTasksManager[1];
        Assertions.assertDoesNotThrow(
                () -> {
                    serialize[0] = FileBackedTasksManager.loadFromFile(file);},
                "вызвано исключение"
        );

        Assertions.assertNotNull(serialize[0], "не сохранен");
        Assertions.assertArrayEquals( manager.getAllTasks().toArray(),
                serialize[0].getAllTasks().toArray(), "не совпадают задачи");
        Assertions.assertArrayEquals( manager.getAllSubtasks().toArray(),
                serialize[0].getAllSubtasks().toArray(), "не совпадают подзадачи");
        Assertions.assertArrayEquals( manager.getAllEpics().toArray(),
                serialize[0].getAllEpics().toArray(), "не совпадают эпики");
        Assertions.assertArrayEquals( manager.getHistory().toArray(),
                serialize[0].getHistory().toArray(), "не совпадает история");
    }

    @Test
    public void actionSaveEmptyTaskAndHistory(){
        Task task = new Task("com.practikum.tracker.task 0", "test", Status.NEW);
        task.setDefaultTimeAndDuration();
        manager.createTask(task);
        manager.getTask(task.getId());
        manager.removeTask(task.getId());

        String expected = "\n";

        String[] actual = new String[1];

        Assertions.assertDoesNotThrow(
                () -> {
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                    if (in.available() > 0) {
                        String s = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                        actual[0] = s.substring(s.indexOf("\n") + 1);
                    }
                }, "ошибка ввода вывода в файле"
        );
        Assertions.assertNotNull(actual[0], "не сохранен");
        Assertions.assertEquals( expected, actual[0], "не совпадают");
    }

    @Test
    public void actionLoadEmptyTaskAndHistory(){

        Task task = new Task("com.practikum.tracker.task 0", "test", Status.NEW);
        task.setDefaultTimeAndDuration();
        manager.createTask(task);
        manager.getTask(task.getId());
        manager.removeTask(task.getId());

        final FileBackedTasksManager[] serialize = new FileBackedTasksManager[1];
        Assertions.assertDoesNotThrow(
                () -> {
                    serialize[0] = FileBackedTasksManager.loadFromFile(file);},
                "вызвано исключение"
        );

        Assertions.assertNotNull(serialize[0], "не сохранен");
        Assertions.assertTrue(manager.getAllTasks().isEmpty(), "список задач не пустой");
        Assertions.assertTrue(manager.getAllSubtasks().isEmpty(), "список подзадач не пустой");
        Assertions.assertTrue(manager.getAllEpics().isEmpty(), "список эпиков не пустой");
        Assertions.assertTrue(manager.getHistory().isEmpty(), "история не пустая");
    }

    @Test
    public void actionSaveEmptyHistory(){
        StringBuilder savedString = new StringBuilder();

        Task task = new Task("com.practikum.tracker.task 0", "test", Status.NEW);
        manager.createTask(task);

        Epic epic = new Epic("epic 0", "test");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("subtask 1", "test", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("subtask 2", "test", Status.IN_PROGRESS, epic.getId());
        Subtask subtask3 = new Subtask("subtask 3", "test", Status.DONE, epic.getId());

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        // в строго таком порядке
        savedString.append(task.toString());
        savedString.append(epic.toString());
        savedString.append(subtask1.toString());
        savedString.append(subtask2.toString());
        savedString.append(subtask3.toString());
        savedString.append("\n");

        String expected = savedString.toString();

        String[] actual = new String[1];

        Assertions.assertDoesNotThrow(
                () -> {
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                    if (in.available() > 0) {
                        String s = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                        actual[0] = s.substring(s.indexOf("\n") + 1);

                    }
                }, "ошибка ввода вывода в файле"
        );
        Assertions.assertNotNull(actual[0], "не сохранен");
        Assertions.assertEquals( expected, actual[0], "не совпадают");
    }

    @Test
    public void actionSaveEpicWithEmptySubtask(){
        StringBuilder savedString = new StringBuilder();


        Epic epic = new Epic("epic 0", "test");
        manager.createEpic(epic);

        savedString.append(epic.toString());
        savedString.append("\n");
        manager.getEpic(epic.getId());
        savedString.append(epic.getId());

        String expected = savedString.toString();

        String[] actual = new String[1];

        Assertions.assertDoesNotThrow(
                () -> {
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                    if (in.available() > 0) {
                        String s = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                        actual[0] = s.substring(s.indexOf("\n")+1);
                    }

                }, "ошибка ввода вывода в файле"
        );
        Assertions.assertNotNull(actual[0], "не сохранен");
        Assertions.assertEquals( expected, actual[0], "не совпадают");
    }

    @Test
    public void actionLoadWithEmptySubtask(){

        Epic epic = new Epic("epic 0", "test");
        manager.createEpic(epic);
        manager.getEpic(epic.getId());

        final FileBackedTasksManager[] serialize = new FileBackedTasksManager[1];
        Assertions.assertDoesNotThrow(
                () -> {
                    serialize[0] = FileBackedTasksManager.loadFromFile(file);},
                "вызвано исключение"
        );

        Assertions.assertNotNull(serialize[0], "не сохранен");

        Assertions.assertArrayEquals( manager.getAllEpics().toArray(),
                serialize[0].getAllEpics().toArray(), "не совпадают эпики");

        Assertions.assertArrayEquals( manager.getAllSubtasks().toArray(),
                serialize[0].getAllSubtasks().toArray(), "не совпадают подзадачи");

        Assertions.assertArrayEquals( manager.getHistory().toArray(),
                serialize[0].getHistory().toArray(), "не совпадает история");
    }

}
