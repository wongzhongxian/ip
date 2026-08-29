package clearblue;

/**
 * Makes sense of a raw command line: splits it into a command type and the
 * arguments that follow it.
 */
public class Parser {
    /**
     * Determines the command type represented by a command line.
     *
     * @param command complete command entered by the user
     * @return matching command type, or {@link CommandType#UNKNOWN} when no command matches
     */
    public static CommandType parseCommandType(String command) {
        return CommandType.fromCommand(command);
    }

    /**
     * Returns the part of a command that follows its command word.
     *
     * @param command complete command entered by the user
     * @param commandType parsed command type
     * @return trimmed command arguments, or the original input for an unknown command
     */
    public static String parseArguments(String command, CommandType commandType) {
        if (commandType == CommandType.UNKNOWN) {
            return command;
        }
        return command.substring(commandType.getCommandWord().length()).trim();
    }
}
