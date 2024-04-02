package ru.yandex.kanban.service;

import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;


    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    private void calculateNextId(int id) {
        if (id >= sequence) {
            sequence = id + 1;
        }
    }

    @Override
    public Task createTask(String name, String description) {
        Task task = super.createTask(name, description);
        save();
        return task;

    }

    @Override
    public Task updateTask(Task task) {
        Task task1 = super.updateTask(task);
        save();
        return task1;
    }

    @Override
    public Task getTaskById(int id) {
        Task taskById = super.getTaskById(id);
        save();
        return taskById;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public SubTask createSubTask(String name, String description, int epicId) {
        SubTask subTask = super.createSubTask(name, description, epicId);
        save();
        return subTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        SubTask subTask1 = super.updateSubTask(subTask);
        save();
        return subTask1;
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTaskById = super.getSubTaskById(id);
        save();
        return subTaskById;
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }


    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = super.createEpic(name, description);
        save();
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic epic1 = super.updateEpic(epic);
        save();
        return epic1;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epicById = super.getEpicById(id);
        save();
        return epicById;
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic,startTime,duration,\n");
            for (Task task : getAllTasks().values()) {
                writer.write(toString(task));
            }
            for (SubTask subTask : getAllSubTasks().values()) {
                writer.write(toString(subTask));
            }
            for (Epic epic : getAllEpics().values()) {
                writer.write(toString(epic));
            }
            writer.write("\n");
            writer.write(HistoryMapper.historyToString(getInMemoryHistoryManager(), this));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка в файле: " + file.getAbsolutePath() + " " + file.getName(), e);
        }
    }

    public String toString(Task task) {
        if (task.getDuration() == null) {
            return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getEpic() + "," + task.getStartTime() + "," +
                    task.getDuration() + ",\n";
        } else {
            return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getEpic() + "," + task.getStartTime() + "," +
                    task.getDuration().toMinutes() + ",\n";
        }
    }

    public String toString(SubTask subTask) {
        if (subTask.getDuration() == null) {
            return subTask.getId() + "," + subTask.getTaskType() + "," + subTask.getName() + "," + subTask.getStatus()
                    + "," + subTask.getDescription() + "," + subTask.getEpicId() + "," + subTask.getStartTime()
                    + "," + subTask.getDuration() + ",\n";
        } else {
            return subTask.getId() + "," + subTask.getTaskType() + "," + subTask.getName() + "," + subTask.getStatus()
                    + "," + subTask.getDescription() + "," + subTask.getEpicId() + "," + subTask.getStartTime()
                    + "," + subTask.getDuration().toMinutes() + ",\n";
        }
    }

    public String toString(Epic epic) {
        if (epic.getDuration() == null) {
            return epic.getId() + "," + epic.getTaskType() + "," + epic.getName() + "," + epic.getStatus() + ","
                    + epic.getDescription() + "," + epic.getEpic() + "," + epic.getStartTime() + "," +
                    epic.getDuration() + ",\n";
        } else {
            return epic.getId() + "," + epic.getTaskType() + "," + epic.getName() + "," + epic.getStatus() + ","
                    + epic.getDescription() + "," + epic.getEpic() + "," + epic.getStartTime() + "," +
                    epic.getDuration().toMinutes() + ",\n";
        }
    }

    public void fromStringList(List<String> list) {
        String nullString = "null";
        for (String line : list) {
            if (line.isEmpty()) {
                continue;
            }
            String[] split = line.split(",");
            for (int i = 0; i < 2; i++) {
                if (!split[0].equals("id")) {
                    calculateNextId(Integer.parseInt(split[0]));
                    switch (split[1]) {
                        case "TASK" -> {
                            Task task = new Task(Integer.parseInt(split[0]), split[2], split[4]);
                            task.setStatus(Status.valueOf(split[3]));
                            if (split[6].equals(nullString)) {
                                task.setStartTime(null);
                            } else {
                                task.setStartTime(LocalDateTime.parse(split[6]));
                            }
                            if (split[7].equals(nullString)) {
                                task.setDuration(null);
                            } else {
                                task.setDuration(Duration.ofMinutes(Integer.parseInt(split[7])));
                            }
                            this.getAllTasks().put(task.getId(), task);
                        }
                        case "SUBTASK" -> {
                            SubTask subTask = new SubTask(Integer.parseInt(split[0]), split[2], split[4],
                                    Integer.parseInt(split[5]));
                            subTask.setStatus(Status.valueOf(split[3]));
                            if (split[6].equals(nullString)) {
                                subTask.setStartTime(null);
                            } else {
                                subTask.setStartTime(LocalDateTime.parse(split[6]));
                            }
                            if (split[7].equals(nullString)) {
                                subTask.setDuration(null);
                            } else {
                                subTask.setDuration(Duration.ofMinutes(Integer.parseInt(split[7])));
                            }
                            this.getAllSubTasks().put(subTask.getId(), subTask);
                        }
                        case "EPIC" -> {
                            Epic epic = new Epic(Integer.parseInt(split[0]), split[2], split[4]);
                            epic.setStatus(Status.valueOf(split[3]));
                            for (SubTask subtask : this.getAllSubTasks().values()) {
                                if (subtask.getEpicId() == epic.getId()) {
                                    epic.getSubTasksListInEpic().add(subtask.getId());
                                }
                            }
                            this.getAllEpics().put(epic.getId(), epic);
                            Epic epic1 = calculateTimeEpic(epic);
                            this.getAllEpics().put(epic1.getId(), epic1);
                        }
                    }
                }
            }
        }
    }
}
