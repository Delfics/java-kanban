package ru.yandex.kanban.service;

import org.junit.jupiter.api.Test;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest {

    private final TaskManager manager = Managers.getDefault();

    @Test
    void inMemoryTaskManagerShouldAddTasksAllTypes() {
        String name = "Name";
        String description = "Description";

        Task task1 = manager.createTask(name, description);
        Epic epic1 = manager.createEpic(name, description);
        SubTask subTask1 = manager.createSubTask(name, description, epic1.getId());
        Epic epic = manager.updateEpic(epic1);

        assertNotNull(task1, "Менеджер не содержит Тип Задача");
        assertNotNull(epic1, "Менеджер не содержит Тип Эпик");
        assertNotNull(subTask1, "Менеджер не содержит Тип Подзадача");
        assertNotNull(manager.getTaskById(task1.getId()), "Менеджер не находит Задачу по ID");
        assertEquals(manager.getTaskById(task1.getId()), task1, "Таски не равны");
        assertEquals(manager.getTaskById(task1.getId()), task1, "Таски не равны");
        assertNotNull(manager.getEpicById(epic1.getId()), "Менеджер не находит Эпик по ID");
        assertEquals(manager.getEpicById(epic1.getId()), epic, "Эпики не равны");
        assertNotNull(manager.getSubTaskById(subTask1.getId()), "Менеджер не находит Подзадачу по ID");
        assertEquals(manager.getSubTaskById(subTask1.getId()), subTask1, "Подзадачи не равны");
    }

    @Test
    void shouldBeEqualsTaskAndTaskCreatedByManager() {
        String name = "TestTask";
        String description = "Test Task Description";

        Task taskResponse = manager.createTask(name, description);

        assertNotEquals(taskResponse.getId(), 0, "Id Задачи равен нулю");
        assertEquals(taskResponse.getName(), name, "Имена Задачи не совпадают");
        assertEquals(taskResponse.getDescription(), description, "Описания Задачи не совпадают");
        assertTrue(manager.getAllTasks().containsValue(taskResponse), "Задачи нет в списке созданных");
    }

    @Test
    void shouldBeEqualsSubTaskAndSubTaskCreatedByManager() {
        int id = 2;
        int epicId = 1;
        String name = "TestSubTask";
        String description = "Test SubTask Description";
        SubTask subTask = new SubTask(id, name, description, epicId);
        Epic responseEpic = manager.createEpic("Test Epic", "Test Epic Description");

        SubTask responseSubTask = manager.createSubTask(name, description, responseEpic.getId());

        Epic epicById = manager.getEpicById(responseEpic.getId());
        int subTaskId = epicById.getSubTasksListInEpic().get(0);

        assertEquals(subTask, responseSubTask, "Подзадачи не совпадают");
        assertTrue(manager.getAllSubTasks().containsValue(responseSubTask), "Подзадачи нет в списке созданных");
        assertEquals(subTaskId, responseSubTask.getId(),
                "В списке подзадач id в Эпике, id совпадает с id Подзадачи");
    }

    @Test
    void shouldBeEqualsEpicAndEpicCreatedByManager() {
        String name = "Test Epic";
        String description = "Test Epic Description";
        Epic epicResponse = manager.createEpic(name, description);

        assertNotEquals(epicResponse.getId(), 0, "Id Эпика равен нулю");
        assertEquals(epicResponse.getName(), name, " Имена Эпика не совпадают");
        assertEquals(epicResponse.getDescription(), description, "Описания Эпика не совпадают");
        assertTrue(manager.getAllEpics().containsValue(epicResponse), "Эпика нет в списке созданных");
    }

    @Test
    void shouldBeUpdateTaskAndSendChangedTaskToUpdate() {
        String name = "Test Task";
        String description = "Test Task Description";
        Task taskResponse = manager.createTask("Another Task", "Another Description");
        Task task = new Task(taskResponse.getId(), name, description);
        assertNotEquals(taskResponse, task, "Задачи равны");

        taskResponse = manager.updateTask(task);

        assertEquals(taskResponse.getName(), task.getName(), "Имена Задачи не равны");
        assertEquals(taskResponse.getDescription(), task.getDescription(), "Описания Задачи не совпадают");
        assertNotEquals(taskResponse.getId(), 0, "Id Эпика равен нулю");
    }

    @Test
    void shouldBeUpdateSubTaskAndSendChangedSubTaskToUpdate() {
        int idEpic = 1;
        String name = "Test SubTask";
        String description = "Test SubTask Description";
        Epic epic = manager.createEpic(name, description);
        SubTask subTaskResponse = manager.createSubTask("Another Subtask", "Another Description",
                epic.getId());
        SubTask subTask = new SubTask(subTaskResponse.getId(), name, description, idEpic);
        assertNotEquals(subTaskResponse, subTask, "Подзадачи равны");
        subTask.setName(name + "1");
        subTask.setDescription(description + "1");

        subTaskResponse = manager.updateSubTask(subTask);

        assertEquals(subTaskResponse, subTask, "Подзадача не обновилась");
    }

    @Test
    void shouldBeUpdateEpicAndSendChangedEpicToUpdate() {
        String name = "Test Epic";
        String description = "Test Epic Description";
        Epic epicResponse = manager.createEpic("Another Epic", "Another Description");
        Epic epic = new Epic(epicResponse.getId(), name, description);
        assertNotEquals(epicResponse, epic, "Эпики равны");
        epic.setName(name);
        epic.setDescription(description);

        epicResponse = manager.updateEpic(epic);

        assertEquals(epicResponse, epic, "Эпики не обновилась");
    }

    @Test
    void shouldCorrectlyCalculateStatusEpicIfUpdateStatusesHisSubTasks() {
        String name = "Test Epic";
        String description = "Test Epic Description";
        Epic epicResponse = manager.createEpic("Another Epic", "Another Description");
        assertEquals(epicResponse.getStatus(), Status.NEW, "Статусы не равны");

        SubTask subTaskResponse = manager.createSubTask("Another Subtask", "Another Description",
                epicResponse.getId());
        assertEquals(epicResponse.getStatus(), Status.NEW, "Статусы не равны");

        SubTask subTask = new SubTask(subTaskResponse.getId(), name, description, epicResponse.getId());
        subTask.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask);
        Epic epicById = manager.getEpicById(epicResponse.getId());
        assertEquals(epicById.getStatus(), Status.IN_PROGRESS, "Статусы не равны");

        SubTask subTaskResponse1 = manager.createSubTask("Another Subtask", "Another Description",
                epicResponse.getId());
        assertEquals(epicById.getStatus(), Status.IN_PROGRESS, "Статусы не равны");

        SubTask subTask1 = new SubTask(subTaskResponse1.getId(), name, description, epicResponse.getId());
        subTask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask1);
        assertEquals(epicById.getStatus(), Status.IN_PROGRESS, "Статусы не равны");

        subTask.setStatus(Status.DONE);
        manager.updateSubTask(subTask);
        assertEquals(epicById.getStatus(), Status.IN_PROGRESS, "Статусы не равны");

        subTask1.setStatus(Status.DONE);
        SubTask subTask2 = manager.updateSubTask(subTask1);
        Epic epicById1 = manager.getEpicById(subTask2.getEpicId());
        assertEquals(epicById1.getStatus(), Status.DONE, "Статусы не равны");
    }

    @Test
    void shouldDoNotCrossSubTasks() {
        String name = "Test Epic";
        String description = "Test Epic Description";
        Epic epic = manager.createEpic(name, description);
        SubTask subTask = manager.createSubTask("Another Subtask", "Another Description",
                epic.getId());
        SubTask subTask1 = manager.createSubTask("Another Subtask1", "Another Description1",
                epic.getId());

        subTask.setStartTime(LocalDateTime.of(2024, Month.MARCH, 31, 9, 59));
        subTask.setDuration(Duration.ofMinutes(20));

        subTask1.setStartTime(LocalDateTime.of(2024, Month.MARCH, 31, 9, 59));
        subTask1.setDuration(Duration.ofMinutes(20));

        SubTask subTask3 = manager.updateSubTask(subTask);
        SubTask subTask2 = manager.updateSubTask(subTask1);
        Epic epic1 = manager.updateEpic(epic);
        Epic epicById = manager.getEpicById(epic.getId());


        assertEquals(epicById.getStartTime(), subTask.getStartTime(), "Время Epic будет равно SubTask " +
                "из-за пересечения");

        subTask1.setStartTime(LocalDateTime.of(2024, Month.MARCH, 31, 10, 20));
        subTask1.setDuration(Duration.ofMinutes(20));
        subTask1.setStatus(Status.IN_PROGRESS);
        SubTask subTaskUpdated = manager.updateSubTask(subTask1);
        epic1.setStatus(Status.IN_PROGRESS);
        Epic epicUpdated = manager.updateEpic(epic1);
        Epic epicById1 = manager.getEpicById(epic1.getId());

        assertEquals(epicById1.getStartTime(), subTask3.getStartTime(), "Время начала Epic будет равно началу" +
                "самой ранней SubTask");
        assertEquals(epicById1.getEndTime(), subTaskUpdated.getEndTime(), "Время окончания Epic будет равно" +
                "концу самой поздней SubTask");
        assertEquals(epicById1.getStatus(), subTaskUpdated.getStatus(), "Если у SubTask изменился статус, " +
                "то и у эпика меняем Статус");

        subTask3.setStatus(Status.DONE);

        assertEquals(epicById1.getStatus(), subTaskUpdated.getStatus(), "Если у SubTask изменился статус, " +
                " на DONE то и у эпика остается статус IN_PROGRESS");

        subTaskUpdated.setStatus(Status.DONE);
        epicById1.setStatus(Status.DONE);

        assertEquals(epicById1.getStatus(), subTaskUpdated.getStatus(), "Если у SubTask статус DONE, " +
                " то и у эпика  статус DONE");
    }
}

