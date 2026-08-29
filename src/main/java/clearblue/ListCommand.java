package clearblue;

/**
 * Prints all tasks in the list, or reports that the list is empty.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.isEmpty()) {
            ui.showEmptyList();
        } else {
            ui.showTaskList(tasks);
        }
    }
}
