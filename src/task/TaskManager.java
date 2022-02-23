package task;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;
    private int countId;

    public int getId() {
        return countId++;
    }

    public TaskManager() {
        countId = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    // 2.1 Получение списка всех задач

    public ArrayList<Task> getAllTasks(){
        ArrayList<Task> request = new ArrayList<>();
        request.addAll(tasks.values());
        return request;
    }

    public ArrayList<Subtask> getAllSubtasks(){
        ArrayList<Subtask> request = new ArrayList<>();
        request.addAll(subtasks.values());
        return request;
    }

    public ArrayList<Epic> getAllEpics(){
        ArrayList<Epic> request = new ArrayList<>();
        for (Epic epic: epics.values()) {
            defineStatusEpic(epic.getId());
            request.add(epic);
        }
        return request;
    }

    // 2.2 Удаление всех задач

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        for (Integer id: subtasks.keySet()) {
            removeSubtaskById(id);
        }
    }

    public void removeAllEpics() {
        for (Integer id: epics.keySet()) {
            removeEpicById(id);
        }
    }

    // 2.3 Получение по идентификатору

    public Task getTaskById(Integer id){
        return tasks.get(id);
    }

    public Subtask getSubtaskById(Integer id){
        return subtasks.get(id);
    }

    public Epic getEpicById(Integer id){
        defineStatusEpic(id);
        return epics.get(id);
    }

    // 2.4 Создание

    public void createTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        }
    }

    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    public void createEpic(Epic epic){
        if (epic != null) {
            epics.put(epic.getId(), epic);
        }
    }

    // 2.5 Обновление

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    // 2.5 Удаление по идентификатору

    public void removeTaskById(Integer id) {
        tasks.remove(id);
    }

    public void removeSubtaskById(Integer id){
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getIdEpic());
        epic.removeSubtaskById(id);
        subtasks.remove(id);
    }

    public void removeEpicById(Integer id) {
        Epic epic = epics.get(id);
        for (Integer i: epic.subtasks) {
            subtasks.remove(i);
        }
        epic.subtasks.clear();
        epics.remove(id);
    }

    // 3.1 Получение списка подзадач определённого task.Epic

    public ArrayList<Subtask> getSubtasksEpicById(Integer id) {
        Epic epic = epics.get(id);
        ArrayList<Subtask> request = new ArrayList<>();
        for (Integer i: epic.subtasks) {
            request.add(subtasks.get(i));
        }
        return request;
    }

    // 4.2 Управление статусами эпиков

    private void defineStatusEpic(Integer id) {
        Epic epic = epics.get(id);
        ArrayList<Subtask> subtask = getSubtasksEpicById(id);
        if (subtask.size() == 0) {
            epic.setStatus(Task.NEW);
        } else {
            int countNew = 0;
            int countDone = 0;

            for (Integer i : epic.getSubtasks()) {
                Subtask s = subtasks.get(i);
                if (s.getStatus().equals(Task.NEW)){
                    ++countNew;
                } else if (s.getStatus().equals(Task.DONE)) {
                    ++countDone;
                }
            }
            if (countNew == subtask.size()) {
                epic.setStatus(Task.NEW);
            } else if (countDone == subtask.size()) {
                epic.setStatus(Task.DONE);
            } else {
                epic.setStatus(Task.IN_PROGRESS);
            }
        }
    }
}


