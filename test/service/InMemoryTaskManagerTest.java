package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private final TaskManager manager = Managers.getDefault();

    @Test
    void inMemoryTaskManagerShouldAddTasksAllTypes() {
        String name = "Name";
        String description = "Description";

        Task task1 = manager.createTask(name, description);
        Epic epic1 = manager.createEpic(name, description);
        SubTask subTask1 = manager.createSubTask(name, description, epic1.getId());

        assertNotNull(task1, "Менеджер не содержит Тип Задача");
        assertNotNull(epic1, "Менеджер не содержит Тип Эпик");
        assertNotNull(subTask1, "Менеджер не содержит Тип Подзадача");
        assertNotNull(manager.getTaskById(task1.getId()), "Менеджер не находит Задачу по ID");
        assertEquals(manager.getTaskById(task1.getId()), task1, "Таски не равны");
        assertEquals(manager.getTaskById(task1.getId()), task1, "Таски не равны");
        assertNotNull(manager.getEpicById(epic1.getId()), "Менеджер не находит Эпик по ID");
        assertEquals(manager.getEpicById(epic1.getId()), epic1, "Эпики не равны");
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
        int subTaskId = responseEpic.getSubTasksListInEpic().get(0);

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

        assertNotEquals(epicResponse.getId(), 0 ,"Id Эпика равен нулю");
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
        assertEquals(epicResponse.getStatus(), Status.IN_PROGRESS, "Статусы не равны");

        SubTask subTaskResponse1 = manager.createSubTask("Another Subtask", "Another Description",
                epicResponse.getId());
        assertEquals(epicResponse.getStatus(), Status.IN_PROGRESS, "Статусы не равны");

        SubTask subTask1 = new SubTask(subTaskResponse1.getId(), name, description, epicResponse.getId());
        subTask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask1);
        assertEquals(epicResponse.getStatus(), Status.IN_PROGRESS, "Статусы не равны");

        subTask.setStatus(Status.DONE);
        manager.updateSubTask(subTask);
        assertEquals(epicResponse.getStatus(), Status.IN_PROGRESS, "Статусы не равны");

        subTask1.setStatus(Status.DONE);
        manager.updateSubTask(subTask1);
        assertEquals(epicResponse.getStatus(), Status.DONE, "Статусы не равны");
    }

    @Test
    @DisplayName("Мы напрямую работаем с InMemoryTaskManager, а не с HistoryManager." +
            "Поэтому вся логика работы HistoryManager находится здесь в InMemoryTaskManager. ")
    void shouldCorrectlyCalculateHistoryIfGetTasksByIds() {
        String name = "Name";
        String desc = "Desc";
        int maxSize = 10;
        Task task1 = manager.createTask(name, desc);
        manager.getTaskById(task1.getId());
        Task task2 = manager.createTask(name + 1, desc + 1);
        manager.getTaskById(task2.getId());
        Task task3 = manager.createTask(name + 2, desc + 2);
        manager.getTaskById(task3.getId());
        Task task4 = manager.createTask(name + 3, desc + 3);
        manager.getTaskById(task4.getId());
        Task task5 = manager.createTask(name + 4, desc + 4);
        manager.getTaskById(task5.getId());
        Task task6 = manager.createTask(name + 5, desc + 5);
        manager.getTaskById(task6.getId());
        Task task7 = manager.createTask(name + 6, desc + 6);
        manager.getTaskById(task7.getId());
        Task task8 = manager.createTask(name + 7, desc + 7);
        manager.getTaskById(task8.getId());
        Task task9 = manager.createTask(name + 8, desc + 8);
        manager.getTaskById(task9.getId());
        Task task10 = manager.createTask(name + 9, desc + 9);
        manager.getTaskById(task10.getId());

        assertEquals(manager.getHistory().size(), maxSize, "Размеры истории не совпадают");
        assertEquals(manager.getHistory().get(0), task1, "Первая таска истории не совпадает с реальной");
        assertEquals(manager.getHistory().get(9), task10, "Последняя таска истории не совпадает с реальной");

        Epic epic11 = manager.createEpic(name + 10, desc + 10);
        manager.getEpicById(epic11.getId());

        assertEquals(manager.getHistory().size(), maxSize, "Размеры истории не совпадают");
        assertEquals(manager.getHistory().get(0), task2, "Первая таска истории не совпадает с реальной");
        assertEquals(manager.getHistory().get(9), epic11, "Последняя таска истории не совпадает с реальной");

        //убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
        Task task12 = new Task(task3.getId(), name + 11, desc + 11);
        manager.updateTask(task12);
        manager.getTaskById(task12.getId());

        assertEquals(manager.getHistory().size(), maxSize, "Размеры истории не совпадают");
        assertEquals(manager.getHistory().get(0), task3, "Первая таска истории не совпадает с реальной");
        assertEquals(manager.getHistory().get(9), task12, "Последняя таска истории не совпадает с реальной");
    }
}

