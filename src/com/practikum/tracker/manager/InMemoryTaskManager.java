package com.practikum.tracker.manager;

import com.practikum.tracker.history.HistoryManager;
import com.practikum.tracker.model.Epic;
import com.practikum.tracker.model.Status;
import com.practikum.tracker.model.Subtask;
import com.practikum.tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Long, Task> tasks;
    private final Map<Long, Subtask> subtasks;
    private final Map<Long, Epic> epics;
    private final TreeSet<Task> priority;
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
        priority =  new TreeSet<>(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                if (o1.getStartTime() != null && o2.getStartTime() != null) {

                    if (o1.getStartTime().isBefore(o2.getStartTime())) {
                        return -1;
                    } else if (o1.getStartTime().isEqual(o2.getStartTime())) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else if (o1.getStartTime() != null && o2.getStartTime() == null){
                    return -1;
                } else if (o1.getStartTime() == null && o2.getStartTime() != null) {
                    return 1;
                } else {
                    return (int) (o1.getId() - o2.getId());
                }
            }
        });
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
        List<Long> removedIdSubtasks = new ArrayList<>(subtasks.keySet());
        for (Long id: removedIdSubtasks) {
            removeSubtask(id);
        }
    }

    @Override
    public void removeAllEpics() {
        List<Long> removedIdEpics = new ArrayList<Long>(epics.keySet());
        for (long id: removedIdEpics) {
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
            defineStateEpic(id);
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

            try {
                validateStartTime(task);
            } catch (ManagerValidateException e) {
                task.setStartTime(getFirstFreeTime(task));
            }

            if (task.isNotSetId()) {
                task.setId(getId());
            }

            tasks.put(task.getId(), task);
            priority.add(task);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            try {
                validateStartTime(subtask);
            } catch (ManagerValidateException e) {
                subtask.setStartTime(getFirstFreeTime(subtask));
            }

            long idEpic = subtask.getIdEpic();
            if (epics.containsKey(idEpic)) {
                if (subtask.isNotSetId()) {
                    subtask.setId(getId());
                }
                long idSubtask = subtask.getId();
                epics.get(idEpic).addSubtask(idSubtask);
                subtasks.put(idSubtask, subtask);
                defineStateEpic(idEpic);
                priority.add(subtask);
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
            epic.clearSubtasks();
            epics.put(idEpic, epic);
            defineStateEpic(idEpic);
        }
    }

    // 2.5 Обновление
    @Override
    public void updateTask(Task newTask) {
        if (newTask != null) {
            if (tasks.containsKey(newTask.getId())) {
                // удаляем старый
                Task old = tasks.get(newTask.getId());
                priority.remove(old);

                try {
                    validateStartTime(newTask);
                } catch (ManagerValidateException e) {
                    newTask.setStartTime(getFirstFreeTime(newTask));
                }

                priority.add(newTask); // добавляем новый
                tasks.replace(newTask.getId(), newTask);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        if (newSubtask != null) {
            if (subtasks.containsKey(newSubtask.getId()) && epics.containsKey(newSubtask.getIdEpic())) {

                // удаляем старый
                Subtask old = subtasks.get(newSubtask.getId());
                priority.remove(old);

                try {
                    validateStartTime(newSubtask);
                }
                catch (ManagerValidateException e) {
                    newSubtask.setStartTime(getFirstFreeTime(newSubtask));
                }

                priority.add(newSubtask); // добавляем новый
                subtasks.replace(newSubtask.getId(), newSubtask);
                defineStateEpic(newSubtask.getIdEpic());
            }
        }
    }

    @Override
    public void updateEpic(Epic newEpic) {
        if (newEpic != null) {
            if (epics.containsKey(newEpic.getId())) {
                List<Long> subtask = epics.get(newEpic.getId()).getSubtasks();
                for (Long id : subtask) {
                    newEpic.addSubtask(id);
                }
                epics.replace(newEpic.getId(), newEpic);
                defineStateEpic(newEpic.getId());
            }
        }
    }

    // 2.5 Удаление по идентификатору
    @Override
    public void removeTask(Long id) {
        if (id != null) {
            priority.remove(tasks.get(id));
            tasks.remove(id);
            history.remove(id);
        }
    }

    @Override
    public void removeSubtask(Long idSubtask){
        if (idSubtask != null) {
            if (subtasks.containsKey(idSubtask)) {
                long idEpic = subtasks.get(idSubtask).getIdEpic();
                Epic epic = epics.get(idEpic);
                epic.removeSubtaskById(idSubtask);
                priority.remove(subtasks.get(idSubtask));
                subtasks.remove(idSubtask);
                history.remove(idSubtask);
                defineStateEpic(idEpic);
            }
        }
    }

    @Override
    public void removeEpic(Long id) {
        if (id != null) {
            if (epics.containsKey(id)) {
                Epic epic = epics.get(id);
                for (Long i: epic.getSubtasks()) {
                    priority.remove(subtasks.get(i));
                    subtasks.remove(i);
                    history.remove(i);
                }
                epics.remove(id);
                history.remove(id);
            }
        }
    }

    // 3.1 Получение списка подзадач определённого com.practikum.tracker.task.Epic
    @Override
    public List<Subtask> getSubtasksEpic(Long id) {
        List<Subtask> request = new ArrayList<>();
        if (id != null && epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Long i: epic.getSubtasks()) {
                request.add(subtasks.get(i));
            }
        }
        return request;
    }

    // 4.2 Управление статусами эпиков
    private void defineStateEpic(Long id) {
        if (epics.containsKey(id) && id != null) {
            Epic epic = epics.get(id);
            // определяем статус эпика
            defineStatusEpic(epic);
            // определяем время выполнения эпика
            defineTimeEpic(epic);
        }
    }

    private void defineStatusEpic(Epic epic) {
        List<Subtask> subtask = getSubtasksEpic(epic.getId());
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
    
    // расчёт времени завершения Epic
    private void defineTimeEpic(Epic epic) {
        final List<Subtask> subtasksEpic = getSubtasksEpic(epic.getId());

        // если есть хотя бы одна subtask без startTime или duration у эпика не определяется endTime
        subtasksEpic.removeIf(subtask -> {
            if (subtask.getStartTime() == null && subtask.getDuration() == null) {
                return true;
            }
            return false;
        });


        if (subtasksEpic.size() == epic.getSubtasks().size() && !subtasksEpic.isEmpty()) {
            subtasksEpic.sort((o1, o2) -> {
                LocalDateTime time1 = o1.getStartTime();
                LocalDateTime time2 = o2.getStartTime();
                if (time1.isEqual(time2)) return 0;
                return time1.isBefore(time2) ? 1 : -1;
            });

            LocalDateTime startTime = subtasksEpic.get(0).getStartTime();
            LocalDateTime endTime = subtasksEpic.get(subtasksEpic.size() - 1).getEndTime();
            Duration duration = Duration.between(startTime, endTime);
            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            epic.setDuration(duration);
        }
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return priority;
    }

    private void validateStartTime(Task task) {
        if (task == null) throw new ManagerValidateException("Task == null");

        if (task.getStartTime() != null && task.getDuration() != null) {
            LocalDateTime startTask = task.getStartTime();
            LocalDateTime endTask = task.getEndTime();

            for (Task t: priority) {
                LocalDateTime start = t.getStartTime();
                LocalDateTime end = t.getEndTime();
                if (start == null) continue;
                String massage = startTask.toString() + " intersect " + task.toString();

                if (startTask.isEqual(start) || endTask.isEqual(end))
                    throw new ManagerValidateException(massage);

                if (startTask.isBefore(start) && endTask.isAfter(end))
                    throw new ManagerValidateException(massage);

                if (startTask.isAfter(start) && startTask.isBefore(end))
                    throw new ManagerValidateException(massage);

                if (endTask.isAfter(start) && endTask.isBefore(end))
                    throw new ManagerValidateException(massage);
            }
        }
    }
    public LocalDateTime getFirstFreeTime(Task task) {
        if (task == null) return null;

        LocalDateTime startPriority = task.getStartTime();
        LocalDateTime endPriority = task.getEndTime();
        Duration duration = task.getDuration();

        if (priority.isEmpty()) return startPriority;

        if (
                priority.first().getStartTime().isAfter(endPriority) ||
                priority.last().getEndTime().isBefore(startPriority)) {

            return startPriority;
        }

        for (Task t: priority) {
            LocalDateTime end = t.getEndTime();
            Task next = priority.higher(t);
            if (next != null) {
                LocalDateTime startSecond = next.getStartTime();
                if (end.plus(duration).isBefore(startSecond)) {
                    return t.getEndTime();
                }
            } else {
                return t.getEndTime();
            }
        }
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }
}