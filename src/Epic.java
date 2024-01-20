import java.util.ArrayList;

public class Epic {
    private String name;
    private Status status;
    private ArrayList<SubTask> description = new ArrayList<>();

    public Epic (String name) {
        this.name = name;
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

    public ArrayList<SubTask> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<SubTask> description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Тип: Эпик " + "\nСтатус: " + status + "\nНазвание: " + name;
    }

}
