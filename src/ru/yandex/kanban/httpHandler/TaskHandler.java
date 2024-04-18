package ru.yandex.kanban.httpHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.Task;
import ru.yandex.kanban.service.InMemoryTaskManager;
import ru.yandex.kanban.service.TypeAdapters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final InMemoryTaskManager inMemoryTaskManager;

    public TaskHandler(InMemoryTaskManager inMemoryTaskManager) {
        this.inMemoryTaskManager = inMemoryTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        System.out.println(exchange.getRequestURI().getPath());
        System.out.println(exchange.getRequestMethod());

        switch (endpoint) {
            case Endpoint.GET -> getTasks(exchange);
            case Endpoint.GET_BY_ID -> getTaskById(exchange);
            case Endpoint.POST -> {
                Task task = getTaskFromJson(exchange);
                if (task.getId() == 0) {
                    createTask(exchange, task);
                } else {
                    updateTask(exchange, task);
                }
            }
            case Endpoint.DELETE -> deleteTask(exchange);
            case Endpoint.UNKNOWN -> writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        Gson gson = gsonWithSettings();
        String json = gson.toJson(parseHashMapToList());
        System.out.println(json);
        writeResponse(exchange, json, 200);
    }

    private void getTaskById(HttpExchange exchange) throws IOException {
        Gson gson = gsonWithSettings();
        String uri = exchange.getRequestURI().getPath();
        String[] path = uri.split("/");
        if (Integer.parseInt(path[2]) <= 0) {
            writeResponse(exchange, "Задачи по заданному id не существует", 404);
        } else {
            String json = gson.toJson(parseHashMapToListById(path[2]));
            writeResponse(exchange, "Задача по заданному id \n" + json, 200);
        }
    }

    private Task getTaskFromJson(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
        Gson gson = gsonWithSettings();
        return gson.fromJson(body, Task.class);
    }

    private void createTask(HttpExchange exchange, Task task) throws IOException {
        if (task.getName() == null | task.getDescription() == null ||
                task.getName().equals("null") | task.getDescription().equals("null")) {
            writeResponse(exchange, "Task передан неправильно", 400);
        } else if (task.getStartTime() == null && task.getDuration() == null) {
            task = inMemoryTaskManager.createTask(task.getName(), task.getDescription());
            writeResponse(exchange, "Task успешно создан", 201);
        } else {
            task = inMemoryTaskManager.createTask(task.getName(), task.getDescription(), task.getStartTime(),
                    task.getDuration());
            if (task.getStartTime() == null && task.getDuration() == null) {
                writeResponse(exchange, "Task не может быть добавлена из-за пересечения времени с другими " +
                        "задачами", 406);
            } else {
                writeResponse(exchange, "Task успешно создан", 201);
            }
        }
        System.out.println(task);
    }

    private void updateTask(HttpExchange exchange, Task task) throws IOException {
        if (task.getName() == null | task.getDescription() == null ||
                task.getName().equals("null") | task.getDescription().equals("null")) {
            writeResponse(exchange, "Task передан неправильно", 400);
        } else if (!inMemoryTaskManager.getAllTasks().containsKey(task.getId())) {
            writeResponse(exchange, "Невозможно обновить, Task не существует", 404);
        } else {
            if (task.getStatus() == null) {
                task.setStatus(Status.NEW);
            }
            Task task1 = inMemoryTaskManager.updateTask(task);
            if (task1.getStartTime() == null && task1.getDuration() == null) {
                writeResponse(exchange, "Task не может быть добавлена из-за пересечения времени с другими " +
                        "задачами", 406);
            } else {
                System.out.println(task);
                writeResponse(exchange, "Task успешно обновёлн", 200);
            }
        }
        System.out.println(task);
    }

    private void deleteTask(HttpExchange exchange) throws IOException {
        Task taskFromJson = getTaskFromJson(exchange);
        if (inMemoryTaskManager.getAllTasks().containsKey(taskFromJson.getId())) {
            inMemoryTaskManager.removeTaskById(taskFromJson.getId());
            writeResponse(exchange, "Задача по заданному id удалена", 200);
        } else {
            writeResponse(exchange, "Задача по заданному id не существует", 404);
        }
    }


    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            System.out.println(responseString);
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");

        if (path[1].equals("tasks") && requestMethod.equals("GET") && path.length == 2) {
            return Endpoint.GET;
        } else if (path[1].equals("tasks") && path.length == 3) {
            return Endpoint.GET_BY_ID;
        } else if (requestMethod.equals("POST")) {
            return Endpoint.POST;
        } else if (requestMethod.equals("DELETE")) {
            return Endpoint.DELETE;
        } else {
            return Endpoint.UNKNOWN;
        }

    }

    class ListTypeToken extends TypeToken<List<Task>> {
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

    private Gson gsonWithSettings() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(Duration.class, TypeAdapters.durationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, TypeAdapters.localDateTimeAdapter());
        return gsonBuilder.create();
    }

    enum Endpoint {
        GET,
        GET_BY_ID,
        POST,
        DELETE,
        UNKNOWN
    }

}
