package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    File autoSave;

    FileBackedTaskManager(File file) {
        this.autoSave = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(autoSave, true))) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks().values()) {
                writer.write(task.getId() + "," + task.taskType() + "," + task.getName() + "," + task.getStatus()
                        + "," + task.getDescription() + "," + task.getEpic());
                writer.newLine();
            }

            for (SubTask subTask : getAllSubTasks().values()) {
                writer.write(subTask.getId() + "," + subTask.taskType() + "," + subTask.getName()
                        + "," + subTask.getStatus()
                        + "," + subTask.getDescription() + "," + subTask.getEpicId());
                writer.newLine();
            }

            for (Epic epic : getAllEpics().values()) {
                writer.write(epic.getId() + "," + epic.taskType() + "," + epic.getName() + "," + epic.getStatus()
                        + "," + epic.getDescription() + "," + epic.getEpic());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка в файле: " + autoSave.getAbsolutePath() + " " + autoSave.getName(), e);
        }
    }

    public Task fromString(String value) {
        try {
            String[] split = value.split(",");
            if (split[1].equals("TASK")) {
                Task task = new Task(Integer.parseInt(split[0]), split[2], split[4]);
                task.setStatus(Status.valueOf(split[3]));
                return task;
            } else if (split[1].equals("SUBTASK")) {
                SubTask subTask = new SubTask(Integer.parseInt(split[0]), split[2], split[4], Integer.parseInt(split[5]));
                subTask.setStatus(Status.valueOf(split[3]));
                return subTask;
            } else if (split[1].equals("EPIC")) {
                Epic epic = new Epic(Integer.parseInt(split[0]), split[2], split[4]);
                epic.setStatus(Status.valueOf(split[3]));
                return epic;
            }
        } catch (NullPointerException e) {
            System.out.println("Ошибка содания задачи из строки: " + e.getMessage());
        }
        return null;
    }
}
