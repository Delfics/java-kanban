package httpHandler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.httpHandler.HttpTaskServer;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.Task;
import ru.yandex.kanban.service.HttpService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TaskHandlerTests {
    HttpTaskServer httpTaskServer = new HttpTaskServer();
    HttpClient client = HttpClient.newHttpClient();
    int codeSucceed = 200;
    int codeSucceedPost = 201;
    int codeNotFound = 404;
    int codeCrossOver = 406;


    static class TaskListTypeToken extends TypeToken<List<Task>> {
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
    @DisplayName("Возвращает все tasks")
    void shouldGETAllTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        int statusCode = response.statusCode();

        Gson gson = HttpService.gsonWithSettings();
        List<Task> tasks = gson.fromJson(body, new TaskListTypeToken().getType());


        List<Task> streamTasks = httpTaskServer.getInMemoryTaskManager().getAllTasks().values().stream()
                .toList();

        assertEquals(codeSucceed, statusCode, "Код ответа 200 соответсвует успеху");
        assertEquals(tasks, streamTasks, "Таски сервера, идентичны полученным таскам");
    }

    @Test
    @DisplayName("Возвращает task по id")
    void shouldGETTaskById() throws IOException, InterruptedException {
        int id = 1;
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        JsonElement jsonElement1 = jsonArray.get(0);
        Gson gson = HttpService.gsonWithSettings();
        Task task = gson.fromJson(jsonElement1, Task.class);

        Task taskById = httpTaskServer.getInMemoryTaskManager().getTaskById(id);

        assertEquals(codeSucceed, statusCode, "Код ответа 200 соответсвует успеху");
        assertEquals(task, taskById, "Таска на сервере идентична полученной таски");
    }

    @Test
    @DisplayName("Не находит таску")
    void shouldGETNotFoundTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/3");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        assertEquals(codeNotFound, statusCode, "Код ответа 404 соответсвует если задача не найдена");
    }

    @Test
    @DisplayName("Создает Task")
    void shouldPOSTCreateTask() throws IOException, InterruptedException {
        Task task = new Task(3, "Name", "Test");
        Gson gson = HttpService.gsonWithSettings();
        String json = gson.toJson(task);


        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();


        Task taskById = httpTaskServer.getInMemoryTaskManager().getTaskById(task.getId());

        assertEquals(codeSucceedPost, statusCode, "Код ответа 201 соответсвует успеху");
        assertEquals(task, taskById, "Таска на сервере идентична полученной таски");
    }

    @Test
    @DisplayName("Обновляет task")
    void shouldPOSTUpdateTask() throws IOException, InterruptedException {
        Task task = new Task(2, "UpdateName", "UpdateTest",
                LocalDateTime.of(2024, 4, 19, 14, 37, 35), Duration.ofMinutes(5));
        task.setStatus(Status.IN_PROGRESS);
        Gson gson = HttpService.gsonWithSettings();
        String json = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();


        Task taskById = httpTaskServer.getInMemoryTaskManager().getTaskById(task.getId());

        assertEquals(codeSucceedPost, statusCode, "Код ответа 201 соответсвует успеху");
        assertEquals(task, taskById, "Таска на сервере идентична полученной таски");
    }

    @Test
    @DisplayName("Пересечение задачи если Id не указан")
    void shouldPOSTTaskIfHaveId() throws IOException, InterruptedException {
        Task task = new Task(3, "UpdateName", "UpdateTest",
                LocalDateTime.of(1994, 12, 25, 12, 25), Duration.ofMinutes(5));
        task.setStatus(Status.IN_PROGRESS);
        Gson gson = HttpService.gsonWithSettings();
        String json = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        assertEquals(codeCrossOver, statusCode, "Код ответа 406 соответсвует пересечению");
    }

    @Test
    @DisplayName("Пересечение задачи если Id указан")
    void shouldPOSTTaskIfHaveNoId() throws IOException, InterruptedException {
        Task taskById = httpTaskServer.getInMemoryTaskManager().getTaskById(2);
        taskById.setStartTime(LocalDateTime.of(1994, 12, 25, 12, 25));
        taskById.setDuration(Duration.ofMinutes(5));

        Gson gson = HttpService.gsonWithSettings();
        String json = gson.toJson(taskById);

        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        assertEquals(codeCrossOver, statusCode, "Код ответа 406 соответсвует пересечению");
    }


    @Test
    @DisplayName("Удаляет таску")
    void shouldDeleteTask() throws IOException, InterruptedException {
        int id = 2;

        URI url = URI.create("http://localhost:8080/tasks/2");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        boolean b = httpTaskServer.getInMemoryTaskManager().getAllTasks().containsKey(id);

        assertEquals(codeSucceed, statusCode, "Код ответа 204 соответсвует успеху");
        assertFalse(b, "Менеджер не содержит данную таску");
    }
}
