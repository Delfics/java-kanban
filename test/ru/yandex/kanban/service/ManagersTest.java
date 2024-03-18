package ru.yandex.kanban.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.kanban.service.Managers.getDefault;
import static ru.yandex.kanban.service.Managers.getDefaultHistory;

class ManagersTest {

    @Test
    void shouldGetDefaultInMemoryTaskManager() {
        TaskManager inMemoryTaskManagerResponse = getDefault();

        assertEquals(InMemoryTaskManager.class, inMemoryTaskManagerResponse.getClass(),
                "Возвращает не проинициализированный экзмепляр");
    }

    @Test
    void shouldGetDefaultHistoryInMemoryHistoryManager() {
        HistoryManager inHistoryManagerResponse = getDefaultHistory();

        assertEquals(InMemoryHistoryManager.class, inHistoryManagerResponse.getClass(),
                "Возвращает не проинициализированный экзмепляр");
    }
}