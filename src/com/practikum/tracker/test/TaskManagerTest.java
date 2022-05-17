package com.practikum.tracker.test;

import com.practikum.tracker.manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.practikum.tracker.model.Epic;
import com.practikum.tracker.model.Status;
import com.practikum.tracker.model.Subtask;
import com.practikum.tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;


public abstract class TaskManagerTest<T extends TaskManager> {

    public T manager;

    // 2.1 Получение списка всех задач

    // методы инициализации списка задач
    List<Task> initTestTasks(){
        Task t1 = new Task("com.practikum.tracker.task 0", "test", Status.NEW);
        t1.setDefaultTimeAndDuration();
        Task t2 = new Task("com.practikum.tracker.task 1", "test", Status.IN_PROGRESS);
        t2.setDefaultTimeAndDuration();
        Task t3 = new Task("com.practikum.tracker.task 2", "test", Status.DONE);
        t3.setDefaultTimeAndDuration();

        manager.createTask(t1);
        manager.createTask(t2);
        manager.createTask(t3);

        return List.of(t1, t2, t3);
    }

    Epic initTestEmptyEpic(){
        Epic epic = new Epic("epic 0", "test");
        manager.createEpic(epic);
        return epic;
    }

    List<Subtask> initTestSubtasks() {
        Epic epic = initTestEmptyEpic();

        Subtask s1 = new Subtask("subtask 1", "test", Status.NEW, epic.getId());
        s1.setDefaultTimeAndDuration();
        Subtask s2 = new Subtask("subtask 2", "test", Status.IN_PROGRESS, epic.getId());
        s2.setDefaultTimeAndDuration();
        Subtask s3 = new Subtask("subtask 3", "test", Status.DONE, epic.getId());
        s3.setDefaultTimeAndDuration();

        manager.createSubtask(s1);
        manager.createSubtask(s2);
        manager.createSubtask(s3);

        return List.of(s1, s2, s3);
    }

    List<Epic> initTestEpics(){
        Epic e1 = new Epic("epic 0", "test");
        Epic e2 = new Epic("epic 1", "test");
        Epic e3 = new Epic("epic 2", "test");

        manager.createEpic(e1);
        manager.createEpic(e2);
        manager.createEpic(e3);

        Subtask s1 = new Subtask("subtask 1", "test", Status.NEW, e1.getId());
        manager.createSubtask(s1);

        Subtask s2 = new Subtask("subtask 2", "test", Status.NEW, e2.getId());
        manager.createSubtask(s2);

        return List.of(e1, e2, e3);
    }

    // тесты на getAll
    @Test
    public void standardActionGetAllTasks(){
        List<Task> expected = initTestTasks();
        List<Task> actual = manager.getAllTasks();
        Assertions.assertNotNull(actual, "вернул null");
        Assertions.assertArrayEquals(expected.toArray(), actual.toArray(), "списки объектов не совпадают");
        TreeSet<Task> tasks = manager.getPrioritizedTasks();
        Assertions.assertArrayEquals(expected.toArray(), tasks.toArray());
    }

    @Test
    public void standardActionGetAllSubtasks(){
        List<Subtask> expected = initTestSubtasks();
        List<Subtask> actual = manager.getAllSubtasks();
        Assertions.assertNotNull(actual, "вернул null");
        Assertions.assertArrayEquals(expected.toArray(), actual.toArray(), "списки объектов не совпадают");

        TreeSet<Task> tasks = manager.getPrioritizedTasks();
        Assertions.assertArrayEquals(expected.toArray(), tasks.toArray());
    }

    @Test
    public void standardActionGetAllEpics(){
        List<Epic> expected = initTestEpics();
        List<Epic> actual = manager.getAllEpics();
        Assertions.assertNotNull(actual, "вернул null");
        Assertions.assertArrayEquals(expected.toArray(), actual.toArray(), "списки объектов не совпадают");
    }

