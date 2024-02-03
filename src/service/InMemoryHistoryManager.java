package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> historyTasks = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistoryTasks() {
        return historyTasks;
    }

    @Override
    public void add(Task task) {
        historyTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("\nИстория просмотров задач: \n" + historyTasks);
        return historyTasks;
    }

    @Override
    public void historyCleaner(Task task) {
        int MAX_SIZE = 10;
        if (historyTasks.size() < MAX_SIZE) {
            historyTasks.add(task);
        } else {
            historyTasks.remove(0);
            historyTasks.add(task);
        }
    }
}
