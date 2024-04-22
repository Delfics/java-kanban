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
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.SubTask;
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

public class SubTaskHandlerTest {
    HttpTaskServer httpTaskServer = new HttpTaskServer();
    HttpClient client = HttpClient.newHttpClient();
    int codeSucceed = 200;
    int codeSucceedPost = 201;
    int codeNotFound = 404;
    int codeCrossOver = 406;


    static class SubTaskListTypeToken extends TypeToken<List<SubTask>> {
    }

    @BeforeEach
    void shouldStartHttpServer() throws IOException {
        Epic epic = httpTaskServer.getInMemoryTaskManager().createEpic("Epic", "TestEpic");

        SubTask subTask = httpTaskServer.getInMemoryTaskManager().createSubTask("TestName",
                "TestDescription", epic.getId());
        subTask.setStartTime(LocalDateTime.of(1994, 12, 25, 12, 25));
        subTask.setDuration(Duration.ofMinutes(20));
        httpTaskServer.getInMemoryTaskManager().updateSubTask(subTask);

        SubTask subTask1 = httpTaskServer.getInMemoryTaskManager().createSubTask("TestName2",
                "TestDescription", epic.getId());
        subTask1.setStartTime(LocalDateTime.of(1995, 12, 25, 12, 25));
        subTask1.setDuration(Duration.ofMinutes(20));
        httpTaskServer.getInMemoryTaskManager().updateSubTask(subTask1);

        httpTaskServer.startServer();
    }

    @AfterEach
    void shouldStopHttpServer() {
        httpTaskServer.stopServer();
    }

    @Test
    @DisplayName("Возвращает все subtasks")
    void shouldGETAllSubTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        int statusCode = response.statusCode();

        Gson gson = HttpService.gsonWithSettings();
        List<SubTask> subtasks = gson.fromJson(body, new SubTaskListTypeToken().getType());


        List<SubTask> streamTasks = httpTaskServer.getInMemoryTaskManager().getAllSubTasks().values().stream()
                .toList();

        assertEquals(codeSucceed, statusCode, "Код ответа 200 соответсвует успеху");
        assertEquals(subtasks, streamTasks, "subtasks сервера, идентичны полученным таскам");
    }

    @Test
    @DisplayName("Возвращает subtask по id")
    void shouldGETSubTaskById() throws IOException, InterruptedException {
        int id = 2;
        URI url = URI.create("http://localhost:8080/subtasks/2");
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
        SubTask subTask = gson.fromJson(jsonElement1, SubTask.class);

        SubTask subTaskById = httpTaskServer.getInMemoryTaskManager().getSubTaskById(id);

        assertEquals(codeSucceed, statusCode, "Код ответа 200 соответсвует успеху");
        assertEquals(subTask, subTaskById, "subtask на сервере идентична полученной таски");
    }

    @Test
    @DisplayName("Не находит subtask")
    void shouldGETNotFoundSubTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/4");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        assertEquals(codeNotFound, statusCode, "Код ответа 404 соответсвует если subtask не найдена");
    }

    @Test
    @DisplayName("Создает subtask")
    void shouldPOSTCreateSubTask() throws IOException, InterruptedException {
        Epic epicById = httpTaskServer.getInMemoryTaskManager().getEpicById(1);
        SubTask subTask = new SubTask(4, "Name", "Test", epicById.getId());
        Gson gson = HttpService.gsonWithSettings();
        String json = gson.toJson(subTask);


        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();


        SubTask subTaskById = httpTaskServer.getInMemoryTaskManager().getSubTaskById(subTask.getId());

        assertEquals(codeSucceedPost, statusCode, "Код ответа 201 соответсвует успеху");
        assertEquals(subTask, subTaskById, "subtask на сервере идентична полученной таски");
    }

    @Test
    @DisplayName("Обновляет subtask")
    void shouldPOSTUpdateSubTask() throws IOException, InterruptedException {
        Epic epicById = httpTaskServer.getInMemoryTaskManager().getEpicById(1);
        SubTask subTask = new SubTask(4, "UpdateName", "UpdateTest", epicById.getId(),
                LocalDateTime.of(2024, 4, 19, 14, 37, 35), Duration.ofMinutes(5));
        Gson gson = HttpService.gsonWithSettings();
        String json = gson.toJson(subTask);

        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();


        SubTask subTaskById = httpTaskServer.getInMemoryTaskManager().getSubTaskById(subTask.getId());

        assertEquals(codeSucceedPost, statusCode, "Код ответа 201 соответсвует успеху");
        assertEquals(subTask, subTaskById, "subtask на сервере идентична полученной таски");
    }

    @Test
    @DisplayName("Пересечение subtask если Id не указан")
    void shouldPOSTSubTaskIfHaveId() throws IOException, InterruptedException {
        Epic epicById = httpTaskServer.getInMemoryTaskManager().getEpicById(1);
        SubTask subTask = httpTaskServer.getInMemoryTaskManager().createSubTask("TestName",
                "TestDescription", epicById.getId());
        subTask.setStartTime(LocalDateTime.of(1994, 12, 25, 12, 25));
        subTask.setDuration(Duration.ofMinutes(20));

        Gson gson = HttpService.gsonWithSettings();
        String json = gson.toJson(subTask);

        URI url = URI.create("http://localhost:8080/subtasks");

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
    @DisplayName("Пересечение subtask если Id указан")
    void shouldPOSTSubTaskIfHaveNoId() throws IOException, InterruptedException {
        SubTask subTaskId = httpTaskServer.getInMemoryTaskManager().getSubTaskById(2);
        subTaskId.setStartTime(LocalDateTime.of(1994, 12, 25, 12, 25));
        subTaskId.setDuration(Duration.ofMinutes(5));

        Gson gson = HttpService.gsonWithSettings();
        String json = gson.toJson(subTaskId);

        URI url = URI.create("http://localhost:8080/subtasks");

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
    @DisplayName("Удаляет subtask")
    void shouldDeleteSubTask() throws IOException, InterruptedException {
        int id = 2;

        URI url = URI.create("http://localhost:8080/subtasks/2");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        boolean b = httpTaskServer.getInMemoryTaskManager().getAllSubTasks().containsKey(id);

        assertEquals(codeSucceed, statusCode, "Код ответа 204 соответсвует успеху");
        assertFalse(b, "Менеджер не содержит данную subtask");
    }
}
