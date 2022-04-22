package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Long, Task> tasks;
    private final Map<Long, Subtask> subtasks;
    private final Map<Long, Epic> epics;
    private long countId;

    protected final HistoryManager history;

    @Override
    public long getId() {
        return countId++;
    }

    public InMemoryTaskManager() {
        countId = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        history = Managers.getHistory();
    }

    // 2.1 Получение списка всех задач

    @Override
    public List<Task> getAllTasks(){
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks(){
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics(){
        return new ArrayList<>(epics.values());
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
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            history.add(task);
            return task;
        } else {
            return null;
        }
    }

    @Override
    public Subtask getSubtask(Long id){
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            history.add(subtask);
            return subtask;
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpic(Long id){
        if (epics.containsKey(id)) {
            defineStatusEpic(id);
            Epic epic = epics.get(id);
            history.add(epic);
            return epic;
        } else {
            return null;
        }
    }

    // 2.4 Создание
    @Override
    public void createTask(Task task) {
        if (task != null) {
            if (task.isNotSetId()) {
                task.setId(getId());
            }
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            long idEpic = subtask.getIdEpic();
            if (epics.containsKey(idEpic)) {
                if (subtask.isNotSetId()) {
                    subtask.setId(getId());
                }
                long idSubtask = subtask.getId();
                epics.get(idEpic).addSubtask(idSubtask);
                subtasks.put(idSubtask, subtask);
                defineStatusEpic(idEpic);
            }
        }
    }

    @Override
    public void createEpic(Epic epic){
        if (epic != null) {
            if (epic.isNotSetId()) {
                epic.setId(getId());
            }
            long idEpic = epic.getId();
            epics.put(idEpic, epic);
            defineStatusEpic(idEpic);
        }
    }

    // 2.5 Обновление
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.replace(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.replace(subtask.getId(), subtask);
            defineStatusEpic(subtask.getIdEpic());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.replace(epic.getId(), epic);
            defineStatusEpic(epic.getId());
        }
    }

    // 2.5 Удаление по идентификатору
    @Override
    public void removeTask(Long id) {
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public void removeSubtask(Long idSubtask){
        long idEpic = subtasks.get(idSubtask).getIdEpic();
        Epic epic = epics.get(idEpic);
        epic.removeSubtaskById(idSubtask);
        subtasks.remove(idSubtask);
        history.remove(idSubtask);
        defineStatusEpic(idEpic);
    }

    @Override
    public void removeEpic(Long id) {
        Epic epic = epics.get(id);
        for (Long i: epic.getSubtasks()) {
            subtasks.remove(i);
            history.remove(i);
        }
        epics.remove(id);
        history.remove(id);
    }

    // 3.1 Получение списка подзадач определённого task.Epic
    @Override
    public List<Subtask> getSubtasksEpic(Long id) {
        Epic epic = epics.get(id);
        List<Subtask> request = new ArrayList<>();
        for (Long i: epic.getSubtasks()) {
            request.add(subtasks.get(i));
        }
        return request;
    }

    // 4.2 Управление статусами эпиков
    private void defineStatusEpic(Long id) {
        Epic epic = epics.get(id);
        List<Subtask> subtask = getSubtasksEpic(id);
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
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }

            if (countNew == subtask.size()) {
                epic.setStatus(Status.NEW);
            } else if (countDone == subtask.size()){
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}