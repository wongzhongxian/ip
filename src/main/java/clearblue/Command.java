package clearblue;

/**
 * Represents one user command: something a chatbot input line resolves to
 * once it has been parsed, ready to be carried out against the task list.
 */
public abstract class Command {
    /**
     * Carries out this command: reading or changing {@code tasks} and
     * reporting through {@code ui}, saving via {@code storage} if the task
     * list changed.
     *
     * @param tasks task list to read or modify
     * @param ui user interface to report through
     * @param storage storage to persist any changes through
     * @throws ClearblueException if the command's target is invalid, or a save fails
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ClearblueException;

    /**
     * Returns whether this command should end the chatbot's session.
     *
     * @return {@code true} for {@link ExitCommand}, {@code false} otherwise
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Resolves a user-provided task number to a zero-based index, checking
     * it against the current task list. Shared by commands that target one
     * existing task by number.
     *
     * @param tasks task list the index must fall within
     * @param taskNumber one-based task number to validate
     * @param action operation that will use the selected task, for the error message
     * @return zero-based task index
     * @throws ClearblueException if there are no tasks, or the number is out of range
     */
    protected static int requireValidIndex(TaskList tasks, int taskNumber, String action)
            throws ClearblueException {
        if (tasks.isEmpty()) {
            throw new ClearblueException("There are no tasks to " + action + ".");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            String validRange = tasks.size() == 1 ? "1" : "1 to " + tasks.size();
            throw new ClearblueException("Task " + taskNumber + " does not exist. Choose " + validRange + ".");
        }
        return taskNumber - 1;
    }
}
