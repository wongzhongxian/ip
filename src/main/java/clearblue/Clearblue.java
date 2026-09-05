package clearblue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import clearblue.command.Command;
import clearblue.parser.Parser;
import clearblue.storage.Storage;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Runs the Clearblue chatbot and responds to task-management commands.
 */
public class Clearblue {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private boolean isExit;

    /**
     * Creates the chatbot, loading any previously saved tasks from the
     * given file.
     *
     * @param filePath path to the save file, relative to the project root
     */
    public Clearblue(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (ClearblueException exception) {
            ui.showError(exception.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Prints the banner and greeting, then reads and responds to commands
     * from standard input until a {@code bye} command is received or input
     * runs out.
     */
    public void run() {
        ui.showWelcome();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (ClearblueException exception) {
                ui.showError(exception.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Parses and executes one command line for the GUI, returning everything
     * the chatbot would normally print to the console as a single string.
     * Reuses the same {@link Parser}, {@link Command}, and {@link Ui} logic
     * as {@link #run()} by temporarily capturing standard output, so console
     * and GUI behaviour cannot drift apart.
     *
     * @param input one line of user input, as typed into the GUI
     * @return the chatbot's reply, with no leading or trailing blank lines
     */
    public String getResponse(String input) {
        return captureOutput(() -> {
            try {
                Command command = Parser.parse(input);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (ClearblueException exception) {
                ui.showError(exception.getMessage());
            }
        });
    }

    /**
     * Returns whether the most recent command handled by
     * {@link #getResponse(String)} was a {@code bye} command, so the GUI
     * knows when to close its window.
     *
     * @return {@code true} if the chatbot should exit
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Runs {@code action} with standard output redirected to a buffer, then
     * restores standard output and returns everything that was printed.
     *
     * @param action code whose console output should be captured
     * @return the captured output, with surrounding whitespace trimmed
     */
    private String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString().trim();
    }

    /**
     * Starts the chatbot.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        new Clearblue("data/clearblue.txt").run();
    }
}
