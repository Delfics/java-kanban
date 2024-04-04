package ru.yandex.kanban.service;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void init() {
        manager = Managers.getDefault();
    }
}

