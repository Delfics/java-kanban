package ru.yandex.kanban.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    public void init() throws IOException {
        File dir = new File("test/resources");
        File file = File.createTempFile("File", "New", dir);
        manager = TaskManagerFactory.createFileBackedTaskManager(file);
    }

    @AfterEach
    public void destroy() throws IOException {
        Path path = manager.getFile().toPath();
        Files.delete(path);
    }

    @Test
    void shouldLoadExistEmptyFileAtCreateFileBackedTaskManager() throws IOException {
        int lengthZero = 0;

        File dir = new File("test/resources");
        File tempFile = File.createTempFile("File", "New", dir);

        FileBackedTaskManager fileBackedTaskManager = TaskManagerFactory.createFileBackedTaskManager(tempFile);
        File autoSave = fileBackedTaskManager.getFile();

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
        File autoSave = fileBackedTaskManager.getFile();

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
        File autoSave = fileBackedTaskManager.getFile();

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
        int epicsInFile = 10;
        int subtaskInFile = 9;
        int taskInFile = 6;
        int historyFile = 14;

        File file = new File("test/resources/task1.txt");
        FileBackedTaskManager fileBackedTaskManager = TaskManagerFactory.createFileBackedTaskManager(file);
        File autoSave = fileBackedTaskManager.getFile();

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

    @Test
    void shouldBeEqualsManagersCreateAndManagerCreated() throws IOException {
        File dir = new File("test/resources");
        File tempFile = File.createTempFile("File", "New", dir);
        FileBackedTaskManager fileBackedTaskManager = TaskManagerFactory.createFileBackedTaskManager(tempFile);

        Task task = fileBackedTaskManager.createTask("Таска", "Описание Таски");
        Epic epic = fileBackedTaskManager.createEpic("Эпик", "Описание Эпика");
        SubTask subTask = fileBackedTaskManager.createSubTask("СабТаска", "Описание Сабтаски",
                epic.getId());
        task.setStatus(Status.IN_PROGRESS);
        subTask.setStatus(Status.IN_PROGRESS);
        fileBackedTaskManager.updateTask(task);
        fileBackedTaskManager.updateSubTask(subTask);

        fileBackedTaskManager.getTaskById(task.getId());
        fileBackedTaskManager.getSubTaskById(subTask.getId());
        fileBackedTaskManager.getEpicById(epic.getId());
        fileBackedTaskManager.getHistory();

        FileBackedTaskManager fileBackedTaskManager1 = TaskManagerFactory.createFileBackedTaskManager
                (fileBackedTaskManager.getFile());

        assertEquals(fileBackedTaskManager.getAllTasks(), fileBackedTaskManager1.getAllTasks(),
                "Сравнили список тасок у менеджера Создателя файла и у менеджера Загрузчика файла");
        assertEquals(fileBackedTaskManager.getAllSubTasks(), fileBackedTaskManager1.getAllSubTasks(),
                "Сравнили список сабтасок у менеджера Создателя файла и у менеджера Загрузчика файла ");
        assertEquals(fileBackedTaskManager.getAllEpics(), fileBackedTaskManager1.getAllEpics(),
                "Сравнили список епиков у менеджера Создателя файла и у менеджера Загрузчика файла");
        assertEquals(fileBackedTaskManager.getHistory(), fileBackedTaskManager1.getHistory(),
                "Сравнили список историй у менеджера Создателя файла и у менеджера Загрузчика файла");

        Task task1 = fileBackedTaskManager.createTask("Проверка", "Некст айди");
        Task task2 = fileBackedTaskManager1.createTask("Проверка", "Некст айди");

        assertEquals(task1, task2);

        Path path = tempFile.toPath();
        Files.delete(path);
    }

    @Test
    public void shouldThrowArrayIndexOutOfBoundsExceptionWhenLoadInvalidFile() {
        File file = new File("test/resources/task.txt");
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> TaskManagerFactory.createFileBackedTaskManager(file)
                , "При считывании битого файла возникает ArrayIndexOutOfBoundsException");
    }
}