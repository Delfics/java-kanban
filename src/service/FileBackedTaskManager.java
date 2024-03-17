package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File autoSave;

    public FileBackedTaskManager(File file) {
        this.autoSave = file;
    }

    public File getAutoSave() {
        return autoSave;
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(autoSave, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic,\n");
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
            throw new RuntimeException("Ошибка в файле: " + autoSave.getAbsolutePath() + " " + autoSave.getName(), e);
        }
    }

    public String toString(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus()
                + "," + task.getDescription() + "," + task.getEpic() + ",\n";
    }

    public String toString(SubTask subTask) {
        return subTask.getId() + "," + subTask.getTaskType() + "," + subTask.getName() + "," + subTask.getStatus()
                + "," + subTask.getDescription() + "," + subTask.getEpicId() + ",\n";
    }

    public String toString(Epic epic) {

        return epic.getId() + "," + epic.getTaskType() + "," + epic.getName() + "," + epic.getStatus()
                + "," + epic.getDescription() + "," + epic.getEpic() + ",\n";
    }

    public void fromStringList(List<String> list) {
        try {
            for (String line : list) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] split = line.split(",");
                for (int i = 0; i < 2; i++) {
                    if (split[1].equals("TASK")) {
                        Task task = new Task(Integer.parseInt(split[0]), split[2], split[4]);
                        task.setStatus(Status.valueOf(split[3]));
                        this.getAllTasks().put(task.getId(), task);
                    } else if (split[1].equals("SUBTASK")) {
                        SubTask subTask = new SubTask(Integer.parseInt(split[0]), split[2], split[4],
                                Integer.parseInt(split[5]));
                        subTask.setStatus(Status.valueOf(split[3]));
                        this.getAllSubTasks().put(subTask.getId(), subTask);
                    } else if (split[1].equals("EPIC")) {
                        Epic epic = new Epic(Integer.parseInt(split[0]), split[2], split[4]);
                        epic.setStatus(Status.valueOf(split[3]));
                        for (SubTask subtask : this.getAllSubTasks().values()) {
                            if (subtask.getEpicId() == epic.getId()) {
                                epic.getSubTasksListInEpic().add(subtask.getEpicId());
                            }
                        }
                        this.getAllEpics().put(epic.getId(), epic);
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Ошибка создания задачи из списка: " + e.getMessage());
        }
    }
}
