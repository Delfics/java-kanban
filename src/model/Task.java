package model;

import java.util.Objects;

public class Task {
    private final int id;
    private String name;
    private Status status;
    private String description;

    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.status = Status.NEW;
        this.description = description;
    }

    public int getId() {
        return id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Идентификатор " + id + "\nТип: Задача " + "\nСтатус: " + status
                + "\nНазвание: " + name + "\n" + description + "\n";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null) {
            return false;
        } else if (this.getClass() != o.getClass()) {
            return false;
        }
        Task otherTask = (Task) o;
        return Objects.equals(name, otherTask.name) &&
                Objects.equals(status, otherTask.status) &&
                Objects.equals(description, otherTask.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, description);
    }
}
