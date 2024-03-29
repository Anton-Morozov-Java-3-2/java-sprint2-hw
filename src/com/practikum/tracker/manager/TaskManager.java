package com.practikum.tracker.manager;

import com.practikum.tracker.model.Epic;
import com.practikum.tracker.model.Subtask;
import com.practikum.tracker.model.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    long getId();

    // 2.1 Получение списка всех задач

    List<Task> getAllTasks();
    List<Subtask> getAllSubtasks();
    List<Epic> getAllEpics();

    // 2.2 Удаление всех задач

    void removeAllTasks();
    void removeAllSubtasks();
    void removeAllEpics();

    // 2.3 Получение по идентификатору

    Task getTask(Long id);
    Subtask getSubtask(Long id);
    Epic getEpic(Long id);

    // 2.4 Создание

    void createTask(Task task);
    void createSubtask(Subtask subtask);
    void createEpic(Epic epic);

    // 2.5 Обновление

    void updateTask(Task task);
    void updateSubtask(Subtask subtask);
    void updateEpic(Epic epic);

    // 2.5 Удаление по идентификатору

    void removeTask(Long id);
    void removeSubtask(Long id);
    void removeEpic(Long id);

    // 3.1 Получение списка подзадач определённого Epic

    List<Subtask> getSubtasksEpic(Long id);

    // 4.2 Управление статусами эпиков
    // внутренняя реализация конкретного класса и не является методом интерфейса

    // Получить список всех задач по приоритету
    TreeSet<Task> getPrioritizedTasks();

    List<Task> getHistory();
}


