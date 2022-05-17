package com.practikum.tracker.test;

import com.practikum.tracker.http.HttpTaskServer;
import com.practikum.tracker.model.Epic;
import com.practikum.tracker.model.Subtask;
import com.practikum.tracker.server.KVServer;
import org.junit.jupiter.api.*;

import com.practikum.tracker.model.Status;
import com.practikum.tracker.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public  class HttpTaskServerTest {
    private HttpTaskServer server;
    private static KVServer kvServer;
    public static final int CODE_SUCCESS = HttpTaskServer.TaskHandler.DEFAULT_CODE_SUCCESS;
    public static final int CODE_ERROR = HttpTaskServer.TaskHandler.DEFAULT_CODE_ERROR;
    public static final String DEFAULT_RESPONSE = HttpTaskServer.TaskHandler.DEFAULT_RESPONSE;


    @BeforeEach
    void runServer(){
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            server = new HttpTaskServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
        kvServer.stop();
    }

    public HttpRequest createPOSTRequest(URI url, Task task, boolean isJSONContent) {
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(task.toJSON());
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(url).POST(body);

        if (isJSONContent) builder.header("Content-Type", "application/json");
        return builder.build();
    }

    public HttpRequest createGETRequest(URI url, boolean isJSONAccept) {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(url).GET();
        if (isJSONAccept) builder.header("Accept", "application/json");
        return builder.build();
    }

    public HttpRequest createDELETERequest(URI url) {
        return HttpRequest.newBuilder().uri(url).DELETE().build();
    }

    public HttpResponse<String> sendPOST(URI url, Task task, HttpClient client) {
        HttpRequest request = createPOSTRequest(url, task, true);
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e){
            Assertions.assertDoesNotThrow(e::fillInStackTrace, "выброшено исключение при " +
                    "обработке запроса POST");
        }
        return response;
    }

    public HttpResponse<String> sendGET(URI url, HttpClient client) {
        HttpRequest request = createGETRequest(url, true);

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e){
            Assertions.assertDoesNotThrow(e::fillInStackTrace, "вызвано исключение при обработке запроса GET");
        }
        return response;
    }

    public HttpResponse<String> sendDelete(URI url, HttpClient client) {
        HttpRequest request = createDELETERequest(url);

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e){
            Assertions.assertDoesNotThrow(e::fillInStackTrace, "вызвано исключение при обработке запроса GET");
        }
        return response;
    }

    // Task
    @Test
    public void standardActionCreateAndGetTask() {

        HttpClient client = HttpClient.newHttpClient();

        //POST
        URI urlPOST = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task/");

        Task task = new Task("task", "test", Status.NEW);
        task.setDefaultTimeAndDuration();

        final HttpResponse<String> responsePOST = sendPOST(urlPOST, task, client);
        Assertions.assertNotNull(responsePOST, "не получен ответ от сервера при обработке запроса POST");
        Assertions.assertEquals(CODE_SUCCESS, responsePOST.statusCode(), "статус код неверный");

        //GET
        URI urlGET = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task?id=0");
        final HttpResponse<String> responseGET = sendGET(urlGET, client);
        Assertions.assertEquals(CODE_SUCCESS, responseGET.statusCode(), "статус код неверный");
        Assertions.assertNotNull(responseGET, "не получен ответ при обработке запроса GET");

        Task taskGET = Task.fromJSON(responseGET.body());
        task.setId(0L);

        Assertions.assertEquals(task, taskGET, "запрос GET вернул неверный объект");
    }

    @Test
    public void actionGetWithErrorIdTask() {

        HttpClient client = HttpClient.newHttpClient();

        URI urlGET = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task?id=0");

        HttpResponse<String> responseGET = sendGET(urlGET, client);

        Assertions.assertNotNull(responseGET, "нет ответа на сервере");
        Assertions.assertEquals(DEFAULT_RESPONSE, responseGET.body(), "ответ неверный");
        Assertions.assertEquals(CODE_ERROR, responseGET.statusCode(), "код ответа неверный");
    }

    @Test
    public void standardActionUpdateAndGetTask() {

        HttpClient client = HttpClient.newHttpClient();

        // url
        URI urlCreate = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task/");
        URI urlUpdate = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task?id=0");
        URI urlGet = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task?id=0");


        // создание
        Task task = new Task("task", "test", Status.NEW);
        task.setDefaultTimeAndDuration();

        HttpResponse<String> responseCreate = sendPOST(urlCreate, task, client);

        String header = "при отправке запроса на создание Task ";
        Assertions.assertNotNull(responseCreate,   header + "не получен ответ");
        Assertions.assertEquals(CODE_SUCCESS, responseCreate.statusCode(), header + "статус код неверный");

        // обновление
        Task newTask = new Task("task update", "test", Status.DONE);
        newTask.setDefaultTimeAndDuration();
        newTask.setId(0);

        HttpResponse<String> responseUpdate = sendPOST(urlUpdate, newTask, client);
        Assertions.assertEquals(CODE_SUCCESS, responseUpdate.statusCode(), "статус код неверный");

        header = "при отправке запроса на обновление Task ";
        Assertions.assertNotNull(responseUpdate,   header + "не получен ответ");
        Assertions.assertEquals(CODE_SUCCESS, responseCreate.statusCode(), "статус код неверный");


        // GET
        HttpResponse<String> responseGET = sendGET(urlGet, client);
        Assertions.assertNotNull(responseGET, "не получен ответ от сервера при обработке запроса GET");

        Task taskGET = Task.fromJSON(responseGET.body());
        task.setId(0L);
        Assertions.assertEquals(newTask, taskGET, "запрос GET вернул неверный объект");
    }

    @Test
    public void standardActionCreateAndDeleteTask() {

        HttpClient client = HttpClient.newHttpClient();

        // url
        URI urlCreate = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task/");
        URI urlDelete = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task?id=0");
        URI urlGet = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task?id=0");

        // создание
        Task task = new Task("task", "test", Status.NEW);
        task.setDefaultTimeAndDuration();

        HttpResponse<String> responseCreate = sendPOST(urlCreate, task, client);

        String header = "при отправке запроса на создание Task ";
        Assertions.assertNotNull(responseCreate,   header + "не получен ответ");
        Assertions.assertEquals(CODE_SUCCESS, responseCreate.statusCode(), header + "статус код не 200");

        // удаление
        header = "при отправке запроса на удаление Task ";
        HttpResponse<String> responseDelete = sendDelete(urlDelete, client);
        Assertions.assertNotNull(responseDelete,   header + "не получен ответ");
        Assertions.assertEquals(CODE_SUCCESS, responseCreate.statusCode(), header + "статус код не 200");

        // GET
        HttpResponse<String> responseGET = sendGET(urlGet, client);
        Assertions.assertNotNull(responseGET, "не получен ответ от сервера при обработке запроса GET");
        Assertions.assertEquals(CODE_ERROR, responseGET.statusCode());
    }

    @Test
    public void standardActionCreateAndGetAllTask() {

        HttpClient client = HttpClient.newHttpClient();

        // POST
        URI urlPOST = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task/");

        Task task0 = new Task("task 0", "test", Status.NEW);
        task0.setDefaultTimeAndDuration();
        Task task1 = new Task("task 1", "test", Status.DONE);

        sendPOST(urlPOST, task0, client);
        sendPOST(urlPOST, task1, client);

        // GET
        URI urlGET = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task");

        HttpResponse<String> responseGET = sendGET(urlGET, client);
        Assertions.assertNotNull(responseGET, "не получен ответ от сервера при обработке запроса GET");
        List<Task> tasksGET = Task.tasksFromJSON(responseGET.body());

        task0.setId(0);
        task1.setId(1);
        List<Task> expected = List.of(task0, task1);
        Assertions.assertEquals(2, tasksGET.size());
        Assertions.assertEquals(expected, tasksGET, "запрос GET вернул неверный объект");
    }

    @Test
    public void standardActionCreateAndDeleteAllTask() {

        HttpClient client = HttpClient.newHttpClient();

        // POST
        URI urlPOST = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task/");

        Task task0 = new Task("task 0", "test", Status.NEW);
        task0.setDefaultTimeAndDuration();
        Task task1 = new Task("task 1", "test", Status.DONE);

        sendPOST(urlPOST, task0, client);
        sendPOST(urlPOST, task1, client);

        // GET
        URI urlGET = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task");

        HttpResponse<String> responseGET = sendDelete(urlGET, client);
        Assertions.assertNotNull(responseGET, "не получен ответ от сервера при обработке запроса GET");
        List<Task> tasksGET = Task.tasksFromJSON(responseGET.body());
        Assertions.assertEquals(0, tasksGET.size());
    }
    // Subtask

    public Epic initEpic(HttpClient client) {
        URI urlPOST = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/");
        Epic epic = new Epic("epic 0", "test");
        sendPOST(urlPOST, epic, client);
        return epic;
    }


    @Test
    public void standardActionCreateAndGetSubtask() {

        HttpClient client = HttpClient.newHttpClient();
        initEpic(client);

        //POST
        URI urlPOST = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/");

        Subtask subtask = new Subtask("task", "test", Status.NEW, 0L);
        subtask.setDefaultTimeAndDuration();

        final HttpResponse<String> responsePOST = sendPOST(urlPOST, subtask, client);
        Assertions.assertNotNull(responsePOST, "не получен ответ от сервера при обработке запроса POST");
        Assertions.assertEquals(CODE_SUCCESS, responsePOST.statusCode(), "статус код неверный");

        //GET
        URI urlGET = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask?id=1");
        final HttpResponse<String> responseGET = sendGET(urlGET, client);
        Assertions.assertEquals(CODE_SUCCESS, responseGET.statusCode(), "статус код неверный");
        Assertions.assertNotNull(responseGET, "не получен ответ при обработке запроса GET");

        Subtask subtaskGET = (Subtask) Subtask.fromJSON(responseGET.body());
        subtask.setId(1L);

        Assertions.assertEquals(subtask, subtaskGET, "запрос GET вернул неверный объект");
    }

    @Test
    public void actionGetWithErrorIdSubtask() {

        HttpClient client = HttpClient.newHttpClient();

        URI urlGET = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask?id=0");

        HttpResponse<String> responseGET = sendGET(urlGET, client);

        Assertions.assertNotNull(responseGET, "нет ответа на сервере");
        Assertions.assertEquals(DEFAULT_RESPONSE, responseGET.body(), "ответ неверный");
        Assertions.assertEquals(CODE_ERROR, responseGET.statusCode(), "код ответа неверный");
    }

    @Test
    public void standardActionUpdateAndGetSubtask() {

        HttpClient client = HttpClient.newHttpClient();
        initEpic(client);

        // url
        URI urlCreate = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/");
        URI urlUpdate = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask?id=1");
        URI urlGet = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask?id=1");


        // создание
        Subtask subtask = new Subtask("task", "test", Status.NEW, 0L);
        subtask.setDefaultTimeAndDuration();

        HttpResponse<String> responseCreate = sendPOST(urlCreate, subtask, client);

        String header = "при отправке запроса на создание Subtask";
        Assertions.assertNotNull(responseCreate,   header + "не получен ответ");
        Assertions.assertEquals(CODE_SUCCESS, responseCreate.statusCode(), header + "статус код неверный");

        // обновление
        Subtask newSubtask = new Subtask("task update", "test", Status.DONE, 0L);
        newSubtask.setDefaultTimeAndDuration();
        newSubtask.setId(1L);

        HttpResponse<String> responseUpdate = sendPOST(urlUpdate, newSubtask, client);
        Assertions.assertEquals(CODE_SUCCESS, responseUpdate.statusCode(), "статус код неверный");

        header = "при отправке запроса на обновление Task ";
        Assertions.assertNotNull(responseUpdate,   header + "не получен ответ");
        Assertions.assertEquals(CODE_SUCCESS, responseCreate.statusCode(), "статус код неверный");


        // GET
        HttpResponse<String> responseGET = sendGET(urlGet, client);
        Assertions.assertNotNull(responseGET, "не получен ответ от сервера при обработке запроса GET");

        Subtask subtaskGET = (Subtask) Subtask.fromJSON(responseGET.body());
        subtask.setId(1L);
        Assertions.assertEquals(newSubtask, subtaskGET, "запрос GET вернул неверный объект");
    }

    @Test
    public void standardActionCreateAndDeleteSubtask() {

        HttpClient client = HttpClient.newHttpClient();
        initEpic(client);

        // url
        URI urlCreate = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/");
        URI urlDelete = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask?id=1");
        URI urlGet = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask?id=1");
        URI urlGetEpic = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic?id=0");

        // создание
        Subtask subtask = new Subtask("task", "test", Status.DONE, 0L);
        subtask.setDefaultTimeAndDuration();

        HttpResponse<String> responseCreate = sendPOST(urlCreate, subtask, client);

        String header = "при отправке запроса на создание Subtask ";
        Assertions.assertNotNull(responseCreate,   header + "не получен ответ");
        Assertions.assertEquals(CODE_SUCCESS, responseCreate.statusCode(), header + "статус код неверный");

        // удаление
        header = "при отправке запроса на удаление Task ";
        HttpResponse<String> responseDelete = sendDelete(urlDelete, client);
        Assertions.assertNotNull(responseDelete,   header + "не получен ответ");
        Assertions.assertEquals(CODE_SUCCESS, responseCreate.statusCode(), header + "статус код неверный");

        // GET
        HttpResponse<String> responseGET = sendGET(urlGet, client);
        Assertions.assertNotNull(responseGET, "не получен ответ от сервера при обработке запроса GET");
        Assertions.assertEquals(CODE_ERROR, responseGET.statusCode());
    }

    @Test
    public void standardActionCreateAndGetAllSubtask() {

        HttpClient client = HttpClient.newHttpClient();
        initEpic(client);

        // POST
        URI urlPOST = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/");

        Subtask subtask1 = new Subtask("task 0", "test", Status.NEW, 0L);
        subtask1.setDefaultTimeAndDuration();
        Subtask subtask2 = new Subtask("task 1", "test", Status.DONE, 0L);

        sendPOST(urlPOST, subtask1, client);
        sendPOST(urlPOST, subtask2, client);

        // GET
        URI urlGET = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask");

        HttpResponse<String> responseGET = sendGET(urlGET, client);
        Assertions.assertNotNull(responseGET, "не получен ответ от сервера при обработке запроса GET");
        List<Task> subtasksGET = Subtask.tasksFromJSON(responseGET.body());

        subtask1.setId(1);
        subtask2.setId(2);
        List<Task> expected = List.of(subtask1, subtask2);
        Assertions.assertEquals(2, subtasksGET.size());
        Assertions.assertEquals(expected, subtasksGET, "запрос GET вернул неверный объект");
    }

    @Test
    public void standardActionCreateAndDeleteAllSubtask() {

        HttpClient client = HttpClient.newHttpClient();
        initEpic(client);

        // POST
        URI urlPOST = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/");

        Subtask subtask1 = new Subtask("task 0", "test", Status.NEW, 0L);
        subtask1.setDefaultTimeAndDuration();
        Subtask subtask2 = new Subtask("task 1", "test", Status.DONE, 0L);

        sendPOST(urlPOST, subtask1, client);
        sendPOST(urlPOST, subtask2, client);

        // GET
        URI urlGET = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask");

        HttpResponse<String> responseGET = sendDelete(urlGET, client);
        Assertions.assertNotNull(responseGET, "не получен ответ от сервера при обработке запроса GET");
        List<Task> tasksGET = Subtask.tasksFromJSON(responseGET.body());
        Assertions.assertEquals(0, tasksGET.size());
    }

    // Epic
    public Subtask initSubtask(HttpClient client, Long idEpic) {
        URI urlPOST = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask/");
        Subtask subtask = new Subtask("subtask", "test", Status.NEW, idEpic);
        subtask.setDefaultTimeAndDuration();
        sendPOST(urlPOST, subtask, client);
        return subtask;
    }

    @Test
    public void standardActionCreateAndGetEpic() {

        HttpClient client = HttpClient.newHttpClient();

        //POST
        URI urlPOST = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/");

        Epic epic = new Epic("epic", "test");

        final HttpResponse<String> responsePOST = sendPOST(urlPOST, epic, client);
        Assertions.assertNotNull(responsePOST, "не получен ответ от сервера при обработке запроса POST");
        Assertions.assertEquals(CODE_SUCCESS, responsePOST.statusCode(), "статус код неверный");

        Subtask subtask =  initSubtask(client, 0L);

        //GET
        URI urlGET = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic?id=0");
        final HttpResponse<String> responseGET = sendGET(urlGET, client);
        Assertions.assertEquals(CODE_SUCCESS, responseGET.statusCode(), "статус код неверный");
        Assertions.assertNotNull(responseGET, "не получен ответ при обработке запроса GET");

        Epic epicGET = (Epic) Epic.fromJSON(responseGET.body());
        epic.setId(0L);
        epic.addSubtask(1L);
        epic.setStatus(subtask.getStatus());
        epic.setStartTime(subtask.getStartTime());
        epic.setDuration(subtask.getDuration());

        Assertions.assertEquals(epic, epicGET, "запрос GET вернул неверный объект");
    }

    @Test
    public void actionGetWithErrorIdEpic() {

        HttpClient client = HttpClient.newHttpClient();

        URI urlGET = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic?id=0");

        HttpResponse<String> responseGET = sendGET(urlGET, client);

        Assertions.assertNotNull(responseGET, "нет ответа на сервере");
        Assertions.assertEquals(DEFAULT_RESPONSE, responseGET.body(), "ответ неверный");
        Assertions.assertEquals(CODE_ERROR, responseGET.statusCode(), "код ответа неверный");
    }

    @Test
    public void standardActionUpdateAndGetEpic() {

        HttpClient client = HttpClient.newHttpClient();

        // url
        URI urlCreate = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/");
        URI urlUpdate = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic?id=0");
        URI urlGet = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic?id=0");

        // создание
        Epic epic = new Epic("task", "test");

        HttpResponse<String> responseCreate = sendPOST(urlCreate, epic, client);

        String header = "при отправке запроса на создание Subtask";
        Assertions.assertNotNull(responseCreate,   header + "не получен ответ");
        Assertions.assertEquals(CODE_SUCCESS, responseCreate.statusCode(), header + "статус код неверный");

        Subtask subtask = initSubtask(client, 0L);

        // обновление
        Epic newEpic = new Epic("task update", "test");
        newEpic.setId(0L);
        newEpic.setStatus(subtask.getStatus());
        newEpic.setStartTime(subtask.getStartTime());
        newEpic.setDuration(subtask.getDuration());

        HttpResponse<String> responseUpdate = sendPOST(urlUpdate, newEpic, client);
        Assertions.assertEquals(CODE_SUCCESS, responseUpdate.statusCode(), "статус код неверный");

        header = "при отправке запроса на обновление Task ";
        Assertions.assertNotNull(responseUpdate,   header + "не получен ответ");
        Assertions.assertEquals(CODE_SUCCESS, responseCreate.statusCode(), "статус код неверный");

        // GET
        HttpResponse<String> responseGET = sendGET(urlGet, client);
        Assertions.assertNotNull(responseGET, "не получен ответ от сервера при обработке запроса GET");

        Epic epicGET = (Epic) Epic.fromJSON(responseGET.body());
        Assertions.assertEquals(newEpic, epicGET, "запрос GET вернул неверный объект");
    }

    @Test
    public void standardActionCreateAndDeleteEpic() {

        HttpClient client = HttpClient.newHttpClient();

        // url
        URI urlCreate = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/");
        URI urlDelete = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic?id=0");
        URI urlGet = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic?id=0");

        // создание
        Epic epic = new Epic("task", "test");

        HttpResponse<String> responseCreate = sendPOST(urlCreate, epic, client);

        String header = "при отправке запроса на создание Subtask ";
        Assertions.assertNotNull(responseCreate,   header + "не получен ответ");
        Assertions.assertEquals(CODE_SUCCESS, responseCreate.statusCode(), header + "статус код неверный");

        // удаление
        header = "при отправке запроса на удаление Task ";
        HttpResponse<String> responseDelete = sendDelete(urlDelete, client);
        Assertions.assertNotNull(responseDelete,   header + "не получен ответ");
        Assertions.assertEquals(CODE_SUCCESS, responseCreate.statusCode(), header + "статус код неверный");

        // GET
        HttpResponse<String> responseGET = sendGET(urlGet, client);
        Assertions.assertNotNull(responseGET, "не получен ответ от сервера при обработке запроса GET");
        Assertions.assertEquals(CODE_ERROR, responseGET.statusCode());
    }

    @Test
    public void standardActionCreateAndGetAllEpic() {

        HttpClient client = HttpClient.newHttpClient();

        // POST
        URI urlPOST = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/");

        Epic epic0 = new Epic("task 0", "test");
        Epic epic1 = new Epic("task 1", "test");

        sendPOST(urlPOST, epic0, client);
        sendPOST(urlPOST, epic1, client);

        Subtask subtask = initSubtask(client, 1L);

        // GET
        URI urlGET = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic");

        HttpResponse<String> responseGET = sendGET(urlGET, client);
        Assertions.assertNotNull(responseGET, "не получен ответ от сервера при обработке запроса GET");
        List<Task> epicsGET = Epic.tasksFromJSON(responseGET.body());

        epic0.setId(0L);
        epic0.setStatus(Status.NEW);
        epic1.setId(1L);
        epic1.setStatus(Status.NEW);
        epic1.setStartTime(subtask.getStartTime());
        epic1.setDuration(subtask.getDuration());

        List<Task> expected = List.of(epic0, epic1);
        Assertions.assertEquals(2, epicsGET.size());
        Assertions.assertEquals(expected, epicsGET, "запрос GET вернул неверный объект");
    }

    @Test
    public void standardActionCreateAndDeleteAllEpics() {

        HttpClient client = HttpClient.newHttpClient();

        // POST
        URI urlPOST = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic/");

        Epic epic0 = new Epic("task 0", "test");
        Epic epic1 = new Epic("task 1", "test");

        sendPOST(urlPOST, epic0, client);
        sendPOST(urlPOST, epic1, client);

        initSubtask(client, 1L);

        // GET
        URI urlGET = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic");

        HttpResponse<String> responseGET = sendDelete(urlGET, client);
        Assertions.assertNotNull(responseGET, "не получен ответ от сервера при обработке запроса GET");
        List<Task> epicsGET = Epic.tasksFromJSON(responseGET.body());
        Assertions.assertEquals(0, epicsGET.size());

        // GET
        URI urlGETSubtask = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask");

        HttpResponse<String> responseGETSubtasks = sendDelete(urlGETSubtask, client);
        Assertions.assertNotNull(responseGETSubtasks, "не получен ответ от сервера при обработке запроса GET");
        List<Task> tasksGET = Subtask.tasksFromJSON(responseGETSubtasks.body());
        Assertions.assertEquals(0, tasksGET.size());
    }

    public Task initTasks(HttpClient client) {
        URI urlPOST = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task/");
        Task task = new Task("task", "test", Status.NEW);
        task.setDefaultTimeAndDuration();
        sendPOST(urlPOST, task, client);
        return task;
    }

    @Test
    public void standardActionGetALLTasksEpicsSubtasks() {

        HttpClient client = HttpClient.newHttpClient();
        Epic epic = initEpic(client);
        Subtask subtask = initSubtask(client, 0L);
        Task task = initTasks(client);

        // GET
        URI urlGET = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks");

        HttpResponse<String> responseGET = sendGET(urlGET, client);
        Assertions.assertNotNull(responseGET, "не получен ответ от сервера при обработке запроса GET");
        List<Task> actual = Task.tasksFromJSON(responseGET.body());

        epic.setId(0L);
        epic.setStatus(Status.NEW);
        epic.addSubtask(1L);
        epic.setStartTime(subtask.getStartTime());
        epic.setDuration(subtask.getDuration());

        subtask.setId(1L);
        task.setId(2L);

        List<Task> expected = List.of(task, subtask, epic);

        Assertions.assertEquals(3, actual.size());

        for (Task t : expected){
            if (t instanceof Subtask) {
                Subtask s = (Subtask) t;
                Assertions.assertEquals(s, subtask, "запрос GET вернул неверный объект");
            } else if (t instanceof Epic){
                Epic e = (Epic) t;
                Assertions.assertEquals(e, epic, "запрос GET вернул неверный объект");
            } else {
                Assertions.assertEquals(t, task, "запрос GET вернул неверный объект");
            }
        }
    }

    @Test
    public void standardActionGetHistory() {

        HttpClient client = HttpClient.newHttpClient();
        Epic epic = initEpic(client);
        Subtask subtask = initSubtask(client, 0L);
        Task task = initTasks(client);

        // GET
        URI urlGETTask = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/task?id=2");
        sendGET(urlGETTask, client);
        URI urlGETSubtask = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/subtask?id=1");
        sendGET(urlGETSubtask, client);
        URI urlGETEpic = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/epic?id=0");
        sendGET(urlGETEpic, client);

        URI urlGETHistory = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/history");
        HttpResponse<String> responseHistory = sendGET(urlGETHistory, client);
        Assertions.assertNotNull(responseHistory, "не получен ответ от сервера при обработке запроса GET");
        List<Task> actual = Task.tasksFromJSON(responseHistory.body());

        epic.setId(0L);
        epic.setStatus(Status.NEW);
        epic.addSubtask(1L);
        epic.setStartTime(subtask.getStartTime());
        epic.setDuration(subtask.getDuration());

        subtask.setId(1L);
        task.setId(2L);

        List<Task> expected = List.of(task, subtask, epic);
        Assertions.assertEquals(3, actual.size());

        for (Task t : expected){
            if (t instanceof Subtask) {
                Subtask s = (Subtask) t;
                Assertions.assertEquals(s, subtask, "запрос GET вернул неверный объект");
            } else if (t instanceof Epic){
                Epic e = (Epic) t;
                Assertions.assertEquals(e, epic, "запрос GET вернул неверный объект");
            } else {
                Assertions.assertEquals(t, task, "запрос GET вернул неверный объект");
            }
        }
    }
}
