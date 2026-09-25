# বাংলা শিখি — Learn Bangla (JavaFX desktop app)

A Duolingo-style desktop app for learning the Bangla script and basic
vocabulary, built with JavaFX + Maven. Structured to match a typical
university course covering Java/OOP, Git, JavaFX GUI, multithreading,
SQLite, and JSON parsing.

## Requirements
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA (recommended) or any IDE with Maven support

## Running it
```bash
mvn clean javafx:run
```
Or open the folder in IntelliJ as a Maven project and run `Main.java`
directly (IntelliJ will need the JavaFX VM options if you don't use the
Maven run configuration — the `javafx-maven-plugin` in `pom.xml` handles
this automatically when you use `javafx:run`).

To build a runnable fat jar:
```bash
mvn clean package
java -jar target/bangla-duolingo-1.0.0.jar
```

Progress is stored in a `bangla_progress.db` SQLite file created next to
wherever you run the app from.

## Project structure (mirrors the course topics)

| Course topic       | Where it lives                                                |
|---------------------|----------------------------------------------------------------|
| Java / OOP           | `model/` — `LessonItem` interface + `BanglaCharacter`/`BanglaWord` records, `lesson/` — `LessonFactory`, `QuizSession`, `Exercise` |
| JavaFX GUI            | `ui/` controllers + `resources/fxml/*.fxml`, sidebar shell in `main.fxml` |
| Multithreading         | `util/BackgroundTasks.java` — wraps every DB/IO call in a JavaFX `Task` run on an `ExecutorService`, so the UI thread never blocks |
| SQLite                  | `db/DatabaseManager.java` (schema + connection), `db/ProgressDAO.java` (CRUD), used from the Profile screen's `TableView` |
| JSON parsing             | `data/ContentLoader.java` parses `resources/data/characters.json` and `words.json` (Jackson) into the model classes |
| Git                       | Use this folder as-is — `git init && git add . && git commit -m "Initial Bangla learning app"` |

```
src/main/java/com/banglalearn/
  Main.java                 — application entry point, scene switching
  model/                    — LessonItem interface, records, enums
  lesson/                   — generic exercise/quiz generation engine
  data/                     — JSON content loader
  db/                       — SQLite schema, DAO, profile/progress records
  ui/                       — FXML controllers (one per screen)
  util/                     — BackgroundTasks (threading helper)
src/main/resources/
  fxml/                     — one FXML file per screen
  css/style.css             — app theme (Bangladeshi green/red palette)
  data/characters.json      — Bangla alphabet content
  data/words.json           — vocabulary content, grouped by topic
```

## How the content model works

Everything the app teaches — alphabet characters *and* vocabulary words —
implements the same `LessonItem` interface (`bangla()`, `romanization()`,
`englishMeaning()`, `lessonGroup()`, `difficulty()`). `LessonFactory` only
ever depends on that interface, so it generates multiple-choice quizzes for
*any* content type without knowing whether it's looking at a character or a
word. Adding a new content type (e.g. numbers, common phrases) later just
means adding a new record that implements `LessonItem` and a JSON file — no
change needed to the quiz engine itself.

## Adding more content

Add entries to `src/main/resources/data/characters.json` or `words.json`
following the existing shape. New `lessonGroup` values automatically show
up as new lesson buttons and vocabulary filter options — no code changes
needed.

## Extending further (matches the course's remaining topics)

- **Git**: commit after each phase (domain model → GUI shell → lesson
  engine → multithreading → SQLite → JSON → polish) so you have a clean
  history to show.
- **More exercise types**: `ExerciseType` already includes
  `ENGLISH_TO_BANGLA` and `BANGLA_TO_ROMANIZATION` — wire a difficulty
  selector in `LessonController` to mix them in.
- **Streaks / daily goals**: extend the `lesson_progress` table with a
  `completed_at` date index and compute consecutive days in `ProgressDAO`.
