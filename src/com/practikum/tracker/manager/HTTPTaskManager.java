package com.practikum.tracker.manager;

import com.practikum.tracker.model.Epic;
import com.practikum.tracker.model.Subtask;
import com.practikum.tracker.model.Task;
import com.practikum.tracker.server.KVTaskClient;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HTTPTaskManager extends FileBackedTasksManager{
    KVTaskClient client;

    public HTTPTaskManager(String url) {
        super(url);
        try {
            client = new KVTaskClient(url);
            loadFromServer(client);
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
    }

    public String getAPI_TOKEN(){
        return client.getAPI_TOKEN();
    }

    public HTTPTaskManager(String url, String API_TOKEN) {
        super(url);
        try {
            client = new KVTaskClient(url, API_TOKEN);
            loadFromServer(client);
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
    }

    @Override
    void save() {
        String jsonTasks = "[" + getAllTasks().stream().map(Task::toJSON).
                collect(Collectors.joining(",")) + "]";
        String jsonSubtasks = "[" + getAllSubtasks().stream().map(Task::toJSON).
                collect(Collectors.joining(",")) + "]";
        String jsonEpics = "[" + getAllEpics().stream().map(Task::toJSON).
                collect(Collectors.joining(",")) + "]";
        String jsonHistory = "[" + getHistory().stream().map(Task::toJSON).
                collect(Collectors.joining(",")) + "]";
        try {
            client.put("tasks", jsonTasks);
            client.put("subtasks", jsonSubtasks);
            client.put("epics", jsonEpics);
            client.put("history", jsonHistory);
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
    }

    public void loadFromServer(KVTaskClient client) {
        String jsonTasks = "";
        String jsonSubtasks = "";
        String jsonEpics = "";
        String jsonHistory = "";
        try {
            jsonTasks = client.load("tasks");
            jsonSubtasks = client.load("subtasks");
            jsonEpics = client.load("epics");
            jsonHistory = client.load("history");
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
        List<Task> tasks = Task.tasksFromJSON(jsonTasks);
        List<Task> subtasks = Subtask.tasksFromJSON(jsonSubtasks);
        List<Task> epics = Epic.tasksFromJSON(jsonEpics);
        List<Task> history = Task.tasksFromJSON(jsonHistory);

        for (Task t : tasks) {
            createTask(t);
        }
        for (Task e : epics) {
            createEpic((Epic) e);
        }
        for (Task s : subtasks) {
            createSubtask((Subtask) s);
        }
        for (Task t: history) {
            getSubtask(t.getId());
            getEpic(t.getId());
            getTask(t.getId());
        }
    }
}
