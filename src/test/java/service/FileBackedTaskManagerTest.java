package java.service;

import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import service.TaskManagerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    @Test
    void shouldLoadExistEmptyFileAndSucces() throws IOException {
        int length = 0;

        File dir = new File("src/test/resources");
        File tempFile = File.createTempFile("File", "New", dir);

        FileBackedTaskManager fileBackedTaskManager = TaskManagerFactory.createFileBackedTaskManager(tempFile);
        File autoSave = fileBackedTaskManager.getAutoSave();

        assertTrue(autoSave.exists(), "Проверили что файл создан");
        assertEquals(length, autoSave.length(), "Сохранили пустой файл и проверили его длину");
        Path path = autoSave.toPath();

        Files.delete(path);
    }

    @Test
    void shouldSaveEmptyFileAndSuccess() {
        int length = 0;

        File file = new File("src/test/resources/text1.txt");
//        File tempFile = File.createTempFile("File", "New", dir);

        FileBackedTaskManager fileBackedTaskManager = TaskManagerFactory.createFileBackedTaskManager(file);
        File autoSave = fileBackedTaskManager.getAutoSave();

        assertTrue(autoSave.exists(), "Проверили что файл создан");
        assertEquals(length, autoSave.length(), "Сохранили пустой файл и проверили его длину");
        Path path = autoSave.toPath();

//        Files.delete(path);

    }


//    @Test
//    void shouldLoadExistFileAndSuccess() {
//        File file = new File("src/test/resources/text.txt");
//        FileBackedTaskManager fileBackedTaskManager = TaskManagerFactory.createFileBackedTaskManager(file);
//
//        File autoSave = fileBackedTaskManager.getAutoSave();
//        assertTrue(autoSave.exists(), "Проверили что файл создан");
//        assertNotEquals(length, autoSave.length(), "Сохранили пустой файл и проверили его длину");
//        Path path = autoSave.toPath();
//
//        Files.delete(path);
//
//    }


    @Test
    void fromStringList() {
    }
}