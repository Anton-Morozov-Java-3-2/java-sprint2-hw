package manager;
import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class FileBackedTasksManager extends InMemoryTaskManager{

    private static final String header = "id,type,name,status,description,epic\n";
    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            FileBackedTasksManager manager = new FileBackedTasksManager(file);

            if (in.available() > 0) {
                String s = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                String[] data = s.split("\n");
                manager.initializationTaskManager(data);
                manager.initializationHistoryManager(data);
            }
            return manager;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения фала");
        }
    }

    public List<Task> getHistory() {
        return history.getHistory();
    }

    private void save()  {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            out.write(header.getBytes(StandardCharsets.UTF_8));
            for (String s: getTaskInString()) {
                out.write(s.getBytes(StandardCharsets.UTF_8));
            }
            out.write(" \n".getBytes(StandardCharsets.UTF_8));
            out.write(InMemoryHistoryManager.toString(history).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private void initializationIdCounter(Long max_id) {
        if (max_id > 0) {
            while (getId() != max_id) ;
        }
    }

    private void initializationTaskManager(final String[] data) {
        long max_id = 0L;
        for (int i = 1; !data[i].isBlank(); i++) {

            long id = 0L;
            if (data[i].indexOf(Type.EPIC.toString()) > 0) {
                Epic e = Epic.fromString(data[i]);
                super.createEpic(e, e.getId());
                id = e.getId();
            } else if (data[i].indexOf(Type.SUBTASK.toString()) > 0) {
                Subtask s = Subtask.fromString(data[i]);
                super.createSubtask(s, s.getId());
                id = s.getId();
            } else {
                Task t = Task.fromString(data[i]);
                super.createTask(t);
                id = t.getId();
            }

            if (max_id < id) {
                max_id = id;
            }
        }

        initializationIdCounter(max_id);
        initializationEpics();
    }

    private void initializationHistoryManager(final String[] data) {
        if (data != null) {
            String s = data[data.length - 1];
            if (!s.isEmpty() && !s.isBlank()) {
                List<Long> id = InMemoryHistoryManager.fromString(s);
                for (Long l : id) {
                    if (super.getTask(l) != null) {
                        history.add(super.getTask(l));
                    } else if (super.getSubtask(l) != null) {
                        history.add(super.getSubtask(l));
                    } else {
                        history.add(super.getEpic(l));
                    }

                }
            }
        }
    }

    private void initializationEpics() {
        for (Epic e : getAllEpics()) {
            for (Subtask s : getAllSubtasks()) {
                if (s.getIdEpic() == e.getId()) {
                    e.addSubtask(s.getId());
                }
            }
        }
    }

    public Collection<String> getTaskInString(){
        List<String> str = new ArrayList<>();
        for (Task t : getAllTasks()) {
            str.add(t.toString());
        }
        for (Subtask s : getAllSubtasks()) {
            str.add(s.toString());
        }
        for (Epic e : getAllEpics()) {
            str.add(e.toString());
        }
        sortTaskStringById(str);
        return str;
    }

    private void sortTaskStringById(List<String> strings) {
        Comparator<String> comp = new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return (int) (Long.parseLong(lhs.split(",")[0]) - Long.parseLong(rhs.split(",")[0]));
            }
        };
        if (strings.size() > 1) strings.sort(comp);
    }

    @Override
    public Task getTask(Long id) {
        Task t = super.getTask(id);
        save();
        return t;
    }

    @Override
    public Subtask getSubtask(Long id) {
        Subtask s = super.getSubtask(id);
        save();
        return s;
    }

    @Override
    public Epic getEpic(Long id) {
        Epic e = super.getEpic(id);
        save();
        return e;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic){
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTask(Long id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubtask(Long id){
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeEpic(Long id) {
        super.removeEpic(id);
        save();
    }

    public static void main(String[] args) {

        File file = new File("file.csv");

        FileBackedTasksManager old = new FileBackedTasksManager(file);

        Task task1 = new Task("Задача 1", "Тест", Status.NEW);
        Task task2 = new Task("Задача 2", "Тест", Status.NEW);

        old.createTask(task1);
        old.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Тест");
        Subtask subtask1 = new Subtask("Подзадача 1", "Тест", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Тест", Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Тест", Status.NEW, epic1.getId());

        old.createEpic(epic1);

        old.createSubtask(subtask1);
        old.createSubtask(subtask2);
        old.createSubtask(subtask3);

        Epic epic2 = new Epic("Эпик 2", "Тест");
        old.createEpic(epic2);

        old.getTask(task1.getId());
        old.getTask(task2.getId());

        old.getSubtask(subtask1.getId());
        old.getEpic(epic1.getId());
        old.getEpic(epic2.getId());

        try {
            FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(file);

            String old_data = String.join(",",old.getTaskInString());
            String new_data = String.join(",", newManager.getTaskInString());

            if (old_data.equals(new_data)) {
                System.out.println("Тест 1 пройден успешно");
            } else {
                System.out.println("Тест 1 провален!");
            }

            InMemoryHistoryManager.toString(old.history);

            String old_history_data = InMemoryHistoryManager.toString(old.history);;
            String new_history_data = InMemoryHistoryManager.toString(newManager.history);;

            if (old_history_data.equals(new_history_data)) {
                System.out.println("Тест 2 пройден успешно");
            } else {
                System.out.println("Тест 2 провален!");
                System.out.println(old_history_data);
                System.out.println(new_history_data);
            }


        } catch (RuntimeException e) {
            System.out.println("Ошибка при загрузке");
        }
    }
}
