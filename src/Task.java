import java.util.ArrayList;

public class Task {
    private String name;
    private String content;
    private Status status;
    private ArrayList<String> description = new ArrayList<>();

    public Task(String name, String content, Status status) {
        this.name = name;
        this.content = content;
        this.status = status;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Тип: Задача " + "\nСтатус: " + status + "\nНазвание: " + name + "\n" + description;

    }


}
