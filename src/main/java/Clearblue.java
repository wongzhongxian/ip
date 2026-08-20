import java.util.Scanner;

/**
 * Runs the Clearblue chatbot and responds to task-management commands.
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
            } else if (command.startsWith("todo ")) {
                String description = command.substring("todo ".length()).trim();
                if (description.isEmpty()) {
                    System.out.println("     The description of a todo cannot be empty.");
                } else {
                    tasks[taskCount] = new Todo(description);
                    taskCount++;
                    printTaskAdded(tasks[taskCount - 1], taskCount);
                }
            } else if (command.startsWith("deadline ")) {
                String details = command.substring("deadline ".length()).trim();
                int byIndex = details.indexOf(" /by ");

                if (byIndex < 0) {
                    System.out.println("     Use: deadline DESCRIPTION /by DATE_OR_TIME");
                } else {
                    String description = details.substring(0, byIndex).trim();
                    String by = details.substring(byIndex + " /by ".length()).trim();

                    if (description.isEmpty() || by.isEmpty()) {
                        System.out.println("     A deadline needs both a description and a /by value.");
                    } else {
                        tasks[taskCount] = new Deadline(description, by);
                        taskCount++;
                        printTaskAdded(tasks[taskCount - 1], taskCount);
                    }
                }
            } else if (command.startsWith("event ")) {
                String details = command.substring("event ".length()).trim();
                int fromIndex = details.indexOf(" /from ");
                int toIndex = details.indexOf(" /to ", fromIndex + 1);

                if (fromIndex < 0 || toIndex < 0) {
                    System.out.println("     Use: event DESCRIPTION /from START /to END");
                } else {
                    String description = details.substring(0, fromIndex).trim();
                    String from = details.substring(fromIndex + " /from ".length(), toIndex).trim();
                    String to = details.substring(toIndex + " /to ".length()).trim();

                    if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                        System.out.println("     An event needs a description, a /from value, and a /to value.");
                    } else {
                        tasks[taskCount] = new Event(description, from, to);
                        taskCount++;
                        printTaskAdded(tasks[taskCount - 1], taskCount);
                    }
                }
            } else {
                System.out.println("     I don't understand that command.");
            }

            System.out.println(DIVIDER);
        }
    }

    /**
     * Prints confirmation after a task is added.
     *
     * @param task task that was added
     * @param taskCount total number of tasks after adding the task
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
    }
}
