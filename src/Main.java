import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.InMemoryTaskManager;
import service.TaskManager;


public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        System.out.println("Проверка 1: Создайте две задачи, а также " +
                "эпик с двумя подзадачами и эпик с одной подзадачей.");
        Task task1 = inMemoryTaskManager.createTask("Задача простая 1", "Помыть полы");
        Task task2 = inMemoryTaskManager.createTask("Задача простая 2", "Постирать вещи");

        Epic epic1 = inMemoryTaskManager.createEpic("Эпик две подзадачи", "создали эпик");

        SubTask subtask2 = inMemoryTaskManager.createSubTask("Подзадача 1", "Собрать вещи", epic1.getId());
        SubTask subTask3 = inMemoryTaskManager.createSubTask("Подзадача 2", "Упаковать вещи", epic1.getId());

        Epic epic2 = inMemoryTaskManager.createEpic("Эпик одна подзадача", "создали эпик");
        SubTask subTask7 = inMemoryTaskManager.createSubTask("Подзадача 1", "Упаковать чемодан", epic2.getId());

        inMemoryTaskManager.showAllTasks();
        inMemoryTaskManager.showAllEpics();
        inMemoryTaskManager.showAllSubtasks();
        System.out.println("Проверка 1: окончена.");

        System.out.println("Поверка 2: Измените статусы созданных объектов, распечатайте их." +
                " Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался " +
                "по статусам подзадач.");
        task1.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task1);
        task2.setStatus(Status.DONE);
        inMemoryTaskManager.updateTask(task2);

        subtask2.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubTask(subtask2);
        subTask3.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubTask(subTask3);

        subTask7.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubTask(subTask7);

        inMemoryTaskManager.showAllTasks();
        inMemoryTaskManager.showAllEpics();
        inMemoryTaskManager.showAllSubtasks();
        System.out.println("Поверка 2: окончена.");

        System.out.println("Проверка 3: наконец, попробуйте удалить одну из задач и один из эпиков.");
        inMemoryTaskManager.removeTaskById(task2.getId());
        inMemoryTaskManager.removeEpicById(epic1.getId());

        inMemoryTaskManager.showAllTasks();
        inMemoryTaskManager.showAllEpics();
        inMemoryTaskManager.showAllSubtasks();
        System.out.println("Проверка 3 окончена.");


        inMemoryTaskManager.getEpicById(epic1.getId());
       inMemoryTaskManager.getSubTaskById(subtask2.getId());
       inMemoryTaskManager.getTaskById(task1.getId());
       inMemoryTaskManager.getHistoryTasks().add(epic1);
        inMemoryTaskManager.getHistory();
    }
}
