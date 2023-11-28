package ru.kpfu.itis.lldan.bot;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class Chat extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
        AnchorPane root = loader.load();
        loader.getController();
        primaryStage.setTitle("Chat lldan");
        primaryStage.setScene(new Scene(root, 532, 300));
        primaryStage.show();
    }
}