import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskManager;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        Task invite = new Task("Пригласить друзей", "Согласовать удобное время для всех",
                manager.getId(), Task.NEW);

        Task cake = new Task("Купить торт", "Заказать в кондитерской с доставкой",
                manager.getId(), Task.NEW);

        Epic tea = new Epic("Вскипятить чайник", "Чайник кипятит программист", manager.getId(), Task.NEW,
                    new ArrayList<Integer>());
        Subtask init = new Subtask("Проинициализировать чайник", "Вылить воду из чайника и налить полный",
                manager.getId(), Task.NEW, tea.getId());
        Subtask boil = new Subtask("Вскипятить чайник",
                "Поставить на плиту, зажечь газ и нагреть до кипения", manager.getId(), Task.NEW, tea.getId());
        tea.addSubtask(init.getId());
        tea.addSubtask(boil.getId());

        Epic coffee = new Epic("Сделать кофе", "Кофе в турке", manager.getId(), Task.NEW,
                new ArrayList<Integer>());
        Subtask grind = new Subtask("Помолоть кофе", "Помолоть кофе в пыль", manager.getId(), Task.NEW,
                coffee.getId());
        coffee.addSubtask(grind.getId());

        manager.createTask(invite);
        manager.createTask(cake);

        manager.createEpic(tea);
        manager.createSubtask(init);
        manager.createSubtask(boil);

        manager.createEpic(coffee);
        manager.createSubtask(grind);

        System.out.println("Начальное состояние объектов");
        System.out.println("");

        for (Task t: manager.getAllTasks()){
            System.out.println(t);
        }

        for (Subtask s: manager.getAllSubtasks()){
            System.out.println(s);
        }

        for (Epic e: manager.getAllEpics()){
            System.out.println(e);
        }

        System.out.println("");

        invite = new Task("Пригласить друзей", "Согласовать удобное время для всех", invite.getId(),
                Task.IN_PROGRESS);
        cake = new Task("Купить торт", "Заказать в кондитерской с доставкой", cake.getId(), Task.DONE);
        manager.updateTask(invite);
        manager.updateTask(cake);

        System.out.println("После изменения статуса задач");
        System.out.println("");

        for (Task t: manager.getAllTasks()){
            System.out.println(t);
        }

        System.out.println("");
        System.out.println("После изменения статуса подзадач Epic tea");
        System.out.println("");

        init = new Subtask("Проинициализировать чайник", "Вылить воду из чайника и налить полный",
                init.getId(), Task.DONE, init.getIdEpic());
        boil = new Subtask("Вскипятить чайник",
                "Поставить на плиту, зажечь газ и нагреть до кипения", boil.getId(), Task.IN_PROGRESS,
                boil.getIdEpic());

        manager.updateSubtask(init);
        manager.updateSubtask(boil);

        System.out.println(manager.getSubtaskById(init.getId()));
        System.out.println(manager.getSubtaskById(boil.getId()));
        System.out.println(manager.getEpicById(init.getIdEpic()));

        System.out.println("");
        System.out.println("После изменения статуса подзадач в Epic coffee");
        System.out.println("");

        grind = new Subtask("Помолоть кофе", "Помолоть кофе в пыль", grind.getId(), Task.DONE,
                coffee.getId());

        manager.updateSubtask(grind);
        System.out.println(manager.getSubtaskById(grind.getId()));
        System.out.println(manager.getEpicById(grind.getIdEpic()));

        System.out.println("");
        System.out.println("После изменения удаления задачи с тортом");
        System.out.println("");

        manager.removeTaskById(cake.getId());
        for (Task t: manager.getAllTasks()){
            System.out.println(t);
        }

        System.out.println("");
        System.out.println("После изменения удаления Epic coffee");
        System.out.println("");

        manager.removeEpicById(coffee.getId());
        for (Epic e: manager.getAllEpics()){
            System.out.println(e);
        }
        for (Subtask s: manager.getAllSubtasks()){
            System.out.println(s);
        }
    }
}
