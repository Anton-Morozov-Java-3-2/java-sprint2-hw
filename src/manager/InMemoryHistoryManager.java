package manager;

import task.Task;
import task.Epic;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

public class InMemoryHistoryManager implements HistoryManager{
    public static final int MAX_TASKS = 10;
    private final Map<Long, Node> idTasks;

    private Node head;
    private Node tail;
    private int size;

    public InMemoryHistoryManager() {
        idTasks = new HashMap<>();

        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (idTasks.containsKey(task.getId())) {
                remove(task.getId());
            }
            idTasks.put(
                    task.getId(),
                    linkLast(task));
        }

        if (size() > MAX_TASKS) {
            remove(getFirst().getTask().getId());
        }
    }

    @Override
    public void remove(long id) {

        Task t = idTasks.get(id).getTask();
        if (t instanceof Epic) {
            Epic e = (Epic) t;
            for (Long i : e.getSubtasks()) {
                removeNode(idTasks.get(i));
                idTasks.remove(i);
            }
        }
        removeNode(idTasks.get(id));
        idTasks.remove(id);
    }

    @Override
    public Collection<Task> getHistory(){
        return getTasks();
    }

    private Node linkLast(Task t){
        final Node oldNode = tail;
        tail = new Node(oldNode, t, null);
        if (oldNode == null) {
            head = tail;
        } else {
            tail.setPrev(oldNode);
            oldNode.setNext(tail);
        }
        size++;
        return tail;
    }

    private Collection<Task> getTasks() {
        Collection <Task> tasks = new ArrayList<>();
        Node node = head;
        while ( node != null) {
            tasks.add(node.getTask());
            node = node.getNext();
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (head == node && tail == node) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = head.getNext();
            head.setPrev(null);
        } else if (node == tail) {
            tail = tail.getPrev();
            tail.setNext(null);
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
        --size;
    }

    private Node getFirst() {
        return head;
    }

    public int size() {
        return size;
    }
}



