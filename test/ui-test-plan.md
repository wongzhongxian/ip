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
