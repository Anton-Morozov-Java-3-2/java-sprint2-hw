package manager;

class Node<T> {
    public Node<T> prev;
    public Node<T> next;
    public T task;

    public Node(Node<T> prev, T task, Node<T> next){
        this.prev = null;
        this.task = task;
        this.next = null;
    }
}
