package clearblue;

import java.time.LocalDate;
import java.util.List;

/**
 * Prints the deadlines and events on a given date, or reports that there
 * are none.
 */
public class OnCommand extends Command {
    private final LocalDate queryDate;

    /**
     * Creates a command that searches for tasks on the given date.
     *
     * @param queryDate date to match against
     */
    public OnCommand(LocalDate queryDate) {
        this.queryDate = queryDate;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.getTasksOnDate(queryDate);
        String displayDate = TaskDateTime.formatDate(queryDate);
        if (matches.isEmpty()) {
            ui.showNoTasksOnDate(displayDate);
        } else {
            ui.showTasksOnDate(matches, displayDate);
        }
    }
}
