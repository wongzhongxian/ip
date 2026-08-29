package clearblue.ui;

import java.util.List;
import java.util.Scanner;

import clearblue.task.Task;
import clearblue.task.TaskList;

/**
 * Handles all interaction with the user: printing messages to standard
 * output and reading commands from standard input.
 */
public class Ui {
    private static final String DIVIDER = "    ____________________________________________________________";

    private final Scanner scanner;

    /**
     * Creates a Ui that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command line is available to read.
     *
     * @return {@code true} if there is another line of input
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and returns the next command line, with surrounding whitespace
     * trimmed.
     *
     * @return the trimmed command line
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints the startup banner and greeting.
     */
    public void showWelcome() {
        String banner = "   ________                __    __\n"
                + "  / ____/ /__  ____ ______/ /_  / /_  _____\n"
                + " / /   / / _ \\/ __ `/ ___/ __ \\/ / / / / _ \\\n"
                + "/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/\n"
                + "\\____/_/\\___/\\__,_/_/  /_.___/_/\\__,_/\\___/\n";
        String introMessage = "     Hello! I'm Clearblue.\n"
                + "     What can I do for you? :)";
        System.out.println(banner);
        showLine();
        System.out.println(introMessage);
        showLine();
    }

    /**
     * Prints the divider line used to separate one command's output from
     * the next.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Prints the farewell message shown when the chatbot exits.
     */
    public void showBye() {
        System.out.println("     Bye. Hope to see you again soon! :)");
    }

    /**
     * Prints an error in a consistent, user-friendly format.
     *
     * @param message explanation of the error and, where useful, how to correct it
     */
    public void showError(String message) {
        System.out.println("     OOPS!!! " + message);
    }

    /**
     * Prints the list of supported commands after an unknown command.
     */
    public void showUnknownCommand() {
        showError("I don't recognize that command. "
                + "Try todo, deadline, event, list, mark, unmark, delete, on, or bye.");
    }

    /**
     * Prints confirmation after a task is added.
     *
     * @param task task that was added
     * @param taskCount total number of tasks after adding the task
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Prints confirmation after a task is removed.
     *
     * @param task task that was removed
     * @param taskCount total number of tasks remaining after removal
     */
    public void showTaskRemoved(Task task, int taskCount) {
        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Prints confirmation after a task's done status changes.
     *
     * @param task task whose status changed
     * @param isDone {@code true} if the task was just marked done
     */
    public void showTaskStatusChanged(Task task, boolean isDone) {
        System.out.println(isDone
                ? "     Nice! I've marked this task as done:"
                : "     OK, I've marked this task as not done yet:");
        System.out.println("       " + task);
    }

    /**
     * Prints a message noting that the task list has no tasks.
     */
    public void showEmptyList() {
        System.out.println("     Your task list is empty.");
    }

    /**
     * Prints all tasks in the list, numbered from 1.
     *
     * @param tasks tasks to print
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints a message noting that no deadlines or events fall on the
     * given date.
     *
     * @param displayDate formatted date that was searched for
     */
    public void showNoTasksOnDate(String displayDate) {
        System.out.println("     There are no deadlines or events on " + displayDate + ".");
    }

    /**
     * Prints the deadlines and events that fall on the given date, numbered
     * from 1.
     *
     * @param matches matching tasks
     * @param displayDate formatted date that was searched for
     */
    public void showTasksOnDate(List<Task> matches, String displayDate) {
        System.out.println("     Here are the deadlines and events on " + displayDate + ":");
        for (int i = 0; i < matches.size(); i++) {
            System.out.println("     " + (i + 1) + "." + matches.get(i));
        }
    }
}
