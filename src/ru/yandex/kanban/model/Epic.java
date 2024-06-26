package ru.yandex.kanban.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subTaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void removeSubTasks() {
        this.subTaskIds = new ArrayList<>();
    }

    public void addSubTaskId(int id) {
        this.subTaskIds.add(id);
    }

    public List<Integer> getSubTasksListInEpic() {
        return subTaskIds;
    }

    @Override
    public String toString() {
        return "Идентификатор " + super.getId() + "\nТип: Эпик " + "\nСтатус: "
                + super.getStatus() + "\nНазвание: " + super.getName() + "\nId подзадач в эпике: \n" + subTaskIds
                + "\nДата начала: " + super.getStartTime() + "\nДата окончания:" + super.getEndTime() +
                "\nПродолжительность задачи: " + super.getDuration();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        } else if (!super.equals(o)) {
            return false;
        }
        Epic otherEpic = (Epic) o;
        return Objects.equals(subTaskIds, otherEpic.subTaskIds);// сравнить все поля эпика
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIds);
    }

    @Override
    public TasksType getTaskType() {
        return TasksType.EPIC;
    }
}
