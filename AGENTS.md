# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Basic
* IDE and level of expertise: IntelliJ

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI regression testing

After every source-code update, and before reporting the change as complete:

1. Review `test/ui-test-plan.md` against the behavior affected by the code change. Update the plan when test aims, inputs, or expected console output need to change; do not alter expectations merely to make an unintended behavior pass.
2. Invoke the `$test-ui` skill and run the full UI test plan, even when the plan itself did not need an update.
3. If a UI test fails, stop at that failure and report it according to the skill. Do not report the code update as complete unless the tests pass or the unresolved failure is clearly disclosed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

Follow [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html):

* Commit subject: imperative mood, capitalized, no trailing period, aim for ≤50 characters (hard limit 72).
* Commit body: separated from the subject by a blank line, wrapped at 72 characters, explains *what* changed
  and *why* (not *how* — that's what the diff is for).
* Branch names: kebab-case keywords (e.g. `refactor-ui-tests`); for issue-related branches, use
  `issueNumber-keywords-from-title`.

## Java coding conventions

Follow [SE-EDU Java (Intermediate) conventions](https://se-education.org/guides/conventions/java/intermediate.html):

* Naming: PascalCase classes, camelCase methods/variables, SCREAMING_SNAKE_CASE constants; boolean
  names/methods prefixed with `is`/`has`/`was`/`can`/`should`; never keep an abbreviation uppercase inside a
  name (`exportHtmlSource`, not `exportHTMLSource`).
* Every class belongs in a package (no default package).
* 4-space indentation, no tabs; line length soft limit 110 characters, hard limit 120; K&R brace style
  (opening brace on the same line); always brace loop and if/else bodies, even for a single statement.
* Javadoc is required on public classes and methods (short-summary first sentence, blank line before
  `@param`/`@return`, `@param` included for all parameters or none); optional for simple getters/setters,
  overridden methods, and test code.
