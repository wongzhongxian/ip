import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves tasks to, and loads tasks from, a fixed file on disk so that the
 * task list survives between runs of the chatbot.
 */
public class Storage {
    private static final Path DATA_FILE = Path.of("data", "clearblue.txt");
    private static final String FIELD_SEPARATOR = " | ";

    /**
     * Writes the current task list to {@link #DATA_FILE}, creating the
     * containing folder first if it does not already exist.
     *
     * @param tasks task storage
     * @param taskCount number of tasks currently stored
     */
    public static void save(Task[] tasks, int taskCount) {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            lines.add(encode(tasks[i]));
        }

        try {
            Path parentDirectory = DATA_FILE.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(DATA_FILE, lines);
        } catch (IOException exception) {
            System.out.println("     OOPS!!! Could not save tasks to disk: " + exception.getMessage());
        }
    }

    /**
     * Encodes a single task as one line of the save-file format
     * {@code TYPE | doneFlag | description[ | extra fields...]}.
     *
     * @param task task to encode
     * @return encoded line
     */
    private static String encode(Task task) {
        String doneFlag = task.isDone() ? "1" : "0";

        if (task instanceof Todo) {
            return String.join(FIELD_SEPARATOR, "T", doneFlag, task.getDescription());
        } else if (task instanceof Deadline deadline) {
            return String.join(FIELD_SEPARATOR, "D", doneFlag, deadline.getDescription(), deadline.getBy());
        } else if (task instanceof Event event) {
            return String.join(FIELD_SEPARATOR, "E", doneFlag, event.getDescription(),
                    event.getFrom(), event.getTo());
        }
        throw new IllegalArgumentException("Unknown task type: " + task.getClass());
    }
}
