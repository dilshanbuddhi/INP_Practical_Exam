package lk.ijse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/server_form.fxml"))));

        Stage stage1 = new Stage();
        stage1.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/client_form.fxml"))));

        stage.setTitle("Server Form");
        stage1.setTitle("Client Form");

        stage.centerOnScreen();
        stage1.centerOnScreen();

        stage.show();
        stage1.show();

    }
}