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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.TreeSet;


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
        System.out.println("\n Проверка 1: окончена.");

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
                new File("src\\resources\\task1.txt"));


        Task task = fileBackedTaskManager.createTask("Проверка таски", "Сохранилась ли таска в файл");
        Task task3 = fileBackedTaskManager.createTask("Проверка таски1", "Сохранилась ли таска в файл1");
        Epic epic = fileBackedTaskManager.createEpic("Проверка Эпика", "Сохранился ли в файл эпик");
        fileBackedTaskManager.createEpic("Проверка Эпика", "Сохранился ли в файл эпик");
        fileBackedTaskManager.createEpic("Проверка Эпика", "Сохранился ли в файл эпик");
        SubTask subTask = fileBackedTaskManager.createSubTask("Проверка СабТаски", "Сохарнился ли файл", epic.getId());
        SubTask subTask1 = fileBackedTaskManager.createSubTask("Проверка Сабтаски1", "Соханился ли файл1", epic.getId());
        fileBackedTaskManager.getSubTaskById(subTask.getId());
        fileBackedTaskManager.getEpicById(epic.getId());

        System.out.println("Проверка на пересечение \n \n");
        subTask.setStartTime(LocalDateTime.of(2024, Month.MARCH, 31, 9, 40));
        subTask.setDuration(Duration.ofMinutes(20));
        subTask1.setStartTime(LocalDateTime.of(2024, Month.MARCH, 31, 10, 1));
        subTask1.setDuration(Duration.ofMinutes(10));
        fileBackedTaskManager.updateSubTask(subTask);
        fileBackedTaskManager.updateSubTask(subTask1);
        task.setStartTime(LocalDateTime.of(2024, Month.MARCH, 31, 9, 40));
        task.setDuration(Duration.ofMinutes(20));
        task3.setStartTime(LocalDateTime.of(2024, Month.MARCH, 31, 10, 1));
        task3.setDuration(Duration.ofMinutes(10));
        fileBackedTaskManager.updateTask(task);
        Task task4 = fileBackedTaskManager.updateTask(task3);

        Task task5 = fileBackedTaskManager.createTask("Проверка Таски2", "Сохранилалсь ли таска 2", LocalDateTime.of(
                2024, Month.MARCH, 31, 9, 59), Duration.ofMinutes(5));
        fileBackedTaskManager.updateTask(task5);

        System.out.println("Проверка хранения \n\n" + fileBackedTaskManager.getPrioritizedTasks());


        Epic epic3 = fileBackedTaskManager.updateEpic(epic);
        System.out.println(epic3);

        String s = "id,type,name,status,description,epic,\n" +
                "11,SUBTASK,Проверка СабТаски,NEW,Сохарнился ли файл,8,\n" +
                "8,EPIC,Проверка Эпика,NEW,Сохранился ли в файл эпик,null,\n" +
                "9,EPIC,Проверка Эпика,NEW,Сохранился ли в файл эпик,null,\n" +
                "10,EPIC,Проверка Эпика,NEW,Сохранился ли в файл эпик,null,\n" +
                "\n" +
                "8,11,";



        System.out.println("Проверка на загрузку из файла : \n");


        FileBackedTaskManager fileBackedTaskManager1 = TaskManagerFactory.createFileBackedTaskManager(new
                File("src\\resources\\task1.txt"));
        /*System.out.println("История загрузки \n\n" + fileBackedTaskManager1.getHistory());
        System.out.println("Получить все Task \n\n" + fileBackedTaskManager1.getAllTasks());
        System.out.println("Получить все SubTasks \n\n" + fileBackedTaskManager1.getAllSubTasks());
        System.out.println("Получить все Epics \n\n" + fileBackedTaskManager1.getAllEpics());*/
        Task loadTask = fileBackedTaskManager1.getAllTasks().get(task.getId());
        System.out.println(loadTask.getStartTime());
        System.out.println(loadTask.getDuration());

        TreeSet<Task> prioritizedTasks = fileBackedTaskManager1.getPrioritizedTasks();

        System.out.println(prioritizedTasks);

    }
}
