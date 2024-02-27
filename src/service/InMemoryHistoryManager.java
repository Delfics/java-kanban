package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Node<Task> {
    Task task;
    Node<Task> next;
    Node<Task> prev;

    Node(Node<Task> prev, Task task, Node<Task> next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }
}

public class InMemoryHistoryManager implements HistoryManager {
    static Map<Integer, Node<Task>> nodeMap = new HashMap<>();

    Node<Task> first;
    Node<Task> last;

    private final ArrayList<Task> historyTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
        Node<Task> taskNode = nodeMap.get(task.getId());
        if (taskNode == null) {
            linkLast(task);
        } else {
            removeNode(taskNode);
            linkLast(task);
        }
    }

    public void linkLast(Task task) {
        final Node<Task> l = last;
        final Node<Task> newNode = new Node<>(l, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
            nodeMap.put(task.getId(), first);
        } else {
            l.next = newNode;
            nodeMap.put(task.getId(), l.next);
        }
    }

    public List<Task> getTasks() {
        List <Task> historyTasks = new ArrayList<>();
        for (Node<Task> node : nodeMap.values()) {
            historyTasks.add(node.task);
        }
        return historyTasks;
    }

    public void removeNode(Node<Task> node) {
        nodeMap.remove(node);
    }

    @Override
    public void remove(int id) {
        Node<Task> taskNode = nodeMap.get(id);
        historyTasks.remove(taskNode);
    }

    @Override
    public List<Task> getHistory() {
        List <Task> historyTasks = new ArrayList<>();
        System.out.println("История просмотров задач");
        for (Node<Task> node : nodeMap.values()) {
            historyTasks.add(node.task);
        }
        return historyTasks;
    }
}
