package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

public interface TaskManager {

    long getId();

    // 2.1 Получение списка всех задач

    ArrayList<Task> getAllTasks();
    ArrayList<Subtask> getAllSubtasks();
    ArrayList<Epic> getAllEpics();

    // 2.2 Удаление всех задач

    void removeAllTasks();
    void removeAllSubtasks();
    void removeAllEpics();

    // 2.3 Получение по идентификатору

    Task getTaskById(Long id);
    Subtask getSubtaskById(Long id);
    Epic getEpicById(Long id);

    // 2.4 Создание

    void createTask(Task task);
    void createSubtask(Subtask subtask);
    void createEpic(Epic epic);

    // 2.5 Обновление

    void updateTask(Task task);
    void updateSubtask(Subtask subtask);
    void updateEpic(Epic epic);

    // 2.5 Удаление по идентификатору

    void removeTaskById(Long id);
    void removeSubtaskById(Long id);
    void removeEpicById(Long id);

    // 3.1 Получение списка подзадач определённого task.Epic

    ArrayList<Subtask> getSubtasksEpicById(Long id);

    // 4.2 Управление статусами эпиков

    void defineStatusEpic(Long id);
}


