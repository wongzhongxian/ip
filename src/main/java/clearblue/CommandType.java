package clearblue;

/**
 * Identifies the fixed set of commands understood by Clearblue.
 */
public enum CommandType {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    BYE("bye"),
    UNKNOWN("");

    private final String commandWord;

    /**
     * Creates a command type associated with its user-facing command word.
     *
     * @param commandWord word entered at the start of a command
     */
    CommandType(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Finds the command type represented by the first word of an input line.
     *
     * @param command complete command entered by the user
     * @return matching command type, or {@link #UNKNOWN} when no command matches
     */
    public static CommandType fromCommand(String command) {
        String trimmedCommand = command.trim();
        if (trimmedCommand.isEmpty()) {
            return UNKNOWN;
        }

        int firstSpace = trimmedCommand.indexOf(' ');
        String firstWord = firstSpace < 0
                ? trimmedCommand
                : trimmedCommand.substring(0, firstSpace);

        for (CommandType type : values()) {
            if (type.commandWord.equals(firstWord)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns the word users enter to select this command type.
     *
     * @return command word, or an empty string for {@link #UNKNOWN}
     */
    public String getCommandWord() {
        return commandWord;
    }
}
