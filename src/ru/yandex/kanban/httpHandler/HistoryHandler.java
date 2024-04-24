package ru.yandex.kanban.httpHandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.model.Task;
import ru.yandex.kanban.service.HttpService;
import ru.yandex.kanban.service.InMemoryTaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler implements HttpHandler {
    private final InMemoryTaskManager inMemoryTaskManager;

    public HistoryHandler(InMemoryTaskManager inMemoryTaskManager) {
        this.inMemoryTaskManager = inMemoryTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            HttpService.Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());


            switch (endpoint) {
                case HttpService.Endpoint.GET -> getHistory(exchange);

                case HttpService.Endpoint.UNKNOWN -> HttpService.writeResponse(exchange,
                        "Такого эндпоинта не существует",
                        404);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        List<Task> history = inMemoryTaskManager.getHistory();
        Gson json = HttpService.gsonWithSettings();
        String toJson = json.toJson(history);
        if (toJson.equals("[]")) {
            HttpService.writeResponse(exchange, "Список пуст", 404);
        } else {
            HttpService.writeResponse(exchange, toJson, 200);
        }
    }

    private HttpService.Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");

        if (path[1].equals("history") && requestMethod.equals("GET") && path.length == 2) {
            return HttpService.Endpoint.GET;
        } else {
            return HttpService.Endpoint.UNKNOWN;
        }
    }
}
