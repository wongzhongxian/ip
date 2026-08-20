import java.util.Scanner;

/**
 * Runs the Clearblue chatbot and responds to task-management commands.
 */
public class Clearblue {
    private static final String DIVIDER = "    ____________________________________________________________";
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        String banner = "   ________                __    __\n"
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
            String command = scanner.nextLine().trim();
            System.out.println(DIVIDER);

            if (command.equals("bye")) {
                System.out.println("     Bye. Hope to see you again soon! :)");
                System.out.println(DIVIDER);
                break;
            }

            if (command.isEmpty()) {
                printError("Please enter a command.");
            } else if (command.equals("list")) {
                if (taskCount == 0) {
                    System.out.println("     Your task list is empty.");
                } else {
                    System.out.println("     Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println("     " + (i + 1) + "." + tasks[i]);
                    }
                }
            } else if (command.equals("mark") || command.startsWith("mark ")) {
                String taskNumber = command.substring("mark".length()).trim();
                updateTaskStatus(tasks, taskCount, taskNumber, true);
            } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                String taskNumber = command.substring("unmark".length()).trim();
                updateTaskStatus(tasks, taskCount, taskNumber, false);
            } else if (command.equals("todo")) {
                printError("A todo needs a description after \"todo\".");
            } else if (command.startsWith("todo ")) {
                String description = command.substring("todo ".length()).trim();
                if (description.isEmpty()) {
                    printError("A todo needs a description after \"todo\".");
                } else {
                    taskCount = addTask(tasks, taskCount, new Todo(description));
                }
            } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                String details = command.substring("deadline".length()).trim();
                int byIndex = details.indexOf("/by");

                if (byIndex < 0) {
                    printError("A deadline needs a /by separator. "
                            + "Example: deadline return book /by Sunday");
                } else {
                    String description = details.substring(0, byIndex).trim();
                    String by = details.substring(byIndex + "/by".length()).trim();

                    if (description.isEmpty()) {
                        printError("A deadline needs a description before /by.");
                    } else if (by.isEmpty()) {
                        printError("A deadline needs a date or time after /by.");
                    } else {
                        taskCount = addTask(tasks, taskCount, new Deadline(description, by));
                    }
                }
            } else if (command.equals("event") || command.startsWith("event ")) {
                String details = command.substring("event".length()).trim();
                int fromIndex = details.indexOf("/from");

                if (fromIndex < 0) {
                    printError("An event needs a /from separator. "
                            + "Example: event meeting /from 2pm /to 4pm");
                } else {
                    int toIndex = details.indexOf("/to", fromIndex + "/from".length());

                    if (toIndex < 0) {
                        printError("An event needs a /to separator. "
                                + "Example: event meeting /from 2pm /to 4pm");
                    } else {
                        String description = details.substring(0, fromIndex).trim();
                        String from = details.substring(fromIndex + "/from".length(), toIndex).trim();
                        String to = details.substring(toIndex + "/to".length()).trim();

                        if (description.isEmpty()) {
                            printError("An event needs a description before /from.");
                        } else if (from.isEmpty()) {
                            printError("An event needs a start date or time after /from.");
                        } else if (to.isEmpty()) {
                            printError("An event needs an end date or time after /to.");
                        } else {
                            taskCount = addTask(tasks, taskCount, new Event(description, from, to));
                        }
                    }
                }
            } else {
                printError("I don't recognize that command. "
                        + "Try todo, deadline, event, list, mark, unmark, or bye.");
            }

            System.out.println(DIVIDER);
        }
    }

    /**
     * Adds a task when there is space and returns the new task count.
     *
     * @param tasks task storage
     * @param taskCount number of tasks currently stored
     * @param task task to add
     * @return the number of stored tasks after the operation
     */
    private static int addTask(Task[] tasks, int taskCount, Task task) {
        if (taskCount >= tasks.length) {
            printError("The task list is full; no more than " + tasks.length + " tasks can be stored.");
            return taskCount;
        }

        tasks[taskCount] = task;
        int newTaskCount = taskCount + 1;
        printTaskAdded(task, newTaskCount);
        return newTaskCount;
    }

    /**
     * Validates a task number before marking or unmarking the selected task.
     *
     * @param tasks task storage
     * @param taskCount number of tasks currently stored
     * @param taskNumberText user-provided task number
     * @param isMarkCommand whether the task should be marked as done
     */
    private static void updateTaskStatus(Task[] tasks, int taskCount, String taskNumberText,
                                         boolean isMarkCommand) {
        String action = isMarkCommand ? "mark" : "unmark";
        if (taskCount == 0) {
            printError("There are no tasks to " + action + ".");
            return;
        }
        if (taskNumberText.isEmpty()) {
            printError("Tell me which task to " + action + ". Example: " + action + " 1");
            return;
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            printError("The task number must be a whole number. Example: " + action + " 1");
            return;
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            String validRange = taskCount == 1 ? "1" : "1 to " + taskCount;
            printError("Task " + taskNumber + " does not exist. Choose " + validRange + ".");
            return;
        }

        Task task = tasks[taskNumber - 1];
        if (isMarkCommand) {
            task.markAsDone();
            System.out.println("     Nice! I've marked this task as done:");
        } else {
            task.markAsNotDone();
            System.out.println("     OK, I've marked this task as not done yet:");
        }
        System.out.println("       " + task);
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

    /**
     * Prints an error in a consistent, user-friendly format.
     *
     * @param message explanation of the error and, where useful, how to correct it
     */
    private static void printError(String message) {
        System.out.println("     OOPS!!! " + message);
    }
}
