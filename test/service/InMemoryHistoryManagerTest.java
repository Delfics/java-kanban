package service;

import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryHistoryManagerTest {
    private final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    private final TaskManager taskManager = Managers.getDefault();

    @Test
    @DisplayName("На Дубликаты")
    void shouldHaveNoDuplicatesInHistoryTasks() {
        int size = 1;
        Task task1 = taskManager.createTask("Name " + 1, "Task" + 1);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());

        assertEquals(size, taskManager.getInMemoryHistoryManager().getHistory().size(), "Не содержит Дубликатов");
        assertEquals(task1, taskManager.getInMemoryHistoryManager().getHistory().get(0), "Содержит последнюю таску");
    }

    @Test
    @DisplayName("На связный список Node")
    void shouldAddAndRemoveTasksInNodeMap() {
        Task task1 = taskManager.createTask("Name " + 1, "Task" + 1);

        historyManager.linkLast(task1);

        InMemoryHistoryManager.Node taskNode = historyManager.getNodeMap().get(task1.getId());
        assertEquals(task1, taskNode.task, "Добавление, в Связном списке Node содержит Таску");

        historyManager.removeNode(taskNode);
        assertNotEquals(task1, historyManager.getNodeMap().get(taskNode.task),
                "Удаление, Node Не Содержит таску в Связном списке");
    }

    @Test
    @DisplayName("На добавление и удаление")
    void shouldAddAndRemoveHistory() {
        Task task1 = taskManager.createTask("Name " + 1, "Task" + 1);

        historyManager.add(task1);
        assertEquals(task1, historyManager.getTasks().get(0), "Добавление работает корректно");

        historyManager.remove(task1.getId());
        assertTrue(historyManager.getHistory().size() == 0, "Удаление работает корректно");
    }

    @Test
    @DisplayName("На размер истории")
    void shouldHistoryMustBeUnlimitedSize() {
        int size = 1000;
        for (int i = 0; i < size; i++) {
            historyManager.linkLast(new Task(i, "Name " + i, "Task" + i));
        }
        assertEquals(size, historyManager.getHistory().size(), "Размер истории должен быть равен size");
    }
}