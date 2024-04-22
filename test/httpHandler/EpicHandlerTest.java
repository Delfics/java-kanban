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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EpicHandlerTest {
    HttpTaskServer httpTaskServer = new HttpTaskServer();
    HttpClient client = HttpClient.newHttpClient();
    int codeSucceed = 200;
    int codeSucceedPost = 201;
    int codeNotFound = 404;


    static class EpicListTypeToken extends TypeToken<List<Epic>> {
    }

    @BeforeEach
    void shouldStartHttpServer() throws IOException {
        Epic epic = httpTaskServer.getInMemoryTaskManager().createEpic("Epic", "TestEpic");

        SubTask subTask = httpTaskServer.getInMemoryTaskManager().createSubTask("TestName",
                "TestDescription", epic.getId());
        subTask.setStartTime(LocalDateTime.of(1994, 12, 25, 12, 25));
        subTask.setDuration(Duration.ofMinutes(20));
        httpTaskServer.getInMemoryTaskManager().updateSubTask(subTask);

        SubTask subTask1 = httpTaskServer.getInMemoryTaskManager().createSubTask("TestName1",
                "TestDescription1", epic.getId());
        subTask1.setStartTime(LocalDateTime.of(1995, 12, 25, 12, 25));
        subTask1.setDuration(Duration.ofMinutes(20));
        httpTaskServer.getInMemoryTaskManager().updateSubTask(subTask1);

        Epic epic1 = httpTaskServer.getInMemoryTaskManager().createEpic("Epic1", "TestEpic1");

        SubTask subTask2 = httpTaskServer.getInMemoryTaskManager().createSubTask("TestName2",
                "TestDescription2", epic1.getId());
        subTask.setStartTime(LocalDateTime.of(1994, 12, 25, 12, 25));
        subTask.setDuration(Duration.ofMinutes(20));
        httpTaskServer.getInMemoryTaskManager().updateSubTask(subTask2);

        httpTaskServer.startServer();
    }

    @AfterEach
    void shouldStopHttpServer() {
        httpTaskServer.stopServer();
    }

    @Test
    @DisplayName("Возвращает все Epics")
    void shouldGETAllEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        int statusCode = response.statusCode();

        Gson gson = HttpService.gsonWithSettings();
        List<Epic> epics = gson.fromJson(body, new EpicListTypeToken().getType());


        List<Epic> streamTasks = httpTaskServer.getInMemoryTaskManager().getAllEpics().values().stream()
                .toList();

        assertEquals(codeSucceed, statusCode, "Код ответа 200 соответсвует успеху");
        assertEquals(epics, streamTasks, "epics сервера, идентичны полученным таскам");
    }

    @Test
    @DisplayName("Возвращает epic по id")
    void shouldGETEpicById() throws IOException, InterruptedException {
        int id = 1;
        URI url = URI.create("http://localhost:8080/epics/1");
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
        Epic epic = gson.fromJson(jsonElement1, Epic.class);

        Epic epicById = httpTaskServer.getInMemoryTaskManager().getEpicById(id);

        assertEquals(codeSucceed, statusCode, "Код ответа 200 соответсвует успеху");
        assertEquals(epic, epicById, "subtask на сервере идентична полученной таски");
    }

    @Test
    @DisplayName("Возвращает subtasks by epic id")
    void shouldGETEpicByIdSubtasks() throws IOException, InterruptedException {
        int id = 1;
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Gson gson = HttpService.gsonWithSettings();
        List<SubTask> fromEpic = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            SubTask subTask = gson.fromJson(element, SubTask.class);
            fromEpic.add(subTask);
        }

        Epic epicById = httpTaskServer.getInMemoryTaskManager().getEpicById(id);
        List<Integer> subTasksListInEpic = epicById.getSubTasksListInEpic();
        List<SubTask> fromEpicById = new ArrayList<>();
        for (SubTask subTask : httpTaskServer.getInMemoryTaskManager().getAllSubTasks().values()) {
            for (Integer ids : subTasksListInEpic) {
                if (subTask.getId() == ids) {
                    fromEpicById.add(subTask);
                }
            }
        }
        assertEquals(codeSucceed, statusCode, "Код ответа 200 соответсвует успеху");
        assertEquals(fromEpic, fromEpicById, "Спиок subtasks от сервера идентичны списку subtasks содержащего" +
                " epic");
    }

    @Test
    @DisplayName("Не находит epic")
    void shouldGETNotFoundEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epic/3");
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
    @DisplayName("Создает epic")
    void shouldPOSTCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(6, "Epic5", "TestEpic5");

        Gson gson = HttpService.gsonWithSettings();
        String json = gson.toJson(epic);


        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();


        Epic epicById1 = httpTaskServer.getInMemoryTaskManager().getEpicById(epic.getId());

        assertEquals(codeSucceedPost, statusCode, "Код ответа 201 соответсвует успеху");
        assertEquals(epic, epicById1, "Epic создана и идентична epic");
    }


    @Test
    @DisplayName("Удаляет epic")
    void shouldDeleteSubTask() throws IOException, InterruptedException {
        int id = 1;

        URI url = URI.create("http://localhost:8080/epics/1");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        boolean b = httpTaskServer.getInMemoryTaskManager().getAllSubTasks().containsKey(id);

        assertEquals(codeSucceed, statusCode, "Код ответа 204 соответсвует успеху");
        assertFalse(b, "Менеджер не содержит данный epic");
    }
}
