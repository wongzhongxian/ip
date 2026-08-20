import java.util.Scanner;

/**
 * Runs the Clearblue chatbot and responds to commands entered by the user.
 */
public class Clearblue {
    private static final String DIVIDER = "    ____________________________________________________________";
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        String banner = "   ________                __    __         \n"
                + "  / ____/ /__  ____ ______/ /_  / /_  _____\n"
                + " / /   / / _ \\/ __ `/ ___/ __ \\/ / / / / _ \\\n"
                + "/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/\n"
                + "\\____/_/\\___/\\__,_/_/  /_.___/_/\\__,_/\\___/\n";
        String introMessage = "     Hello! I'm Clearblue.\n"
                + "     What can I do for you? :)";
        System.out.println(banner);
        System.out.println(DIVIDER);
        System.out.println(introMessage);
        System.out.println(DIVIDER);

        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(DIVIDER);

            if (command.equals("bye")) {
                System.out.println("     Bye. Hope to see you again soon! :)");
                System.out.println(DIVIDER);
                break;
            }

            if (command.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println("     " + (i + 1) + ". " + tasks[i]);
                }
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("     added: " + command);
            }

            System.out.println(DIVIDER);
        }
    }
}
