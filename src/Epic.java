import java.util.ArrayList;

public class Epic {
    private int id ;
    private String name;
    private Status status;
    private String description;
    private ArrayList<SubTask> subTasksListInEpic = new ArrayList<>();

    public Epic () {

    }
    public Epic (String name, String description) {
        this.name = name;
        this.description = description;
        this.id = TaskManager.nextId();
        this.status = Status.NEW;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ArrayList<SubTask> getSubTasksListInEpic() {
        return subTasksListInEpic;
    }

    public void setSubTasksInEpic(ArrayList<SubTask> subTasksListInEpic) {
        this.subTasksListInEpic = subTasksListInEpic;
    }

    @Override
    public String toString() {
        return  "Идентификатор " + id + "\nТип: Эпик " + "\nСтатус: "
                + status + "\nНазвание: " + name
                + "\nСписок подзадач: " + subTasksListInEpic;
    }

}
