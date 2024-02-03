package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {
    ArrayList<Task> getHistoryTasks();

    void historyCleaner(Task task);

    void add(Task task);

    List<Task> getHistory();
}
