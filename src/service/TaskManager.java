package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    HashMap<Integer, SubTask> getAllSubTasks();

    HashMap<Integer, Task> getAllTasks();

    HashMap<Integer, Epic> getAllEpics();

    Task createTask(String name, String description);

    Task updateTask(Task task);

    Task getTaskById(int id);

    void removeTaskById(int id);

    void removeAllTasks();

    SubTask createSubTask(String name, String description, int epicId);

    SubTask updateSubTask(SubTask subTask);

    void removeSubTaskById(int id);

    SubTask getSubTaskById(int id);

    void removeAllSubTasks();

    List<SubTask> getSubTasksByEpicId(int epicId);

    Epic createEpic(String name, String description);

    Epic updateEpic(Epic epic);

    Epic getEpicById(int id);

    void removeEpicById(int id);

    void removeAllEpics();

    void showAllEpics();

    void showAllTasks();

    void showAllSubtasks();

    void calculateStatus(Status status, int epicId);
}
