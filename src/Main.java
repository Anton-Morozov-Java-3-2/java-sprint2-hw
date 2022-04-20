import manager.*;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        HistoryManager history = Managers.getHistory();

        // создать две задачи
        Task task1 = new Task("Задача 1", "Тест", manager.getId(), Status.NEW);
        manager.createTask(task1);

        Task task2 = new Task("Задача 2", "Тест", manager.getId(), Status.NEW);
        manager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Тест", manager.getId(), Status.NEW, new ArrayList<Long>());

        Subtask subtask1 = new Subtask("Подзадача 1", "Тест", manager.getId(), Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Тест", manager.getId(), Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Тест", manager.getId(), Status.NEW, epic1.getId());

        epic1.addSubtask(subtask1.getId());
        epic1.addSubtask(subtask2.getId());
        epic1.addSubtask(subtask3.getId());

        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        Epic epic2 = new Epic("Эпик 2", "Тест", manager.getId(), Status.NEW, new ArrayList<Long>());
        manager.createEpic(epic2);

        manager.getTask(task1.getId());
        history.add(task1);

        manager.getTask(task2.getId());
        history.add(task2);

        System.out.println("Запросили задачи 0, 1");
        System.out.println(printHistory(history.getHistory()));

        manager.getTask(task2.getId());
        history.add(task1);

        System.out.println("Запросили задачу 0");
        System.out.println(printHistory(history.getHistory()));

        for (Task t: manager.getSubtasksEpic(epic1.getId())) {
            history.add(t);
        }

        System.out.println("Запросили подзадачи 3, 4, 5");
        System.out.println(printHistory(history.getHistory()));

        for (Task e: manager.getAllEpics()) {
            history.add(e);
        }

        System.out.println("Запросили эпики 2, 6");
        System.out.println(printHistory(history.getHistory()));

        manager.getTask(task1.getId());
        history.add(task1);

        manager.getTask(task2.getId());
        history.add(task2);

        System.out.println("Запросили задачи 0, 1");
        System.out.println(printHistory(history.getHistory()));

        System.out.println("Удаляем задачу 0");
        history.remove(task1.getId());
        System.out.println(printHistory(history.getHistory()));

        System.out.println("Удаляем эпик 1");
        //history.remove(epic1.getId());
        manager.removeEpic(epic1.getId());
        System.out.println(printHistory(history.getHistory()));
        System.out.println();
    }

    public static String printHistory(Collection<Task> history) {
        String out = "";
        for (Task t : history){
            out += t.getId() + " ";
        }
        return out;
    }
}
