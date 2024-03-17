package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    @Test
    void shouldLoadExistEmptyFileAtCreateFileBackedTaskManager() throws IOException {
        int lengthZero = 0;

        File dir = new File("test/resources");
        File tempFile = File.createTempFile("File", "New", dir);

        FileBackedTaskManager fileBackedTaskManager = TaskManagerFactory.createFileBackedTaskManager(tempFile);
        File autoSave = fileBackedTaskManager.getAutoSave();

        assertTrue(autoSave.exists(), "Проверили что файл создан");
        assertEquals(lengthZero, autoSave.length(), "Сохранили пустой файл и проверили его длину");

        Path path = autoSave.toPath();
        Files.delete(path);
    }

    @Test
    void shouldCreateAndSaveEmptyFileAtCreateFileBackedTaskManager() throws IOException {
        int lengthZero = 0;

        File file = new File("test/resources/test_not_exist_file.txt");
        FileBackedTaskManager fileBackedTaskManager = TaskManagerFactory.createFileBackedTaskManager(file);
        File autoSave = fileBackedTaskManager.getAutoSave();

        assertTrue(autoSave.exists(), "Проверили что файл создан");
        assertEquals(lengthZero, autoSave.length(), "Сохранили пустой файл и проверили его длину");

        Path path = autoSave.toPath();
        Files.delete(path);
    }

    @Test
    void shouldSaveTasksInExistFile() throws IOException {
        int lengthZero = 0;
        File file = new File("test/resources/test_not_exist_file.txt");
        FileBackedTaskManager fileBackedTaskManager = TaskManagerFactory.createFileBackedTaskManager(file);
        File autoSave = fileBackedTaskManager.getAutoSave();

        assertTrue(autoSave.exists(), "Проверили что файл создан");
        assertEquals(lengthZero, autoSave.length(), "Сохранили пустой файл и проверили его длину");

        Task task = fileBackedTaskManager.createTask("Таска", "Описание Таски");
        Epic epic = fileBackedTaskManager.createEpic("Эпик", "Описание Эпика");
        SubTask subTask = fileBackedTaskManager.createSubTask("СабТаска", "Описание Сабтаски",
                epic.getId());

        assertTrue(autoSave.length() > lengthZero, "Проверили что файл не пустой");

        Path path = autoSave.toPath();
        Files.delete(path);
    }

    @Test
    void shouldLoadTasksFromExistFile() {
        int lengthZero = 0;
        int epicsInFile = 3;
        int subtaskInFile = 1;
        int taskInFile = 1;
        int historyFile = 2;

        File file = new File("test/resources/task.txt");
        FileBackedTaskManager fileBackedTaskManager = TaskManagerFactory.createFileBackedTaskManager(file);
        File autoSave = fileBackedTaskManager.getAutoSave();

        assertTrue(autoSave.exists(), "Проверили что файл создан");
        assertTrue(autoSave.length() > lengthZero, "Загрузили файл и проверили его длину");

        int sizeTasks = fileBackedTaskManager.getAllTasks().size();
        int sizeEpics = fileBackedTaskManager.getAllEpics().size();
        int sizeSubTasks = fileBackedTaskManager.getAllSubTasks().size();
        int sizeHistory = fileBackedTaskManager.getHistory().size();

        assertTrue(sizeTasks > lengthZero, "Проверили что число тасок не равно нулю");
        assertTrue(sizeEpics > lengthZero, "Проверили что число эпиков не равно нулю");
        assertTrue(sizeSubTasks > lengthZero, "Проверили что число сабтасок не равно нулю");
        assertTrue(sizeHistory > lengthZero, "Проверили что история не равна нулю");

        assertEquals(sizeTasks, taskInFile, "Равны количеству тасок");
        assertEquals(sizeEpics, epicsInFile, "Равны количеству эпиков");
        assertEquals(sizeSubTasks, subtaskInFile, "Равны количеству сабтасок");
        assertEquals(sizeHistory, historyFile, "Равны количству историй");
    }
}