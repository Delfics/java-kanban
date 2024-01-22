import java.util.HashMap;

public class Epic {
    private final int id;
    private String name;
    private Status status;
    private String description;
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public Epic(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = TaskManager.nextId();
        this.status = Status.NEW;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void removeSubTasks() {
        this.subTasks = new HashMap<>();
    }

    public void addSubTask(SubTask subTask) {
        this.subTasks.put(subTask.getId(), subTask);
    }

    public void calculateStatus(Status status) {
        if (status.equals(Status.IN_PROGRESS)) {
            if (this.status.equals(Status.NEW)) {
                this.status = Status.IN_PROGRESS;
            }
        } else if (status.equals(Status.DONE)) {
            for (SubTask subTask : subTasks.values()) {
                if (subTask.getStatus().equals(Status.IN_PROGRESS)
                        || subTask.getStatus().equals(Status.NEW)) {
                    return;
                }
            }
            this.status = Status.DONE;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public HashMap<Integer, SubTask> getSubTasksListInEpic() {
        return subTasks;
    }

    @Override
    public String toString() {
        return "Идентификатор " + "\nТип: Эпик " + "\nСтатус: "
                + status + "\nНазвание: " + name
                + "\nСписок подзадач в эпике: \n" + subTasks;
    }

}