    @Test
    public void actionGetAllWhenEmptyTasksSubtasksEpics(){

        List<Task> allTasks = manager.getAllTasks();
        List<Subtask> allSubtasks = manager.getAllSubtasks();
        List<Epic> allEpics = manager.getAllEpics();

        Assertions.assertNotNull(allTasks, "вернул null");
        Assertions.assertNotNull(allSubtasks, "вернул null");
        Assertions.assertNotNull(allEpics, "вернул null");

        Assertions.assertTrue(allTasks.isEmpty(), "вернул непустой список");
        Assertions.assertTrue(allSubtasks.isEmpty(), "вернул непустой список");
        Assertions.assertTrue(allEpics.isEmpty(), "вернул непустой список");
    }

    // тесты на removeAll
    @Test
    public void standardActionRemoveAllTask(){
        initTestTasks();
        manager.removeAllTasks();

        List<Task> allTasks = manager.getAllTasks();
        Assertions.assertTrue(allTasks.isEmpty(), "не удалил все Task");
    }

    @Test
    public void standardActionRemoveAllSubtasks(){
        initTestSubtasks();
        manager.removeAllSubtasks();

        List<Subtask> allSubtasks = manager.getAllSubtasks();
        Assertions.assertEquals(0, allSubtasks.size(), "не удалил все Subtask");

        List<Epic> allEpics = manager.getAllEpics();
        for (Epic e : allEpics) {
            Assertions.assertTrue(e.getSubtasks().isEmpty(),"не удалил Subtask из Epic");
        }
    }

    @Test
    public void standardActionRemoveAllEpics(){
        initTestEpics();
        manager.removeAllEpics();

        List<Epic> allEpics = manager.getAllEpics();
        Assertions.assertTrue(allEpics.isEmpty(), "не удалил все объекты Epic");

        List<Subtask> allSubtasks = manager.getAllSubtasks();
        Assertions.assertTrue(allSubtasks.isEmpty(), "не удалил все объекты Subtask");
    }

    @Test
    public void actionRemovedAllWhenEmptyTasksSubtasksEpics(){
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();

        List<Task> allTasks = manager.getAllTasks();
        List<Subtask> allSubtasks = manager.getAllSubtasks();
        List<Epic> allEpics = manager.getAllEpics();

        Assertions.assertTrue(allTasks.isEmpty(), "вернул непустой список Tasks");
        Assertions.assertTrue(allSubtasks.isEmpty(), "вернул непустой список Subtasks");
        Assertions.assertTrue(allEpics.isEmpty(), "вернул непустой список Epics");
    }

    // 2.3 Получение по идентификатору
    // тесты на get
    @Test
    public void standardActionGetTask(){
        Task expected = initTestTasks().get(0);
        Task actual = manager.getTask(expected.getId());

        Assertions.assertNotNull(actual, "нет объекта c данным id");
        Assertions.assertEquals(expected, actual, "не совпадают");
    }

    @Test
    public void actionGetTaskByErrorId(){

        Task nullIdTask = manager.getTask(null);
        Assertions.assertNull(nullIdTask, "вернул объект не null");

        Task errorIdTask = manager.getTask(manager.getId());
        Assertions.assertNull(errorIdTask, "вернул объект не null");
    }

    @Test
    public void standardActionGetSubtasks(){

        Subtask expected = initTestSubtasks().get(0);
        Subtask actual = manager.getSubtask(expected.getId());

        Assertions.assertNotNull(actual, "нет объекта с данным id");
        Assertions.assertEquals(expected, actual, "не совпадают");

        Epic epicSubtask = manager.getEpic(actual.getIdEpic());
        Assertions.assertNotNull(epicSubtask, "Epic не существует");

        List<Long> idSubtasks = epicSubtask.getSubtasks();
        Assertions.assertTrue(idSubtasks.contains(actual.getId()), "У Epic нет Subtask");
    }

    @Test
    public void actionGetSubtaskByErrorId(){

        Subtask nullIdSubtask = manager.getSubtask(null);
        Assertions.assertNull(nullIdSubtask, "вернул объект не null");

        Subtask errorIdSubtask = manager.getSubtask(1000L);
        Assertions.assertNull(errorIdSubtask, "вернул объект не null");
    }

