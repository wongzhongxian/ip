package clearblue;

import java.util.Scanner;

/**
 * Runs the Clearblue chatbot and responds to task-management commands.
 */
public class Clearblue {
    private static final String DIVIDER = "    ____________________________________________________________";
    private static final int MAX_TASKS = 100;

    /**
     * Starts the chatbot: prints the banner and greeting, then reads and
     * responds to commands from standard input until a {@code bye} command
     * is received.
     *
     * @param args unused
     */
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
        int taskCount = Storage.load(tasks);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            CommandType commandType = CommandType.fromCommand(command);
            String commandArguments = getCommandArguments(command, commandType);
            System.out.println(DIVIDER);

            if (commandType == CommandType.BYE && commandArguments.isEmpty()) {
                System.out.println("     Bye. Hope to see you again soon! :)");
                System.out.println(DIVIDER);
                break;
            }

            switch (commandType) {
            case LIST -> {
                if (!commandArguments.isEmpty()) {
                    printUnknownCommand();
                } else if (taskCount == 0) {
                    System.out.println("     Your task list is empty.");
                } else {
                    System.out.println("     Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println("     " + (i + 1) + "." + tasks[i]);
                    }
                }
            }
            case MARK -> updateTaskStatus(tasks, taskCount, commandArguments, true);
            case UNMARK -> updateTaskStatus(tasks, taskCount, commandArguments, false);
            case DELETE -> taskCount = deleteTask(tasks, taskCount, commandArguments);
            case TODO -> {
                if (commandArguments.isEmpty()) {
                    printError("A todo needs a description after \"todo\".");
                } else {
                    taskCount = addTask(tasks, taskCount, new Todo(commandArguments));
                }
            }
            case DEADLINE -> {
                int byIndex = commandArguments.indexOf("/by");

                if (byIndex < 0) {
                    printError("A deadline needs a /by separator. "
                            + "Example: deadline return book /by Sunday");
                } else {
                    String description = commandArguments.substring(0, byIndex).trim();
                    String by = commandArguments.substring(byIndex + "/by".length()).trim();

                    if (description.isEmpty()) {
                        printError("A deadline needs a description before /by.");
                    } else if (by.isEmpty()) {
                        printError("A deadline needs a date or time after /by.");
                    } else {
                        taskCount = addTask(tasks, taskCount, new Deadline(description, by));
                    }
                }
            }
            case EVENT -> {
                int fromIndex = commandArguments.indexOf("/from");

                if (fromIndex < 0) {
                    printError("An event needs a /from separator. "
                            + "Example: event meeting /from 2pm /to 4pm");
                } else {
                    int toIndex = commandArguments.indexOf("/to", fromIndex + "/from".length());

                    if (toIndex < 0) {
                        printError("An event needs a /to separator. "
                                + "Example: event meeting /from 2pm /to 4pm");
                    } else {
                        String description = commandArguments.substring(0, fromIndex).trim();
                        String from = commandArguments.substring(
                                fromIndex + "/from".length(), toIndex).trim();
                        String to = commandArguments.substring(toIndex + "/to".length()).trim();

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
            }
            case BYE, UNKNOWN -> {
                if (command.isEmpty()) {
                    printError("Please enter a command.");
                } else {
                    printUnknownCommand();
                }
            }
            }

            System.out.println(DIVIDER);
        }
    }

    /**
     * Returns the part of a command that follows its command word.
     *
     * @param command complete command entered by the user
     * @param commandType parsed command type
     * @return trimmed command arguments, or the original input for an unknown command
     */
    private static String getCommandArguments(String command, CommandType commandType) {
        if (commandType == CommandType.UNKNOWN) {
            return command;
        }
        return command.substring(commandType.getCommandWord().length()).trim();
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
        Storage.save(tasks, newTaskCount);
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
        int taskIndex = getValidTaskIndex(taskCount, taskNumberText, action);
        if (taskIndex < 0) {
            return;
        }

        Task task = tasks[taskIndex];
        if (isMarkCommand) {
            task.markAsDone();
            System.out.println("     Nice! I've marked this task as done:");
        } else {
            task.markAsNotDone();
            System.out.println("     OK, I've marked this task as not done yet:");
        }
        System.out.println("       " + task);
        Storage.save(tasks, taskCount);
    }

    /**
     * Removes a selected task, shifts later tasks forward, and returns the new task count.
     *
     * @param tasks task storage
     * @param taskCount number of tasks currently stored
     * @param taskNumberText user-provided task number
     * @return the number of stored tasks after the operation
     */
    private static int deleteTask(Task[] tasks, int taskCount, String taskNumberText) {
        int taskIndex = getValidTaskIndex(taskCount, taskNumberText, "delete");
        if (taskIndex < 0) {
            return taskCount;
        }

        Task removedTask = tasks[taskIndex];
        for (int i = taskIndex; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[taskCount - 1] = null;
        int newTaskCount = taskCount - 1;

        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + removedTask);
        System.out.println("     Now you have " + newTaskCount + " tasks in the list.");
        Storage.save(tasks, newTaskCount);
        return newTaskCount;
    }

    /**
     * Validates a user-provided task number for an operation.
     *
     * @param taskCount number of tasks currently stored
     * @param taskNumberText user-provided task number
     * @param action operation that will use the selected task
     * @return zero-based task index, or {@code -1} when validation fails
     */
    private static int getValidTaskIndex(int taskCount, String taskNumberText, String action) {
        if (taskCount == 0) {
            printError("There are no tasks to " + action + ".");
            return -1;
        }
        if (taskNumberText.isEmpty()) {
            printError("Tell me which task to " + action + ". Example: " + action + " 1");
            return -1;
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            printError("The task number must be a whole number. Example: " + action + " 1");
            return -1;
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            String validRange = taskCount == 1 ? "1" : "1 to " + taskCount;
            printError("Task " + taskNumber + " does not exist. Choose " + validRange + ".");
            return -1;
        }

        return taskNumber - 1;
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

    /**
     * Prints the list of supported commands after an unknown command.
     */
    private static void printUnknownCommand() {
        printError("I don't recognize that command. "
                + "Try todo, deadline, event, list, mark, unmark, delete, or bye.");
    }
}
