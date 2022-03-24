import manager.HistoryManager;
import manager.Managers;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import manager.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        HistoryManager history = Managers.getHistory();

        System.out.println("Начальное состояние объектов");
        System.out.println("");

        Task invite = new Task("Пригласить друзей", "Согласовать удобное время для всех",
                manager.getId(), Status.NEW);

        Task cake = new Task("Купить торт", "Заказать в кондитерской с доставкой",
                manager.getId(), Status.NEW);

        Epic tea = new Epic("Вскипятить чайник", "Чайник кипятит программист", manager.getId(),
                Status.NEW, new ArrayList<Long>());
        Subtask init = new Subtask("Проинициализировать чайник", "Вылить воду из чайника и налить полный",
                manager.getId(), Status.NEW, tea.getId());
        Subtask boil = new Subtask("Вскипятить чайник",
                "Поставить на плиту, зажечь газ и нагреть до кипения", manager.getId(), Status.NEW, tea.getId());
        tea.addSubtask(init.getId());
        tea.addSubtask(boil.getId());

        Epic coffee = new Epic("Сделать кофе", "Кофе в турке", manager.getId(), Status.NEW,
                new ArrayList<Long>());
        Subtask grind = new Subtask("Помолоть кофе", "Помолоть кофе в пыль", manager.getId(), Status.NEW,
                coffee.getId());
        coffee.addSubtask(grind.getId());

        manager.createTask(invite);
        manager.createTask(cake);

        manager.createEpic(tea);
        manager.createSubtask(init);
        manager.createSubtask(boil);

        manager.createEpic(coffee);
        manager.createSubtask(grind);

        for (Task t: manager.getAllTasks()){
            history.add(t);
            System.out.println(t);
        }

        for (Subtask s: manager.getAllSubtasks()){
            history.add(s);
            System.out.println(s);
        }

        for (Epic e: manager.getAllEpics()){
            history.add(e);
            System.out.println(e);
        }

        System.out.println("История просмотров: ");
        System.out.println(printHistory(history.getHistory()));
        System.out.println("");
        System.out.println("");

        System.out.println("После изменения статуса задач");
        System.out.println("");

        invite = new Task("Пригласить друзей", "Согласовать удобное время для всех", invite.getId(),
                Status.IN_PROGRESS);
        cake = new Task("Купить торт", "Заказать в кондитерской с доставкой", cake.getId(), Status.DONE);
        manager.updateTask(invite);
        manager.updateTask(cake);

        for (Task t: manager.getAllTasks()){
            history.add(t);
            System.out.println(t);
        }

        System.out.println("");
        System.out.println("После изменения статуса подзадач Epic tea");
        System.out.println("");

        init = new Subtask("Проинициализировать чайник", "Вылить воду из чайника и налить полный",
                init.getId(), Status.DONE, init.getIdEpic());
        boil = new Subtask("Вскипятить чайник",
                "Поставить на плиту, зажечь газ и нагреть до кипения", boil.getId(), Status.IN_PROGRESS,
                boil.getIdEpic());

        manager.updateSubtask(init);
        manager.updateSubtask(boil);

        System.out.println(manager.getSubtask(init.getId()));
        history.add(manager.getSubtask(init.getId()));
        System.out.println(manager.getSubtask(boil.getId()));
        history.add(manager.getSubtask(boil.getId()));
        System.out.println(manager.getEpic(init.getIdEpic()));
        history.add(manager.getEpic(init.getIdEpic()));

        System.out.println("История просмотров: ");
        System.out.println(printHistory(history.getHistory()));
        System.out.println("");

        System.out.println("");
        System.out.println("После изменения статуса подзадач в Epic coffee");
        System.out.println("");

        grind = new Subtask("Помолоть кофе", "Помолоть кофе в пыль", grind.getId(), Status.DONE,
                coffee.getId());

        manager.updateSubtask(grind);

        System.out.println(manager.getSubtask(grind.getId()));
        history.add(manager.getSubtask(grind.getId()));

        System.out.println(manager.getEpic(grind.getIdEpic()));
        history.add(manager.getEpic(grind.getIdEpic()));

        System.out.println("История просмотров: ");
        System.out.println(printHistory(history.getHistory()));
        System.out.println("");

        System.out.println("");
        System.out.println("После изменения удаления задачи с тортом");
        System.out.println("");

        manager.removeTask(cake.getId());
        for (Task t: manager.getAllTasks()){
            history.add(t);
            System.out.println(t);
        }

        System.out.println("История просмотров: ");
        System.out.println(printHistory(history.getHistory()));
        System.out.println("");

        System.out.println("");
        System.out.println("После изменения удаления Epic coffee");
        System.out.println("");

        manager.removeEpic(coffee.getId());
        for (Epic e: manager.getAllEpics()){
            history.add(e);
            System.out.println(e);
        }
        for (Subtask s: manager.getAllSubtasks()){
            history.add(s);
            System.out.println(s);
        }

        System.out.println("История просмотров: ");
        System.out.println(printHistory(history.getHistory()));
        System.out.println("");
    }

    public static String printHistory(List<Task> history) {
        String out = "";
        for (Task t : history){
            out += t.getId() + " ";
        }
        return out;
    }
}
