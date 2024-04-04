package ru.yandex.kanban.service;

import ru.yandex.kanban.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>();

    private final HistoryManager manager = Managers.getDefaultHistory();

    protected int sequence = 1;

    protected int nextId() {
        return sequence++;
    }

    public TreeSet<Task> getPrioritizedTasks() {
        for (Task task : tasks.values()) {
            if (task.getStartTime() != null && !task.getStatus().equals(Status.DONE)) {
                prioritizedTasks.add(task);
            }
        }

        for (SubTask subTask : subTasks.values()) {
            if (subTask.getStartTime() != null && !subTask.getStatus().equals(Status.DONE)) {
                prioritizedTasks.add(subTask);
            }
        }
        return prioritizedTasks;
    }

    public HistoryManager getInMemoryHistoryManager() {
        return manager;
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
        return duplicateTask(task);
    }

    @Override
    public Task createTask(String name, String description, LocalDateTime startTime, Duration duration) {
        Task task = new Task(nextId(), name, description, startTime, duration);
        Task task1 = calculateTimeTask(task);
        if (task1.getStartTime() == null) {
            return duplicateTask(task1);
        } else {
            tasks.put(task.getId(), task);
            return duplicateTask(task);
        }
    }

    @Override
    public Task updateTask(Task task) {
        if (task.getStartTime() == null) {
            tasks.put(task.getId(), task);
            return duplicateTask(task);
        } else {
            return calculateTimeTask(task);
        }
    }

    @Override
    public Task getTaskById(int id) {
        manager.add(tasks.get(id));
        return duplicateTask(tasks.get(id));
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
        return duplicateSubTask(subTask);
    }

    @Override
    public SubTask createSubTask(String name, String description, int epicId, LocalDateTime startTime,
                                 Duration duration) {
        SubTask subTask = new SubTask(nextId(), name, description, epicId);
        epics.get(epicId).addSubTaskId(subTask.getId());
        calculateStatus(subTask.getStatus(), epicId);
        SubTask subTask1 = (SubTask) calculateTimeTask(subTask);
        if (subTask.getStartTime() == null) {
            return duplicateSubTask(subTask1);
        } else {
            subTasks.put(subTask.getId(), subTask);
            return duplicateSubTask(subTask);
        }
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        calculateStatus(subTask.getStatus(), subTask.getEpicId());
        if (subTask.getStartTime() == null) {
            subTasks.put(subTask.getId(), subTask);
            return duplicateSubTask(subTask);
        } else {
            return duplicateSubTask((SubTask) calculateTimeTask(subTask));
        }
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
        manager.add(subTasks.get(id));
        return duplicateSubTask(subTasks.get(id));
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
        return duplicateEpic(epic);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getId());
        if (currentEpic.getStatus().equals(epic.getStatus())) {
            epic = calculateTimeEpic(epic);
            epics.put(epic.getId(), epic);
            return duplicateEpic(epic);
        }
        System.out.println("У Эпика статус изменять нельзя");
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        manager.add(epics.get(id));
        return duplicateEpic(epics.get(id));
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

    public Epic calculateTimeEpic(Epic epic) {
        Duration time;
        LocalDateTime nTime = null;
        LocalDateTime eTime = null;
        List<Integer> subTasksListInEpic = epic.getSubTasksListInEpic();
        if (subTasksListInEpic.isEmpty()) {
            return epic;
        }
        for (Integer subtask : subTasksListInEpic) {
            SubTask subTaskById = getSubTaskById(subtask);
            LocalDateTime startTime = subTaskById.getStartTime();
            LocalDateTime endTime = subTaskById.getEndTime();
            if (startTime == null && endTime == null) {
                continue;
            } else if (subTaskById.getStatus().equals(Status.DONE)) {
                continue;
            }
            if (nTime == null) {
                nTime = startTime;
                epic.setStartTime(nTime);
            } else if (startTime.isBefore(nTime)) {
                nTime = startTime;
                epic.setStartTime(nTime);
            }
            if (eTime == null) {
                eTime = endTime;
                epic.setEndTime(eTime);
            } else if (endTime.isAfter(eTime)) {
                eTime = endTime;
                epic.setEndTime(eTime);
            }
        }
        if (nTime == null || eTime == null) {
            time = null;
        } else {
            time = Duration.between(nTime, eTime);
        }
        epic.setDuration(time);
        return epic;
    }

    public boolean checkDataTime(Task task1, Task task2) {
        return task1.getEndTime().isBefore(task2.getStartTime());
    }

    private Task calculateTimeTask(Task task) {
        if (prioritizedTasks.isEmpty()) {
            if (task.getTaskType().equals(TasksType.TASK)) {
                tasks.put(task.getId(), task);
            } else if (task.getTaskType().equals(TasksType.SUBTASK)) {
                subTasks.put(task.getId(), (SubTask) task);
            }
            prioritizedTasks.add(task);
            return task;
        }
        TreeSet<Task> newPrioritizedTasks = prioritizedTasks.stream()
                .filter((task1) -> checkDataTime(task1, task))
                .collect(Collectors.toCollection(TreeSet::new));
        if (prioritizedTasks.equals(newPrioritizedTasks)) {
            if (task.getTaskType().equals(TasksType.TASK)) {
                tasks.put(task.getId(), task);
            } else if (task.getTaskType().equals(TasksType.SUBTASK)) {
                subTasks.put(task.getId(), (SubTask) task);
            }
        } else {
            task.setStartTime(null);
            task.setDuration(null);
            System.out.println(task.getName() + " " + task.getId() + "\nНе может быть добавлена из-за пересечения" +
                    " времени с другими задачами");
        }
        return task;
    }

    private Task duplicateTask(Task task) {
        Task cloneTask = new Task(task.getId(), task.getName(), task.getDescription());
        cloneTask.setStatus(task.getStatus());
        cloneTask.setStartTime(task.getStartTime());
        cloneTask.setDuration(task.getDuration());
        return cloneTask;
    }

    private SubTask duplicateSubTask(SubTask subTask) {
        SubTask cloneSubTask = new SubTask(subTask.getId(), subTask.getName(), subTask.getDescription(),
                subTask.getEpicId());
        cloneSubTask.setStatus(subTask.getStatus());
        cloneSubTask.setStartTime(subTask.getStartTime());
        cloneSubTask.setDuration(subTask.getDuration());
        return cloneSubTask;
    }

    private Epic duplicateEpic(Epic epic) {
        Epic cloneEpic = new Epic(epic.getId(), epic.getName(), epic.getDescription());
        for (Integer ids : epic.getSubTasksListInEpic()) {
            cloneEpic.addSubTaskId(ids);
        }
        cloneEpic.setStatus(epic.getStatus());
        cloneEpic.setStartTime(epic.getStartTime());
        cloneEpic.setEndTime(epic.getEndTime());
        cloneEpic.setDuration(epic.getDuration());
        return cloneEpic;
    }
}
