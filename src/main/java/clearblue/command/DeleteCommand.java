package clearblue.command;

import clearblue.ClearblueException;
import clearblue.storage.Storage;
import clearblue.task.Task;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Removes a task by its one-based list number, reports it to the user, and
 * saves the list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that removes the given task number.
     *
     * @param taskNumber one-based task number
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes the task at this command's task number from {@code tasks},
     * reports it through {@code ui}, and saves the updated list via
     * {@code storage}.
     *
     * @param tasks task list to remove from
     * @param ui user interface to report through
     * @param storage storage to persist the change through
     * @throws ClearblueException if the task number is invalid, or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ClearblueException {
        int taskIndex = requireValidIndex(tasks, taskNumber, "delete");

        Task removedTask = tasks.remove(taskIndex);
        ui.showTaskRemoved(removedTask, tasks.size());
        storage.save(tasks.asList());
    }
}
