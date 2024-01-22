public class SubTask {
    private final int id;
    private final int epicId;
    private String name;
    private String content;
    private String description;
    private Status status;

    public SubTask(String name, String description, int epicId) {
        this.name = name;
        this.description = description;
        this.epicId = epicId;
        this.id = TaskManager.nextId();
        this.status = Status.NEW;
    }

    public int getId() {
        return id;
    }

    public int getEpicId() {
        return epicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Идентификатор " + "\nТип: Подзадача " + "\nСтатус: " + status
                + "\nНазвание: " + name + "\n" + description;
    }
}
