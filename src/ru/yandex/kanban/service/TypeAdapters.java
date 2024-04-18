package ru.yandex.kanban.service;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TypeAdapters {
    public static LocalDateTimeAdapter localDateTimeAdapter() {
        return new LocalDateTimeAdapter();
    }

    public static DurationAdapter durationAdapter() {
        return new DurationAdapter();
    }
}

class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.value((String) null);
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

class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.value(String.valueOf(duration));
        } else {
            jsonWriter.value(duration.toMinutes());
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        String result = jsonReader.nextString();
        Duration duration = Duration.ofMinutes(Long.parseLong(result));
        if (result.equals("null")) {
            return null;
        } else {
            return Duration.parse(duration.toString());
        }
    }
}