    @Test
    public void standardActionGetEpic(){

        Epic expected = initTestEpics().get(0);
        Epic actual = manager.getEpic(expected.getId());

        Assertions.assertNotNull(actual, "нет объекта с данным id");
        Assertions.assertEquals(expected, actual, "не совпадают");

        List<Long> subtasksEpic = actual.getSubtasks();
        Assertions.assertNotNull(subtasksEpic, "вернул null");

        for (Long id : subtasksEpic) {
            Assertions.assertNotNull(manager.getSubtask(id), "нет Subtask из Epic в списке всех Subtasks");
        }
    }

    @Test
    public void actionGetEpicByErrorId(){
        initTestEpics();

        Epic nullIdEpic = manager.getEpic(null);
        Assertions.assertNull(nullIdEpic, "вернул объект не null");

        Subtask errorIdEpic = manager.getSubtask(1000L);
        Assertions.assertNull(errorIdEpic, "вернул объект не null");
    }

    // 2.4 Создание
    // тесты на create
    @Test
    public void standardActionCreateTask() {
        LocalDateTime start = LocalDateTime.of(2022, 5, 12, 8, 30, 0);
        Duration duration = Duration.ofMinutes(30);

        Task expected = new Task("com/practikum/tracker/model", "test");
        expected.setStartTime(start);
        expected.setDuration(duration);

        manager.createTask(expected);

        Task actual = manager.getTask(expected.getId());
        Assertions.assertNotNull(actual, "не сохранено");
        Assertions.assertEquals(expected, actual, "не совпадают");
        TreeSet<Task> tasks = manager.getPrioritizedTasks();
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals(actual, tasks.first());

    }

    @Test
    public void actionCreateTaskWithDefaultTime() {

        Task expected = new Task("com/practikum/tracker/model", "test");
        expected.setDefaultTimeAndDuration();

        manager.createTask(expected);

        Task actual = manager.getTask(expected.getId());
        TreeSet<Task> tasks = manager.getPrioritizedTasks();
        Assertions.assertNotNull(actual, "не сохранено");
        Assertions.assertEquals(expected, actual, "не совпадают");
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals(actual, tasks.first());

    }

    @Test
    public void actionCreateNullTask() {
        initTestTasks();
        List<Task> expected = manager.getAllTasks();

        Assertions.assertDoesNotThrow(() -> manager.createTask(null), "вызвано исключение при создании");

        List<Task> actual = manager.getAllTasks();

        TreeSet<Task> tasks = manager.getPrioritizedTasks();

        Assertions.assertNotNull(actual, "нет списка задач");
        Assertions.assertEquals(expected, actual, "список задач изменился");
        Assertions.assertEquals(3, tasks.size());
    }

    @Test
    public void standardActionCreateSubtask() {
        Epic epic = initTestEmptyEpic();
        manager.createEpic(epic);

        LocalDateTime start = LocalDateTime.of(2022, 5, 12, 8, 30, 0);
        Duration duration = Duration.ofMinutes(30);

        Subtask expected = new Subtask("Subtask", "Test", Status.NEW, epic.getId());
        expected.setStartTime(start);
        expected.setDuration(duration);

        manager.createSubtask(expected);

        Subtask actual = manager.getSubtask(expected.getId());

        Assertions.assertNotNull(actual, "не сохранено");
        Assertions.assertEquals(expected, actual, "не совпадают");
        Assertions.assertTrue(epic.getSubtasks().contains(actual.getId()), "нет подзадачи в Epic");
        Assertions.assertTrue(epic.getStartTime().isEqual(start));
        Assertions.assertTrue(epic.getEndTime().isEqual(actual.getEndTime()));

        TreeSet<Task> tasks = manager.getPrioritizedTasks();
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals(actual, tasks.first());
    }

    @Test
    public void actionCreateNullSubtask() {
        initTestSubtasks();
        List<Subtask> expected = manager.getAllSubtasks();

        Assertions.assertDoesNotThrow(() -> manager.createSubtask(null));

        List<Subtask> actual = manager.getAllSubtasks();

        Assertions.assertNotNull(actual, "нет списка Epic");
        Assertions.assertEquals(expected, actual, "не совпадают");

        TreeSet<Task> tasks = manager.getPrioritizedTasks();
        Assertions.assertEquals(3, tasks.size());
    }

