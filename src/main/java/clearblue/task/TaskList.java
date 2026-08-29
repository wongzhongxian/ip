package clearblue.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the list of tasks and the operations that add, remove, and look
 * them up.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list pre-populated with the given tasks (e.g. loaded
     * from storage).
     *
     * @param tasks initial tasks
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index zero-based index of the task to remove
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index zero-based index
     * @return the task at that index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks.
     *
     * @return {@code true} if the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns all tasks currently in the list, e.g. for saving to disk.
     *
     * @return an independent copy of the task list
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the deadlines and events whose date matches {@code queryDate}.
     * A deadline matches on its {@code by} date; an event matches if either
     * its {@code from} or {@code to} date matches. Todos never match, since
     * they carry no date.
     *
     * @param queryDate date to match against
     * @return matching tasks, in list order
     */
    public List<Task> getTasksOnDate(LocalDate queryDate) {
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (isOnDate(task, queryDate)) {
                matches.add(task);
            }
        }
        return matches;
    }

    private static boolean isOnDate(Task task, LocalDate queryDate) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy().isDate() && deadline.getBy().getDate().equals(queryDate);
        } else if (task instanceof Event event) {
            return (event.getFrom().isDate() && event.getFrom().getDate().equals(queryDate))
                    || (event.getTo().isDate() && event.getTo().getDate().equals(queryDate));
        }
        return false;
    }
}
