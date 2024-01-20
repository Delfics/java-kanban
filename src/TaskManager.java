import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }
    private static int ID = 1;

    public int nextId() {
        return ID++;
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

    public void addTask(Task task) {
        task.getDescription().add(task.getContent());
        tasks.put(nextId(), task);
    }

    public void updateTask(Task task, int id) {
        task.getDescription().add(task.getContent());
        tasks.put(id, task);
    }

    public Task getTaskId(int id) {
        return tasks.get(id);
    }

    public void removeTaskId(int id) {
        tasks.remove(id);
    }

    public void showTasks() {
        int key;
        for (Task task : tasks.values()) {
            for (int k : tasks.keySet()) {
                if (tasks.get(k).equals(task)) {
                    key = k;
                    System.out.println("Индентифкатор задачи " + key);
                }
            }
            System.out.println(task.toString());
        }
    }

    public void removeAllTasks() {
        tasks = new HashMap<>();
    }

    public void addSubTask(SubTask subTask) {
        subTask.getDescription().add(subTask.getContent());
        subTasks.put(nextId(), subTask);
    }

    public void updateSubTask(SubTask subTask, int id) {
        subTask.getDescription().add(subTask.getContent());
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
        int key;
        for (SubTask subTask : subTasks.values()) {
            for (int k : subTasks.keySet()) {
                if (subTasks.get(k).equals(subTask)) {
                    key = k;
                    System.out.println("Индентифкатор задачи " + key);
                }
            }
            System.out.println(subTask.toString());
        }
    }

    public void addEpic(Epic epic, int id) {
        epic.getDescription().add(subTasks.get(id));
        epic.setStatus(Status.NEW);
        epics.put(nextId(),epic);
    }

    public void showEpic() {
        int key;
        for (Epic epic : epics.values()) {
            for (int k : epics.keySet()) {
                if (epics.get(k).equals(epic)) {
                    key = k;
                    System.out.println("Индентифкатор задачи " + key);
                }
            }
            System.out.println(epic.toString());
        }
    }

    public Epic getEpicId(int id) {
        return epics.get(id);
    }
}
