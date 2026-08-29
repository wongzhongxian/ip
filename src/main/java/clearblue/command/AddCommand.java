package clearblue.command;

import clearblue.ClearblueException;
import clearblue.storage.Storage;
import clearblue.task.Task;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Adds a task to the list, reports it to the user, and saves the list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the wrapped task to {@code tasks}, reports it through
     * {@code ui}, and saves the updated list via {@code storage}.
     *
     * @param tasks task list to add to
     * @param ui user interface to report through
     * @param storage storage to persist the change through
     * @throws ClearblueException if saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ClearblueException {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks.asList());
    }
}
