package ru.yandex.kanban.httpHandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.Task;
import ru.yandex.kanban.service.HttpService;
import ru.yandex.kanban.service.InMemoryTaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TaskHandler implements HttpHandler {
    private final InMemoryTaskManager inMemoryTaskManager;

    public TaskHandler(InMemoryTaskManager inMemoryTaskManager) {
        this.inMemoryTaskManager = inMemoryTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            HttpService.Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

            switch (endpoint) {
                case HttpService.Endpoint.GET -> getTasks(exchange);
                case HttpService.Endpoint.GET_BY_ID -> getTaskById(exchange);
                case HttpService.Endpoint.POST -> {
                    Task task = getTaskFromJson(exchange);
                    if (!inMemoryTaskManager.getAllTasks().containsKey(task.getId())) {
                        createTask(exchange, task);
                    } else {
                        updateTask(exchange, task);
                    }
                }
                case HttpService.Endpoint.DELETE -> deleteTask(exchange);
                case HttpService.Endpoint.UNKNOWN -> HttpService.writeResponse(exchange,
                        "Такого эндпоинта не существует",
                        404);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        Gson gson = HttpService.gsonWithSettings();
        String json = gson.toJson(parseHashMapToList());
        if (json.equals("[]")) {
            HttpService.writeResponse(exchange, "Список пуст", 404);
        }
        HttpService.writeResponse(exchange, json, 200);
    }

    private void getTaskById(HttpExchange exchange) throws IOException {
        Gson gson = HttpService.gsonWithSettings();
        String uri = exchange.getRequestURI().getPath();
        String[] path = uri.split("/");
        if (!inMemoryTaskManager.getAllTasks().containsKey(Integer.parseInt(path[2]))) {
            HttpService.writeResponse(exchange, "Task по заданному id не существует", 404);
        } else {
            String json = gson.toJson(parseHashMapToListById(path[2]));
            HttpService.writeResponse(exchange, json, 200);
        }
    }

    private Task getTaskFromJson(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
        Gson gson = HttpService.gsonWithSettings();
        return gson.fromJson(body, Task.class);
    }

    private void createTask(HttpExchange exchange, Task task) throws IOException {
        if (task.getName() == null | task.getDescription() == null ||
                task.getName().equals("null") | task.getDescription().equals("null")) {
            HttpService.writeResponse(exchange, "Task передан неправильно", 400);
        } else if (task.getStartTime() == null && task.getDuration() == null) {
            task = inMemoryTaskManager.createTask(task.getName(), task.getDescription());
            HttpService.writeResponse(exchange, "Task успешно создан", 201);
        } else {
            task = inMemoryTaskManager.createTask(task.getName(), task.getDescription(), task.getStartTime(),
                    task.getDuration());
            if (task.getStartTime() == null && task.getDuration() == null) {
                HttpService.writeResponse(exchange, "Task не может быть добавлена из-за пересечения " +
                        "времени с другими задачами", 406);
            } else {
                HttpService.writeResponse(exchange, "Task успешно создан", 201);
            }
        }
    }

    private void updateTask(HttpExchange exchange, Task task) throws IOException {
        if (task.getName() == null | task.getDescription() == null ||
                task.getName().equals("null") | task.getDescription().equals("null")) {
            HttpService.writeResponse(exchange, "Task передан неправильно", 400);
        } else if (!inMemoryTaskManager.getAllTasks().containsKey(task.getId())) {
            HttpService.writeResponse(exchange, "Невозможно обновить, Task не существует", 404);
        } else {
            if (task.getStatus() == null) {
                task.setStatus(Status.NEW);
            }
            Task task1 = inMemoryTaskManager.updateTask(task);
            if (task1.getStartTime() == null && task1.getDuration() == null) {
                HttpService.writeResponse(exchange, "Task не может быть добавлена из-за пересечения " +
                        "времени с другими задачами", 406);
            } else {
                HttpService.writeResponse(exchange, "Task успешно обновёлн", 201);
            }
        }
    }

    private void deleteTask(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        String[] path = uri.split("/");
        if (inMemoryTaskManager.getAllTasks().containsKey(Integer.parseInt(path[2]))) {
            inMemoryTaskManager.removeTaskById(Integer.parseInt(path[2]));
            HttpService.writeResponse(exchange, "Task по заданному id удалена", 200);
        } else {
            HttpService.writeResponse(exchange, "Task по заданному id не существует", 404);
        }
    }


    private HttpService.Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");

        if (path[1].equals("tasks") && requestMethod.equals("GET") && path.length == 2) {
            return HttpService.Endpoint.GET;
        } else if (path[1].equals("tasks") && path.length == 3 && requestMethod.equals("GET")) {
            return HttpService.Endpoint.GET_BY_ID;
        } else if (requestMethod.equals("POST")) {
            return HttpService.Endpoint.POST;
        } else if (requestMethod.equals("DELETE")) {
            return HttpService.Endpoint.DELETE;
        } else {
            return HttpService.Endpoint.UNKNOWN;
        }
    }

    private List<Task> parseHashMapToList() {
        return new ArrayList<>(inMemoryTaskManager.getAllTasks().values());
    }

    private List<Task> parseHashMapToListById(String id) {
        List<Task> tasks = new ArrayList<>();
        Task task = inMemoryTaskManager.getTaskById(Integer.parseInt(id));
        tasks.add(task);
        return tasks;
    }
}
