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

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ClearblueException {
        int taskIndex = requireValidIndex(tasks, taskNumber, "delete");

        Task removedTask = tasks.remove(taskIndex);
        ui.showTaskRemoved(removedTask, tasks.size());
        storage.save(tasks.asList());
    }
}
