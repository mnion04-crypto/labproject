package com.banglalearn;

/**
 * Entry point used when running the packaged fat jar directly
 * (java -jar bangla-duolingo-1.0.0.jar).
 *
 * A class that extends javafx.application.Application cannot be the
 * Main-Class of a jar launched with plain `java -jar` — the JVM checks
 * for JavaFX modules on the module path before Main.main() even runs,
 * and fails with "Error: JavaFX runtime components are missing, and are
 * required to run this application" even though the classes are on the
 * classpath. Routing through this plain launcher class avoids that check.
 *
 * mvn javafx:run and running Main.java directly from an IDE both still
 * work fine without this class — it's only needed for the `java -jar` path.
 */
public class Launcher {
    public static void main(String[] args) {
        Main.main(args);
    }
}