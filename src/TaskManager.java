import java.util.HashMap;

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
        Task task = new Task(name, description);
        tasks.put(task.getId(), task);
        return task;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void showTasks() {
        for (Task task : tasks.values()) {
            System.out.println(task.toString());
        }
    }

    public void removeAllTasks() {
        tasks = new HashMap<>();
    }

    public SubTask createSubTask(String name, String description, int epicId) {
        SubTask subTask = new SubTask(name, description, epicId);
        subTasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).addSubTask(subTask);
        epics.get(epicId).calculateStatus(subTask.getStatus());
        return subTask;
    }

    public SubTask updateSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        epic.calculateStatus(subTask.getStatus());
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
        for (Epic epic : epics.values()) {
            epic.removeSubTasks();
        }
    }

    public HashMap<Integer, SubTask> getSubTasksByEpicId(int id) {
        return epics.get(id).getSubTasksListInEpic();
    }

    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void removeEpicById(int id) {
        epics.remove(id);
    }
}
