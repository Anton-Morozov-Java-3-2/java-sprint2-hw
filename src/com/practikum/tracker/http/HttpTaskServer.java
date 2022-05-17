package com.practikum.tracker.http;

import com.practikum.tracker.manager.Managers;
import com.practikum.tracker.manager.TaskManager;
import com.practikum.tracker.model.Epic;
import com.practikum.tracker.model.Subtask;
import com.practikum.tracker.model.Task;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskServer {

    public static final int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
            manager = Managers.getDefault();
            server = HttpServer.create();
            server.bind(new InetSocketAddress(PORT), 0);
            /* создаём ручки */
            server.createContext("/tasks/task", new TaskHandler());
            server.createContext("/tasks/subtask", new SubtaskHandler());
            server.createContext("/tasks/epic", new EpicHandler());
            server.createContext("/tasks", new AllTasksHandler());
            server.createContext("/tasks/history", new HistoryHandler());
            //    /tasks/history
            server.start();
    }

    public void stop(int delay) {
        server.stop(delay);
    }
    class HistoryHandler extends TaskHandler {

        public Pair handlingGET(String query) {
            int rCode = DEFAULT_CODE_SUCCESS;
            final String response;
            if (query==null) {
                response = "[" +
                        manager.getHistory().stream().map(Task::toJSON).collect(Collectors.joining(",")) +
                        "]";
            } else {
                rCode = DEFAULT_CODE_ERROR;
                response = DEFAULT_RESPONSE;
            }
            return new Pair(rCode, response);
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            URI requestURI = httpExchange.getRequestURI();

            Headers requestHeader =  httpExchange.getRequestHeaders();
            Headers responseHeader = httpExchange.getResponseHeaders();

            String request = getRequest(httpExchange);
            String query = requestURI.getQuery();

            final Pair response;

            if ("GET".equals(method)) {
                responseHeader.set("Content-type", "application/json");
                response = handlingGET(query);
            } else {
                response = new Pair(DEFAULT_CODE_ERROR, DEFAULT_RESPONSE);
            }
            httpExchange.sendResponseHeaders(response.getCode(), 0);
            try (OutputStream out = httpExchange.getResponseBody()) {
                out.write(response.getResponse().getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    class AllTasksHandler extends TaskHandler {

        public Pair handlingGET(String query) {
            int rCode = DEFAULT_CODE_SUCCESS;
            final String response;
            if (query==null) {
                List<Task> tasks = manager.getAllTasks();
                tasks.addAll(manager.getAllSubtasks());
                tasks.addAll(manager.getAllEpics());
                response = "[" +
                        tasks.stream().map(Task::toJSON).collect(Collectors.joining(",")) +
                        "]";
            } else {
                    rCode = DEFAULT_CODE_ERROR;
                    response = DEFAULT_RESPONSE;
            }
            return new Pair(rCode, response);
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            URI requestURI = httpExchange.getRequestURI();

            Headers requestHeader =  httpExchange.getRequestHeaders();
            Headers responseHeader = httpExchange.getResponseHeaders();

            String request = getRequest(httpExchange);
            String query = requestURI.getQuery();

            final Pair response;

            if ("GET".equals(method)) {
                responseHeader.set("Content-type", "application/json");
                response = handlingGET(query);
            } else {
                response = new Pair(DEFAULT_CODE_ERROR, DEFAULT_RESPONSE);
            }
            httpExchange.sendResponseHeaders(response.getCode(), 0);
            try (OutputStream out = httpExchange.getResponseBody()) {
                out.write(response.getResponse().getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    class EpicHandler extends TaskHandler {
        @Override
        public Pair handlingGET(Long id) {
            int rCode = DEFAULT_CODE_SUCCESS;
            final String response;
            if (id == null) {
                response = "[" +
                        manager.getAllEpics().stream().map(Task::toJSON).collect(Collectors.joining(",")) +
                        "]";
            } else {
                Epic epic = manager.getEpic(id);
                if (epic == null) {
                    rCode = DEFAULT_CODE_ERROR;
                    response = DEFAULT_RESPONSE;
                } else {
                    response = epic.toJSON();
                }
            }
            return new Pair(rCode, response);
        }

        @Override
        public Pair handlingPOST(Long id, String request) {
            int rCode = DEFAULT_CODE_SUCCESS;
            // создаём
            if (id == null) {
                Epic epic = (Epic) Epic.fromJSON(request);
                if (epic == null) {
                    rCode = DEFAULT_CODE_ERROR;
                } else {
                    manager.createEpic(epic);
                }
            } else { // обновляем
                manager.updateEpic((Epic) Epic.fromJSON(request));
            }
            return new Pair(rCode, DEFAULT_RESPONSE);
        }

        @Override
        public Pair handlingDELETE(Long id) {
            int rCode = DEFAULT_CODE_SUCCESS;
            if (id == null) {
                manager.removeAllEpics();;
            } else {
                manager.removeEpic(id);
            }
            return new Pair(rCode, DEFAULT_RESPONSE);
        }
    }

    class SubtaskHandler extends TaskHandler {
        @Override
        public Pair handlingGET(Long id) {
            int rCode = DEFAULT_CODE_SUCCESS;
            final String response;
            if (id == null) {
                response = "[" +
                        manager.getAllSubtasks().stream().map(Task::toJSON).collect(Collectors.joining(",")) +
                        "]";
            } else {
                Subtask subtask = manager.getSubtask(id);
                if (subtask == null) {
                    rCode = DEFAULT_CODE_ERROR;
                    response = DEFAULT_RESPONSE;
                } else {
                    response = subtask.toJSON();
                }
            }
            return new Pair(rCode, response);
        }

        @Override
        public Pair handlingPOST(Long id, String request) {
            int rCode = DEFAULT_CODE_SUCCESS;
            // создаём
            if (id == null) {
                Subtask subtask = (Subtask) Subtask.fromJSON(request);
                if (subtask == null) {
                    rCode = DEFAULT_CODE_ERROR;
                } else {
                    manager.createSubtask(subtask);
                }
            } else { // обновляем
                manager.updateSubtask((Subtask) Subtask.fromJSON(request));
            }
            return new Pair(rCode, DEFAULT_RESPONSE);
        }

        @Override
        public Pair handlingDELETE(Long id) {
            int rCode = DEFAULT_CODE_SUCCESS;
            if (id == null) {
                manager.removeAllSubtasks();;
            } else {
                if (manager.getSubtask(id) == null) {
                    rCode = DEFAULT_CODE_ERROR;
                } else {
                    manager.removeSubtask(id);
                }
            }
            return new Pair(rCode, DEFAULT_RESPONSE);
        }
    }

    public class TaskHandler implements HttpHandler {

        public static final String DEFAULT_RESPONSE = "";
        public static final int DEFAULT_CODE_ERROR = 400;
        public static final int DEFAULT_CODE_SUCCESS = 200;

        public class Pair {

            private final int code;
            private final String response;

            public int getCode() {
                return code;
            }

            public String getResponse() {
                return response;
            }

            public Pair(int code, String response) {
                this.code = code;
                this.response = response;
            }
        }

        public String getRequest(HttpExchange httpExchange) throws IOException {
            String request;
            try (InputStream in = httpExchange.getRequestBody()) {
                request = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
            return request;
        }

        public Long getIdRequest(URI requestURI) {
            Long id = null;
            String query = requestURI.getQuery();
            if (query != null) {
                id = Long.parseLong(query.split("=")[1]);
            }
            return id;
        }

        public Pair handlingGET(Long id) {
            int rCode = DEFAULT_CODE_SUCCESS;
            final String response;
            if (id == null) {
                response = "[" +
                        manager.getAllTasks().stream().map(Task::toJSON).collect(Collectors.joining(",")) +
                        "]";
            } else {
                Task task = manager.getTask(id);
                if (task == null) {
                    rCode = DEFAULT_CODE_ERROR;
                    response = DEFAULT_RESPONSE;
                } else {
                    response = task.toJSON();
                }
            }
            return new Pair(rCode, response);
        }

        public Pair handlingPOST(Long id, String request) {
            int rCode = DEFAULT_CODE_SUCCESS;
            // создаём
            if (id == null) {
                Task task = Task.fromJSON(request);
                if (task == null) {
                    rCode = DEFAULT_CODE_ERROR;
                } else {
                    manager.createTask(task);
                }
            } else { // обновляем
                manager.updateTask(Task.fromJSON(request));
            }
            return new Pair(rCode, DEFAULT_RESPONSE);
        }

        public Pair handlingDELETE(Long id) {
            int rCode = DEFAULT_CODE_SUCCESS;
            if (id == null) {
                manager.removeAllTasks();;
            } else {
                manager.removeTask(id);
            }
            return new Pair(rCode, DEFAULT_RESPONSE);
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String method = httpExchange.getRequestMethod();
            URI requestURI = httpExchange.getRequestURI();

            Headers requestHeader =  httpExchange.getRequestHeaders();
            Headers responseHeader = httpExchange.getResponseHeaders();

            String request = getRequest(httpExchange);
            Long id = getIdRequest(requestURI);

            final Pair response;

            switch (method) {
                case "GET":
                    responseHeader.set("Content-type", "application/json");
                    response = handlingGET(id);
                    break;
                case "POST":
                    response = handlingPOST(id, request);
                    break;
                case "DELETE":
                    response = handlingDELETE(id);
                    break;
                default:
                    response = new Pair(DEFAULT_CODE_ERROR, DEFAULT_RESPONSE);
                    break;
            }
            httpExchange.sendResponseHeaders(response.getCode(), 0);
            try (OutputStream out = httpExchange.getResponseBody()) {
                out.write(response.getResponse().getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}


