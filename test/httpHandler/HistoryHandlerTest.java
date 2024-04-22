package httpHandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.httpHandler.HttpTaskServer;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;
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

public class HistoryHandlerTest {
    HttpTaskServer httpTaskServer = new HttpTaskServer();
    HttpClient client = HttpClient.newHttpClient();
    int codeSucceed = 200;

    static class TaskListTypeToken extends TypeToken<List<Task>> {
    }

    static class SubListTypeToken extends TypeToken<List<SubTask>> {
    }

    static class EpicListTypeToken extends TypeToken<List<Epic>> {
    }

    @BeforeEach
    void shouldStartHttpServer() throws IOException {
        Task task2 = httpTaskServer.getInMemoryTaskManager().createTask("TestName1", "TestDescription1");
        task2.setStartTime(LocalDateTime.of(1994, 12, 25, 12, 25));
        task2.setDuration(Duration.ofMinutes(20));
        httpTaskServer.getInMemoryTaskManager().updateTask(task2);

        Task task = httpTaskServer.getInMemoryTaskManager().createTask("TestName2", "TestDescription");
        task.setStartTime(LocalDateTime.of(1995, 12, 27, 12, 25));
        task.setDuration(Duration.ofMinutes(20));
        httpTaskServer.getInMemoryTaskManager().updateTask(task);

        Epic epic = httpTaskServer.getInMemoryTaskManager().createEpic("Epic", "TestEpic");

        SubTask subTask = httpTaskServer.getInMemoryTaskManager().createSubTask("TestName4",
                "TestDescription4", epic.getId());
        subTask.setStartTime(LocalDateTime.of(1996, 12, 29, 12, 25));
        subTask.setDuration(Duration.ofMinutes(20));

        httpTaskServer.getInMemoryTaskManager().updateSubTask(subTask);
        httpTaskServer.getInMemoryTaskManager().updateEpic(epic);
        httpTaskServer.getInMemoryTaskManager().getTaskById(task2.getId());
        httpTaskServer.getInMemoryTaskManager().getTaskById(task.getId());
        httpTaskServer.getInMemoryTaskManager().getEpicById(epic.getId());
        httpTaskServer.getInMemoryTaskManager().getSubTaskById(subTask.getId());

        httpTaskServer.startServer();
    }

    @AfterEach
    void shouldStopHttpServer() {
        httpTaskServer.stopServer();
    }

    @Test
    @DisplayName("Возвращает history")
    void shouldGetHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        int statusCode = response.statusCode();

        Gson gson = HttpService.gsonWithSettings();
        List<Task> history = gson.fromJson(body, new TaskListTypeToken().getType());
        List<SubTask> historySub = gson.fromJson(body, new SubListTypeToken().getType());
        List<Epic> historyEpic = gson.fromJson(body, new EpicListTypeToken().getType());
        List<Task> allTasksFromJson = new ArrayList<>(history);

        for (Epic epic : historyEpic) {
            if (epic.getSubTasksListInEpic() != null) {
                for (Task task : history) {
                    if (epic.getName().equals(task.getName())) {
                        int i = history.indexOf(task);
                        allTasksFromJson.remove(task);
                        allTasksFromJson.add(i, epic);
                    }
                }
            }
        }

        for (SubTask subTask : historySub) {
            if (subTask.getEpicId() != 0) {
                for (Task task : history) {

                    if (subTask.getEndTime().equals(task.getEndTime()) && subTask.getName().equals(task.getName())) {
                        int i = history.indexOf(task);
                        allTasksFromJson.remove(task);
                        allTasksFromJson.add(i, subTask);
                    }
                }
            }
        }


        List<Task> historyFromManager = httpTaskServer.getInMemoryTaskManager().getHistory();

        assertEquals(historyFromManager, allTasksFromJson, "Истории идентичны");
        assertEquals(codeSucceed, statusCode, "Код успеха");
    }
}
