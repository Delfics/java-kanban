package ru.yandex.kanban;

import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;
import ru.yandex.kanban.service.FileBackedTaskManager;
import ru.yandex.kanban.service.Managers;
import ru.yandex.kanban.service.TaskManager;
import ru.yandex.kanban.service.TaskManagerFactory;

import java.io.File;


public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        System.out.println("Проверка 1: Создайте две задачи, а также " +
                "эпик с двумя подзадачами и эпик с одной подзадачей.");
        Task task1 = taskManager.createTask("Задача простая 1", "Помыть полы");
        Task task2 = taskManager.createTask("Задача простая 2", "Постирать вещи");

        Epic epic1 = taskManager.createEpic("Эпик две подзадачи", "создали эпик");

        SubTask subtask2 = taskManager.createSubTask("Подзадача 1", "Собрать вещи", epic1.getId());
        SubTask subTask3 = taskManager.createSubTask("Подзадача 2", "Упаковать вещи", epic1.getId());

        Epic epic2 = taskManager.createEpic("Эпик одна подзадача", "создали эпик");
        SubTask subTask7 = taskManager.createSubTask("Подзадача 1", "Упаковать чемодан", epic2.getId());

        taskManager.showAllTasks();
        taskManager.showAllEpics();
        taskManager.showAllSubtasks();
        System.out.println("Проверка 1: окончена.");

        System.out.println("Поверка 2: Измените статусы созданных объектов, распечатайте их." +
                " Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался " +
                "по статусам подзадач.");
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        task2.setStatus(Status.DONE);
        taskManager.updateTask(task2);

        subtask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subtask2);
        subTask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask3);

        subTask7.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask7);

        taskManager.showAllTasks();
        taskManager.showAllEpics();
        taskManager.showAllSubtasks();
        System.out.println("Поверка 2: окончена.");

        System.out.println("Проверка 3: наконец, попробуйте удалить одну из задач и один из эпиков.");
        taskManager.removeTaskById(task2.getId());
        taskManager.removeEpicById(epic1.getId());

        taskManager.showAllTasks();
        taskManager.showAllEpics();
        taskManager.showAllSubtasks();
        System.out.println("Проверка 3 окончена.");

/*        taskManager.getSubTaskById(subTask7.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask3.getId());
        taskManager.getHistory();*/

        System.out.println("Проверка 4: Проверяю новые добавления");
        taskManager.getTaskById(task1.getId());
        taskManager.getSubTaskById(subTask3.getId());
        taskManager.getEpicById(epic2.getId());

        System.out.println("Проверка getTasks " + taskManager.getInMemoryHistoryManager().getHistory());

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(
                new File("C:\\Users\\USER\\IdeaProjects\\java-kanban\\resources\\task.txt"));


        fileBackedTaskManager.createTask("Проверка таски", "Сохранилась ли таска в файл");
        Epic epic = fileBackedTaskManager.createEpic("Проверка Эпика", "Сохранился ли в файл эпик");
        fileBackedTaskManager.createEpic("Проверка Эпика", "Сохранился ли в файл эпик");
        fileBackedTaskManager.createEpic("Проверка Эпика", "Сохранился ли в файл эпик");
        SubTask subTask = fileBackedTaskManager.createSubTask("Проверка СабТаски", "Сохарнился ли файл", epic.getId());
        fileBackedTaskManager.getSubTaskById(subTask.getId());
        fileBackedTaskManager.getEpicById(epic.getId());

        String s = "id,type,name,status,description,epic,\n" +
                "11,SUBTASK,Проверка СабТаски,NEW,Сохарнился ли файл,8,\n" +
                "8,EPIC,Проверка Эпика,NEW,Сохранился ли в файл эпик,null,\n" +
                "9,EPIC,Проверка Эпика,NEW,Сохранился ли в файл эпик,null,\n" +
                "10,EPIC,Проверка Эпика,NEW,Сохранился ли в файл эпик,null,\n" +
                "\n" +
                "8,11,";

        /*   System.out.println("Проверка тасок " + fileBackedTaskManager.fromString());*/


        System.out.println("Проверка на загрузку из файла : \n");


        FileBackedTaskManager fileBackedTaskManager1 = TaskManagerFactory.createFileBackedTaskManager(new
                File("C:\\Users\\USER\\IdeaProjects\\java-kanban\\resources\\task.txt"));
        System.out.println("История загрузки \n\n" + fileBackedTaskManager1.getHistory());
        System.out.println("Получить все Task \n\n" + fileBackedTaskManager1.getAllTasks());
        System.out.println("Получить все SubTasks \n\n" + fileBackedTaskManager1.getAllSubTasks());
        System.out.println("Получить все Epics \n\n" + fileBackedTaskManager1.getAllEpics());
    }
}
