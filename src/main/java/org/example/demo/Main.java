package org.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.demo.controllers.MainController;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/MainLayout.fxml"));
        Parent root = null;
        try {
            root = loader.load();
            MainController controller = loader.getController();
            controller.loadDeadlineLayout();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene toDoListScene = new Scene(root, 1000, 700);

        primaryStage.setTitle("Personal App");
        primaryStage.setScene(toDoListScene);
        primaryStage.show();


    }

}
