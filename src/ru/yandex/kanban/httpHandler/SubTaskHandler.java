package ru.yandex.kanban.httpHandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.service.HttpService;
import ru.yandex.kanban.service.InMemoryTaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class SubTaskHandler implements HttpHandler {
    private final InMemoryTaskManager inMemoryTaskManager;

    public SubTaskHandler(InMemoryTaskManager inMemoryTaskManager) {
        this.inMemoryTaskManager = inMemoryTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            HttpService.Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());


            switch (endpoint) {
                case HttpService.Endpoint.GET -> getSubTasks(exchange);
                case HttpService.Endpoint.GET_BY_ID -> getSubTaskById(exchange);
                case HttpService.Endpoint.POST -> {
                    SubTask subTask = getSubTaskFromJson(exchange);
                    if (!inMemoryTaskManager.getAllSubTasks().containsKey(subTask.getId())) {
                        createSubTask(exchange, subTask);
                    } else {
                        updateSubTask(exchange, subTask);
                    }
                }
                case HttpService.Endpoint.DELETE -> deleteSubTask(exchange);
                case HttpService.Endpoint.UNKNOWN -> HttpService.writeResponse(exchange,
                        "Такого эндпоинта не существует",
                        404);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getSubTasks(HttpExchange exchange) throws IOException {
        Gson gson = HttpService.gsonWithSettings();
        String json = gson.toJson(parseHashMapToList());
        if (json.equals("[]")) {
            HttpService.writeResponse(exchange, "Список пуст", 404);
        }
        HttpService.writeResponse(exchange, json, 200);
    }

    private void getSubTaskById(HttpExchange exchange) throws IOException {
        Gson gson = HttpService.gsonWithSettings();
        String uri = exchange.getRequestURI().getPath();
        String[] path = uri.split("/");
        if (!inMemoryTaskManager.getAllSubTasks().containsKey(Integer.parseInt(path[2]))) {
            HttpService.writeResponse(exchange, "SubTask по заданному id не существует", 404);
        } else {
            String json = gson.toJson(parseHashMapToListById(path[2]));
            HttpService.writeResponse(exchange, json, 200);
        }
    }

    private SubTask getSubTaskFromJson(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
        Gson gson = HttpService.gsonWithSettings();
        return gson.fromJson(body, SubTask.class);
    }

    private void createSubTask(HttpExchange exchange, SubTask subTask) throws IOException {
        if (subTask.getName() == null | subTask.getDescription() == null ||
                subTask.getName().equals("null") | subTask.getDescription().equals("null") || subTask.getEpicId() == 0) {
            HttpService.writeResponse(exchange, "SubTask передан неправильно", 400);
        } else if (subTask.getStartTime() == null && subTask.getDuration() == null) {
            subTask = inMemoryTaskManager.createSubTask(subTask.getName(), subTask.getDescription(),
                    subTask.getEpicId());
            HttpService.writeResponse(exchange, "SubTask успешно создан", 201);
        } else {
            subTask = inMemoryTaskManager.createSubTask(subTask.getName(), subTask.getDescription(),
                    subTask.getEpicId(), subTask.getStartTime(), subTask.getDuration());
            if (subTask.getStartTime() == null && subTask.getDuration() == null) {
                HttpService.writeResponse(exchange, "SubTask не может быть добавлена из-за пересечения " +
                        "времени с другими задачами", 406);
            } else {
                HttpService.writeResponse(exchange, "SubTask успешно создан", 201);
            }
        }
    }

    private void updateSubTask(HttpExchange exchange, SubTask subTask) throws IOException {
        if (subTask.getName() == null | subTask.getDescription() == null ||
                subTask.getName().equals("null") | subTask.getDescription().equals("null") || subTask.getEpicId() == 0) {
            HttpService.writeResponse(exchange, "SubTask передан неправильно", 400);
        } else if (!inMemoryTaskManager.getAllSubTasks().containsKey(subTask.getId())) {
            HttpService.writeResponse(exchange, "Невозможно обновить, SubTask не существует",
                    404);
        } else {
            if (subTask.getStatus() == null) {
                subTask.setStatus(Status.NEW);
            }
            SubTask subTask1 = inMemoryTaskManager.updateSubTask(subTask);
            if (subTask1.getStartTime() == null && subTask1.getDuration() == null) {
                HttpService.writeResponse(exchange, "SubTask не может быть добавлена из-за пересечения " +
                        "времени с другими задачами", 406);
            } else {
                HttpService.writeResponse(exchange, "SubTask успешно обновлена", 200);
            }
        }
    }

    private void deleteSubTask(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        String[] path = uri.split("/");
        if (inMemoryTaskManager.getAllSubTasks().containsKey(Integer.parseInt(path[2]))) {
            inMemoryTaskManager.removeSubTaskById(Integer.parseInt(path[2]));
            HttpService.writeResponse(exchange, "SubTask по заданному id удалена", 200);
        } else {
            HttpService.writeResponse(exchange, "SubTask по заданному id не существует", 404);
        }
    }


    private HttpService.Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");

        if (path[1].equals("subtasks") && requestMethod.equals("GET") && path.length == 2) {
            return HttpService.Endpoint.GET;
        } else if (path[1].equals("subtasks") && path.length == 3 && requestMethod.equals("GET")) {
            return HttpService.Endpoint.GET_BY_ID;
        } else if (requestMethod.equals("POST")) {
            return HttpService.Endpoint.POST;
        } else if (requestMethod.equals("DELETE")) {
            return HttpService.Endpoint.DELETE;
        } else {
            return HttpService.Endpoint.UNKNOWN;
        }
    }

    private List<SubTask> parseHashMapToList() {
        return new ArrayList<>(inMemoryTaskManager.getAllSubTasks().values());
    }

    private List<SubTask> parseHashMapToListById(String id) {
        List<SubTask> subTasks = new ArrayList<>();
        SubTask subTask = inMemoryTaskManager.getSubTaskById(Integer.parseInt(id));
        subTasks.add(subTask);
        return subTasks;
    }
}