    @Test
    public void actionCreateSubtaskWithErrorIdEpic() {
        List<Subtask> allSubtasks =  initTestSubtasks();
        List<Epic> allEpics = manager.getAllEpics();

        Subtask expected = new Subtask("Subtask", "Test", Status.NEW, manager.getId());

        Assertions.assertDoesNotThrow(() -> manager.createSubtask(expected), "вызвано исключение");

        List<Subtask> actualSubtasks = manager.getAllSubtasks();
        List<Epic> actualEpics = manager.getAllEpics();


        Assertions.assertEquals(allSubtasks, actualSubtasks, "изменился список Subtasks");
        Assertions.assertEquals(allEpics, actualEpics, "изменился список Epics");
    }

    // a. пустой список задач
    @Test
    void standardActionCreateEpicWithEmptyListSubtasks() {
        Epic expected = new Epic("epic", "test");
        manager.createEpic(expected);

        Epic actual = manager.getEpic(expected.getId());

        Assertions.assertNotNull(actual, "не сохранён");
        Assertions.assertEquals(expected, actual, "не совпадают");
        Assertions.assertEquals(Status.NEW, actual.getStatus(), "Статус пустого Epic должен быть NEW");
    }

    // b. Все подзадачи со статусом NEW.
    @Test
    void standardActionCreateEpicWithListNewSubtasks(){
        Epic epic = new Epic("epic", "test");
        manager.createEpic(epic);

        Subtask s1 = new Subtask("subtasks", "test", Status.NEW, epic.getId());
        Subtask s2 = new Subtask("subtasks", "test", Status.NEW, epic.getId());
        Subtask s3 = new Subtask("subtasks", "test", Status.NEW, epic.getId());

        manager.createSubtask(s1);
        manager.createSubtask(s2);
        manager.createSubtask(s3);

        Epic saved = manager.getEpic(epic.getId());

        Assertions.assertNotNull(saved, "Epic не сохранён");
        Assertions.assertEquals(epic, saved, "Epic не совпадают");
        Assertions.assertEquals(Status.NEW, saved.getStatus(), "Статус Epic c Subtask NEW должен быть NEW");
    }

    // c. Все подзадачи со статусом DONE.
    @Test
    void standardActionCreateEpicWithListDoneSubtasks(){
        Epic expected = new Epic("epic", "test");
        manager.createEpic(expected);

        Subtask s1 = new Subtask("subtasks", "test", Status.DONE, expected.getId());
        Subtask s2 = new Subtask("subtasks", "test", Status.DONE, expected.getId());
        Subtask s3 = new Subtask("subtasks", "test", Status.DONE, expected.getId());
        manager.createSubtask(s1);
        manager.createSubtask(s2);
        manager.createSubtask(s3);

        Epic actual = manager.getEpic(expected.getId());
        Assertions.assertEquals(Status.DONE, actual.getStatus());
    }

    //d. Подзадачи со статусами NEW и DONE.
    @Test
    void standardActionCreateEpicWithListDoneAndNewSubtasks(){
        Epic epic = new Epic("epic", "test");
        manager.createEpic(epic);

        Subtask s1 = new Subtask("subtasks", "test", Status.DONE, epic.getId());
        Subtask s2 = new Subtask("subtasks", "test", Status.NEW, epic.getId());
        Subtask s3 = new Subtask("subtasks", "test", Status.DONE, epic.getId());
        manager.createSubtask(s1);
        manager.createSubtask(s2);
        manager.createSubtask(s3);

        Epic saved = manager.getEpic(epic.getId());
        Assertions.assertEquals(Status.IN_PROGRESS, saved.getStatus());
    }

    //e. Подзадачи со статусом IN_PROGRESS
    @Test
    void standardActionCreateEpicWithListAnyOneInProgressSubtasks(){
        Epic epic = new Epic("epic", "test");
        manager.createEpic(epic);

        Subtask s1 = new Subtask("subtasks", "test", Status.NEW, epic.getId());
        Subtask s2 = new Subtask("subtasks", "test", Status.IN_PROGRESS, epic.getId());
        Subtask s3 = new Subtask("subtasks", "test", Status.DONE, epic.getId());
        manager.createSubtask(s1);
        manager.createSubtask(s2);
        manager.createSubtask(s3);

        Epic saved = manager.getEpic(epic.getId());
        Assertions.assertEquals(Status.IN_PROGRESS, saved.getStatus());
    }

