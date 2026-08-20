# Console UI Test Plan

This plan contains exact-output regression tests for Clearblue's command-line interface. Each test case runs in a fresh program process with an empty task list.

## Configuration

- **Working directory:** `.`
- **Required Java major version:** `25`
- **Compile command:** `javac -d {classes_dir} src/main/java/*.java`
- **Run command:** `java -cp {classes_dir} Clearblue`
- **Timeout seconds:** `10`
- **Comparison:** Standard output is compared line-for-line. CRLF and LF are treated equally, and one final newline is ignored. Any standard-error output fails the case.

## Test cases

### TC-01: Add and list a todo

**Aim:** Verify that a todo is stored without date information and displayed with the `[T]` type marker.

**Inputs:**
```text
todo borrow book
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] borrow book
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-02: Preserve a deadline value as text

**Aim:** Verify that an arbitrary `/by` value is retained as a string and displayed with the `[D]` type marker.

**Inputs:**
```text
deadline do homework /by no idea :-p
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] do homework (by: no idea :-p)
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] do homework (by: no idea :-p)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-03: Add, mark, and list an event

**Aim:** Verify that an event retains both time strings and its `[E]` type marker after being marked as done.

**Inputs:**
```text
event project meeting /from Mon 2pm /to 4pm
mark 1
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [E][X] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[E][X] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-04: Reject empty and unknown commands

**Aim:** Verify that blank input, an empty todo, and an unknown command produce helpful errors without ending the session.

**Inputs:**
```text

todo
blah
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Please enter a command.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A todo needs a description after "todo".
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or bye.
    ____________________________________________________________
    ____________________________________________________________
     Your task list is empty.
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-05: Reject malformed deadlines and events

**Aim:** Verify that every missing deadline or event component produces an error that identifies the missing value or separator.

**Inputs:**
```text
deadline
deadline /by Sunday
deadline return book /by
event
event project meeting /from Mon 2pm
event /from Mon 2pm /to 4pm
event meeting /from /to 4pm
event meeting /from 2pm /to
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A deadline needs a /by separator. Example: deadline return book /by Sunday
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A deadline needs a description before /by.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! A deadline needs a date or time after /by.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event needs a /from separator. Example: event meeting /from 2pm /to 4pm
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event needs a /to separator. Example: event meeting /from 2pm /to 4pm
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event needs a description before /from.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event needs a start date or time after /from.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! An event needs an end date or time after /to.
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```

### TC-06: Reject invalid mark and unmark commands

**Aim:** Verify that task status commands handle an empty list, missing numbers, non-numeric values, and out-of-range numbers without crashing or changing the wrong task.

**Inputs:**
```text
mark 1
todo read book
mark
mark one
mark 2
mark 1
unmark
unmark zero
unmark 0
unmark 1
list
bye
```

**Expected output:**
```text
   ________                __    __
  / ____/ /__  ____ ______/ /_  / /_  _____
 / /   / / _ \/ __ `/ ___/ __ \/ / / / / _ \
/ /___/ /  __/ /_/ / /  / /_/ / / /_/ /  __/
\____/_/\___/\__,_/_/  /_.___/_/\__,_/\___/

    ____________________________________________________________
     Hello! I'm Clearblue.
     What can I do for you? :)
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! There are no tasks to mark.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Tell me which task to mark. Example: mark 1
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! The task number must be a whole number. Example: mark 1
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Task 2 does not exist. Choose 1.
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Tell me which task to unmark. Example: unmark 1
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! The task number must be a whole number. Example: unmark 1
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! Task 0 does not exist. Choose 1.
    ____________________________________________________________
    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Bye. Hope to see you again soon! :)
    ____________________________________________________________
```
