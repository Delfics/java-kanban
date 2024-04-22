package httpHandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.httpHandler.HttpTaskServer;
import ru.yandex.kanban.model.Task;
import ru.yandex.kanban.service.HttpService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest {
    HttpTaskServer httpTaskServer = new HttpTaskServer();
    HttpClient client = HttpClient.newHttpClient();
    int codeSucceed = 200;

    static class TaskListTypeToken extends TypeToken<TreeSet<Task>> {
    }

    @BeforeEach
    void shouldStartHttpServer() throws IOException {
        Task task2 = httpTaskServer.getInMemoryTaskManager().createTask("TestName", "TestDescription");
        task2.setStartTime(LocalDateTime.of(1994, 12, 25, 12, 25));
        task2.setDuration(Duration.ofMinutes(20));
        httpTaskServer.getInMemoryTaskManager().updateTask(task2);

        Task task = httpTaskServer.getInMemoryTaskManager().createTask("TestName2", "TestDescription");
        task.setStartTime(LocalDateTime.of(1995, 12, 25, 12, 25));
        task.setDuration(Duration.ofMinutes(20));
        httpTaskServer.getInMemoryTaskManager().updateTask(task);

        httpTaskServer.startServer();
    }

    @AfterEach
    void shouldStopHttpServer() {
        httpTaskServer.stopServer();
    }

    @Test
    @DisplayName("Возвращает prioritizedTasks")
    void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        int statusCode = response.statusCode();

        Gson gson = HttpService.gsonWithSettings();
        TreeSet<Task> prioritized = gson.fromJson(body, new TaskListTypeToken().getType());

        TreeSet<Task> prioritizedTasks = httpTaskServer.getInMemoryTaskManager().getPrioritizedTasks();

        assertEquals(prioritized, prioritizedTasks, "Приоритетные задачи идентичны");
        assertEquals(statusCode, codeSucceed, "Код успеха идентичен");
    }
}
