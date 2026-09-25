package com.banglalearn;

import com.banglalearn.util.BackgroundTasks;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        stage.setTitle("বাংলা শিখি — Learn Bangla");
        try {
            Image icon = new Image(getClass().getResourceAsStream("/data/icon.png"));
            stage.getIcons().add(icon);
        } catch (Exception ignored) {
            // Icon is optional; app still runs fine without it.
        }

        showProfilePicker();
        stage.setMinWidth(760);
        stage.setMinHeight(520);
        stage.show();
    }

    /** Called by ProfilePickerController once a profile is chosen. */
    public static void showMainShell() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/main.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        scene.getStylesheets().add(Main.class.getResource("/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    /** Called on "switch profile" to go back to the picker without restarting the app. */
    public static void showProfilePicker() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/profile-picker.fxml"));
        Scene scene = new Scene(loader.load(), 420, 480);
        scene.getStylesheets().add(Main.class.getResource("/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    @Override
    public void stop() {
        BackgroundTasks.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
