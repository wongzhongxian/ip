package clearblue.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import clearblue.ClearblueException;
import clearblue.task.Deadline;
import clearblue.task.Event;
import clearblue.task.Task;
import clearblue.task.Todo;

/**
 * Saves tasks to, and loads tasks from, a file on disk so that the task
 * list survives between runs of the chatbot.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path dataFile;

    /**
     * Creates a Storage backed by the given file.
     *
     * @param filePath path to the save file, relative to the project root
     */
    public Storage(String filePath) {
        this.dataFile = Path.of(filePath);
    }

    /**
     * Writes the given tasks to this Storage's file, creating the
     * containing folder first if it does not already exist.
     *
     * @param tasks tasks to save
     * @throws ClearblueException if the tasks could not be written to disk
     */
    public void save(List<Task> tasks) throws ClearblueException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(encode(task));
        }

        try {
            Path parentDirectory = dataFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(dataFile, lines);
        } catch (IOException exception) {
            throw new ClearblueException("Could not save tasks to disk: " + exception.getMessage(), exception);
        }
    }

    /**
     * Loads previously saved tasks from this Storage's file. If the file
     * or its folder does not exist yet (e.g. on a fresh install), this
     * returns an empty list without treating that as an error. Any line
     * that cannot be parsed (corrupted data) is skipped instead of causing
     * the chatbot to crash on startup.
     *
     * @return the loaded tasks, or an empty list if there is nothing saved yet
     * @throws ClearblueException if the save file exists but could not be read
     */
    public List<Task> load() throws ClearblueException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(dataFile);
        } catch (IOException exception) {
            throw new ClearblueException("Could not load saved tasks: " + exception.getMessage(), exception);
        }

        for (String line : lines) {
            Task task = decode(line);
            if (task != null) {
                tasks.add(task);
            }
        }
        return tasks;
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
