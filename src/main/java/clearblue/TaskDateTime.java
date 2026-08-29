package clearblue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a deadline/event date-or-time value that may be a real
 * calendar date or arbitrary free text, depending on what the user typed.
 *
 * <p>Text matching the {@code yyyy-MM-dd} format (e.g. {@code 2019-06-06})
 * is parsed into a {@link LocalDate} and displayed as {@code MMM dd yyyy}
 * (e.g. {@code Jun 06 2019}). Any other text is kept and displayed exactly
 * as typed.
 */
public class TaskDateTime {
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final String rawText;
    private final LocalDate date;

    /**
     * Creates a date-or-text value from user input, parsing it as a date
     * when possible.
     *
     * @param text raw text entered by the user (e.g. after {@code /by})
     */
    public TaskDateTime(String text) {
        this.rawText = text;
        this.date = parseOrNull(text);
    }

    /**
     * Returns this value formatted for display: a real date is shown as
     * {@code MMM dd yyyy}; unparsed text is shown exactly as entered.
     *
     * @return display text
     */
    public String toDisplayString() {
        return date != null ? date.format(DISPLAY_FORMAT) : rawText;
    }

    /**
     * Returns this value as originally entered, for saving to disk. Saving
     * the raw text (rather than the display text) lets {@link Storage}
     * re-parse it into a date again on the next load.
     *
     * @return original raw text
     */
    public String toStorageString() {
        return rawText;
    }

    @Override
    public String toString() {
        return toDisplayString();
    }

    private static LocalDate parseOrNull(String text) {
        try {
            return LocalDate.parse(text, INPUT_FORMAT);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }
}
