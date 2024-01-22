public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        System.out.println("Проверка 1: Создайте две задачи, а также " +
                "эпик с двумя подзадачами и эпик с одной подзадачей.");
        taskManager.createTask("Задача простая 1", "Помыть полы");
        taskManager.createTask("Задача простая 2", "Постирать вещи");

        taskManager.createEpic("Эпик две подзадачи", "создали эпик");

        taskManager.createSubTask("Подзадача 1", "Собрать вещи",3);
        taskManager.createSubTask("Подзадача 2", "Упаковать вещи", 3);

        taskManager.createEpic("Эпик одна подзадача", "создали эпик");
        taskManager.createSubTask("Подзадача 1", "Упаковать чемодан", 6);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println("Проверка 1: окончена.");

        System.out.println("Поверка 2: Измените статусы созданных объектов, распечатайте их." +
                " Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался " +
                "по статусам подзадач.");
        taskManager.getTaskById(1).setStatus(Status.IN_PROGRESS);
        taskManager.getTaskById(2).setStatus(Status.DONE);

        taskManager.getSubTaskById(4).setStatus(Status.DONE);
        taskManager.updateSubTask(taskManager.getSubTaskById(4));
        taskManager.getSubTaskById(5).setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(taskManager.getSubTaskById(5));

        taskManager.getSubTaskById(7).setStatus(Status.DONE);
        taskManager.updateSubTask(taskManager.getSubTaskById(7));

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println("Поверка 2: окончена.");

        System.out.println("Проверка 3: наконец, попробуйте удалить одну из задач и один из эпиков.");
        taskManager.removeTaskById(2);
        taskManager.removeEpicById(3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println("Проверка 3 окончена.");
    }
}
