package org.example.contraller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFormContraller {
    public TextArea serverTextArea;
    public TextField servertextfield;
    public VBox servervbox;
    public VBox serverVbox;
    public ScrollPane scrollPane;

    private ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    private String message="";

    public void initialize(){
        serverVbox.heightProperty().addListener((observable, oldValue, newValue) ->
                scrollPane.setVvalue(1.0)); // bottom ekt ynn
        new Thread(() -> {
            try {
                appendMsg("\n client side");
                serverSocket = new ServerSocket(4000);

                socket = serverSocket.accept();

                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (!message.equals("Disconnect")) {
                    message = dataInputStream.readUTF();

                    if (message.startsWith("IMG")) {
                        String imagePath = message.substring(3);
                        Platform.runLater(() -> {
                            displayOwnSide(imagePath);
                            appendMsg("Client: [Image Received]");
                        });
                    } else {
                        Platform.runLater(() -> appendMsg("Client: " + message));
                    }
                }
            } catch (IOException e) {
                Platform.runLater(() -> appendMsg("Error: Connection lost"));
                e.printStackTrace();
            }
        }).start();

    }

    public void sentOnAction(ActionEvent actionEvent) throws IOException {
        String masssage = servertextfield.getText();
        dataOutputStream.writeUTF(masssage);
        dataOutputStream.flush();
        appendMsg("server :" + masssage+"\n");
        servertextfield.clear();
    }

    public void ImageSentOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                displayOwnSide(selectedFile.getPath());
                dataOutputStream.writeUTF("IMG" + selectedFile.getPath());
                dataOutputStream.flush();

                appendMsg("Server: Image Sent.....");
            } catch (IOException e) {
                Platform.runLater(() -> appendMsg("Error: cant send image"));
                e.printStackTrace();
            }
        }
    }

    private void displayOwnSide(String path) {
        try {
            File file = new File(path);
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            serverVbox.getChildren().add(imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appendMsg(String text) {
        Label label = new Label(text);
        serverVbox.getChildren().add(label);
    }
}
