package model;

import java.util.Objects;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(int id, String name, String description, int epicId) {
        super(id, name, description);
        this.epicId = epicId;

    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Идентификатор " + super.getId() + "\nТип: Подзадача " + "\nСтатус: " + super.getStatus()
                + "\nНазвание: " + super.getName() + "\n" + super.getDescription() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null) {
            return false;
        } else if (this.getClass() != o.getClass()) {
            return false;
        } else if (!super.equals(o)) {
            return false;
        }
        SubTask otherSubtask = (SubTask) o;
        return (epicId == otherSubtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicId);
    }


    public TasksType getSubTaskType() {
        return TasksType.SUBTASK;
    }
}
