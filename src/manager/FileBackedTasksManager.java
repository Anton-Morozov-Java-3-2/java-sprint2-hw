package manager;
import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class FileBackedTasksManager extends InMemoryTaskManager{

    private static final String header = "id,type,name,status,description,epic\n";
    private final File file;
    private final HistoryManager history;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
        this.history = Managers.getHistory();
    }

    public static FileBackedTasksManager loadFromFile(File file) throws IOException {

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
            throw new IOException("Ошибка чтения фала");
        }
    }

    public Collection<Task> getHistory() {
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
                super.createTask(e);
                id = e.getId();
            } else if (data[i].indexOf(Type.SUBTASK.toString()) > 0) {
                Subtask s = Subtask.fromString(data[i]);
                super.createSubtask(s);
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
                Collection<Long> id = InMemoryHistoryManager.fromString(s);
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
        for (Epic e : super.getAllEpics()) {
            for (Subtask s : super.getAllSubtasks()) {
                if (s.getIdEpic() == e.getId()) {
                    e.addSubtask(s.getId());
                }
            }
        }
    }

    public Collection<String> getTaskInString(){
        List<String> str = new ArrayList<>();
        for (Task t : super.getAllTasks()) {
            str.add(t.toString());
        }
        for (Subtask s : super.getAllSubtasks()) {
            str.add(s.toString());
        }
        for (Epic e : super.getAllEpics()) {
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
        history.add(t);
        save();
        return t;
    }

    @Override
    public Subtask getSubtask(Long id) {
        Subtask s = super.getSubtask(id);
        history.add(s);
        save();
        return s;
    }

    @Override
    public Epic getEpic(Long id) {
        Epic e = super.getEpic(id);
        history.add(e);
        save();
        return e;
    }

    @Override
    public Collection<Task> getAllTasks() {
        Collection<Task> tasks = super.getAllTasks();
        for (Task t : tasks) {
            history.add(t);
        }
        save();
        return tasks;
    }

    @Override
    public Collection<Subtask> getAllSubtasks() {
        Collection<Subtask> subtasks = super.getAllSubtasks();
        for (Subtask s : subtasks) {
            history.add(s);
        }
        save();
        return subtasks;
    }

    @Override
    public Collection<Epic> getAllEpics() {
        Collection<Epic> epics = super.getAllEpics();
        for (Epic e : epics) {
            history.add(e);
        }
        save();
        return epics;
    }

    @Override
    public void removeAllTasks() {
        Collection<Task> tasks = super.getAllTasks();
        for (Task t : tasks) {
            history.remove(t.getId());
        }
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        Collection<Subtask> subtasks = super.getAllSubtasks();
        for (Subtask s : subtasks) {
            history.remove(s.getId());
        }
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        Collection<Epic> epics = super.getAllEpics();
        for (Task e : epics) {
            history.remove(e.getId());
        }
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
        history.remove(id);
        save();
    }

    @Override
    public void removeSubtask(Long id){
        super.removeSubtask(id);
        history.remove(id);
        save();
    }

    @Override
    public void removeEpic(Long id) {
        super.removeEpic(id);
        history.remove(id);
        save();
    }

    public static void main(String[] args) {

        File file = new File("file.csv");

        FileBackedTasksManager old = new FileBackedTasksManager(file);

        Task task1 = new Task("Задача 1", "Тест", old.getId(), Status.NEW);
        Task task2 = new Task("Задача 2", "Тест", old.getId(), Status.NEW);

        old.createTask(task1);
        old.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Тест", old.getId(), Status.NEW, new ArrayList<Long>());
        Subtask subtask1 = new Subtask("Подзадача 1", "Тест", old.getId(), Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Тест", old.getId(), Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Тест", old.getId(), Status.NEW, epic1.getId());

        epic1.addSubtask(subtask1.getId());
        epic1.addSubtask(subtask2.getId());
        epic1.addSubtask(subtask3.getId());

        old.createSubtask(subtask1);
        old.createSubtask(subtask2);
        old.createSubtask(subtask3);

        old.createEpic(epic1);

        Epic epic2 = new Epic("Эпик 2", "Тест", old.getId(), Status.NEW, new ArrayList<Long>());
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


        } catch (IOException e) {
            System.out.println("Ошибка при загрузке");
        }
    }
}
