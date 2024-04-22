package ru.yandex.kanban.httpHandler;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;
import ru.yandex.kanban.service.InMemoryTaskManager;
import ru.yandex.kanban.service.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private InMemoryTaskManager inMemoryTaskManager = Managers.getDefault();

    public HttpServer httpServer;

    public InMemoryTaskManager getInMemoryTaskManager() {
        return inMemoryTaskManager;
    }


    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        Task task2 = httpTaskServer.inMemoryTaskManager.createTask("TestName", "TestDescription");
        task2.setStartTime(LocalDateTime.of(1994, 12, 25, 12, 25));
        task2.setDuration(Duration.ofMinutes(20));
        httpTaskServer.inMemoryTaskManager.updateTask(task2);

        Task task = httpTaskServer.inMemoryTaskManager.createTask("TestName2", "TestDescription");
        task.setStartTime(LocalDateTime.of(1995, 12, 25, 12, 25));
        task.setDuration(Duration.ofMinutes(20));
        httpTaskServer.inMemoryTaskManager.updateTask(task);


        Epic epic = httpTaskServer.inMemoryTaskManager.createEpic("TestEpic", "TestDescription");

        SubTask subTask = httpTaskServer.getInMemoryTaskManager().createSubTask("TestName",
                "TestDescription", epic.getId());
        subTask.setStartTime(LocalDateTime.of(1995, 12, 26, 12, 25));
        subTask.setDuration(Duration.ofMinutes(20));
        httpTaskServer.getInMemoryTaskManager().updateSubTask(subTask);
        httpTaskServer.getInMemoryTaskManager().getTaskById(task2.getId());
        httpTaskServer.getInMemoryTaskManager().getEpicById(epic.getId());


        httpTaskServer.startServer();
    }

    public void startServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(inMemoryTaskManager));
        httpServer.createContext("/subtasks", new SubTaskHandler(inMemoryTaskManager));
        httpServer.createContext("/epics", new EpicHandler(inMemoryTaskManager));
        httpServer.createContext("/history", new HistoryHandler(inMemoryTaskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(inMemoryTaskManager));
        httpServer.start();
    }

    public void stopServer() {
        httpServer.stop(0);
    }
}
