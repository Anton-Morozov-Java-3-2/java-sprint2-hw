package com.practikum.tracker.history;

import com.practikum.tracker.model.Task;
import com.practikum.tracker.model.Epic;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private static final int MAX_TASKS = 10;
    private final Map<Long, Node> idTasks;

    private Node head;
    private Node tail;
    private int size;

    public InMemoryHistoryManager() {
        idTasks = new HashMap<>();
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

        if (idTasks.containsKey(id)) {
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
    }

    @Override
    public List<Task> getHistory(){
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

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
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

    public static String toString(HistoryManager manager) {
        if (manager != null) {
            StringBuilder str = new StringBuilder();
            for (Task t : manager.getHistory()) {
                str.append(t.getId());
                str.append(",");
            }
            if (str.length() > 0) str.deleteCharAt(str.length() - 1);
            return str.toString();
        } else {
            return null;
        }
    }

    public static List<Long> fromString(String value) {
        List<Long> history = new ArrayList<>();

        String[] data = value.split(",");
        for (String s : data) {
            history.add(Long.parseLong(s));
        }
        return history;
    }
}



