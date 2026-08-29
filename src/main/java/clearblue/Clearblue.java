package clearblue;

/**
 * Runs the Clearblue chatbot and responds to task-management commands.
 */
public class Clearblue {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

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
     * Starts the chatbot.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        new Clearblue("data/clearblue.txt").run();
    }
}
