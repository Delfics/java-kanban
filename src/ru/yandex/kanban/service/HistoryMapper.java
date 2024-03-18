package ru.yandex.kanban.service;

import ru.yandex.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class HistoryMapper {
    public static String historyToString(HistoryManager manager, FileBackedTaskManager fileBackedTaskManager) {
        StringBuilder ids = new StringBuilder();
        for (Task task : manager.getHistory()) {
            ids.append(task.getId()).append(",");
        }
        return ids.toString();
    }

    public static List<Integer> historyFromString(List<String> value) {
        List<Integer> integers = new ArrayList<>();
        String last = value.getLast();
        String[] split = last.split(",");
        for (String ids : split) {
            integers.add(Integer.parseInt(ids));
        }
        return integers;
    }
}
