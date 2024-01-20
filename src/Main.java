import java.util.ArrayList;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        taskManager.addSubTask(new SubTask("Задача 1", "постирать носки", Status.NEW));
        taskManager.addSubTask(new SubTask("Задача 2", "помыть ноги", Status.NEW));
        taskManager.addEpic(new Epic("Большая задача 1"), 1);
     //   taskManager.showSubTasks();
        taskManager.getEpicId(3).getDescription().add(taskManager.getSubTaskId(2));
        taskManager.showEpic();
    }
}
