package clearblue.command;

import clearblue.ClearblueException;
import clearblue.storage.Storage;
import clearblue.task.Task;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Marks or unmarks a task by its one-based list number, reports it to the
 * user, and saves the list.
 */
public class MarkCommand extends Command {
    private final int taskNumber;
    private final boolean isDone;

    /**
     * Creates a command that marks or unmarks the given task number.
     *
     * @param taskNumber one-based task number
     * @param isDone {@code true} to mark the task as done, {@code false} to unmark it
     */
    public MarkCommand(int taskNumber, boolean isDone) {
        this.taskNumber = taskNumber;
        this.isDone = isDone;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ClearblueException {
        String action = isDone ? "mark" : "unmark";
        int taskIndex = requireValidIndex(tasks, taskNumber, action);

        Task task = tasks.get(taskIndex);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        ui.showTaskStatusChanged(task, isDone);
        storage.save(tasks.asList());
    }
}
