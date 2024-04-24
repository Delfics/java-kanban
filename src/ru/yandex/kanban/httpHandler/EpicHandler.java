package ru.yandex.kanban.httpHandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.service.HttpService;
import ru.yandex.kanban.service.InMemoryTaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EpicHandler implements HttpHandler {
    private final InMemoryTaskManager inMemoryTaskManager;

    public EpicHandler(InMemoryTaskManager inMemoryTaskManager) {
        this.inMemoryTaskManager = inMemoryTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            HttpService.Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());


            switch (endpoint) {
                case HttpService.Endpoint.GET -> getEpics(exchange);
                case HttpService.Endpoint.GET_BY_ID -> getEpicById(exchange);
                case HttpService.Endpoint.GET_SUBTASKS -> getEpicListSubtasks(exchange);
                case HttpService.Endpoint.POST -> createEpic(exchange);
                case HttpService.Endpoint.DELETE -> deleteEpic(exchange);
                case HttpService.Endpoint.UNKNOWN -> HttpService.writeResponse(exchange,
                        "Такого эндпоинта не существует",
                        404);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getEpics(HttpExchange exchange) throws IOException {
        Gson gson = HttpService.gsonWithSettings();
        String json = gson.toJson(parseHashMapToList());
        if (json.equals("[]")) {
            HttpService.writeResponse(exchange, "Список пуст", 404);
        }

        HttpService.writeResponse(exchange, json, 200);
    }

    private void getEpicById(HttpExchange exchange) throws IOException {
        Gson gson = HttpService.gsonWithSettings();
        String uri = exchange.getRequestURI().getPath();
        String[] path = uri.split("/");
        int id = Integer.parseInt(path[2]);
        if (!inMemoryTaskManager.getAllEpics().containsKey(id)) {
            HttpService.writeResponse(exchange, "Epic по заданному id не существует", 404);
        } else {
            Epic epicById = inMemoryTaskManager.getEpicById(id);
            inMemoryTaskManager.updateEpic(epicById);
            String json = gson.toJson(parseHashMapToListById(path[2]));
            HttpService.writeResponse(exchange, json, 200);
        }
    }

    private Epic getEpicFromJson(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
        Gson gson = HttpService.gsonWithSettings();
        return gson.fromJson(body, Epic.class);
    }

    private void createEpic(HttpExchange exchange) throws IOException {
        Epic epic = getEpicFromJson(exchange);
        if (epic.getName() == null | epic.getDescription() == null ||
                epic.getName().equals("null") | epic.getDescription().equals("null")) {
            HttpService.writeResponse(exchange, "Epic передан неправильно", 400);
        } else {
            epic = inMemoryTaskManager.createEpic(epic.getName(), epic.getDescription());
            HttpService.writeResponse(exchange, "Epic успешно создан", 201);
        }
    }

    private void deleteEpic(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        String[] path = uri.split("/");
        if (inMemoryTaskManager.getAllEpics().containsKey(Integer.parseInt(path[2]))) {
            inMemoryTaskManager.removeEpicById(Integer.parseInt(path[2]));
            HttpService.writeResponse(exchange, "Epic по заданному id удалена", 200);
        } else {
            HttpService.writeResponse(exchange, "Epic по заданному id не существует", 404);
        }
    }

    private void getEpicListSubtasks(HttpExchange exchange) throws IOException {
        Gson gson = HttpService.gsonWithSettings();
        String uri = exchange.getRequestURI().getPath();
        String[] path = uri.split("/");
        if (!inMemoryTaskManager.getAllEpics().containsKey(Integer.parseInt(path[2]))) {
            HttpService.writeResponse(exchange, "Epic по заданному id не существует", 404);
        } else {
            Epic epicById = inMemoryTaskManager.getEpicById(Integer.parseInt(path[2]));
            List<Integer> subTasksListInEpic = epicById.getSubTasksListInEpic();
            Collection<SubTask> valuesSubtasks = inMemoryTaskManager.getAllSubTasks().values();
            List<SubTask> subtasksByEpicId = new ArrayList<>();

            for (SubTask subTask : valuesSubtasks) {
                for (Integer id : subTasksListInEpic) {
                    if (subTask.getId() == id) {
                        subtasksByEpicId.add(subTask);
                    }
                }
            }
            String json = gson.toJson(subtasksByEpicId);
            HttpService.writeResponse(exchange, json,
                    200);
        }
    }


    private HttpService.Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");

        if (path[1].equals("epics") && requestMethod.equals("GET") && path.length == 2) {
            return HttpService.Endpoint.GET;
        } else if (path[1].equals("epics") && path.length == 3 && requestMethod.equals("GET")) {
            return HttpService.Endpoint.GET_BY_ID;
        } else if (path[1].equals("epics") && path.length == 4) {
            if ((path[3].equals("subtasks"))) {
                return HttpService.Endpoint.GET_SUBTASKS;
            } else {
                return HttpService.Endpoint.UNKNOWN;
            }
        } else if (requestMethod.equals("POST")) {
            return HttpService.Endpoint.POST;
        } else if (requestMethod.equals("DELETE")) {
            return HttpService.Endpoint.DELETE;
        } else {
            return HttpService.Endpoint.UNKNOWN;
        }
    }

    private List<Epic> parseHashMapToList() {
        for (Epic epic : inMemoryTaskManager.getAllEpics().values()) {
            inMemoryTaskManager.updateEpic(epic);
        }
        return new ArrayList<>(inMemoryTaskManager.getAllEpics().values());

    }

    private List<Epic> parseHashMapToListById(String id) {
        List<Epic> epics = new ArrayList<>();
        Epic epic = inMemoryTaskManager.getEpicById(Integer.parseInt(id));
        epics.add(epic);
        return epics;
    }

    private List<SubTask> parseHashMapToLisSubtasksById(String id) {
        List<SubTask> subTasks = new ArrayList<>();
        SubTask subTask = inMemoryTaskManager.getSubTaskById(Integer.parseInt(id));
        subTasks.add(subTask);
        return subTasks;
    }
}
