package clearblue.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clearblue.ClearblueException;
import clearblue.command.AddCommand;
import clearblue.command.Command;
import clearblue.command.DeleteCommand;
import clearblue.command.ExitCommand;
import clearblue.command.ListCommand;
import clearblue.command.MarkCommand;
import clearblue.command.OnCommand;
import clearblue.storage.Storage;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Tests for {@link Parser#parse(String)}: every command type's happy path,
 * verified by executing the returned {@link Command} and checking its
 * effect, plus each command's validation errors.
 */
public class ParserTest {
    @TempDir
    private Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("test-data.txt").toString());
    }

    @Test
    public void parse_validTodo_addsDescribedTask() throws ClearblueException {
        Command command = Parser.parse("todo read book");
        assertInstanceOf(AddCommand.class, command);

        TaskList tasks = new TaskList();
        command.execute(tasks, new Ui(), newStorage());

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void parse_todoWithoutDescription_throwsWithHelpfulMessage() {
        ClearblueException exception = assertThrows(ClearblueException.class, () -> Parser.parse("todo"));
        assertEquals("A todo needs a description after \"todo\".", exception.getMessage());
    }

    @Test
    public void parse_validDeadline_addsTaskWithFormattedDate() throws ClearblueException {
        Command command = Parser.parse("deadline return book /by 2019-06-06");

        TaskList tasks = new TaskList();
        command.execute(tasks, new Ui(), newStorage());

        assertEquals("[D][ ] return book (by: Jun 06 2019)", tasks.get(0).toString());
    }

    @Test
    public void parse_deadlineMissingBySeparator_throwsWithExample() {
        ClearblueException exception =
                assertThrows(ClearblueException.class, () -> Parser.parse("deadline return book"));
        assertTrue(exception.getMessage().contains("/by separator"));
    }

    @Test
    public void parse_deadlineEmptyDescription_throws() {
        ClearblueException exception =
                assertThrows(ClearblueException.class, () -> Parser.parse("deadline /by Sunday"));
        assertEquals("A deadline needs a description before /by.", exception.getMessage());
    }

    @Test
    public void parse_deadlineEmptyByValue_throws() {
        ClearblueException exception =
                assertThrows(ClearblueException.class, () -> Parser.parse("deadline return book /by"));
        assertEquals("A deadline needs a date or time after /by.", exception.getMessage());
    }

    @Test
    public void parse_validEvent_addsTaskWithBothTimes() throws ClearblueException {
        Command command = Parser.parse("event trip /from 2019-06-06 /to 2019-06-08");

        TaskList tasks = new TaskList();
        command.execute(tasks, new Ui(), newStorage());

        assertEquals("[E][ ] trip (from: Jun 06 2019 to: Jun 08 2019)", tasks.get(0).toString());
    }

    @Test
    public void parse_eventMissingFromSeparator_throws() {
        ClearblueException exception = assertThrows(ClearblueException.class, () -> Parser.parse("event trip"));
        assertTrue(exception.getMessage().contains("/from separator"));
    }

    @Test
    public void parse_eventMissingToSeparator_throws() {
        ClearblueException exception =
                assertThrows(ClearblueException.class, () -> Parser.parse("event trip /from 2pm"));
        assertTrue(exception.getMessage().contains("/to separator"));
    }

    @Test
    public void parse_mark_marksTheGivenTaskDone() throws ClearblueException {
        TaskList tasks = new TaskList();
        Parser.parse("todo read book").execute(tasks, new Ui(), newStorage());

        Command command = Parser.parse("mark 1");
        assertInstanceOf(MarkCommand.class, command);
        command.execute(tasks, new Ui(), newStorage());

        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void parse_unmark_marksTheGivenTaskNotDone() throws ClearblueException {
        TaskList tasks = new TaskList();
        Parser.parse("todo read book").execute(tasks, new Ui(), newStorage());
        tasks.get(0).markAsDone();

        Parser.parse("unmark 1").execute(tasks, new Ui(), newStorage());

        assertEquals(false, tasks.get(0).isDone());
    }

    @Test
    public void parse_markWithoutNumber_throwsWithExample() {
        ClearblueException exception = assertThrows(ClearblueException.class, () -> Parser.parse("mark"));
        assertEquals("Tell me which task to mark. Example: mark 1", exception.getMessage());
    }

    @Test
    public void parse_markNonNumeric_throwsWholeNumberMessage() {
        ClearblueException exception = assertThrows(ClearblueException.class, () -> Parser.parse("mark one"));
        assertEquals("The task number must be a whole number. Example: mark 1", exception.getMessage());
    }

    @Test
    public void parse_delete_removesTheGivenTask() throws ClearblueException {
        TaskList tasks = new TaskList();
        Parser.parse("todo read book").execute(tasks, new Ui(), newStorage());

        Command command = Parser.parse("delete 1");
        assertInstanceOf(DeleteCommand.class, command);
        command.execute(tasks, new Ui(), newStorage());

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void parse_list_returnsListCommand() throws ClearblueException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_listWithExtraArguments_throwsUnknownCommand() {
        ClearblueException exception = assertThrows(ClearblueException.class, () -> Parser.parse("list extra"));
        assertTrue(exception.getMessage().startsWith("I don't recognize that command."));
    }

    @Test
    public void parse_validOnDate_returnsOnCommand() throws ClearblueException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-06-06"));
    }

    @Test
    public void parse_onMissingDate_throwsWithExample() {
        ClearblueException exception = assertThrows(ClearblueException.class, () -> Parser.parse("on"));
        assertEquals("Tell me which date to check. Example: on 2019-06-06", exception.getMessage());
    }

    @Test
    public void parse_onInvalidDateFormat_throws() {
        ClearblueException exception = assertThrows(ClearblueException.class, () -> Parser.parse("on not-a-date"));
        assertEquals("The date must be in yyyy-MM-dd format. Example: on 2019-06-06", exception.getMessage());
    }

    @Test
    public void parse_byeWithNoArguments_returnsExitCommand() throws ClearblueException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_byeWithExtraArguments_throwsUnknownCommand() {
        ClearblueException exception = assertThrows(ClearblueException.class, () -> Parser.parse("bye now"));
        assertTrue(exception.getMessage().startsWith("I don't recognize that command."));
    }

    @Test
    public void parse_emptyInput_throwsPleaseEnterACommand() {
        ClearblueException exception = assertThrows(ClearblueException.class, () -> Parser.parse(""));
        assertEquals("Please enter a command.", exception.getMessage());
    }

    @Test
    public void parse_unrecognizedWord_throwsUnknownCommandMessage() {
        ClearblueException exception = assertThrows(ClearblueException.class, () -> Parser.parse("blah"));
        assertEquals(
                "I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, on, or bye.",
                exception.getMessage());
    }
}
