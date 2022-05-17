package com.practikum.tracker.manager;
import com.practikum.tracker.history.InMemoryHistoryManager;
import com.practikum.tracker.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class FileBackedTasksManager extends InMemoryTaskManager{

    private static final String header = "id,type,name,status,description,duration, start time,epic\n";
    private final File file;

    public FileBackedTasksManager(String file) {
        super();
        this.file = new File(file);
    }

    public static FileBackedTasksManager loadFromFile(String fileName) {
        File file = new File(fileName);

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            FileBackedTasksManager manager = new FileBackedTasksManager(fileName);

            if (in.available() > 0) {
                String s = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                String[] data = s.split("\n");
                if (data.length > 1) {
                    manager.initializationTaskManager(data);
                    manager.initializationHistoryManager(data);
                }
            }
            return manager;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения фала");
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    void save()  {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            out.write(header.getBytes(StandardCharsets.UTF_8));
            for (String s: getTaskInString()) {
                out.write(s.getBytes(StandardCharsets.UTF_8));
            }
            out.write("\n".getBytes(StandardCharsets.UTF_8));
            out.write(InMemoryHistoryManager.toString(history).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private void initializationIdCounter(Long max_id) {
        if (max_id > 0) {
            while (getId() != max_id) ;
        }
    }

    private void initializationTaskManager(final String[] data) {
        long max_id = 0L;
        for (int i = 1; !data[i].isBlank(); i++) {

            long id = 0L;
            if (data[i].indexOf(Type.EPIC.toString()) > 0) {
                Epic epic = Epic.fromString(data[i]);
                super.createEpic(epic);
                id = epic.getId();
            } else if (data[i].indexOf(Type.SUBTASK.toString()) > 0) {
                Subtask subtask = Subtask.fromString(data[i]);
                super.createSubtask(subtask);
                id = subtask.getId();
            } else {
                Task task = Task.fromString(data[i]);
                super.createTask(task);
                id = task.getId();
            }

            if (max_id < id) {
                max_id = id;
            }
        }

        initializationIdCounter(max_id);
        initializationEpics();
    }

    private void initializationHistoryManager(final String[] data) {
        if (data != null) {
            String s = data[data.length - 1];
            if (!s.isEmpty() && !s.isBlank()) {
                List<Long> idHistory = InMemoryHistoryManager.fromString(s);
                for (Long id : idHistory) {
                    super.getTask(id);
                    super.getSubtask(id);
                    super.getEpic(id);
                }
            }
        }
    }

    private void initializationEpics() {
        for (Epic e : getAllEpics()) {
            for (Subtask s : getAllSubtasks()) {
                if (s.getIdEpic() == e.getId()) {
                    e.addSubtask(s.getId());
                }
            }
        }
    }

    public List<String> getTaskInString(){
        List<String> str = new ArrayList<>();
        for (Task t : getAllTasks()) {
            str.add(t.toString());
        }
        for (Subtask s : getAllSubtasks()) {
            str.add(s.toString());
        }
        for (Epic e : getAllEpics()) {
            str.add(e.toString());
        }
        sortTaskStringById(str);
        return str;
    }

    private void sortTaskStringById(List<String> strings) {
        Comparator<String> comp = new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return (int) (Long.parseLong(lhs.split(",")[0]) - Long.parseLong(rhs.split(",")[0]));
            }
        };
        if (strings.size() > 1) strings.sort(comp);
    }

    @Override
    public Task getTask(Long id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(Long id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpic(Long id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic){
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTask(Long id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubtask(Long id){
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeEpic(Long id) {
        super.removeEpic(id);
        save();
    }

    public static void main(String[] args) {

        String file = "file.csv";

        FileBackedTasksManager old = new FileBackedTasksManager(file);

        Task task1 = new Task("Задача 1", "Тест", Status.NEW);
        Task task2 = new Task("Задача 2", "Тест", Status.NEW);

        old.createTask(task1);
        old.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Тест");
        old.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Тест", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Тест", Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Тест", Status.NEW, epic1.getId());

        old.createSubtask(subtask1);
        old.createSubtask(subtask2);
        old.createSubtask(subtask3);

        Epic epic2 = new Epic("Эпик 2", "Тест");
        old.createEpic(epic2);

        // смотрим задачи 0, 1
        old.getTask(task1.getId());
        old.getTask(task2.getId());

        // смотрим подзадачу 3
        old.getSubtask(subtask1.getId());

        // Смотрим эпики 2, 6
        old.getEpic(epic1.getId());
        old.getEpic(epic2.getId());


        subtask1.setStatus(Status.IN_PROGRESS);
        old.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        old.updateSubtask(subtask2);
        subtask3.setStatus(Status.DONE);
        old.updateSubtask(subtask3);

        // удаляем задачу 0
        old.removeTask(task1.getId());

        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(file);

        String old_data = String.join(",",old.getTaskInString());
        String new_data = String.join(",", newManager.getTaskInString());

        if (old_data.equals(new_data)) {
            System.out.println("Тест 1 пройден успешно");
        } else {
            System.out.println("Тест 1 провален!");
        }

        String old_history_data = InMemoryHistoryManager.toString(old.history);;
        String new_history_data = InMemoryHistoryManager.toString(newManager.history);;

        if (old_history_data.equals(new_history_data)) {
            System.out.println("Тест 2 пройден успешно");
        } else {
            System.out.println("Тест 2 провален!");
            System.out.println(old_history_data);
            System.out.println(new_history_data);
        }
    }
}
