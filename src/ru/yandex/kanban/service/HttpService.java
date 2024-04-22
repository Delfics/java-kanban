package ru.yandex.kanban.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpService {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    static LocalDateTimeAdapter localDateTimeAdapter() {
        return new LocalDateTimeAdapter();
    }

    static DurationAdapter durationAdapter() {
        return new DurationAdapter();
    }

    public static void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            System.out.println(responseString);
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    public static Gson gsonWithSettings() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(Duration.class, HttpService.durationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, HttpService.localDateTimeAdapter());
        return gsonBuilder.create();
    }

    public enum Endpoint {
        GET,
        GET_BY_ID,
        GET_SUBTASKS,
        POST,
        DELETE,
        UNKNOWN
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null || localDateTime.equals("null")) {
                jsonWriter.value(String.valueOf(localDateTime));
            } else {
                jsonWriter.value(localDateTime.format(dtf));
            }
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            String result = jsonReader.nextString();
            if (result.equals("null")) {
                return null;
            } else {
                return LocalDateTime.parse(result, dtf);
            }
        }
    }

    private static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            if (duration == null || duration.equals("null")) {
                jsonWriter.value(String.valueOf(duration));
            } else {
                jsonWriter.value(duration.toMinutes());
            }
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            String result = jsonReader.nextString();
            if (result.equals("null")) {
                return null;
            } else {
                return Duration.ofMinutes(Long.parseLong(result));
            }
        }
    }
}