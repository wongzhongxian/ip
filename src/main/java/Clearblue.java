import java.util.Scanner;

/**
 * Runs the Clearblue chatbot and responds to commands entered by the user.
 */
public class Clearblue {
    private static final String DIVIDER = "    ____________________________________________________________";
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        String banner = "   ________                __    __         \n"
                + "  / ____/ /__  ____ ______/ /_  / /_  _____\n"
                + " / /   / / _ \\/ __ `/ ___/ __ \\/ / / / / _ \\\n"
                + "/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/\n"
                + "\\____/_/\\___/\\__,_/_/  /_.___/_/\\__,_/\\___/\n";
        String introMessage = "     Hello! I'm Clearblue.\n"
                + "     What can I do for you? :)";
        System.out.println(banner);
        System.out.println(DIVIDER);
        System.out.println(introMessage);
        System.out.println(DIVIDER);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(DIVIDER);

            if (command.equals("bye")) {
                System.out.println("     Bye. Hope to see you again soon! :)");
                System.out.println(DIVIDER);
                break;
            }

            if (command.equals("list")) {
                System.out.println("     Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println("     " + (i + 1) + "." + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring("mark ".length()));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsDone();
                System.out.println("     Nice! I've marked this task as done:");
                System.out.println("       " + tasks[taskIndex]);
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring("unmark ".length()));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println("     OK, I've marked this task as not done yet:");
                System.out.println("       " + tasks[taskIndex]);
            } else {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println("     added: " + command);
            }

            System.out.println(DIVIDER);
        }
    }
}
