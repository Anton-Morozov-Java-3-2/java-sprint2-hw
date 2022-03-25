package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Long, Task> tasks;
    private final Map<Long, Subtask> subtasks;
    private final Map<Long, Epic> epics;
    private long countId;

    @Override
    public long getId() {
        return countId++;
    }

    public InMemoryTaskManager() {
        countId = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    // 2.1 Получение списка всех задач

    @Override
    public Collection<Task> getAllTasks(){
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Collection<Subtask> getAllSubtasks(){
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Collection<Epic> getAllEpics(){
        Collection<Epic> request = new ArrayList<>();
        for (Epic epic: epics.values()) {
            defineStatusEpic(epic.getId());
            request.add(epic);
        }
        return request;
    }

    // 2.2 Удаление всех задач
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Long id: subtasks.keySet()) {
            removeSubtask(id);
        }
    }

    @Override
    public void removeAllEpics() {
        for (Long id: epics.keySet()) {
            removeEpic(id);
        }
    }

    // 2.3 Получение по идентификатору
    @Override
    public Task getTask(Long id){
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(Long id){
        return subtasks.get(id);
    }

    @Override
    public Epic getEpic(Long id){
        defineStatusEpic(id);
        return epics.get(id);
    }

    // 2.4 Создание
    @Override
    public void createTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    @Override
    public void createEpic(Epic epic){
        if (epic != null) {
            epics.put(epic.getId(), epic);
        }
    }

    // 2.5 Обновление
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    // 2.5 Удаление по идентификатору
    @Override
    public void removeTask(Long id) {
        tasks.remove(id);
    }

    @Override
    public void removeSubtask(Long id){
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getIdEpic());
        epic.removeSubtaskById(id);
        subtasks.remove(id);
    }

    @Override
    public void removeEpic(Long id) {
        Epic epic = epics.get(id);
        for (Long i: epic.getSubtasks()) {
            subtasks.remove(i);
        }
        epic.removeAllSubtasks();
        epics.remove(id);
    }

    // 3.1 Получение списка подзадач определённого task.Epic
    @Override
    public Collection<Subtask> getSubtasksEpic(Long id) {
        Epic epic = epics.get(id);
        ArrayList<Subtask> request = new ArrayList<>();
        for (Long i: epic.getSubtasks()) {
            request.add(subtasks.get(i));
        }
        return request;
    }

    // 4.2 Управление статусами эпиков
    private void defineStatusEpic(Long id) {
        Epic epic = epics.get(id);
        Collection<Subtask> subtask = getSubtasksEpic(id);
        if (subtask.size() == 0) {
            epic.setStatus(Status.NEW);
        } else {
            int countNew = 0;
            int countDone = 0;

            for (Long i : epic.getSubtasks()) {
                Subtask s = subtasks.get(i);
                if (s.getStatus() == Status.NEW){
                    ++countNew;
                } else if (s.getStatus() == Status.DONE) {
                    ++countDone;
                }
            }
            if (countNew == subtask.size()) {
                epic.setStatus(Status.NEW);
            } else if (countDone == subtask.size()) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}