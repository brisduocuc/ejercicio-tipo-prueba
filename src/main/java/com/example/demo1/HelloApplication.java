package com.example.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 1. Apuntamos al archivo FXML en la carpeta resources
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        // 2. Cargamos el FXML dentro de la escena (el tamaño 320x240 o el que prefieras)
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}