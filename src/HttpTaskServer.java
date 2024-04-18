import com.sun.net.httpserver.HttpServer;
import ru.yandex.kanban.httpHandler.TaskHandler;
import ru.yandex.kanban.model.Task;
import ru.yandex.kanban.service.InMemoryTaskManager;
import ru.yandex.kanban.service.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    static InMemoryTaskManager inMemoryTaskManager = Managers.getDefault();

    public static void main(String[] args) throws IOException {
        Task task2 = inMemoryTaskManager.createTask("TestName", "TestDescription");
        task2.setStartTime(LocalDateTime.of(1994, 12, 25, 12, 25));
        task2.setDuration(Duration.ofMinutes(20));

        Task task = inMemoryTaskManager.createTask("TestName2", "TestDescription");
        task.setStartTime(LocalDateTime.of(1995, 12, 25, 12, 25));
        task.setDuration(Duration.ofMinutes(20));
        inMemoryTaskManager.updateTask(task);

        /*Task task1 = inMemoryTaskManager.createTask("TestName3", "TestDescription1");
        task1.setStartTime(LocalDateTime.of(1995, 12,25,12,25));
        task1.setDuration(Duration.ofMinutes(20));
        inMemoryTaskManager.updateTask(task1);*/

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(inMemoryTaskManager));
        httpServer.start();


    }
}
