package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    private static int sequence = 1;

    public static int nextId() {
        return sequence++;
    }

    public HashMap<Integer, SubTask> getAllSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public Task createTask(String name, String description) {
        Task task = new Task(nextId(), name, description);
        tasks.put(task.getId(), task);
        return task;
    }

    public Task updateTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeAllTasks() {
        tasks = new HashMap<>();
    }

    public SubTask createSubTask(String name, String description, int epicId) {
        SubTask subTask = new SubTask(nextId(), name, description, epicId);
        subTasks.put(subTask.getId(), subTask);
        epics.get(epicId).addSubTaskId(subTask.getId());
        calculateStatus(subTask.getStatus(), epicId);
        return subTask;
    }

    public SubTask updateSubTask(SubTask subTask) {
        calculateStatus(subTask.getStatus(), subTask.getEpicId());
        SubTask updated = subTasks.put(subTask.getId(), subTask);
        return updated;
    }

    public void removeSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubTasksListInEpic().remove(subTask.getId());
        subTasks.remove(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public void removeAllSubTasks() {
        subTasks = new HashMap<>();
    }

    public List<SubTask> getSubTasksByEpicId(int epicId) {
        List<SubTask> subTasksNew = new ArrayList<>();
        List<Integer> subTasksListInEpic = epics.get(epicId).getSubTasksListInEpic();
        for (int subTaskId : subTasksListInEpic) {
            SubTask subTask = subTasks.get(subTaskId);
            subTasksNew.add(subTask);
        }
        return subTasksNew;
    }

    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(nextId(), name, description);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getId());
        if (currentEpic.getStatus().equals(epic.getStatus())) {
            epics.put(epic.getId(), epic);
            return;
        }
        System.out.println("У Эпика статус изменять нельзя");
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void removeEpicById(int id) {
        epics.remove(id);
    }

    public void removeAllEpics() {
        epics = new HashMap<>();
    }

    public void showAllEpics() {
        for (Epic epic : epics.values()) {
            System.out.println(epic);
            System.out.println(getSubTasksByEpicId(epic.getId()));
        }
    }

    public void showAllTasks() {
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
    }

    public void showAllSubtasks() {
        for (SubTask subTask : subTasks.values()) {
            System.out.println(subTask);
        }
    }

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
}
