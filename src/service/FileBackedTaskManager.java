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
import java.util.ArrayList;
import java.util.HashMap;
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
    public HistoryManager getInMemoryHistoryManager() {
        return super.getInMemoryHistoryManager();
    }

    @Override
    public HashMap<Integer, SubTask> getAllSubTasks() {
        return super.getAllSubTasks();
    }

    @Override
    public HashMap<Integer, Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public HashMap<Integer, Epic> getAllEpics() {
        return super.getAllEpics();
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
    public List<SubTask> getSubTasksByEpicId(int epicId) {
        return super.getSubTasksByEpicId(epicId);
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

    @Override
    public void showAllEpics() {
        super.showAllEpics();
    }

    @Override
    public void showAllTasks() {
        super.showAllTasks();
    }

    @Override
    public void showAllSubtasks() {
        super.showAllSubtasks();
    }

    @Override
    public void calculateStatus(Status status, int epicId) {
        super.calculateStatus(status, epicId);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(autoSave, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic \n");
            writer.append('\uFEFF');

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
            writer.write(historyToString(getInMemoryHistoryManager()));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка в файле: " + autoSave.getAbsolutePath() + " " + autoSave.getName(), e);
        }
    }

    public String toString(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus()
                + "," + task.getDescription() + "," + task.getEpic() + "\n";
    }

    public String toString(SubTask subTask) {
        return subTask.getId() + "," + subTask.getSubTaskType() + "," + subTask.getName()
                + "," + subTask.getStatus() + "," + subTask.getDescription() + "," + subTask.getEpicId() + "\n";
    }

    public String toString(Epic epic) {
        return epic.getId() + "," + epic.getEpicType() + "," + epic.getName() + "," + epic.getStatus()
                + "," + epic.getDescription() + "," + epic.getEpic() + "\n";
    }

    public Task fromString(String value) {
        try {
            String[] split = value.split(",");
            if (split[1].equals("TASK")) {
                Task task = new Task(Integer.parseInt(split[0]), split[2], split[4]);
                task.setStatus(Status.valueOf(split[3]));
                return task;
            } else if (split[1].equals("SUBTASK")) {
                SubTask subTask = new SubTask(Integer.parseInt(split[0]), split[2], split[4],
                        Integer.parseInt(split[5]));
                subTask.setStatus(Status.valueOf(split[3]));
                return subTask;
            } else if (split[1].equals("EPIC")) {
                Epic epic = new Epic(Integer.parseInt(split[0]), split[2], split[4]);
                epic.setStatus(Status.valueOf(split[3]));
                return epic;
            }
        } catch (NullPointerException e) {
            System.out.println("Ошибка создания задачи из строки: " + e.getMessage());
        }
        return null;
    }

    public static String historyToString(HistoryManager manager) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(
        );
        StringBuilder history = new StringBuilder();
        String className = "";
        for (int i = 0; i < manager.getHistory().size(); i++) {
            className = String.valueOf(manager.getHistory().get(i).getClass());
            Task task = manager.getHistory().get(i);
            if (className.equals("class model.Task")) {
                history.append(fileBackedTaskManager.toString(task));
            } else if (className.equals("class model.SubTask")) {
                SubTask subTask = (SubTask) task;
                history.append(fileBackedTaskManager.toString(subTask));
            } else if (className.equals("class model.Epic")) {
                Epic epic = (Epic) task;
                history.append(fileBackedTaskManager.toString(epic));
            }
        }
        return history.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> integers = new ArrayList<>();
        String[] split = value.split("\n");
        for (int i = 0; i < split.length; i++) {
            String newString = split[i];
            String[] newSplit = newString.split(",");
            if (newSplit[1].equals("TASK")) {
                Task task = new Task(Integer.parseInt(newSplit[0]), newSplit[2], newSplit[4]);
                task.setStatus(Status.valueOf(newSplit[3]));
                integers.add(task.getId());
            } else if (newSplit[1].equals("SUBTASK")) {
                SubTask subTask = new SubTask(Integer.parseInt(newSplit[0]), newSplit[2], newSplit[4],
                        Integer.parseInt(newSplit[5]));
                subTask.setStatus(Status.valueOf(newSplit[3]));
                integers.add(subTask.getId());
            } else if (newSplit[1].equals("EPIC")) {
                Epic epic = new Epic(Integer.parseInt(newSplit[0]), newSplit[2], newSplit[4]);
                epic.setStatus(Status.valueOf(newSplit[3]));
                integers.add(epic.getId());
            }
        }
        return integers;
    }

 /*   static FileBackedTaskManager loadFromFile (File file) {
        try {
            Files.readString(file.toPath());



        } catch (IOException e) {
            throw new RuntimeException("Ошибка в файле: " + file.getAbsolutePath() + " " + file.getName(), e);
        }
    }*/
}
