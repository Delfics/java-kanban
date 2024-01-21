import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    private static int sequence = 1;

    public static int nextId() {
        return sequence++;
    }
    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    public void setSubTasks(HashMap<Integer, SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public void setEpics(HashMap<Integer, Epic> epics) {
        this.epics = epics;
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task, int id) {
        task.setId(id);
        tasks.put(id, task);
    }

    public Task getTaskId(int id) {
        return tasks.get(id);
    }

    public void removeTaskId(int id) {
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

    public void createSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).getSubTasksListInEpic().add(subTask);
    }

    public void updateSubTask(SubTask subTask, int id) {
        subTasks.put(id, subTask);

    }

    public void removeSubTaskId(int id) {
        subTasks.remove(id);
    }

    public SubTask getSubTaskId(int id) {
        return subTasks.get(id);
    }

    public void removeAllSubTasks() {
        subTasks = new HashMap<>();
    }

    public void showSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            System.out.println(subTask.toString());
        }
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic, int id) {
        epics.put(id,epic);
    }

    public void showEpics() {
        for (Epic epic : epics.values()) {
            System.out.println(epic.toString());
        }
    }

    public Epic getEpicId(int id) {
        return epics.get(id);
    }

    public void calculateStatus(int id) {
        if (epics.get(id).getSubTasksListInEpic().isEmpty() || )
    }
}
