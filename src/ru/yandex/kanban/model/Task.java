package ru.yandex.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private final int id;
    private String name;
    private Status status;
    private String description;
    private Duration duration;
    private LocalDateTime startTime;


    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.status = Status.NEW;
        this.description = description;
    }

    public Task(int id, String name, String description, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.status = Status.NEW;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null | duration == null) {
            return null;
        }
        return startTime.plusMinutes(duration.toMinutes());
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
        return "Идентификатор " + id + "\nТип: Задача " + "\nСтатус: " + status + "\nНазвание: " + name + "\n"
                + description + "\n" + "Дата: " + startTime + "\nПродолжительность задачи: " + duration;

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
        return (id == otherTask.getId()) &&
                Objects.equals(name, otherTask.name) &&
                Objects.equals(status, otherTask.status) &&
                Objects.equals(description, otherTask.description) &&
                Objects.equals(startTime, otherTask.startTime) &&
                Objects.equals(duration, otherTask.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, description);
    }

    public TasksType getTaskType() {
        return TasksType.TASK;
    }

    public Epic getEpic() {
        return null;
    }

    @Override
    public int compareTo(Task t) {
        return startTime.compareTo(t.getStartTime());
    }
}
