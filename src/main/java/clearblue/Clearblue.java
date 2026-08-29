package clearblue;

import java.time.LocalDate;
import java.util.List;

/**
 * Runs the Clearblue chatbot and responds to task-management commands.
 */
public class Clearblue {
    /**
     * Starts the chatbot: prints the banner and greeting, then reads and
     * responds to commands from standard input until a {@code bye} command
     * is received.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        TaskList tasks = new TaskList(Storage.load());

        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = CommandType.fromCommand(command);
            String commandArguments = getCommandArguments(command, commandType);
            ui.showLine();

            if (commandType == CommandType.BYE && commandArguments.isEmpty()) {
                ui.showBye();
                ui.showLine();
                break;
            }

            switch (commandType) {
            case LIST -> {
                if (!commandArguments.isEmpty()) {
                    ui.showUnknownCommand();
                } else if (tasks.isEmpty()) {
                    ui.showEmptyList();
                } else {
                    ui.showTaskList(tasks);
                }
            }
            case MARK -> updateTaskStatus(tasks, ui, commandArguments, true);
            case UNMARK -> updateTaskStatus(tasks, ui, commandArguments, false);
            case DELETE -> deleteTask(tasks, ui, commandArguments);
            case ON -> {
                if (commandArguments.isEmpty()) {
                    ui.showError("Tell me which date to check. Example: on 2019-06-06");
                } else {
                    LocalDate queryDate = TaskDateTime.parseDate(commandArguments);
                    if (queryDate == null) {
                        ui.showError("The date must be in yyyy-MM-dd format. Example: on 2019-06-06");
                    } else {
                        showTasksOnDate(tasks, ui, queryDate);
                    }
                }
            }
            case TODO -> {
                if (commandArguments.isEmpty()) {
                    ui.showError("A todo needs a description after \"todo\".");
                } else {
                    addTask(tasks, ui, new Todo(commandArguments));
                }
            }
            case DEADLINE -> {
                int byIndex = commandArguments.indexOf("/by");

                if (byIndex < 0) {
                    ui.showError("A deadline needs a /by separator. "
                            + "Example: deadline return book /by Sunday");
                } else {
                    String description = commandArguments.substring(0, byIndex).trim();
                    String by = commandArguments.substring(byIndex + "/by".length()).trim();

                    if (description.isEmpty()) {
                        ui.showError("A deadline needs a description before /by.");
                    } else if (by.isEmpty()) {
                        ui.showError("A deadline needs a date or time after /by.");
                    } else {
                        addTask(tasks, ui, new Deadline(description, by));
                    }
                }
            }
            case EVENT -> {
                int fromIndex = commandArguments.indexOf("/from");

                if (fromIndex < 0) {
                    ui.showError("An event needs a /from separator. "
                            + "Example: event meeting /from 2pm /to 4pm");
                } else {
                    int toIndex = commandArguments.indexOf("/to", fromIndex + "/from".length());

                    if (toIndex < 0) {
                        ui.showError("An event needs a /to separator. "
                                + "Example: event meeting /from 2pm /to 4pm");
                    } else {
                        String description = commandArguments.substring(0, fromIndex).trim();
                        String from = commandArguments.substring(
                                fromIndex + "/from".length(), toIndex).trim();
                        String to = commandArguments.substring(toIndex + "/to".length()).trim();

                        if (description.isEmpty()) {
                            ui.showError("An event needs a description before /from.");
                        } else if (from.isEmpty()) {
                            ui.showError("An event needs a start date or time after /from.");
                        } else if (to.isEmpty()) {
                            ui.showError("An event needs an end date or time after /to.");
                        } else {
                            addTask(tasks, ui, new Event(description, from, to));
                        }
                    }
                }
            }
            case BYE, UNKNOWN -> {
                if (command.isEmpty()) {
                    ui.showError("Please enter a command.");
                } else {
                    ui.showUnknownCommand();
                }
            }
            }

            ui.showLine();
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
     * Adds a task to the list, reports it to the user, and saves the list.
     *
     * @param tasks task list to add to
     * @param ui user interface to report through
     * @param task task to add
     */
    private static void addTask(TaskList tasks, Ui ui, Task task) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        Storage.save(tasks.asList());
    }

    /**
     * Validates a task number, then marks or unmarks that task, reports it
     * to the user, and saves the list.
     *
     * @param tasks task list to update
     * @param ui user interface to report through
     * @param taskNumberText user-provided task number
     * @param isMarkCommand whether the task should be marked as done
     */
    private static void updateTaskStatus(TaskList tasks, Ui ui, String taskNumberText, boolean isMarkCommand) {
        String action = isMarkCommand ? "mark" : "unmark";
        int taskIndex = getValidTaskIndex(tasks.size(), taskNumberText, ui, action);
        if (taskIndex < 0) {
            return;
        }

        Task task = tasks.get(taskIndex);
        if (isMarkCommand) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        ui.showTaskStatusChanged(task, isMarkCommand);
        Storage.save(tasks.asList());
    }

    /**
     * Validates a task number, then removes that task, reports it to the
     * user, and saves the list.
     *
     * @param tasks task list to remove from
     * @param ui user interface to report through
     * @param taskNumberText user-provided task number
     */
    private static void deleteTask(TaskList tasks, Ui ui, String taskNumberText) {
        int taskIndex = getValidTaskIndex(tasks.size(), taskNumberText, ui, "delete");
        if (taskIndex < 0) {
            return;
        }

        Task removedTask = tasks.remove(taskIndex);
        ui.showTaskRemoved(removedTask, tasks.size());
        Storage.save(tasks.asList());
    }

    /**
     * Validates a user-provided task number for an operation.
     *
     * @param taskCount number of tasks currently stored
     * @param taskNumberText user-provided task number
     * @param ui user interface to report a validation error through
     * @param action operation that will use the selected task
     * @return zero-based task index, or {@code -1} when validation fails
     */
    private static int getValidTaskIndex(int taskCount, String taskNumberText, Ui ui, String action) {
        if (taskCount == 0) {
            ui.showError("There are no tasks to " + action + ".");
            return -1;
        }
        if (taskNumberText.isEmpty()) {
            ui.showError("Tell me which task to " + action + ". Example: " + action + " 1");
            return -1;
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            ui.showError("The task number must be a whole number. Example: " + action + " 1");
            return -1;
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            String validRange = taskCount == 1 ? "1" : "1 to " + taskCount;
            ui.showError("Task " + taskNumber + " does not exist. Choose " + validRange + ".");
            return -1;
        }

        return taskNumber - 1;
    }

    /**
     * Reports the deadlines and events on the given date, or reports that
     * there are none.
     *
     * @param tasks task list to search
     * @param ui user interface to report through
     * @param queryDate date to match against
     */
    private static void showTasksOnDate(TaskList tasks, Ui ui, LocalDate queryDate) {
        List<Task> matches = tasks.getTasksOnDate(queryDate);
        String displayDate = TaskDateTime.formatDate(queryDate);
        if (matches.isEmpty()) {
            ui.showNoTasksOnDate(displayDate);
        } else {
            ui.showTasksOnDate(matches, displayDate);
        }
    }
}
