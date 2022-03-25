package manager;

import task.Task;

class Node {
    private Node prev;
    private Node next;
    private Task task;

    public Node(Node prev, Task task, Node next){
        this.prev = null;
        this.task = task;
        this.next = null;
    }

    public Node getPrev() {
        return prev;
    }

    public Node getNext() {
        return next;
    }

    public Task getTask() {
        return task;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
