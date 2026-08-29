package clearblue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
     * Loads previously saved tasks from {@link #DATA_FILE} into {@code tasks}.
     * If the file or its folder does not exist yet (e.g. on a fresh
     * install), this returns 0 without treating that as an error. Any line
     * that cannot be parsed (corrupted data) is skipped instead of causing
     * the chatbot to crash on startup.
     *
     * @param tasks task storage to fill
     * @return number of tasks loaded
     */
    public static int load(Task[] tasks) {
        if (!Files.exists(DATA_FILE)) {
            return 0;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(DATA_FILE);
        } catch (IOException exception) {
            System.out.println("     OOPS!!! Could not load saved tasks: " + exception.getMessage());
            return 0;
        }

        int taskCount = 0;
        for (String line : lines) {
            if (taskCount >= tasks.length) {
                break;
            }
            Task task = decode(line);
            if (task != null) {
                tasks[taskCount] = task;
                taskCount++;
            }
        }
        return taskCount;
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
            return String.join(FIELD_SEPARATOR, "D", doneFlag, deadline.getDescription(),
                    deadline.getBy().toStorageString());
        } else if (task instanceof Event event) {
            return String.join(FIELD_SEPARATOR, "E", doneFlag, event.getDescription(),
                    event.getFrom().toStorageString(), event.getTo().toStorageString());
        }
        throw new IllegalArgumentException("Unknown task type: " + task.getClass());
    }

    /**
     * Decodes one line of the save-file format back into a task.
     *
     * @param line encoded line
     * @return decoded task, or {@code null} if the line is corrupted
     */
    private static Task decode(String line) {
        String[] fields = line.split(Pattern.quote(FIELD_SEPARATOR), -1);
        if (fields.length < 3) {
            return null;
        }

        String type = fields[0];
        boolean isDone = fields[1].equals("1");
        String description = fields[2];

        Task task;
        if (type.equals("T")) {
            task = new Todo(description);
        } else if (type.equals("D") && fields.length >= 4) {
            task = new Deadline(description, fields[3]);
        } else if (type.equals("E") && fields.length >= 5) {
            task = new Event(description, fields[3], fields[4]);
        } else {
            return null;
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
