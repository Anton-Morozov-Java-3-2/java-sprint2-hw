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
        List<Epic> request = new ArrayList<>();
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
        Task t = tasks.get(id);
        history.add(t);
        return t;
    }

    @Override
    public Subtask getSubtask(Long id){
        Subtask s = subtasks.get(id);
        history.add(s);
        return s;
    }

    @Override
    public Epic getEpic(Long id){
        defineStatusEpic(id);
        Epic e = epics.get(id);
        history.add(e);
        return e;
    }

    // 2.4 Создание
    @Override
    public void createTask(Task task) {
        createTask(task, getId());
    }

    public void createTask(Task task, Long id) {
        if (task != null) {
            task.setId(id);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        createSubtask(subtask, getId());
    }

    public void createSubtask(Subtask subtask, Long id) {
        if (subtask != null) {
            long idEpic = subtask.getIdEpic();
            if (epics.containsKey(idEpic)) {
                long idSubtask = id;
                subtask.setId(idSubtask);
                epics.get(idEpic).addSubtask(idSubtask);
                subtasks.put(idSubtask, subtask);
                defineStatusEpic(idEpic);
            }
        }
    }

    @Override
    public void createEpic(Epic epic){
        createEpic(epic, getId());
    }
    public void createEpic(Epic epic, Long id){
        if (epic != null) {
            long idEpic = id;
            epic.setId(idEpic);
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
        Epic e = epics.get(idEpic);
        e.removeSubtaskById(idSubtask);
        subtasks.remove(idSubtask);
        history.remove(idSubtask);
        defineStatusEpic(idEpic);
    }

    @Override
    public void removeEpic(Long id) {
        Epic e = epics.get(id);
        for (Long i: e.getSubtasks()) {
            subtasks.remove(i);
            history.remove(i);
        }
        e.removeAllSubtasks();
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

            for (Long i : epic.getSubtasks()) {
                Subtask s = subtasks.get(i);
                if (s.getStatus() == Status.NEW){
                    ++countNew;
                } else if (s.getStatus() == Status.DONE) {
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }

            if (countNew == subtask.size()) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.DONE);
            }
        }
    }
}