package com.banglalearn.util;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Central place for running data loading and database operations off the
 * JavaFX Application Thread (Phase 4 of the build). Every DB/IO call in the
 * app (ContentLoader, ProgressDAO) should be wrapped with run(...) rather
 * than called directly from a controller.
 *
 * A single cached thread pool is enough here: work items are short (JSON
 * parses, single SQLite statements) and infrequent (triggered by user clicks).
 */
public final class BackgroundTasks {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(runnable -> {
        Thread t = new Thread(runnable, "bangla-app-worker");
        t.setDaemon(true); // don't block JVM shutdown
        return t;
    });

    private BackgroundTasks() {
    }

    /**
     * Runs {@code work} on a background thread and calls {@code onSuccess}
     * back on the JavaFX Application Thread with the result, or
     * {@code onError} on the JavaFX thread if it throws.
     */
    public static <T> void run(Callable<T> work, OnSuccess<T> onSuccess, OnError onError) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return work.call();
            }
        };
        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        task.setOnFailed(e -> onError.accept(task.getException()));
        EXECUTOR.submit(task);
    }

    public static void shutdown() {
        EXECUTOR.shutdown();
    }

    @FunctionalInterface
    public interface OnSuccess<T> {
        void accept(T result);
    }

    @FunctionalInterface
    public interface OnError {
        void accept(Throwable error);
    }
}
