package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    private final HistoryManager manager = Managers.getDefaultHistory();

    private static final int MAX_SIZE = 10;

    private static int sequence = 1;

    private int nextId() {
        return sequence++;
    }

    @Override
    public HashMap<Integer, SubTask> getAllSubTasks() {
        return subTasks;
    }

    @Override
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    @Override
    public Task createTask(String name, String description) {
        Task task = new Task(nextId(), name, description);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task getTaskById(int id) {
        historyCleaner(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeAllTasks() {
        tasks = new HashMap<>();
    }

    @Override
    public SubTask createSubTask(String name, String description, int epicId) {
        SubTask subTask = new SubTask(nextId(), name, description, epicId);
        subTasks.put(subTask.getId(), subTask);
        epics.get(epicId).addSubTaskId(subTask.getId());
        calculateStatus(subTask.getStatus(), epicId);
        return subTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        calculateStatus(subTask.getStatus(), subTask.getEpicId());
        subTasks.put(subTask.getId(), subTask);
        return subTask;
    }

    @Override
    public void removeSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubTasksListInEpic().remove(subTask.getId());
        subTasks.remove(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        historyCleaner(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public void removeAllSubTasks() {
        subTasks = new HashMap<>();
    }

    @Override
    public List<SubTask> getSubTasksByEpicId(int epicId) {
        List<SubTask> subTasksNew = new ArrayList<>();
        List<Integer> subTasksListInEpic = epics.get(epicId).getSubTasksListInEpic();
        for (int subTaskId : subTasksListInEpic) {
            SubTask subTask = subTasks.get(subTaskId);
            subTasksNew.add(subTask);
        }
        return subTasksNew;
    }

    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(nextId(), name, description);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getId());
        if (currentEpic.getStatus().equals(epic.getStatus())) {
            epics.put(epic.getId(), epic);
            return epic;
        }
        System.out.println("У Эпика статус изменять нельзя");
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        historyCleaner(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void removeEpicById(int id) {
        epics.remove(id);
    }

    @Override
    public void removeAllEpics() {
        epics = new HashMap<>();
    }

    @Override
    public void showAllEpics() {
        for (Epic epic : epics.values()) {
            System.out.println(epic);
            System.out.println(getSubTasksByEpicId(epic.getId()));
        }
    }

    @Override
    public void showAllTasks() {
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
    }

    @Override
    public void showAllSubtasks() {
        for (SubTask subTask : subTasks.values()) {
            System.out.println(subTask);
        }
    }

    @Override
    public void calculateStatus(Status status, int epicId) {
        Epic epic = epics.get(epicId);
        if (status.equals(Status.IN_PROGRESS)) {
            if (epic.getStatus().equals(Status.NEW)) {
                epic.setStatus(Status.IN_PROGRESS);
            }
        } else if (status.equals(Status.DONE)) {
            List<SubTask> subTasksByEpicId = getSubTasksByEpicId(epic.getId());
            for (SubTask subTask : subTasksByEpicId) {
                if (subTask.getStatus().equals(Status.IN_PROGRESS)
                        || subTask.getStatus().equals(Status.NEW)) {
                    return;
                }
            }
            epic.setStatus(Status.DONE);
        }
    }

    @Override
    public List<Task> getHistory() {
        return manager.getHistory();
    }

    private void historyCleaner(Task task) {
        if (manager.getHistory().size() < MAX_SIZE) {
            manager.add(task);
        } else {
            manager.getHistory().remove(0);
            manager.add(task);
        }
    }
}
