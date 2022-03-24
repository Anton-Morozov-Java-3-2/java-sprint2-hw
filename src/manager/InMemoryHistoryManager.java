package manager;

import task.Task;


import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class InMemoryHistoryManager implements HistoryManager{
    public static final int MAX_TASKS = 10;
    private final Map<Long, Node<Task>> idTasks;

    private Node<Task> head;
    private Node<Task> tail;
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
            remove(getFirst().task.getId());
        }
    }

    @Override
    public void remove(long id) {
        removeNode(idTasks.get(id));
        idTasks.remove(id);
    }

    @Override
    public List<Task> getHistory(){
        return getTasks();
    }

    private Node<Task> linkLast(Task t){
        final Node<Task> oldNode = tail;
        tail = new Node<Task>(oldNode, t, null);
        if (oldNode == null) {
            head = tail;
        } else {
            tail.prev = oldNode;
            oldNode.next = tail;
        }
        size++;
        return tail;
    }

    private List<Task> getTasks() {
        List <Task> tasks = new ArrayList<>();
        Node<Task> node = head;
        while ( node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != head && node !=tail) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        } else if (node == head && size > 1) {
            head = head.next;
            head.prev = null;
        } else if (node == tail && size > 1){
            tail = tail.prev;
            tail.next = null;
        } else {
            head = null;
            tail = null;
        }
        --size;
    }

    private void removeNode1(Node<Task> node) {
        if (head == node && tail == node) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = head.next;
            head.prev = null;
        } else if (node == tail) {
            tail = tail.prev;
            tail.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        --size;
    }

    private Node<Task> getFirst() {
        return head;
    }

    public int size() {
        return size;
    }
}



