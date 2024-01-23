import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
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
    }
}
