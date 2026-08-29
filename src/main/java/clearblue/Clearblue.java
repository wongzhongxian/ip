package clearblue;

import java.time.LocalDate;
import java.util.List;

/**
 * Runs the Clearblue chatbot and responds to task-management commands.
 */
public class Clearblue {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Creates the chatbot, loading any previously saved tasks from the
     * given file.
     *
     * @param filePath path to the save file, relative to the project root
     */
    public Clearblue(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (ClearblueException exception) {
            ui.showError(exception.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Prints the banner and greeting, then reads and responds to commands
     * from standard input until a {@code bye} command is received.
     */
    public void run() {
        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = Parser.parseCommandType(command);
            String commandArguments = Parser.parseArguments(command, commandType);
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
            case MARK -> updateTaskStatus(commandArguments, true);
            case UNMARK -> updateTaskStatus(commandArguments, false);
            case DELETE -> deleteTask(commandArguments);
            case ON -> {
                if (commandArguments.isEmpty()) {
                    ui.showError("Tell me which date to check. Example: on 2019-06-06");
                } else {
                    LocalDate queryDate = TaskDateTime.parseDate(commandArguments);
                    if (queryDate == null) {
                        ui.showError("The date must be in yyyy-MM-dd format. Example: on 2019-06-06");
                    } else {
                        showTasksOnDate(queryDate);
                    }
                }
            }
            case TODO -> {
                if (commandArguments.isEmpty()) {
                    ui.showError("A todo needs a description after \"todo\".");
                } else {
                    addTask(new Todo(commandArguments));
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
                        addTask(new Deadline(description, by));
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
                            addTask(new Event(description, from, to));
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
     * Starts the chatbot.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        new Clearblue("data/clearblue.txt").run();
    }

    /**
     * Adds a task to the list, reports it to the user, and saves the list.
     *
     * @param task task to add
     */
    private void addTask(Task task) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        saveQuietly();
    }

    /**
     * Validates a task number, then marks or unmarks that task, reports it
     * to the user, and saves the list.
     *
     * @param taskNumberText user-provided task number
     * @param isMarkCommand whether the task should be marked as done
     */
    private void updateTaskStatus(String taskNumberText, boolean isMarkCommand) {
        String action = isMarkCommand ? "mark" : "unmark";
        int taskIndex = getValidTaskIndex(taskNumberText, action);
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
        saveQuietly();
    }

    /**
     * Validates a task number, then removes that task, reports it to the
     * user, and saves the list.
     *
     * @param taskNumberText user-provided task number
     */
    private void deleteTask(String taskNumberText) {
        int taskIndex = getValidTaskIndex(taskNumberText, "delete");
        if (taskIndex < 0) {
            return;
        }

        Task removedTask = tasks.remove(taskIndex);
        ui.showTaskRemoved(removedTask, tasks.size());
        saveQuietly();
    }

    /**
     * Validates a user-provided task number for an operation.
     *
     * @param taskNumberText user-provided task number
     * @param action operation that will use the selected task
     * @return zero-based task index, or {@code -1} when validation fails
     */
    private int getValidTaskIndex(String taskNumberText, String action) {
        int taskCount = tasks.size();
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
     * @param queryDate date to match against
     */
    private void showTasksOnDate(LocalDate queryDate) {
        List<Task> matches = tasks.getTasksOnDate(queryDate);
        String displayDate = TaskDateTime.formatDate(queryDate);
        if (matches.isEmpty()) {
            ui.showNoTasksOnDate(displayDate);
        } else {
            ui.showTasksOnDate(matches, displayDate);
        }
    }

    /**
     * Saves the task list, reporting a failure through {@code ui} instead
     * of letting it crash the chatbot.
     */
    private void saveQuietly() {
        try {
            storage.save(tasks.asList());
        } catch (ClearblueException exception) {
            ui.showError(exception.getMessage());
        }
    }
}
