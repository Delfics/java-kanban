import java.util.ArrayList;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        taskManager.createEpic(new Epic("Переезд", "Список задач переезда"));
        taskManager.createSubTask(new SubTask("Задача 1", "собрать вещи" ,1));
        taskManager.showEpics();

    }
}