    @Test
    public void actionCreateNullEpic() {
        initTestEpics();
        List<Epic> expected = manager.getAllEpics();

        Assertions.assertDoesNotThrow(() -> manager.createEpic(null));

        List<Epic> actual = manager.getAllEpics();

        Assertions.assertNotNull(actual, "нет списка Epic");
        Assertions.assertEquals(expected, actual, "не совпадают");
    }

    // 2.5 Обновление
    // тесты на update
    @Test
    public void standardActionUpdateTask() {
        LocalDateTime startOld = LocalDateTime.of(2022, 5, 12, 8, 30, 0);
        Duration durationOLd = Duration.ofMinutes(30);

        Task task = new Task("com/practikum/tracker/model", "test", Status.NEW);
        task.setStartTime(startOld);
        task.setDuration(durationOLd);
        manager.createTask(task);

        Task expected = new Task("com.practikum.tracker.task update", "test update", Status.DONE);
        expected.setStartTime(startOld);
        expected.setDuration(Duration.ofMinutes(60));
        expected.setId(task.getId());

        manager.updateTask(expected);
        Task actual = manager.getTask(task.getId());

        Assertions.assertNotNull(actual, "не сохранён");
        Assertions.assertEquals(expected, actual, "не совпадают");
        Assertions.assertEquals(expected.getId(), task.getId(), "id не совпадают");
        Assertions.assertNotEquals(actual, task, "задача не обновлена");

        TreeSet<Task> tasks = manager.getPrioritizedTasks();
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals(actual, tasks.first());

    }

    @Test
    public void actionUpdateTaskNull() {
        initTestTasks();
        List<Task> expected = manager.getAllTasks();
        Assertions.assertDoesNotThrow(() -> manager.updateTask(null), "вызвано исключение");
        List<Task> actual = manager.getAllTasks();
        Assertions.assertArrayEquals(expected.toArray(), actual.toArray(), "изменено состояние объектов");
    }

    @Test
    public void actionUpdateTaskErrorId() {
        Task task = new Task("com/practikum/tracker/model", "test", Status.NEW);
        manager.createTask(task);

        Task errorIdTask = new Task("com.practikum.tracker.task update", "test update", Status.DONE);
        errorIdTask.setId(manager.getId()); // назначаем несуществующий Id

        Assertions.assertDoesNotThrow(() -> manager.updateTask(errorIdTask), "вызвано исключение");

        Task actual = manager.getTask(task.getId());
        Assertions.assertNotNull(actual, "не сохранён");
        Assertions.assertNotEquals(errorIdTask, actual, "совпадают");
        Assertions.assertNotEquals(errorIdTask.getId(), task.getId(), "id совпадают");
        Assertions.assertEquals(actual, task, "задача обновлена");

        Task nullTask = manager.getTask(errorIdTask.getId());
        Assertions.assertNull(nullTask, "сохранён ошибочный Task");
    }

    @Test
    public void standardActionUpdateSubtask(){
        Epic epic = new Epic("epic", "test");
        manager.createEpic(epic);

        LocalDateTime startOld = LocalDateTime.of(2022, 5, 12, 8, 30, 0);
        Duration durationOLd = Duration.ofMinutes(30);

        Subtask subtask = new Subtask("com/practikum/tracker/model", "test", Status.NEW, epic.getId());
        subtask.setStartTime(startOld);
        subtask.setDuration(durationOLd);
        manager.createSubtask(subtask);

        Subtask expected = new Subtask("com.practikum.tracker.task update", "test update", Status.DONE, epic.getId());
        expected.setId(subtask.getId());
        expected.setStartTime(startOld);
        expected.setDuration(Duration.ofMinutes(15));

        manager.updateSubtask(expected);
        Subtask actual = manager.getSubtask(subtask.getId());

        Assertions.assertNotNull(actual, "не сохранён");
        Assertions.assertEquals(expected.getId(), actual.getId(), "id не совпадают");
        Assertions.assertNotEquals(actual, subtask, "задача не обновлена");

        Assertions.assertTrue(epic.getStartTime().isEqual(actual.getStartTime()));
        Assertions.assertTrue(epic.getEndTime().isEqual(actual.getEndTime()));

        TreeSet<Task> tasks = manager.getPrioritizedTasks();
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals(actual, tasks.first());
    }

