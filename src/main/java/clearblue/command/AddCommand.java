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

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ClearblueException {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks.asList());
    }
}
