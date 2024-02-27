package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    static Map<Integer, Node> nodeMap = new HashMap<>();

    Node first;
    Node last;

    private final ArrayList<Task> historyTasks = new ArrayList<>();

    static class Node {
        Task task;
        Node next;
        Node prev;

        Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public void add(Task task) {
        Node taskNode = nodeMap.get(task.getId());
        if (taskNode == null) {
            linkLast(task);
        } else {
            removeNode(taskNode);
            linkLast(task);
        }
    }

    public void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
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
        List<Task> historyTasks = new ArrayList<>();
        for (Node node : nodeMap.values()) {
            historyTasks.add(node.task);
        }
        return historyTasks;
    }

    public void removeNode(Node node) {
        nodeMap.remove(node);
    }

    @Override
    public void remove(int id) {
        Node taskNode = nodeMap.get(id);
        historyTasks.remove(taskNode);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyTasks = new ArrayList<>();
        System.out.println("История просмотров задач");
        for (Node node : nodeMap.values()) {
            historyTasks.add(node.task);
        }
        return historyTasks;
    }
}