    @Test
    public void actionUpdateSubtaskNull() {
        initTestSubtasks();
        List<Subtask> expected = manager.getAllSubtasks();
        Assertions.assertDoesNotThrow(() -> manager.updateSubtask(null), "вызвано исключение");
        List<Subtask> actual = manager.getAllSubtasks();
        Assertions.assertArrayEquals(expected.toArray(), actual.toArray(), "изменено состояние объектов");
    }

    @Test
    public void actionUpdateSubtaskErrorId() {

        Epic epic = new Epic("epic", "test");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("com/practikum/tracker/model", "test", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        Subtask errorIdSubtask = new Subtask("com.practikum.tracker.task update", "test update", Status.DONE, epic.getId());
        errorIdSubtask.setId(manager.getId()); // назначаем несуществующий Id

        Assertions.assertDoesNotThrow(() -> manager.updateTask(errorIdSubtask), "вызвано исключение");

        Subtask actual = manager.getSubtask(subtask.getId());
        Assertions.assertNotNull(actual, "не сохранён");
        Assertions.assertNotEquals(errorIdSubtask, actual, "совпадают");
        Assertions.assertNotEquals(errorIdSubtask.getId(), subtask.getId(), "id совпадают");
        Assertions.assertEquals(actual, subtask, "задача обновлена");

        Subtask nullTask = manager.getSubtask(errorIdSubtask.getId());
        Assertions.assertNull(nullTask, "сохранён ошибочный Subtask");
    }

    @Test
    public void actionUpdateSubtaskErrorIdEpic() {

        Epic epic = new Epic("epic", "test");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("com/practikum/tracker/model", "test", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        // назначаем несуществующий id epic
        Subtask errorIdEpicSubtask = new Subtask("com.practikum.tracker.task update", "test update", Status.DONE, manager.getId());
        errorIdEpicSubtask.setId(subtask.getId());

        Assertions.assertDoesNotThrow(() -> manager.updateTask(errorIdEpicSubtask), "вызвано исключение");

        Subtask actual = manager.getSubtask(subtask.getId());

        Assertions.assertNotNull(actual, "не сохранён");
        Assertions.assertNotEquals(errorIdEpicSubtask, actual, "совпадают");
        Assertions.assertEquals(actual, subtask, "задача обновлена");
        Assertions.assertNull(manager.getEpic(errorIdEpicSubtask.getId()), "создан Epic");
    }

    @Test
    public void standardActionUpdateEpic(){
        Epic epic = new Epic("epic", "test");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "test", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        Epic updateEpic = new Epic("epic update", "test update");
        updateEpic.setId(epic.getId());
        manager.updateEpic(updateEpic);

        Epic actual = manager.getEpic(epic.getId());

        Assertions.assertNotNull(actual, "не сохранён");
        Assertions.assertEquals(updateEpic, actual, "не совпадают");
        Assertions.assertEquals(actual.getStatus(), Status.NEW, "статус неверен");
        Assertions.assertEquals(1, actual.getSubtasks().size() , "список подзадач не пустой");
    }

    @Test
    public void standardActionRemoveTask(){
        Task task = new Task("com/practikum/tracker/model", "test", Status.NEW);
        manager.createTask(task);

        Task expected = manager.getTask(task.getId());
        manager.removeTask(expected.getId());
        Task actual = manager.getTask(task.getId());

        Assertions.assertNull(actual, "объект не удалён");
        Assertions.assertTrue(manager.getPrioritizedTasks().isEmpty());
    }
    @Test
    public void actionRemovedTaskErrorIdAndNullId() {
        Assertions.assertDoesNotThrow(
                () -> manager.removeTask(manager.getId()), "вызвано исключение при удалении несуществующего id"
        );
        Assertions.assertDoesNotThrow(
                () -> manager.removeTask(null), "вызвано исключение при id = null"
        );
    }

    @Test
    public void standardActionRemovedSubtask(){
        Epic epic = new Epic("epic", "test");
        manager.createEpic(epic);

        Subtask expected = new Subtask("subtask", "test", Status.NEW, epic.getId());
        manager.createSubtask(expected);

        manager.removeSubtask(expected.getId());

        Subtask actual = manager.getSubtask(expected.getId());
        Assertions.assertNull(actual, "объект не удалён");
        Assertions.assertFalse(epic.getSubtasks().contains(expected.getId()), "объект не удалён из Epic");
        Assertions.assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    @Test
    public void actionRemovedSubtaskErrorId(){
        Assertions.assertDoesNotThrow(
                () -> manager.removeSubtask(0L), "вызвано исключение при удалении по" +
                        " несуществующему id"
        );
        Assertions.assertDoesNotThrow(
                () -> manager.removeSubtask(null), "вызвано исключение при удалении id = null"
        );
    }

    @Test
    public void standardActionRemovedEpic(){
        Epic epic = new Epic("epic", "test");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("subtask", "test", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        manager.removeEpic(epic.getId());

        Epic actual = manager.getEpic(epic.getId());
        Assertions.assertNull(actual, "объект не удалён");
        Assertions.assertNull(manager.getSubtask(subtask.getId()), "объект не удалён из subtask");
        Assertions.assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    @Test
    public void actionRemovedEpicErrorId(){
        Assertions.assertDoesNotThrow(
                () -> manager.removeEpic(manager.getId()), "вызвано исключение при несуществующем id"
        );
        Assertions.assertDoesNotThrow(
                () -> manager.removeEpic(null), "вызвано исключение при id = null"
        );
    }

    @Test
    public void standardActionGetSubtasksEpic(){
        Epic epic = new Epic("epic", "test");
        manager.createEpic(epic);

        Subtask s1 = new Subtask("subtask 1", "test", Status.NEW, epic.getId());
        manager.createSubtask(s1);
        Subtask s2 = new Subtask("subtask 2", "test", Status.IN_PROGRESS, epic.getId());
        manager.createSubtask(s2);
        Subtask s3 = new Subtask("subtask 3", "test", Status.DONE, epic.getId());
        manager.createSubtask(s3);

        List<Subtask> subtasksEpic = manager.getSubtasksEpic(epic.getId());
        Assertions.assertNotNull(subtasksEpic, "список не сформирован");
        Assertions.assertArrayEquals(List.of(s1, s2, s3).toArray(), subtasksEpic.toArray(), "не соответствуют");
    }

    @Test
    public void actionGetSubtasksEpicByErrorId(){

        Assertions.assertDoesNotThrow(() -> manager.getSubtasksEpic(manager.getId()),
                "вызвано исключение при вызове метода с несуществующим id");

        Assertions.assertDoesNotThrow(() -> manager.getSubtasksEpic(null),
                "вызвано исключение при вызове метода с  id = null");
    }

    @Test
    public void standardActionGetPrioritized() {
        LocalDateTime start = LocalDateTime.of(2022, 5, 12, 8, 30, 0);
        Duration duration = Duration.ofMinutes(30);

        Task t1 = new Task("com.practikum.tracker.task 0", "test", Status.NEW);
        t1.setStartTime(start);
        t1.setDuration(duration);

        Task t2 = new Task("com.practikum.tracker.task 1", "test", Status.IN_PROGRESS);
        t2.setStartTime(start.minus(duration));
        t2.setDuration(duration);
        Task t3 = new Task("com.practikum.tracker.task 3", "test", Status.DONE);
        t3.setStartTime(start);
        t3.setDuration(duration);
        Task t4 = new Task("com.practikum.tracker.task 4", "test", Status.DONE);
        Task t5 = new Task("com.practikum.tracker.task 5", "test", Status.DONE);

        manager.createTask(t1);
        manager.createTask(t2);
        manager.createTask(t3);
        manager.createTask(t4);
        manager.createTask(t5);

        Task[] expected = {t2, t1, t3, t4, t5};
        TreeSet<Task> actual = manager.getPrioritizedTasks();
        Assertions.assertArrayEquals(expected, actual.toArray());
    }
}
