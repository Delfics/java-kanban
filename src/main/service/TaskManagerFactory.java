package service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class TaskManagerFactory {
    private static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            List<String> saved = Files.readAllLines(file.toPath());
            fileBackedTaskManager.fromStringList(saved);
            List<Integer> ids = HistoryMapper.historyFromString(saved);
            for (Integer id : ids) {
                if (fileBackedTaskManager.getAllTasks().containsKey(id)) {
                    fileBackedTaskManager.getTaskById(id);
                } else if (fileBackedTaskManager.getAllSubTasks().containsKey(id)) {
                    fileBackedTaskManager.getSubTaskById(id);
                } else if (fileBackedTaskManager.getAllEpics().containsKey(id)) {
                    fileBackedTaskManager.getEpicById(id);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка в файле: " + file.getAbsolutePath() + " " + file.getName(), e);
        }
        return fileBackedTaskManager;
    }

    public static FileBackedTaskManager createFileBackedTaskManager(File file) {
        if (file.length() == 0) {
            return new FileBackedTaskManager(file);
//        } else if(!file.exists()) {
//            file.createNewFile();
//            return new FileBackedTaskManager(file);
//        }
        } else {
            return loadFromFile(file);
        }
    }
}
